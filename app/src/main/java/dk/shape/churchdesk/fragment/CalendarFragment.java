package dk.shape.churchdesk.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.roomorama.caldroid.CaldroidFragment;


import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import dk.shape.churchdesk.BaseActivity;
import dk.shape.churchdesk.BaseFloatingButtonFragment;
import dk.shape.churchdesk.EventDetailsActivity;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.StartActivity;
import dk.shape.churchdesk.entity.Event;
import dk.shape.churchdesk.entity.Holyday;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.HttpStatusCode;
import dk.shape.churchdesk.network.RequestHandler;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.GetEvents;
import dk.shape.churchdesk.request.GetHolydays;
import dk.shape.churchdesk.util.AccountUtils;
import dk.shape.churchdesk.view.BaseFrameLayout;
import dk.shape.churchdesk.view.CalendarView;
import dk.shape.churchdesk.viewmodel.CalendarHeaderViewModel;
import dk.shape.churchdesk.viewmodel.CalendarViewModel;
import dk.shape.churchdesk.viewmodel.EventItemViewModel;
import io.intercom.android.sdk.Intercom;

import static dk.shape.churchdesk.util.MapUtils.merge;

/**
 * Created by steffenkarlsson on 17/03/15.
 */
public class CalendarFragment extends BaseFloatingButtonFragment {

    private enum RequestTypes {
        PREV,
        CURRENT,
        NEXT,
        HOLYDAYS
    }

    private SimpleDateFormat mFormatter = new SimpleDateFormat("MMMM", Locale.getDefault());

    @Override
    protected int getTitleResource() {
        return -1;
    }

    private CalendarView mView;
    private CalendarViewModel mViewModel;
    private BaseActivity mActivity;
    private Crouton mCrouton;
    private Set<String> mIds;
    private int mHolyYear;
    private  String mLanguage;
    private static boolean isLoaded = false;

    private static final Style INFINITE = new Style.Builder()
            .setBackgroundColor(R.color.background_blue)
            .setTextColor(android.R.color.white)
            .setHeightDimensionResId(R.dimen.crouton_height)
            .build();

    private static final Configuration CONFIGURATION_INFINITE = new Configuration.Builder()
            .setDuration(Configuration.DURATION_INFINITE)
            .build();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mActivity = (BaseActivity) getActivity();
        mActivity.setHasDrawable(mTitleClickListener);

