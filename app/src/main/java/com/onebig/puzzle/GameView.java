package com.onebig.puzzle;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameView extends SurfaceView {

    private static int instances = 0;

    private Context mContext;                           // контекст приложения

    private GameManager mGameLoopThread;                // главный поток

    private Tiles mTiles = new Tiles();                 // массив спрайтов

    private SettingsScreen mScreenSettings;             // настройки
    private InterfaceScreen mScreenInterface;           // элементы интерфейса
    private FieldOverlay mOverlaySolved;                // экран конца игры
    private FieldOverlay mOverlayPause;                 // экран паузы
    private RectF mRectField;                           // область игрового поля

    public boolean solved = false;                      // состояние головоломки (решено/не решено)
    public boolean paused = false;                      // пауза

    private int mStartX;                                // координата x и y начальной точки
    private int mStartY;                                // вектора перемещения
    private boolean mButtonResult = false;              // результат взаимодействия нажатия с элементом интерфейса

    public Timer gameClock;                             // таймер для отслеживания затраченного времени

    // конструктор
    public GameView(Context context) {
        super(context);
        this.mContext = context;
        mGameLoopThread = new GameManager(this);
        instances++;

        SurfaceHolder holder = getHolder();

        holder.addCallback(new SurfaceHolder.Callback() {

            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                mGameLoopThread.setRunning(false);
                while (retry) {
                    try {
                        mGameLoopThread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                        Log.e("surfaceDestroyed", e.toString());
                    }
                }
            }

            public void surfaceCreated(SurfaceHolder holder) {
                createNewGame(false);

                mScreenSettings = new SettingsScreen();
                mScreenInterface = new InterfaceScreen();
                mOverlaySolved = new FieldOverlay(getResources().getString(R.string.info_win));
                mOverlayPause = new FieldOverlay(getResources().getString(R.string.info_pause));

                mGameLoopThread.setRunning(true);
                try {
                    mGameLoopThread.start();
                } catch (IllegalThreadStateException e) {
                    Log.e("surfaceCreated", e.toString());
                }
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

        });

    }

    //
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartX = x;
                mStartY = y;
                if (mScreenSettings.isShown()) {
                    mButtonResult = mScreenSettings.onClick(x, y);
                } else {
                    if (solved && mRectField.contains(x, y)) {
                        solved = false;
                        createNewGame(true);
                    } else {
                        mButtonResult = mScreenInterface.onClick(x, y);
                    }
                }
                break; // ACTION_DOWN

            case MotionEvent.ACTION_UP:
                int dx = x - mStartX;
                int dy = y - mStartY;

                if (Math.sqrt(dx * dx + dy * dy) > (Constraints.tileWidth / 6.0f) && !paused) {
                    mTiles.move(mStartX, mStartY, Tools.direction(dx, dy));
                } else if (!mButtonResult && paused && mRectField.contains(x, y)) {
                    paused = false;
                    mOverlayPause.hide();
                } else if (!solved && !mButtonResult) {
                    mTiles.move(mStartX, mStartY, Tools.DIRECTION_DEFAULT);
                }
                break; // ACTION_UP

        }

        return true;
    }

    //
    public boolean onBackPressed() {
        return !mScreenSettings.isShown() || mScreenSettings.hide();
    }

    //
    public void onPause() {
        if (gameClock != null) {
            gameClock.cancel();
            gameClock = null;
        }
    }

    //
    private void createNewGame(boolean isUser) {
        Game.create(Settings.gameWidth, Settings.gameHeight);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        // если создание новой игры не было инициировано пользователем,
        // загружаем сохрененную игру (если имеется)
        if (prefs.contains(Settings.KEY_GAME_ARRAY) && !isUser && Settings.saveGame) {
            String string_array = prefs.getString(Settings.KEY_GAME_ARRAY, Game.getGrid().toString());
            ArrayList<Integer> list = Tools.getIntegerArray(Arrays.asList(string_array.split("\\s*,\\s*")));
            if (list.size() == Game.getSize()) {
                Game.load(list, prefs.getInt(Settings.KEY_GAME_MOVES, 0), prefs.getLong(Settings.KEY_GAME_TIME, 0));
            }

            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(Settings.KEY_GAME_ARRAY);
            editor.remove(Settings.KEY_GAME_MOVES);
            editor.remove(Settings.KEY_GAME_TIME);
            editor.commit();

            if (Game.move(0) > 0) {
                paused = true;
            }
        } else {
            paused = false;
        }

        solved = false;

        // вычисление размеров
        Constraints.compute(this.getWidth(), this.getHeight(), 1.0f);
        mRectField = new RectF(
                Constraints.fieldMarginLeft - Constraints.spacing,
                Constraints.fieldMarginTop - Constraints.spacing,
                Constraints.fieldMarginLeft + Constraints.fieldWidth + Constraints.spacing,
                Constraints.fieldMarginTop + Constraints.fieldHeight + Constraints.spacing
        );

        mTiles.clear();

        Random rnd = new Random();
        int size = Game.getSize();                      // размер
        int animationType = rnd.nextInt(10);            // тип анимации
        int shift = size / 26 + 1;                      // коэффициент смещения анимации (группировка)
        int delay = 0;
        for (int i = 0; i < size; i++) {
            int n = Game.getAt(i);
            if (n > 0) {
                Tile t = new Tile(this, n, i);
                if (Settings.animationEnabled) {
                    switch (animationType) {
                        case 1:
                            // по порядку ячеек (сверху вниз)
                            delay = i / shift;
                            break;
                        case 2:
                            // в обратном порядке ячеек (снизу вверх)
                            delay = (size - i) / shift;
                            break;
                        case 3:
                            // случайный порядок
                            delay = rnd.nextInt(10 + 10 * (shift - 1));
                            break;
                        case 4:
                            // по порядку (1, 2, 3, ..., n-1, n)
                            delay = n / shift;
                            break;
                        case 5:
                            // в обратном порядке (n, n-1, n-2, ..., 2, 1)
                            delay = (size - n) / shift;
                            break;
                        case 6:
                            // по строкам
                            delay = i / Settings.gameWidth * 3; // 3 - задержка в кадрах между группами
                            break;
                        case 7:
                            // по столбцам
                            delay = i % Settings.gameWidth * 3;
                            break;
                        case 8:
                            // по строкам в обратном порядке
                            delay = (Settings.gameHeight - i / Settings.gameWidth) * 3;
                            break;
                        case 9:
                            // по столбцам в обратном порядке
                            delay = (Settings.gameWidth - i % Settings.gameWidth) * 3;
                            break;
                        default:
                            // все вместе
                            delay = 0;
                    }
                }
                mTiles.add(t.setAnimation(Tile.Animation.SCALE, delay));
            }
        }

        if (gameClock != null) {
            gameClock.cancel();
            gameClock = null;
        }

        gameClock = new Timer();
        GameClock gameClockTask = new GameClock();

        gameClock.schedule(gameClockTask, 100, 100);

    } // createNewGame

    public void draw(Canvas canvas) {
        mScreenInterface.draw(canvas);                   // рисование элементов интерфейса

        mTiles.draw(canvas);

        if (solved) {
            mOverlaySolved.draw(canvas);                 // оверлей "solved"
        }
        if (mScreenSettings.isShown()) {
            mScreenSettings.draw(canvas);                // экран настроек
        } else if (paused && !solved) {
            mOverlayPause.draw(canvas);                  // оверлей "paused"
        }
    }

    //
    class Tiles extends ArrayList<Tile> {

        public int at(float x, float y) {
            for (Tile t : this) {
                if (t.isCollision(x, y)) {
                    return t.getIndex();
                }
            }
            return -1;
        }

        public void move(float sx, float sy, int direction) {
            int startIndex = at(sx, sy);
            if (startIndex >= 0) {
                if (direction == Tools.DIRECTION_DEFAULT) {
                    direction = Game.getDirection(startIndex);
                }
                // вычисляем индексы ячеек, которые нам нужно переместить
                ArrayList<Integer> numbersToMove = Game.slide(direction, startIndex);
                // перемещаем выбранные ячейки, если таковые есть
                for (int i : numbersToMove) {
                    for (Tile s : this) {
                        if (s.getIndex() == i) {
                            s.onClick();
                        }
                    }
                }

                Game.move(1);

                if (solved = Game.isSolved()) {
                    if (gameClock != null) {
                        gameClock.cancel();
                        gameClock = null;
                    }
                    mOverlaySolved.show();
                }

                if (Settings.sounds) {
                    Sound.playSound();
                }

            } // if
        } // move

        public void draw(Canvas canvas) {
            for (int i = 0; i < size(); i++) {
                get(i).draw(canvas);
            }
        }

    } // Tiles

    public class InterfaceScreen {

        private static final int BTN_COUNT = 3;
        private static final int BTN_NEW = 0;
        private static final int BTN_SETTINGS = 1;
        private static final int BTN_PAUSE = 2;
        private final int OVERLAY_FRAMES = (int) 1.5 * Settings.screenAnimFrames;

        private Paint mPaintButton;                     // Paint для рисования иконки приложения (вверху слева)
        private Paint mPaintField;                      // ... фон игрового поля
        private Paint mPaintTextButton;                 // ... текст кнопок интерфейса
        private Paint mPaintTextValue;                  // ... отображение текстов инфо
        private Paint mPaintTextCaption;                // ... отображение заголовков инфо
        private Paint mPaintOverlay;                    // ... анимация подсветки кнопок

        private String mTextMode[];                     // режим игры
        private String mTextMoves;                      // ходы
        private String mTextTime;                       // время

        private RectF mRectInfo;                        // ... инфо
        private RectF mRectMode;                        // режим игры

        private int mButtonTextOffset;
        private int mValueTextOffset;
        private int mCaptionTextOffset;

        private ArrayList<Button> mButtons = new ArrayList<Button>(); // кнопки вверху экрана
        private float mButtonHeight;

        public InterfaceScreen() {
            mPaintTextButton = new Paint();
            mPaintTextButton.setAntiAlias(Settings.antiAlias);
            mPaintTextButton.setColor(Colors.getTileTextColor());
            mPaintTextButton.setTypeface(Settings.typeface);
            mPaintTextButton.setTextAlign(Paint.Align.CENTER);
            mPaintTextButton.setTextSize(Constraints.interfaceFontSize);

            mPaintTextValue = new Paint(mPaintTextButton);
            mPaintTextValue.setTextSize(Constraints.interfaceFontSize * 1.9f);
            mPaintTextValue.setColor(Colors.getInfoTextColor());

            mPaintTextCaption = new Paint(mPaintTextButton);
            mPaintTextCaption.setTextSize(Constraints.interfaceFontSize * 1.4f);
            mPaintTextCaption.setTextAlign(Paint.Align.LEFT);

            mPaintField = new Paint();
            mPaintField.setAntiAlias(Settings.antiAlias);
            mPaintField.setColor(Colors.backgroundField);

            mPaintButton = new Paint();
            mPaintButton.setAntiAlias(Settings.antiAlias);

            mPaintOverlay = new Paint();
            mPaintOverlay.setColor(Colors.getBgColor());

            mButtonHeight = Constraints.surfaceHeight * 0.07f;
            float w = (Constraints.surfaceWidth / BTN_COUNT);

            mButtons.add(new Button(new RectF(w * mButtons.size(), 0.0f, w * (mButtons.size() + 1), mButtonHeight), getResources().getString(R.string.action_new)));
            mButtons.add(new Button(new RectF(w * mButtons.size(), 0.0f, w * (mButtons.size() + 1), mButtonHeight), getResources().getString(R.string.action_settings)));
            mButtons.add(new Button(new RectF(w * mButtons.size(), 0.0f, w * (mButtons.size() + 1), mButtonHeight), getResources().getString(R.string.action_about)));

            float height = Constraints.surfaceHeight * 0.13f;
            float center = (Constraints.fieldMarginTop - Constraints.spacing - mButtonHeight) / 2.0f + mButtonHeight;
            mRectInfo = new RectF(0.0f, center - height / 2.0f, Constraints.surfaceWidth, center + height / 2.0f);
            mRectMode = new RectF(0.0f, center - height / 4.0f, Constraints.surfaceWidth * 0.5f, center + height / 4.0f);

            Rect r = new Rect();
            mPaintTextButton.getTextBounds("A", 0, 1, r);
            mButtonTextOffset = r.centerY();
            mPaintTextValue.getTextBounds("A", 0, 1, r);
            mValueTextOffset = r.centerY();
            mPaintTextCaption.getTextBounds("A", 0, 1, r);
            mCaptionTextOffset = r.centerY();

            mTextMode = getResources().getStringArray(R.array.game_modes);
            mTextMoves = getResources().getString(R.string.info_moves);
            mTextTime = getResources().getString(R.string.info_time);
        } // constructor

        public boolean onClick(float x, float y) {

            // -- создание новой игры --
            if (mButtons.get(BTN_NEW).contains(x, y)) {
                createNewGame(true);
                if (Settings.animationEnabled) {
                    mButtons.get(BTN_NEW).setOverlay();
                }
                return true;
            }

            // -- отображение настроек --
            if (mButtons.get(BTN_SETTINGS).contains(x, y)) {
                paused = Game.move(0) > 0;
                mScreenSettings.show();
                if (Settings.animationEnabled) {
                    mButtons.get(BTN_SETTINGS).setOverlay();
                }
                return true;
            }

            // -- о программе --
            if (mButtons.get(BTN_PAUSE).contains(x, y)) {
                if (!solved) {
                    paused = true;
                }
                if (!mOverlayPause.isShown()) {
                    mOverlayPause.show();
                }
                if (Settings.animationEnabled) {
                    mButtons.get(BTN_PAUSE).setOverlay();
                }
                return true;
            }

            // -- режим игры --
            if (mRectMode.contains(x, y)) {
                Settings.gameMode = (++Settings.gameMode % 2);
                Settings.save();
                createNewGame(true);
                return true;
            }

            return false;
        } // onClick

        public void draw(Canvas canvas) {
            canvas.drawColor(Colors.getBgColor());
            canvas.drawRect(mRectField, mPaintField);
            canvas.drawRect(mRectInfo, mPaintField);
            mPaintButton.setColor(Colors.getTileColor());
            canvas.drawRect(0.0f, 0.0f, Constraints.surfaceWidth, mButtonHeight, mPaintButton);

            for (Button b : mButtons) {
                if (b.frame > 5) {
                    float a = (float) Tools.easeOut(b.frame--, 0.0f, 1.0f, OVERLAY_FRAMES);
                    mPaintOverlay.setAlpha((int) (255 * (1.0f - a)));
                    canvas.drawRect(b.rect, mPaintOverlay);
                }
                canvas.drawText(b.caption, b.rect.centerX(), b.rect.centerY() - mButtonTextOffset, mPaintTextButton);
            }

            // режим игры
            canvas.drawText(mTextMode[Settings.gameMode].toUpperCase(), Constraints.surfaceWidth * 0.25f, mRectInfo.centerY() - mValueTextOffset, mPaintTextValue);

            float row1 = mRectInfo.top + mRectInfo.height() * 0.3f - mCaptionTextOffset;
            float row2 = mRectInfo.top + mRectInfo.height() * 0.7f - mCaptionTextOffset;
            // надписи
            mPaintTextCaption.setColor(Colors.getTileTextColor());
            mPaintTextCaption.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(mTextMoves, Constraints.surfaceWidth / 2.0f, row1, mPaintTextCaption);
            canvas.drawText(mTextTime, Constraints.surfaceWidth / 2.0f, row2, mPaintTextCaption);
            // значения
            mPaintTextCaption.setColor(Colors.getInfoTextColor());
            mPaintTextCaption.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(Integer.toString(Game.move(0)), Constraints.surfaceWidth - Constraints.spacing * 2.0f, row1, mPaintTextCaption);
            canvas.drawText(Tools.timeToString(Game.time(0)), Constraints.surfaceWidth - Constraints.spacing * 2.0f, row2, mPaintTextCaption);
        } // draw

        public void update() {
            mPaintTextValue.setColor(Colors.getInfoTextColor());
            mPaintTextButton.setColor(Colors.getTileTextColor());
            mPaintField.setColor(Colors.backgroundField);
            mPaintOverlay.setColor(Colors.getBgColor());
        }

        //
        private class Button {
            public RectF rect;
            public String caption;
            public int frame = 0;

            Button(RectF r, String s) {
                rect = r;
                caption = s;
            }

            public boolean contains(float x, float y) {
                return rect.contains(x, y);
            }

            public void setOverlay() {
                frame = OVERLAY_FRAMES;
            }

        } // Button

    } // InterfaceScreen

    public class SettingsScreen {

        private Paint mPaintText;                       // заголовок элемента настроек
        private Paint mPaintValue;                      // значение элемента настроек
        private Paint mPaintControls;                   // кнопки управления (назад)
        private Paint mPaintIcon;                       // для графического представления (например, цвет плиток)

        private String mTextWidth;                      // ширина поля
        private String mTextWidthValue;
        private String mTextHeight;                     // высота поля
        private String mTextHeightValue;
        private String mTextAnimations;                 // анимации
        private String mTextAnimationsValue[];
        private String mTextColor;                      // цвет плиток
        private String mTextColorMode;                  // цвет фона
        private String mTextColorModeValue[];           // цветовая тема
        private String mTextMode;                       // режим игры
        private String mTextModeValue[];
        private String mTextBack;                       // кнопка "назад"

        private RectF mRectWidth;                       // граница элемента настройки ширины
        private RectF mRectHeight;                      // ... высоты
        private RectF mRectColor;                       // ... цвета
        private RectF mRectColorMode;                   // ... цвета фона
        private RectF mRectColorIcon;                   // ... визуальное представление цвета
        private RectF mRectAnimations;                  // ... анимации
        private RectF mRectMode;                        // ... режим игры
        private RectF mRectBack;                        // ... "назад"

        private boolean mShow = false;

        public SettingsScreen() {
            int h = (int) (Constraints.surfaceHeight * 0.082f); // промежуток между строками
            int ch = (int) (Constraints.surfaceHeight * 0.15f); // отступ от верхнего края экрана

            mPaintText = new Paint();
            mPaintText.setAntiAlias(Settings.antiAlias);
            mPaintText.setColor(Colors.getOverlayTextColor());
            mPaintText.setTextSize(Constraints.menuFontSize);
            mPaintText.setTypeface(Settings.typeface);
            mPaintText.setTextAlign(Paint.Align.RIGHT);

            mPaintValue = new Paint();
            mPaintValue.setAntiAlias(Settings.antiAlias);
            mPaintValue.setColor(Colors.menuTextValue);
            mPaintValue.setTextSize(Constraints.menuFontSize);
            mPaintValue.setTypeface(Settings.typeface);
            mPaintValue.setTextAlign(Paint.Align.LEFT);

            mPaintControls = new Paint(mPaintText);
            mPaintControls.setTextAlign(Paint.Align.CENTER);

            mPaintIcon = new Paint();
            mPaintIcon.setAntiAlias(Settings.antiAlias);

            Rect r = new Rect();
            mTextWidth = getResources().getString(R.string.pref_width);
            mPaintText.getTextBounds(mTextWidth, 0, mTextWidth.length(), r);

            // -- настройки --

            ch += h;
            mTextMode = getResources().getString(R.string.pref_mode);
            mRectMode = new RectF(0, ch, Constraints.surfaceWidth, ch + r.height());

            mTextModeValue = getResources().getStringArray(R.array.game_modes);

            ch += h;
            mRectWidth = new RectF(0, ch, Constraints.surfaceWidth, ch + r.height());

            mTextWidthValue = Integer.toString(Settings.gameWidth);

            ch += h;
            mTextHeight = getResources().getString(R.string.pref_height);
            mRectHeight = new RectF(0, ch, Constraints.surfaceWidth, ch + r.height());

            mTextHeightValue = Integer.toString(Settings.gameHeight);

            ch += h;
            mTextAnimations = getResources().getString(R.string.pref_animation);
            mRectAnimations = new RectF(0, ch, Constraints.surfaceWidth, ch + r.height());

            mTextAnimationsValue = getResources().getStringArray(R.array.animations);

            ch += h;
            mTextColorMode = getResources().getString(R.string.pref_color_mode);
            mRectColorMode = new RectF(0, ch, Constraints.surfaceWidth, ch + r.height());

            mTextColorModeValue = getResources().getStringArray(R.array.color_mode);

            ch += h;
            mTextColor = getResources().getString(R.string.pref_color);
            mRectColor = new RectF(0, ch, Constraints.surfaceWidth, ch + r.height());
            mRectColorIcon = new RectF(Constraints.surfaceWidth / 2 + 2.0f * Constraints.spacing, mRectColor.top, Constraints.surfaceWidth / 2 + 2.0f * Constraints.spacing + r.height(), mRectColor.bottom);
            mRectColorIcon.inset(-mRectColorIcon.width() / 4, -mRectColorIcon.width() / 4);

            // -- элементы управления --

            mTextBack = getResources().getString(R.string.back);
            mRectBack = new RectF(0, Constraints.surfaceHeight - h, Constraints.surfaceWidth, Constraints.surfaceHeight - h + r.height());
        }

        public boolean onClick(int x, int y) {
            // -- ширина поля --
            if (mRectWidth.contains(x, y)) {
                Settings.gameWidth++;
                if (Settings.gameWidth > Settings.MAX_GAME_WIDTH) {
                    Settings.gameWidth = (Settings.gameHeight > 2) ? 2 : 3;
                }
                Settings.save();
                createNewGame(true);
            }

            // -- высота поля --
            if (mRectHeight.contains(x, y)) {
                Settings.gameHeight++;
                if (Settings.gameHeight > Settings.MAX_GAME_HEIGHT) {
                    Settings.gameHeight = (Settings.gameWidth > 2) ? 2 : 3;
                }
                Settings.save();
                createNewGame(true);
            }

            // -- переключение анимаций --
            if (mRectAnimations.contains(x, y)) {
                Settings.animationEnabled = !Settings.animationEnabled;
                Settings.save();
            }

            // -- цвет спрайтов --
            if (mRectColor.contains(x, y)) {
                Settings.tileColor = (++Settings.tileColor % Colors.tiles.length);
                Settings.save();
            }

            // -- цвет фона --
            if (mRectColorMode.contains(x, y)) {
                Settings.colorMode = (++Settings.colorMode % Settings.COLOR_MODES);
                Settings.save();

                mScreenInterface.update();
                mScreenSettings.update();
                mOverlayPause.update();
                mOverlaySolved.update();
            }

            // -- режим игры --
            if (mRectMode.contains(x, y)) {
                Settings.gameMode = (++Settings.gameMode % Settings.GAME_MODES);
                Settings.save();
                createNewGame(true);
            }

            // -- назад --
            if (mRectBack.contains(x, y)) {
                mScreenSettings.hide();
            }

            return true;
        }

        public boolean show() {
            return (mShow = true);
        }

        public boolean hide() {
            return (mShow = false);
        }

        public boolean isShown() {
            return mShow;
        }

        public void draw(Canvas canvas) {
            float right = Constraints.surfaceWidth / 2 + Constraints.spacing; // отступ от центра
            float left = Constraints.surfaceWidth / 2 - Constraints.spacing;  // для выравнивания элементов
            float s = Constraints.spacing / 2.0f;       // смещение по вертикали

            // фон
            canvas.drawColor(Colors.getOverlayColor());

            // чтение настроек игры
            mTextWidthValue = Integer.toString(Settings.gameWidth);
            mTextHeightValue = Integer.toString(Settings.gameHeight);

            // ширина поля
            canvas.drawText(mTextWidth, left, mRectWidth.bottom - s, mPaintText);
            canvas.drawText(mTextWidthValue, right, mRectWidth.bottom - s, mPaintValue);

            // высота поля
            canvas.drawText(mTextHeight, left, mRectHeight.bottom - s, mPaintText);
            canvas.drawText(mTextHeightValue, right, mRectHeight.bottom - s, mPaintValue);

            // анимации
            canvas.drawText(mTextAnimations, left, mRectAnimations.bottom - s, mPaintText);
            canvas.drawText(mTextAnimationsValue[Settings.animationEnabled ? 1 : 0], right, mRectAnimations.bottom - s, mPaintValue);

            // цвет
            canvas.drawText(mTextColor, left, mRectColor.bottom - s, mPaintText);
            mPaintIcon.setColor(Colors.getTileColor());
            canvas.drawRect(mRectColorIcon, mPaintIcon);

            // цвет фона
            canvas.drawText(mTextColorMode, left, mRectColorMode.bottom - s, mPaintText);
            canvas.drawText(mTextColorModeValue[Settings.colorMode], right, mRectColorMode.bottom - s, mPaintValue);

            // режим
            canvas.drawText(mTextMode, left, mRectMode.bottom - s, mPaintText);
            canvas.drawText(mTextModeValue[Settings.gameMode], right, mRectMode.bottom - s, mPaintValue);

            // кнопка "назад"
            canvas.drawText(mTextBack, Constraints.surfaceWidth / 2, mRectBack.bottom - s, mPaintControls);
        }

        public void update() {
            mPaintText.setColor(Colors.getOverlayTextColor());
            mPaintControls.setColor(Colors.getOverlayTextColor());
            mPaintValue.setColor(Colors.menuTextValue);
        }

    }

    public class FieldOverlay {

        private Paint mPaintBg;                         // Paint для отрисовки фона
        private Paint mPaintText;                       // Paint для отрисовки текста

        private Rect mRectBounds;                       // рассчет границ текста

        private String mCaption;                        // отображаемый текст
        private int mAnimFrames = 0;                    // кол-во кадров анимации
        private boolean mShow = false;                  // видимость

        public FieldOverlay(String s) {
            mCaption = s;
            mPaintBg = new Paint();
            mPaintBg.setAntiAlias(Settings.antiAlias);
            mPaintBg.setColor(Colors.getOverlayColor());

            mPaintText = new Paint();
            mPaintText.setAntiAlias(Settings.antiAlias);
            mPaintText.setColor(Colors.getOverlayTextColor());
            mPaintText.setTypeface(Settings.typeface);
            mPaintText.setTextAlign(Paint.Align.CENTER);
            mPaintText.setTextSize(2.3f * Constraints.interfaceFontSize);

            mRectBounds = new Rect();
            mPaintText.getTextBounds(mCaption, 0, mCaption.length(), mRectBounds);
        }

        public void show() {
            if (Settings.animationEnabled) {
                mAnimFrames = Settings.screenAnimFrames;
            }
            mShow = true;
        }

        public void hide() {
            mShow = false;
        }

        public boolean isShown() {
            return mShow;
        }

        public void draw(Canvas canvas) {
            if (mAnimFrames > 0) {
                mAnimFrames--;
            }
            float alpha = (float) Tools.easeOut(mAnimFrames, 0.0f, 1.0f, Settings.screenAnimFrames);
            mPaintBg.setAlpha((int) (Color.alpha(Colors.getOverlayColor()) * alpha));
            mPaintText.setAlpha((int) (255 * alpha));

            canvas.drawRect(mRectField, mPaintBg);
            canvas.drawText(
                    mCaption,
                    Constraints.fieldMarginLeft + Constraints.fieldWidth / 2.0f,
                    Constraints.fieldMarginTop + Constraints.fieldHeight / 2.0f - mRectBounds.centerY(),
                    mPaintText
            );
        }

        public void update() {
            mPaintBg.setColor(Colors.getOverlayColor());
            mPaintText.setColor(Colors.getOverlayTextColor());
        }

    }

    // TimerTask для таймера
    class GameClock extends TimerTask {

        @Override
        public void run() {
            if (!paused && !solved) {
                Game.time(100);
            }
        }
    }

}