package dk.shape.churchdesk.viewmodel;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import dk.shape.churchdesk.BaseActivity;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Holyday;
import dk.shape.churchdesk.util.DateAppearanceUtils;
import dk.shape.churchdesk.util.OnStateScrollListener;
import dk.shape.churchdesk.view.CalendarView;
import dk.shape.churchdesk.view.WeekView;
import dk.shape.library.collections.adapters.RecyclerAdapter;
import dk.shape.library.collections.adapters.StickyHeaderRecyclerAdapter;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 07/04/15.
 */
public class CalendarViewModel extends ViewModel<CalendarView> {

    public interface OnChangeTitle {
        void changeTitle(Date date);
    }

    public interface OnLoadMoreData {
        void onLoadFuture(Calendar toLoad);
        void onLoadPast(Calendar toLoad);
        void onLoadHolyYear(int year);
    }

    public interface OnCalendarDateSelectedListener {
        void onDateSelected(Calendar calendar);
    }

    public enum DataType {
        BEGINNING, MIDDLE, FUTURE
    }

    private final BaseActivity mParent;
    private final CaldroidFragment.OnMonthChangedListener mMonthChangedListener;
    private final OnCalendarDateSelectedListener mOnCalendarDateSelectedListener;
    private final CaldroidFragment mCaldroidFragment = new CaldroidFragment();
    private final Calendar mNow;
    private final OnChangeTitle mOnChangeTitle;
    private final OnLoadMoreData mOnLoadMoreData;

    private Calendar mSelectedDate;
    private CalendarView mCalendarView;
    private WeekPagerAdapter mWeekAdapter;
    private RecyclerAdapter<EventItemViewModel> mAdapter;
    private LinearLayoutManager mManager;
    private StickyHeaderRecyclerAdapter mStickyAdapter;
    private SortedMap<Long, CalendarHeaderViewModel> mHeaderMap = new TreeMap<>();

    private int mStartPosition;
    private int mEndPosition = 0;
    private boolean isLoading = false;

    private boolean isLoadingHoly = false;
    private Calendar mNextHolyYear;
    private Calendar mPrevHolyYear;

    public CalendarViewModel(BaseActivity parent, CaldroidFragment.OnMonthChangedListener onMonthChangedListener,
                             CalendarViewModel.OnCalendarDateSelectedListener onCalendarDateSelectedListener, OnChangeTitle onChangeTitle,
                             OnLoadMoreData onLoadMoreData) {
        this.mParent = parent;
        this.mMonthChangedListener = onMonthChangedListener;
        this.mOnCalendarDateSelectedListener = onCalendarDateSelectedListener;
        this.mNow = DateAppearanceUtils.reset(Calendar.getInstance());
        this.mNextHolyYear = DateAppearanceUtils.resetHard(Calendar.getInstance());
        this.mPrevHolyYear = DateAppearanceUtils.resetHard(Calendar.getInstance());
        this.mSelectedDate = mNow;
        this.mOnChangeTitle = onChangeTitle;
        this.mOnLoadMoreData = onLoadMoreData;
    }

    private WeekViewModel.OnDateClick mOnDateClickListener = new WeekViewModel.OnDateClick() {
        @Override
        public void onDateClick(Calendar calendar) {
            boolean updateCaldroid = true;
            boolean hideCaldroid = false;
            boolean scrollListToDate = true;
            boolean selectWeekAndDay = false;
            updateCurrentDateAndCalenderView(calendar, updateCaldroid, hideCaldroid, scrollListToDate, selectWeekAndDay);
        }
    };

    private CaldroidListener mCaldroidListener = new CaldroidListener() {
        @Override
        public void onSelectDate(Calendar calendar, View view) {
            Date theDate = calendar.getTime();
            String dateString = theDate.toString();
            String log = String.format("Calendar -> onSelectDate: %s", dateString);
            Log.d("INFO", log);

            boolean updateCaldroid = true;
            boolean hideCaldroid = true;
            boolean scrollListToDate = true;
            boolean selectWeekAndDay = true;
            updateCurrentDateAndCalenderView(calendar, updateCaldroid, hideCaldroid, scrollListToDate, selectWeekAndDay);
        }
    };