        if (isLoaded && mViewModel != null) {
            onChangeTitle.changeTitle(mViewModel.mSelectedDate.getTime());
        } else {
            onChangeTitle.changeTitle(new Date());
            mIds = new HashSet<>();
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_calendar_filter, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mActivity.setHasDrawable(mTitleClickListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.setHasDrawable(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_calendar_filter) {
            AlertDialog.Builder filterDialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            filterDialog.setTitle(R.string.messages_filter_title);
            filterDialog.setNegativeButton(R.string.calendar_filter_button_negative,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mViewModel != null) {
                                if (mCrouton != null)
                                    mCrouton.hide();
                                mViewModel.setIsMyEvents(false);
                            }
                        }
                    });
            filterDialog.setPositiveButton(R.string.calendar_filter_button_positive,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mViewModel != null) {
                                mViewModel.setIsMyEvents(true);
                                mCrouton = Crouton.makeText(getActivity(),
                                            R.string.showing_my_events, INFINITE, mView.mContainer)
                                        .setConfiguration(CONFIGURATION_INFINITE)
                                        .setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mCrouton.hide();
                                                mViewModel.setIsMyEvents(false);
                                            }
                                        });
                                mCrouton.show();
                            }
                        }
                    });
            filterDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private BaseActivity.OnTitleClickListener mTitleClickListener
            = new BaseActivity.OnTitleClickListener() {
        @Override
        public void onClick(final boolean isSelected) {

            final View view = mView.mCalendarView;
            Animation animation;

            mViewModel.updateCaldroidToCurrentDate();
            mViewModel.setIsCaldroidVisibile(isSelected);

            if (isSelected) {
                animation = AnimationUtils.loadAnimation(mActivity, R.anim.in_from_top);
                view.setVisibility(View.VISIBLE);
                view.invalidate();
            } else {
                animation = AnimationUtils.loadAnimation(mActivity, R.anim.out_top);
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

            final View view = mView.mCalendarView;
            Animation animation;

            mViewModel.setIsCaldroidVisibile(false);

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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (!isLoaded || !prefs.getBoolean("isLoaded", false)) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            mIds.add(String.format("%d%d", year, month));
            Date date = new Date();
            prefs.edit().putBoolean("isLoaded", true).apply();
            prefs.edit().putLong("calendarTimestamp", date.getTime()).apply();
            new GetEvents(year, month)
                    .withContext(getActivity())
                    .setOnRequestListener(listener)
                    .runAsync(RequestTypes.CURRENT);
        }
    }

    private CalendarViewModel.OnLoadMoreData onLoadMoreData = new CalendarViewModel.OnLoadMoreData() {
        @Override
        public void onLoadFuture(Calendar toLoad) {
            onLoadFuture(toLoad.get(Calendar.YEAR), toLoad.get(Calendar.MONTH));
        }

        @Override
        public void onLoadFuture(int year, int month) {
            month = month == 0 ? 1 : month;
            String id = String.format("%d%d", year, month);
            if (!mIds.contains(id)) {
                mIds.add(id);
                new GetEvents(year, month)
                        .withContext(getActivity())
                        .setOnRequestListener(listener)
                        .runAsync(RequestTypes.NEXT);
            }
        }

        @Override
        public void onLoadPast(Calendar toLoad) {
            onLoadPast(toLoad.get(Calendar.YEAR), toLoad.get(Calendar.MONTH));
        }

        @Override
        public void onLoadPast(int year, int month) {
            if (month == 0){
                month = 12;
                year = year - 1;
            }
            String id = String.format("%d%d", year, month);
            if (!mIds.contains(id)) {
                mIds.add(id);
                new GetEvents(year, month)
                        .withContext(getActivity())
                        .setOnRequestListener(listener)
                        .runAsync(RequestTypes.PREV);
            }
        }

        @Override
        public void onLoadHolyYear(int year, String language) {
            new GetHolydays(mHolyYear = year, mLanguage = language)
                    .withContext(getActivity())
                    .setOnRequestListener(listener)
                    .runAsync(RequestTypes.HOLYDAYS);
        }
    };

    private BaseRequest.OnRequestListener listener = new BaseRequest.OnRequestListener() {
        @Override
        public void onError(int id, ErrorCode errorCode) {
            if (errorCode == ErrorCode.INVALID_GRANT){
                AccountUtils.getInstance(getActivity()).clear();
                Intercom.client().reset();
                showActivity(StartActivity.class, false, null);
                getActivity().finish();
            }
        }

        @Override
        public void onSuccess(int id, Result result) {
            if ((result.statusCode == HttpStatusCode.SC_OK || result.statusCode == 304) && result.response != null) {
                RequestTypes type;
                switch (type = RequestHandler.getRequestIdentifierFromId(id)) {
                    case HOLYDAYS: {
                        List<Holyday> holydays = (List<Holyday>) result.response;
                        Collections.sort(holydays);
                        List<EventItemViewModel> viewModels = new ArrayList<>();
                        for (Holyday holyday : holydays)
                            viewModels.add(EventItemViewModel.instantiateAsDummy(
                                    Event.instantiateAsDummy(holyday.getId())));
                        mViewModel.setHolyContent(mHolyYear, mLanguage, holydays, viewModels);
                        break;
                    }
                    default:
                        if (type == RequestTypes.CURRENT) {
                            Calendar cal = Calendar.getInstance();
                            onLoadMoreData.onLoadHolyYear(mHolyYear = cal.get(Calendar.YEAR), _user.mLocale.get("country"));
                            onLoadMoreData.onLoadPast(cal);
                            cal.add(Calendar.MONTH, 2);
                            onLoadMoreData.onLoadFuture(cal);
                        }
                        SortedMap<Long, List<Event>> eventMap = (SortedMap<Long, List<Event>>) result.response;
                        if (eventMap != null && !eventMap.isEmpty()) {
                            Pair<List<EventItemViewModel>, List<CalendarHeaderViewModel>> viewModels
                                    = convertToViewModel(eventMap);
                            if (type == RequestTypes.CURRENT) {
                                mViewModel.setInitialContent(viewModels);
                                isLoaded = true;
                            } else {
                                mViewModel.setContent(viewModels,
                                        type == RequestTypes.NEXT
                                                ? CalendarViewModel.DataType.FUTURE
                                                : CalendarViewModel.DataType.BEGINNING);
                            }
                        } else
                            mViewModel.setLoading(false);
                        break;
                }
            }
        }

        @Override
        public void onProcessing() {

        }
    };

    private Pair<List<EventItemViewModel>, List<CalendarHeaderViewModel>> convertToViewModel(
            SortedMap<Long, List<Event>> eventMap) {
        List<Event> events = new ArrayList<>();
        for (List<Event> eventList : eventMap.values()) {
            events.addAll(eventList);
        }

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
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if (event.mType.equals("absence") )
                prefs.edit().putBoolean("absence", true).apply();
            else
                prefs.edit().putBoolean("absence", false).apply();
            Bundle bundle = new Bundle();
            bundle.putParcelable(EventDetailsActivity.KEY_EVENT, Parcels.wrap(event));
            showActivity(EventDetailsActivity.class, true, bundle);
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
        if (mView == null)
            mView = new CalendarView(getActivity());
        if (mViewModel == null) {
            mViewModel = new CalendarViewModel(mActivity,
                    mCalendarDateSelectedListener, onChangeTitle, onLoadMoreData);
            mViewModel.bind(mView);
        }
        return mView;
    }
}
