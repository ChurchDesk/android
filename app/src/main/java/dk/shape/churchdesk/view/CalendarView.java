package dk.shape.churchdesk.view;

import android.content.Context;
import android.graphics.drawable.RotateDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;

import butterknife.InjectView;
import dk.shape.churchdesk.BaseFloatingButtonFragment;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.InfiniteViewPager;

/**
 * Created by steffenkarlsson on 07/04/15.
 */
public class CalendarView extends BaseFrameLayout {

    @InjectView(R.id.calendar_data)
    public RecyclerView mDataList;

    @InjectView(R.id.today)
    public FloatingActionButton mTodayButton;

    @InjectView(R.id.dimming_layer)
    public View mDimmingLayer;

    @InjectView(R.id.calendar_view)
    public FrameLayout mCalendarView;

    @InjectView(R.id.week_pager)
    public InfiniteViewPager mWeekPager;

    public CalendarView(Context context) {
        super(context);

        mDataList.setLayoutManager(new LinearLayoutManager(getContext()));

//        RotateDrawable drawable = (RotateDrawable) getResources().getDrawable(R.drawable.calendar_today);
//        mTodayButton.setIconDrawable(BaseFloatingButtonFragment.resize(
//                getContext(), drawable.getDrawable()));
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_calendar;
    }
}
