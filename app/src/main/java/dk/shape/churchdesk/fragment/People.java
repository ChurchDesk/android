package dk.shape.churchdesk.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.http.HttpStatus;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dk.shape.churchdesk.PeopleFloatingButtonFragment;
import dk.shape.churchdesk.PersonDetailsActivity;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.StartActivity;
import dk.shape.churchdesk.entity.Event;
import dk.shape.churchdesk.entity.Person;
import dk.shape.churchdesk.entity.Segment;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.RequestHandler;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.GetPeople;
import dk.shape.churchdesk.request.GetSegments;
import dk.shape.churchdesk.util.AccountUtils;
import dk.shape.churchdesk.util.MavenPro;
import dk.shape.churchdesk.view.BaseFrameLayout;
import dk.shape.churchdesk.view.DashboardView;
import dk.shape.churchdesk.view.RefreshLoadMoreView;
import dk.shape.churchdesk.viewmodel.BaseDashboardViewModel;
import dk.shape.churchdesk.viewmodel.DashboardViewModel;
import dk.shape.churchdesk.viewmodel.PeopleItemViewModel;
import dk.shape.churchdesk.viewmodel.PeopleViewModel;
import dk.shape.churchdesk.viewmodel.SegmentItemViewModel;
import dk.shape.churchdesk.viewmodel.SegmentsViewModel;
import io.intercom.android.sdk.Intercom;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class People extends PeopleFloatingButtonFragment {

    private enum RequestTypes {
        PEOPLE, SEGMENTS
    }
    public String selectedOrganizationId;
    private static final int TAB_1 = 0;
    private static final int TAB_2 = 1;

    private PeoplePagerAdapter _adapter;
    private HashMap<Integer, Pair<RefreshLoadMoreView, BaseDashboardViewModel>> mTabs = new HashMap<>();
    private DashboardView _peopleDashboardView;
    private DashboardViewModel _dashboardViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        selectedOrganizationId = prefs.getString("selectedOrgaziationIdForPeople", "");
        _adapter = new PeoplePagerAdapter(getActivity());
        _dashboardViewModel = new DashboardViewModel(_adapter);
        _peopleDashboardView = new DashboardView(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_people_select, menu);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    protected int getTitleResource() {
        return R.string.people;
    }

    @Override
    protected BaseFrameLayout getContentView() {
        _dashboardViewModel.bind(_peopleDashboardView);
        return _peopleDashboardView;
    }

    private void loadPeople() {
        List<String> segments = new ArrayList<String>();
        new GetPeople(selectedOrganizationId, segments).withContext(getActivity())
                .setOnRequestListener(listener)
                .runAsync(RequestTypes.PEOPLE);
    }

    private void loadSegments() {
        new GetSegments(selectedOrganizationId).withContext(getActivity())
                .setOnRequestListener(listener)
                .runAsync(RequestTypes.SEGMENTS);
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
            if (result.statusCode == HttpStatus.SC_OK && result.response != null) {
                switch (RequestHandler.<RequestTypes>getRequestIdentifierFromId(id)) {
                    case PEOPLE: {
                        Pair<RefreshLoadMoreView, BaseDashboardViewModel> viewModelPair = mTabs.get(TAB_1);
                        BaseDashboardViewModel viewModel = viewModelPair.second;
                        viewModel.extBind(viewModelPair.first, (List<Event>) result.response);
                        viewModel.bind(viewModelPair.first);
                        break;
                    }
                    case SEGMENTS: {
                        Pair<RefreshLoadMoreView, BaseDashboardViewModel> viewModelPair = mTabs.get(TAB_2);
                        BaseDashboardViewModel viewModel = viewModelPair.second;
                        viewModel.extBind(viewModelPair.first, (List<Event>) result.response);
                        viewModel.bind(viewModelPair.first);
                        break;
                    }
                }
            }
        }

        @Override
        public void onProcessing() {
        }
    };

    private class PeoplePagerAdapter extends PagerAdapter {
        private Context _context;

        public PeoplePagerAdapter(Context context) {
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
                    resId = R.string.people;
                    break;
                case TAB_2:
                    resId = R.string.segments;
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
                        loadPeople();
                        viewModel = new PeopleViewModel(_user,
                                new BaseDashboardViewModel.OnRefreshData() {
                                    @Override
                                    public void onRefresh() {
                                        loadPeople();
                                    }
                                }, new PeopleItemViewModel.OnPersonClickListener() {
                            @Override
                            public void onClick(Person person) {
                                Bundle bundle = new Bundle();
                                bundle.putParcelable(PersonDetailsActivity.KEY_PERSON, Parcels.wrap(person));
                                showActivity(PersonDetailsActivity.class, true, bundle);
                                //show person details activity here
                            }
                        });
                        break;
                    case TAB_2:
                        loadSegments();
                        viewModel = new SegmentsViewModel(_user,
                                new BaseDashboardViewModel.OnRefreshData() {
                                    @Override
                                    public void onRefresh() {
                                        loadSegments();
                                    }
                                }, new SegmentItemViewModel.OnSegmentClickListener() {
                            @Override
                            public void onClick(Segment segment) {
                                Bundle bundle = new Bundle();
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
