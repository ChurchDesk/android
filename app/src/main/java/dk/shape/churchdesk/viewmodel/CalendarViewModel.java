package dk.shape.churchdesk.viewmodel;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.antonyt.infiniteviewpager.InfinitePagerAdapter;
import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import dk.shape.churchdesk.BaseActivity;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Event;
import dk.shape.churchdesk.view.CalendarView;
import dk.shape.churchdesk.view.WeekView;
import dk.shape.library.collections.adapters.RecyclerAdapter;
import dk.shape.library.collections.adapters.StickyHeaderRecyclerAdapter;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 07/04/15.
 */
public class CalendarViewModel extends ViewModel<CalendarView> {

    public enum DataType {
        BEGINNING, MIDDLE, FUTURE
    }

    private final BaseActivity mParent;
    private final CaldroidFragment.OnMonthChangedListener mMonthChangedListener;
    private final CaldroidListener mCaldroidListener;
    private final CaldroidFragment mCaldroidFragment = new CaldroidFragment();
    private final Calendar mNow;
    private final WeekViewModel.OnDateClick mOnDateClickListener;

    private CalendarView mCalendarView;
    private WeekPagerAdapter mWeekAdapter;
    private RecyclerAdapter mAdapter;
    private StickyHeadersItemDecoration mHeaderDecoration;

    public CalendarViewModel(BaseActivity parent, CaldroidFragment.OnMonthChangedListener onMonthChangedListener,
                             WeekViewModel.OnDateClick onDateClickListener,
                             CaldroidListener caldroidListener, Calendar now) {
        this.mParent = parent;
        this.mMonthChangedListener = onMonthChangedListener;
        this.mOnDateClickListener = onDateClickListener;
        this.mCaldroidListener = caldroidListener;
        this.mNow = now;
    }

    @Override
    public void bind(final CalendarView calendarView) {
        this.mCalendarView = calendarView;

        CaldroidFragment.selectedBackgroundDrawable = R.drawable.calendar_background_selected_fill;
        Bundle args = new Bundle();
        args.putInt(CaldroidFragment.MONTH, mNow.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, mNow.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, true);

        mCaldroidFragment.setArguments(args);
        mCaldroidFragment.selectDate(mNow);
        mCaldroidFragment.setMonthChangedListener(mMonthChangedListener);
        mCaldroidFragment.setCaldroidListener(mCaldroidListener);

        FragmentTransaction t = mParent.getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar_view, mCaldroidFragment);
        t.commit();

        mWeekAdapter = new WeekPagerAdapter();
        calendarView.mWeekPager.setAdapter(new InfinitePagerAdapter(mWeekAdapter));
        ArrayList<Calendar> data = getCalendars(mNow, 0);
        mWeekAdapter.setData(data);

        calendarView.mWeekPager.setCurrentItem(0);
        calendarView.mWeekPager.setOffscreenPageLimit(2);

        calendarView.mWeekPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                int relativePosition = i - calendarView.mWeekPager.getOffsetAmount();

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(mNow.getTime());
                calendar.add(Calendar.WEEK_OF_YEAR, relativePosition);

                if (mod(relativePosition, 2) == 0) {
                    mWeekAdapter.setData(getCalendars(calendar,
                            calendarView.mWeekPager.getCurrentItem()));
                    mWeekAdapter.notifyNewData();
                }
                Toast.makeText(mParent, mParent.getString(R.string.week,
                        calendar.get(Calendar.WEEK_OF_YEAR)), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    public void setContent(List<Event> events, DataType type) {
        if (mAdapter == null) {
            mAdapter = new RecyclerAdapter(mParent);
            mAdapter.setHasStableIds(true);

            mHeaderDecoration = new StickyHeadersBuilder()
                    .setAdapter(mAdapter)
                    .setRecyclerView(mCalendarView.mDataList)
                    .setStickyHeadersAdapter(new StickyHeaderRecyclerAdapter(mAdapter))
                    .build();



            mCalendarView.mDataList.addItemDecoration(mHeaderDecoration);
            mCalendarView.mDataList.setAdapter(mAdapter);
        }
        switch (type) {
            case BEGINNING:

                break;
            case FUTURE:

                break;
        }
    }

    private ArrayList<Calendar> getCalendars(Calendar middle, int position) {
        ArrayList<Calendar> calendars = new ArrayList<Calendar>() {{
            add(null);
            add(null);
            add(null);
            add(null);
            add(null);
        }};
        calendars.remove(position);
        calendars.add(position, middle);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(middle.getTime());
        calendar.add(Calendar.WEEK_OF_YEAR, 1);

        int newPosition = mod(position + 1, 5);
        calendars.remove(newPosition);
        calendars.add(newPosition, calendar);

        calendar = Calendar.getInstance();
        calendar.setTime(middle.getTime());
        calendar.add(Calendar.WEEK_OF_YEAR, 2);

        newPosition = mod(position + 2, 5);
        calendars.remove(newPosition);
        calendars.add(newPosition, calendar);

        calendar = Calendar.getInstance();
        calendar.setTime(middle.getTime());
        calendar.add(Calendar.WEEK_OF_YEAR, -2);

        newPosition = mod(position - 2, 5);
        calendars.remove(newPosition);
        calendars.add(newPosition, calendar);

        calendar = Calendar.getInstance();
        calendar.setTime(middle.getTime());
        calendar.add(Calendar.WEEK_OF_YEAR, -1);

        newPosition = mod(position - 1, 5);
        calendars.remove(newPosition);
        calendars.add(newPosition, calendar);
        return calendars;
    }

    private int mod(int x, int y) {
        int result = x % y;
        return result < 0 ? result + y : result;
    }

    public void selectDate(Calendar calendar) {
        mCaldroidFragment.selectDate(calendar);

//        mWeekAdapter.setData(getCalendars(calendar, 0));
//        mCalendarView.mWeekPager.setCurrentItem(0);
//        mWeekAdapter.notifyNewData();
    }

    public void deselectDate(Calendar calendar) {
        mCaldroidFragment.deselectDate(calendar);
    }

    private class WeekPagerAdapter extends PagerAdapter {

        private HashMap<Integer, Pair<WeekView, WeekViewModel>> mViews = new HashMap<>();

        public void setData(ArrayList<Calendar> data) {
            this.mData = data;
        }

        private ArrayList<Calendar> mData = new ArrayList<>();

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position > 5)
                return new View(container.getContext());

            WeekView view = new WeekView(container.getContext());
            WeekViewModel viewModel = new WeekViewModel(mOnDateClickListener);

            Calendar calendar = mData.get(position);
            viewModel.setData(calendar.get(Calendar.WEEK_OF_YEAR),
                    calendar.get(Calendar.YEAR));
            viewModel.bind(view);

            mViews.put(position, new Pair<>(view, viewModel));

            container.addView(view);
            return view;
        }

        public void notifyNewData() {
            for (int i = 0; i < getCount(); i++) {
                Pair<WeekView, WeekViewModel> viewModelPair = mViews.get(i);
                WeekViewModel viewModel = viewModelPair.second;

                Calendar calendar = mData.get(i);
                viewModel.setData(calendar.get(Calendar.WEEK_OF_YEAR),
                        calendar.get(Calendar.YEAR));
                viewModel.bind(viewModelPair.first);
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
