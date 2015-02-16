package com.onebig.puzzle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Game {

    public static final int MODE_CLASSIC = 0;
    public static final int MODE_SNAKE = 1;

    public static Game instance;                        // статический экземпляр класса
    private int width;                                  // ширина головоломки в ячейках
    private int height;                                 // высота головоломки в ячейках
    private ArrayList<Integer> grid = new ArrayList<Integer>(); // игровое поле
    private int zeroPos;                                // позиция пустой ячейки
    private int moves;                                  // счетчик количества ходов
    private long time;                                  // счетчик времени
    private boolean stateChanged = false;               // изменение состояния

    // создание класса с параметрами
    public static Game create(int w, int h) {
        if (instance == null) {
            instance = new Game();
        }
        instance.init(w, h);
        return instance;
    }

    // инициализация с помощью сохраненного массива
    public static void load(ArrayList<Integer> savedGrid, int savedMoves, long savedTime) {
        instance.grid = savedGrid;
        instance.moves = savedMoves;
        instance.time = savedTime;
        instance.zeroPos = savedGrid.indexOf(0);
    }

    // инициализация новой игры
    public void init(int w, int h) {
        height = h;                                     // сохраняем размер
        width = w;                                      // поля
        int size = height * width;                      // рассчет размер поля

        grid.clear();

        // создаем игровое поле
        for (int i = 0; i < size; i++) {
            grid.add(i);
        }

        Collections.shuffle(grid, new Random());        // перемешиваем поле

        zeroPos = grid.indexOf(0);                      // записываем позицию нулевой ячейки

        moves = 0;                                      // обнуление счетчика ходов
        time = 0;                                       // обнуление счетчика времени

        // проверка на возможность решения данной раскладки
        if (!isSolvable()) {
            // если паззл не решается, достаточно лишь поменять местами
            // последнее и предпоследнее число
            Collections.swap(grid, grid.indexOf(size - 1), grid.indexOf(size - 2));
        }
        // редкий случай, когда после всех перемещений получается собранный паззл
        if (isSolved() && size > 3) {
            // в этом случае мы создаем поле заного
            init(width, height);
        }
    }

    // проверка решаемости головоломки
    public static boolean isSolvable() {
        int summ = 0, size = instance.height * instance.width;
        int n, m, s;
        switch (Settings.gameMode) {
            case MODE_CLASSIC:
                for (int i = 0; i < size; i++) {
                    n = instance.grid.get(i);
                    s = 0;
                    for (int j = i + 1; j < size; j++) {
                        m = instance.grid.get(j);
                        // для каждого числа высчитываем количество чисел, которые
                        // 1) меньше данного числа
                        // 2) стоят после него (по строкам)
                        if (n > m && m > 0) {
                            s++;
                        }
                    }
                    summ += s;
                }

                // для головоломок с четным количеством столбцов нужно прибавить номер
                // строки (считая снизу), в которой находится нулевая ячейка
                if (instance.width % 2 == 0) {
                    int z = instance.height - instance.zeroPos / instance.width;
                    if (z % 2 == 0) {
                        return summ % 2 == 1;
                    }
                }

                return summ % 2 == 0;
            // MODE_CLASSIC

            case MODE_SNAKE:
                for (int i = 0; i < size; i++) {

                    // для "змейки" нужно проверить,
                    // является ли номер текущей строки четным
                    if ((i / instance.width) % 2 == 0) {
                        n = instance.grid.get(i);
                    } else {
                        // если строка имеет четный номер, элементы массива нужно перебирать в обратном порядке
                        n = instance.grid.get(instance.width * (1 + i / instance.width) - i % instance.width - 1);
                    }

                    s = 0;
                    for (int j = i + 1; j < size; j++) {

                        if ((j / instance.width) % 2 == 0) {
                            m = instance.grid.get(j);
                        } else {
                            m = instance.grid.get(instance.width * (1 + j / instance.width) - j % instance.width - 1);
                        }

                        if (n > m && m > 0) {
                            s++;
                        }
                    }
                    summ += s;
                }

//                if (instance.height % 2 == 1) {
//                    int z = instance.width - instance.zeroPos % instance.width;
//                    if (z % 2 == 0) {
//                        return summ % 2 == 1;
//                    }
//                }

                return summ % 2 == 0;
            // MODE_SNAKE

        }

        return false;
    }

    // проверка решения
    public static boolean isSolved() {
        if (instance == null) {
            return false;
        }

        int size = instance.height * instance.width;
        int v, i;

        switch (Settings.gameMode) {
            case MODE_CLASSIC:
                for (i = 0; i < size - 1; i++) {
                    if (instance.grid.get(i) != (i + 1)) {
                        return false;
                    }
                }
                break; // MODE_CLASSIC

            case MODE_SNAKE:
                for (i = 0; i < size - 1; i++) {
                    if ((i / instance.width) % 2 == 0) {
                        v = i + 1;
                        if (instance.grid.get(i) != v) {
                            return false;
                        }
                    } else {
                        v = (instance.width * (1 + i / instance.width) - i % instance.width);
                        if (v == size) {
                            v = 0;
                        }
                        if (instance.grid.get(i) != v) {
                            return false;
                        }
                    }
                }
                break; // MODE_SNAKE

        }

        return true;
    }

    // перемещение ячеек на поле
    public static int move(int x, int y) {
        int pos = y * instance.width + x;               // вычисление позиции ячейки в массиве

        if (instance.grid.get(pos) == 0) {
            return pos;
        }
        int x0 = instance.zeroPos % instance.width;     // вычисление координат
        int y0 = instance.zeroPos / instance.width;     // пустой ячейки

        if (manhattan(x0, y0, x, y) > 1) {        // проверка дистанции
            return pos;
        }

        // меняем местами пустую и выбранную ячейку
        Collections.swap(instance.grid, pos, instance.zeroPos);
        instance.stateChanged = true;
        int newPos = instance.zeroPos;
        instance.zeroPos = pos;

        return newPos;
    }

    // вычисление расстояния между клетками на игровом поле
    public static int manhattan(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    // функция перемещает выбранный столбец (строку)
    // относительно выбранного направления
    // возвращает массив индексов плиток, которые требуется переместить
    public static ArrayList<Integer> slide(int direction, int index) {
        // массив индексов ячеек, которые будут перемещены
        ArrayList<Integer> result = new ArrayList<Integer>();

        // проверка принадлежности начальной точки перемещения игровому полю
        if (index < 0) {
            return result;
        }

        int x, y;                                       // переменные для расчетов

        int x1 = index % instance.width;                // вычисление координат начальной
        int y1 = index / instance.width;                // ячейки (начальная точка "жеста")

        int x0 = instance.zeroPos % instance.width;     // вычисление позиции
        int y0 = instance.zeroPos / instance.width;     // пустой ячейки

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
    } // end slide

    // возвращает число по координатам (x;y)
    public static int getAt(int x, int y) {
        return instance.grid.get(y * instance.width + x);
    }

    // возвращает число с индексом index
    public static int getAt(int index) {
        return instance.grid.get(index);
    }

    // вычисляет направление вектора с началом index и концом zeroPos
    public static int getDirection(int index) {
        return Tools.direction(instance.zeroPos % instance.width - index % instance.width, instance.zeroPos / instance.width - index / instance.width);
    }

    // количество ходов
    public static int move(int m) {
        if (instance.stateChanged && m > 0) {
            instance.stateChanged = false;
            instance.moves++;
        }
        return instance.moves;
    }

    // затраченное время
    public static long time(long t) {
        if (instance.moves > 0 && t > 0) {
            instance.time += t;
        }
        return instance.time;
    }

    // поле как массив чисел
    public static ArrayList<Integer> getGrid() {
        return instance.grid;
    }

    // размер поля
    public static int getGridSize() {
        return instance.grid.size();
    }

}
