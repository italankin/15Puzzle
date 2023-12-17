package com.italankin.fifteen;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import com.italankin.fifteen.export.ExportCallback;
import com.italankin.fifteen.game.Game;
import com.italankin.fifteen.statistics.StatisticsManager;
import com.italankin.fifteen.views.*;
import com.italankin.fifteen.views.help.HelpOverlay;
import com.italankin.fifteen.views.overlay.FieldTextOverlay;

public class GameSurface extends View implements TopPanelView.Callback {

    private static final int BTN_NEW = 0;
    private static final int BTN_SETTINGS = 1;
    private static final int BTN_LEADERBOARD = 2;
    private static final int BTN_FOUR = 3;

    private static final long LAST_IMPOSSIBLE_MOVE_WINDOW_MILLIS = 100;
    private static final float SWIPE_FACTOR = 3;

    private final DBHelper dbHelper;
    private final ExportCallback exportCallback;

    private final Resources mResources;

    private NewAppBannerView mNewAppBanner;
    private TopPanelView mTopPanel;
    private InfoPanelView mInfoPanel;
    private FieldView mField;
    private SettingsView mSettings;
    private StatisticsView mStatistics;
    private LeaderboardView mLeaderboard;
    private FieldTextOverlay mSolvedOverlay;
    private FieldTextOverlay mPauseOverlay;
    private HardModeView mHardModeView;
    private HelpOverlay mHelpOverlay;

    private final RectF mRectField = new RectF();

    private final StatisticsManager statisticsManager;

    private float mGestureStartX;
    private float mGestureStartY;
    private boolean mGestureTrail = false;
    private boolean mSecondaryPointer = false;
    private long lastSolvedTimestamp = 0;
    private long lastOnDrawTimestamp = 0;

    private int lastImpossibleMoveIndex = -1;
    private long lastImpossibleMoveTimestamp = 0;

    private final TileAppearAnimator tileAppearAnimator = new TileAppearAnimator();

