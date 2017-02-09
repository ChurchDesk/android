package dk.shape.churchdesk.viewmodel;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.Nullable;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import dk.shape.churchdesk.util.DateAppearanceUtils;
import dk.shape.churchdesk.view.WeekView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 08/04/15.
 */
public class WeekViewModel extends ViewModel<WeekView> {

    public interface OnDateClick {
        void onDateClick(Calendar calendar);
    }

    private int mWeekNum;
    private int mYear;
    private ArrayList<Boolean> mHasEventsArray = null;
    private int mSelectedPosition = 0;

    private SimpleDateFormat formatter = new SimpleDateFormat("dd", Locale.getDefault());

    private final OnDateClick listener;

    public WeekViewModel(OnDateClick listener) {
        this.listener = listener;
    }

    public void bind(WeekView weekView, int selectedPosition) {
        mSelectedPosition = selectedPosition;
        bind(weekView);
    }


    @Override
    public void bind(final WeekView weekView) {
        weekView.setSelected(mSelectedPosition);

        final Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.WEEK_OF_YEAR, mWeekNum);
        calendar.set(Calendar.DAY_OF_WEEK, 2);
        calendar.set(Calendar.YEAR, mYear);

        for (int i = 0; i < weekView.mDayNums.size(); i++) {
            final Calendar cal = Calendar.getInstance();
            cal.clear();
            cal.setTime(calendar.getTime());

            weekView.mDayNums.get(i).setText(
                    String.valueOf(Integer.valueOf(formatter.format(calendar.getTime()))));

            final int dayIdx = i;
            weekView.mDayWrappers.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    weekView.setSelected(dayIdx);
                    listener.onDateClick(cal);
                }
            });

            if (mHasEventsArray != null)
                weekView.setHasEvents(mHasEventsArray.get(i), i);

            calendar.add(Calendar.DATE, 1);
        }
    }

    public void setData(int weekNum, int year) {
        this.mWeekNum = weekNum;
        this.mYear = year;
    }

    public void updateWithEventIndicators(@Nullable ArrayList<Boolean> hasEventsArray) {
        this.mHasEventsArray = hasEventsArray;
    }
}
