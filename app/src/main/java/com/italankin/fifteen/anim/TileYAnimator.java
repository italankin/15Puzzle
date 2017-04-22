package com.italankin.fifteen.anim;

import com.italankin.fifteen.Tile;

public class TileYAnimator extends TileAnimator {

    public TileYAnimator(Tile target) {
        super(target);
    }

    @Override
    protected void update(Tile target, float value) {
        target.setCanvasY(value);
    }

}