    public void updateCaldroidToCurrentDate() {
        mCaldroidFragment.clearSelectedDates();
        mCaldroidFragment.selectDate(mSelectedDate);
        mCaldroidFragment.moveToDate(mSelectedDate);
    }

    public void updateCurrentDateAndCalenderView(Calendar newCurrentDate, boolean updateCaldroid, boolean hideCaldroid, boolean scrollListToDate, boolean selectWeekAndDay) {
        Log.d("TAG", "updateCurrentDateAndCalenderView");
        String oldDateString = String.format("old date: %s", mSelectedDate.getTime().toString());
        Log.d("TAG", oldDateString);

        mSelectedDate = newCurrentDate;

        String newDateString = String.format("new date: %s", mSelectedDate.getTime().toString());
        Log.d("TAG", newDateString);

        if (updateCaldroid) {
            mCaldroidFragment.clearSelectedDates();
            mCaldroidFragment.selectDate(mSelectedDate);
            if (!mCaldroidFragment.isHidden()) {
                mCaldroidFragment.moveToDate(mSelectedDate);
            }
        }
        Date newCurrentDateAsDate = newCurrentDate.getTime();
        mOnChangeTitle.changeTitle(newCurrentDateAsDate);

        if (hideCaldroid) {
            mOnCalendarDateSelectedListener.onDateSelected(mSelectedDate);
        }
        if (scrollListToDate) {
            scrollToEventWithDate(mSelectedDate);
        }
        if (selectWeekAndDay) {
            Date date = mSelectedDate.getTime();
            mWeekAdapter.selectWeekAndDay(date);
        }
    }

    @Override
    public void bind(final CalendarView calendarView) {
        this.mCalendarView = calendarView;

//        CaldroidFragment.selectedBackgroundDrawable = R.drawable.calendar_background_selected_fill;
        CaldroidFragment.selectedBackgroundDrawable = R.drawable.calendar_background_selected;
        Bundle args = new Bundle();
        args.putInt(CaldroidFragment.MONTH, mNow.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, mNow.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, true);

        mCaldroidFragment.setArguments(args);
        mCaldroidFragment.selectDate(mSelectedDate);
        mCaldroidFragment.setMonthChangedListener(mMonthChangedListener);
        mCaldroidFragment.setCaldroidListener(mCaldroidListener);

        FragmentTransaction t = mParent.getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar_view, mCaldroidFragment);
        t.commit();

        mWeekAdapter = new WeekPagerAdapter();
        calendarView.mWeekPager.setAdapter(new InfinitePagerAdapter(mWeekAdapter));
        mWeekAdapter.setData(getCalendars(mNow, 0));

        calendarView.setTodayOnClickListener(onNowClickListener);

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

                Calendar cal = Calendar.getInstance();
                cal.clear();
                cal.setTime(calendar.getTime());
                cal.set(Calendar.DAY_OF_WEEK, 2);

                boolean updateCaldroid = true;
                boolean hideCaldroid = false;
                boolean scrollListToDate = true;
                boolean selectWeekAndDay = true;
                updateCurrentDateAndCalenderView(cal, updateCaldroid, hideCaldroid, scrollListToDate, selectWeekAndDay);



            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    private void updateWeekView(Date date) {
        // Updates the week view to show the current week based on the passed in date
        mWeekAdapter.selectWeekAndDay(date);
    }

