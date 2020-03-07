package com.italankin.fifteen;

import android.animation.TimeInterpolator;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.italankin.fifteen.anim.TileScaleAnimator;
import com.italankin.fifteen.anim.TileXAnimator;
import com.italankin.fifteen.anim.TileYAnimator;

public class Tile {

    /**
     * Paint для рисования текста
     */
    private static Paint sPaintText;
    /**
     * Paint для рисования фона плитки
     */
    private static Paint sPaintPath;
    /**
     * Rect для определения границ текста
     */
    private static final Rect mRectBounds = new Rect();

    /**
     * Path для рисования фона плитки
     */
    private final Path mShape;
    /**
     * RectF для определения границ плитки и создания пути
     */
    private final RectF mRectShape;

    /**
     * Индекс плитки в общем массиве
     */
    private volatile int mIndex;
    /**
     * раскрашивание по слоям
     */
    private int mMultiColorIndex;
    private final int mNumber;
    /**
     * Позиция X плитки на поле
     */
    private volatile float mCanvasX;
    /**
     * Позиция Y плитки на поле
     */
    private volatile float mCanvasY;

    private TileXAnimator mTileXAnimator;
    private TileYAnimator mTileYAnimator;
    private TileScaleAnimator mTileScaleAnimator;
    private float mTextScale = 1.0f;
    private Path mDrawPath = new Path();

    private Matrix mMatrix = new Matrix();
    private String mDataText;

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

    /**
     * @param number данные для отображения
     * @param index  индекс в общем массиве
     */
    public Tile(int number, int index) {
        mIndex = index;
        mNumber = number;
        mMultiColorIndex = getMultiColorIndex(number);
        tileColor = getTileColor();
        mDataText = Integer.toString(number);

        updatePaint();

        mRectShape = new RectF();
        mShape = new Path();

        mCanvasX = Dimensions.fieldMarginLeft +
                (Dimensions.tileSize + Dimensions.spacing) * (mIndex % Settings.gameWidth);
        mCanvasY = Dimensions.fieldMarginTop +
                (Dimensions.tileSize + Dimensions.spacing) * (mIndex / Settings.gameWidth);

        mShape.addRoundRect(
                new RectF(mCanvasX, mCanvasY,
                        mCanvasX + Dimensions.tileSize, mCanvasY + Dimensions.tileSize),
                Dimensions.tileCornerRadius, Dimensions.tileCornerRadius,
                Path.Direction.CW);

        mShape.computeBounds(mRectShape, false);
        mDrawPath.set(mShape);

        TimeInterpolator interpolator = new OvershootInterpolator(.8f);
        mTileXAnimator = new TileXAnimator(this);
        mTileXAnimator.setInterpolator(interpolator);
        mTileYAnimator = new TileYAnimator(this);
        mTileYAnimator.setInterpolator(interpolator);

        TimeInterpolator scaleInterpolator = new DecelerateInterpolator(1.5f);
        mTileScaleAnimator = new TileScaleAnimator(this);
        mTileScaleAnimator.setInterpolator(scaleInterpolator);
    } // constructor

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

        if (mDrawPath == null) {
            return;
        }

        sPaintPath.setColor(tileColor);

        canvas.drawPath(mDrawPath, sPaintPath);

