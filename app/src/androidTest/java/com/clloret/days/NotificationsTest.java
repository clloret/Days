package com.clloret.days;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import android.content.res.Resources;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;
import com.clloret.days.activities.MainActivity;
import com.clloret.days.dagger.AppTestComponent;
import com.clloret.days.domain.entities.Event;
import com.clloret.days.domain.events.EventPeriodFormat;
import com.clloret.days.domain.interactors.events.DeleteEventUseCase;
import com.clloret.days.domain.interactors.events.ResetEventDateUseCase;
import com.clloret.days.domain.utils.StringResourceProvider;
import io.reactivex.Maybe;
import java.util.Date;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
@LargeTest
@RunWith(AndroidJUnit4.class)
public class NotificationsTest {

  private static final String CLEAR_ALL_NOTIFICATION_RES = "com.android.systemui:id/dismiss_text";
  private static final String EXPECTED_ACTION_RES = "android:id/action0";

  @Rule
  public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(
      MainActivity.class);

  @Inject
  DeleteEventUseCase deleteEventUseCase;

  @Inject
  ResetEventDateUseCase resetEventDateUseCase;

  @Inject
  StringResourceProvider stringResourceProvider;

  @Inject
  EventPeriodFormat eventPeriodFormat;

  private String expectedActionNameDelete;
  private String expectedActionNameReset;
  private String expectedAppName;
  private UiDevice uiDevice;
  private NotificationsCommon notificationsCommon;

  @Before
  public void setUp() {

    TestApp app = (TestApp) InstrumentationRegistry.getInstrumentation()
        .getTargetContext().getApplicationContext();

    AppTestComponent appComponent = app.getAppComponent();
    appComponent.inject(this);

    ActivityScenario<MainActivity> scenario = activityRule.getScenario();
    scenario.onActivity(activity -> {
      Resources res = activity.getResources();
      expectedAppName = res.getString(R.string.app_name);
    });

    expectedActionNameDelete = stringResourceProvider.getEventDeleteNotificationAction();
    expectedActionNameReset = stringResourceProvider.getEventResetNotificationAction();

    notificationsCommon = new NotificationsCommon(stringResourceProvider, eventPeriodFormat);

    uiDevice = getUiDevice();
  }

  @After
  public void tearDown() {

    hideNotification();
  }

  private UiDevice getUiDevice() {

    return UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
  }

  @SuppressWarnings("unused")
  private void clearAllNotifications() {

    UiDevice uiDevice = getUiDevice();
    uiDevice.openNotification();
    uiDevice.wait(Until.hasObject(By.textStartsWith(expectedAppName)), NotificationsCommon.TIMEOUT);
    UiObject2 clearAll = uiDevice.findObject(By.res(CLEAR_ALL_NOTIFICATION_RES));
    clearAll.click();
  }

  @SuppressWarnings("unused")
  private void hideNotification() {

    uiDevice.swipe(
        200,
        200,
        200,
        100,
        5
    );
  }

  private Event createTestEvent(String id, String name) {

    Event event = new Event();
    event.setId(id);
    event.setName(name);
    event.setDate(new Date());

    return event;
  }

  @Test
  public void showNotification_WhenOpen_ShowEventDetails() {

    uiDevice.pressHome();

    final Event event = notificationsCommon.getSampleEventWithTodayDate(0);
    notificationsCommon.showNotification(event);

    uiDevice.openNotification();
    uiDevice.wait(Until.hasObject(By.textStartsWith(expectedAppName)), NotificationsCommon.TIMEOUT);
    UiObject2 title = uiDevice.findObject(By.text(event.getName()));
    title.click();
    uiDevice.wait(Until.hasObject(By.text(event.getName())), NotificationsCommon.TIMEOUT);

    onView(withId(R.id.textview_eventdetail_name))
        .check(matches(isDisplayed()))
        .check(matches(withText(event.getName())));

    uiDevice.pressBack();
  }

  @Test
  public void showNotification_WhenActionDelete_DeleteEvent() throws UiObjectNotFoundException {

    Mockito
        .when(deleteEventUseCase.execute(any()))
        .thenReturn(Maybe.just(true));

    uiDevice.pressHome();

    final Event event = notificationsCommon.getSampleEventWithTodayDate(1);
    notificationsCommon.showNotification(event);

    uiDevice.openNotification();

    uiDevice.wait(Until.hasObject(By.textStartsWith(expectedAppName)), NotificationsCommon.TIMEOUT);
    UiObject notificationOpenItem = uiDevice.findObject(new UiSelector()
        .resourceId(EXPECTED_ACTION_RES)
        .description(expectedActionNameDelete)
        .enabled(true));
    notificationOpenItem.click();

    uiDevice.pressHome();

    verify(deleteEventUseCase, timeout(NotificationsCommon.TIMEOUT).atLeastOnce()).execute(any());
  }

  @Test
  public void showNotification_WhenActionReset_ResetEventDate() throws UiObjectNotFoundException {

    final Event event = notificationsCommon.getSampleEventWithTodayDate(2);

    Mockito
        .when(resetEventDateUseCase.execute(any()))
        .thenReturn(Maybe.just(event));

    uiDevice.pressHome();

    notificationsCommon.showNotification(event);

    uiDevice.openNotification();

    uiDevice.wait(Until.hasObject(By.textStartsWith(expectedAppName)), NotificationsCommon.TIMEOUT);
    UiObject notificationOpenItem = uiDevice.findObject(new UiSelector()
        .resourceId(EXPECTED_ACTION_RES)
        .description(expectedActionNameReset)
        .enabled(true));
    notificationOpenItem.click();

    uiDevice.pressHome();

    verify(resetEventDateUseCase, timeout(NotificationsCommon.TIMEOUT).atLeastOnce())
        .execute(any());
  }

}
