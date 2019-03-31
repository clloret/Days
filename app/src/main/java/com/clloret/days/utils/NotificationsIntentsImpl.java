package com.clloret.days.utils;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static com.clloret.days.device.receivers.ReminderReceiver.ACTION_DELETE_REMINDER;
import static com.clloret.days.device.receivers.ReminderReceiver.ACTION_RESET_EVENT_DATE;
import static com.clloret.days.device.receivers.ReminderReceiver.EXTRA_EVENT_ID;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import com.clloret.days.device.notifications.NotificationsIntents;
import com.clloret.days.device.receivers.ReminderReceiver;
import com.clloret.days.domain.entities.Event;
import com.clloret.days.events.edit.EventEditActivity;
import com.clloret.days.model.entities.EventViewModel;
import com.clloret.days.model.entities.mapper.EventViewModelMapper;

public class NotificationsIntentsImpl implements NotificationsIntents {

  private final Context context;
  private final EventViewModelMapper eventViewModelMapper;

  public NotificationsIntentsImpl(Context context, EventViewModelMapper eventViewModelMapper) {

    this.context = context;
    this.eventViewModelMapper = eventViewModelMapper;
  }

  @Override
  public PendingIntent getViewEventIntent(Event event) {

    EventViewModel eventViewModel = eventViewModelMapper.fromEvent(event);
    Intent intent = EventEditActivity.getCallingIntent(context, eventViewModel);

    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
    stackBuilder.addNextIntentWithParentStack(intent);
    return stackBuilder
        .getPendingIntent(0, FLAG_UPDATE_CURRENT);
  }

  @Override
  public PendingIntent getDeleteEventIntent(Event event) {

    Intent intent = new Intent(context, ReminderReceiver.class);
    intent.setAction(ACTION_DELETE_REMINDER);
    intent.putExtra(EXTRA_EVENT_ID, event.getId());

    return PendingIntent.getBroadcast(context, 0, intent, FLAG_UPDATE_CURRENT);
  }

  @Override
  public PendingIntent getResetEventIntent(Event event) {

    Intent intent = new Intent(context, ReminderReceiver.class);
    intent.setAction(ACTION_RESET_EVENT_DATE);
    intent.putExtra(EXTRA_EVENT_ID, event.getId());

    return PendingIntent.getBroadcast(context, 0, intent, FLAG_UPDATE_CURRENT);
  }

}