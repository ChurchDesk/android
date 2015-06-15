package dk.shape.churchdesk.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

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
import dk.shape.churchdesk.request.GetMessagesSearchRequest;
import dk.shape.churchdesk.request.GetUnreadMessagesRequest;
import dk.shape.churchdesk.request.MarkMessageAsReadRequest;
import dk.shape.churchdesk.view.BaseFrameLayout;
import dk.shape.churchdesk.view.RefreshLoadMoreView;
import dk.shape.churchdesk.viewmodel.MessagesViewModel;
import dk.shape.churchdesk.viewmodel.MessageItemViewModel;

/**
 * Created by steffenkarlsson on 17/03/15.
 */
public class MessagesFragment extends BaseFloatingButtonFragment implements SearchView.OnQueryTextListener{

    private enum RequestTypes {
        MESSAGES, READ_MESSAGE, LOAD_MORE
    }

    private MessagesViewModel viewModel;
    private RefreshLoadMoreView view;
    private MenuItem mSearchItem;
    private SearchView mSv;
    private MenuItem mFilterItem;
    private String searchQuery = "";
    private boolean showUnread = false;
    private Message lastMessageLoaded;

    @Override
    public boolean onQueryTextSubmit(String query) {
        loadMessagesByDate(new Date(), RequestTypes.MESSAGES, query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchQuery = newText;
        if(newText.isEmpty())
            loadMessagesByDate(new Date(), RequestTypes.MESSAGES, newText);
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_messages, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mSearchItem = menu.findItem(R.id.menu_search);
        mFilterItem = menu.findItem(R.id.menu_filter);
        mSv = new SearchView(getActivity());
        mSv.setOnQueryTextListener(this);
        mSv.setQueryHint("Search messages");
        mSearchItem.setActionView(mSv);
        mSearchItem.getActionView().requestFocus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.equals(mFilterItem)){
            AlertDialog.Builder filterDialog = new AlertDialog.Builder(getActivity());
            filterDialog.setTitle("Choose filter");
            filterDialog.setNegativeButton("Show all",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showUnread = false;
                            loadMessagesByDate(new Date(), RequestTypes.MESSAGES, searchQuery);
                        }
                    });
            filterDialog.setPositiveButton("Show unread",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showUnread = true;
                            loadUnreadMessages();
                        }
                    });
            filterDialog.show();
        } else if(item.equals(mSearchItem)){
            mSearchItem.expandActionView();
            mSv.setIconified(false);
            ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        return super.onOptionsItemSelected(item);
    }



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

    private void loadMessagesByDate(Date date, RequestTypes type, String query) {
        if(query.isEmpty()) {
            loadMessagesByDate(date, type);
        } else {
            new GetMessagesSearchRequest(date, query)
                    .withContext(getActivity())
                    .setOnRequestListener(listener)
                    .runAsync(type);
        }
    }

    private void loadUnreadMessages(){
        new GetUnreadMessagesRequest()
                .withContext(getActivity())
                .setOnRequestListener(listener)
                .runAsync(RequestTypes.MESSAGES);
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
            if(showUnread){
                loadUnreadMessages();
            } else {
                loadMessagesByDate(new Date(), RequestTypes.MESSAGES, searchQuery);
            }
        }
    };

    private RefreshLoadMoreView.OnLoadMoreDataListener mOnLoadMoreData
            = new RefreshLoadMoreView.OnLoadMoreDataListener() {
        @Override
        public void onLoadMore() {
            if(!showUnread) {
                loadMessagesByDate(viewModel.getOldestMessage() == null ? new Date() : viewModel.getOldestMessage().mLastActivity, RequestTypes.LOAD_MORE, searchQuery);
            }
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
                        if(result.response != null && ((List<Message>) result.response).size()>0) {
                            lastMessageLoaded = ((List<Message>) result.response).get(0);
                        }
                        break;
                    case LOAD_MORE:
                        List<Message> resp = (List<Message>) result.response;
                        if(lastMessageLoaded != null && (resp.get(0).equals(lastMessageLoaded))) {
                            resp.remove(0);
                        }
                        viewModel.newData(view, resp);
                        if(result.response != null && resp.size() > 0) {
                            lastMessageLoaded = resp.get(0);
                        }
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
