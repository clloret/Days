package com.clloret.days.screenshots;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.allOf;

import android.Manifest;
import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;
import com.clloret.days.R;
import com.clloret.days.TestApp;
import com.clloret.days.activities.MainActivity;
import com.clloret.days.screenshots.demo.DemoMode;
import java.util.Locale;
import java.util.Objects;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import tools.fastlane.screengrab.Screengrab;

@LargeTest
@RunWith(AndroidJUnit4.class)
public abstract class BaseScreenshotsTest {

  static final Locale DEVICE_LOCALE = new Locale("en", "US");

  @Rule
  public final ActivityScenarioRule<MainActivity> activityRule =
      new ActivityScenarioRule<>(MainActivity.class);

  @Rule
  public final GrantPermissionRule grantPermissionRule = GrantPermissionRule
      .grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);

  private Resources resources;

  @SuppressWarnings("SameParameterValue")
  private static Matcher<View> childAtPosition(
      final Matcher<View> parentMatcher, final int position) {

    return new TypeSafeMatcher<View>() {
      @Override
      public void describeTo(Description description) {

        description.appendText("Child at position " + position + " in parent ");
        parentMatcher.describeTo(description);
      }

      @Override
      public boolean matchesSafely(View view) {

        ViewParent parent = view.getParent();
        return parent instanceof ViewGroup && parentMatcher.matches(parent)
            && view.equals(((ViewGroup) parent).getChildAt(position));
      }
    };
  }

  private static String getFunctionName(Object object) {

    return Objects.requireNonNull(object.getClass().getEnclosingMethod()).getName();
  }

  private static void configureDemoMode(Context context) {

    new DemoMode(context)
        .enter()
        .setClock()
        .setNetwork()
        .hideNotifications();
  }

  private void openNavigationDrawer() {

    onView(withId(R.id.drawer_layout))
        .check(matches(isClosed(Gravity.LEFT)))
        .perform(DrawerActions.open());
  }

  @Before
  public void setUp() {

    TestApp app = (TestApp) getInstrumentation()
        .getTargetContext().getApplicationContext();

    resources = app.getResources();

    app.getAppComponent().inject(this);

    configureDemoMode(app);
  }

  @Test
  public void makeScreenshot_NewEvent() {

    onView(withId(R.id.fab_main_newevent))
        .perform(click());

    onView(withId(R.id.edittext_eventdetail_name))
        .check(matches(isDisplayed()));

    Espresso.closeSoftKeyboard();

    Screengrab.screenshot(getFunctionName(new Object() {
    }));
  }

  @Test
  public void makeScreenshot_MainView() throws InterruptedException {

    Thread.sleep(2000L);

    onView(withId(R.id.fab_main_newevent))
        .check(matches(isDisplayed()));

    Screengrab.screenshot(getFunctionName(new Object() {
    }));
  }

  @Test
  public void makeScreenshot_ShowEventDetails() {

    onView(withId(R.id.recyclerView))
        .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

    onView(withId(R.id.textview_eventdetail_name))
        .check(matches(isDisplayed()));

    Screengrab.screenshot(getFunctionName(new Object() {
    }));
  }

  @Test
  public void makeScreenshot_ShowMenu() {

    openNavigationDrawer();

    Screengrab.screenshot(getFunctionName(new Object() {
    }));
  }

  @Test
  public void makeScreenshot_ShowEventOrder() {

    Context context = getInstrumentation().getTargetContext();
    openActionBarOverflowOrOptionsMenu(context);
    onView(withText(R.string.title_order))
        .perform(click());

    final String functionName = Objects.requireNonNull(new Object() {
    }.getClass().getEnclosingMethod()).getName();
    Screengrab.screenshot(functionName);
  }

  @Test
  public void makeScreenshot_NewTag() {

    openNavigationDrawer();

    DataInteraction item = onData(
        Matchers.hasToString(resources.getString(R.string.action_new_tag)))
        .inAdapterView(allOf(withId(R.id.listView),
            childAtPosition(
                withId(R.id.navigation_drawer),
                0)));
    item.perform(click());

    Espresso.closeSoftKeyboard();

    Screengrab.screenshot(getFunctionName(new Object() {
    }));
  }

  @Test
  public void makeScreenshot_ShowSettings() {

    openNavigationDrawer();

    DataInteraction item = onData(
        Matchers.hasToString(resources.getString(R.string.action_settings)))
        .inAdapterView(allOf(withId(R.id.listView),
            childAtPosition(
                withId(R.id.navigation_drawer),
                0)));

    item.perform(click());

    Screengrab.screenshot(getFunctionName(new Object() {
    }));
  }
}
