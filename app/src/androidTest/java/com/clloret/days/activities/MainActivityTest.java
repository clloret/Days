package com.clloret.days.activities;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.clloret.days.R;
import com.clloret.days.TestApp;
import com.clloret.days.dagger.AppTestComponent;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

  @Rule
  public ActivityTestRule activityRule = new ActivityTestRule<>(MainActivity.class);

  @Before
  public void setup() {

    TestApp app = (TestApp) InstrumentationRegistry.getInstrumentation()
        .getTargetContext().getApplicationContext();

    AppTestComponent appComponent = app.getAppComponent();
    appComponent.inject(this);
  }

  @Test
  public void eventCard_WhenClick_ShowEventDetails() {

    onView(withId(R.id.recyclerView))
        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

    onView(withId(R.id.textview_eventdetail_name))
        .check(matches(isDisplayed()));
  }

  @Test
  public void fabNewEvent_WhenClick_ShowNewEvent() {

    onView(withId(R.id.fab_main_newevent))
        .perform(click());

    onView(withId(R.id.edittext_eventdetail_name))
        .check(matches(isDisplayed()));
  }

}