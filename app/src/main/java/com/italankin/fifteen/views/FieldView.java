package com.italankin.fifteen.views;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.italankin.fifteen.Colors;
import com.italankin.fifteen.Dimensions;
import com.italankin.fifteen.Game;
import com.italankin.fifteen.Settings;
import com.italankin.fifteen.Tile;
import com.italankin.fifteen.Tools;

import java.util.ArrayList;

public class FieldView extends BaseView {

    private Paint mPaintField;

    /**
     * Массив элементов {@link Tile}
     */
    private ArrayList<Tile> mData;

    private RectF mRectField;

    public FieldView(RectF rect) {
        mData = new ArrayList<>();
        mRectField = rect;
        mPaintField = new Paint();

        mPaintField.setAntiAlias(Settings.antiAlias);
        mPaintField.setColor(Colors.backgroundField);

        mShow = true;
    }

    /**
     * Добавление элемента в массив
     *
     * @param tile элемент
     */
    public boolean addTile(Tile tile) {
        return mData.add(tile);
    }

    /**
     * Перемещение элементов
     *
     * @param sx        координата x начальной ячейки в массиве
     * @param sy        координата y начальной ячейки в массиве
     * @param direction направление перемещения
     */
    public void moveTiles(float sx, float sy, int direction) {
        int startIndex = at(sx, sy);
        if (startIndex >= 0) {
            if (direction == Tools.DIRECTION_DEFAULT) {
                direction = Game.getDirection(startIndex);
            }
            // вычисляем индексы ячеек, которые нам нужно переместить
            ArrayList<Integer> numbersToMove = Game.getSlidingElements(direction, startIndex);
            // перемещаем выбранные ячейки, если таковые есть
            for (int i = 0, s = numbersToMove.size(); i < s; i++) {
                int num = numbersToMove.get(i);
                for (Tile t : mData) {
                    if (t.getIndex() == num) {
                        t.onClick();
                    }
                }
            }

            if (numbersToMove.size() > 0) {
                Game.incMoves();
            }
        }
    }

    /**
     * Определяет спрайт, находящийся по координатам
     *
     * @param x координата x на экране
     * @param y координата y на экране
     * @return индекс элемента в игровом массиве
     */
    private int at(float x, float y) {
        for (Tile t : mData) {
            if (t.at(x, y)) {
                return t.getIndex();
            }
        }
        return -1;
    }

    /**
     * Отрисовка спрайтов
     */
    @Override
    public void draw(Canvas canvas, long elapsedTime) {
        canvas.drawRoundRect(mRectField, Dimensions.tileCornerRadius,
                Dimensions.tileCornerRadius, mPaintField);
        ArrayList<Tile> tiles;
        synchronized (this) {
            tiles = new ArrayList<>(mData);
        }
        for (Tile tile : tiles) {
            tile.draw(canvas, elapsedTime);
        }
    }

    public void clear() {
        ArrayList<Tile> list;
        synchronized (this) {
            list = new ArrayList<>(mData);
            mData.clear();
        }
        for (Tile tile : list) {
            tile.recycle();
        }
    }

    @Override
    public void update() {
        Tile.updatePaint();
        mPaintField.setAntiAlias(Settings.antiAlias);
    }

}
