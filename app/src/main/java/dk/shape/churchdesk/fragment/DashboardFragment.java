package dk.shape.churchdesk.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;


import org.parceler.Parcels;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import dk.shape.churchdesk.BaseFloatingButtonFragment;
import dk.shape.churchdesk.EventDetailsActivity;
import dk.shape.churchdesk.MessageActivity;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.StartActivity;
import dk.shape.churchdesk.entity.Event;
import dk.shape.churchdesk.entity.Message;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.HttpStatusCode;
import dk.shape.churchdesk.network.RequestHandler;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.GetInvitesRequest;
import dk.shape.churchdesk.request.GetTodayEvents;
import dk.shape.churchdesk.request.GetUnreadMessagesRequest;
import dk.shape.churchdesk.util.AccountUtils;
import dk.shape.churchdesk.util.MavenPro;
import dk.shape.churchdesk.view.BaseFrameLayout;
import dk.shape.churchdesk.view.DashboardView;
import dk.shape.churchdesk.view.RefreshLoadMoreView;
import dk.shape.churchdesk.viewmodel.BaseDashboardViewModel;
import dk.shape.churchdesk.viewmodel.DashboardViewModel;
import dk.shape.churchdesk.viewmodel.EventItemViewModel;
import dk.shape.churchdesk.viewmodel.EventsViewModel;
import dk.shape.churchdesk.viewmodel.InvitationItemViewModel;
import dk.shape.churchdesk.viewmodel.InvitationsViewModel;
import dk.shape.churchdesk.viewmodel.MessageItemViewModel;
import dk.shape.churchdesk.viewmodel.MessagesViewModel;
import io.intercom.android.sdk.Intercom;

/**
 * Created by steffenkarlsson on 17/03/15.
 */
public class DashboardFragment extends BaseFloatingButtonFragment {

    private enum RequestTypes {
        EVENTS, INVITATIONS, MESSAGES, READ_MESSAGE
    }

    private static final int TAB_1 = 0;
    private static final int TAB_2 = 1;
    private static final int TAB_3 = 2;

    private DashboardPagerAdapter _adapter;

    private HashMap<Integer, Pair<RefreshLoadMoreView, BaseDashboardViewModel>> mTabs = new HashMap<>();

