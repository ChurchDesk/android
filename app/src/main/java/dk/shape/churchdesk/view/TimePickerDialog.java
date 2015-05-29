package dk.shape.churchdesk.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.Calendar;

import butterknife.ButterKnife;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.ButtonSwitch;
import hirondelle.date4j.DateTime;

/**
 * Created by Martin on 21/05/2015.
 */
public class TimePickerDialog extends DialogFragment{

    public ButtonSwitch mButtonSwitch;

    //@InjectView(R.id.dialog_time_hourpicker)
    public TimePicker mHourPicker;

    //@InjectView(R.id.dialog_time_date)
    public RelativeLayout mCalendarView;

    TimePicker.OnTimeChangedListener mTimeChangedListener;

    Boolean allDay;
    Calendar calendar = Calendar.getInstance();
    CaldroidListener mCaldroidListener;
    public CaldroidFragment caldroidFragment;

    public TimePickerDialog() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.dialog_time_picker, container);
        ButterKnife.inject(this, mView);
        Bundle b = this.getArguments();
        allDay = b.getBoolean("allDay");
        calendar.setTimeInMillis(b.getLong("date"));

        mButtonSwitch = (ButtonSwitch)mView.findViewById(R.id.dialog_time_buttons);
        mHourPicker = (TimePicker)mView.findViewById(R.id.dialog_time_hourpicker);
        mCalendarView = (RelativeLayout)mView.findViewById(R.id.dialog_time_date);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        mButtonSwitch.init(getActivity(), allDay ? 1 : 2,
                new ButtonSwitch.OnButtonSwitchClickListener() {
                    @Override
                    public void onClick(int position) {
                        if (position == 0) {
                            mCalendarView.setVisibility(View.VISIBLE);
                            mHourPicker.setVisibility(View.GONE);
                            System.out.println("Clicked on button " + position);

                        } else if (position == 1) {
                            mCalendarView.setVisibility(View.GONE);
                            mHourPicker.setVisibility(View.VISIBLE);
                            System.out.println("Clicked on button " + position);

                        }
                        System.out.println("Clicked on button " + position);
                    }
                }, "Date", "Time");

        mButtonSwitch.setSelected(0);
        mHourPicker.setIs24HourView(true);
        mHourPicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        mHourPicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        mHourPicker.setOnTimeChangedListener(mTimeChangedListener);


        //set calendar
        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        final Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, true);
        caldroidFragment.selectedBackgroundDrawable = R.drawable.calendar_background_selected;
        caldroidFragment.setArguments(args);
        if(calendar.getTimeInMillis() > System.currentTimeMillis()) {
            caldroidFragment.selectDate(calendar);
        }

        caldroidFragment.setCaldroidListener(mCaldroidListener);

        FragmentTransaction t = getChildFragmentManager().beginTransaction();
        t.add(R.id.dialog_time_date, caldroidFragment);
        t.commit();


        return mView;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mTimeChangedListener.onTimeChanged(mHourPicker, mHourPicker.getCurrentHour(), mHourPicker.getCurrentMinute());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public DateTime convertDateToDateTime(Calendar date) {
        int year = date.get(Calendar.YEAR);
        int javaMonth = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DATE);

        // javaMonth start at 0. Need to plus 1 to get datetimeMonth
        return new DateTime(year, javaMonth + 1, day, 0, 0, 0, 0);
    }

    public void setOnSelectDateListener(CaldroidListener listener){
        mCaldroidListener = listener;
    }

    public void setOnTimeChangedListener(TimePicker.OnTimeChangedListener listener){
        mTimeChangedListener = listener;
    }


}
