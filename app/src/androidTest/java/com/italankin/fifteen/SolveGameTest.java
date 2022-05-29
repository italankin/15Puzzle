package com.italankin.fifteen;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static com.italankin.fifteen.util.CustomViewActions.clickXy;

import android.content.SharedPreferences;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.italankin.fifteen.game.ClassicGame;
import com.italankin.fifteen.game.Game;
import com.italankin.fifteen.game.SnakeGame;
import com.italankin.fifteen.game.SpiralGame;
import com.italankin.fifteen.util.Util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SolveGameTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void before() {
        Settings.animationSpeed = 0;
        Settings.postInvalidateDelay = 20;
    }

    @Test
    public void classic() {
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
        solveAndAssert(solution);
    }

    @Test
    public void snake() {
        Settings.gameType = Constants.TYPE_SNAKE;
        setSavedGame(new SnakeGame(4, 4, Arrays.asList(
                0, 4, 1, 7,
                9, 12, 6, 5,
                14, 10, 3, 2,
                8, 15, 11, 13
        )));

        onView(withId(R.id.game_view)).check(matches(isDisplayed()));

        List<Integer> solution = Arrays.asList(
                4, 1, 6, 12, 9, 4, 1, 6, 7, 5, 12, 3,
                2, 12, 3, 2, 10, 14, 8, 15, 14, 9, 6,
                7, 2, 6, 4, 8, 9, 10, 6, 4, 7, 2, 4,
                3, 5, 4, 3, 6, 11, 14, 15);
        solveAndAssert(solution);
    }

    @Test
    public void spiral() {
        Settings.gameType = Constants.TYPE_SPIRAL;
        setSavedGame(new SpiralGame(4, 4, Arrays.asList(
                12, 7, 0, 3,
                4, 14, 5, 2,
                1, 10, 11, 9,
                15, 13, 8, 6
        )));

        onView(withId(R.id.game_view)).check(matches(isDisplayed()));

        List<Integer> solution = Arrays.asList(
                5, 2, 3, 5, 7, 12, 4, 1, 10, 14, 12, 4,
                1, 12, 2, 7, 4, 2, 14, 13, 8, 11, 7, 3,
                5, 4, 3, 14, 13, 8, 11, 7, 9, 6, 7, 9,
                8, 11, 15, 10, 11, 15, 9, 8, 15);
        solveAndAssert(solution);
    }

    private void setSavedGame(Game game) {
        GameState.set(game, false);
        SharedPreferences.Editor editor = Settings.prefs.edit();
        SaveGameManager.saveGame(editor);
        editor.commit();
    }

    private void solveAndAssert(List<Integer> solution) {
        Game game = GameState.get().game;
        for (Integer move : solution) {
            float[] coordinates = Util.tileCenterCoordinates(game, move);
            onView(withId(R.id.game_view))
                    .perform(clickXy(coordinates[0], coordinates[1]));
        }
        Assert.assertTrue(game.isSolved());
    }
}
