package com.italankin.fifteen.views;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.italankin.fifteen.Colors;
import com.italankin.fifteen.Game;
import com.italankin.fifteen.Settings;
import com.italankin.fifteen.Tile;
import com.italankin.fifteen.Tools;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FieldView extends BaseView {

    private final Paint mPaintField;

    /**
     * Массив элементов {@link Tile}
     */
    private final CopyOnWriteArrayList<Tile> mData;

    private final RectF mRectField;

    public FieldView(RectF rect) {
        mData = new CopyOnWriteArrayList<>();
        mRectField = rect;
        mPaintField = new Paint();

        mPaintField.setAntiAlias(Settings.antiAlias);
        mPaintField.setColor(Colors.backgroundField);

        mShow = true;
    }

    public void addTile(Tile tile) {
        mData.add(tile);
    }

    public void moveTiles(float startX, float startY, int direction) {
        moveTiles(startX, startY, direction, true);
    }

    public void moveTiles(float startX, float startY, int direction, boolean forced) {
        int startIndex = at(startX, startY);
        if (startIndex >= 0) {
            if (direction == Tools.DIRECTION_DEFAULT) {
                direction = Game.getDirection(startIndex);
            }
            List<Integer> numbersToMove = Game.findMovingTiles(direction, startIndex);
            if (numbersToMove.isEmpty()) {
                return;
            }
            for (int i = 0, s = numbersToMove.size(); i < s; i++) {
                int num = numbersToMove.get(i);
                for (Tile t : mData) {
                    if ((forced || !t.isAnimating()) && t.getIndex() == num) {
                        t.onClick();
                    }
                }
            }
        }
    }

    public boolean emptySpaceAt(float x, float y) {
        return at(x, y) == -1;
    }

    private int at(float x, float y) {
        for (Tile t : mData) {
            if (t.contains(x, y)) {
                return t.getIndex();
            }
        }
        return -1;
    }

    @Override
    public void draw(Canvas canvas, long elapsedTime) {
        canvas.drawRect(mRectField, mPaintField);
        for (Tile tile : mData) {
            tile.draw(canvas, elapsedTime);
        }
    }

    public void clear() {
        for (Tile tile : mData) {
            tile.recycle();
        }
        mData.clear();
    }

    @Override
    public void update() {
        Tile.updatePaint();
        mPaintField.setAntiAlias(Settings.antiAlias);
        for (Tile tile : mData) {
            tile.update();
        }
    }
}
