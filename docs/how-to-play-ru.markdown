---
layout: page
title: Как играть
permalink: /ru/how-to-play/
---

1. [Базовые правила](#1-базовые-правила)
2. [Управление](#2-управление)
3. [Типы игры](#3-типы-игры)
4. [Режимы игры](#4-режимы-игры)
5. [Рекорды](#5-рекорды)
6. [Опции](#6-опции)
7. [Мульти-цвет](#7-мульти-цвет)
8. [Статистика](#8-статистика)

## 1. Базовые правила

Классическая версия игры Пятнашек состоит из 15 фишек (на каждой из которых нарисована цифра) и одной пустой клетки.
Фишки можно двигать горизонтально и вертикально, меняя местами с пустой клеткой.

Цель игры состоит в том, чтобы расположить все 15 фишек в правильном порядке, а пустую клетку поставить в правый нижний
угол.

Например, мы начинаем с такой позиции (вариант с 8 фишками):

<p align="center"><img src="/assets/start.jpg" alt="Начальная позиция" /></p>

Чтобы решить головоломку, нам нужно расположить фишки в следующем порядке:

<p align="center"><img src="/assets/end.jpg" alt="Конечная позиция" /></p>

Существует много путей решения головоломки, один из них - располагать фишки в порядке по строкам.

Для начала, мы переместим фишку с номером `1` на своё место, затем фишку `2`, затем `3` и так далее. В нашем примере
правильно расположить первый ряд можно так:

<p align="center"><img src="/assets/first-row.gif" alt="Первый ряд" /></p>

## 2. Управление

В игре фишки перемещать можно тремя способами: клики, жесты и наведение.

### 2.1. Клики

Просто кликните на фишку, чтобы переместить ее.

Также можно перемещать несколько фишек:

<p align="center"><img src="/assets/click-move.gif" alt="Перемещение кликами" /></p>

### 2.2. Жесты

Проведите пальцем на фишках в нужном направлении, чтобы переместить их:

<p align="center"><img src="/assets/gesture-move.gif" alt="Перемещение жестами" /></p>

### 2.3. Наведение

Наведение более продвинутый (и быстрый) способ решения головоломки. Нажмите и не отпускайте палец на пустой клетке на
поле, затем наведите палец на ту фишку, которую хотите переместить.

<p align="center"><img src="/assets/hover.gif" alt="Наведение" /></p>

## 3. Типы игры

Всего есть три типа игры: `классика`, `змейка` и `спираль`. Тип игры можно изменить в [Опциях](#6-опции).

Вы всегда можете посмотреть конечную позицию паззла, нажав на имя текущего режима игры на главном экране:

<p align="center"><img src="/assets/how-to-play.jpg" alt="Как играть" /></p>

### 3.1. Классика

Цифры располагаются слева направо, сверху вниз.

<p align="center"><img src="/assets/classic.jpg" alt="Классика" /></p>

### 3.2. Змейка

Начало такое же, как и у Классики: верхняя строчка заполняется цифрами слева направо. Вторая строчка идет справа налево,
третья снова начинается слева - и так далее.

<p align="center"><img src="/assets/snake.jpg" alt="Змейка" /></p>

### 3.3. Спираль

Спираль начинается в левом верхнем углу, затем двигается направо. Достигнув края, она заполяет цифры сверху вниз; внизу
идет справа налево и так далее - таким образом получается спираль, которая идет во внутрь поля.

<p align="center"><img src="/assets/spiral.jpg" alt="Спираль" /></p>

## 4. Режимы игры

У каждого типа игры есть два режима: `легкий` и `сложный`. Изменить режим игры можно
в [дополнительных настройках](#62-дополнительные)

### 4.1. Легкий

Режим по умолчанию, ничего особенного.

### 4.2. Сложный

В сложном режиме паззл должен быть собран "вслепую": игрок может видеть цифры на фишках только перед тем, как сделан
первый ход. После любого хода цифры перестают показываться на фишках.

Чтобы собрать паззл, игроку нужно расположить цифры в правильном порядке и нажать "проверить", чтобы решение было
зачтено.

<p align="center"><img src="/assets/hard-check.gif" alt="Сложный режим: проверить" /></p>

Если вы чувствуете, что этот режим слишком сложен для вас, вы можете воспользоваться функцией подглядывания. Для этого
нужно нажать и не отпуска кнопку "открыть" - цифры будут отображаться на поле, пока вы держите палец на кнопке.

<p align="center"><img src="/assets/hard-peek.gif" alt="Сложный режим: открыть" /></p>

## 5. Рекорды

Самые быстрые и эффективные решения попадают в Рекорды.

Верхняя панель рекордов интерактивна: есть фильтры `Тип`, `Режим`, `Ширина` и `Высота`, и два
режима  `Сортировки`: `меньше ходов` and `лучшее время`.

Таблица показывает место в таблице рекордов, количество ходов, затраченное на решение время и дату, в которую был
установлен рекорд

<p align="center"><img src="/assets/records.jpg" alt="Рекорды" /></p>

### 5.1. Экспорт

Рекорды могут быть экспортированы в [CSV](https://ru.wikipedia.org/wiki/CSV) файл (кнопка `экспорт`).

Файл будет содержать следующую информацию:

| тип                      | сложный режим                                           | ширина      | высота      | время                               | ходы             | дата         |
|--------------------------|---------------------------------------------------------|-------------|-------------|-------------------------------------|------------------|--------------|
| [тип игры](#3-типы-игры) | `0` для легкого режима, `1` для [сложного](#42-сложный) | ширина поля | высота поля | время в секундах (с миллисекундами) | количество ходов | дата рекорда |

Например:

```
тип;сложный режим;ширина;высота;время;ходы;дата
классика;0;4;4;40.619;127;5/1/21
классика;0;4;4;29.906;128;5/1/21
```

### 5.2. Импорт

Импорт из [экспортированного файла рекордов](#51-экспорт).

## 6. Опции

### 6.1. Базовые

| Опция    | Значения                        | Описание                                                                                                |
|----------|---------------------------------|---------------------------------------------------------------------------------------------------------|
| Тип      | `классика`, `змейка`, `спираль` | Смотрите [типы игры](#3-типы-игры)                                                                      |
| Ширина   | `3 - 10`                        | Ширина паззла                                                                                           |
| Высота   | `3 - 10`                        | Высота паззла                                                                                           |
| Анимация | `вкл`, `выкл`                   | Если включена, то будут проигрываться анимации при перемещении фишек и т.д.                             |
| Тема     | `день`, `ночь`, `система`       | Общая цветовая тема приложения. При выборе `система` будет использоваться текущая цветовая тема системы |
| Цвета    | разные цвета                    | Цвет фишек и интерфейсных элементов                                                                     |

### 6.2. Дополнительные

| Опция             | Значения                                                    | Описание                                                                                                       |
|-------------------|-------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------|
| Режим             | `легкий`, `сложный`                                         | Смотрите [режимы игры](#4-режимы-игры)                                                                         |
| Пустая клетка     | `по умолчанию`, `случайно`                                  | Смотрите [пустая клетка](#622-пустая-клетка)                                                                   |
| Сглаживание       | `вкл`, `выкл`                                               | Сглаженное рисование линий. Выключение этой опции может повысить производительность игры                       |
| Мульти-цвет       | `выкл`, `строки`, `столбцы`, `края`, `решенные`, `fringe-3` | Смотрите [мульти-цвет](#7-мульти-цвет)                                                                         |
| Скорость анимации | `выкл`, `быстрая`, `обычная`                                | Скорость анимации перемещения фишек                                                                            |
| Delay             | `вкл`, `выкл`                                               | После решения паззла есть небольшая задержка, прежде чем вы сможете начать новую игру, кликнув на игровое поле |
| Инфо              |                                                             | Смотрите [инфо](#621-инфо)                                                                                     |
| Статистика        | `вкл`, `выкл`                                               | Смотрите [статистика](#8-статистика)                                                                           |

### 6.2.1. Инфо

| Setting        | Values                                 | Description                                                                                                                                                                                                                    |
|----------------|----------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Формат времени | `0:12.3`, `0:12.345`, `12.345`, `0:12` | Формат, в котором будет отображаться время. Влияет на главный экран, [рекорды](#5-рекорды) и [статистику](#8-статистика)                                                                                                       |
| Ходы           | `вкл`,`после решения`, `выкл`          | Отображение текущего количества ходов на главном экране. `вкл` - всегда показывать, `после решения` - только после успешного решения паззла, `выкл` - никогда не показывать                                                    |
| Время          | `вкл`,`после решения`, `выкл`          | Отображение текущего времени на главном экране. `вкл` - всегда показывать, `после решения` - только после успешного решения паззла, `выкл` - никогда не показывать                                                             |
| TPS            | `вкл`,`после решения`, `выкл`          | Отображение текущего [TPS](https://www.speedsolving.com/wiki/index.php/Turns_per_second) на главном экране. `вкл` - всегда показывать, `после решения` - только после успешного решения паззла, `выкл` - никогда не показывать |

### 6.2.2. Пустая клетка

По умолчанию в качестве отсутствующей клетки будет последний номер (например, для 4x4 это 16, 9 для 3x3, 12 для 4x3 и
так далее). Если выбрано `случайно`, отсутствующий номер каждый раз будет выбираться случайно.

Например, на этом поле отсутстует номер 6:

<p align="center"><img src="/assets/missing-tile-start.jpg" alt="Пустая клетка начальная позиция" /></p>

Таким образом, чтобы решить паззл, нужно достичь такой позиции:

<p align="center"><img src="/assets/missing-tile-goal.jpg" alt="Пустая клетка финальная позиция" /></p>

## 7. Мульти-цвет

Мульти-цвет влияет на то, каким цветом будут отображаться фишки на поле.

| Режим      | Описание                                               | Пример                             |
|------------|--------------------------------------------------------|------------------------------------|
| `выкл`     | Фишки окрашиваются в [выбранный цвет](#61-базовые)     | ![off](/assets/mc-off.jpg)         |
| `строки`   | Строки окрашиваются в разные цвета                     | ![rows](/assets/mc-rows.jpg)       |
| `столбцы`  | Столбцы окрашиваются в разные цвета                    | ![columns](/assets/mc-columns.jpg) |
| `края`     | Фишки окрашиваются по слоям                            | ![fringe](/assets/mc-fringe.jpg)   |
| `fringe-3` | Как `края`, но колонки дополнительно выделяются цветом | ![fringe](/assets/mc-fringe-3.jpg) |
| `решенные` | Фишки, находящиеся на своих местах, выделяются цветом  | ![solved](/assets/mc-solved.jpg)   |

## 8. Статистика

Экран Статистики показывает различную информацию о текущей сессии, такую
как [среднее](https://www.speedsolving.com/wiki/index.php/Average) время, ходы
и [TPS](https://www.speedsolving.com/wiki/index.php/Turns_per_second).

<p align="center"><img src="/assets/stats.jpg" alt="Statistics" /></p>

Включить Статистику можно в [дополнительных настройках](#62-дополнительные).

### 8.1. Экспорт

Статистика текущей сессии может быть экспортирована в [CSV](https://ru.wikipedia.org/wiki/CSV) файл (кнопка `экспорт`).

Файл будет содержать следующую информацию:

| тип                      | сложный режим                                           | ширина      | высота      | время                               | ходы             | tps                                                                 |
|--------------------------|---------------------------------------------------------|-------------|-------------|-------------------------------------|------------------|---------------------------------------------------------------------|
| [тип игры](#3-типы-игры) | `0` для легкого режима, `1` для [сложного](#42-сложный) | ширина поля | высота поля | время в секундах (с миллисекундами) | количество ходов | [TPS](https://www.speedsolving.com/wiki/index.php/Turns_per_second) |

Например:

```
тип;сложный режим;ширина;высота;время;ходы;tps
classic;0;4;4;11.732;78;6.648
classic;0;4;4;16.315;134;8.213
```
