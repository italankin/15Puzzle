package com.italankin.fifteen.util;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static com.italankin.fifteen.util.Util.tileCenterCoordinates;

import android.view.MotionEvent;
import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.MotionEvents;

import com.italankin.fifteen.game.Game;

import org.hamcrest.Matcher;

import java.util.List;

public class HoverSolveAction implements ViewAction {

    private final Game game;
    private final List<Integer> solution;

    public HoverSolveAction(Game game, List<Integer> solution) {
        this.game = game;
        this.solution = solution;
    }

    @Override
    public Matcher<View> getConstraints() {
        return isDisplayed();
    }

    @Override
    public String getDescription() {
        return "Solve a game using hover gesture";
    }

    @Override
    public void perform(UiController uiController, final View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int offsetX = location[0];
        int offsetY = location[1];

        float[] zero = tileCenterCoordinates(game, 0);
        float[] coordinates = new float[]{zero[0] + offsetX, zero[1] + offsetY};
        float[] precision = new float[]{1f, 1f};

        MotionEvent down = MotionEvents.sendDown(uiController, coordinates, precision).down;

        for (Integer number : solution) {
            float[] numberXy = tileCenterCoordinates(game, number);
            coordinates[0] = numberXy[0] + offsetX;
            coordinates[1] = numberXy[1] + offsetY;
            MotionEvents.sendMovement(uiController, down, coordinates);
            uiController.loopMainThreadForAtLeast(20);
        }
        MotionEvents.sendUp(uiController, down, coordinates);
    }
}
