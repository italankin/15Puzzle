---
layout: page
title: How to play
permalink: /how-to-play/
---

1. [Basic rules](#1-basic-rules)
2. [Controls](#2-controls)
3. [Game types](#3-game-types)
4. [Game modes](#4-game-modes)
5. [Records](#5-records)
6. [Settings](#6-settings)
7. [Multi-color](#7-multi-color)
8. [Statistics](#8-statistics)

## 1. Basic rules

A classic 15 puzzle uses tiles with 15 numbers written on each and one empty slot. Tiles can be moved either vertically or horizontally, if they stand next to empty space.

The goal of the game is to rearrange numbers on field into order.

Suppose we have starting position like this (8 tiles variant):

<p align="center"><img src="/assets/start.jpg" alt="Starting position" /></p>

So, to solve the puzzle we must arrange them in order:

<p align="center"><img src="/assets/end.jpg" alt="Final position" /></p>

There are many ways to reach the final position, one of them is solve puzzle row by row.

To start, we place number `1` on its place, then number `2`, then `3` and so on. For example, first row can be done this way:

<p align="center"><img src="/assets/first-row.gif" alt="First row solve" /></p>

## 2. Controls

The game support 3 different types of controls: clicks, gestures and hover.

### 2.1. Clicks

Just click on a tile to make a move.

It is possible to move multiple tiles in a row (or column):

<p align="center"><img src="/assets/click-move.gif" alt="Move by clicks" /></p>

### 2.2. Gestures

Swipe on a tile(s) to move:

<p align="center"><img src="/assets/gesture-move.gif" alt="Move with gestures" /></p>

### 2.3. Hover

Hover is more advanced (and faster) way to solve the puzzle. Start by pressing and holding on the empty space, and then move finger over the tiles you want to move.

<p align="center"><img src="/assets/hover.gif" alt="Hover method" /></p>

## 3. Game types

There are 3 game types: `classic`, `snake` and `spiral`. The game type can be changed in [Settings](#6-settings).

You can always view the final position of the game by pressing ❓ on the main screen:

<p align="center"><img src="/assets/how-to-play.jpg" alt="How to play" /></p>

### 3.1. Classic

Numbers arranged from left to right, top to bottom.

<p align="center"><img src="/assets/classic.jpg" alt="Classic mode" /></p>

### 3.2. Snake

Starting at top left corner go right, then next row from right to left, the third row arranged from left to right and so on.

<p align="center"><img src="/assets/snake.jpg" alt="Snake mode" /></p>

### 3.3. Spiral

Start at top left corner, move right to the edge, then move from top to bottom, then from right to left, then from bottom to top and so on. 

<p align="center"><img src="/assets/spiral.jpg" alt="Spiral mode" /></p>

## 4. Game modes

There are 2 modes: `easy` and `hard`. It can be changed in [advanced settings](#62-advanced).

### 4.1. Easy

The default mode you start with.

### 4.2. Hard

In a hard mode the puzzle must be solved blindly - the player can see the numbers only the on first move. If any move is done, numbers disappear from field.

To solve the puzzle, player must arrange numbers in their order, and then hit `check` button to check if they solved puzzle correctly.

<p align="center"><img src="/assets/hard-check.gif" alt="Hard mode check" /></p>

If you find intimidating playing the hard mode, `peek` button comes to the rescue. Press and hold the button to show numbers on tiles.

<p align="center"><img src="/assets/hard-peek.gif" alt="Hard mode peek" /></p>

## 5. Records

The fastest and most efficient solves are put to the Records.

The top panel of `Records` screen is interactive. There are `Type`, `Mode`, `Width` and `Height` filters, and two `Sort` modes: `fewer moves` and `less time`.

The table shows place, moves count, time spent and date of the solve.

<p align="center"><img src="/assets/records.jpg" alt="Records" /></p>

## 6. Settings

### 6.1. Basic

|Setting|Values|Description|
|-|-|-|
|Type|`classic`, `snake`, `spiral`|See [game types](#3-game-types)|
|Width|`3 - 8`|Width of the puzzle|
|Height|`3 - 8`|Height of the puzzle|
|Animation|`on`, `off`|Play animations on tiles moves, etc.|
|Color theme|`day`, `night`, `system`|The overall color theme of the app. `system` will follow system dark mode settings|
|Color|various colors|The color of tiles and interface|

### 6.2. Advanced

|Setting|Values|Description|
|-|-|-|
|Mode|`easy`, `hard`|See [game modes](#4-game-modes)|
|Anti-alias|`on`, `off`|Smoother appearance of lines. Disabling can improve performance|
|Multi-color|`off`, `rows`, `columns`, `fringe`, `solved`|See [multi-color](#7-multi-color)|
|Delay|`on`, `off`|After solving a puzzle, there is a small delay before you can start a new game by clicking on a game field|
|Ingame&nbsp;info||See [ingame-info](#621-ingame-info)|
|Stats|`on`, `off`|See [statistics](#8-statistics)|

### 6.2.1. Ingame info

|Setting|Values|Description|
|-|-|-|
|Time&nbsp;format|`0:12.3`, `0:12.345`, `12.345`, `0:12`|Time format to display. Affects main screen, [records](#5-records) and [statistics](#8-statistics)|
|Moves|`on`,<br>`after solve`, `off`|Show number of moves on the main screen. `on` - always show, `after solve` - show only if puzzle is solved, `off` - never show|
|Time|`on`,<br>`after solve`, `off`|Show elapsed time on the main screen. `on` - always show, `after solve` - show only if puzzle is solved, `off` - never show|
|TPS|`on`,<br>`after solve`, `off`|Show current [TPS](https://www.speedsolving.com/wiki/index.php/Turns_per_second) on the main screen. `on` - always show, `after solve` - show only if puzzle is solved, `off` - never show|

## 7. Multi-color

Multi-color is a way the tiles on game field are colored.

|Mode|Description|Image|
|-|-|-|
|`off`|All tiles colored by [chosen color](#61-basic)|![off](/assets/mc-off.jpg)|
|`rows`|Each row has different color|![rows](/assets/mc-rows.jpg)|
|`columns`|Each column has different color|![columns](/assets/mc-columns.jpg)|
|`fringe`|Tiles colored by layers|![fringe](/assets/mc-fringe.jpg)|
|`solved`|Solved tiles have a brighter color|![solved](/assets/mc-solved.jpg)|

## 8. Statistics

Statistics screen shows various information about current session, such as [average](https://www.speedsolving.com/wiki/index.php/Average) times, moves and [TPS](https://www.speedsolving.com/wiki/index.php/Turns_per_second).

<p align="center"><img src="/assets/stats.jpg" alt="Statistics" /></p>

Statistics can be enabled in [advanced settings](#62-advanced).