    private OnStateScrollListener onStateScrollListener = new OnStateScrollListener(
            new OnStateScrollListener.StateScrollListener<EventItemViewModel>() {
        @Override
        public void onFirstItemChanged(EventItemViewModel model, int position) {
            Date date = new Date(model.getCategoryId());
            Log.d("TAG", date.toString());

//            mOnChangeTitle.changeTitle(date);
            showHideNow(model, getLastVisible());
            loadMoreData(position);
            loadMoreHolyData(position, true);

//            updateWeekView(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            boolean updateCaldroid = true;
            boolean hideCaldroid = false;
            boolean scrollListToDate = false;
            boolean selectWeekAndDay = true;
            updateCurrentDateAndCalenderView(calendar, updateCaldroid, hideCaldroid, scrollListToDate, selectWeekAndDay);
        }

        @Override
        public void onLastItemChanged(EventItemViewModel model, int position) {
            EventItemViewModel firstVisible = getFirstVisible();

            Date date = new Date(firstVisible.getCategoryId());
            Log.d("TAG", date.toString());
//            mOnChangeTitle.changeTitle(date);
            showHideNow(firstVisible, model);
            loadMoreData(position);
            loadMoreHolyData(position, false);

//            updateWeekView(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            boolean updateCaldroid = true;
            boolean hideCaldroid = false;
            boolean scrollListToDate = false;
            boolean selectWeekAndDay = true;
            updateCurrentDateAndCalenderView(calendar, updateCaldroid, hideCaldroid, scrollListToDate, selectWeekAndDay);
        }
    });

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public void loadMoreData(int position) {
        if (!isLoading) {
            Calendar calendar = Calendar.getInstance();
            if (position < mStartPosition + 5) {
                calendar.setTimeInMillis(mAdapter.getItem(mStartPosition).getCategoryId());
                mOnLoadMoreData.onLoadPast(calendar);
                isLoading = true;
            }

            if (position > mEndPosition - 5) {
                calendar.setTimeInMillis(mAdapter.getItem(mEndPosition).getCategoryId());
                calendar.add(Calendar.MONTH, 2);
                mOnLoadMoreData.onLoadFuture(calendar);
                isLoading = true;
            }
        }
    }

    public void loadMoreHolyData(int position, boolean scrollUp) {
        if (!isLoadingHoly) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mAdapter.getItem(position).getCategoryId());
            if (scrollUp) {
                Calendar tmp = Calendar.getInstance();
                tmp.setTimeInMillis(mPrevHolyYear.getTimeInMillis());
                tmp.add(Calendar.MONTH, -1);
                if (calendar.getTimeInMillis() <= tmp.getTimeInMillis()) {
                    mOnLoadMoreData.onLoadHolyYear(mPrevHolyYear.get(Calendar.YEAR));
                    isLoadingHoly = true;
                }
            } else {
                Calendar tmp = Calendar.getInstance();
                tmp.setTimeInMillis(mNextHolyYear.getTimeInMillis());
                tmp.add(Calendar.MONTH, -1);

                if (calendar.getTimeInMillis() >= tmp.getTimeInMillis()) {
                    mOnLoadMoreData.onLoadHolyYear(mNextHolyYear.get(Calendar.YEAR));
                    isLoadingHoly = true;
                }
            }
        }
    }

