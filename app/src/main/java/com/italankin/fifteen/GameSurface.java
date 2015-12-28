package com.italankin.fifteen;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.italankin.fifteen.views.FieldOverlay;
import com.italankin.fifteen.views.FieldView;
import com.italankin.fifteen.views.InfoPanelView;
import com.italankin.fifteen.views.LeaderboardView;
import com.italankin.fifteen.views.SettingsView;
import com.italankin.fifteen.views.TopPanelView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameSurface extends SurfaceView implements TopPanelView.Callbacks, SurfaceHolder.Callback,
        InfoPanelView.Callbacks {

    private static final int BTN_NEW = 0;
    private static final int BTN_SETTINGS = 1;
    private static final int BTN_LEADERBOARD = 2;
    private static final int BTN_PAUSE = 3;

    /**
     * Главный поток для перерисовки изображения
     */
    private GameManager mGameLoopThread;
    /**
     * Помощник для работы с базой данных
     */
    private DBHelper dbHelper;

    /**
     * Ресурсы приложения
     */
    private Resources mResources;

    /**
     * Верхняя панель
     */
    private TopPanelView mTopPanel;
    /**
     * Инфо панель
     */
    private InfoPanelView mInfoPanel;
    private FieldView mField;
    /**
     * Экран настроек
     */
    private SettingsView mSettings;
    /**
     * Экран рекордов
     */
    private LeaderboardView mLeaderboard;
    /**
     * Оверлей конца игры
     */
    private FieldOverlay mSolvedOverlay;
    /**
     * Оверлей паузы
     */
    private FieldOverlay mPauseOverlay;

    /**
     * Область игрового поля
     */
    private RectF mRectField = new RectF();

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
    public static final int TIME_CLOCK = 100;

    /**
     * Конструктор по умолчанию
     *
     * @param context контекст приложения
     */
    public GameSurface(Context context) {
        super(context);

        mGameLoopThread = new GameManager(this);

        dbHelper = new DBHelper(context);
        mResources = getResources();

        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Dimensions.update(this.getWidth(), this.getHeight(), 1.0f);

        mTopPanel = new TopPanelView();
        mTopPanel.addButton(BTN_NEW, mResources.getString(R.string.action_new));
        mTopPanel.addButton(BTN_SETTINGS, mResources.getString(R.string.action_settings));
        mTopPanel.addButton(BTN_LEADERBOARD, mResources.getString(R.string.action_leaderboard));
        mTopPanel.addButton(BTN_PAUSE, mResources.getString(R.string.action_about));
        mTopPanel.addCallback(this);

        mInfoPanel = new InfoPanelView(mResources);
        mInfoPanel.addCallback(this);

        mField = new FieldView(mRectField);

        mSettings = new SettingsView(mResources);
        mSettings.addCallback(needUpdate -> {
            if (needUpdate) {
                createNewGame(true);
            }
            updateViews();
        });
        mLeaderboard = new LeaderboardView(dbHelper, getResources());
        mLeaderboard.addCallback(this::updateViews);
        mSolvedOverlay = new FieldOverlay(mRectField, mResources.getString(R.string.info_win));
        mPauseOverlay = new FieldOverlay(mRectField, mResources.getString(R.string.info_pause));

        Game.addCallback(() -> {
            if (gameClock != null) {
                gameClock.cancel();
                gameClock = null;
            }
            mSolvedOverlay.show();
            dbHelper.insert(
                    Settings.gameMode,
                    Settings.gameWidth,
                    Settings.gameHeight, Settings.hardmode ? 1 : 0,
                    Game.getMoves() + 1,
                    Game.getTime());
        });

        try {
            mGameLoopThread.start();
            mGameLoopThread.setRunning(true);
        } catch (Exception e) {
            Tools.log("surfaceCreated: " + e.toString());
        }

        updateViews();

        createNewGame(false);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        mGameLoopThread.setRunning(false);
        while (retry) {
            try {
                mGameLoopThread.join();
                retry = false;
            } catch (InterruptedException e) {
                Tools.log("surfaceDestroyed: " + e.toString());
            }
        }
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
                if (!mSettings.isShown() && !mLeaderboard.isShown()) {
                    if (Game.isSolved() && mRectField.contains(x, y)) {
                        createNewGame(true);
                    } else {
                        mTopPanel.onClick(x, y);
                        mInfoPanel.onClick(x, y);
                    }
                }
                break; // ACTION_DOWN

            case MotionEvent.ACTION_UP:
                int dx = x - mStartX;
                int dy = y - mStartY;

                if (mLeaderboard.isShown()) {
                    mLeaderboard.onClick(mStartX, mStartY, dx);
                    return true;
                }
                if (mSettings.isShown()) {
                    mSettings.onClick(mStartX, mStartY, dx);
                    return true;
                }

                if (Math.sqrt(dx * dx + dy * dy) > (Dimensions.tileSize / 6.0f) && !Game.isPaused()) {
                    mField.moveTiles(mStartX, mStartY, Tools.direction(dx, dy));
                } else if (Game.isPaused() && mRectField.contains(x, y)) {
                    Game.setPaused(false);
                    mPauseOverlay.hide();
                } else if (!Game.isSolved()) {
                    mField.moveTiles(mStartX, mStartY, Tools.DIRECTION_DEFAULT);
                }
                break; // ACTION_UP

        }

        return true;
    }

    @Override
    public void onTopPanelButtonClick(int id) {
        switch (id) {
            case BTN_NEW:
                createNewGame(true);
                break;

            case BTN_SETTINGS:
                Game.setPaused(Game.getMoves() > 0);
                mSettings.show();
                break;

            case BTN_LEADERBOARD:
                Game.setPaused(Game.getMoves() > 0);
                mLeaderboard.show();
                break;

            case BTN_PAUSE:
                if (!Game.isSolved()) {
                    Game.invertPaused();
                }
                if (mPauseOverlay.isShown()) {
                    mPauseOverlay.hide();
                } else {
                    mPauseOverlay.show();
                }
                break;
        }
    }

    @Override
    public void onModeClick() {
        Settings.gameMode = (++Settings.gameMode % Settings.GAME_MODES);
        Settings.save();
        createNewGame(true);
    }

    /**
     * Нажатие клавиши "Назад" на устройстве
     */
    public boolean onBackPressed() {
        return mSettings.hide() || mLeaderboard.hide();
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
     * Рендер изображения
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        canvas.drawColor(Colors.getBackgroundColor());

        mTopPanel.draw(canvas);
        mInfoPanel.draw(canvas);
        mField.draw(canvas);

        if (Game.isSolved() && mSolvedOverlay.isShown()) {
            mSolvedOverlay.draw(canvas);                 // оверлей "solved"
        }

        if (mLeaderboard.isShown()) {
            mLeaderboard.draw(canvas);
        } else if (mSettings.isShown()) {
            mSettings.draw(canvas);                // экран настроек
        } else if (Game.isPaused() && !Game.isSolved() && mPauseOverlay.isShown()) {
            mPauseOverlay.draw(canvas);                  // оверлей "paused"
        }
    }

    public void updateViews() {
        mTopPanel.update();
        mInfoPanel.update();
        mField.update();
        mLeaderboard.update();
        mSettings.update();
        mPauseOverlay.update();
        mSolvedOverlay.update();
    }

    /**
     * Создание новой игры
     *
     * @param isUser <b>true</b>, если действие было вызвано пользователем
     */
    private void createNewGame(boolean isUser) {
        Game.create(Settings.gameWidth, Settings.gameHeight);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
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
                Game.setPaused(true);
            }

        }

        // вычисление размеров
        Dimensions.update(this.getWidth(), this.getHeight(), 1.0f);
        mRectField.set(
                Dimensions.fieldMarginLeft - Dimensions.spacing,
                Dimensions.fieldMarginTop - Dimensions.spacing,
                Dimensions.fieldMarginLeft + Dimensions.fieldWidth + Dimensions.spacing,
                Dimensions.fieldMarginTop + Dimensions.fieldHeight + Dimensions.spacing);

        mField.clear();

        Random rnd = new Random();
        int size = Game.getSize();                      // размер
        int animationType = rnd.nextInt(10);            // тип анимации
        int shift = size / 26 + 1;                      // коэффициент смещения анимации (группировка)
        int delay;                                  // задержка появляения
        for (int index = 0; index < size; index++) {
            int number = Game.getAt(index);
            if (number > 0) {
                Tile t = new Tile(number, index);
                if (Settings.animations && !mSettings.isShown() && !mLeaderboard.isShown()) {
                    switch (animationType) {
                        case 1:
                            // по порядку ячеек (сверху вниз)
                            delay = index / shift;
                            break;
                        case 2:
                            // в обратном порядке ячеек (снизу вверх)
                            delay = (size - index) / shift;
                            break;
                        case 3:
                            // случайный порядок
                            delay = rnd.nextInt(10 + 10 * (shift - 1));
                            break;
                        case 4:
                            // по порядку (1, 2, 3, ..., n-1, n)
                            delay = number / shift;
                            break;
                        case 5:
                            // в обратном порядке (n, n-1, n-2, ..., 2, 1)
                            delay = (size - number) / shift;
                            break;
                        case 6:
                            // по строкам
                            delay = index / Settings.gameWidth * 5; // 5 - задержка в кадрах между группами
                            break;
                        case 7:
                            // по столбцам
                            delay = index % Settings.gameWidth * 5;
                            break;
                        case 8:
                            // по строкам в обратном порядке
                            delay = (Settings.gameHeight - index / Settings.gameWidth) * 5;
                            break;
                        case 9:
                            // по столбцам в обратном порядке
                            delay = (Settings.gameWidth - index % Settings.gameWidth) * 5;
                            break;
                        default:
                            // все вместе
                            delay = 0;
                    }
                    t.setAnimation(Tile.Animation.SCALE, delay);
                }
                mField.addTile(t);
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

    // TimerTask для таймера
    class GameClock extends TimerTask {

        @Override
        public void run() {
            if (!Game.isPaused() && !Game.isSolved()) {
                Game.incTime(TIME_CLOCK);
            }
        }
    }

}