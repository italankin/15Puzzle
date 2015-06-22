package com.onebig.puzzle;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

public class Tile {

    private static Paint sPaintText;                    // Paint для рисования текста
    private static Paint sPaintPath;                    // Paint для рисования фона плитка
    private static Rect mRectBounds = new Rect();       // определение границ текста

    private GameView mRootView;

    private Path mShape;                                // путь для рисования фона плитки
    private RectF mRectShape;                           // определение границ плитки и создания пути

    private int mData;                                  // отображаемое число на плитке
    private int mIndex;                                 // индекс плитки в общем массиве
    private float mCanvasX = 0.0f;                      // позиция плитки
    private float mCanvasY = 0.0f;                      // на поле (canvas)

    private Animation mAnimation = new Animation();

    public Tile(GameView root, int d, int i) {
        this.mRootView = root;
        this.mData = d;
        this.mIndex = i;

        if (sPaintText == null) {
            sPaintText = new Paint();
            sPaintText.setTypeface(Settings.typeface);
            sPaintText.setTextAlign(Paint.Align.CENTER);
            sPaintText.setAntiAlias(Settings.antiAlias);
        }

        if (sPaintPath == null) {
            sPaintPath = new Paint();
            sPaintPath.setAntiAlias(Settings.antiAlias);
        }

        mRectShape = new RectF();
        mShape = new Path();

        mCanvasX = Dimensions.fieldMarginLeft + (Dimensions.tileWidth + Dimensions.spacing) * (mIndex % Settings.gameWidth);
        mCanvasY = Dimensions.fieldMarginTop + (Dimensions.tileHeight + Dimensions.spacing) * (mIndex / Settings.gameWidth);

        mShape.addRoundRect(
                new RectF(
                        mCanvasX,
                        mCanvasY,
                        mCanvasX + Dimensions.tileWidth,
                        mCanvasY + Dimensions.tileHeight
                ),
                Dimensions.tileCornerRadius,
                Dimensions.tileCornerRadius,
                Path.Direction.CW
        );
    }

    public void draw(Canvas canvas) {

        // задержка анимации (в кадрах)
        if (mAnimation.delay > 0) {
            mAnimation.delay--;
            return;
        }

        mCanvasX = Dimensions.fieldMarginLeft + (Dimensions.tileWidth + Dimensions.spacing) * (mIndex % Settings.gameWidth);
        mCanvasY = Dimensions.fieldMarginTop + (Dimensions.tileHeight + Dimensions.spacing) * (mIndex / Settings.gameWidth);

        if (mAnimation.isPlaying()) {
            mShape = mAnimation.getTransformPath(mShape);
        }

        sPaintPath.setColor(Colors.getTileColor());
        canvas.drawPath(mShape, sPaintPath);

        if (!mRootView.paused) {
            mRectShape.inset(-Dimensions.spacing / 2.0f, -Dimensions.spacing / 2.0f);
            String text = Integer.toString(mData);
            mShape.computeBounds(mRectShape, true);
            sPaintText.setTextSize(mAnimation.getScale() * Dimensions.tileFontSize);
            sPaintText.getTextBounds(text, 0, text.length(), mRectBounds);
            sPaintText.setColor(Colors.getTileTextColor());
            if (!Settings.blindfolded || Game.getMoves() == 0 || mRootView.solved) {
                canvas.drawText(Integer.toString(mData), mRectShape.centerX(), mRectShape.centerY() - mRectBounds.centerY(), sPaintText);
            }
        }
    }

    /**
     * @return индекс данного спрайта в общем массиве
     */
    public int getIndex() {
        return mIndex;
    }

    // для отслеживания событий onClick
    public boolean isCollision(float x2, float y2) {
        return mRectShape.contains(x2, y2);
    }

