package com.italankin.fifteen;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

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
    private static Rect mRectBounds = new Rect();

    /**
     * Path для рисования фона плитки
     */
    private Path mShape;
    /**
     * RectF для определения границ плитки и создания пути
     */
    private RectF mRectShape;

    /**
     * Отображаемое число на плитке
     */
    private int mData;
    /**
     * Индекс плитки в общем массиве
     */
    private int mIndex;
    /**
     * Позиция X плитки на поле
     */
    private float mCanvasX = 0.0f;
    /**
     * Позиция Y плитки на поле
     */
    private float mCanvasY = 0.0f;

    /**
     * Объект для проведения анимаций
     */
    private Animation mAnimation = new Animation();

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

        sPaintPath.setColor(Colors.getTileColor());
        sPaintPath.setAntiAlias(Settings.antiAlias);

        sPaintText.setColor(Colors.getTileTextColor());
        sPaintText.setAntiAlias(Settings.antiAlias);
    }

    /**
     * @param number данные для отображения
     * @param index  индекс в общем массиве
     */
    public Tile(int number, int index) {
        this.mData = number;
        this.mIndex = index;

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
    } // constructor

    public void draw(Canvas canvas, long elapsedTime) {

        // задержка анимации
        if (mAnimation.delay > 0) {
            mAnimation.delay -= elapsedTime;
            return;
        }

        mCanvasX = Dimensions.fieldMarginLeft +
                (Dimensions.tileSize + Dimensions.spacing) * (mIndex % Settings.gameWidth);
        mCanvasY = Dimensions.fieldMarginTop +
                (Dimensions.tileSize + Dimensions.spacing) * (mIndex / Settings.gameWidth);

        if (mAnimation.isPlaying()) {
            mShape = mAnimation.getTransformPath(mShape, elapsedTime);
        }

        canvas.drawPath(mShape, sPaintPath);

        if (!Game.isPaused()) {
            mRectShape.inset(-Dimensions.spacing / 2.0f, -Dimensions.spacing / 2.0f);
            String text = Integer.toString(mData);
            mShape.computeBounds(mRectShape, true);
            sPaintText.setTextSize(mAnimation.getScale() * Dimensions.tileFontSize);
            sPaintText.getTextBounds(text, 0, text.length(), mRectBounds);
            if (!Settings.hardmode || Game.getMoves() == 0 || Game.isSolved()) {
                canvas.drawText(text, mRectShape.centerX(),
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

    /**
     * Вызывается при нажатии на спрайт на экране
     *
     * @return <b>true</b>, если
     */
    public boolean onClick() {
        // при проигрывании анимации взаимодействовать со спрайтом нельзя
        if (mAnimation.isPlaying()) {
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

            if (Settings.animations) {
                // получение новых координат
                x = mIndex % Settings.gameWidth;
                y = mIndex / Settings.gameWidth;

                // расчет величины перемещения по осям
                mAnimation.dx = Dimensions.fieldMarginLeft +
                        (Dimensions.tileSize + Dimensions.spacing) * x - mCanvasX;
                mAnimation.dy = Dimensions.fieldMarginTop +
                        (Dimensions.tileSize + Dimensions.spacing) * y - mCanvasY;

                mAnimation.type = Animation.TRANSLATE;
                mAnimation.frames = Settings.tileAnimFrames;
            } else {
                mCanvasX = Dimensions.fieldMarginLeft +
                        (Dimensions.tileSize + Dimensions.spacing) * (mIndex % Settings.gameWidth);
                mCanvasY = Dimensions.fieldMarginTop +
                        (Dimensions.tileSize + Dimensions.spacing) * (mIndex / Settings.gameWidth);

                mShape.reset();
                mShape.addRoundRect(
                        new RectF(mCanvasX, mCanvasY,
                                mCanvasX + Dimensions.tileSize,
                                mCanvasY + Dimensions.tileSize),
                        Dimensions.tileCornerRadius, Dimensions.tileCornerRadius,
                        Path.Direction.CW);
            } // if animations

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
    public void setAnimation(int type, int delay) {
        if (Settings.animations) {
            mAnimation.delay = delay;
            mAnimation.type = type;
            mAnimation.frames = Settings.tileAnimFrames;
        }
    }

    /**
     * Внутренний класс для управления анимацией
     */
    public class Animation {

        public static final int STATIC = 0;
        public static final int SCALE = 1;
        public static final int TRANSLATE = 2;

        /**
         * Тип анимации ({@link #STATIC}, {@link #SCALE}, {@link #TRANSLATE})
         */
        public int type;
        /**
         * Оставшееся кол-во кадров анимации
         */
        public long frames;
        /**
         * Задержка анимации (в кадрах)
         */
        public int delay;
        /**
         * Перемещение по x
         */
        public float dx;
        /**
         * Перемещение по y
         */
        public float dy;

        private float lastTx = 0;
        private float lastTy = 0;

        public Animation() {
            this.type = STATIC;
            this.frames = 0;
            this.delay = 0;
        }

        /**
         * Производит преобразование фигуры исходя из выбранного типа анимации
         *
         * @return трансформированный {@link Path}
         */
        public Path getTransformPath(Path p, long time) {
            float ds, ds2, tx, ty;
            Matrix m;

            switch (type) {
                case SCALE:
                    m = new Matrix();
                    ds = (float) Tools.easeOut(frames, 0.0f, 1.0f, Settings.tileAnimFrames);
                    tx = (1 - ds) * (mCanvasX + Dimensions.tileSize / 2.0f);
                    ty = (1 - ds) * (mCanvasY + Dimensions.tileSize / 2.0f);
                    m.postScale(ds, ds);
                    m.postTranslate(tx, ty);
                    p.reset();
                    p.addRoundRect(
                            new RectF(mCanvasX, mCanvasY,
                                    mCanvasX + Dimensions.tileSize, mCanvasY + Dimensions.tileSize),
                            Dimensions.tileCornerRadius, Dimensions.tileCornerRadius,
                            Path.Direction.CW);
                    p.transform(m);
                    break; // SCALE

                case TRANSLATE:
                    m = new Matrix();
                    if (frames < time) {
                        frames = 0;
                    }
                    ds = (float) Tools.easeOut(frames, 0, 1.0f, Settings.tileAnimFrames);
                    tx = ds * dx - lastTx;
                    ty = ds * dy - lastTy;
                    lastTx = ds * dx;
                    lastTy = ds * dy;
                    m.postTranslate(tx, ty);
                    p.transform(m);
                    break; // TRANSLATE

            } // switch

            if (frames > 0) {
                frames -= time;
            } else {
                type = STATIC;
                lastTx = 0;
                lastTy = 0;
                p.reset();
                p.addRoundRect(
                        new RectF(mCanvasX, mCanvasY,
                                mCanvasX + Dimensions.tileSize, mCanvasY + Dimensions.tileSize),
                        Dimensions.tileCornerRadius, Dimensions.tileCornerRadius,
                        Path.Direction.CW);
            }

            return p;
        } // getTransformPath

        /**
         * Функция для определения текущего значения масштаба (только для {@link #SCALE})
         *
         * @return текущее значение масштаба
         */
        public float getScale() {
            if (isPlaying() && type == SCALE) {
                return (float) Tools.easeOut(frames, 0.0f, 1.0f, Settings.tileAnimFrames);
            } else {
                return 1.0f;
            }
        }

        /**
         * @return <b>true</b>, если оставшееся кол-во кадров > 0
         */
        public boolean isPlaying() {
            return frames > 0;
        }

    } // Animation

}