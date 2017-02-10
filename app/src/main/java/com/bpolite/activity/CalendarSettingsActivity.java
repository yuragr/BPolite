package com.bpolite.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bpolite.R;
import com.bpolite.data.comparator.WeekDayComparator;
import com.bpolite.data.enums.CalendarStatus;
import com.bpolite.data.enums.RingerRestoreDelay;
import com.bpolite.data.enums.WeekDay;
import com.bpolite.data.pojo.Calendar;
import com.bpolite.data.pojo.EventInstance;
import com.bpolite.data.repo.CalendarRepository;
import com.bpolite.data.repo.EventInstanceRepository;
import com.bpolite.utils.CalendarCheckSchedulerUtils;
import com.bpolite.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class CalendarSettingsActivity extends Activity {
	private final CharSequence[] calendarStatuses = new CharSequence[] { CalendarStatus.VIBRATE.getValue(),
			CalendarStatus.MUTED.getValue(), CalendarStatus.NONE.getValue() };
	private Calendar calendar = null;
	private Calendar origCalendar = null;
	private ArrayList<WeekDay> weekDays = null;
	private AlertDialog calendarStatusDialog = null;
	private AlertDialog weekDaySelectionDialog = null;
	private AlertDialog delayRingerRestoreDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        Log.d(this.getClass().getSimpleName(), ">> onCreate");
        super.onCreate(savedInstanceState);

		int calendarHashCode = getIntent().getIntExtra("calendarHashCode", -1);
		calendar = CalendarRepository.getCalendarByHashCode(this, calendarHashCode);
		origCalendar = new Calendar(calendar);

		setContentView(R.layout.calendar_settings);
		setTitle(calendar.getDisplayName() + " (" + calendar.getAccountName() + ")");
		updateCalendarStatusView();
		updateWeekDaysView();
		updateRingerRestoreDelayView();
        Log.d(this.getClass().getSimpleName(), "<< onCreate");
	}

	public void onCalendarStatusSelection(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select Profile"); // localize
		builder.setItems(calendarStatuses, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				calendar.setStatus(CalendarStatus.getAsEnum(calendarStatuses[which].toString()));
				if (calendarStatuses[which].equals(CalendarStatus.NONE.getValue())) {
					calendar.setWeekDays(null);
					calendar.setRingerRestoreDelay(null);
				}
				updateCalendarStatusView();
				updateWeekDaysView();
				updateRingerRestoreDelayView();
			}
		});
		calendarStatusDialog = builder.create();
		calendarStatusDialog.show();
	}

	public void onWeekDaysSelection(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Select Days");
		createWeekDaysList();
		builder.setMultiChoiceItems(getAllWeekDays(), getSelectedWeekDays(),
				new DialogInterface.OnMultiChoiceClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						if (isChecked)
							calendar.addWeekDay(weekDays.get(which));
						else
							calendar.addRemoveDay(weekDays.get(which));
						CalendarRepository.saveCalendar(CalendarSettingsActivity.this, calendar);
						updateWeekDaysView();
					}
				});
		weekDaySelectionDialog = builder.create();
		weekDaySelectionDialog.show();
	}

	private boolean[] getSelectedWeekDays() {
		boolean[] selectedWeekDays = new boolean[weekDays.size()];
		HashSet<WeekDay> calendarWeekDays = new HashSet<>(calendar.getWeekDays());

		for (int i = 0; i < weekDays.size(); i++) {
			selectedWeekDays[i] = calendarWeekDays.contains(weekDays.get(i));
		}

		return selectedWeekDays;
	}

	private CharSequence[] getAllWeekDays() {
		CharSequence[] allWeekDays = new CharSequence[weekDays.size()];
		for (int i = 0; i < weekDays.size(); i++) {
			allWeekDays[i] = weekDays.get(i).getName();
		}
		return allWeekDays;
	}

	private void createWeekDaysList() {
		if (weekDays == null) {
			weekDays = new ArrayList<>(Arrays.asList(WeekDay.values()));
			Collections.sort(weekDays, new WeekDayComparator());
		}
	}

	private void updateCalendarStatusView() {
		TextView calendarStatusView = (TextView) findViewById(R.id.calendarStatus);

		if (calendar.getStatus().equals(CalendarStatus.VIBRATE)
				|| calendar.getStatus().equals(CalendarStatus.MUTED)) {
			calendarStatusView.setTextColor(0xFF669900);
			calendarStatusView.setTypeface(null, Typeface.BOLD);
		} else {
			calendarStatusView.setTextColor(Color.BLACK);
			calendarStatusView.setTypeface(null, Typeface.NORMAL);
		}

		calendarStatusView.setText(calendar.getStatus().getValue());
	}

	private void updateWeekDaysView() {
		TextView weekDaysView = (TextView) findViewById(R.id.weekDays);

		String weekDaysString = StringUtils.convertListToCommaSeparatedStringForDisplay(calendar
				.getWeekDays());

		weekDaysView.setText(weekDaysString);
		if (calendar.getStatus().equals(CalendarStatus.VIBRATE)
				|| calendar.getStatus().equals(CalendarStatus.MUTED)) {
			weekDaysView.setTextColor(0xFF669900);
			weekDaysView.setTypeface(null, Typeface.BOLD);
		} else {
			weekDaysView.setTextColor(Color.BLACK);
			weekDaysView.setTypeface(null, Typeface.NORMAL);
		}
	}

	private void updateRingerRestoreDelayView() {
		TextView ringerRestoreDelayView = (TextView) findViewById(R.id.ringerRestoreDelay);

		ringerRestoreDelayView.setText(calendar.getRingerRestoreDelay().getName());
		if (calendar.getStatus().equals(CalendarStatus.VIBRATE)
				|| calendar.getStatus().equals(CalendarStatus.MUTED)) {
			ringerRestoreDelayView.setTextColor(0xFF669900);
			ringerRestoreDelayView.setTypeface(null, Typeface.BOLD);
		} else {
			ringerRestoreDelayView.setTextColor(Color.BLACK);
			ringerRestoreDelayView.setTypeface(null, Typeface.NORMAL);
		}
	}

	public void onDelayRingerRestoreSelection(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Ringer Restore Delay");

		builder.setItems(RingerRestoreDelay.getNames(), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				calendar.setRingerRestoreDelay(RingerRestoreDelay.values()[which]);
				CalendarRepository.saveCalendar(CalendarSettingsActivity.this, calendar);

				updateRingerRestoreDelayView();
			}
		});

		delayRingerRestoreDialog = builder.create();
		delayRingerRestoreDialog.show();

	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(this.getClass().getSimpleName(), "CalendarSettingsActivity: onPause");
		if (calendarStatusDialog != null && calendarStatusDialog.isShowing())
			calendarStatusDialog.dismiss();
		if (weekDaySelectionDialog != null && weekDaySelectionDialog.isShowing())
			weekDaySelectionDialog.dismiss();
		if (delayRingerRestoreDialog != null && delayRingerRestoreDialog.isShowing())
			delayRingerRestoreDialog.dismiss();

		if (isCalendarChanged()) {

			// cancel all the event instances for this calendar
			HashSet<EventInstance> eventInstances = EventInstanceRepository.getDeviceEventInstances(
					CalendarSettingsActivity.this, calendar);
			for (EventInstance eventInstance : eventInstances) {
				eventInstance.cancel(CalendarSettingsActivity.this);
			}
			EventInstanceRepository.deleteEventInstances(CalendarSettingsActivity.this, eventInstances);
			CalendarRepository.saveCalendar(CalendarSettingsActivity.this, calendar);
			CalendarCheckSchedulerUtils.scheduleCalendarCheck(this);
			origCalendar = new Calendar(calendar);
		}
	}

	private boolean isCalendarChanged() {

		// we don't check if the week days have changed because we check the week days when the event happens
		return calendar.getStatus() != origCalendar.getStatus()
				|| calendar.getRingerRestoreDelay() != origCalendar.getRingerRestoreDelay()
				|| !calendar.getWeekDays().equals(origCalendar.getWeekDays());
	}
}
