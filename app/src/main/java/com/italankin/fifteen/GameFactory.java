package com.italankin.fifteen;

import com.italankin.fifteen.game.ClassicGame;
import com.italankin.fifteen.game.Game;
import com.italankin.fifteen.game.SnakeGame;
import com.italankin.fifteen.game.SpiralGame;

import java.util.List;

public class GameFactory {

    public static Game create(int type,
            int width,
            int height,
            boolean hardmode,
            boolean randomMissingTile) {
        switch (type) {
            case Constants.TYPE_CLASSIC:
                return new ClassicGame(width, height, hardmode, randomMissingTile);
            case Constants.TYPE_SNAKE:
                return new SnakeGame(width, height, hardmode, randomMissingTile);
            case Constants.TYPE_SPIRAL:
                return new SpiralGame(width, height, hardmode, randomMissingTile);
            default:
                throw new IllegalStateException("Unknown type=" + type);
        }
    }

    public static Game create(int type,
            int width,
            int height,
            boolean hardmode,
            List<Integer> savedGrid,
            int savedMoves,
            long savedTime) {
        switch (type) {
            case Constants.TYPE_CLASSIC:
                return new ClassicGame(width, height, hardmode, savedGrid, savedMoves, savedTime);
            case Constants.TYPE_SNAKE:
                return new SnakeGame(width, height, hardmode, savedGrid, savedMoves, savedTime);
            case Constants.TYPE_SPIRAL:
                return new SpiralGame(width, height, hardmode, savedGrid, savedMoves, savedTime);
            default:
                throw new IllegalStateException("Unknown type=" + type);
        }
    }

    private GameFactory() {
    }
}
