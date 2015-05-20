package dk.shape.churchdesk.viewmodel;

import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
        weekView.setSelected(0);

        final Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.WEEK_OF_YEAR, mWeekNum);
        calendar.set(Calendar.YEAR, mYear);

        for (int i = 0; i < weekView.mDayNums.size(); i++) {
            calendar.add(Calendar.DATE, 1);
            weekView.mDayNums.get(i).setText(
                    String.valueOf(Integer.valueOf(formatter.format(calendar.getTime()))));

            final int dayIdx = i;
            weekView.mDayWrappers.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    weekView.setSelected(dayIdx);
                    listener.onDateClick(calendar);
                }
            });
        }
    }

    public void setData(int weekNum, int year) {
        this.mWeekNum = weekNum;
        this.mYear = year;
    }
}
