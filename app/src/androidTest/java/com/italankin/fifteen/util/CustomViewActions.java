package com.italankin.fifteen.util;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Tap;

public class CustomViewActions {

    public static ViewAction clickXy(float x, float y) {
        return new GeneralClickAction(
                Tap.SINGLE,
                view -> {
                    int[] screenPos = new int[2];
                    view.getLocationOnScreen(screenPos);
                    float screenX = screenPos[0] + x;
                    float screenY = screenPos[1] + y;
                    return new float[]{screenX, screenY};
                },
                Press.FINGER,
                0,
                0);
    }
}
