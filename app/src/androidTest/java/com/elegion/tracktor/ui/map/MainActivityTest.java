package com.elegion.tracktor.ui.map;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.UiDevice;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.elegion.tracktor.R;
import com.elegion.tracktor.utils.StringUtils;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

public class MainActivityTest {
    private int mRecyclerItemsCount;

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);


    @Test
    public void testMainActivityStartTracking() {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        Intents.init();
        onView(withId(R.id.buttonStart)).perform(click());
        onView(isRoot()).perform(waitFor(TimeUnit.SECONDS.toMillis(15)));
        onView(withId(R.id.buttonStop)).perform(click());
        onView(isRoot()).perform(waitFor(TimeUnit.SECONDS.toMillis(5)));
        onView(withId(R.id.ivScreenshot)).check(matches(isDisplayed()));
        clickMenuItem("Поделиться");
        intended(hasAction(Intent.ACTION_CHOOSER));
        device.pressBack();
        device.pressBack();
        clickMenuItem("Статистика");
        mRecyclerItemsCount = getCountFromRecyclerView(R.id.rvTrackList);
        onView(withId(R.id.rvTrackList)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        clickMenuItem("Удалить");
        onView(withId(R.id.rvTrackList)).check(new RecyclerViewItemCountAssertion(mRecyclerItemsCount - 1));
        Intents.release();
    }

    private static void clickMenuItem(String text) {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getContext());
        onView(isRoot()).perform(waitFor(TimeUnit.SECONDS.toMillis(2)));
        onView(withText(text)).perform(click());
    }

    private static ViewAction waitFor(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, final View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }


    public static int getCountFromRecyclerView(@IdRes int RecyclerViewId) {
        final int[] COUNT = new int[1];
        Matcher matcher = new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View item) {
                COUNT[0] = ((RecyclerView) item).getAdapter().getItemCount();
                return true;
            }

            @Override
            public void describeTo(Description description) {
            }
        };
        onView(allOf(withId(RecyclerViewId), isDisplayed())).check(matches(matcher));
        int result = COUNT[0];
        return result;
    }

    public class RecyclerViewItemCountAssertion implements ViewAssertion {

        private final Matcher<Integer> matcher;

        public RecyclerViewItemCountAssertion(int expectedCount) {
            this.matcher = is(expectedCount);
        }

        public RecyclerViewItemCountAssertion(Matcher<Integer> matcher) {
            this.matcher = matcher;
        }

        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }

            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            assertThat(adapter.getItemCount(), matcher);
        }

    }
}
