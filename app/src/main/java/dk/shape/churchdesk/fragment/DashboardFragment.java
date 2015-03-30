package dk.shape.churchdesk.fragment;

import android.util.Pair;
import android.widget.TabHost;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.List;

import dk.shape.churchdesk.BaseFloatingButtonFragment;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Message;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.RequestHandler;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.GetUnreadMessagesRequest;
import dk.shape.churchdesk.view.BaseFrameLayout;
import dk.shape.churchdesk.view.DashboardView;
import dk.shape.churchdesk.view.MessageFragmentView;
import dk.shape.churchdesk.viewmodel.DashboardViewModel;
import dk.shape.churchdesk.viewmodel.MessageFragmentViewModel;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 17/03/15.
 */
public class DashboardFragment extends BaseFloatingButtonFragment {

    private enum RequestTypes {
        EVENTS, INVITATIONS, MESSAGES
    }

    private static final String TAB_1 = "events";
    private static final String TAB_2 = "invitations";
    private static final String TAB_3 = "messages";

    private DashboardView mContentView;

    private HashMap<String, Pair<BaseFrameLayout, ViewModel>> mTabs = new HashMap<>();

    @Override
    protected int getTitleResource() {
        return R.string.menu_dashboard;
    }

    @Override
    protected BaseFrameLayout getContentView() {
        mContentView = new DashboardView(getActivity());
        DashboardViewModel viewModel = new DashboardViewModel(mOnTabChangeListener);
        viewModel.bind(mContentView);
        return mContentView;
    }

    private TabHost.OnTabChangeListener mOnTabChangeListener = new TabHost.OnTabChangeListener() {
        @Override
        public void onTabChanged(String tabId) {
            if (!tabId.equalsIgnoreCase(TAB_1)
                    && !tabId.equalsIgnoreCase(TAB_2)
                    && !tabId.equalsIgnoreCase(TAB_3))
                // SHOULD NEVER HAPPEN!
                return;
            mContentView.setContent(null);

            BaseFrameLayout view;
            ViewModel viewModel;
            if (mTabs.containsKey(tabId)) {
                Pair<BaseFrameLayout, ViewModel> viewModelPair = mTabs.get(tabId);
                view = viewModelPair.first;
                viewModel = viewModelPair.second;
                viewModel.bind(view);
                mContentView.setContent(view);
            } else {
                switch (tabId) {
                    case TAB_1:
                        break;
                    case TAB_2:
                        break;
                    case TAB_3:
                        loadMessages();
                        break;
                }
            }
        }
    };

    private void loadMessages() {
        new GetUnreadMessagesRequest()
                .withContext(getActivity())
                .setOnRequestListener(listener)
                .runAsync(RequestTypes.MESSAGES);
    }

    private MessageFragmentViewModel.OnRefreshData mOnRefreshData =
            new MessageFragmentViewModel.OnRefreshData() {
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
                        MessageFragmentView view = new MessageFragmentView(getActivity());
                        MessageFragmentViewModel viewModel = new MessageFragmentViewModel(_user, mOnRefreshData);
                        mTabs.put(TAB_3, new Pair<BaseFrameLayout, ViewModel>(view, viewModel));

                        viewModel.setData((List<Message>) result.response);
                        viewModel.bind(view);

                        mContentView.setContent(view);
                        break;
                }
            }
        }

        @Override
        public void onProcessing() {

        }
    };
}