        if (!Game.isPaused() && mTextScale > 0) {
            sPaintText.setTextSize(mTextScale * Dimensions.tileFontSize);
            sPaintText.getTextBounds(mDataText, 0, mDataText.length(), mRectBounds);
            if (!Settings.hardmode || Game.isNotStarted() || Game.isSolved()) {
                canvas.drawText(mDataText, mRectShape.centerX(),
                        mRectShape.centerY() - mRectBounds.centerY(), sPaintText);
            }
        }
    }

    /**
     * @return индекс данного спрайта в общем массиве
     */
    public int getIndex() {
        return mIndex;
    }

    /**
     * Функция для опредления принадлежности координат спрайту
     *
     * @param x координата x
     * @param y координата y
     * @return принадлежат ли координаты {@link #mRectShape}
     */
    public boolean at(float x, float y) {
        return mRectShape.contains(x, y);
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
        mMatrix.setScale(scale, scale, mRectShape.centerX(), mRectShape.centerY());
        mDrawPath.transform(mMatrix);
    }

    public void update() {
        useMultiColor = Settings.useMultiColor();
        mMultiColorIndex = getMultiColorIndex(mNumber);
        tileColor = getTileColor();
    }

    public boolean isAnimating() {
        if (!Settings.animations) {
            return false;
        }
        return mTileScaleAnimator.isRunning() || mTileYAnimator.isRunning() || mTileXAnimator.isRunning();
    }

    private void updatePath() {
        mShape.computeBounds(mRectShape, false);
        mDrawPath.set(mShape);
    }

    /**
     * Вызывается при нажатии на спрайт на экране
     *
     * @return <b>true</b>, если
     */
    public boolean onClick() {
        if (recycled) {
            return false;
        }

        // получение текущих координат спрайта на поле
        int x = mIndex % Settings.gameWidth;
        int y = mIndex / Settings.gameWidth;

        // новый индекс спрайта после перемещения
        int newIndex = Game.move(x, y);

        // если текущий индекс не равен новому индексу
        // (т.е. был сделан ход и ситуация на поле изменилась)
        if (mIndex != newIndex) {
            mIndex = newIndex;
            tileColor = getTileColor();

            float newX = Dimensions.fieldMarginLeft +
                    (Dimensions.tileSize + Dimensions.spacing) * (mIndex % Settings.gameWidth);
            float newY = Dimensions.fieldMarginTop +
                    (Dimensions.tileSize + Dimensions.spacing) * (mIndex / Settings.gameWidth);

            if (Settings.animations) {
                // задание значений анимации
                if (mTileXAnimator.isRunning()) {
                    mTileXAnimator.cancel();
                }
                if (mTileYAnimator.isRunning()) {
                    mTileYAnimator.cancel();
                }

                mTileXAnimator.setValues(mCanvasX, newX);
                mTileYAnimator.setValues(mCanvasY, newY);

                // задание длительности
                mTileXAnimator.setDuration(Settings.tileAnimDuration);
                mTileYAnimator.setDuration(Settings.tileAnimDuration);

                // старт анимации
                mTileXAnimator.start();
                mTileYAnimator.start();
            } else {
                // обновление позиции плитки
                setCanvasX(newX);
                setCanvasY(newY);
            } // if animations

            return true;
        } // if index

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
        mTileScaleAnimator.setDuration(Settings.tileAnimDuration);
        mTileScaleAnimator.setValues(0, 1);
        mTileScaleAnimator.setStartDelay(delay);
        mTileScaleAnimator.start();
    }

    private int getTileColor() {
        int tileColor = Colors.getTileColor();
        if (useMultiColor && !Game.isPaused()) {
            if (Settings.multiColor == Settings.MULTI_COLOR_SOLVED) {
                int targetIndex;
                if (Settings.gameMode == Game.MODE_CLASSIC) {
                    targetIndex = mNumber - 1;
                } else {
                    int n = mNumber - 1;
                    int gameWidth = Settings.gameWidth;
                    int row = n / gameWidth;
                    if (row % 2 == 0) {
                        targetIndex = n;
                    } else {
                        int column = gameWidth - n % gameWidth - 1;
                        targetIndex = row * gameWidth + column;
                    }
                }
                return mIndex == targetIndex ? tileColor : Colors.getUnsolvedTileColor();
            } else {
                int colorIndex = mMultiColorIndex;
                if (colorIndex >= 0 && colorIndex < Colors.multiColorTiles.length) {
                    return Colors.multiColorTiles[colorIndex];
                }
            }
        }
        return tileColor;
    }

    public void recycle() {
        recycled = true;
        mTileXAnimator.cancel();
        mTileYAnimator.cancel();
        mTileScaleAnimator.cancel();
    }

    private int getMultiColorIndex(int number) {
        switch (Settings.gameMode) {
            case Game.MODE_SNAKE:
                return getSnakeMultiColorIndex(number);
            case Game.MODE_CLASSIC:
                return getClassicMultiColorIndex(number);
            default:
                return -1;
        }
    }

    private int getSnakeMultiColorIndex(int number) {
        int index = number - 1;
        int gameWidth = Settings.gameWidth;
        int row = index / gameWidth;
        int column = ((row % 2) == 0) ? (index % gameWidth) : (gameWidth - (index % gameWidth) - 1);
        switch (Settings.multiColor) {
            case Settings.MULTI_COLOR_ROWS:
                return row;
            case Settings.MULTI_COLOR_COLUMNS:
                return column;
            case Settings.MULTI_COLOR_FRINGE:
                return Math.min(row, column);
            case Settings.MULTI_COLOR_OFF:
            case Settings.MULTI_COLOR_SOLVED:
            default:
                return -1;
        }
    }

    private int getClassicMultiColorIndex(int number) {
        int index = number - 1;
        int gameWidth = Settings.gameWidth;
        switch (Settings.multiColor) {
            case Settings.MULTI_COLOR_ROWS:
                return index / gameWidth;
            case Settings.MULTI_COLOR_COLUMNS:
                return index % gameWidth;
            case Settings.MULTI_COLOR_FRINGE:
                return Math.min(index % gameWidth, index / gameWidth);
            case Settings.MULTI_COLOR_OFF:
            case Settings.MULTI_COLOR_SOLVED:
            default:
                return -1;
        }
    }
}
