package com.italankin.fifteen;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameView extends SurfaceView
        implements Game.Callback, SurfaceHolder.Callback {

    /**
     * Контекст приложения
     */
    private Context mContext;

    /**
     * Главный поток для перерисовки изображения
     */
    private GameManager mGameLoopThread;
    /**
     * Помощник для работы с базой данных
     */
    private DBHelper dbHelper;

    /**
     * Массив отображаемых спрайтов
     */
    private Tiles mTiles = new Tiles();

    /**
     * Экран настроек
     */
    private SettingsScreen mScreenSettings;
    /**
     * Элементы интерфейса
     */
    private InterfaceScreen mScreenInterface;
    /**
     * Экран рекордов
     */
    private LeaderboardScreen mScreenLeaderboard;
    /**
     * Оверлей конца игры
     */
    private FieldOverlay mOverlaySolved;
    /**
     * Оверлей паузы
     */
    private FieldOverlay mOverlayPause;
    /**
     * Область игрового поля
     */
    private RectF mRectField;

    /**
     * Состояние решения головоломки
     */
    public boolean solved = false;
    /**
     * Состояние паузы
     */
    public boolean paused = false;

    /**
     * Координата x начальной точки жеста
     */
    private int mStartX;
    /**
     * Координата y начальной точки жеста
     */
    private int mStartY;

    /**
     * Таймер для отслеживания затраченного времени
     */
    public Timer gameClock;
    /**
     * Интервал срабатывания таймера (мс)
     */
    public static final int TIME_CLOCK = 20;

    /**
     * Конструктор по умолчанию
     *
     * @param context контекст приложения
     */
    public GameView(Context context) {
        super(context);
        mContext = context;
        mGameLoopThread = new GameManager(this);

        dbHelper = new DBHelper(context);

        SurfaceHolder holder = getHolder();

        holder.addCallback(this);

    }

    public void surfaceCreated(SurfaceHolder holder) {
        createNewGame(false);

        mScreenSettings = new SettingsScreen();
        mScreenInterface = new InterfaceScreen();
        mScreenLeaderboard = new LeaderboardScreen();
        mOverlaySolved = new FieldOverlay(getResources().getString(R.string.info_win));
        mOverlayPause = new FieldOverlay(getResources().getString(R.string.info_pause));

        Game.addCallback(this);

        mGameLoopThread.setRunning(true);
        try {
            mGameLoopThread.start();
        } catch (IllegalThreadStateException e) {
            Log.e("surfaceCreated", e.toString());
        }
    }

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

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void onGameCreate(int width, int height) {
    }

    public void onGameLoad() {
    }

    public void onGameMove() {
    }

    public void onGameSolve() {
        solved = true;
        if (gameClock != null) {
            gameClock.cancel();
            gameClock = null;
        }
        mOverlaySolved.show();
        dbHelper.insert(Settings.gameMode,
                Settings.gameWidth,
                Settings.gameHeight, Settings.hardmode ? 1 : 0,
                Game.getMoves() + 1,
                Game.getTime());
    }

    /**
     * Обработка событий нажатия на экран
     */
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStartX = x;
                mStartY = y;

                if (!mScreenSettings.isShown() && !mScreenLeaderboard.isShown()) {
                    if (solved && mRectField.contains(x, y)) {
                        solved = false;
                        createNewGame(true);
                    } else {
                        mScreenInterface.onClick(x, y);
                    }
                }

                break; // ACTION_DOWN

            case MotionEvent.ACTION_UP:
                int dx = x - mStartX;
                int dy = y - mStartY;

                if (mScreenLeaderboard.isShown()) {
                    mScreenLeaderboard.onClick(mStartX, mStartY, dx);
                    return true;
                }
                if (mScreenSettings.isShown()) {
                    mScreenSettings.onClick(mStartX, mStartY, dx);
                    return true;
                }

                if (Math.sqrt(dx * dx + dy * dy) > (Dimensions.tileSize / 6.0f) && !paused) {
                    mTiles.move(mStartX, mStartY, Tools.direction(dx, dy));
                } else if (paused && mRectField.contains(x, y)) {
                    paused = false;
                    mOverlayPause.hide();
                } else if (!solved) {
                    mTiles.move(mStartX, mStartY, Tools.DIRECTION_DEFAULT);
                }
                break; // ACTION_UP

        }

        return true;
    }

    /**
     * Нажатие клавиши "Назад" на устройстве
     */
    public boolean onBackPressed() {
        return mScreenSettings.hide() || mScreenLeaderboard.hide();
    }

    /**
     * Приостановка {@link android.app.Activity}
     */
    public void onPause() {
        if (gameClock != null) {
            gameClock.cancel();
            gameClock = null;
        }
    }

    /**
     * Создание новой игры
     *
     * @param isUser <b>true</b>, если действие было вызвано пользователем
     */
    private void createNewGame(boolean isUser) {
        Game.create(Settings.gameWidth, Settings.gameHeight);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        // если создание новой игры не было инициировано пользователем,
        // загружаем сохрененную игру (если имеется)
        if (prefs.contains(Settings.KEY_GAME_ARRAY) && !isUser && Settings.saveGame) {
            String string_array = prefs.getString(Settings.KEY_GAME_ARRAY, Game.getGridStr());
            ArrayList<Integer> list = Tools.getIntegerArray(
                    Arrays.asList(string_array.split("\\s*,\\s*")));

            if (list.size() == Game.getSize()) {
                Game.load(list,
                        prefs.getInt(Settings.KEY_GAME_MOVES, 0),
                        prefs.getLong(Settings.KEY_GAME_TIME, 0));
            }

            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(Settings.KEY_GAME_ARRAY);
            editor.remove(Settings.KEY_GAME_MOVES);
            editor.remove(Settings.KEY_GAME_TIME);
            editor.commit();

            if (Game.getMoves() > 0) {
                paused = true;
            }

        } else {
            paused = false;
        }

        solved = false;

        // вычисление размеров
        Dimensions.update(this.getWidth(), this.getHeight(), 1.0f);
        mRectField = new RectF(
                Dimensions.fieldMarginLeft - Dimensions.spacing,
                Dimensions.fieldMarginTop - Dimensions.spacing,
                Dimensions.fieldMarginLeft + Dimensions.fieldWidth + Dimensions.spacing,
                Dimensions.fieldMarginTop + Dimensions.fieldHeight + Dimensions.spacing
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
                if (Settings.animations) {
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

        gameClock.schedule(gameClockTask, TIME_CLOCK, TIME_CLOCK);

    } // END createNewGame

    /**
     * Рендер изображения
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        mScreenInterface.draw(canvas);                   // рисование элементов интерфейса

        mTiles.draw(canvas);

        if (solved) {
            mOverlaySolved.draw(canvas);                 // оверлей "solved"
        }
        if (mScreenLeaderboard.isShown()) {
            mScreenLeaderboard.draw(canvas);
        } else if (mScreenSettings.isShown()) {
            mScreenSettings.draw(canvas);                // экран настроек
        } else if (paused && !solved) {
            mOverlayPause.draw(canvas);                  // оверлей "paused"
        }
    }

    /**
     * Массив элементов {@link Tile}
     */
    class Tiles extends ArrayList<Tile> {

        /**
         * Определяет спрайт, находящийся по координатам
         *
         * @param x координата x на экране
         * @param y координата y на экране
         * @return индекс элемента в игровом массиве
         */
        public int at(float x, float y) {
            for (Tile t : this) {
                if (t.at(x, y)) {
                    return t.getIndex();
                }
            }
            return -1;
        }

        /**
         * Перемещение элементов
         *
         * @param sx        координата x начальной ячейки в массиве
         * @param sy        координата y начальной ячейки в массиве
         * @param direction направление перемещения
         */
        public void move(float sx, float sy, int direction) {
            int startIndex = at(sx, sy);
            if (startIndex >= 0) {
                if (direction == Tools.DIRECTION_DEFAULT) {
                    direction = Game.getDirection(startIndex);
                }
                // вычисляем индексы ячеек, которые нам нужно переместить
                ArrayList<Integer> numbersToMove = Game.getSlidingElements(direction, startIndex);
                // перемещаем выбранные ячейки, если таковые есть
                for (int i : numbersToMove) {
                    for (Tile s : this) {
                        if (s.getIndex() == i) {
                            s.onClick();
                        }
                    }
                }

                if (numbersToMove.size() > 0) {
                    Game.incMoves();
                }

            } // if
        } // END move

        /**
         * Отрисовка спрайтов
         */
        public void draw(Canvas canvas) {
            for (int i = 0; i < size(); i++) {
                get(i).draw(canvas);
            }
        }

    } // END Tiles

    /**
     * Базовый класс элемента интерфейса
     */
    public abstract class BaseScreen {

        protected boolean mShow = false;

        public boolean show() {
            return (mShow = true);
        }

        /**
         * @return <b>false</b>, если элемент не отображался, иначе <b>true</b>
         */
        public boolean hide() {
            return mShow && !(mShow = false);
        }

        public boolean isShown() {
            return mShow;
        }

    }

    /**
     * Класс объединяет элементы интерфейса и управляет их отрисовкой и поведением
     */
    public class InterfaceScreen extends BaseScreen {

        private static final int MENU_BUTTON_COUNT = 4;
        private static final int BTN_NEW = 0;
        private static final int BTN_SETTINGS = 1;
        private static final int BTN_LB = 2;
        private static final int BTN_PAUSE = 3;

        private final int OVERLAY_FRAMES = (int) (1.5 * Settings.screenAnimFrames);

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

        private ArrayList<TopBarButton> mButtons = new ArrayList<TopBarButton>(); // кнопки вверху экрана
        private float mButtonHeight;

        public InterfaceScreen() {
            mPaintTextButton = new Paint();
            mPaintTextButton.setAntiAlias(Settings.antiAlias);
            mPaintTextButton.setColor(Colors.getTileTextColor());
            mPaintTextButton.setTypeface(Settings.typeface);
            mPaintTextButton.setTextAlign(Paint.Align.CENTER);
            mPaintTextButton.setTextSize(Dimensions.interfaceFontSize);

            mPaintTextValue = new Paint(mPaintTextButton);
            mPaintTextValue.setTextSize(Dimensions.interfaceFontSize * 1.8f);
            mPaintTextValue.setColor(Colors.getInfoTextColor());

            mPaintTextCaption = new Paint(mPaintTextButton);
            mPaintTextCaption.setTextSize(Dimensions.interfaceFontSize * 1.4f);
            mPaintTextCaption.setTextAlign(Paint.Align.LEFT);

            mPaintField = new Paint();
            mPaintField.setAntiAlias(Settings.antiAlias);
            mPaintField.setColor(Colors.backgroundField);

            mPaintButton = new Paint();
            mPaintButton.setAntiAlias(Settings.antiAlias);

            mPaintOverlay = new Paint();
            mPaintOverlay.setColor(Colors.getBackgroundColor());

            mButtonHeight = Dimensions.surfaceHeight * 0.07f;
            float w = (Dimensions.surfaceWidth / MENU_BUTTON_COUNT);

            mButtons.add(new TopBarButton(
                    new RectF(w * mButtons.size(), 0.0f, w * (mButtons.size() + 1), mButtonHeight),
                    getResources().getString(R.string.action_new), BTN_NEW));
            mButtons.add(new TopBarButton(
                    new RectF(w * mButtons.size(), 0.0f, w * (mButtons.size() + 1), mButtonHeight),
                    getResources().getString(R.string.action_settings), BTN_SETTINGS));
            mButtons.add(new TopBarButton(
                    new RectF(w * mButtons.size(), 0.0f, w * (mButtons.size() + 1), mButtonHeight),
                    getResources().getString(R.string.action_lb), BTN_LB));
            mButtons.add(new TopBarButton(
                    new RectF(w * mButtons.size(), 0.0f, w * (mButtons.size() + 1), mButtonHeight),
                    getResources().getString(R.string.action_about), BTN_PAUSE));

            float height = Dimensions.surfaceHeight * 0.13f;
            float center = (Dimensions.fieldMarginTop - Dimensions.spacing - mButtonHeight)
                    / 2.0f + mButtonHeight;
            mRectInfo = new RectF(0.0f, center - height / 2.0f,
                    Dimensions.surfaceWidth, center + height / 2.0f);
            mRectMode = new RectF(0.0f, center - height / 4.0f,
                    Dimensions.surfaceWidth * 0.5f, center + height / 4.0f);

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

        /**
         * Обработка событий нажатия
         *
         * @param x координата x нажатия
         * @param y координата y нажатия
         * @return <b>true</b>, если событие было обработано, иначе <b>false</b>
         */
        public boolean onClick(float x, float y) {
            int id = -1;
            TopBarButton b;

            for (int i = 0; i < MENU_BUTTON_COUNT; i++) {
                b = mButtons.get(i);
                if (b.contains(x, y)) {
                    id = b.id;
                    b.setOverlay();
                    break;
                }
            }

            switch (id) {
                case BTN_NEW:
                    createNewGame(true);
                    return true;

                case BTN_SETTINGS:
                    paused = Game.getMoves() > 0;
                    mScreenSettings.show();
                    return true;

                case BTN_LB:
                    paused = Game.getMoves() > 0;
                    mScreenLeaderboard.show();
                    return true;

                case BTN_PAUSE:
                    if (!solved) {
                        paused = !paused;
                    }
                    if (mOverlayPause.isShown()) {
                        mOverlayPause.hide();
                    } else {
                        mOverlayPause.show();
                    }
                    return true;
            }

            // -- режим игры --
            if (mRectMode.contains(x, y)) {
                Settings.gameMode = (++Settings.gameMode % Settings.GAME_MODES);
                Settings.save();
                createNewGame(true);
                return true;
            }

            return false;
        } // onClick

        public void draw(Canvas canvas) {
            canvas.drawColor(Colors.getBackgroundColor());
            canvas.drawRect(mRectField, mPaintField);
            canvas.drawRect(mRectInfo, mPaintField);
            mPaintButton.setColor(Colors.getTileColor());
            canvas.drawRect(0.0f, 0.0f, Dimensions.surfaceWidth, mButtonHeight, mPaintButton);

            for (TopBarButton b : mButtons) {
                if (b.frame > 5) {
                    float a = (float) Tools.easeOut(b.frame--, 0.0f, 1.0f, OVERLAY_FRAMES);
                    mPaintOverlay.setAlpha((int) (255 * (1.0f - a)));
                    canvas.drawRect(b.rect, mPaintOverlay);
                }
                canvas.drawText(b.caption, b.rect.centerX(),
                        b.rect.centerY() - mButtonTextOffset, mPaintTextButton);
            }

            // режим игры
            canvas.drawText(
                    mTextMode[Settings.gameMode].toUpperCase() + (Settings.hardmode ? "*" : ""),
                    Dimensions.surfaceWidth * 0.25f, mRectInfo.centerY() - mValueTextOffset,
                    mPaintTextValue);

            float row1 = mRectInfo.top + mRectInfo.height() * 0.3f - mCaptionTextOffset;
            float row2 = mRectInfo.top + mRectInfo.height() * 0.7f - mCaptionTextOffset;
            // надписи
            mPaintTextCaption.setColor(Colors.getTileTextColor());
            mPaintTextCaption.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(mTextMoves, Dimensions.surfaceWidth / 2.0f, row1, mPaintTextCaption);
            canvas.drawText(mTextTime, Dimensions.surfaceWidth / 2.0f, row2, mPaintTextCaption);
            // значения
            mPaintTextCaption.setColor(Colors.getInfoTextColor());
            mPaintTextCaption.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(Integer.toString(Game.getMoves()),
                    Dimensions.surfaceWidth - Dimensions.spacing * 2.0f, row1, mPaintTextCaption);
            canvas.drawText(Tools.timeToString(Game.getTime()),
                    Dimensions.surfaceWidth - Dimensions.spacing * 2.0f, row2, mPaintTextCaption);
        } // END draw

        public void update() {
            mPaintTextValue.setColor(Colors.getInfoTextColor());
            mPaintTextButton.setColor(Colors.getTileTextColor());
            mPaintField.setColor(Colors.backgroundField);
            mPaintOverlay.setColor(Colors.getBackgroundColor());
        }

        private class TopBarButton {
            public RectF rect;
            public String caption;
            public int id;
            public int frame = 0;

            TopBarButton(RectF r, String s, int id) {
                rect = r;
                caption = s;
                this.id = id;
            }

            public boolean contains(float x, float y) {
                return rect.contains(x, y);
            }

            public void setOverlay() {
                if (Settings.animations) {
                    frame = OVERLAY_FRAMES;
                }
            }

        } // END Button

    } // END InterfaceScreen

    /**
     * Класс объединяет элементы интерфейса настроек и управляет их отрисовкой и поведением
     */
    public class SettingsScreen extends BaseScreen {

        private Paint mPaintText;                       // заголовок элемента настроек
        private Paint mPaintValue;                      // значение элемента настроек
        private Paint mPaintControls;                   // кнопки управления (назад)
        private Paint mPaintIcon;                       // для графического представления (например, цвет плиток)

        private String mTextWidth;                      // ширина поля
        private String mTextWidthValue;
        private String mTextHeight;                     // высота поля
        private String mTextHeightValue;
        private String mTextBf;                         // hardmode
        private String mTextBfValue[];
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
        private RectF mRectBf;                          // ... "слепого" режима
        private RectF mRectColor;                       // ... цвета
        private RectF mRectColorMode;                   // ... цвета фона
        private RectF mRectColorIcon;                   // ... визуальное представление цвета
        private RectF mRectAnimations;                  // ... анимации
        private RectF mRectMode;                        // ... режим игры
        private RectF mRectBack;                        // ... "назад"

        public SettingsScreen() {
            int h = (int) (Dimensions.surfaceHeight * 0.082f); // промежуток между строками
            int ch = (int) (Dimensions.surfaceHeight * 0.15f); // отступ от верхнего края экрана
            int sp = -h / 4;

            mPaintText = new Paint();
            mPaintText.setAntiAlias(Settings.antiAlias);
            mPaintText.setColor(Colors.getOverlayTextColor());
            mPaintText.setTextSize(Dimensions.menuFontSize);
            mPaintText.setTypeface(Settings.typeface);
            mPaintText.setTextAlign(Paint.Align.RIGHT);

            mPaintValue = new Paint();
            mPaintValue.setAntiAlias(Settings.antiAlias);
            mPaintValue.setColor(Colors.menuTextValue);
            mPaintValue.setTextSize(Dimensions.menuFontSize);
            mPaintValue.setTypeface(Settings.typeface);
            mPaintValue.setTextAlign(Paint.Align.LEFT);

            mPaintControls = new Paint(mPaintText);
            mPaintControls.setTextAlign(Paint.Align.CENTER);

            mPaintIcon = new Paint();
            mPaintIcon.setAntiAlias(Settings.antiAlias);

            mTextHeight = getResources().getString(R.string.pref_height);
            mTextHeightValue = Integer.toString(Settings.gameHeight);
            mTextWidth = getResources().getString(R.string.pref_width);
            mTextWidthValue = Integer.toString(Settings.gameWidth);
            mTextMode = getResources().getString(R.string.pref_mode);
            mTextModeValue = getResources().getStringArray(R.array.game_modes);
            mTextBf = getResources().getString(R.string.pref_bf);
            mTextBfValue = getResources().getStringArray(R.array.difficulty_modes);
            mTextAnimations = getResources().getString(R.string.pref_animation);
            mTextAnimationsValue = getResources().getStringArray(R.array.toggle);
            mTextColorMode = getResources().getString(R.string.pref_color_mode);
            mTextColorModeValue = getResources().getStringArray(R.array.color_mode);
            mTextColor = getResources().getString(R.string.pref_color);
            mTextBack = getResources().getString(R.string.back);

            Rect r = new Rect();
            mPaintText.getTextBounds(mTextWidth, 0, mTextWidth.length(), r);

            ch += h;
            mRectMode = new RectF(0, ch, Dimensions.surfaceWidth, ch + r.height());
            mRectMode.inset(0, sp);

            ch += h;
            mRectBf = new RectF(0, ch, Dimensions.surfaceWidth, ch + r.height());
            mRectBf.inset(0, sp);

            ch += h;
            mRectWidth = new RectF(0, ch, Dimensions.surfaceWidth, ch + r.height());
            mRectWidth.inset(0, sp);

            ch += h;
            mRectHeight = new RectF(0, ch, Dimensions.surfaceWidth, ch + r.height());
            mRectHeight.inset(0, sp);

            ch += h;
            mRectAnimations = new RectF(0, ch, Dimensions.surfaceWidth, ch + r.height());
            mRectAnimations.inset(0, sp);

            ch += h;
            mRectColorMode = new RectF(0, ch, Dimensions.surfaceWidth, ch + r.height());
            mRectColorMode.inset(0, sp);

            ch += h;
            mRectColor = new RectF(0, ch, Dimensions.surfaceWidth, ch + r.height());
            mRectColor.inset(0, sp);
            mRectColorIcon = new RectF(Dimensions.surfaceWidth / 2 + 2.0f * Dimensions.spacing,
                    mRectColor.top - sp,
                    Dimensions.surfaceWidth / 2 + 2.0f * Dimensions.spacing + r.height(),
                    mRectColor.bottom + sp);
            mRectColorIcon.inset(-mRectColorIcon.width() / 4, -mRectColorIcon.width() / 4);


            mRectBack = new RectF(0, Dimensions.surfaceHeight - h,
                    Dimensions.surfaceWidth, Dimensions.surfaceHeight - h + r.height());
            mRectBack.inset(0, sp);
        }

        /**
         * Обработка событий нажатия
         *
         * @param x  координата x нажатия
         * @param y  координата y нажатия
         * @param dx направление жеста
         */
        public boolean onClick(int x, int y, int dx) {

            if (Math.abs(dx) < 15) {
                dx = 0;
            }

            // -- ширина поля --
            if (mRectWidth.contains(x, y)) {
                Settings.gameWidth += ((dx == 0) ? 1 : Math.signum(dx));
                if (Settings.gameWidth < Settings.MIN_GAME_WIDTH) {
                    Settings.gameWidth = Settings.MAX_GAME_WIDTH;
                }
                if (Settings.gameWidth > Settings.MAX_GAME_WIDTH) {
                    Settings.gameWidth = Settings.MIN_GAME_WIDTH;
                }
                Settings.save();
                createNewGame(true);
                return true;
            }

            // -- высота поля --
            if (mRectHeight.contains(x, y)) {
                Settings.gameHeight += ((dx == 0) ? 1 : Math.signum(dx));
                if (Settings.gameHeight < Settings.MIN_GAME_HEIGHT) {
                    Settings.gameHeight = Settings.MAX_GAME_HEIGHT;
                }
                if (Settings.gameHeight > Settings.MAX_GAME_HEIGHT) {
                    Settings.gameHeight = Settings.MIN_GAME_HEIGHT;
                }
                Settings.save();
                createNewGame(true);
                return true;
            }

            // -- переключение анимаций --
            if (mRectAnimations.contains(x, y)) {
                Settings.animations = !Settings.animations;
                Settings.save();
                return true;
            }

            // -- цвет спрайтов --
            if (mRectColor.contains(x, y)) {
                if (dx < 0) {
                    if (--Settings.tileColor < 0) {
                        Settings.tileColor += Colors.tiles.length;
                    }
                } else {
                    Settings.tileColor = (++Settings.tileColor % Colors.tiles.length);
                }
                Settings.save();
                return true;
            }

            // -- цвет фона --
            if (mRectColorMode.contains(x, y)) {
                Settings.colorMode = (++Settings.colorMode % Settings.COLOR_MODES);
                mScreenInterface.update();
                mScreenSettings.update();
                mScreenLeaderboard.update();
                mOverlayPause.update();
                mOverlaySolved.update();
                Settings.save();
                return true;
            }

            // -- режим игры --
            if (mRectMode.contains(x, y)) {
                Settings.gameMode = (++Settings.gameMode % Settings.GAME_MODES);
                Settings.save();
                createNewGame(true);
                return true;
            }

            // -- режим игры --
            if (mRectBf.contains(x, y)) {
                Settings.hardmode = !Settings.hardmode;
                Settings.save();
                createNewGame(true);
                return true;
            }

            // -- назад --
            if (mRectBack.contains(x, y)) {
                hide();
                return true;
            }

            return false;
        }

        public void draw(Canvas canvas) {
            // отступ от центра
            float right = Dimensions.surfaceWidth / 2 + Dimensions.spacing;
            // для выравнивания элементов
            float left = Dimensions.surfaceWidth / 2 - Dimensions.spacing;
            // смещение по вертикали
            float s = (int) (Dimensions.surfaceHeight * 0.02f);

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
            canvas.drawText(mTextAnimationsValue[Settings.animations ? 1 : 0],
                    right, mRectAnimations.bottom - s, mPaintValue);

            // цвет
            canvas.drawText(mTextColor, left, mRectColor.bottom - s, mPaintText);
            mPaintIcon.setColor(Colors.getTileColor());
            canvas.drawRect(mRectColorIcon, mPaintIcon);

            // цвет фона
            canvas.drawText(mTextColorMode, left, mRectColorMode.bottom - s, mPaintText);
            canvas.drawText(mTextColorModeValue[Settings.colorMode],
                    right, mRectColorMode.bottom - s, mPaintValue);

            // режим
            canvas.drawText(mTextMode, left, mRectMode.bottom - s, mPaintText);
            canvas.drawText(mTextModeValue[Settings.gameMode],
                    right, mRectMode.bottom - s, mPaintValue);

            // bf
            canvas.drawText(mTextBf, left, mRectBf.bottom - s, mPaintText);
            canvas.drawText(mTextBfValue[Settings.hardmode ? 1 : 0],
                    right, mRectBf.bottom - s, mPaintValue);

            // кнопка "назад"
            canvas.drawText(mTextBack, Dimensions.surfaceWidth / 2,
                    mRectBack.bottom - s, mPaintControls);
        }

        public void update() {
            mPaintText.setColor(Colors.getOverlayTextColor());
            mPaintControls.setColor(Colors.getOverlayTextColor());
            mPaintValue.setColor(Colors.menuTextValue);
        }

    }

    public class LeaderboardScreen extends BaseScreen {

        private Paint mPaintText;
        private Paint mPaintValue;
        private Paint mPaintTable;

        private String mTextWidth;                      // ширина поля
        private String mTextHeight;                     // высота поля
        private String mTextBf;                         // hardmode
        private String mTextBfValue[];
        private String mTextMode;                       // режим игры
        private String mTextModeValue[];
        private String mTextSort;                       // сортировка
        private String mTextSortValue[];
        private String mTextBack;                       // кнопка "назад"

        private Rect mRectWidth;
        private Rect mRectHeight;
        private Rect mRectMode;
        private Rect mRectBf;
        private Rect mRectSort;
        private Rect mRectBack;

        private ArrayList<TableItem> mTableItems = new ArrayList<TableItem>();

        private float mTableGuides[] = {
                Dimensions.surfaceWidth * 0.12f,
                Dimensions.surfaceWidth * 0.27f,
                Dimensions.surfaceWidth * 0.53f,
                Dimensions.surfaceWidth * 0.95f
        };
        private int mSettingsGuides[] = {
                (int) (Dimensions.surfaceWidth * 0.07f),
                (int) (Dimensions.surfaceWidth * 0.31f),
                (int) (Dimensions.surfaceWidth * 0.58f),
                (int) (Dimensions.surfaceWidth * 0.86f)
        };

        private float mTableMarginTop;

        private int mSortMode = 0;
        private int mGameWidth = Settings.gameWidth;
        private int mGameHeight = Settings.gameHeight;
        private int mGameMode = Settings.gameMode;
        private int mHardMode = Settings.hardmode ? 1 : 0;

        public LeaderboardScreen() {
            mPaintText = new Paint();
            mPaintText.setAntiAlias(Settings.antiAlias);
            mPaintText.setColor(Colors.getOverlayTextColor());
            mPaintText.setTextSize(Dimensions.menuFontSize * 0.9f);
            mPaintText.setTypeface(Settings.typeface);
            mPaintText.setTextAlign(Paint.Align.LEFT);

            mPaintValue = new Paint();
            mPaintValue.setAntiAlias(Settings.antiAlias);
            mPaintValue.setColor(Colors.menuTextValue);
            mPaintValue.setTextSize(Dimensions.menuFontSize * 0.9f);
            mPaintValue.setTypeface(Settings.typeface);
            mPaintValue.setTextAlign(Paint.Align.LEFT);

            mPaintTable = new Paint();
            mPaintTable.setAntiAlias(Settings.antiAlias);
            mPaintTable.setTextSize(Dimensions.menuFontSize * 0.9f);
            mPaintTable.setTypeface(Settings.typeface);
            mPaintTable.setTextAlign(Paint.Align.RIGHT);

            mTextWidth = getResources().getString(R.string.pref_width);
            mTextHeight = getResources().getString(R.string.pref_height);
            mTextBf = getResources().getString(R.string.pref_bf);
            mTextBfValue = getResources().getStringArray(R.array.difficulty_modes);
            mTextMode = getResources().getString(R.string.pref_mode);
            mTextModeValue = getResources().getStringArray(R.array.game_modes);
            mTextSort = getResources().getString(R.string.pref_sort);
            mTextSortValue = getResources().getStringArray(R.array.sort_types);
            mTextBack = getResources().getString(R.string.back);

            Rect r = new Rect();
            mPaintText.getTextBounds(mTextWidth, 0, 1, r);

            int lineHeight = r.height();
            int marginTop = (int) (Dimensions.surfaceHeight * 0.12f);
            int mLineGap = (int) (Dimensions.surfaceHeight * 0.075f);
            mTableMarginTop = marginTop + lineHeight + 3.4f * mLineGap;

            mRectMode = new Rect(0, marginTop, mSettingsGuides[2], marginTop + lineHeight);
            mRectMode.inset(0, -lineHeight / 3);
            mRectWidth = new Rect(mSettingsGuides[2], marginTop,
                    (int) Dimensions.surfaceWidth, marginTop + lineHeight);
            mRectWidth.inset(0, -lineHeight / 3);
            mRectBf = new Rect(0, marginTop + mLineGap,
                    mSettingsGuides[2], marginTop + mLineGap + lineHeight);
            mRectBf.inset(0, -lineHeight / 3);
            mRectHeight = new Rect(mSettingsGuides[2], marginTop + mLineGap,
                    (int) Dimensions.surfaceWidth, marginTop + mLineGap + lineHeight);
            mRectHeight.inset(0, -lineHeight / 3);
            mRectSort = new Rect(0, marginTop + 2 * mLineGap,
                    (int) Dimensions.surfaceWidth, marginTop + 2 * mLineGap + lineHeight);
            mRectSort.inset(0, -lineHeight / 3);
            mRectBack = new Rect(0, (int) Dimensions.surfaceHeight - 3 * lineHeight,
                    (int) Dimensions.surfaceWidth, (int) Dimensions.surfaceHeight);
        }

        public boolean onClick(int x, int y, int dx) {

            if (Math.abs(dx) < 15) {
                dx = 0;
            }

            if (mRectMode.contains(x, y)) {
                mGameMode = ++mGameMode % Settings.GAME_MODES;
                updateData();
                return true;
            }

            if (mRectBf.contains(x, y)) {
                mHardMode = ++mHardMode % 2;
                updateData();
                return true;
            }

            if (mRectSort.contains(x, y)) {
                mSortMode = ++mSortMode % 2;
                updateData();
                return true;
            }

            if (mRectWidth.contains(x, y)) {
                mGameWidth += ((dx == 0) ? 1 : Math.signum(dx));
                if (mGameWidth < Settings.MIN_GAME_WIDTH) {
                    mGameWidth = Settings.MAX_GAME_WIDTH;
                }
                if (mGameWidth > Settings.MAX_GAME_WIDTH) {
                    mGameWidth = Settings.MIN_GAME_WIDTH;
                }
                updateData();
                return true;
            }

            // -- высота поля --
            if (mRectHeight.contains(x, y)) {
                mGameHeight += ((dx == 0) ? 1 : Math.signum(dx));
                if (mGameHeight < Settings.MIN_GAME_HEIGHT) {
                    mGameHeight = Settings.MAX_GAME_HEIGHT;
                }
                if (mGameHeight > Settings.MAX_GAME_HEIGHT) {
                    mGameHeight = Settings.MIN_GAME_HEIGHT;
                }
                updateData();
                return true;
            }

            if (mRectBack.contains(x, y)) {
                hide();
                return true;
            }

            return false;
        }

        /**
         * Запрос данных из бд, обновление {@link #mTableItems}
         */
        public void updateData() {
            mTableItems.clear();

            Cursor q = dbHelper.query(mGameMode, mGameWidth, mGameHeight, mHardMode, mSortMode);

            if (q.moveToFirst()) {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                int indexMoves = q.getColumnIndex(DBHelper.KEY_MOVES);
                int indexTime = q.getColumnIndex(DBHelper.KEY_TIME);
                int indexTimestamp = q.getColumnIndex(DBHelper.KEY_TIMESTAMP);

                do {
                    TableItem item = new TableItem();

                    item.id = Integer.toString(q.getPosition() + 1);
                    item.moves = Integer.toString(q.getInt(indexMoves));
                    item.time = Tools.timeToString(q.getInt(indexTime));

                    Date d = new Date(q.getLong(indexTimestamp));
                    item.timestamp = format.format(d);

                    mTableItems.add(item);
                } while (q.moveToNext());
            }

            q.close();
        }

        @Override
        public boolean show() {
            mGameMode = Settings.gameMode;
            mHardMode = Settings.hardmode ? 1 : 0;
            mGameWidth = Settings.gameWidth;
            mGameHeight = Settings.gameHeight;
            mSortMode = 0;

            updateData();

            return super.show();
        }

        public void draw(Canvas canvas) {
            canvas.drawColor(Colors.getOverlayColor());

            mPaintText.setTextAlign(Paint.Align.LEFT);

            float s = Dimensions.menuFontSize * 0.29f;

            canvas.drawText(mTextMode, mSettingsGuides[0], mRectMode.bottom - s, mPaintText);
            canvas.drawText(mTextModeValue[mGameMode], mSettingsGuides[1],
                    mRectMode.bottom - s, mPaintValue);
            canvas.drawText(mTextBf, mSettingsGuides[0], mRectBf.bottom - s, mPaintText);
            canvas.drawText(mTextBfValue[mHardMode], mSettingsGuides[1],
                    mRectBf.bottom - s, mPaintValue);
            canvas.drawText(mTextSort, mSettingsGuides[0], mRectSort.bottom - s, mPaintText);
            canvas.drawText(mTextSortValue[mSortMode], mSettingsGuides[1],
                    mRectSort.bottom - s, mPaintValue);

            canvas.drawText(mTextWidth, mSettingsGuides[2], mRectWidth.bottom - s, mPaintText);
            canvas.drawText("" + mGameWidth, mSettingsGuides[3],
                    mRectWidth.bottom - s, mPaintValue);
            canvas.drawText(mTextHeight, mSettingsGuides[2], mRectHeight.bottom - s, mPaintText);
            canvas.drawText("" + mGameHeight, mSettingsGuides[3],
                    mRectHeight.bottom - s, mPaintValue);

            mPaintText.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(mTextBack, mRectBack.centerX(), mRectBack.centerY(), mPaintText);

            if (mTableItems.size() == 0) {
                canvas.drawText(getResources().getString(R.string.info_no_data),
                        Dimensions.surfaceWidth * .5f, mTableMarginTop, mPaintText);
                return;
            }

            // отступ новой строки
            float gap = Dimensions.surfaceHeight * 0.05f;

            for (int i = 0; i < mTableItems.size(); i++) {
                TableItem item = mTableItems.get(i);

                mPaintTable.setColor(Colors.getOverlayTextColor());
                canvas.drawText(item.id, mTableGuides[0],
                        mTableMarginTop + gap * i, mPaintTable);

                mPaintTable.setColor(Colors.menuTextValue);
                canvas.drawText(item.moves, mTableGuides[1],
                        mTableMarginTop + gap * i, mPaintTable);
                canvas.drawText(item.time, mTableGuides[2],
                        mTableMarginTop + gap * i, mPaintTable);
                canvas.drawText(item.timestamp, mTableGuides[3],
                        mTableMarginTop + gap * i, mPaintTable);
            }

        }

        @Override
        public boolean hide() {
            dbHelper.close();
            return super.hide();
        }

        public void update() {
            mPaintText.setColor(Colors.getOverlayTextColor());
        }

        /**
         * Хранит данные о записи в таблице рекордов
         */
        private class TableItem {
            public String id;
            public String moves;
            public String time;
            public String timestamp;
        }
    }

    /**
     * Вспомогательный класс для отображения оверлеев
     */
    public class FieldOverlay extends BaseScreen {

        private Paint mPaintBg;                         // Paint для отрисовки фона
        private Paint mPaintText;                       // Paint для отрисовки текста

        private Rect mRectBounds;                       // рассчет границ текста

        private String mCaption;                        // отображаемый текст
        private int mAnimFrames = 0;                    // кол-во кадров анимации

        /**
         * @param s текст надписи на оверлее
         */
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
            mPaintText.setTextSize(2.3f * Dimensions.interfaceFontSize);

            mRectBounds = new Rect();
            mPaintText.getTextBounds(mCaption, 0, mCaption.length(), mRectBounds);
        }

        @Override
        public boolean show() {
            if (Settings.animations) {
                mAnimFrames = Settings.screenAnimFrames;
            }
            return (mShow = true);
        }

        public void draw(Canvas canvas) {
            if (mAnimFrames > 0) {
                mAnimFrames--;
            }
            double alpha = Tools.easeOut(mAnimFrames, 0.0f, 1.0f, Settings.screenAnimFrames);
            mPaintBg.setAlpha((int) (Color.alpha(Colors.getOverlayColor()) * alpha));
            mPaintText.setAlpha((int) (255 * alpha));

            canvas.drawRect(mRectField, mPaintBg);
            canvas.drawText(mCaption,
                    Dimensions.fieldMarginLeft + Dimensions.fieldWidth / 2.0f,
                    Dimensions.fieldMarginTop + Dimensions.fieldHeight / 2.0f -
                            mRectBounds.centerY(),
                    mPaintText);
        }

        public void update() {
            mPaintBg.setColor(Colors.getOverlayColor());
            mPaintText.setColor(Colors.getOverlayTextColor());
        }

    } // FieldOverlay


    // TimerTask для таймера
    class GameClock extends TimerTask {

        @Override
        public void run() {
            if (!paused && !solved) {
                Game.incTime(TIME_CLOCK);
            }
        }
    }

}