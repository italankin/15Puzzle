package com.italankin.fifteen;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.SharedPreferences;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.italankin.fifteen.game.ClassicGame;
import com.italankin.fifteen.game.Game;
import com.italankin.fifteen.util.HoverSolveAction;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HoverTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void before() {
        Settings.animationSpeed = 0;
        Settings.postInvalidateDelay = 20;
    }

    @Test
    public void test() {
        Settings.gameType = Constants.TYPE_CLASSIC;
        setSavedGame(new ClassicGame(4, 4, Arrays.asList(
                7, 10, 11, 6,
                13, 3, 12, 1,
                2, 15, 9, 5,
                0, 4, 8, 14
        )));

        onView(withId(R.id.game_view)).check(matches(isDisplayed()));

        List<Integer> solution = Arrays.asList(
                2, 15, 9, 12, 1, 5, 12, 8, 4, 9,
                15, 13, 3, 10, 7, 3, 10, 1, 8, 4,
                14, 12, 4, 8, 5, 4, 8, 15, 13, 2,
                9, 13, 2, 10, 1, 5, 11, 7, 3, 1,
                5, 2, 10, 9, 13, 14, 15, 11, 7, 6,
                4, 7, 6, 3, 2, 6, 7, 8, 12);
        Game game = GameState.get().game;

        onView(withId(R.id.game_view)).perform(new HoverSolveAction(game, solution));

        Assert.assertTrue(game.isSolved());
    }

    private void setSavedGame(Game game) {
        GameState.set(game, false);
        SharedPreferences.Editor editor = Settings.prefs.edit();
        SaveGameManager.saveGame(editor);
        editor.commit();
    }
}