    private DashboardView _dashboardView;
    private DashboardViewModel _dashboardViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _adapter = new DashboardPagerAdapter(getActivity());
        _dashboardViewModel = new DashboardViewModel(_adapter);
        _dashboardView = new DashboardView(getActivity());
    }

    @Override
    public void onResume(){
        super.onResume();

        Date date = new Date();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean newEvent = prefs.getBoolean("newEvent", false);
        long millis = prefs.getLong("eventsTimestamp", 0L);
        Date eventsTimestamp = new Date(millis);

        long mills = date.getTime() - eventsTimestamp.getTime();
        long Mins = mills / (1000*60);
                if (newEvent || (Mins > 10)){
            prefs.edit().putBoolean("newEvent", false).apply();
            loadTodayEvents();
        }
    }

    @Override
    protected int getTitleResource() {
        return R.string.menu_dashboard;
    }

    @Override
    protected BaseFrameLayout getContentView() {
        _dashboardViewModel.bind(_dashboardView);
        return _dashboardView;
    }

    private void loadMessages() {
        new GetUnreadMessagesRequest()
                .withContext(getActivity())
                .setOnRequestListener(listener)
                .runAsync(RequestTypes.MESSAGES);
    }

    private void loadTodayEvents() {
        new GetTodayEvents().withContext(getActivity())
                .setOnRequestListener(listener)
                .runAsync(RequestTypes.EVENTS);
    }

    private void loadEventInvites() {
        new GetInvitesRequest().withContext(getActivity()).setOnRequestListener(listener)
                .runAsync(RequestTypes.INVITATIONS);
    }

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
            if (result.statusCode == HttpStatusCode.SC_OK && result.response != null) {
                switch (RequestHandler.<RequestTypes>getRequestIdentifierFromId(id)) {
                    case EVENTS: {
                        Date date = new Date();
                        try {
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            prefs.edit().putLong("eventsTimestamp", date.getTime()).apply();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        Pair<RefreshLoadMoreView, BaseDashboardViewModel> viewModelPair = mTabs.get(TAB_1);
                        BaseDashboardViewModel viewModel = viewModelPair.second;
                        viewModel.extBind(viewModelPair.first, (List<Event>) result.response);
                        viewModel.bind(viewModelPair.first);
                        break;
                    }
                    case INVITATIONS: {
                        Pair<RefreshLoadMoreView, BaseDashboardViewModel> viewModelPair = mTabs.get(TAB_2);
                        BaseDashboardViewModel viewModel = viewModelPair.second;
                        viewModel.extBind(viewModelPair.first, (List<Event>) result.response);
                        viewModel.bind(viewModelPair.first);
                        break;
                    }
                    case MESSAGES: {
                        Pair<RefreshLoadMoreView, BaseDashboardViewModel> viewModelPair = mTabs.get(TAB_3);
                        BaseDashboardViewModel viewModel = viewModelPair.second;
                        viewModel.extBind(viewModelPair.first, (List<Message>) result.response);
                        viewModel.bind(viewModelPair.first);
                        break;
                    }
                    case READ_MESSAGE:
                        break;
                }
            }
        }

        @Override
        public void onProcessing() {

        }
    };

    private class DashboardPagerAdapter extends PagerAdapter {

        private Context _context;

        public DashboardPagerAdapter(Context context) {
            _context = context;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            int resId = 0;

            switch (position) {
                case TAB_1:
                    resId = R.string.dashboard_tab_1;
                    break;
                case TAB_2:
                    resId = R.string.dashboard_tab_2;
                    break;
                case TAB_3:
                    resId = R.string.dashboard_tab_3;
                    break;
            }
            if(resId > 0) {
                return MavenPro.getInstance().getStringWithCorrectFont(_context, getString(resId));
            }
            return "";
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            RefreshLoadMoreView view = new RefreshLoadMoreView(getActivity());
            BaseDashboardViewModel viewModel = null;

            if (mTabs.containsKey(position)) {
                Pair<RefreshLoadMoreView, BaseDashboardViewModel> viewModelPair = mTabs.get(position);
                view = viewModelPair.first;
                viewModel = viewModelPair.second;
                viewModel.bind(view);
            } else {
                switch (position) {
                    case TAB_1:
                        loadTodayEvents();
                        viewModel = new EventsViewModel(_user,
                                new BaseDashboardViewModel.OnRefreshData() {
                                    @Override
                                    public void onRefresh() {
                                        loadTodayEvents();
                                    }
                                }, new EventItemViewModel.OnEventClickListener() {
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
                        });
                        break;
                    case TAB_2:
                        loadEventInvites();
                        viewModel = new InvitationsViewModel(_user,
                                new BaseDashboardViewModel.OnRefreshData() {
                                    @Override
                                    public void onRefresh() {
                                        loadEventInvites();
                                    }
                                }, new InvitationItemViewModel.OnInvitationClickListener() {
                                    @Override
                                    public void onClick(Event invitation) {
                                        Bundle bundle = new Bundle();
                                        bundle.putParcelable(EventDetailsActivity.KEY_EVENT, Parcels.wrap(invitation));
                                        showActivity(EventDetailsActivity.class, true, bundle);
                                    }
                        });
                        break;
                    case TAB_3:
                        loadMessages();
                        viewModel = new MessagesViewModel(_user,
                                new BaseDashboardViewModel.OnRefreshData() {
                                    @Override
                                    public void onRefresh() {
                                        loadMessages();
                                    }
                                }, new MessageItemViewModel.OnMessageClickListener() {
                                    @Override
                                    public void onClick(Message message) {
//                                        new MarkMessageAsReadRequest(message)
//                                                .withContext(getActivity())
//                                                .setOnRequestListener(listener)
//                                                .runAsync(RequestTypes.READ_MESSAGE);

                                        Bundle extras = new Bundle();
                                        extras.putParcelable(MessageActivity.KEY_MESSAGE, Parcels.wrap(message));
                                        showActivity(MessageActivity.class, true, extras);
                                    }
                                }, true);
                        break;
                }
                mTabs.put(position, new Pair<>(view, viewModel));
            }
            if (view != null) {
                container.addView(view, 0);
                return view;
            }
            return new View(getActivity());
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
