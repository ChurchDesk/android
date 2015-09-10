package dk.shape.churchdesk.viewmodel;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import dk.shape.churchdesk.view.EventDetailsView;
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
        void onLoadFuture(int year, int month);
        void onLoadPast(Calendar toLoad);
        void onLoadPast(int year, int month);
        void onLoadHolyYear(int year);
    }

    public interface OnCalendarDateSelectedListener {
        void onDateSelected(Calendar calendar);
    }

    public enum DataType {
        BEGINNING, MIDDLE, FUTURE
    }

    public Calendar mSelectedDate;

    private final BaseActivity mParent;
    private final OnCalendarDateSelectedListener mOnCalendarDateSelectedListener;
    private final CaldroidFragment mCaldroidFragment = new CaldroidFragment();
    private final Calendar mNow;
    private final OnChangeTitle mOnChangeTitle;

    private final OnLoadMoreData mOnLoadMoreData;
    private List<EventItemViewModel> mAllEvents;

    private List<EventItemViewModel> mMyEvents;
    private CalendarView mCalendarView;
    private WeekPagerAdapter mWeekAdapter;
    private RecyclerAdapter<EventItemViewModel> mAdapter;
    private LinearLayoutManager mManager;
    private StickyHeaderRecyclerAdapter mStickyAdapter;
    private SortedMap<Long, CalendarHeaderViewModel> mHeaderMap = new TreeMap<>();

    private int mStartPosition;
    private int mEndPosition = 0;
    private boolean isLoading = false;
    private boolean isMyEvents = false;
    private boolean isCaldroidVisibile = false;

    private boolean isLoadingHoly = false;
    private Calendar mNextHolyYear;
    private Calendar mPrevHolyYear;

    public CalendarViewModel(BaseActivity parent, CalendarViewModel.OnCalendarDateSelectedListener onCalendarDateSelectedListener,
                             OnChangeTitle onChangeTitle, OnLoadMoreData onLoadMoreData) {
        this.mParent = parent;
        this.mOnCalendarDateSelectedListener = onCalendarDateSelectedListener;
        this.mNow = DateAppearanceUtils.reset(Calendar.getInstance());
        this.mNextHolyYear = DateAppearanceUtils.resetHard(Calendar.getInstance());
        this.mPrevHolyYear = DateAppearanceUtils.resetHard(Calendar.getInstance());
        this.mSelectedDate = mNow;
        this.mOnChangeTitle = onChangeTitle;
        this.mOnLoadMoreData = onLoadMoreData;
        this.mAllEvents = new ArrayList<>();
    }

    private void observeShowHideNow(final EventItemViewModel firstItem) {
        final ViewTreeObserver observer = mCalendarView.mDataList.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        EventItemViewModel firstItem2 = getFirstVisible();
                        if (firstItem2 != null && firstItem != null && !firstItem2.compareOnId(firstItem)) {
                            showHideNow(firstItem2, getLastVisible());
                            observer.removeGlobalOnLayoutListener(this);
                        }
                    }
                });
    }

    private WeekViewModel.OnDateClick mOnDateClickListener = new WeekViewModel.OnDateClick() {
        @Override
        public void onDateClick(Calendar calendar) {
            final EventItemViewModel firstItem = getFirstVisible();
            updateCurrentDateAndCalenderView(calendar, true, false, true, false, true);
            observeShowHideNow(firstItem);
        }
    };

    private CaldroidListener mCaldroidListener = new CaldroidListener() {
        @Override
        public void onSelectDate(Calendar calendar, View view) {
            final EventItemViewModel firstItem = getFirstVisible();
            updateCurrentDateAndCalenderView(calendar, false, true, true, true, true);
            observeShowHideNow(firstItem);
        }
    };

    public void updateCaldroidToCurrentDate() {
        mCaldroidFragment.clearSelectedDates();
        mCaldroidFragment.selectDate(mSelectedDate);
        mCaldroidFragment.moveToDate(mSelectedDate);
    }

    public void updateCurrentDateAndCalenderView(Calendar newCurrentDate, boolean updateCaldroid,
                                                 boolean hideCaldroid, boolean scrollListToDate,
                                                 boolean selectWeekAndDay, boolean updateAdapter) {
        mSelectedDate = newCurrentDate;

        if (updateCaldroid && isCaldroidVisibile) {
            mCaldroidFragment.moveToDate(mSelectedDate);
        }
        Date newCurrentDateAsDate = newCurrentDate.getTime();
        mOnChangeTitle.changeTitle(newCurrentDateAsDate);

        if (hideCaldroid)
            mOnCalendarDateSelectedListener.onDateSelected(mSelectedDate);
        if (scrollListToDate)
            scrollToEventWithDate(newCurrentDate);
        if (selectWeekAndDay)
            mWeekAdapter.selectWeekAndDay(newCurrentDateAsDate, updateAdapter);
    }

    public void setIsCaldroidVisibile(boolean isVisible) {
        this.isCaldroidVisibile = isVisible;
    }

    @Override
    public void bind(final CalendarView calendarView) {
        this.mCalendarView = calendarView;

        CaldroidFragment.selectedBackgroundDrawable = R.drawable.calendar_background_selected;
        Bundle args = new Bundle();
        args.putInt(CaldroidFragment.MONTH, mNow.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, mNow.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, true);

        mCaldroidFragment.setArguments(args);
        mCaldroidFragment.selectDate(mSelectedDate);
        mCaldroidFragment.setMonthChangedListener(new CaldroidFragment.OnMonthChangedListener() {
            @Override
            public void onChanged(String month) {
                Bundle extras = mCaldroidFragment.getSavedStates();
                int year = extras.getInt(CaldroidFragment.YEAR);
                int monthNum = extras.getInt(CaldroidFragment.MONTH);

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.MONTH, monthNum - 1); // Subtract 1 since Caldroid months are 1 based and Calendar is 0 based
                cal.set(Calendar.YEAR, year);
                mOnChangeTitle.changeTitle(cal.getTime());

                if (cal.before(mNow)) {
                    mOnLoadMoreData.onLoadFuture(cal);
                }
                else if (cal.after(mNow)) {
                    mOnLoadMoreData.onLoadFuture(cal);
                }
            }
        });
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
                calendar.setTimeInMillis(mWeekAdapter.getItem(calendarView.mWeekPager.getCurrentItem()));

                final EventItemViewModel model = getFirstVisible();

                if (mod(relativePosition, 2) == 0) {
                    mWeekAdapter.setData(getCalendars(calendar,
                            calendarView.mWeekPager.getCurrentItem()));
                    mWeekAdapter.notifyNewData();
                }
                Toast.makeText(mParent, mParent.getString(R.string.week,
                        calendar.get(Calendar.WEEK_OF_YEAR)), Toast.LENGTH_SHORT).show();

                calendar.set(Calendar.DAY_OF_WEEK, 2);
                updateCurrentDateAndCalenderView(calendar, true, false, true, true, false);
                observeShowHideNow(model);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    public void setIsMyEvents(boolean isMyEvents) {
        this.isMyEvents = isMyEvents;

        List<EventItemViewModel> viewModels = mAllEvents;
        if (isMyEvents) {
            mMyEvents = new ArrayList<>();
            for (EventItemViewModel event : mAllEvents) {
                if (event.isDummy() || event.isMyEvent())
                    mMyEvents.add(event);
            }
            viewModels = mMyEvents;
        }

        mAdapter.clear();
        mAdapter.add(viewModels.toArray(new EventItemViewModel[viewModels.size()]));
        mAdapter.notifyDataSetChanged();

        mCalendarView.mDataList.invalidateItemDecorations();

        updatePositionPointers();
        int position = scrollToEventWithDate(mSelectedDate);
        if (position != -1) {
            Date date = new Date(mAdapter.getItem(position).getCategoryId());
            mWeekAdapter.selectWeekAndDay(date, true);
            mOnChangeTitle.changeTitle(date);
        }
    }

    private OnStateScrollListener onStateScrollListener = new OnStateScrollListener(
            new OnStateScrollListener.StateScrollListener<EventItemViewModel>() {
        @Override
        public void onFirstItemChanged(EventItemViewModel model, int position) {
            Calendar calendar = Calendar.getInstance();
            showHideNow(model, getLastVisible());

            loadMoreData(position, calendar);
            loadMoreHolyData(position, true, calendar);

            calendar.setTimeInMillis(model.getCategoryId());

            updateCurrentDateAndCalenderView(calendar, true, false, false, true,
                    calendar.get(Calendar.WEEK_OF_YEAR) != mCurrentlySelectedWeek);
        }

        @Override
        public void onLastItemChanged(EventItemViewModel model, int position) {
            EventItemViewModel firstVisible = getFirstVisible();
            Calendar calendar = Calendar.getInstance();
            showHideNow(firstVisible, model);

            loadMoreData(position, calendar);
            loadMoreHolyData(position, false, calendar);

            calendar.setTimeInMillis(firstVisible.getCategoryId());

            updateCurrentDateAndCalenderView(calendar, true, false, false, true,
                    calendar.get(Calendar.WEEK_OF_YEAR) != mCurrentlySelectedWeek);
        }
    });

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public void loadMoreData(int position, Calendar calendar) {
        if (!isLoading) {
            if (position < mStartPosition + 5) {
                calendar.setTimeInMillis(mAdapter.getItem(mStartPosition).getCategoryId());
                mOnLoadMoreData.onLoadPast(calendar);
                isLoading = true;
            }

            if (position > mEndPosition - 5) {
                calendar.setTimeInMillis(mAdapter.getItem(mEndPosition).getCategoryId());
                calendar.add(Calendar.MONTH, 1);
                mOnLoadMoreData.onLoadFuture(calendar);
                isLoading = true;
            }
        }
    }

    public void loadMoreHolyData(int position, boolean scrollUp, Calendar calendar) {
        if (!isLoadingHoly) {
            calendar.setTimeInMillis(mAdapter.getItem(position).getCategoryId());
            Calendar tmp = Calendar.getInstance();

            if (scrollUp) {
                tmp.setTimeInMillis(mPrevHolyYear.getTimeInMillis());
                tmp.add(Calendar.MONTH, -1);
                if (calendar.getTimeInMillis() <= tmp.getTimeInMillis()) {
                    mOnLoadMoreData.onLoadHolyYear(mPrevHolyYear.get(Calendar.YEAR));
                    isLoadingHoly = true;
                }
            } else {
                tmp.setTimeInMillis(mNextHolyYear.getTimeInMillis());
                tmp.add(Calendar.MONTH, -1);

                if (calendar.getTimeInMillis() >= tmp.getTimeInMillis()) {
                    mOnLoadMoreData.onLoadHolyYear(mNextHolyYear.get(Calendar.YEAR));
                    isLoadingHoly = true;
                }
            }
        }
    }

    private View.OnClickListener onNowClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCalendarView.mTodayWrapper.setVisibility(View.GONE);
            updateCurrentDateAndCalenderView(mNow, true, false, true, true, true);
        }
    };

    private int scrollToEventWithDate(Calendar calendar) {
        int position = getPositionOfEventWithDate(calendar) - 1;
        if (position != -1) {
            mManager.scrollToPositionWithOffset(position, 0);
        }
        return position;
    }

    private int getPositionOfEventWithDate(Calendar calendar) {
        if (calendar != null && mAdapter != null) {
            for (int i = 0; i < mAdapter.getItems().size(); i++) {
                EventItemViewModel viewModel = mAdapter.getItem(i);
                if (viewModel != null) {
                    if (viewModel.equals(calendar))
                        return i+1;
                    if (viewModel.after(calendar))
                        return i;
                }
            }
            return mAdapter.getItemCount();
        }
        return -1;
    }

    private EventItemViewModel getFirstVisible() {
        if (mManager != null && mAdapter != null) {
            int index = mManager.findFirstVisibleItemPosition();
            if (index != -1)
                return mAdapter.getItem(index);
        }
        return null;
    }

    private EventItemViewModel getLastVisible() {
        if (mManager != null && mAdapter != null) {
            int index = mManager.findLastVisibleItemPosition();
            if (index != -1)
                return mAdapter.getItem(index);
        }
        return null;
    }

    private void showHideNow(EventItemViewModel first, EventItemViewModel last) {
        if (first != null && last != null)
            mCalendarView.mTodayWrapper.setVisibility(
                    last.before(mNow) || first.after(mNow)
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
        mCalendarView.mTodayWrapper.setVisibility(View.GONE);
        setContent(eventHeaderPair, DataType.MIDDLE);
    }

    public void setContent(Pair<List<EventItemViewModel>, List<CalendarHeaderViewModel>> ehp, DataType type) {
        List<EventItemViewModel> viewModels = ehp.first;

        List<EventItemViewModel> tmpMyEvents = new ArrayList<>();
        for (EventItemViewModel viewModel : viewModels) {
            if (viewModel.isMyEvent()) {
                tmpMyEvents.add(viewModel);
            }
        }
        if (!tmpMyEvents.isEmpty() && isMyEvents)
            viewModels = tmpMyEvents;

        switch (type) {
            case BEGINNING:
                addToAdapter(viewModels, 0);
                break;
            case MIDDLE:
                mAdapter.add(viewModels.toArray(new EventItemViewModel[viewModels.size()]));
                mAllEvents.addAll(viewModels);
                break;
            case FUTURE:
                Collections.reverse(viewModels);
                addToAdapter(viewModels, 0);
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
            scrollToEventWithDate(mSelectedDate);
            mCurrentlySelectedWeek = mSelectedDate.get(Calendar.WEEK_OF_YEAR);
            mWeekAdapter.selectWeekAndDay(mSelectedDate.getTime(), false);
        }
        isLoading = false;
        updatePositionPointers();

        mWeekAdapter.notifyEventIndicators();
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
            int position = getPositionOfEventWithDate(cal);
            if (position != -1) {
                mAdapter.add(position + offset, viewModel);
                mAllEvents.add(position + offset, viewModel);
            }
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

        EventItemViewModel first = mAllEvents.get(0);
        int idxLast = mAllEvents.size();
        EventItemViewModel last = mAllEvents.get(idxLast - 1);
        Collections.reverse(events);


        eventsLoop : for (EventItemViewModel event : events) {
            if (event.before(first)) {
                mAllEvents.add(0, event);
                continue;
            }
            if (event.after(last)) {
                mAllEvents.add(idxLast, event);
                continue;
            }

            for (int i = 0; i < mAdapter.getItems().size(); i++) {
                EventItemViewModel viewModel = mAdapter.getItem(i);
                if (event.equals(viewModel))
                    continue eventsLoop;
                if (event.before(viewModel)) {
                    mAllEvents.add(i, event);
                    continue eventsLoop;
                }
            }
        }

        setIsMyEvents(isMyEvents);
        isLoadingHoly = false;
    }

    private void updatePositionPointers() {
        List<EventItemViewModel> viewModels = isMyEvents ? mMyEvents : mAllEvents;
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

    ArrayList<Long> mCalendarsContainer = new ArrayList<Long>() {{
        add(null);
        add(null);
        add(null);
        add(null);
        add(null);
    }};

    private ArrayList<Long> getCalendars(Calendar middle, int position) {
        mCalendarsContainer.remove(position);
        mCalendarsContainer.add(position, middle.getTimeInMillis());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(middle.getTime());
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        int newPosition = mod(position + 1, 5);
        mCalendarsContainer.remove(newPosition);
        mCalendarsContainer.add(newPosition, calendar.getTimeInMillis());

        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        newPosition = mod(position + 2, 5);
        mCalendarsContainer.remove(newPosition);
        mCalendarsContainer.add(newPosition, calendar.getTimeInMillis());

        calendar.add(Calendar.WEEK_OF_YEAR, -4);
        newPosition = mod(position - 2, 5);
        mCalendarsContainer.remove(newPosition);
        mCalendarsContainer.add(newPosition, calendar.getTimeInMillis());

        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        newPosition = mod(position - 1, 5);
        mCalendarsContainer.remove(newPosition);
        mCalendarsContainer.add(newPosition, calendar.getTimeInMillis());
        return mCalendarsContainer;
    }

    private int mod(int x, int y) {
        int result = x % y;
        return result < 0 ? result + y : result;
    }

    private int mCurrentlySelectedWeek;

    private class WeekPagerAdapter extends PagerAdapter {

        public WeekPagerAdapter() {
            this.placeHolder = Calendar.getInstance();
            this.placeHolder.setFirstDayOfWeek(Calendar.MONDAY);
        }

        private HashMap<Integer, Pair<WeekView, WeekViewModel>> mViews = new HashMap<>();

        public void setData(ArrayList<Long> data) {
            this.mData = data;
        }

        private ArrayList<Long> mData = new ArrayList<>();
        private Calendar placeHolder;

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        public Long getItem(int position) {
            return (mData.isEmpty() || mData.size() <= position) ? null : mData.get(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position > 5)
                return new View(container.getContext());

            WeekView view = new WeekView(container.getContext());
            WeekViewModel viewModel = new WeekViewModel(mOnDateClickListener);

            placeHolder.setTimeInMillis(mData.get(position));
            viewModel.setData(placeHolder.get(Calendar.WEEK_OF_YEAR), placeHolder.get(Calendar.YEAR));
            viewModel.bind(view);
            mViews.put(position, new Pair<>(view, viewModel));

            container.addView(view);
            return view;
        }

        public void notifyNewData() {
            for (int i = 0; i < getCount(); i++) {
                Pair<WeekView, WeekViewModel> viewModelPair = mViews.get(i);
                if (viewModelPair != null) {
                    WeekViewModel viewModel = viewModelPair.second;

                    long millis = mData.get(i);
                    placeHolder.setTimeInMillis(millis);
                    viewModel.setData(placeHolder.get(Calendar.WEEK_OF_YEAR), placeHolder.get(Calendar.YEAR));

                    viewModel.updateWithEventIndicators(getEventIndicators(millis));
                    viewModel.bind(viewModelPair.first);
                }
            }
        }

        public void notifyEventIndicators() {
            for (int i = 0; i < getCount(); i++) {
                Pair<WeekView, WeekViewModel> viewModelPair = mViews.get(i);
                WeekViewModel viewModel = viewModelPair.second;

                viewModel.updateWithEventIndicators(getEventIndicators(mData.get(i)));
                viewModel.bind(viewModelPair.first);
            }
        }

        public void selectWeekAndDay(Date date, boolean updateAdapter) {
            // Replaces the current views in the pager with week views for the current week
            // based on the passed in date and selects the day in that week
            Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            calendar.setTime(date);
            int week = calendar.get(Calendar.WEEK_OF_YEAR);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            // The day of week starts on a Sunday and setting first day of the week isn't working, so we'll manually adjust
            dayOfWeek--;
            if (dayOfWeek == 0) {
                dayOfWeek = 7;
            }
            mCurrentlySelectedWeek = week;

            // If the week has changed update the week views
            if (updateAdapter) {
                mWeekAdapter.setData(getCalendars(calendar, mCalendarView.mWeekPager.getCurrentItem()));
                mWeekAdapter.notifyNewData();
            }

            // Find the view model for the currently selected week and set the day
            for (int i = 0; i < getCount(); i++) {
                placeHolder.setTimeInMillis(mData.get(i));
                if (placeHolder.get(Calendar.WEEK_OF_YEAR) == mCurrentlySelectedWeek) {
                    Pair<WeekView, WeekViewModel> viewModelPair = mViews.get(i);
                    viewModelPair.second.bind(viewModelPair.first, dayOfWeek - 1);
                }
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
