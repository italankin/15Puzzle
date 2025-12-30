package com.italankin.fifteen.util;

import android.view.View;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import com.italankin.fifteen.GameSurface;
import org.hamcrest.Matcher;

public class CreateNewGameAction implements ViewAction {

    @Override
    public Matcher<View> getConstraints() {
        return ViewMatchers.isAssignableFrom(GameSurface.class);
    }

    @Override
    public String getDescription() {
        return "Create new game";
    }

    @Override
    public void perform(UiController uiController, View view) {
        ((GameSurface) view).createNewGame(false);
    }
}
