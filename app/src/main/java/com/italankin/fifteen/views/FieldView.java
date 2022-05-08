package com.italankin.fifteen.views;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.italankin.fifteen.Colors;
import com.italankin.fifteen.CurrentGame;
import com.italankin.fifteen.Settings;
import com.italankin.fifteen.Tile;
import com.italankin.fifteen.game.Game;

import java.util.ArrayList;
import java.util.List;

public class FieldView extends BaseView {

    private final Paint mPaintField;
    private final ArrayList<Tile> mData;
    private final RectF mRectField;

    public FieldView(RectF rect) {
        mData = new ArrayList<>();
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
            Game game = CurrentGame.get();
            List<Integer> numbersToMove = game.findMovingTiles(startIndex, direction);
            for (Integer num : numbersToMove) {
                for (Tile t : mData) {
                    if (t.getIndex() == num && (forced || !t.isAnimating())) {
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
