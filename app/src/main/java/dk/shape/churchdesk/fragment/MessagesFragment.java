package dk.shape.churchdesk.fragment;

import org.apache.http.HttpStatus;

import java.util.List;

import dk.shape.churchdesk.BaseFloatingButtonFragment;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Message;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.GetMessagesRequest;
import dk.shape.churchdesk.view.BaseFrameLayout;
import dk.shape.churchdesk.view.MessageFragmentView;
import dk.shape.churchdesk.viewmodel.MessageFragmentViewModel;

/**
 * Created by steffenkarlsson on 17/03/15.
 */
public class MessagesFragment extends BaseFloatingButtonFragment {

    private MessageFragmentViewModel viewModel;
    private MessageFragmentView view;

    @Override
    protected int getTitleResource() {
        return R.string.menu_messages;
    }

    @Override
    protected BaseFrameLayout getContentView() {
        view = new MessageFragmentView(getActivity());
        viewModel = new MessageFragmentViewModel(_user, mOnRefreshData);
        return view;
    }

    @Override
    protected void onUserAvailable() {
        super.onUserAvailable();

        new GetMessagesRequest()
                .withContext(getActivity())
                .setOnRequestListener(listener)
                .runAsync();
    }

    private MessageFragmentViewModel.OnRefreshData mOnRefreshData =
            new MessageFragmentViewModel.OnRefreshData() {
        @Override
        public void onRefresh() {
            new GetMessagesRequest()
                    .withContext(getActivity())
                    .setOnRequestListener(listener)
                    .runAsync();
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
                viewModel.setData((List<Message>) result.response);
                viewModel.bind(view);
            }
        }

        @Override
        public void onProcessing() {

        }
    };
}
