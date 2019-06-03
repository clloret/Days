package com.clloret.days.events.create;

import static com.clloret.days.utils.FabProgressUtils.fixFinalIconPosition;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.clloret.days.R;
import com.clloret.days.base.BaseMvpActivity;
import com.clloret.days.domain.utils.Optional;
import com.clloret.days.events.common.PeriodTextFormatter;
import com.clloret.days.events.common.SelectDateHelper;
import com.clloret.days.events.common.SelectPeriodHelper;
import com.clloret.days.events.common.SelectTagsDialog.SelectTagsDialogListener;
import com.clloret.days.events.common.SelectTagsHelper;
import com.clloret.days.model.entities.EventViewModel;
import com.clloret.days.model.entities.TagViewModel;
import com.clloret.days.model.events.EventCreatedEvent;
import com.clloret.days.model.events.ShowMessageEvent;
import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import dagger.android.AndroidInjection;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.joda.time.LocalDate;

public class EventCreateActivity extends
    BaseMvpActivity<EventCreateView, EventCreatePresenter> implements EventCreateView,
    SelectTagsDialogListener, FABProgressListener {

  private static final String EXTRA_TAG = "com.clloret.days.extras.EXTRA_TAG";

  @Inject
  EventCreatePresenter injectPresenter;

  @BindView(R.id.toolbar)
  Toolbar toolbar;

  @BindView(R.id.event_switcher)
  ViewSwitcher eventSwitcher;

  @BindView(R.id.description_switch)
  ViewSwitcher descriptionSwitcher;

  @BindView(R.id.layout_eventdetail_name)
  TextInputLayout nameLayout;

  @BindView(R.id.edittext_eventdetail_name)
  EditText nameEdit;

  @BindView(R.id.edittext_eventdetail_description)
  EditText descriptionEdit;

  @BindView(R.id.textview_eventdetail_date)
  TextView dateText;

  @BindView(R.id.textview_eventdetail_tags)
  TextView tagsText;

  @BindView(R.id.textview_eventdetail_reminder)
  TextView reminderText;

  @BindView(R.id.textview_eventdetail_reset)
  TextView timeLapseResetText;

  @BindView(R.id.fab)
  FloatingActionButton fab;

  @BindView(R.id.fabProgress)
  FABProgressCircle fabProgress;

  @Inject
  SelectTagsHelper selectTagsHelper;

  @Inject
  SelectPeriodHelper selectPeriodHelper;

  @Inject
  PeriodTextFormatter periodTextFormatter;

  private LocalDate selectedDate;
  private EventViewModel newEvent = new EventViewModel();
  private Optional<TagViewModel> selectedTag;

  public static Intent getCallingIntent(Context context, Optional<TagViewModel> tag) {

    Intent intent = new Intent(context, EventCreateActivity.class);
    tag.ifPresent(value -> intent.putExtra(EXTRA_TAG, value));

    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_event_detail);

    ButterKnife.bind(this);

    configureActionBar(toolbar);

    fab.setImageDrawable(getDrawable(R.drawable.ic_save_wht_24dp));

    fixFinalIconPosition(fabProgress);
    fabProgress.attachListener(this);

    eventSwitcher.showNext();
    descriptionSwitcher.showNext();

    showSoftKeyboard();

    TagViewModel tagViewModel = getIntent().getParcelableExtra(EXTRA_TAG);
    selectedTag = Optional.ofNullable(tagViewModel);

    presenter.loadTags();
  }

  @Override
  protected void injectDependencies() {

    AndroidInjection.inject(this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    getMenuInflater().inflate(R.menu.menu_event_details, menu);
    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {

    menu.findItem(R.id.menu_add_to_calendar).setVisible(false);

    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    int id = item.getItemId();

    if (id == R.id.menu_delete) {
      discardEvent();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @NonNull
  @Override
  public EventCreatePresenter createPresenter() {

    return injectPresenter;
  }

  @Override
  public void onSuccessfully(EventViewModel event) {

    EventBus.getDefault().post(new EventCreatedEvent(event));
  }

  @Override
  public void onError(String message) {

    showToastMessage(message);
  }

  @Override
  public void setData(List<TagViewModel> data) {

    selectTagsHelper.setMapTags(data);

    selectedTag.ifPresent(value -> selectTagsHelper.addTagToSelection(value));

    showSelectedTags();
    showSelectedReminder();
    showSelectedTimeLapseReset();
  }

  @Override
  public void showError(Throwable t) {

    showToastMessage(t.getLocalizedMessage());
  }

  @Override
  public void showIndeterminateProgress() {

    fabProgress.show();
  }

  @Override
  public void showIndeterminateProgressFinalAnimation() {

    fabProgress.beginFinalAnimation();
  }

  @Override
  public void hideIndeterminateProgress() {

    fabProgress.hide();
  }

  @Override
  public void onFABProgressAnimationEnd() {

    finish();
  }

  @Override
  public void onEmptyEventNameError() {

    nameLayout.setError(getString(R.string.msg_error_event_name_required));
    nameLayout.setErrorEnabled(true);
    nameLayout.getEditText().requestFocus();
  }

  @Override
  public void onEmptyEventDateError() {

    showToastMessage(R.string.msg_error_event_date_required);
  }

  @Override
  public void onFinishTagsDialog(Collection<TagViewModel> selectedItems) {

    selectTagsHelper.updateSelectedTags(selectedItems);
    showSelectedTags();
  }

  @OnClick(R.id.layout_eventdetail_date)
  public void onClickDay() {

    selectDate();
  }

  @OnClick(R.id.layout_eventdetail_tags)
  public void onClickTags() {

    selectTags();
  }

  @OnClick(R.id.layout_eventdetail_reminder)
  public void onClickReminder() {

    selectReminder();
  }

  @OnClick(R.id.button_eventdetail_clear_reminder)
  public void onClickClearReminder() {

    clearReminder();
  }

  @OnClick(R.id.layout_eventdetail_reset)
  public void onClickReset() {

    selectTimeLapseReset();
  }

  @OnClick(R.id.fab)
  public void onClickFab() {

    saveEvent();
  }

  private void showSelectedTags() {

    tagsText.setText(selectTagsHelper.showSelectedTags());
  }

  private void showSelectedReminder() {

    reminderText.setText(periodTextFormatter.formatReminder(newEvent));
  }

  private void showSelectedTimeLapseReset() {

    timeLapseResetText.setText(periodTextFormatter.formatTimeLapseReset(newEvent));
  }

  private void selectDate() {

    LocalDate today = new LocalDate();
    LocalDate defaultDate = selectedDate == null ? today : selectedDate;
    SelectDateHelper.selectDate(this, defaultDate, (date, formattedDate) -> {

      selectedDate = date;
      dateText.setText(formattedDate);
    });
  }

  private void selectTags() {

    selectTagsHelper.showSelectTagsDialog(this, this::showSnackbarMessage);
  }

  private void selectReminder() {

    selectPeriodHelper.showSelectReminderDialog(this, newEvent, (period, timeUnit) -> {
      newEvent.setReminder(period);
      newEvent.setReminderUnit(timeUnit);

      showSelectedReminder();
    });
  }

  private void clearReminder() {

    if (newEvent.hasReminder()) {
      newEvent.setReminder(null);

      showSelectedReminder();
    }
  }

  private void selectTimeLapseReset() {

    selectPeriodHelper.showSelectTimeLapseResetDialog(this, newEvent,
        (period, timeUnit) -> {
          newEvent.setTimeLapse(period);
          newEvent.setTimeLapseUnit(timeUnit);

          showSelectedTimeLapseReset();
        });
  }

  private void discardEvent() {

    EventBus.getDefault().post(new ShowMessageEvent(getString(R.string.msg_event_discarded)));

    NavUtils.navigateUpFromSameTask(this);
  }

  private void saveEvent() {

    nameLayout.setErrorEnabled(false);

    String name = nameEdit.getText().toString();
    String description = descriptionEdit.getText().toString();
    Date date = selectedDate != null ? selectedDate.toDate() : null;
    String[] tags = selectTagsHelper.getMapTags().getKeySelection(TagViewModel::getId)
        .toArray(new String[0]);

    newEvent.setName(name);
    newEvent.setDescription(description);
    newEvent.setDate(date);
    newEvent.setTags(tags);

    presenter.createEvent(newEvent);
  }

}