//    public void selectFirstDate() {
//        if (mSelectedDate != null)
//            mCaldroidFragment.deselectDate(mSelectedDate);
//
//        EventItemViewModel model = mAdapter.getItem(mManager.findFirstVisibleItemPosition());
//        Calendar cal = Calendar.getInstance();
//        cal.setTimeInMillis(model.getCategoryId());
//        mSelectedDate = cal;
//        mCaldroidFragment.moveToDate(cal);
//        mCaldroidFragment.selectDate(cal);
//    }

    private View.OnClickListener onNowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            scrollToEventWithDate(mNow);
            mCalendarView.mTodayWrapper.setVisibility(View.GONE);

            boolean updateCaldroid = true;
            boolean hideCaldroid = false;
            boolean scrollListToDate = true;
            boolean selectWeekAndDay = true;
            updateCurrentDateAndCalenderView(mNow, updateCaldroid, hideCaldroid, scrollListToDate, selectWeekAndDay);
        }
    };

    private void scrollToEventWithDate(Calendar calendar) {
        int position = getPositionOfEventWithDate(calendar);
        if (position != -1) {
            mManager.scrollToPositionWithOffset(position, 0);
            // mOnChangeTitle.changeTitle(mNow.getTime());
        }
    }

    private int getPositionOfEventWithDate(Calendar calendar) {
        for (int i = 0; i < mAdapter.getItems().size(); i++) {
            EventItemViewModel viewModel = mAdapter.getItem(i);
            if (viewModel.equals(calendar) || viewModel.after(calendar))
                return i;
        }
        return -1;
    }

    private EventItemViewModel getFirstVisible() {
        return mAdapter.getItem(mManager.findFirstVisibleItemPosition());
    }

    private EventItemViewModel getLastVisible() {
        return mAdapter.getItem(mManager.findLastVisibleItemPosition());
    }

    private void showHideNow(EventItemViewModel first, EventItemViewModel last) {
        mCalendarView.mTodayWrapper.setVisibility(last.before(mNow) || first.after(mNow)
                ? View.VISIBLE : View.GONE);
    }

    public void setInitialContent(Pair<List<EventItemViewModel>, List<CalendarHeaderViewModel>> eventHeaderPair) {
        if (mAdapter == null) {
            mAdapter = new RecyclerAdapter<>(mParent);
            mAdapter.setHasStableIds(true);

            mManager = (LinearLayoutManager) mCalendarView.mDataList.getLayoutManager();
            mCalendarView.mDataList.addOnScrollListener(onStateScrollListener);

            StickyHeadersItemDecoration headerDecoration = new StickyHeadersBuilder()
                    .setAdapter(mAdapter)
                    .setRecyclerView(mCalendarView.mDataList)
                    .setStickyHeadersAdapter(mStickyAdapter = new StickyHeaderRecyclerAdapter(mAdapter))
                    .build();

            mCalendarView.mDataList.addItemDecoration(headerDecoration);
            mCalendarView.mDataList.setAdapter(mAdapter);
        }
        setContent(eventHeaderPair, DataType.MIDDLE);
    }

    public void setContent(Pair<List<EventItemViewModel>, List<CalendarHeaderViewModel>> ehp, DataType type) {
        List<EventItemViewModel> viewModels = ehp.first;
        switch (type) {
            case BEGINNING:
                addToAdapter(viewModels, -1);
                break;
            case MIDDLE:
                mAdapter.add(viewModels.toArray(new EventItemViewModel[viewModels.size()]));
                break;
            case FUTURE:
                Collections.reverse(viewModels);
                addToAdapter(viewModels, -1);
                break;
        }
        for (CalendarHeaderViewModel viewModel : ehp.second) {
            if (!mHeaderMap.containsKey(viewModel.getId())) {
                mHeaderMap.put(viewModel.getId(), viewModel);
                mStickyAdapter.addHeader(viewModel);
            }
        }

        mAdapter.notifyDataSetChanged();
        mCalendarView.mDataList.invalidateItemDecorations();
        if (!isLoading) {
            scrollToEventWithDate(mNow);
            mWeekAdapter.selectWeekAndDay(mNow.getTime());
            mCalendarView.mTodayWrapper.setVisibility(View.GONE);
        }
        isLoading = false;
        updatePositionPointers();

        mWeekAdapter.notifyEventIndicators();
    }

    private ArrayList<Boolean> getEventIndicators() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mNow.getTime());
        return getEventIndicators(calendar.getTimeInMillis());
    }

    private ArrayList<Boolean> getEventIndicators(long calTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calTime);

        // Start with monday
        calendar = DateAppearanceUtils.reset(calendar);
        calendar.set(Calendar.DAY_OF_WEEK, 2);

        ArrayList<Boolean> hasEventDay = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            if (i != 0)
                calendar.add(Calendar.DAY_OF_WEEK, 1);
            hasEventDay.add(mHeaderMap.containsKey(calendar.getTimeInMillis()));
        }
        return hasEventDay;
    }

    private void addToAdapter(List<EventItemViewModel> viewModels, int offset) {
        Calendar cal = Calendar.getInstance();
        for (EventItemViewModel viewModel : viewModels) {
            cal.setTimeInMillis(viewModel.getCategoryId());
            // Get the position of the event with the provided date in the view
            // Add 1 so that we insert our event after the one returned
            int position = getPositionOfEventWithDate(cal) + 1;
            if (position != -1)
                mAdapter.add(position + offset, viewModel);
        }
    }

    public void setHolyContent(int year, List<Holyday> holydays, List<EventItemViewModel> events) {
        Collections.sort(holydays);
        mPrevHolyYear.set(Calendar.YEAR, year - 1);
        mNextHolyYear.set(Calendar.YEAR, year + 1);

        for (Holyday holyday : holydays) {
            long id = holyday.getId();
            if (mHeaderMap.containsKey(id))
                mHeaderMap.get(id).extBind(holyday);
            else {
                CalendarHeaderViewModel viewModel = new CalendarHeaderViewModel(holyday);
                mStickyAdapter.addHeader(viewModel);
                mHeaderMap.put(id, viewModel);
            }
        }

        EventItemViewModel first = mAdapter.getItem(0);
        int idxLast = mAdapter.getItemCount();
        EventItemViewModel last = mAdapter.getItem(idxLast - 1);
        Collections.reverse(events);

        eventsLoop : for (EventItemViewModel event : events) {
            if (event.before(first)) {
                mAdapter.add(0, event);
                continue;
            }
            if (event.after(last)) {
                mAdapter.add(idxLast, event);
                continue;
            }

            for (int i = 0; i < mAdapter.getItems().size(); i++) {
                EventItemViewModel viewModel = mAdapter.getItem(i);
                if (event.equals(viewModel))
                    continue eventsLoop;
                if (event.before(viewModel)) {
                    mAdapter.add(i, event);
                    continue eventsLoop;
                }
            }
        }
        mAdapter.notifyDataSetChanged();
        mCalendarView.mDataList.invalidateItemDecorations();
        if (!isLoadingHoly) {
//            scrollToEventWithDate(mNow);
//            mOnChangeTitle.changeTitle(mNow.getTime());
        }
        updatePositionPointers();
        isLoadingHoly = false;
    }

    private void updatePositionPointers() {
        List<EventItemViewModel> viewModels = mAdapter.getItems();
        mStartPosition = findFirstNotDummy(viewModels);
        Collections.reverse(viewModels);
        mEndPosition = mAdapter.getItemCount()-1 - findFirstNotDummy(viewModels);
        Collections.reverse(viewModels);
    }

    private int findFirstNotDummy(List<EventItemViewModel> viewModels) {
        for (int i = 0; i < viewModels.size(); i++) {
            if (!viewModels.get(i).isDummy())
                return i;
        }
        return -1;
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

    private class WeekPagerAdapter extends PagerAdapter {

        private HashMap<Integer, Pair<WeekView, WeekViewModel>> mViews = new HashMap<>();

        public void setData(ArrayList<Calendar> data) {
            this.mData = data;
        }

        private ArrayList<Calendar> mData = new ArrayList<>();

        private int mCurrentlySelectedWeek;

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
            viewModel.setData(calendar.get(Calendar.WEEK_OF_YEAR), calendar.get(Calendar.YEAR));
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

                viewModel.updateWithEventIndicators(getEventIndicators(calendar.getTimeInMillis()));
                viewModel.bind(viewModelPair.first);
            }
        }

        public void notifyEventIndicators() {
            for (int i = 0; i < getCount(); i++) {
                Pair<WeekView, WeekViewModel> viewModelPair = mViews.get(i);
                WeekViewModel viewModel = viewModelPair.second;

                viewModel.updateWithEventIndicators(getEventIndicators(mData.get(i).getTimeInMillis()));
                viewModel.bind(viewModelPair.first);
            }
        }

        public void selectWeekAndDay(Date date) {
            // Replaces the current views in the pager with week views for the current week
            // based on the passed in date and selects the day in that week
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int week = calendar.get(Calendar.WEEK_OF_YEAR);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            // The day of week starts on a Sunday and setting first day of the week isn't working, so we'll manually adjust
            dayOfWeek--;
            if (dayOfWeek == 0) {
                dayOfWeek = 7;
            }

            // If the week has changed update the week views
            if (mCurrentlySelectedWeek != week) {
                mCurrentlySelectedWeek = week;

                mWeekAdapter.setData(getCalendars(calendar, 0));
                mWeekAdapter.notifyNewData();
            }

            // Find the view model for the currently selected week and set the day
            for (int i = 0; i < getCount(); i++) {
                Pair<WeekView, WeekViewModel> viewModelPair = mViews.get(i);
                WeekViewModel viewModel = viewModelPair.second;

                Calendar calendarForViewModel = mData.get(i);
                int weekForViewModel = calendarForViewModel.get(Calendar.WEEK_OF_YEAR);
                if (weekForViewModel == mCurrentlySelectedWeek) {
                    int dayIndex = dayOfWeek - 1;
                    viewModel.bind(viewModelPair.first, dayIndex);
                }
            }

            Log.d("TAG", "Week: " + week + " day: " + dayOfWeek);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