    public boolean onClick() {
        if (mAnimation.isPlaying()) {
            return false;
        }

        int x = mIndex % Settings.gameWidth;
        int y = mIndex / Settings.gameWidth;

        int newIndex = Game.move(x, y);

        if (mIndex != newIndex) {
            mIndex = newIndex;

            if (Settings.animationEnabled) {
                x = mIndex % Settings.gameWidth;
                y = mIndex / Settings.gameWidth;

                mAnimation.dx = Dimensions.fieldMarginLeft + (Dimensions.tileWidth + Dimensions.spacing) * x - mCanvasX;
                mAnimation.dy = Dimensions.fieldMarginTop + (Dimensions.tileHeight + Dimensions.spacing) * y - mCanvasY;
                mAnimation.type = Animation.TRANSLATE;
                mAnimation.frames = Settings.tileAnimFrames;
            } else {
                mCanvasX = Dimensions.fieldMarginLeft + (Dimensions.tileWidth + Dimensions.spacing) * (mIndex % Settings.gameWidth);
                mCanvasY = Dimensions.fieldMarginTop + (Dimensions.tileHeight + Dimensions.spacing) * (mIndex / Settings.gameWidth);

                mShape.reset();
                mShape.addRoundRect(
                        new RectF(
                                mCanvasX,
                                mCanvasY,
                                mCanvasX + Dimensions.tileWidth,
                                mCanvasY + Dimensions.tileHeight
                        ),
                        Dimensions.tileCornerRadius,
                        Dimensions.tileCornerRadius,
                        Path.Direction.CW
                );
            } // if

            return true;

        } // if index

        return false;
    }

    /**
     * Устанавливает анимацию для спрайта
     *
     * @param type  тип анимации
     * @param delay задержка отображения анимации
     */
    public Tile setAnimation(int type, int delay) {
        if (Settings.animationEnabled) {
            mAnimation.delay = delay;
            mAnimation.type = type;
            mAnimation.frames = Settings.tileAnimFrames;
        }
        return this;
    }

    //
    public class Animation {

        public static final int STATIC = 0;             // статическая (без анимации)
        public static final int SCALE = 1;              // увеличение
        public static final int TRANSLATE = 2;          // перемещение

        public int type;                                // тип анимации
        public int frames;                              // отсавшееся кол-во кадров
        public int delay;                               // задержка анимации (в кадрах)
        public float dx;                                // перемещение по x
        public float dy;                                // перемещение по y

        public Animation() {
            this.type = STATIC;
            this.frames = 0;
            this.delay = 0;
        }

        /**
         * Производит преобразование фигуры исходя из выбранного типа анимации
         */
        public Path getTransformPath(Path p) {
            float ds, ds2, tx, ty;
            Matrix m;

            switch (type) {
                case SCALE:
                    m = new Matrix();
                    ds = (float) Tools.easeOut(frames, 0.0f, 1.0f, Settings.tileAnimFrames);
                    tx = (1 - ds) * (mCanvasX + Dimensions.tileWidth / 2.0f);
                    ty = (1 - ds) * (mCanvasY + Dimensions.tileHeight / 2.0f);
                    m.postScale(ds, ds);
                    m.postTranslate(tx, ty);
                    p.reset();
                    p.addRoundRect(new RectF(mCanvasX, mCanvasY, mCanvasX + Dimensions.tileWidth, mCanvasY
                                    + Dimensions.tileHeight), Dimensions.tileCornerRadius, Dimensions.tileCornerRadius,
                            Path.Direction.CW);
                    p.transform(m);
                    break; // SCALE

                case TRANSLATE:
                    m = new Matrix();
                    ds = (float) Tools.easeOut(frames, 0, 1.0f, Settings.tileAnimFrames);
                    ds2 = (float) Tools.easeOut(Math.min(frames + 1, Settings.tileAnimFrames), 0.0f, 1.0f,
                            Settings.tileAnimFrames);
                    ds = ds - ds2;
                    tx = ds * dx;
                    ty = ds * dy;
                    m.postTranslate(tx, ty);
                    p.transform(m);
                    break; // TRANSLATE

            } // switch

            if (frames > 0) {
                frames--;
            } else {
                type = STATIC;
            }

            return p;
        }

        public float getScale() {
            float ds = 1.0f;
            if (isPlaying() && type == SCALE) {
                ds = (float) Tools.easeOut(frames, 0.0f, 1.0f, Settings.tileAnimFrames);
            }
            return ds;
        }

        public boolean isPlaying() {
            return frames > 0;
        }
    } // Animation

}