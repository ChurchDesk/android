package dk.shape.churchdesk.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;

import butterknife.InjectView;

import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomTextView;
import dk.shape.churchdesk.widget.InfiniteViewPager;

/**
 * Created by steffenkarlsson on 07/04/15.
 */
public class CalendarView extends BaseFrameLayout {

    @InjectView(R.id.calendar_data)
    public RecyclerView mDataList;

    @InjectView(R.id.today)
    public FloatingActionButton mTodayButton;

    @InjectView(R.id.today_text)
    public CustomTextView mTodayText;

    @InjectView(R.id.today_wrapper)
    public RelativeLayout mTodayWrapper;

    @InjectView(R.id.dimming_layer)
    public View mDimmingLayer;

    @InjectView(R.id.calendar_view)
    public FrameLayout mCalendarView;

    @InjectView(R.id.week_pager)
    public InfiniteViewPager mWeekPager;

    @InjectView(R.id.container)
    public RelativeLayout mContainer;

    public CalendarView(Context context) {
        super(context);

        //mDataList.setLayoutManager(new LinearLayoutManager(getContext()));

//        RotateDrawable drawable = (RotateDrawable) getResources().getDrawable(R.drawable.calendar_today);
//        mTodayButton.setIconDrawable(BaseFloatingButtonFragment.resize(
//                getContext(), drawable.getDrawable()));
    }

    public void setTodayOnClickListener(OnClickListener onClickListener) {
        mTodayButton.setOnClickListener(onClickListener);
        mTodayText.setOnClickListener(onClickListener);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_calendar;
    }
}
