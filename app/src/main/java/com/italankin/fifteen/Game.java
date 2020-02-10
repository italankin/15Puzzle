package com.italankin.fifteen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game {

    public static final int MODE_CLASSIC = 0;
    public static final int MODE_SNAKE = 1;

    /**
     * Singleton
     */
    private static Game instance = new Game();
    /**
     * Ширина головоломки в ячейках
     */
    private int width;
    /**
     * Высота головоломки в ячейках
     */
    private int height;
    /**
     * Массив игрового поля
     */
    private List<Integer> grid = new ArrayList<>();

    /**
     * Позиция нулевой (пустой) ячейки на поле
     */
    private int zeroPos;
    /**
     * Счетчик ходов
     */
    private int moves;
    /**
     * Счетчик времени
     */
    private long time;
    private boolean solved = false;
    private boolean paused = false;

    /**
     * Обработчик событий
     */
    private Callback mCallbacks = null;

    /**
     * Конструктор
     *
     * @param w ширина головоломки
     * @param h высота головоломки
     */
    public static void create(int w, int h) {
        instance.init(w, h);
    }

    /**
     * Инициализация игры с помощью сохраненного массива
     *
     * @param savedGrid  сохраненный массив
     * @param savedMoves количество сделанных ходов
     * @param savedTime  прошедшее время
     */
    public static void load(List<Integer> savedGrid, int savedMoves, long savedTime) {
        instance.grid = savedGrid;
        instance.moves = savedMoves;
        instance.time = savedTime;
        instance.zeroPos = savedGrid.indexOf(0);
    }

    /**
     * Инициализация новой игры
     *
     * @param w ширина головоломки
     * @param h высота головоломки
     */
    public void init(int w, int h) {
        height = h;
        width = w;
        int size = height * width;

        grid.clear();

        // создаем игровое поле
        for (int i = 0; i < size; i++) {
            grid.add(i);
        }

        // перемешивание массива
        Collections.shuffle(grid, new Random());

        // позиция нулевой ячейки
        zeroPos = grid.indexOf(0);

        moves = 0;
        time = 0;
        solved = false;
        paused = false;

        // проверка на возможность решения данной раскладки
        if (!isSolvable()) {
            // если паззл не решается, достаточно лишь поменять местами
            // последнее и предпоследнее число
            Collections.swap(grid, grid.indexOf(size - 1), grid.indexOf(size - 2));
        }

        // редкий случай, когда после всех перемещений получается собранный паззл
        if (checkSolution() && size > 3) {
            // в этом случае мы создаем поле заного
            init(width, height);
        }
    }

    /**
     * Проверка решаемости головоломки
     *
     * @return <b>true</b>, если головоломка решаема, иначе <b>false</b>
     */
    private boolean isSolvable() {
        int summ = 0, size = height * width;
        int n, m, s;
        switch (Settings.gameMode) {
            case MODE_CLASSIC:
                // для каждого числа высчитываем количество чисел, которые
                // 1) меньше данного числа
                // 2) стоят после него (по строкам)
                for (int i = 0; i < size; i++) {
                    n = grid.get(i);
                    s = 0;
                    for (int j = i + 1; j < size; j++) {
                        m = grid.get(j);
                        if (n > m && m > 0) {
                            s++;
                        }
                    }
                    summ += s;
                }

                // для головоломок с четным количеством столбцов нужно прибавить номер
                // строки (считая снизу), в которой находится нулевая ячейка
                if (width % 2 == 0) {
                    int z = height - zeroPos / width;
                    if (z % 2 == 0) {
                        return summ % 2 == 1;
                    }
                }

                return summ % 2 == 0;
            // MODE_CLASSIC

            case MODE_SNAKE:
                // для "змейки" нужно проверить,
                // является ли номер текущей строки четным
                for (int i = 0; i < size; i++) {
                    // если строка имеет четный номер,
                    // элементы массива нужно перебирать в обратном порядке
                    if ((i / width) % 2 == 0) {
                        n = grid.get(i);
                    } else {
                        n = grid.get(width * (1 + i / width) - i % width - 1);
                    }

                    s = 0;
                    for (int j = i + 1; j < size; j++) {

                        if ((j / width) % 2 == 0) {
                            m = grid.get(j);
                        } else {
                            m = grid.get(width * (1 + j / width) - j % width - 1);
                        }

                        if (n > m && m > 0) {
                            s++;
                        }
                    }
                    summ += s;
                }

                return summ % 2 == 0;
            // MODE_SNAKE

        } // switch

        return false;
    } // isSolvable

    /**
     * Проверка решения
     *
     * @return <b>true</b>, если головоломка решена, иначе <b>false</b>
     */
    private boolean checkSolution() {
        int size = height * width;
        int v, i;

        switch (Settings.gameMode) {
            case MODE_CLASSIC:
                for (i = 0; i < size - 1; i++) {
                    if (grid.get(i) != (i + 1)) {
                        return false;
                    }
                }
                break; // MODE_CLASSIC

            case MODE_SNAKE:
                for (i = 0; i < size - 1; i++) {
                    // если номер текущей строки четный
                    if ((i / width) % 2 == 0) {
                        // порядок чисел соответствует обычному порядку
                        v = i + 1;
                        if (grid.get(i) != v) {
                            return false;
                        }
                    } else {
                        // иначе получаем числа в обратном порядке
                        v = (width * (1 + i / width) - i % width);
                        if (v == size) {
                            v = 0;
                        }
                        if (grid.get(i) != v) {
                            return false;
                        }
                    }
                }
                break; // MODE_SNAKE

        } // switch

        // головоломка решена
        return true;
    } // checkSolution

    /**
     * Перемещение ячейки (базовый ход)
     *
     * @param x координата x ячейки
     * @param y координа y ячейки
     * @return конечная позиция ячейки
     */
    public static int move(int x, int y) {
        // вычисление позиции ячейки в массиве
        int pos = y * instance.width + x;

        if (instance.grid.get(pos) == 0) {
            return pos;
        }
        // вычисление координат пустой ячейки
        int x0 = instance.zeroPos % instance.width;
        int y0 = instance.zeroPos / instance.width;

        // проверка дистанции
        if (Tools.manhattan(x0, y0, x, y) > 1) {
            return pos;
        }

        // меняются местами пустая и выбранная ячейка
        Collections.swap(instance.grid, pos, instance.zeroPos);
        int newPos = instance.zeroPos;
        instance.zeroPos = pos;

        // обработка событий
        if (instance.mCallbacks != null) {
            // решение головоломки
            if (instance.checkSolution()) {
                instance.solved = true;
                instance.mCallbacks.onGameSolve();
            }
        }

        return newPos;
    } // move

    /**
     * Определяет элементы, которые необходимо переместить в выбранном жестом направлении
     *
     * @param direction направление перемещения
     * @param index     индекс начального элемента
     * @return массив элементов, которые требуется переместить
     */
    public static ArrayList<Integer> getSlidingElements(int direction, int index) {
        // массив индексов ячеек, которые будут перемещены
        ArrayList<Integer> result = new ArrayList<>();

        // проверка принадлежности начальной точки перемещения игровому полю
        if (index < 0) {
            return result;
        }

        int x, y;

        // вычисление координат ячейки начальной точки жеста
        int x1 = index % instance.width;
        int y1 = index / instance.width;

        // вычисление позиции пустой ячейки
        int x0 = instance.zeroPos % instance.width;
        int y0 = instance.zeroPos / instance.width;

        switch (direction) {
            case Tools.DIRECTION_UP:
                // перемещение должно срабатывать только в том столбце,
                // в котором оно возможно
                if (x1 != x0) {
                    break;
                }
                for (y = y0 + 1; y < Math.min(instance.height, y1 + 1); y++) {
                    result.add(y * instance.width + x0);
                }
                break;

            case Tools.DIRECTION_RIGHT:
                // перемещение должно срабатывать только в той строке,
                // в которой оно возможно
                if (y1 != y0) {
                    break;
                }
                for (x = x0 - 1; x >= Math.max(0, x1); x--) {
                    result.add(y0 * instance.width + x);
                }
                break;

            case Tools.DIRECTION_DOWN:
                // перемещение должно срабатывать только в том столбце,
                // в котором оно возможно
                if (x1 != x0) {
                    break;
                }
                for (y = y0 - 1; y >= Math.max(0, y1); y--) {
                    result.add(y * instance.width + x0);
                }
                break;

            case Tools.DIRECTION_LEFT:
                // перемещение должно срабатывать только в той строке,
                // в которой оно возможно
                if (y1 != y0) {
                    break;
                }
                for (x = x0 + 1; x < Math.min(instance.width, x1 + 1); x++) {
                    result.add(y0 * instance.width + x);
                }
                break;

        }

        return result;
    } // getSlidingElements

    /**
     * Возвращает число в ячейке по его индексу в массиве
     *
     * @param index индекс элемента в массиве
     * @return элемент массива
     */
    public static int getAt(int index) {
        return instance.grid.get(index);
    }

    /**
     * Вычисляет направление вектора перемещения с концом в {@link #zeroPos}
     *
     * @param index начальный индекс
     * @return идентификатор направления
     * @see Tools
     */
    public static int getDirection(int index) {
        return Tools.direction(instance.zeroPos % instance.width - index % instance.width,
                instance.zeroPos / instance.width - index / instance.width);
    }

    public static void incMoves() {
        instance.moves++;
    }

    public static int getMoves() {
        return instance.moves;
    }

    public static void incTime(long time) {
        if (instance.moves > 0) {
            instance.time += time;
        }
    }

    public static long getTime() {
        return instance.time;
    }

    public static boolean isSolved() {
        return instance.solved;
    }

    public static boolean isNotStarted() {
        return instance.moves == 0;
    }

    public static void setPaused(boolean paused) {
        instance.paused = paused;
    }

    public static void invertPaused() {
        instance.paused = !instance.paused;
    }

    public static boolean isPaused() {
        return instance.paused;
    }

    /**
     * @return массив элементов поля
     */
    public static String getGridStr() {
        String gridAsStr = instance.grid.toString();
        return gridAsStr.substring(1, gridAsStr.length() - 1);
    }

    /**
     * @return размер поля (высота * ширина)
     */
    public static int getSize() {
        return instance.grid.size();
    }

    /**
     * Интерфейс для отслеживания событий в игре
     */
    public interface Callback {

        /**
         * Вызывается при решении головоломки
         */
        void onGameSolve();

    }

    /**
     * Привязывает обработчик событий к игре
     *
     * @param callback обработчик событий
     */
    public static void addCallback(Callback callback) {
        instance.mCallbacks = callback;
    }

}
