package com.onebig.puzzle;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;

public class Tile {

    private static Paint textPaint;                     // Paint для рисования текста
    private static Paint pathPaint;                     // Paint для рисования фона плитка

    private GameView root;                              // главная область

    private Path shape;                                 // путь для рисования фона плитки
    private RectF pathRect;                             // используется для определения границ плитки и создания пути
    private Rect measureRect;                           // используется для определения границ текста и т.д.

    private int data;                                   // отображаемое число на плитке
    private int index;                                  // индекс плитки в общем массиве
    private float canvasX = 0.0f;                       // позиция плитки
    private float canvasY = 0.0f;                       // на поле (canvas)

    private Animation animation = new Animation();      // анимация

    public Tile(GameView root, int d, int i) {
        this.root = root;
        this.data = d;
        this.index = i;

        if (textPaint == null) {
            textPaint = new Paint();
            textPaint.setTypeface(Settings.typeface);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setAntiAlias(Settings.antiAlias);
        }

        if (pathPaint == null) {
            pathPaint = new Paint();
            pathPaint.setAntiAlias(Settings.antiAlias);
        }

        pathRect = new RectF();
        measureRect = new Rect();
        shape = new Path();

        canvasX = Constraints.fieldMarginLeft + (Constraints.tileWidth + Constraints.spacing) * (index % Settings.gameWidth);
        canvasY = Constraints.fieldMarginTop + (Constraints.tileHeight + Constraints.spacing) * (index / Settings.gameWidth);

        shape.addRoundRect(
                new RectF(
                        canvasX,
                        canvasY,
                        canvasX + Constraints.tileWidth,
                        canvasY + Constraints.tileHeight
                ),
                Constraints.tileCornerRadius,
                Constraints.tileCornerRadius,
                Path.Direction.CW
        );
    }

    public void draw(Canvas canvas) {

        // задержка анимации (в кадрах)
        if (animation.delay > 0) {
            animation.delay--;
            return;
        }

        canvasX = Constraints.fieldMarginLeft + (Constraints.tileWidth + Constraints.spacing) * (index % Settings.gameWidth);
        canvasY = Constraints.fieldMarginTop + (Constraints.tileHeight + Constraints.spacing) * (index / Settings.gameWidth);

        if (animation.isPlaying()) {
            shape = animation.getTransformPath(shape);
        }

        pathPaint.setColor(Colors.getTileColor());
        canvas.drawPath(shape, pathPaint);

        if (!root.paused) {
            pathRect.inset(-Constraints.spacing / 2.0f, -Constraints.spacing / 2.0f);
            String text = Integer.toString(data);
            shape.computeBounds(pathRect, true);
            textPaint.setTextSize(animation.getScale() * Constraints.tileFontSize);
            textPaint.getTextBounds(text, 0, text.length(), measureRect);
            textPaint.setColor(Colors.getTileTextColor());
            canvas.drawText(Integer.toString(data), pathRect.centerX(), pathRect.centerY() - measureRect.centerY(), textPaint);
        }
    }

    // возвращает индекс данного спрайта в общем массиве
    public int getIndex() {
        return index;
    }

    // для отслеживания событий onClick
    public boolean isCollision(float x2, float y2) {
        return pathRect.contains(x2, y2);
    }

    public boolean onClick() {
        if (animation.isPlaying()) {
            return false;
        }

        int x = index % Settings.gameWidth;
        int y = index / Settings.gameWidth;

        int newIndex = Game.move(x, y);

        if (index != newIndex) {
            index = newIndex;

            if (Settings.animationEnabled) {
                x = index % Settings.gameWidth;
                y = index / Settings.gameWidth;

                animation.dx = Constraints.fieldMarginLeft + (Constraints.tileWidth + Constraints.spacing) * x - canvasX;
                animation.dy = Constraints.fieldMarginTop + (Constraints.tileHeight + Constraints.spacing) * y - canvasY;
                animation.type = Animation.TRANSLATE;
                animation.frames = Settings.tileAnimFrames;
            } else {
                canvasX = Constraints.fieldMarginLeft + (Constraints.tileWidth + Constraints.spacing) * (index % Settings.gameWidth);
                canvasY = Constraints.fieldMarginTop + (Constraints.tileHeight + Constraints.spacing) * (index / Settings.gameWidth);

                shape.reset();
                shape.addRoundRect(
                        new RectF(
                                canvasX,
                                canvasY,
                                canvasX + Constraints.tileWidth,
                                canvasY + Constraints.tileHeight
                        ),
                        Constraints.tileCornerRadius,
                        Constraints.tileCornerRadius,
                        Path.Direction.CW
                );
            } // if

            return true;

        } // if index

        return false;
    }

    public void setAnimation(int type, int delay) {
        if (Settings.animationEnabled) {
            animation.delay = delay;
            animation.type = type;
            animation.frames = Settings.tileAnimFrames;
        }
    }

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

        // производит преобразование фигуры исходя из выбранного типа анимации
        public Path getTransformPath(Path p) {
            float ds, ds2, tx, ty;
            Matrix m;

            switch (type) {
                case SCALE:
                    m = new Matrix();
                    ds = (float) Tools.easeOut(frames, 0.0f, 1.0f, Settings.tileAnimFrames);
                    tx = (1 - ds) * (canvasX + Constraints.tileWidth / 2.0f);
                    ty = (1 - ds) * (canvasY + Constraints.tileHeight / 2.0f);
                    m.postScale(ds, ds);
                    m.postTranslate(tx, ty);
                    p.reset();
                    p.addRoundRect(new RectF(canvasX, canvasY, canvasX + Constraints.tileWidth, canvasY
                                    + Constraints.tileHeight), Constraints.tileCornerRadius, Constraints.tileCornerRadius,
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

            }

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