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
import java.util.concurrent.CopyOnWriteArrayList;

public class FieldView extends BaseView {

    private Paint mPaintField;

    /**
     * Массив элементов {@link Tile}
     */
    private CopyOnWriteArrayList<Tile> mData;

    private RectF mRectField;

    public FieldView(RectF rect) {
        mData = new CopyOnWriteArrayList<>();
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
        moveTiles(sx, sy, direction, true);
    }

    /**
     * Перемещение элементов
     *
     * @param sx        координата x начальной ячейки в массиве
     * @param sy        координата y начальной ячейки в массиве
     * @param direction направление перемещения
     * @param forced    перемещать элементы вне зависимости от состояния анимации
     */
    public void moveTiles(float sx, float sy, int direction, boolean forced) {
        int startIndex = at(sx, sy);
        if (startIndex >= 0) {
            if (direction == Tools.DIRECTION_DEFAULT) {
                direction = Game.getDirection(startIndex);
            }
            // вычисляем индексы ячеек, которые нам нужно переместить
            ArrayList<Integer> numbersToMove = Game.getSlidingElements(direction, startIndex);
            if (numbersToMove.isEmpty()) {
                return;
            }
            // перемещаем выбранные ячейки, если таковые есть
            int moved = 0;
            for (int i = 0, s = numbersToMove.size(); i < s; i++) {
                int num = numbersToMove.get(i);
                for (Tile t : mData) {
                    if ((forced || !t.isAnimating()) && t.getIndex() == num) {
                        t.onClick();
                        moved++;
                    }
                }
            }
            if (moved > 0) {
                Game.incMoves();
            }
        }
    }

    public boolean emptySpaceAt(float x, float y) {
        return at(x, y) == -1;
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
