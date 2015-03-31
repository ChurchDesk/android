package dk.shape.churchdesk.fragment;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import org.apache.http.HttpStatus;
import org.parceler.Parcels;

import java.util.HashMap;

import dk.shape.churchdesk.BaseFloatingButtonFragment;
import dk.shape.churchdesk.MessageActivity;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Message;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.RequestHandler;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.GetUnreadMessagesRequest;
import dk.shape.churchdesk.view.BaseFrameLayout;
import dk.shape.churchdesk.view.DashboardView;
import dk.shape.churchdesk.view.MessagesView;
import dk.shape.churchdesk.viewmodel.BaseDashboardViewModel;
import dk.shape.churchdesk.viewmodel.DashboardViewModel;
import dk.shape.churchdesk.viewmodel.MessagesViewModel;
import dk.shape.churchdesk.viewmodel.MessageItemViewModel;

/**
 * Created by steffenkarlsson on 17/03/15.
 */
public class DashboardFragment extends BaseFloatingButtonFragment {

    private enum RequestTypes {
        EVENTS, INVITATIONS, MESSAGES
    }

    private static final int TAB_1 = 0;
    private static final int TAB_2 = 1;
    private static final int TAB_3 = 2;

    private HashMap<Integer, Pair<BaseFrameLayout, BaseDashboardViewModel>> mTabs = new HashMap<>();

    @Override
    protected int getTitleResource() {
        return R.string.menu_dashboard;
    }

    @Override
    protected BaseFrameLayout getContentView() {
        DashboardView view = new DashboardView(getActivity());
        DashboardViewModel viewModel = new DashboardViewModel(new DashboardPagerAdapter());
        viewModel.bind(view);
        return view;
    }

    private void loadMessages() {
        new GetUnreadMessagesRequest()
                .withContext(getActivity())
                .setOnRequestListener(listener)
                .runAsync(RequestTypes.MESSAGES);
    }

    private MessagesViewModel.OnRefreshData mOnRefreshData =
            new MessagesViewModel.OnRefreshData() {
                @Override
                public void onRefresh() {
                    loadMessages();
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
                switch (RequestHandler.<RequestTypes>getRequestIdentifierFromId(id)) {
                    case MESSAGES:
                        Pair<BaseFrameLayout, BaseDashboardViewModel> viewModelPair = mTabs.get(TAB_3);
                        BaseDashboardViewModel viewModel = viewModelPair.second;
                        viewModel.setData(result.response);
                        viewModel.bind(viewModelPair.first);
                        break;
                }
            }
        }

        @Override
        public void onProcessing() {

        }
    };

    private class DashboardPagerAdapter extends PagerAdapter {

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
            switch (position) {
                case TAB_1:
                    return getString(R.string.dashboard_tab_1);
                case TAB_2:
                    return getString(R.string.dashboard_tab_2);
                case TAB_3:
                    return getString(R.string.dashboard_tab_3);
            }
            return "";
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            BaseFrameLayout view = null;
            BaseDashboardViewModel viewModel = null;

            if (mTabs.containsKey(position)) {
                Pair<BaseFrameLayout, BaseDashboardViewModel> viewModelPair = mTabs.get(position);
                view = viewModelPair.first;
                viewModel = viewModelPair.second;
                viewModel.bind(view);
            } else {
                switch (position) {
                    case TAB_1:
                        break;
                    case TAB_2:
                        break;
                    case TAB_3:
                        loadMessages();
                        view = new MessagesView(getActivity());
                        viewModel = new MessagesViewModel(_user, mOnRefreshData,
                                new MessageItemViewModel.OnMessageClickListener() {
                            @Override
                            public void onClick(Message message) {
                                Bundle extras = new Bundle();
                                extras.putParcelable(MessageActivity.KEY_MESSAGE, Parcels.wrap(message));
                                showActivity(MessageActivity.class, true, extras);
                            }
                        });
                        mTabs.put(TAB_3, new Pair<>(view, viewModel));
                        break;
                }
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
