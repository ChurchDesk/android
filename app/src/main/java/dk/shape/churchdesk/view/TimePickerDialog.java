package dk.shape.churchdesk.view;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.fragment.CalendarDialogFragment;
import dk.shape.churchdesk.widget.ButtonSwitch;

/**
 * Created by Martin on 21/05/2015.
 */
public class TimePickerDialog extends Dialog{

    @InjectView(R.id.dialog_time_buttons)
    public ButtonSwitch mButtonSwitch;

    @InjectView(R.id.dialog_time_hourpicker)
    public TimePicker mHourPicker;

    @InjectView(R.id.dialog_time_date)
    public RelativeLayout mCalendarView;


    public TimePickerDialog(Context context, @StringRes int titleRes) {
        super(context);
        setContentView(R.layout.dialog_time_picker);
        setTitle(titleRes);
        ButterKnife.inject(this);

        /*
        android.support.v4.app.FragmentTransaction ft = ((ActionBarActivity)context).getSupportFragmentManager().beginTransaction();
        Fragment fragment = new CalendarDialogFragment();
        ft.replace(R.id.dialog_time_date, fragment);
        ft.commit();
        */

    }


    @Override
    protected void onStart() {
        super.onStart();
        mButtonSwitch.setSelected(0);
        mHourPicker.setIs24HourView(true);

    }
}
