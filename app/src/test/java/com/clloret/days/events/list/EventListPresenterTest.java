package com.clloret.days.events.list;

import static com.clloret.days.events.SampleBuilder.createEvent;
import static com.clloret.days.events.SampleBuilder.createEventList;
import static com.clloret.days.events.SampleBuilder.createEventViewModel;
import static io.reactivex.Single.just;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.clloret.days.device.TimeProviderImpl;
import com.clloret.days.domain.entities.Event;
import com.clloret.days.domain.events.filter.EventFilterAll;
import com.clloret.days.domain.interactors.events.CreateEventUseCase;
import com.clloret.days.domain.interactors.events.DeleteEventUseCase;
import com.clloret.days.domain.interactors.events.FavoriteEventUseCase;
import com.clloret.days.domain.interactors.events.GetEventsUseCase;
import com.clloret.days.domain.interactors.events.GetFilteredEventsUseCase;
import com.clloret.days.domain.interactors.events.GetFilteredEventsUseCase.RequestValues;
import com.clloret.days.domain.interactors.events.ResetEventDateUseCase;
import com.clloret.days.domain.interactors.events.ToggleEventReminderUseCase;
import com.clloret.days.model.entities.EventViewModel;
import com.clloret.test_android_common.RxImmediateSchedulerRule;
import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.subjects.PublishSubject;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class EventListPresenterTest {

  @ClassRule
  public static final RxImmediateSchedulerRule SCHEDULERS = new RxImmediateSchedulerRule();

  @Mock
  private GetEventsUseCase getEventsUseCase;

  @Mock
  private GetFilteredEventsUseCase getFilteredEventsUseCase;

  @Mock
  private FavoriteEventUseCase favoriteEventUseCase;

  @Mock
  private ResetEventDateUseCase resetEventDateUseCase;

  @Mock
  private ToggleEventReminderUseCase toggleEventReminderUseCase;

  @Mock
  private DeleteEventUseCase deleteEventUseCase;

  @Mock
  private CreateEventUseCase createEventUseCase;

  @Mock
  private EventBus eventBus;

  @Mock
  private TimeProviderImpl timeProvider;

  @Mock
  private EventListView eventListView;

  @InjectMocks
  private EventListPresenter eventListPresenter;

  private void loadEventsAndVerify() {

    List<Event> eventList = createEventList();

    when(getFilteredEventsUseCase.execute(any())).thenReturn(
        just(eventList)
    );

    EventFilterAll eventFilterAll = new EventFilterAll();

    eventListPresenter.setFilterStrategy(eventFilterAll);
    eventListPresenter.loadEvents(false);

    verify(getFilteredEventsUseCase).execute(any(RequestValues.class));
    verify(eventListView).setData(argThat(list -> list.size() == 2));
    verify(eventListView).showContent();
  }

  @Before
  public void setUp() {

    MockitoAnnotations.openMocks(this);

    eventListPresenter.attachView(eventListView);

    loadEventsAndVerify();
  }

  @Test
  public void deleteEvent_Always_CallApiAndNotifyView() {

    final Event event = createEvent();
    final EventViewModel eventViewModel = createEventViewModel();

    when(deleteEventUseCase.execute(event))
        .thenReturn(new Maybe<Boolean>() {
          @Override
          protected void subscribeActual(MaybeObserver<? super Boolean> observer) {

            observer.onSuccess(true);
          }
        });

    eventListPresenter.deleteEvent(eventViewModel);

    verify(deleteEventUseCase).execute(event);
    verify(eventListView).deleteSuccessfully(eventViewModel, true);
  }

  @Test
  public void makeEventFavorite_Always_CallApiAndNotifyView() {

    final Event event = createEvent();
    final EventViewModel eventViewModel = createEventViewModel();

    when(favoriteEventUseCase.execute(event))
        .thenReturn(new Maybe<Event>() {
          @Override
          protected void subscribeActual(MaybeObserver<? super Event> observer) {

            observer.onSuccess(event);
          }
        });

    eventListPresenter.makeEventFavorite(eventViewModel);

    verify(favoriteEventUseCase).execute(any(Event.class));
    verify(eventListView).favoriteSuccessfully(any(EventViewModel.class));
  }

  @Test
  public void resetDate_Always_CallApiAndNotifyView() {

    final Event event = createEvent();
    final EventViewModel eventViewModel = createEventViewModel();

    when(resetEventDateUseCase.execute(event))
        .thenReturn(new Maybe<Event>() {
          @Override
          protected void subscribeActual(MaybeObserver<? super Event> observer) {

            observer.onSuccess(event);
          }
        });

    when(timeProvider.getCurrentDate())
        .thenReturn(new LocalDate(2000, 1, 1));

    eventListPresenter.resetDate(eventViewModel);

    verify(resetEventDateUseCase).execute(any(Event.class));
    verify(eventListView).dateResetSuccessfully(any(EventViewModel.class));
  }

  @Test
  public void undoDelete_Always_CallApiAndNotifyView() {

    final Event event = createEvent();
    final EventViewModel eventViewModel = createEventViewModel();

    when(createEventUseCase.execute(event))
        .thenReturn(new Maybe<Event>() {
          @Override
          protected void subscribeActual(MaybeObserver<? super Event> observer) {

            observer.onSuccess(event);
          }
        });

    eventListPresenter.undoDelete(eventViewModel);

    verify(createEventUseCase).execute(any(Event.class));
    verify(eventListView).undoDeleteSuccessfully(any(EventViewModel.class));
  }

  @Test
  public void toggleEventReminder_Always_CallApiAndNotifyView() {

    final Event event = createEvent();
    final EventViewModel eventViewModel = createEventViewModel();

    when(toggleEventReminderUseCase.execute(event))
        .thenReturn(new Maybe<Event>() {
          @Override
          protected void subscribeActual(MaybeObserver<? super Event> observer) {

            observer.onSuccess(event);
          }
        });

    eventListPresenter.toggleEventReminder(eventViewModel);

    verify(toggleEventReminderUseCase).execute(any(Event.class));
    verify(eventListView).reminderSuccessfully(any(EventViewModel.class));
  }

  @Test
  public void observeSearchQuery_WhenFilterMatches_ShowFilteredEvents() {

    final PublishSubject<String> subject = PublishSubject.create();

    eventListPresenter.observeSearchQuery(subject);

    subject.onNext("Filtered aeiouaeiou");

    verify(eventListView).setData(argThat(list -> list.size() == 1));
    verify(eventListView).showContent();
  }
}
