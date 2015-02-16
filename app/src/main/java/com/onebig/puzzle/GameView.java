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

    private Context context;                            // контекст приложения

    private GameManager gameLoopThread;                 // главный поток

    private Tiles tiles = new Tiles();                  // массив спрайтов

    private SettingsScreen screenSettings;              // настройки
    private InterfaceScreen screenInterface;            // элементы интерфейса
    private FieldOverlay overlaySolved;                 // экран конца игры
    private FieldOverlay overlayPause;                  // экран паузы
    private RectF rectField;                            // область игрового поля

    public boolean solved = false;                      // состояние головоломки (решено/не решено)
    public boolean paused = false;                      // пауза

    private int startX;                                 // координата x и y начальной точки
    private int startY;                                 // вектора перемещения
    private boolean buttonResult = false;               // результат взаимодействия нажатия с элементом интерфейса

    public Timer gameClock;                             // таймер для отслеживания затраченного времени

    // конструктор
    public GameView(Context context) {
        super(context);
        this.context = context;
        gameLoopThread = new GameManager(this);

        SurfaceHolder holder = getHolder();

        holder.addCallback(new SurfaceHolder.Callback() {

            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                gameLoopThread.setRunning(false);
                while (retry) {
                    try {
                        gameLoopThread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                        Log.e("GameView.surfaceDestroyed", e.toString());
                    }
                }
            }

            public void surfaceCreated(SurfaceHolder holder) {
                createNewGame(false);

                screenSettings = new SettingsScreen();
                screenInterface = new InterfaceScreen();
                overlaySolved = new FieldOverlay(getResources().getString(R.string.info_win));
                overlayPause = new FieldOverlay(getResources().getString(R.string.info_pause));

                gameLoopThread.setRunning(true);
                try {
                    gameLoopThread.start();
                } catch (Exception e) {
                    Log.e("GameView.surfaceCreated", e.toString());
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
                startX = x;
                startY = y;
                if (screenSettings.isShown()) {
                    buttonResult = screenSettings.onClick(x, y);
                } else {
                    if (solved && rectField.contains(x, y)) {
                        solved = false;
                        createNewGame(true);
                    } else {
                        buttonResult = screenInterface.onClick(x, y);
                    }
                }
                break; // ACTION_DOWN

            case MotionEvent.ACTION_UP:
                int dx = x - startX;
                int dy = y - startY;

                if (Math.sqrt(dx * dx + dy * dy) > (Constraints.tileWidth / 6.0f) && !paused) {
                    tiles.move(startX, startY, Tools.direction(dx, dy));
                } else if (!buttonResult && paused && rectField.contains(x, y)) {
                    paused = false;
                    overlayPause.hide();
                } else if (!solved && !buttonResult) {
                    tiles.move(startX, startY, Tools.DIRECTION_DEFAULT);
                }
                break; // ACTION_UP

        }

        return true;
    }

    //
    public boolean onBackPressed() {
        return !screenSettings.isShown() || screenSettings.hide();
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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        // если создание новой игры не было инициировано пользователем,
        // загружаем сохрененную игру (если имеется)
        if (prefs.contains(Settings.KEY_GAME_ARRAY) && !isUser && Settings.saveGame) {
            String string_array = prefs.getString(Settings.KEY_GAME_ARRAY, Game.getGrid().toString());
            ArrayList<Integer> list = Tools.getIntegerArray(Arrays.asList(string_array.split("\\s*,\\s*")));
            if (list.size() == Game.getGridSize()) {
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
        rectField = new RectF(Constraints.fieldMarginLeft - Constraints.spacing, Constraints.fieldMarginTop
                - Constraints.spacing, Constraints.fieldMarginLeft + Constraints.fieldWidth + Constraints.spacing,
                Constraints.fieldMarginTop + Constraints.fieldHeight + Constraints.spacing);

        tiles.clear();

        Random rnd = new Random();
        int size = Game.getGridSize();                  // размер
        int animationType = rnd.nextInt(10);            // тип анимации
        int shift = size / 26 + 1;                      // коэффициент смещения анимации (группировка)
        for (int i = 0; i < size; i++) {
            int n = Game.getAt(i);
            if (n > 0) {
                Tile t = new Tile(this, n, i);
                if (Settings.animationEnabled) {
                    switch (animationType) {
                        case 0:
                            // все вместе
                            t.setAnimation(Tile.Animation.SCALE, 0);
                            break;
                        case 1:
                            // по порядку ячеек (сверху вниз)
                            t.setAnimation(Tile.Animation.SCALE, i / shift);
                            break;
                        case 2:
                            // в обратном порядке ячеек (снизу вверх)
                            t.setAnimation(Tile.Animation.SCALE, (size - i) / shift);
                            break;
                        case 3:
                            // случайный порядок
                            t.setAnimation(Tile.Animation.SCALE, rnd.nextInt(10 + 10 * (shift - 1)));
                            break;
                        case 4:
                            // по порядку (1, 2, 3, ..., n-1, n)
                            t.setAnimation(Tile.Animation.SCALE, n / shift);
                            break;
                        case 5:
                            // в обратном порядке (n, n-1, n-2, ..., 2, 1)
                            t.setAnimation(Tile.Animation.SCALE, (size - n) / shift);
                            break;
                        case 6:
                            // по строкам
                            t.setAnimation(Tile.Animation.SCALE, i / Settings.gameWidth * 3);
                            break;
                        case 7:
                            // по столбцам
                            t.setAnimation(Tile.Animation.SCALE, i % Settings.gameWidth * 3);
                            break;
                        case 8:
                            // по строкам в обратном порядке
                            t.setAnimation(Tile.Animation.SCALE, (Settings.gameHeight - i / Settings.gameWidth) * 3);
                            break;
                        case 9:
                            // по столбцам в обратном порядке
                            t.setAnimation(Tile.Animation.SCALE, (Settings.gameWidth - i % Settings.gameWidth) * 3);
                            break;
                    }
                }
                tiles.add(t);
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

    // функция рисования
    public void draw(Canvas canvas) {
        screenInterface.draw(canvas);                   // рисование элементов интерфейса

        // рисование спрайтов
        for (int i = 0; i < tiles.size(); i++) {
            tiles.get(i).draw(canvas);
        }

        if (solved) {
            overlaySolved.draw(canvas);                 // оверлей "solved"
        }
        if (screenSettings.isShown()) {
            screenSettings.draw(canvas);                // экран настроек
        } else if (paused && !solved) {
            overlayPause.draw(canvas);                  // оверлей "paused"
        }
    }

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
                    overlaySolved.show();
                }

                if (Settings.sounds) {
                    Sound.playSound();
                }

            } // if
        } // move

    }

    public class InterfaceScreen {

        private static final int BTN_COUNT = 3;
        private static final int BTN_NEW = 0;
        private static final int BTN_SETTINGS = 1;
        private static final int BTN_PAUSE = 2;
        private final int OVERLAY_FRAMES = (int) 1.5 * Settings.screenAnimFrames;

        private Paint paintButton;                      // Paint для рисования иконки приложения (вверху слева)
        private Paint paintField;                       // ... фон игрового поля
        private Paint paintTextButton;                  // ... текст кнопок интерфейса
        private Paint paintTextValue;                   // ... отображение текстов инфо
        private Paint paintTextCaption;                 // ... отображение заголовков инфо
        private Paint paintOverlay;                     // ... анимация подсветки кнопок

        private String textMode[];                      // режим игры
        private String textMoves;                       // ходы
        private String textTime;                        // время

        private RectF rectInfo;                         // ... инфо
        private RectF rectMode;                         // режим игры

        private Rect boundsTextButton = new Rect();     // границы текста на кнопках
        private Rect boundsTextValue = new Rect();      // границы текста инфо панели (режим игры)
        private Rect boundsTextCaption = new Rect();    // границы надписей инфо панели

        private ArrayList<Button> panelButtons = new ArrayList<Button>(); // кнопки вверху экрана
        private float panelHeight;

        public InterfaceScreen() {
            paintTextButton = new Paint();
            paintTextButton.setAntiAlias(Settings.antiAlias);
            paintTextButton.setColor(Colors.getTileTextColor());
            paintTextButton.setTypeface(Settings.typeface);
            paintTextButton.setTextAlign(Paint.Align.CENTER);
            paintTextButton.setTextSize(Constraints.interfaceFontSize);

            paintTextValue = new Paint(paintTextButton);
            paintTextValue.setTextSize(Constraints.interfaceFontSize * 1.9f);
            paintTextValue.setColor(Colors.getInfoTextColor());

            paintTextCaption = new Paint(paintTextButton);
            paintTextCaption.setTextSize(Constraints.interfaceFontSize * 1.4f);
            paintTextCaption.setTextAlign(Paint.Align.LEFT);

            paintField = new Paint();
            paintField.setAntiAlias(Settings.antiAlias);
            paintField.setColor(Colors.backgroundField);

            paintButton = new Paint();
            paintButton.setAntiAlias(Settings.antiAlias);

            paintOverlay = new Paint();
            paintOverlay.setColor(Colors.getBgColor());

            panelHeight = Constraints.surfaceHeight * 0.07f;
            float w = (Constraints.surfaceWidth / BTN_COUNT);

            panelButtons.add(new Button(new RectF(w * panelButtons.size(), 0.0f, w * (panelButtons.size() + 1), panelHeight), getResources().getString(R.string.action_new)));
            panelButtons.add(new Button(new RectF(w * panelButtons.size(), 0.0f, w * (panelButtons.size() + 1), panelHeight), getResources().getString(R.string.action_settings)));
            panelButtons.add(new Button(new RectF(w * panelButtons.size(), 0.0f, w * (panelButtons.size() + 1), panelHeight), getResources().getString(R.string.action_about)));

            float height = Constraints.surfaceHeight * 0.13f;
            float center = (Constraints.fieldMarginTop - Constraints.spacing - panelHeight) / 2.0f + panelHeight;
            rectInfo = new RectF(0.0f, center - height / 2.0f, Constraints.surfaceWidth, center + height / 2.0f);
            rectMode = new RectF(0.0f, center - height / 4.0f, Constraints.surfaceWidth * 0.5f, center + height / 4.0f);

            paintTextButton.getTextBounds("A", 0, 1, boundsTextButton);
            paintTextValue.getTextBounds("A", 0, 1, boundsTextValue);
            paintTextCaption.getTextBounds("A", 0, 1, boundsTextCaption);

            textMode = getResources().getStringArray(R.array.game_modes);
            textMoves = getResources().getString(R.string.info_moves);
            textTime = getResources().getString(R.string.info_time);
        } // constructor

        public boolean onClick(float x, float y) {

            // -- создание новой игры --
            if (panelButtons.get(BTN_NEW).contains(x, y)) {
                createNewGame(true);
                if (Settings.animationEnabled) {
                    panelButtons.get(BTN_NEW).setOverlay();
                }
                return true;
            }

            // -- отображение настроек --
            if (panelButtons.get(BTN_SETTINGS).contains(x, y)) {
                paused = Game.move(0) > 0;
                screenSettings.show();
                if (Settings.animationEnabled) {
                    panelButtons.get(BTN_SETTINGS).setOverlay();
                }
                return true;
            }

            // -- о программе --
            if (panelButtons.get(BTN_PAUSE).contains(x, y)) {
                if (!solved) {
                    paused = true;
                }
                if (!overlayPause.isShown()) {
                    overlayPause.show();
                }
                if (Settings.animationEnabled) {
                    panelButtons.get(BTN_PAUSE).setOverlay();
                }
                return true;
            }

            // -- режим игры --
            if (rectMode.contains(x, y)) {
                Settings.gameMode = (++Settings.gameMode % 2);
                Settings.save();
                createNewGame(true);
                return true;
            }

            return false;
        } // onClick

        public void draw(Canvas canvas) {
            canvas.drawColor(Colors.getBgColor());
            canvas.drawRect(rectField, paintField);
            canvas.drawRect(rectInfo, paintField);
            paintButton.setColor(Colors.getTileColor());
            canvas.drawRect(0.0f, 0.0f, Constraints.surfaceWidth, panelHeight, paintButton);

            // кнопки вверху экрана
            for (Button b : panelButtons) {
                if (b.frame > 5) {
                    float a = (float) Tools.easeOut(b.frame--, 0.0f, 1.0f, OVERLAY_FRAMES);
                    paintOverlay.setAlpha((int) (255 * (1.0f - a)));
                    canvas.drawRect(b.rect, paintOverlay);
                }
                canvas.drawText(b.caption, b.rect.centerX(), b.rect.centerY() - boundsTextButton.centerY(), paintTextButton);
            }

            // режим игры
            canvas.drawText(textMode[Settings.gameMode].toUpperCase(), Constraints.surfaceWidth * 0.25f, rectInfo.centerY() - boundsTextValue.centerY(), paintTextValue);

            float row1 = rectInfo.top + rectInfo.height() * 0.3f - boundsTextCaption.centerY();
            float row2 = rectInfo.top + rectInfo.height() * 0.7f - boundsTextCaption.centerY();
            // надписи
            paintTextCaption.setColor(Colors.getTileTextColor());
            paintTextCaption.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(textMoves, Constraints.surfaceWidth / 2.0f, row1, paintTextCaption);
            canvas.drawText(textTime, Constraints.surfaceWidth / 2.0f, row2, paintTextCaption);
            // значения
            paintTextCaption.setColor(Colors.getInfoTextColor());
            paintTextCaption.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(Integer.toString(Game.move(0)), Constraints.surfaceWidth - Constraints.spacing * 2.0f, row1, paintTextCaption);
            canvas.drawText(Tools.timeToString(Game.time(0)), Constraints.surfaceWidth - Constraints.spacing * 2.0f, row2, paintTextCaption);
        } // draw

        public void update() {
            paintTextValue.setColor(Colors.getInfoTextColor());
            paintTextButton.setColor(Colors.getTileTextColor());
            paintField.setColor(Colors.backgroundField);
            paintOverlay.setColor(Colors.getBgColor());
        }

        //
        private class Button {
            public RectF rect;
            public String caption;
            public int frame = 0;

            Button(RectF r, String c) {
                rect = r;
                caption = c;
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

        private Paint paintText;                        // заголовок элемента настроек
        private Paint paintValue;                       // значение элемента настроек
        private Paint paintControls;                    // кнопки управления (назад)
        private Paint paintIcon;                        // для графического представления (например, цвет плиток)

        private String textWidth;                       // ширина поля
        private String textWidthValue;
        private String textHeight;                      // высота поля
        private String textHeightValue;
        private String textAnimations;                  // анимации
        private String textAnimationsValue[];
        private String textColor;                       // цвет плиток
        private String textColorMode;                   // цвет фона
        private String textColorModeValue[];            // цветовая тема
        private String textMode;                        // режим игры
        private String textModeValue[];
        private String textBack;                        // кнопка "назад"

        private RectF fieldWidth;                       // граница элемента настройки ширины
        private RectF fieldHeight;                      // ... высоты
        private RectF fieldColor;                       // ... цвета
        private RectF fieldColorMode;                   // ... цвета фона
        private RectF iconColor;                        // ... визуальное представление цвета
        private RectF fieldAnimations;                  // ... анимации
        private RectF fieldMode;                        // ... режим игры
        private RectF fieldBack;                        // ... "назад"

        private boolean show = false;

        public SettingsScreen() {
            int h = (int) (Constraints.surfaceHeight * 0.082f); // промежуток между строками
            int ch = (int) (Constraints.surfaceHeight * 0.15f); // отступ от верхнего края экрана

            paintText = new Paint();
            paintText.setAntiAlias(Settings.antiAlias);
            paintText.setColor(Colors.getOverlayTextColor());
            paintText.setTextSize(Constraints.menuFontSize);
            paintText.setTypeface(Settings.typeface);
            paintText.setTextAlign(Paint.Align.RIGHT);

            paintValue = new Paint();
            paintValue.setAntiAlias(Settings.antiAlias);
            paintValue.setColor(Colors.menuTextValue);
            paintValue.setTextSize(Constraints.menuFontSize);
            paintValue.setTypeface(Settings.typeface);
            paintValue.setTextAlign(Paint.Align.LEFT);

            paintControls = new Paint(paintText);
            paintControls.setTextAlign(Paint.Align.CENTER);

            paintIcon = new Paint();
            paintIcon.setAntiAlias(Settings.antiAlias);

            Rect r = new Rect();
            textWidth = getResources().getString(R.string.pref_width);
            paintText.getTextBounds(textWidth, 0, textWidth.length(), r);

            // -- настройки --

            ch += h;
            textMode = getResources().getString(R.string.pref_mode);
            fieldMode = new RectF(0, ch, Constraints.surfaceWidth, ch + r.height());

            textModeValue = getResources().getStringArray(R.array.game_modes);

            ch += h;
            fieldWidth = new RectF(0, ch, Constraints.surfaceWidth, ch + r.height());

            textWidthValue = Integer.toString(Settings.gameWidth);

            ch += h;
            textHeight = getResources().getString(R.string.pref_height);
            fieldHeight = new RectF(0, ch, Constraints.surfaceWidth, ch + r.height());

            textHeightValue = Integer.toString(Settings.gameHeight);

            ch += h;
            textAnimations = getResources().getString(R.string.pref_animation);
            fieldAnimations = new RectF(0, ch, Constraints.surfaceWidth, ch + r.height());

            textAnimationsValue = getResources().getStringArray(R.array.animations);

            ch += h;
            textColorMode = getResources().getString(R.string.pref_color_mode);
            fieldColorMode = new RectF(0, ch, Constraints.surfaceWidth, ch + r.height());

            textColorModeValue = getResources().getStringArray(R.array.color_mode);

            ch += h;
            textColor = getResources().getString(R.string.pref_color);
            fieldColor = new RectF(0, ch, Constraints.surfaceWidth, ch + r.height());
            iconColor = new RectF(Constraints.surfaceWidth / 2 + 2.0f * Constraints.spacing, fieldColor.top, Constraints.surfaceWidth / 2 + 2.0f * Constraints.spacing + r.height(), fieldColor.bottom);
            iconColor.inset(-iconColor.width() / 4, -iconColor.width() / 4);

            // -- элементы управления --

            textBack = getResources().getString(R.string.back);
            fieldBack = new RectF(0, Constraints.surfaceHeight - h, Constraints.surfaceWidth, Constraints.surfaceHeight - h + r.height());
        }

        public boolean onClick(int x, int y) {
            // -- ширина поля --
            if (fieldWidth.contains(x, y)) {
                Settings.gameWidth++;
                if (Settings.gameWidth > Settings.MAX_GAME_WIDTH) {
                    Settings.gameWidth = (Settings.gameHeight > 2) ? 2 : 3;
                }
                Settings.save();
                createNewGame(true);
            }

            // -- высота поля --
            if (fieldHeight.contains(x, y)) {
                Settings.gameHeight++;
                if (Settings.gameHeight > Settings.MAX_GAME_HEIGHT) {
                    Settings.gameHeight = (Settings.gameWidth > 2) ? 2 : 3;
                }
                Settings.save();
                createNewGame(true);
            }

            // -- переключение анимаций --
            if (fieldAnimations.contains(x, y)) {
                Settings.animationEnabled = !Settings.animationEnabled;
                Settings.save();
            }

            // -- цвет спрайтов --
            if (fieldColor.contains(x, y)) {
                Settings.tileColor = (++Settings.tileColor % Colors.tiles.length);
                Settings.save();
            }

            // -- цвет фона --
            if (fieldColorMode.contains(x, y)) {
                Settings.colorMode = (++Settings.colorMode % Settings.COLOR_MODES);
                Settings.save();

                screenInterface.update();
                screenSettings.update();
                overlayPause.update();
                overlaySolved.update();
            }

            // -- режим игры --
            if (fieldMode.contains(x, y)) {
                Settings.gameMode = (++Settings.gameMode % Settings.GAME_MODES);
                Settings.save();
                createNewGame(true);
            }

            // -- назад --
            if (fieldBack.contains(x, y)) {
                screenSettings.hide();
            }

            return true;
        }

        public boolean show() {
            return (show = true);
        }

        public boolean hide() {
            return (show = false);
        }

        public boolean isShown() {
            return show;
        }

        public void draw(Canvas canvas) {
            float right = Constraints.surfaceWidth / 2 + Constraints.spacing; // отступ от центра
            float left = Constraints.surfaceWidth / 2 - Constraints.spacing;  // для выравнивания элементов
            float s = Constraints.spacing / 2.0f;       // смещение по вертикали

            // фон
            canvas.drawColor(Colors.getOverlayColor());

            // чтение настроек игры
            textWidthValue = Integer.toString(Settings.gameWidth);
            textHeightValue = Integer.toString(Settings.gameHeight);

            // ширина поля
            canvas.drawText(textWidth, left, fieldWidth.bottom - s, paintText);
            canvas.drawText(textWidthValue, right, fieldWidth.bottom - s, paintValue);

            // высота поля
            canvas.drawText(textHeight, left, fieldHeight.bottom - s, paintText);
            canvas.drawText(textHeightValue, right, fieldHeight.bottom - s, paintValue);

            // анимации
            canvas.drawText(textAnimations, left, fieldAnimations.bottom - s, paintText);
            canvas.drawText(textAnimationsValue[Settings.animationEnabled ? 1 : 0], right, fieldAnimations.bottom - s, paintValue);

            // цвет
            canvas.drawText(textColor, left, fieldColor.bottom - s, paintText);
            paintIcon.setColor(Colors.getTileColor());
            canvas.drawRect(iconColor, paintIcon);

            // цвет фона
            canvas.drawText(textColorMode, left, fieldColorMode.bottom - s, paintText);
            canvas.drawText(textColorModeValue[Settings.colorMode], right, fieldColorMode.bottom - s, paintValue);

            // режим
            canvas.drawText(textMode, left, fieldMode.bottom - s, paintText);
            canvas.drawText(textModeValue[Settings.gameMode], right, fieldMode.bottom - s, paintValue);

            // кнопка "назад"
            canvas.drawText(textBack, Constraints.surfaceWidth / 2, fieldBack.bottom - s, paintControls);
        }

        public void update() {
            paintText.setColor(Colors.getOverlayTextColor());
            paintControls.setColor(Colors.getOverlayTextColor());
            paintValue.setColor(Colors.menuTextValue);
        }

    }

    public class FieldOverlay {

        private Paint paintField;                       // Paint для отрисовки фона
        private Paint paintText;                        // Paint для отрисовки текста

        private Rect boundsText;                        // рассчет границ текста

        private String message;                         // отображаемый текст
        private int animationFrames = 0;                // кол-во кадров анимации
        private boolean show = false;                   // видимость

        public FieldOverlay(String s) {
            message = s;
            paintField = new Paint();
            paintField.setAntiAlias(Settings.antiAlias);
            paintField.setColor(Colors.getOverlayColor());

            paintText = new Paint();
            paintText.setAntiAlias(Settings.antiAlias);
            paintText.setColor(Colors.getOverlayTextColor());
            paintText.setTypeface(Settings.typeface);
            paintText.setTextAlign(Paint.Align.CENTER);
            paintText.setTextSize(2.3f * Constraints.interfaceFontSize);

            boundsText = new Rect();
            paintText.getTextBounds(message, 0, message.length(), boundsText);
        }

        public void show() {
            if (Settings.animationEnabled) {
                animationFrames = Settings.screenAnimFrames;
            }
            show = true;
        }

        public void hide() {
            show = false;
        }

        public boolean isShown() {
            return show;
        }

        public void draw(Canvas canvas) {
            if (animationFrames > 0) {
                animationFrames--;
            }
            float alpha = (float) Tools.easeOut(animationFrames, 0.0f, 1.0f, Settings.screenAnimFrames);
            paintField.setAlpha((int) (Color.alpha(Colors.getOverlayColor()) * alpha));
            paintText.setAlpha((int) (255 * alpha));

            canvas.drawRect(rectField, paintField);
            canvas.drawText(
                    message,
                    Constraints.fieldMarginLeft + Constraints.fieldWidth / 2.0f,
                    Constraints.fieldMarginTop + Constraints.fieldHeight / 2.0f - boundsText.centerY(),
                    paintText
            );
        }

        public void update() {
            paintField.setColor(Colors.getOverlayColor());
            paintText.setColor(Colors.getOverlayTextColor());
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