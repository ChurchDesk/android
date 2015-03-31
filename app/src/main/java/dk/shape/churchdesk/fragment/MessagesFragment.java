package dk.shape.churchdesk.fragment;

import android.os.Bundle;

import org.apache.http.HttpStatus;
import org.parceler.Parcels;

import java.util.List;

import dk.shape.churchdesk.BaseFloatingButtonFragment;
import dk.shape.churchdesk.MessageActivity;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Message;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.GetMessagesRequest;
import dk.shape.churchdesk.view.BaseFrameLayout;
import dk.shape.churchdesk.view.RefreshView;
import dk.shape.churchdesk.viewmodel.MessagesViewModel;
import dk.shape.churchdesk.viewmodel.MessageItemViewModel;

/**
 * Created by steffenkarlsson on 17/03/15.
 */
public class MessagesFragment extends BaseFloatingButtonFragment {

    private MessagesViewModel viewModel;
    private RefreshView view;

    @Override
    protected int getTitleResource() {
        return R.string.menu_messages;
    }

    @Override
    protected BaseFrameLayout getContentView() {
        view = new RefreshView(getActivity());
        viewModel = new MessagesViewModel(_user, mOnRefreshData,
                new MessageItemViewModel.OnMessageClickListener() {
            @Override
            public void onClick(Message message) {
                Bundle extras = new Bundle();
                extras.putParcelable(MessageActivity.KEY_MESSAGE, Parcels.wrap(message));
                showActivity(MessageActivity.class, true, extras);
            }
        }, false);
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

    private MessagesViewModel.OnRefreshData mOnRefreshData =
            new MessagesViewModel.OnRefreshData() {
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
                viewModel.extBind(view, (List<Message>) result.response);
                viewModel.bind(view);
            }
        }

        @Override
        public void onProcessing() {

        }
    };
}
