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

import java.util.HashMap;

import dk.shape.churchdesk.EventDetailsActivity;
import dk.shape.churchdesk.MessageActivity;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Event;
import dk.shape.churchdesk.entity.Message;
import dk.shape.churchdesk.util.MavenPro;
import dk.shape.churchdesk.view.RefreshLoadMoreView;
import dk.shape.churchdesk.viewmodel.BaseDashboardViewModel;
import dk.shape.churchdesk.viewmodel.EventItemViewModel;
import dk.shape.churchdesk.viewmodel.EventsViewModel;
import dk.shape.churchdesk.viewmodel.InvitationItemViewModel;
import dk.shape.churchdesk.viewmodel.InvitationsViewModel;
import dk.shape.churchdesk.viewmodel.MessageItemViewModel;
import dk.shape.churchdesk.viewmodel.MessagesViewModel;

/**
 * Created by chirag on 19/09/16.
 */
public class PeopleFragment extends BaseFragment{
    private enum RequestTypes {
        PEOPLE, SEGMENTS
    }

    private static final int TAB_1 = 0;
    private static final int TAB_2 = 1;

    private DashboardPagerAdapter _adapter;
    private HashMap<Integer, Pair<RefreshLoadMoreView, BaseDashboardViewModel>> mTabs = new HashMap<>();

    private class DashboardPagerAdapter extends PagerAdapter {

        private Context _context;

        public DashboardPagerAdapter(Context context) {
            _context = context;
        }

        @Override
        public int getCount() {
            return 2;
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
