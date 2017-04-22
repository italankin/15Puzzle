package com.italankin.fifteen.anim;

import com.italankin.fifteen.Tile;

public class TileXAnimator extends TileAnimator {

    public TileXAnimator(Tile target) {
        super(target);
    }

    @Override
    protected void update(Tile target, float value) {
        target.setCanvasX(value);
    }

}
