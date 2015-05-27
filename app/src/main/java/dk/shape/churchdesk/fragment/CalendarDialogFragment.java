package dk.shape.churchdesk.fragment;





import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.Calendar;

import butterknife.ButterKnife;
import dk.shape.churchdesk.R;
import hirondelle.date4j.DateTime;

/**
 * Created by Martin on 27/05/2015.
 */
public class CalendarDialogFragment extends Fragment {


    private CaldroidFragment caldroidFragment;
    private static View mView;

    public CalendarDialogFragment(){
    }


    @Override
    public void onResume() {
        super.onResume();

        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        final Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        caldroidFragment.selectedBackgroundDrawable = R.drawable.calendar_background_selected;
        caldroidFragment.setArguments(args);

        caldroidFragment.setCaldroidListener(new CaldroidListener() {
            @Override
            public void onSelectDate(Calendar date, View view) {
                if(caldroidFragment.isDateSelected(convertDateToDateTime(date))){
                    caldroidFragment.deselectDate(date);
                } else {
                    Calendar now = Calendar.getInstance();
                    if (now.before(date)) {
                        caldroidFragment.clearSelectedDates();
                        caldroidFragment.selectDate(date);
                    }
                }
            }
        });

        FragmentTransaction t = getChildFragmentManager().beginTransaction();
        t.replace(R.id.dialog_time_calendar_layout, caldroidFragment);
        t.commit();

    }

    public static DateTime convertDateToDateTime(Calendar date) {
        int year = date.get(Calendar.YEAR);
        int javaMonth = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DATE);

        // javaMonth start at 0. Need to plus 1 to get datetimeMonth
        return new DateTime(year, javaMonth + 1, day, 0, 0, 0, 0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView != null) {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null)
                parent.removeView(mView);
        }
        try {
            mView = inflater.inflate(R.layout.fragment_calendar_dialog, container, false);
            ButterKnife.inject(this, mView);
        } catch (InflateException e) {
            System.out.println(e);
        }
        return mView;
    }
}
