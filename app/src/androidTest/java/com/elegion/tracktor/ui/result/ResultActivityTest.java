package com.elegion.tracktor.ui.result;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import com.elegion.tracktor.R;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class ResultActivityTest {

    @Rule
    public ActivityTestRule<ResultActivity> mResultActivityActivityTestRule =
            new ActivityTestRule<>(ResultActivity.class);


    @Test
    public void testResultsListIsShown(){
        onView(withId(R.id.rvTrackList))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testOnListItemClick(){
        onView(withId(R.id.rvTrackList))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.ivScreenshot))
                .check(matches(isDisplayed()));

    }
}