    public GameSurface(Context context, ExportCallback exportCallback, DBHelper dbHelper) {
        super(context);

        this.dbHelper = dbHelper;
        this.statisticsManager = StatisticsManager.getInstance(context);
        this.exportCallback = exportCallback;

        mResources = context.getResources();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Dimensions.update(this.getMeasuredWidth(), this.getMeasuredHeight());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mField == null) {
            init();
        }
    }

    private void init() {
        mNewAppBanner = new NewAppBannerView(getContext());
        if (Settings.showNewAppBanner) {
            Settings.showNewAppBanner = false;
            Settings.save();
            mNewAppBanner.show();
        }
        mTopPanel = new TopPanelView();
        mTopPanel.addButton(BTN_NEW, mResources.getString(R.string.action_new));
        mTopPanel.addButton(BTN_SETTINGS, mResources.getString(R.string.action_settings));
        mTopPanel.addButton(BTN_LEADERBOARD, mResources.getString(R.string.action_leaderboard));
        int btnFourTitle = Settings.stats ? R.string.action_stats : R.string.action_pause;
        mTopPanel.addButton(BTN_FOUR, mResources.getString(btnFourTitle));
        mTopPanel.setCallback(this);

        mInfoPanel = new InfoPanelView(mResources);
        mInfoPanel.addCallback(() -> {
            pauseGame();
            GameState.get().help = true;
            RectF window = new RectF(0, 0, Dimensions.surfaceWidth, Dimensions.surfaceHeight);
            mHelpOverlay = new HelpOverlay(getResources(), tileAppearAnimator, window, mRectField);
            mHelpOverlay.addCallback(() -> Tools.openUrl(getContext(), R.string.help_how_to_play_url));
            mHelpOverlay.show();
        });

        mField = new FieldView(mRectField);

        mHardModeView = new HardModeView(mResources);
        mHardModeView.setCallbacks(() -> {
            GameState state = GameState.get();
            state.hardmodeSolved = state.game.isSolved();
            if (state.isSolved()) {
                onGameSolve(state);
                return true;
            }
            return false;
        });
        mSettings = new SettingsView(getContext());
        mSettings.addCallback(needUpdate -> {
            if (needUpdate) {
                createNewGame(true);
            }
            int title = Settings.stats ? R.string.action_stats : R.string.action_pause;
            mTopPanel.setButtonCaption(BTN_FOUR, mResources.getString(title));
            updateViews();
        });
        mLeaderboard = new LeaderboardView(dbHelper, mResources);
        mLeaderboard.addCallback(new LeaderboardView.Callbacks() {
            @Override
            public void onChanged() {
                updateViews();
            }

            @Override
            public void onExportClicked() {
                exportCallback.exportRecords();
            }

            @Override
            public void onImportClicked() {
                exportCallback.importRecords();
            }
        });
        mSolvedOverlay = new FieldTextOverlay(mRectField, mResources.getString(R.string.info_win));
        mPauseOverlay = new FieldTextOverlay(mRectField, mResources.getString(R.string.info_pause));
        mStatistics = new StatisticsView(statisticsManager, mResources);
        mStatistics.addCallbacks(exportCallback::exportSession);

        updateViews();

        createNewGame(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long current = System.currentTimeMillis();
        if (lastOnDrawTimestamp == 0) {
            lastOnDrawTimestamp = current - 1;
        }
        draw(canvas, current - lastOnDrawTimestamp);
        lastOnDrawTimestamp = current;
        if (Settings.postInvalidateDelay > 0) {
            postInvalidateDelayed(Settings.postInvalidateDelay);
        } else {
            postInvalidateOnAnimation();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        GameState state = GameState.get();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                float x = mGestureStartX = event.getX();
                float y = mGestureStartY = event.getY();
                boolean fieldFullyVisible = isFieldFullyVisible();
                mGestureTrail = fieldFullyVisible && mRectField.contains(x, y) && mField.emptySpaceAt(x, y);
                mSecondaryPointer = false;
                if (fieldFullyVisible && Settings.hardmode && !state.isNotStarted() && !state.paused) {
                    state.peeking = mHardModeView.isPeekAt(x, y);
                }
                return true;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                if (state.paused || state.isSolved() || !isFieldFullyVisible()) {
                    return true;
                }
                if (!mSecondaryPointer) {
                    mSecondaryPointer = true;
                    moveTilesWithLastMoveCheck(event.getX(), event.getY());
                }
                mGestureTrail = false;
                int index = event.getActionIndex();
                moveTilesWithLastMoveCheck(event.getX(index), event.getY(index));
                return true;
            }

            case MotionEvent.ACTION_MOVE: {
                if (state.paused || state.isSolved() || !isFieldFullyVisible() || mSecondaryPointer) {
                    return true;
                }
                float x = event.getX();
                float y = event.getY();
                if (mGestureTrail) {
                    mField.moveTiles(x, y, Game.DIRECTION_DEFAULT, false);
                    return true;
                }
                float dx = x - mGestureStartX;
                float dy = y - mGestureStartY;
                float minSwipeDistance = Dimensions.tileSize / SWIPE_FACTOR;
                if (Math.abs(dx) > minSwipeDistance || Math.abs(dy) > minSwipeDistance) {
                    mField.moveTiles(mGestureStartX, mGestureStartY, Game.direction(dx, dy));
                    event.setAction(MotionEvent.ACTION_CANCEL);
                }
                return true;
            }

            case MotionEvent.ACTION_UP: {
                if (mGestureTrail || mSecondaryPointer) {
                    return true;
                }

                float x = event.getX();
                float y = event.getY();
                if (mHelpOverlay != null && mHelpOverlay.onClick(x, y) || hideHelpOverlay()) {
                    return true;
                }

                if (state.peeking) {
                    state.peeking = false;
                }

                float dx = x - mGestureStartX;
                float dy = y - mGestureStartY;

                if (mLeaderboard.isShown()) {
                    mLeaderboard.onClick(mGestureStartX, mGestureStartY, dx);
                    return true;
                } else if (mSettings.isShown()) {
                    mSettings.onClick(mGestureStartX, mGestureStartY, dx);
                    return true;
                } else if (mStatistics.isShown()) {
                    mStatistics.onClick(mGestureStartX, mGestureStartY);
                    return true;
                } else if (state.isSolved() && mRectField.contains(x, y)) {
                    createNewGame(true);
                    return true;
                } else if (mNewAppBanner.isShown()) {
                    mNewAppBanner.onClick(x, y);
                    return true;
                } else if (mTopPanel.onClick(x, y)) {
                    return true;
                } else if (Settings.hardmode && mHardModeView.onClick(x, y)) {
                    return true;
                }

                if (mInfoPanel.onClick(x, y)) {
                    return true;
                }

                if (Math.sqrt(dx * dx + dy * dy) > (Dimensions.tileSize / SWIPE_FACTOR) && !state.paused) {
                    mField.moveTiles(mGestureStartX, mGestureStartY, Game.direction(dx, dy));
                } else if (state.paused && mRectField.contains(x, y)) {
                    state.paused = false;
                    mPauseOverlay.hide();
                    mField.update();
                } else if (!state.isSolved()) {
                    moveTilesWithLastMoveCheck(mGestureStartX, mGestureStartY);
                }
                return true;
            }

            default:
                return true;
        }
    }

    private void moveTilesWithLastMoveCheck(float x, float y) {
        if (!mField.moveTiles(x, y, Game.DIRECTION_DEFAULT)) {
            lastImpossibleMoveIndex = mField.at(x, y);
            lastImpossibleMoveTimestamp = System.currentTimeMillis();
        } else {
            // when solving puzzle fast, users can accidentally click on correct numbers
            // but in wrong order in short period of time
            // this is the hack we use to improve user experience:
            // if users clicks B then A, but B is possible only after A move - we move A first and then try to move B
            if (lastImpossibleMoveIndex != -1 &&
                    System.currentTimeMillis() - lastImpossibleMoveTimestamp < LAST_IMPOSSIBLE_MOVE_WINDOW_MILLIS) {
                mField.moveTiles(lastImpossibleMoveIndex, Game.DIRECTION_DEFAULT);
                lastImpossibleMoveIndex = -1;
            }
        }
    }

    @Override
    public void onTopPanelButtonClick(int id) {
        switch (id) {
            case BTN_NEW:
                createNewGame(true);
                break;

            case BTN_SETTINGS:
                pauseGame();
                mSettings.show();
                break;

            case BTN_LEADERBOARD:
                pauseGame();
                mLeaderboard.show();
                break;

            case BTN_FOUR:
                if (Settings.stats) {
                    pauseGame();
                    mStatistics.show();
                } else {
                    GameState state = GameState.get();
                    if (!state.isSolved()) {
                        state.invertPaused();
                    }
                    mField.update();
                    if (mPauseOverlay.isShown()) {
                        mPauseOverlay.hide();
                    } else {
                        mPauseOverlay.show();
                    }
                }
                break;
        }
    }

    public boolean onBackPressed() {
        if (hideHelpOverlay()) {
            return true;
        }
        return mSettings.hide() || mLeaderboard.hide() || mStatistics.hide();
    }

    public void onPause() {
        GameState state = GameState.get();
        if (state == null) {
            return;
        }
        if (state.peeking) {
            state.peeking = false;
        }
        if (mField != null) {
            pauseGame();
        }
    }

    private void draw(Canvas canvas, long elapsed) {
        GameState state = GameState.get();
        boolean paused = state.paused;
        boolean solved = state.isSolved();

        if (!paused && !solved && state.getMoves() > 0) {
            state.time += elapsed;
        }

        canvas.drawColor(Colors.getBackgroundColor());

        mTopPanel.draw(canvas, elapsed);
        if (mNewAppBanner.isShown()) {
            mNewAppBanner.draw(canvas, elapsed);
        }
        mInfoPanel.draw(canvas, elapsed);
        boolean helpShown = mHelpOverlay != null && mHelpOverlay.isShown();
        if (!helpShown) {
            mField.draw(canvas, elapsed);
        }

        if (BuildConfig.DEBUG) {
            Paint p = new Paint();
            p.setColor(Colors.getOverlayTextColor());
            float textFontSize = Dimensions.interfaceFontSize * .7f;
            p.setTextSize(textFontSize);
            float x = Dimensions.fieldMarginLeft;
            float y = Dimensions.fieldMarginTop + Dimensions.fieldHeight + Dimensions.spacing + textFontSize;
            canvas.drawText("inversions=" + state.game.inversions(), x, y, p);
        }

        if (Settings.hardmode) {
            mHardModeView.draw(canvas, elapsed);
        }

        if (solved && mSolvedOverlay.isShown() && !mSettings.isShown()) {
            mSolvedOverlay.draw(canvas, elapsed);
        }

        if (mLeaderboard.isShown()) {
            mLeaderboard.draw(canvas, elapsed);
        } else if (mSettings.isShown()) {
            mSettings.draw(canvas, elapsed);
        } else if (mStatistics.isShown()) {
            mStatistics.draw(canvas, elapsed);
        } else if (mHelpOverlay != null && mHelpOverlay.isShown()) {
            mHelpOverlay.draw(canvas, elapsed);
        } else if (!helpShown && paused && !solved && mPauseOverlay.isShown()) {
            mPauseOverlay.draw(canvas, elapsed);
        }
    }

    public void updateViews() {
        if (mField == null) {
            // not initialized yet
            return;
        }
        mTopPanel.update();
        mInfoPanel.update();
        mField.update();
        mLeaderboard.update();
        mSettings.update();
        mPauseOverlay.update();
        mSolvedOverlay.update();
        mStatistics.update();
        mHardModeView.update();
    }

    private void createNewGame(boolean isUser) {
        if (Settings.newGameDelay && ((System.currentTimeMillis() - lastSolvedTimestamp) < Constants.NEW_GAME_DELAY)) {
            return;
        }

        mPauseOverlay.hide();
        mSolvedOverlay.hide();

        Game newGame = null;
        long savedTime = 0;

        if (SaveGameManager.hasSavedGame()) {
            SaveGameManager.SavedGame savedGame = SaveGameManager.getSavedGame();
            if (isUser || (savedGame.state.size() != (Settings.gameWidth * Settings.gameHeight))) {
                SaveGameManager.removeSavedGame();
            } else {
                newGame = GameFactory.create(
                        Settings.gameType,
                        Settings.gameWidth,
                        Settings.gameHeight,
                        savedGame.state,
                        savedGame.moves
                );
                savedTime = savedGame.time;
            }
        }
        if (newGame == null) {
            newGame = GameFactory.create(
                    Settings.gameType,
                    Settings.gameWidth,
                    Settings.gameHeight,
                    Settings.randomMissingTile
            );
        }

        GameState newState = GameState.set(newGame, Settings.hardmode);
        newState.time = savedTime;
        if (newState.paused) {
            mPauseOverlay.show();
        }

        newGame.setCallback(() -> {
            GameState state = GameState.get();
            if (!state.hardmode) {
                // on hardmode solution must be checked manually
                onGameSolve(state);
            }
        });

        Dimensions.update(this.getMeasuredWidth(), this.getMeasuredHeight());
        mRectField.set(
                Dimensions.fieldMarginLeft - Dimensions.spacing,
                Dimensions.fieldMarginTop - Dimensions.spacing,
                Dimensions.fieldMarginLeft + Dimensions.fieldWidth + Dimensions.spacing,
                Dimensions.fieldMarginTop + Dimensions.fieldHeight + Dimensions.spacing);

        mField.clear();

        boolean animate = Settings.animationsEnabled() && !mSettings.isShown() && !mLeaderboard.isShown() &&
                !mStatistics.isShown() && !mPauseOverlay.isShown();
        for (int index = 0, size = newGame.getSize(); index < size; index++) {
            int number = newGame.getState().get(index);
            if (number > 0) {
                Tile t = new Tile(number, index);
                if (animate) {
                    tileAppearAnimator.animateTile(t);
                }
                mField.addTile(t);
            }
        }
        if (animate) {
            tileAppearAnimator.nextAnim();
        }
    }

    private void onGameSolve(GameState state) {
        mSolvedOverlay.show();
        int moves = state.getMoves();
        long time = state.time;
        dbHelper.insert(
                Settings.gameType,
                Settings.gameWidth,
                Settings.gameHeight,
                Settings.hardmode ? 1 : 0,
                moves,
                time);
        statisticsManager.add(
                Settings.gameWidth,
                Settings.gameHeight,
                Settings.gameType,
                Settings.hardmode,
                time,
                moves);
        lastSolvedTimestamp = System.currentTimeMillis();
        SaveGameManager.removeSavedGame();
    }

    private void pauseGame() {
        GameState state = GameState.get();
        state.paused = !state.isSolved() && state.getMoves() > 0;
        if (state.paused) {
            mPauseOverlay.show();
        }
        mField.update();
    }

    private boolean isFieldFullyVisible() {
        boolean overlayVisible = mLeaderboard.isShown()
                || mSettings.isShown()
                || mSolvedOverlay.isShown()
                || mPauseOverlay.isShown()
                || mStatistics.isShown()
                || mHelpOverlay != null && mHelpOverlay.isShown();
        return !overlayVisible;
    }

    private boolean hideHelpOverlay() {
        if (mHelpOverlay != null) {
            GameState.get().help = false;
            mHelpOverlay.hide();
            mHelpOverlay = null;
            return true;
        }
        return false;
    }
}
