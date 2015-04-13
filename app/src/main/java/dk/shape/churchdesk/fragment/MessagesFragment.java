package dk.shape.churchdesk.fragment;

import android.os.Bundle;

import org.apache.http.HttpStatus;
import org.parceler.Parcels;

import java.util.Date;
import java.util.List;

import dk.shape.churchdesk.BaseFloatingButtonFragment;
import dk.shape.churchdesk.MessageActivity;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Message;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.RequestHandler;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.GetMessagesRequest;
import dk.shape.churchdesk.request.MarkMessageAsReadRequest;
import dk.shape.churchdesk.view.BaseFrameLayout;
import dk.shape.churchdesk.view.RefreshLoadMoreView;
import dk.shape.churchdesk.viewmodel.MessagesViewModel;
import dk.shape.churchdesk.viewmodel.MessageItemViewModel;

/**
 * Created by steffenkarlsson on 17/03/15.
 */
public class MessagesFragment extends BaseFloatingButtonFragment {

    private enum RequestTypes {
        MESSAGES, READ_MESSAGE, LOAD_MORE
    }

    private MessagesViewModel viewModel;
    private RefreshLoadMoreView view;

    @Override
    protected int getTitleResource() {
        return R.string.menu_messages;
    }

    @Override
    protected BaseFrameLayout getContentView() {
        view = new RefreshLoadMoreView(getActivity());
        viewModel = new MessagesViewModel(_user, mOnRefreshData, mOnLoadMoreData,
                new MessageItemViewModel.OnMessageClickListener() {
            @Override
            public void onClick(Message message) {
                new MarkMessageAsReadRequest(message)
                        .withContext(getActivity())
                        .setOnRequestListener(listener)
                        .runAsync(RequestTypes.READ_MESSAGE);

                Bundle extras = new Bundle();
                extras.putParcelable(MessageActivity.KEY_MESSAGE, Parcels.wrap(message));
                showActivity(MessageActivity.class, true, extras);
            }
        }, false);
        return view;
    }

    private void loadMessagesByDate(Date date, RequestTypes type) {
        new GetMessagesRequest(date)
                .withContext(getActivity())
                .setOnRequestListener(listener)
                .runAsync(type);
    }

    @Override
    protected void onUserAvailable() {
        super.onUserAvailable();

        loadMessagesByDate(new Date(), RequestTypes.MESSAGES);
    }

    private MessagesViewModel.OnRefreshData mOnRefreshData =
            new MessagesViewModel.OnRefreshData() {
        @Override
        public void onRefresh() {
            loadMessagesByDate(new Date(), RequestTypes.MESSAGES);
        }
    };

    private RefreshLoadMoreView.OnLoadMoreDataListener mOnLoadMoreData
            = new RefreshLoadMoreView.OnLoadMoreDataListener() {
        @Override
        public void onLoadMore() {
            loadMessagesByDate(viewModel.getNewestMessage().mLastActivity, RequestTypes.LOAD_MORE);
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
                        viewModel.extBind(view, (List<Message>) result.response);
                        viewModel.bind(view);
                        break;
                    case LOAD_MORE:
                        viewModel.newData(view, (List<Message>) result.response);
                        break;
                    case READ_MESSAGE:
                        break;
                }
            }
        }

        @Override
        public void onProcessing() {

        }
    };
}
