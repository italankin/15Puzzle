package com.italankin.fifteen;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.italankin.fifteen.views.FieldOverlay;
import com.italankin.fifteen.views.FieldView;
import com.italankin.fifteen.views.InfoPanelView;
import com.italankin.fifteen.views.LeaderboardView;
import com.italankin.fifteen.views.SettingsView;
import com.italankin.fifteen.views.TopPanelView;

import java.util.List;
import java.util.Random;

public class GameSurface extends SurfaceView implements TopPanelView.Callbacks, SurfaceHolder.Callback {

    private static final int BTN_NEW = 0;
    private static final int BTN_SETTINGS = 1;
    private static final int BTN_LEADERBOARD = 2;
    private static final int BTN_PAUSE = 3;

    /**
     * Задержка анимации между тайлами в кадрах
     */
    private static final int ANIM_WINDOW_FRAMES = 5;
    private static final int ANIM_TYPE_COUNT = 16;

    private static final int ANIM_TYPE_ALL = 0;
    private static final int ANIM_TYPE_INDEX_ASC = 1;
    private static final int ANIM_TYPE_INDEX_DESC = 2;
    private static final int ANIM_TYPE_RANDOM = 3;
    private static final int ANIM_TYPE_NUMBER_ASC = 4;
    private static final int ANIM_TYPE_NUMBER_DESC = 5;
    private static final int ANIM_TYPE_ROW = 6;
    private static final int ANIM_TYPE_COLUMN = 7;
    private static final int ANIM_TYPE_ROW_REVERSE = 8;
    private static final int ANIM_TYPE_COLUMN_REVERSE = 9;
    private static final int ANIM_TYPE_TOP_LEFT_CORNER = 10;
    private static final int ANIM_TYPE_TOP_RIGHT_CORNER = 11;
    private static final int ANIM_TYPE_BOTTOM_LEFT_CORNER = 12;
    private static final int ANIM_TYPE_BOTTOM_RIGHT_CORNER = 13;
    private static final int ANIM_TYPE_FROM_CENTER = 14;
    private static final int ANIM_TYPE_TO_CENTER = 15;

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

    private Paint mDebugPaint;

    /**
     * Координата x начальной точки жеста
     */
    private int mStartX;
    /**
     * Координата y начальной точки жеста
     */
    private int mStartY;
    private boolean mTrail = false;
    private int animCurrentIndex = ANIM_TYPE_ALL;
    private long lastSolvedTimestamp = 0;

    /**
     * Конструктор по умолчанию
     *
     * @param context контекст приложения
     */
    public GameSurface(Context context) {
        super(context);

        dbHelper = new DBHelper(context);
        mResources = getResources();

        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mGameLoopThread = new GameManager(this, holder);

        Dimensions.update(this.getWidth(), this.getHeight(), 1.0f);

        mTopPanel = new TopPanelView();
        mTopPanel.addButton(BTN_NEW, mResources.getString(R.string.action_new));
        mTopPanel.addButton(BTN_SETTINGS, mResources.getString(R.string.action_settings));
        mTopPanel.addButton(BTN_LEADERBOARD, mResources.getString(R.string.action_leaderboard));
        mTopPanel.addButton(BTN_PAUSE, mResources.getString(R.string.action_pause));
        mTopPanel.addCallback(this);

        mInfoPanel = new InfoPanelView(mResources);

        mField = new FieldView(mRectField);

        mSettings = new SettingsView(mResources);
        mSettings.addCallback(needUpdate -> {
            if (needUpdate) {
                createNewGame(true);
            }
            updateViews();
        });
        mLeaderboard = new LeaderboardView(dbHelper, getResources());
        mLeaderboard.addCallback(() -> {
            updateViews();
        });
        mSolvedOverlay = new FieldOverlay(mRectField, mResources.getString(R.string.info_win));
        mPauseOverlay = new FieldOverlay(mRectField, mResources.getString(R.string.info_pause));

        Game.addCallback(() -> {
            mSolvedOverlay.show();
            dbHelper.insert(
                    Settings.gameMode,
                    Settings.gameWidth,
                    Settings.gameHeight,
                    Settings.hardmode ? 1 : 0,
                    Game.getMoves() + 1,
                    Game.getTime());
            lastSolvedTimestamp = System.currentTimeMillis();
        });

        try {
            mGameLoopThread.start();
            mGameLoopThread.setRunning(true);
        } catch (Exception e) {
            Tools.log("surfaceCreated: " + e.toString());
        }

        mDebugPaint = new Paint();
        mDebugPaint.setTypeface(Settings.typeface);
        mDebugPaint.setTextSize(Dimensions.interfaceFontSize * .75f);
        mDebugPaint.setTextAlign(Paint.Align.LEFT);
        mDebugPaint.setColor(Color.RED);

        updateViews();

        createNewGame(false);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mGameLoopThread.setRunning(false);
        while (true) {
            try {
                mGameLoopThread.join();
                break;
            } catch (InterruptedException e) {
                Tools.log("surfaceDestroyed: " + e.toString());
            }
        }
        mGameLoopThread = null;
    }

