package com.italankin.fifteen;

import java.util.Random;

public class TileAppearAnimator {

    private static final int ANIM_WINDOW_FRAMES = 5;

    public static final int ANIM_TYPE_ALL = 0;
    public static final int ANIM_TYPE_INDEX_ASC = 1;
    public static final int ANIM_TYPE_INDEX_DESC = 2;
    public static final int ANIM_TYPE_RANDOM = 3;
    public static final int ANIM_TYPE_NUMBER_ASC = 4;
    public static final int ANIM_TYPE_NUMBER_DESC = 5;
    public static final int ANIM_TYPE_ROW = 6;
    public static final int ANIM_TYPE_COLUMN = 7;
    public static final int ANIM_TYPE_ROW_REVERSE = 8;
    public static final int ANIM_TYPE_COLUMN_REVERSE = 9;
    public static final int ANIM_TYPE_TOP_LEFT_CORNER = 10;
    public static final int ANIM_TYPE_TOP_RIGHT_CORNER = 11;
    public static final int ANIM_TYPE_BOTTOM_LEFT_CORNER = 12;
    public static final int ANIM_TYPE_BOTTOM_RIGHT_CORNER = 13;
    public static final int ANIM_TYPE_FROM_CENTER = 14;
    public static final int ANIM_TYPE_TO_CENTER = 15;
    public static final int ANIM_TYPE_NUMBER_ASC_NO_GROUP = 16;

    private static final int[] ANIM_TYPES = {
            ANIM_TYPE_ALL,
            ANIM_TYPE_INDEX_ASC,
            ANIM_TYPE_INDEX_DESC,
            ANIM_TYPE_RANDOM,
            ANIM_TYPE_NUMBER_ASC,
            ANIM_TYPE_NUMBER_DESC,
            ANIM_TYPE_ROW,
            ANIM_TYPE_COLUMN,
            ANIM_TYPE_ROW_REVERSE,
            ANIM_TYPE_COLUMN_REVERSE,
            ANIM_TYPE_TOP_LEFT_CORNER,
            ANIM_TYPE_TOP_RIGHT_CORNER,
            ANIM_TYPE_BOTTOM_LEFT_CORNER,
            ANIM_TYPE_BOTTOM_RIGHT_CORNER,
            ANIM_TYPE_FROM_CENTER,
            ANIM_TYPE_TO_CENTER
    };

    private final Random rnd = new Random();
    private int animCurrentIndex = 0;
    private int animationType = ANIM_TYPES[0];

    public void animateTile(Tile t) {
        animateTile(t, animationType);
    }

    public void animateTile(Tile t, int animationType) {
        if (!Settings.animations || Settings.tileAnimDuration == Constants.ANIMATION_DURATION_OFF) {
            return;
        }

        int size = GameState.get().game.getSize();
        int index = t.getIndex();
        int number = t.getNumber();
        int shift = size / 26 + 1; // group tiles by 'shift' count
        int delay;
        switch (animationType) {
            case ANIM_TYPE_INDEX_ASC:
                delay = index / shift;
                break;
            case ANIM_TYPE_INDEX_DESC:
                delay = (size - index) / shift;
                break;
            case ANIM_TYPE_RANDOM:
                delay = rnd.nextInt(10 + 10 * (shift - 1));
                break;
            case ANIM_TYPE_NUMBER_ASC:
                delay = number / shift;
                break;
            case ANIM_TYPE_NUMBER_ASC_NO_GROUP:
                delay = number * ANIM_WINDOW_FRAMES;
                break;
            case ANIM_TYPE_NUMBER_DESC:
                delay = (size - number) / shift;
                break;
            case ANIM_TYPE_ROW:
                delay = index / Settings.gameWidth * ANIM_WINDOW_FRAMES;
                break;
            case ANIM_TYPE_COLUMN:
                delay = index % Settings.gameWidth * ANIM_WINDOW_FRAMES;
                break;
            case ANIM_TYPE_ROW_REVERSE:
                delay = (Settings.gameHeight - index / Settings.gameWidth) * ANIM_WINDOW_FRAMES;
                break;
            case ANIM_TYPE_COLUMN_REVERSE:
                delay = (Settings.gameWidth - index % Settings.gameWidth) * ANIM_WINDOW_FRAMES;
                break;
            case ANIM_TYPE_TOP_LEFT_CORNER:
                delay = fromPoint(0, 0, index);
                break;
            case ANIM_TYPE_TOP_RIGHT_CORNER:
                delay = fromPoint(Settings.gameWidth - 1, 0, index);
                break;
            case ANIM_TYPE_BOTTOM_LEFT_CORNER:
                delay = fromPoint(0, Settings.gameHeight - 1, index);
                break;
            case ANIM_TYPE_BOTTOM_RIGHT_CORNER:
                delay = fromPoint(Settings.gameWidth - 1, Settings.gameHeight - 1, index);
                break;
            case ANIM_TYPE_FROM_CENTER: {
                float x0 = Settings.gameWidth / 2f - .5f;
                float y0 = Settings.gameHeight / 2f - .5f;
                delay = fromPointF(x0, y0, index);
                break;
            }
            case ANIM_TYPE_TO_CENTER: {
                float x0 = Settings.gameWidth / 2f - .5f;
                float y0 = Settings.gameHeight / 2f - .5f;
                delay = (size - fromPointF(x0, y0, index)) / 2;
                break;
            }
            case ANIM_TYPE_ALL:
            default:
                delay = 0;
        }
        t.animateAppearance(delay * Constants.TILE_ANIM_FRAME_MULTIPLIER);
    }

    public void nextAnim() {
        animationType = ANIM_TYPES[++animCurrentIndex % ANIM_TYPES.length];
    }

    private static int fromPoint(int x0, int y0, int index) {
        int x1 = index % Settings.gameWidth;
        int y1 = index / Settings.gameWidth;
        int distance = Math.max(Math.abs(x1 - x0), Math.abs(y1 - y0));
        return (distance + 1) * ANIM_WINDOW_FRAMES;
    }

    private static int fromPointF(float x0, float y0, int index) {
        int x1 = index % Settings.gameWidth;
        int y1 = index / Settings.gameWidth;
        int distance = (int) Math.floor(Math.max(Math.abs(x1 - x0), Math.abs(y1 - y0)));
        return (distance + 1) * ANIM_WINDOW_FRAMES;
    }
}
