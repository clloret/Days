package com.clloret.days.data.repository;

import static com.google.common.truth.Truth.assertThat;

import android.app.Application;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import com.clloret.days.data.local.DaysDatabase;
import com.clloret.days.data.local.entities.mapper.DbEventDataMapper;
import com.clloret.days.data.local.repository.RoomEventRepository;
import com.clloret.days.data.remote.entities.mapper.ApiEventDataMapper;
import com.clloret.days.data.remote.repository.AirtableEventRepository;
import com.clloret.days.domain.entities.Event;
import com.clloret.days.domain.entities.EventBuilder;
import com.clloret.days.domain.repository.EventRepository;
import com.clloret.test_android_common.MediumTest;
import com.clloret.test_android_common.MockUtils;
import com.clloret.test_android_common.RxImmediateSchedulerRule;
import com.google.common.truth.Correspondence;
import com.google.common.truth.Truth;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import okhttp3.mockwebserver.MockWebServer;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@Category({MediumTest.class})
@Config(sdk = VERSION_CODES.M, application = Application.class)
@RunWith(RobolectricTestRunner.class)
public class AppEventRepositoryTest {

  @ClassRule
  public static final RxImmediateSchedulerRule SCHEDULERS = new RxImmediateSchedulerRule();

  private static final String API_KEY = "api_key";
  private static final String BASE = "base";
  private static final Correspondence<Event, String> EVENT_HAS_NAME =
      Correspondence.from((actual, expected) -> actual != null && actual.getName().equals(expected),
          "contains");
  private final MockWebServer server = new MockWebServer();
  private final MockUtils mockUtils = new MockUtils(server);
  private final DbEventDataMapper dbEventDataMapper = new DbEventDataMapper();
  private final ApiEventDataMapper apiEventDataMapper = new ApiEventDataMapper();
  private EventRepository eventRepository;
  private RoomEventRepository roomEventRepository;
  private DaysDatabase db;

  @Before
  public void setUp() throws Exception {

    server.start();
    String serviceEndpoint = server.url("v0").toString();

    Context appContext = ApplicationProvider.getApplicationContext();

    db = Room.inMemoryDatabaseBuilder(appContext, DaysDatabase.class)
        .allowMainThreadQueries().build();

    roomEventRepository = new RoomEventRepository(db.eventDao(), dbEventDataMapper);
    AirtableEventRepository airtableEventRepository = new AirtableEventRepository(appContext,
        serviceEndpoint, API_KEY, BASE, apiEventDataMapper);
    eventRepository = new AppEventRepository(roomEventRepository, airtableEventRepository);
  }

  @After
  public void tearDown() throws Exception {

    server.shutdown();
    db.close();
  }

  @Test
  public void getEvents_Always_InsertAllValuesInLocalDataStore() throws Exception {

    String fileName = "events_all.json";
    mockUtils.enqueueMockResponse(200, fileName);

    List<Event> events = eventRepository.getAll(true).blockingGet();
    int valueCount = events.size();

    TestObserver<Event> testObserver = roomEventRepository.getAll(false)
        .toObservable()
        .concatMap(Observable::fromIterable)
        .test();

    testObserver.awaitTerminalEvent();

    testObserver
        .assertComplete()
        .assertNoErrors()
        .assertValueCount(valueCount);
  }

  @Test
  public void createEvent_Always_CreateValueInLocalDataStore() throws Exception {

    String fileName = "event_created.json";
    mockUtils.enqueueMockResponse(200, fileName);

    Event newEvent = newEvent(true);

    Event event = eventRepository.create(newEvent).blockingGet();

    TestObserver<Event> testObserver = roomEventRepository.getAll(false)
        .toObservable()
        .concatMap(Observable::fromIterable)
        .test();

    testObserver
        .assertComplete()
        .assertNoErrors()
        .assertValueCount(1)
        .assertValue(result -> Objects.equals(result.getId(), event.getId()));
  }

  @Test
  public void editEvent_Always_EditValueInLocalDataStore() throws Exception {

    String fileName = "event_edited.json";
    mockUtils.enqueueMockResponse(200, fileName);

    Event newEvent = newEvent(false);
    Event createdEvent = roomEventRepository.create(newEvent).blockingGet();

    createdEvent.setName("Edited event");

    eventRepository.edit(newEvent).blockingGet();

    TestObserver<Event> testObserver = roomEventRepository.getAll(false)
        .toObservable()
        .concatMap(Observable::fromIterable)
        .test();

    testObserver
        .assertComplete()
        .assertNoErrors()
        .assertValueCount(1);

    Truth.assertThat(testObserver.values())
        .comparingElementsUsing(EVENT_HAS_NAME)
        .containsExactly(createdEvent.getName());
  }

  @Test
  public void deleteEvent_Always_DeleteValueInLocalDataStore() throws Exception {

    String fileName = "event_deleted.json";
    mockUtils.enqueueMockResponse(200, fileName);

    Event newEvent = newEvent(false);
    Event createdEvent = roomEventRepository.create(newEvent).blockingGet();

    Boolean deleted = eventRepository.delete(createdEvent).blockingGet();

    assertThat(deleted).isTrue();

    TestObserver<Event> testObserver = roomEventRepository.getAll(false)
        .toObservable()
        .concatMap(Observable::fromIterable)
        .test();

    testObserver
        .assertComplete()
        .assertNoErrors()
        .assertNoValues();
  }

  @NonNull
  private Event newEvent(boolean nullId) {

    DateTime dt = new DateTime();
    Date date = dt.withYear(2018).withMonthOfYear(1).withDayOfMonth(22).toDate();

    return new EventBuilder()
        .setId(nullId ? null : "rec0XqfldqwfXeoaa")
        .setName("New event")
        .setDescription("Description")
        .setDate(date)
        .build();
  }

}