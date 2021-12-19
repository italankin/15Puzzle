package com.italankin.fifteen;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;

import com.italankin.fifteen.export.ExportCallback;
import com.italankin.fifteen.statistics.StatisticsManager;
import com.italankin.fifteen.views.FieldView;
import com.italankin.fifteen.views.HardModeView;
import com.italankin.fifteen.views.InfoPanelView;
import com.italankin.fifteen.views.LeaderboardView;
import com.italankin.fifteen.views.SettingsView;
import com.italankin.fifteen.views.StatisticsView;
import com.italankin.fifteen.views.TopPanelView;
import com.italankin.fifteen.views.help.HelpOverlay;
import com.italankin.fifteen.views.overlay.FieldTextOverlay;

public class GameSurface extends View implements TopPanelView.Callback {

    private static final int BTN_NEW = 0;
    private static final int BTN_SETTINGS = 1;
    private static final int BTN_LEADERBOARD = 2;
    private static final int BTN_FOUR = 3;

    private final DBHelper dbHelper;
    private final ExportCallback exportCallback;

    private final Resources mResources;

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

    private int mGestureStartX;
    private int mGestureStartY;
    private boolean mGestureTrail = false;
    private long lastSolvedTimestamp = 0;
    private long lastOnDrawTimestamp = 0;

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
            Game.setHelp(true);
            RectF window = new RectF(0, 0, Dimensions.surfaceWidth, Dimensions.surfaceHeight);
            mHelpOverlay = new HelpOverlay(getResources(), tileAppearAnimator, window, mRectField);
            mHelpOverlay.addCallback(() -> {
                Resources res = getResources();
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse(res.getString(R.string.help_how_to_play_url)));
                Intent chooser = Intent.createChooser(intent, res.getString(R.string.help_how_to_play));
                getContext().startActivity(chooser);
            });
            mHelpOverlay.show();
        });

        mField = new FieldView(mRectField);

        mHardModeView = new HardModeView(mResources);
        mHardModeView.setCallbacks(Game::checkSolvedHm);
        mSettings = new SettingsView(mResources);
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
        });
        mSolvedOverlay = new FieldTextOverlay(mRectField, mResources.getString(R.string.info_win));
        mPauseOverlay = new FieldTextOverlay(mRectField, mResources.getString(R.string.info_pause));
        mStatistics = new StatisticsView(statisticsManager, mResources);
        mStatistics.addCallbacks(exportCallback::exportSession);

        Game.setCallback(() -> {
            mSolvedOverlay.show();
            int moves = Game.getMoves();
            long time = Game.getTime();
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
        });

        updateViews();

        createNewGame(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long current = System.currentTimeMillis();
        draw(canvas, current - lastOnDrawTimestamp);
        lastOnDrawTimestamp = current;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mGestureStartX = x;
                mGestureStartY = y;
                boolean fieldFullyVisible = isFieldFullyVisible();
                mGestureTrail = fieldFullyVisible && mRectField.contains(x, y) && mField.emptySpaceAt(x, y);
                if (fieldFullyVisible && Settings.hardmode && !Game.isNotStarted() && !Game.isPaused()) {
                    Game.setPeeking(mHardModeView.isPeekAt(x, y));
                }
                return true;
            }

            case MotionEvent.ACTION_MOVE: {
                if (Game.isPaused() || Game.isSolved() || !isFieldFullyVisible()) {
                    return true;
                }
                if (mGestureTrail) {
                    mField.moveTiles(x, y, Tools.DIRECTION_DEFAULT, false);
                    return true;
                }
                int dx = x - mGestureStartX;
                int dy = y - mGestureStartY;
                float minSwipeDistance = Dimensions.tileSize / 6.0f;
                if (Math.abs(dx) > minSwipeDistance || Math.abs(dy) > minSwipeDistance) {
                    mField.moveTiles(mGestureStartX, mGestureStartY, Tools.direction(dx, dy));
                    event.setAction(MotionEvent.ACTION_CANCEL);
                }
                return true;
            }

            case MotionEvent.ACTION_UP: {
                if (mGestureTrail) {
                    return true;
                }

                if (mHelpOverlay != null && mHelpOverlay.onClick(x, y) || hideHelpOverlay()) {
                    return true;
                }

                if (Game.isPeeking()) {
                    Game.setPeeking(false);
                }

                int dx = x - mGestureStartX;
                int dy = y - mGestureStartY;

                if (mLeaderboard.isShown()) {
                    mLeaderboard.onClick(mGestureStartX, mGestureStartY, dx);
                    return true;
                } else if (mSettings.isShown()) {
                    mSettings.onClick(mGestureStartX, mGestureStartY, dx);
                    return true;
                } else if (mStatistics.isShown()) {
                    mStatistics.onClick(mGestureStartX, mGestureStartY);
                    return true;
                } else if (Game.isSolved() && mRectField.contains(x, y)) {
                    createNewGame(true);
                    return true;
                } else if (mTopPanel.onClick(x, y)) {
                    return true;
                } else if (Settings.hardmode && mHardModeView.onClick(x, y)) {
                    return true;
                }

                if (mInfoPanel.onClick(x, y)) {
                    return true;
                }

                if (Math.sqrt(dx * dx + dy * dy) > (Dimensions.tileSize / 4.0f) && !Game.isPaused()) {
                    mField.moveTiles(mGestureStartX, mGestureStartY, Tools.direction(dx, dy));
                } else if (Game.isPaused() && mRectField.contains(x, y)) {
                    Game.setPaused(false);
                    mPauseOverlay.hide();
                    mField.update();
                } else if (!Game.isSolved()) {
                    mField.moveTiles(mGestureStartX, mGestureStartY, Tools.DIRECTION_DEFAULT);
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
                    if (!Game.isSolved()) {
                        Game.invertPaused();
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
        if (Game.isPeeking()) {
            Game.setPeeking(false);
        }
        pauseGame();
    }

    private void draw(Canvas canvas, long elapsed) {
        boolean paused = Game.isPaused();
        boolean solved = Game.isSolved();

        if (!paused && !solved) {
            Game.incTime(elapsed);
        }

        canvas.drawColor(Colors.getBackgroundColor());

        mTopPanel.draw(canvas, elapsed);
        mInfoPanel.draw(canvas, elapsed);
        boolean helpShown = mHelpOverlay != null && mHelpOverlay.isShown();
        if (!helpShown) {
            mField.draw(canvas, elapsed);
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

        Game.create(Settings.gameWidth, Settings.gameHeight);
        Game.setPeeking(false);
        Game.setHelp(mHelpOverlay != null && mHelpOverlay.isShown());

        if (SaveGameManager.hasSavedGame()) {
            SaveGameManager.SavedGame savedGame = SaveGameManager.getSavedGame();
            if (isUser || !savedGame.isValid()) {
                SaveGameManager.removeSavedGame();
            } else {
                Game.load(savedGame.grid, savedGame.moves, savedGame.time);
                if (Game.isPaused()) {
                    mPauseOverlay.show();
                }
            }
        }

        Dimensions.update(this.getMeasuredWidth(), this.getMeasuredHeight());
        mRectField.set(
                Dimensions.fieldMarginLeft - Dimensions.spacing,
                Dimensions.fieldMarginTop - Dimensions.spacing,
                Dimensions.fieldMarginLeft + Dimensions.fieldWidth + Dimensions.spacing,
                Dimensions.fieldMarginTop + Dimensions.fieldHeight + Dimensions.spacing);

        mField.clear();

        boolean animate = Settings.animations && !mSettings.isShown() && !mLeaderboard.isShown() &&
                !mStatistics.isShown() && !mPauseOverlay.isShown();
        for (int index = 0, size = Game.getSize(); index < size; index++) {
            int number = Game.getAt(index);
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

    private void pauseGame() {
        Game.setPaused(!Game.isSolved() && Game.getMoves() > 0);
        if (Game.isPaused()) {
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
            Game.setHelp(false);
            mHelpOverlay.hide();
            mHelpOverlay = null;
            return true;
        }
        return false;
    }
}
