package dk.shape.churchdesk.fragment;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;

import org.apache.http.HttpStatus;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import dk.shape.churchdesk.BaseActivity;
import dk.shape.churchdesk.BaseFloatingButtonFragment;
import dk.shape.churchdesk.EventDetailsActivity;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Event;
import dk.shape.churchdesk.entity.Holyday;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.RequestHandler;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.GetEvents;
import dk.shape.churchdesk.request.GetHolydays;
import dk.shape.churchdesk.view.BaseFrameLayout;
import dk.shape.churchdesk.view.CalendarView;
import dk.shape.churchdesk.viewmodel.CalendarHeaderViewModel;
import dk.shape.churchdesk.viewmodel.CalendarViewModel;
import dk.shape.churchdesk.viewmodel.EventItemViewModel;

import static dk.shape.churchdesk.util.MapUtils.merge;

/**
 * Created by steffenkarlsson on 17/03/15.
 */
public class CalendarFragment extends BaseFloatingButtonFragment {

    private enum RequestTypes {
        PREV, CURRENT, NEXT, HOLYDAYS
    }

    private SimpleDateFormat formatter = new SimpleDateFormat("MMMM", Locale.getDefault());

    @Override
    protected int getTitleResource() {
        return -1;
    }

    private CalendarView mView;
    private CalendarViewModel mViewModel;
    private BaseActivity mActivity;

    private SortedMap<Long, List<Event>> mEvents;

