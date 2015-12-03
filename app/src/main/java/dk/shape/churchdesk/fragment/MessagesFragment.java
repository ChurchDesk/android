package dk.shape.churchdesk.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;

import org.apache.http.HttpStatus;
import org.parceler.Parcels;

import java.util.Date;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import dk.shape.churchdesk.BaseFloatingButtonFragment;
import dk.shape.churchdesk.MessageActivity;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.StartActivity;
import dk.shape.churchdesk.entity.Message;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.RequestHandler;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.GetMessagesRequest;
import dk.shape.churchdesk.request.GetMessagesSearchRequest;
import dk.shape.churchdesk.request.GetUnreadMessagesRequest;
import dk.shape.churchdesk.request.MarkMessageAsReadRequest;
import dk.shape.churchdesk.util.AccountUtils;
import dk.shape.churchdesk.view.BaseFrameLayout;
import dk.shape.churchdesk.view.RefreshLoadMoreView;
import dk.shape.churchdesk.viewmodel.MessageItemViewModel;
import dk.shape.churchdesk.viewmodel.MessagesViewModel;
import io.intercom.android.sdk.Intercom;

/**
 * Created by steffenkarlsson on 17/03/15.
 */
public class MessagesFragment extends BaseFloatingButtonFragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

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

    private InputMethodManager _inputManager;

    private Crouton mCrouton;

    private static final Style INFINITE = new Style.Builder()
            .setBackgroundColor(R.color.background_blue)
            .setTextColor(android.R.color.white)
            .setHeightDimensionResId(R.dimen.crouton_height)
            .build();

    private static final Configuration CONFIGURATION_INFINITE = new Configuration.Builder()
            .setDuration(Configuration.DURATION_INFINITE)
            .build();

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

        _inputManager = ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE));
    }

    @Override
    public boolean onClose() {
        View v = getActivity().getCurrentFocus();
        if( v != null) {
            _inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        mSv.setQuery("", false);
        mSearchItem.collapseActionView();
        loadMessagesByDate(new Date(), RequestTypes.MESSAGES, "");
        
        return true;
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
        mSv.setOnCloseListener(this);
        mSv.setQueryHint(getResources().getString(R.string.messages_search_hint));

        TextView view = (TextView) mSv.findViewById(getIdentifier("search_src_text"));
        view.setHintTextColor(getResources().getColor(R.color.white));

        mSearchItem.setActionView(mSv);
        mSearchItem.getActionView().requestFocus();
        MenuItemCompat.setOnActionExpandListener(mSearchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                View v = getActivity().getCurrentFocus();
                if( v != null) {
                    _inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                mSv.setQuery("", false);
                loadMessagesByDate(new Date(), RequestTypes.MESSAGES, "");
                return true;
            }
        });
    }

    protected int getIdentifier(String literalId) {
        return getResources().getIdentifier(
                String.format("android:id/%s", literalId),
                null,
                null
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("Something pressed");
        if(item.equals(mFilterItem)){
            AlertDialog.Builder filterDialog = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            filterDialog.setTitle(R.string.messages_filter_title);
            filterDialog.setNegativeButton(R.string.messages_filter_button_negative,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mCrouton != null)
                                mCrouton.hide();
                            showUnread = false;
                            loadMessagesByDate(new Date(), RequestTypes.MESSAGES, searchQuery);
                        }
                    });
            filterDialog.setPositiveButton(R.string.messages_filter_button_positive,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showUnread = true;
                            mCrouton = Crouton.makeText(getActivity(),
                                    R.string.showing_unread_messages, INFINITE, view)
                                    .setConfiguration(CONFIGURATION_INFINITE)
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            mCrouton.hide();
                                            loadMessagesByDate(new Date(), RequestTypes.MESSAGES, searchQuery);
                                        }
                                    });
                            mCrouton.show();

                            loadUnreadMessages();
                        }
                    });
            filterDialog.show();
        } else if(item.equals(mSearchItem)){
            mSearchItem.expandActionView();
            mSv.setIconified(false);
            mSv.setOnCloseListener(this);
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
//                new MarkMessageAsReadRequest(message)
//                        .withContext(getActivity())
//                        .setOnRequestListener(listener)
//                        .runAsync(RequestTypes.READ_MESSAGE);

                Bundle extras = new Bundle();
                extras.putParcelable(MessageActivity.KEY_MESSAGE, Parcels.wrap(message));
                showActivity(MessageActivity.class, true, extras);
            }
        }, false);
        return view;
    }

    private void loadMessagesByDate(Date date, RequestTypes type) {
        view.setLoading(true);
        new GetMessagesRequest(date)
                .withContext(getActivity())
                .setOnRequestListener(listener)
                .runAsync(type);
    }

    private void loadMessagesByDate(Date date, RequestTypes type, String query) {
        view.setLoading(true);
        if(query.isEmpty()) {
            loadMessagesByDate(date, type);
        } else {
            new GetMessagesSearchRequest(date, query)
                    .withContext(getActivity())
                    .setOnRequestListener(listener)
                    .runAsync(type);
        }
    }

    private void loadUnreadMessages() {
        view.setLoading(true);
        new GetUnreadMessagesRequest()
                .withContext(getActivity())
                .setOnRequestListener(listener)
                .runAsync(RequestTypes.MESSAGES);
    }

    @Override
    protected void onUserAvailable() {
        Date date = new Date();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.edit().putLong("messagesTimestamp", date.getTime()).commit();
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
            if (errorCode == ErrorCode.INVALID_GRANT){
                AccountUtils.getInstance(getActivity()).clear();
                Intercom.client().reset();
                showActivity(StartActivity.class, false, null);
                getActivity().finish();
            }
        }

        @Override
        public void onSuccess(int id, Result result) {
            if (result.statusCode == HttpStatus.SC_OK
                    && result.response != null) {
                switch (RequestHandler.<RequestTypes>getRequestIdentifierFromId(id)) {
                    case MESSAGES:
                        viewModel.extBind(view, (List<Message>) result.response);
                        viewModel.bind(view);

                        if(((List<Message>) result.response).size()>0) {
                            lastMessageLoaded = ((List<Message>) result.response).get(0);
                        }
                        break;
                    case LOAD_MORE:
                        List<Message> resp = (List<Message>) result.response;
                        if(lastMessageLoaded != null && resp.size() > 0) {
                            if ((resp.get(0).equals(lastMessageLoaded))) {
                                resp.remove(0);
                            }
                        }
                        viewModel.newData(view, resp);
                        if(resp.size() > 0) {
                            lastMessageLoaded = resp.get(0);
                        }
                        break;
                    case READ_MESSAGE:
                        break;
                }
                view.setLoading(false);
            }
        }

        @Override
        public void onProcessing() {
        }
    };
}
