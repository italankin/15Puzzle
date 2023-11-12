package com.italankin.fifteen;

import android.animation.TimeInterpolator;
import android.graphics.*;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import com.italankin.fifteen.anim.TileScaleAnimator;
import com.italankin.fifteen.anim.TileXAnimator;
import com.italankin.fifteen.anim.TileYAnimator;
import com.italankin.fifteen.game.Game;

public class Tile {

    private static Paint sPaintText;
    private static Paint sPaintPath;
    private static final Rect mRectBounds = new Rect();

    private final Path mShape;
    private final RectF mRectTileShape;
    private final RectF mRectAreaShape = new RectF();

    private int mIndex;
    private int mMultiColorIndex;
    private final boolean mIsHelpTile;
    private final int mNumber;
    private final int mGoalIndex;

    private float mCanvasX;
    private float mCanvasY;

    private final TileXAnimator mTileXAnimator;
    private final TileYAnimator mTileYAnimator;
    private final TileScaleAnimator mTileScaleAnimator;
    private float mTextScale = 1.0f;
    private final Path mDrawPath = new Path();

    private final Matrix mMatrix = new Matrix();
    private final String mDataText;

    private boolean useMultiColor = Settings.useMultiColor();
    private int tileColor;
    private boolean recycled = false;

    public static void updatePaint() {
        if (sPaintText == null) {
            sPaintText = new Paint();
            sPaintText.setTypeface(Settings.typeface);
            sPaintText.setTextAlign(Paint.Align.CENTER);
            sPaintText.setTextSize(Dimensions.tileFontSize);
        }

        if (sPaintPath == null) {
            sPaintPath = new Paint();
        }

        sPaintPath.setAntiAlias(Settings.antiAlias);

        sPaintText.setColor(Colors.getTileTextColor());
        sPaintText.setAntiAlias(Settings.antiAlias);
    }

    public Tile(int number, int index) {
        this(number, index, false);
    }

    public Tile(int number, int index, boolean isHelpTile) {
        mIndex = index;
        mNumber = number;
        mGoalIndex = GameState.get().game.getGoal().indexOf(number);
        mMultiColorIndex = getMultiColorIndex();
        mIsHelpTile = isHelpTile;
        tileColor = getTileColor();
        mDataText = Integer.toString(number);

        updatePaint();

        mRectTileShape = new RectF();
        mShape = new Path();

        mCanvasX = Dimensions.fieldMarginLeft +
                (Dimensions.tileSize + Dimensions.spacing) * (mIndex % Settings.gameWidth);
        //noinspection IntegerDivisionInFloatingPointContext
        mCanvasY = Dimensions.fieldMarginTop +
                (Dimensions.tileSize + Dimensions.spacing) * (mIndex / Settings.gameWidth);

        mShape.addRoundRect(
                new RectF(mCanvasX, mCanvasY,
                        mCanvasX + Dimensions.tileSize, mCanvasY + Dimensions.tileSize),
                Dimensions.tileCornerRadius, Dimensions.tileCornerRadius,
                Path.Direction.CW);

        updatePath();

        TimeInterpolator interpolator = new OvershootInterpolator(.8f);
        mTileXAnimator = new TileXAnimator(this);
        mTileXAnimator.setInterpolator(interpolator);
        mTileYAnimator = new TileYAnimator(this);
        mTileYAnimator.setInterpolator(interpolator);

        TimeInterpolator scaleInterpolator = new DecelerateInterpolator(1.5f);
        mTileScaleAnimator = new TileScaleAnimator(this);
        mTileScaleAnimator.setInterpolator(scaleInterpolator);
    }

    public void draw(Canvas canvas, long elapsedTime) {
        if (recycled) {
            return;
        }

        if (mTileXAnimator.isRunning()) {
            mTileXAnimator.nextFrame(elapsedTime);
        }
        if (mTileYAnimator.isRunning()) {
            mTileYAnimator.nextFrame(elapsedTime);
        }
        if (mTileScaleAnimator.isRunning()) {
            mTileScaleAnimator.nextFrame(elapsedTime);
        }

        sPaintPath.setColor(tileColor);

        canvas.drawPath(mDrawPath, sPaintPath);

        GameState state = GameState.get();
        if ((!state.paused || state.help) && mTextScale > 0) {
            sPaintText.setTextSize(mTextScale * Dimensions.tileFontSize);
            sPaintText.getTextBounds(mDataText, 0, mDataText.length(), mRectBounds);
            if (state.isNotStarted() || state.isSolved() || !Settings.hardmode || state.peeking || state.help) {
                canvas.drawText(mDataText, mRectTileShape.centerX(),
                        mRectTileShape.centerY() - mRectBounds.centerY(), sPaintText);
            }
        }
    }

    public int getNumber() {
        return mNumber;
    }

    public int getIndex() {
        return mIndex;
    }

    public boolean contains(float x, float y) {
        return mRectAreaShape.contains(x, y);
    }

