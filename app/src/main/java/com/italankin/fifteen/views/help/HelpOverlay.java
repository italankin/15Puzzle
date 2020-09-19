package com.italankin.fifteen.views.help;

import android.graphics.Canvas;
import android.graphics.RectF;

import com.italankin.fifteen.Generator;
import com.italankin.fifteen.Settings;
import com.italankin.fifteen.Tile;
import com.italankin.fifteen.TileAppearAnimator;
import com.italankin.fifteen.views.FieldView;
import com.italankin.fifteen.views.overlay.FieldOverlay;

import java.util.List;

public class HelpOverlay extends FieldOverlay {

    private final FieldView fieldView;

    public HelpOverlay(TileAppearAnimator tileAppearAnimator, RectF rect, RectF fieldRect) {
        super(rect);
        this.fieldView = new FieldView(fieldRect);

        List<Integer> numbers = Generator.generate(Settings.gameWidth, Settings.gameHeight, Settings.gameMode);
        for (int i = 0, size = numbers.size(); i < size; i++) {
            int number = numbers.get(i);
            if (number == 0) {
                continue;
            }
            Tile t = new Tile(number, i);
            if (Settings.animations) {
                tileAppearAnimator.animateTile(t, TileAppearAnimator.ANIM_TYPE_NUMBER_ASC_NO_GROUP);
            }
            fieldView.addTile(t);
        }
    }

    @Override
    public boolean show() {
        if (mShow) {
            return true;
        }
        mAlpha = 1;
        return (mShow = true);
    }

    @Override
    public void draw(Canvas canvas, long elapsedTime) {
        if (!mShow) {
            return;
        }
        super.draw(canvas, elapsedTime);
        fieldView.draw(canvas, elapsedTime);
    }

    @Override
    public void update() {
        super.update();
        fieldView.update();
    }

    @Override
    public boolean hide() {
        fieldView.clear();
        return super.hide();
    }
}
