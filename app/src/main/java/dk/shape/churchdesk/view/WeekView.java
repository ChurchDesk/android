package dk.shape.churchdesk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import butterknife.InjectView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomTextView;

/**
 * Created by steffenkarlsson on 08/04/15.
 */
public class WeekView extends BaseFrameLayout {

    @InjectView(R.id.monWrapper)
    protected LinearLayout mMonWrapper;

    @InjectView(R.id.mon_weekday_number)
    protected CustomTextView mMonNumber;

    @InjectView(R.id.mon_weekday_event_mark)
    protected View mMonMark;

    @InjectView(R.id.tueWrapper)
    protected LinearLayout mTueWrapper;

    @InjectView(R.id.tue_weekday_number)
    protected CustomTextView mTueNumber;

    @InjectView(R.id.tue_weekday_event_mark)
    protected View mTueMark;

    @InjectView(R.id.wedWrapper)
    protected LinearLayout mWedWrapper;

    @InjectView(R.id.wed_weekday_number)
    protected CustomTextView mWedNumber;

    @InjectView(R.id.wed_weekday_event_mark)
    protected View mWedMark;

    @InjectView(R.id.thuWrapper)
    protected LinearLayout mThuWrapper;

    @InjectView(R.id.thu_weekday_number)
    protected CustomTextView mThuNumber;

    @InjectView(R.id.thu_weekday_event_mark)
    protected View mThuMark;

    @InjectView(R.id.friWrapper)
    protected LinearLayout mFriWrapper;

    @InjectView(R.id.fri_weekday_number)
    protected CustomTextView mFriNumber;

    @InjectView(R.id.fri_weekday_event_mark)
    protected View mFriMark;

    @InjectView(R.id.satWrapper)
    protected LinearLayout mSatWrapper;

    @InjectView(R.id.sat_weekday_number)
    protected CustomTextView mSatNumber;

    @InjectView(R.id.sat_weekday_event_mark)
    protected View mSatMark;

    @InjectView(R.id.sunWrapper)
    protected LinearLayout mSunWrapper;

    @InjectView(R.id.sun_weekday_number)
    protected CustomTextView mSunNumber;

    @InjectView(R.id.sun_weekday_event_mark)
    protected View mSunMark;

    public ArrayList<CustomTextView> mDayNums = new ArrayList<CustomTextView>() {{
        add(mMonNumber);
        add(mTueNumber);
        add(mWedNumber);
        add(mThuNumber);
        add(mFriNumber);
        add(mSatNumber);
        add(mSunNumber);
    }};

    public ArrayList<LinearLayout> mDayWrappers = new ArrayList<LinearLayout>() {{
        add(mMonWrapper);
        add(mTueWrapper);
        add(mWedWrapper);
        add(mThuWrapper);
        add(mFriWrapper);
        add(mSatWrapper);
        add(mSunWrapper);
    }};

    public ArrayList<View> mDayMarks = new ArrayList<View>() {{
        add(mMonMark);
        add(mTueMark);
        add(mWedMark);
        add(mThuMark);
        add(mFriMark);
        add(mSatMark);
        add(mSunMark);
    }};

    public WeekView(Context context) {
        super(context);
    }

    public WeekView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.week_view;
    }

    public void setSelected(int day) {
        for (int i = 0; i < mDayWrappers.size(); i++) {
            mDayWrappers.get(i).setSelected(i == day);
        }
    }

    public void setHasEvents(boolean hasEvents, int day) {
        View markView = null;
        switch (day) {
            case 0:
                markView = mMonMark;
                break;
            case 1:
                markView = mTueMark;
                break;
            case 2:
                markView = mWedMark;
                break;
            case 3:
                markView = mThuMark;
                break;
            case 4:
                markView = mFriMark;
                break;
            case 5:
                markView = mSatMark;
                break;
            case 6:
                markView = mSunMark;
                break;
        }

        if (markView != null) {
            markView.setVisibility(hasEvents ? VISIBLE : INVISIBLE);
        }
    }
}