    private int mHolyYear;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = (BaseActivity) getActivity();
        mActivity.setHasDrawable(mTitleClickListener);
        onChangeTitle.changeTitle(new Date());
        mEvents = new TreeMap<>();
    }

    private BaseActivity.OnTitleClickListener mTitleClickListener
            = new BaseActivity.OnTitleClickListener() {
        @Override
        public void onClick(final boolean isSelected) {
            final View view = mView.mCalendarView;
//            mViewModel.selectFirstDate();
            Animation animation;

            if (isSelected) {
                animation = AnimationUtils.loadAnimation(getActivity(), R.anim.in_from_top);
                view.setVisibility(View.VISIBLE);
                view.invalidate();
            } else {
                animation = AnimationUtils.loadAnimation(getActivity(), R.anim.out_top);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mView.mCalendarView.setVisibility(View.GONE);
                        mView.mCalendarView.invalidate();
                        mViewModel.updateCaldroidToCurrentDate();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
            view.startAnimation(animation);
        }
    };

    private CalendarViewModel.OnCalendarDateSelectedListener mCalendarDateSelectedListener = new CalendarViewModel.OnCalendarDateSelectedListener() {
        public void onDateSelected(Calendar calendar) {
            // Animate calendar out of view and deselect titleView
            BaseActivity activity = (BaseActivity) getActivity();
            final TextView titleView = activity.getTitleView();
            titleView.setSelected(false);

//            Date date = calendar.getTime();
//            mActivity.setActionBarTitle(mFormatter.format(date.getTime()));

            final View view = mView.mCalendarView;
            Animation animation;

            animation = AnimationUtils.loadAnimation(getActivity(), R.anim.out_top);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mView.mCalendarView.setVisibility(View.GONE);
                    mView.mCalendarView.invalidate();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            view.startAnimation(animation);
        }
    };

    @Override
    protected void onUserAvailable() {
        super.onUserAvailable();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        new GetEvents(year, calendar.get(Calendar.MONTH)+1)
                .withContext(getActivity())
                .setOnRequestListener(listener)
                .runAsync(RequestTypes.CURRENT);
    }

    private CalendarViewModel.OnLoadMoreData onLoadMoreData = new CalendarViewModel.OnLoadMoreData() {
        @Override
        public void onLoadFuture(Calendar toLoad) {
            new GetEvents(toLoad.get(Calendar.YEAR), toLoad.get(Calendar.MONTH))
                .withContext(getActivity())
                .setOnRequestListener(listener)
                .runAsync(RequestTypes.NEXT);
        }

        @Override
        public void onLoadPast(Calendar toLoad) {
            new GetEvents(toLoad.get(Calendar.YEAR), toLoad.get(Calendar.MONTH))
                    .withContext(getActivity())
                    .setOnRequestListener(listener)
                    .runAsync(RequestTypes.PREV);
        }

        @Override
        public void onLoadHolyYear(int year) {
            new GetHolydays(mHolyYear = year)
                    .withContext(getActivity())
                    .setOnRequestListener(listener)
                    .runAsync(RequestTypes.HOLYDAYS);
        }
    };

    private BaseRequest.OnRequestListener listener = new BaseRequest.OnRequestListener() {
        @Override
        public void onError(int id, ErrorCode errorCode) {

        }

        @Override
        public void onSuccess(int id, Result result) {
            if (result.statusCode == HttpStatus.SC_OK
                    && result.response != null) {
                RequestTypes type;
                switch (type = RequestHandler.getRequestIdentifierFromId(id)) {
                    case HOLYDAYS: {
                        List<Holyday> holydays = (List<Holyday>) result.response;
                        Collections.sort(holydays);
                        List<EventItemViewModel> viewModels = new ArrayList<>();
                        for (Holyday holyday : holydays)
                            viewModels.add(EventItemViewModel.instantiateAsDummy(
                                    Event.instantiateAsDummy(holyday.getId())));
                        mViewModel.setHolyContent(mHolyYear, holydays, viewModels);
                        break;
                    } default:
                        if (type == RequestTypes.CURRENT) {
                            Calendar cal = Calendar.getInstance();
                            onLoadMoreData.onLoadHolyYear(mHolyYear = cal.get(Calendar.YEAR));
                            onLoadMoreData.onLoadPast(cal);
                            cal.add(Calendar.MONTH, 2);
                            onLoadMoreData.onLoadFuture(cal);
                        }
                        SortedMap<Long, List<Event>> eventMap =
                                (SortedMap<Long, List<Event>>) result.response;
                        if (!eventMap.isEmpty()) {
                            Pair<List<EventItemViewModel>, List<CalendarHeaderViewModel>> viewModels
                                    = convertToViewModel(eventMap, true);
                            if (type == RequestTypes.CURRENT) {
                                mViewModel.setInitialContent(viewModels);
                            } else {
                                mViewModel.setContent(viewModels,
                                        type == RequestTypes.NEXT
                                                ? CalendarViewModel.DataType.FUTURE
                                                : CalendarViewModel.DataType.BEGINNING);
                            }
                            mEvents = merge(mEvents, eventMap);
                        } else {
                            mViewModel.setLoading(false);
                        }
                        break;
                }
            }
        }

        @Override
        public void onProcessing() {

        }
    };

    private Pair<List<EventItemViewModel>, List<CalendarHeaderViewModel>> convertToViewModel(
            SortedMap<Long, List<Event>> eventMap, boolean includeHeaders) {
        List<Event> events = new ArrayList<>();
        for (List<Event> eventList : eventMap.values())
            events.addAll(eventList);
        
        List<EventItemViewModel> eventViewModels = new ArrayList<>();
        for (Event event : events)
            eventViewModels.add(new EventItemViewModel(event, _user, mOnEventClickListener, true));

        List<CalendarHeaderViewModel> headerViewModels = new ArrayList<>();
        for (Long dateTime : eventMap.keySet())
            headerViewModels.add(new CalendarHeaderViewModel(dateTime));

        return new Pair<>(eventViewModels, headerViewModels);
    }

    private EventItemViewModel.OnEventClickListener mOnEventClickListener
            = new EventItemViewModel.OnEventClickListener() {
        @Override
        public void onClick(Event event) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(EventDetailsActivity.KEY_EVENT, Parcels.wrap(event));
            showActivity(EventDetailsActivity.class, true, bundle);
        }
    };

    private CaldroidFragment.OnMonthChangedListener mOnMonthChangedListener
            = new CaldroidFragment.OnMonthChangedListener() {
        @Override
        public void onChanged(String month) {
//            mActivity.setActionBarTitle(month);
        }
    };

    private CalendarViewModel.OnChangeTitle onChangeTitle =
            new CalendarViewModel.OnChangeTitle() {
        @Override
        public void changeTitle(Date date) {
            mActivity.setActionBarTitle(mFormatter.format(date.getTime()));
        }
    };

    @Override
    protected BaseFrameLayout getContentView() {
        mView = new CalendarView(getActivity());
        mViewModel = new CalendarViewModel(mActivity, mOnMonthChangedListener,
                mCalendarDateSelectedListener, onChangeTitle, onLoadMoreData);
        mViewModel.bind(mView);
        return mView;
    }
}
