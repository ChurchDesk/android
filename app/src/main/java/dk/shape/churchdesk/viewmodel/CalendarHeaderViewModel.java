package dk.shape.churchdesk.viewmodel;

import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import dk.shape.churchdesk.entity.Holyday;
import dk.shape.churchdesk.view.CalendarHeaderView;
import dk.shape.library.collections.Categorizable;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by root on 6/3/15.
 */
public class CalendarHeaderViewModel extends ViewModel<CalendarHeaderView> implements Categorizable {

    private Calendar mHeaderDate;
    private SimpleDateFormat mTextFormatter = new SimpleDateFormat("EEE'.'", Locale.getDefault());
    private SimpleDateFormat mNumFormatter = new SimpleDateFormat(" dd'.' MMMM", Locale.getDefault());
    private Holyday mHolyDay;

    public CalendarHeaderViewModel(Long dateTime) {
        this.mHeaderDate = Calendar.getInstance();
        this.mHeaderDate.setTimeInMillis(dateTime);
    }

    public CalendarHeaderViewModel(Holyday holyday) {
        this.mHolyDay = holyday;
        this.mHeaderDate = Calendar.getInstance();
        this.mHeaderDate.setTimeInMillis(mHolyDay.getId());
    }

    @Override
    public void bind(CalendarHeaderView calendarHeaderView) {
        calendarHeaderView.mEventDayText.setText(mTextFormatter.format(mHeaderDate.getTime()));
        calendarHeaderView.mEventDayNum.setText(mNumFormatter.format(mHeaderDate.getTime()));

        if (mHolyDay != null) {
            calendarHeaderView.mHolyDay.setVisibility(View.VISIBLE);
            calendarHeaderView.mHolyDay.setText(mHolyDay.mName);
        }
        else {
            calendarHeaderView.mHolyDay.setVisibility(View.GONE);
        }
    }

    public void extBind(Holyday holyday) {
        mHolyDay = holyday;
    }

    @Override
    public long getCategoryId() {
        return mHeaderDate.getTimeInMillis();
    }

    public boolean equals(long o) {
        return o == mHeaderDate.getTimeInMillis();
    }

    public long getId() {
        return mHeaderDate.getTimeInMillis();
    }
}