    /**
     * Обработка событий нажатия на экран
     */
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mStartX = x;
                mStartY = y;
                mTrail = isFieldFullyVisible() && mRectField.contains(x, y) && mField.emptySpaceAt(x, y);
                return true;
            }

            case MotionEvent.ACTION_MOVE: {
                if (Game.isPaused() || Game.isSolved() || !isFieldFullyVisible()) {
                    return true;
                }
                if (mTrail) {
                    mField.moveTiles(x, y, Tools.DIRECTION_DEFAULT, false);
                    return true;
                }
                int dx = x - mStartX;
                int dy = y - mStartY;
                float minSwipeDistance = Dimensions.tileSize / 6.0f;
                if (Math.abs(dx) > minSwipeDistance || Math.abs(dy) > minSwipeDistance) {
                    mField.moveTiles(mStartX, mStartY, Tools.direction(dx, dy));
                    event.setAction(MotionEvent.ACTION_CANCEL);
                }
                return true;
            }

            case MotionEvent.ACTION_UP: {
                if (mTrail) {
                    return true;
                }

                int dx = x - mStartX;
                int dy = y - mStartY;

                if (mLeaderboard.isShown()) {
                    mLeaderboard.onClick(mStartX, mStartY, dx);
                    return true;
                } else if (mSettings.isShown()) {
                    mSettings.onClick(mStartX, mStartY, dx);
                    return true;
                } else if (Game.isSolved() && mRectField.contains(x, y)) {
                    createNewGame(true);
                    return true;
                } else if (mTopPanel.onClick(x, y) || mInfoPanel.onClick(x, y)) {
                    return true;
                }

                if (Math.sqrt(dx * dx + dy * dy) > (Dimensions.tileSize / 4.0f) && !Game.isPaused()) {
                    mField.moveTiles(mStartX, mStartY, Tools.direction(dx, dy));
                } else if (Game.isPaused() && mRectField.contains(x, y)) {
                    Game.setPaused(false);
                    mPauseOverlay.hide();
                    mField.update();
                } else if (!Game.isSolved()) {
                    mField.moveTiles(mStartX, mStartY, Tools.DIRECTION_DEFAULT);
                }
                return true;
            }

            default:
                return true;
        }
    }

    @Override
    public void onTopPanelButtonClick(int id) {
        switch (id) {
            case BTN_NEW:
                createNewGame(true);
                break;

            case BTN_SETTINGS:
                Game.setPaused(Game.getMoves() > 0);
                if (Game.isPaused()) {
                    mPauseOverlay.show();
                }
                mField.update();
                mSettings.show();
                break;

            case BTN_LEADERBOARD:
                Game.setPaused(Game.getMoves() > 0);
                if (Game.isPaused()) {
                    mPauseOverlay.show();
                }
                mField.update();
                mLeaderboard.show();
                break;

            case BTN_PAUSE:
                if (!Game.isSolved()) {
                    Game.invertPaused();
                }
                mField.update();
                if (mPauseOverlay.isShown()) {
                    mPauseOverlay.hide();
                } else {
                    mPauseOverlay.show();
                }
                break;
        }
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
        if (mGameLoopThread != null) {
            mGameLoopThread.setRunning(false);
        }
    }

    /**
     * Рендер изображения
     */
    public void draw(Canvas canvas, long elapsed, String info) {
        super.draw(canvas);

        boolean paused = Game.isPaused();
        boolean solved = Game.isSolved();

        if (!paused && !solved) {
            Game.incTime(elapsed);
        }

        canvas.drawColor(Colors.getBackgroundColor());

        mTopPanel.draw(canvas, elapsed);
        mInfoPanel.draw(canvas, elapsed);
        mField.draw(canvas, elapsed);

        if (solved && mSolvedOverlay.isShown() && !mSettings.isShown()) {
            mSolvedOverlay.draw(canvas, elapsed);
        }

        if (mLeaderboard.isShown()) {
            mLeaderboard.draw(canvas, elapsed);
        } else if (mSettings.isShown()) {
            mSettings.draw(canvas, elapsed);
        } else if (paused && !solved && mPauseOverlay.isShown()) {
            mPauseOverlay.draw(canvas, elapsed);
        }

        if (info != null) {
            canvas.drawText(info, 0, Dimensions.surfaceHeight, mDebugPaint);
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
        if (Settings.newGameDelay &&
                ((System.currentTimeMillis() - lastSolvedTimestamp) < Settings.NEW_GAME_DELAY)) {
            return;
        }

        mPauseOverlay.hide();
        mSolvedOverlay.hide();

        Game.create(Settings.gameWidth, Settings.gameHeight);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        // если создание новой игры не было инициировано пользователем,
        // загружаем сохрененную игру (если имеется)
        if (prefs.contains(Settings.KEY_GAME_ARRAY) && !isUser && Settings.saveGame) {
            String strings = prefs.getString(Settings.KEY_GAME_ARRAY, null);
            if (strings != null) {
                List<Integer> list = Tools.getIntegerArray(strings.split("\\s*,\\s*"));
                if (list.size() == Game.getSize()) {
                    Game.load(list,
                            prefs.getInt(Settings.KEY_GAME_MOVES, 0),
                            prefs.getLong(Settings.KEY_GAME_TIME, 0));
                }
            }

            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(Settings.KEY_GAME_ARRAY);
            editor.remove(Settings.KEY_GAME_MOVES);
            editor.remove(Settings.KEY_GAME_TIME);
            editor.apply();

            if (Game.getMoves() > 0) {
                Game.setPaused(true);
                mPauseOverlay.show();
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
        int size = Game.getSize();
        int animationType = animCurrentIndex++ % ANIM_TYPE_COUNT;
        int shift = size / 26 + 1; // коэффициент смещения анимации (группировка тайлов по shift штук)
        int delay; // задержка появляения
        for (int index = 0; index < size; index++) {
            int number = Game.getAt(index);
            if (number > 0) {
                Tile t = new Tile(number, index);
                if (Settings.animations && !mSettings.isShown() && !mLeaderboard.isShown()) {
                    switch (animationType) {
                        case ANIM_TYPE_INDEX_ASC:
                            delay = index / shift;
                            break;
                        case ANIM_TYPE_INDEX_DESC:
                            delay = (size - index) / shift;
                            break;
                        case ANIM_TYPE_RANDOM:
                            delay = rnd.nextInt(10 + 10 * (shift - 1));
                            break;
                        case ANIM_TYPE_NUMBER_ASC:
                            delay = number / shift;
                            break;
                        case ANIM_TYPE_NUMBER_DESC:
                            delay = (size - number) / shift;
                            break;
                        case ANIM_TYPE_ROW:
                            delay = index / Settings.gameWidth * ANIM_WINDOW_FRAMES;
                            break;
                        case ANIM_TYPE_COLUMN:
                            delay = index % Settings.gameWidth * ANIM_WINDOW_FRAMES;
                            break;
                        case ANIM_TYPE_ROW_REVERSE:
                            delay = (Settings.gameHeight - index / Settings.gameWidth) * ANIM_WINDOW_FRAMES;
                            break;
                        case ANIM_TYPE_COLUMN_REVERSE:
                            delay = (Settings.gameWidth - index % Settings.gameWidth) * ANIM_WINDOW_FRAMES;
                            break;
                        case ANIM_TYPE_TOP_LEFT_CORNER:
                            delay = fromPoint(0, 0, index);
                            break;
                        case ANIM_TYPE_TOP_RIGHT_CORNER:
                            delay = fromPoint(Settings.gameWidth - 1, 0, index);
                            break;
                        case ANIM_TYPE_BOTTOM_LEFT_CORNER:
                            delay = fromPoint(0, Settings.gameHeight - 1, index);
                            break;
                        case ANIM_TYPE_BOTTOM_RIGHT_CORNER:
                            delay = fromPoint(Settings.gameWidth - 1, Settings.gameHeight - 1, index);
                            break;
                        case ANIM_TYPE_FROM_CENTER: {
                            float x0 = Settings.gameWidth / 2f - .5f;
                            float y0 = Settings.gameHeight / 2f - .5f;
                            delay = fromPointF(x0, y0, index);
                            break;
                        }
                        case ANIM_TYPE_TO_CENTER: {
                            float x0 = Settings.gameWidth / 2f - .5f;
                            float y0 = Settings.gameHeight / 2f - .5f;
                            delay = (size - fromPointF(x0, y0, index)) / 2;
                            break;
                        }
                        case ANIM_TYPE_ALL:
                        default:
                            // все вместе
                            delay = 0;
                    }
                    t.animateAppearance(delay * Settings.TILE_ANIM_FRAME_MULTIPLIER);
                }
                mField.addTile(t);
            }
        }
    } // END createNewGame

    private boolean isFieldFullyVisible() {
        return !(mLeaderboard.isShown() || mSettings.isShown() || mSolvedOverlay.isShown() || mPauseOverlay.isShown());
    }

    private static int fromPoint(int x0, int y0, int index) {
        int x1 = index % Settings.gameWidth;
        int y1 = index / Settings.gameWidth;
        int distance = Math.max(Math.abs(x1 - x0), Math.abs(y1 - y0));
        return (distance + 1) * ANIM_WINDOW_FRAMES;
    }

    private static int fromPointF(float x0, float y0, int index) {
        int x1 = index % Settings.gameWidth;
        int y1 = index / Settings.gameWidth;
        int distance = (int) Math.floor(Math.max(Math.abs(x1 - x0), Math.abs(y1 - y0)));
        return (distance + 1) * ANIM_WINDOW_FRAMES;
    }
}