    public void setCanvasX(float newX) {
        if (recycled) {
            return;
        }

        float dx = newX - mCanvasX;
        mShape.offset(dx, 0);
        mCanvasX = newX;
        updatePath();
    }

    public void setCanvasY(float newY) {
        if (recycled) {
            return;
        }

        float dy = newY - mCanvasY;
        mShape.offset(0, dy);
        mCanvasY = newY;
        updatePath();
    }

    public void setScale(float scale) {
        if (recycled) {
            return;
        }

        mTextScale = scale;
        updatePath();
        mMatrix.reset();
        mMatrix.setScale(scale, scale, mRectTileShape.centerX(), mRectTileShape.centerY());
        mDrawPath.transform(mMatrix);
    }

    public void update() {
        useMultiColor = Settings.useMultiColor();
        mMultiColorIndex = getMultiColorIndex();
        tileColor = getTileColor();
    }

    public boolean isAnimating() {
        if (!Settings.animationsEnabled()) {
            return false;
        }
        return mTileScaleAnimator.isRunning() || mTileYAnimator.isRunning() || mTileXAnimator.isRunning();
    }

    private void updatePath() {
        mShape.computeBounds(mRectTileShape, false);
        mDrawPath.set(mShape);
        mRectAreaShape.set(mRectTileShape);
        mRectAreaShape.inset(-Dimensions.spacing / 2, -Dimensions.spacing / 2);
    }

    public boolean move() {
        if (recycled) {
            return false;
        }

        int x = mIndex % Settings.gameWidth;
        int y = mIndex / Settings.gameWidth;

        int newIndex = GameState.get().game.move(x, y);

        // if index has changed, we made a move
        if (mIndex != newIndex) {
            mIndex = newIndex;
            tileColor = getTileColor();

            float newX = Dimensions.fieldMarginLeft +
                    (Dimensions.tileSize + Dimensions.spacing) * (mIndex % Settings.gameWidth);
            float newY = Dimensions.fieldMarginTop +
                    (Dimensions.tileSize + Dimensions.spacing) * (mIndex / Settings.gameWidth);

            if (Settings.animationsEnabled()) {
                if (mTileXAnimator.isRunning()) {
                    mTileXAnimator.cancel();
                }
                if (mTileYAnimator.isRunning()) {
                    mTileYAnimator.cancel();
                }

                mTileXAnimator.setValues(mCanvasX, newX);
                mTileYAnimator.setValues(mCanvasY, newY);

                mTileXAnimator.setDuration(Settings.animationSpeed);
                mTileYAnimator.setDuration(Settings.animationSpeed);

                mTileXAnimator.start();
                mTileYAnimator.start();
            } else {
                setCanvasX(newX);
                setCanvasY(newY);
            }
            return true;
        }
        return false;
    }

    public void animateAppearance(int delay) {
        if (recycled) {
            return;
        }

        setScale(0);
        if (mTileScaleAnimator.isRunning()) {
            mTileScaleAnimator.cancel();
        }
        mTileScaleAnimator.setDuration(Settings.animationSpeed);
        mTileScaleAnimator.setValues(0, 1);
        mTileScaleAnimator.setStartDelay(delay);
        mTileScaleAnimator.start();
    }

    public void recycle() {
        recycled = true;
        mTileXAnimator.cancel();
        mTileYAnimator.cancel();
        mTileScaleAnimator.cancel();
    }

    private int getTileColor() {
        int tileColor = Colors.getTileColor();
        GameState state = GameState.get();
        if (useMultiColor && (mIsHelpTile || !state.paused)) {
            if (Settings.multiColor == Constants.MULTI_COLOR_SOLVED) {
                return mIndex == mGoalIndex ? tileColor : Colors.getUnsolvedTileColor();
            } else {
                int colorIndex = mMultiColorIndex;
                if (colorIndex >= 0 && colorIndex < Colors.multiColorTiles.length) {
                    if (Settings.multiColor == Constants.MULTI_COLOR_FRINGE3) {
                        if (colorIndex != mGoalIndex / state.game.getWidth()) {
                            return Colors.multiColorTilesFringe3[colorIndex];
                        }
                    }
                    return Colors.multiColorTiles[colorIndex];
                }
            }
        }
        return tileColor;
    }

    private int getMultiColorIndex() {
        int gameWidth = Settings.gameWidth;
        switch (Settings.multiColor) {
            case Constants.MULTI_COLOR_ROWS:
                return mGoalIndex / gameWidth;
            case Constants.MULTI_COLOR_COLUMNS:
                return mGoalIndex % gameWidth;
            case Constants.MULTI_COLOR_FRINGE:
                return Math.min(mGoalIndex % gameWidth, mGoalIndex / gameWidth);
            case Constants.MULTI_COLOR_FRINGE3:
                Game game = GameState.get().game;
                int x = mGoalIndex / game.getWidth();
                int y = mGoalIndex % game.getWidth();
                return Math.min(x, y);
            case Constants.MULTI_COLOR_OFF:
            case Constants.MULTI_COLOR_SOLVED:
            default:
                return -1;
        }
    }
}
