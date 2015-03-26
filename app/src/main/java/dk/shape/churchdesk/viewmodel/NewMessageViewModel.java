package dk.shape.churchdesk.viewmodel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import java.util.List;

import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Site;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.entity.resources.Group;
import dk.shape.churchdesk.util.DatabaseUtils;
import dk.shape.churchdesk.view.NewMessageView;
import dk.shape.churchdesk.view.SingleSelectDialog;
import dk.shape.churchdesk.view.SingleSelectListItemView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 26/03/15.
 */
public class NewMessageViewModel extends ViewModel<NewMessageView> {

    public interface SendOkayListener {
        void okay(boolean isOkay);
    }

    private final SendOkayListener mSendOkayListener;
    private final User mCurrentUser;
    private final boolean isSingleUser;

    private Context mContext;
    private List<Group> mGroups;
    private NewMessageView mNewMessageView;

    private Group mSelectedGroup;
    private Site mSelectedSite;

    public NewMessageViewModel(User currentUser, SendOkayListener listener) {
        this.mCurrentUser = currentUser;
        this.isSingleUser = mCurrentUser.isSingleUser();
        this.mSendOkayListener = listener;
    }

    @Override
    public void bind(NewMessageView newMessageView) {
        mContext = newMessageView.getContext();
        mNewMessageView = newMessageView;
        newMessageView.setState(isSingleUser);

        updateText(mCurrentUser.mSites.get(0));
    }

    private void updateText(Site site) {
        mSelectedSite = site;
        mGroups = DatabaseUtils.getInstance().getGroupsBySiteId(site.mSiteUrl);

        if (isSingleUser) {
            mNewMessageView.mWrapperNoSite.setOnClickListener(mOnGroupClickListener);
            mNewMessageView.mGroupTitle.setText("");
        } else {
            mNewMessageView.mSiteTitle.setText(site.mSiteName);
            mNewMessageView.mSiteGroupTitle.setText("");
            mNewMessageView.mWrapperGroupItem.setOnClickListener(mOnGroupClickListener);
            mNewMessageView.mWrapperSiteItem.setOnClickListener(mOnSiteClickListener);
        }
    }

    private View.OnClickListener mOnGroupClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final SingleSelectDialog dialog = new SingleSelectDialog(mContext,
                    new GroupListAdapter(), R.string.new_message_parish);
            dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dialog.dismiss();
                    mSelectedGroup = mGroups.get(position);
                    mNewMessageView.mSiteGroupTitle.setText(mSelectedGroup.mName);
                    mNewMessageView.mGroupTitle.setText(mSelectedGroup.mName);
                }
            });
            dialog.show();
        }
    };

    private View.OnClickListener mOnSiteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final SingleSelectDialog dialog = new SingleSelectDialog(mContext,
                    new SiteListAdapter(), R.string.new_message_parish);
            dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dialog.dismiss();
                    updateText(mCurrentUser.mSites.get(position));
                }
            });
            dialog.show();
        }
    };

    private class SiteListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mCurrentUser.mSites.size();
        }

        @Override
        public Object getItem(int position) {
            return mCurrentUser.mSites.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Site site = mCurrentUser.mSites.get(position);

            SingleSelectListItemView view = new SingleSelectListItemView(mContext);
            view.mItemTitle.setText(site.mSiteName);
            view.mItemSelected.setVisibility(
                    mSelectedSite != null && site.equals(mSelectedSite.mSiteUrl)
                            ? View.VISIBLE
                            : View.GONE);
            return view;
        }
    }

    private class GroupListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mGroups != null ? mGroups.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mGroups.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Group group = mGroups.get(position);

            SingleSelectListItemView view = new SingleSelectListItemView(mContext);
            view.mItemTitle.setText(group.mName);
            view.mItemSelected.setVisibility(
                    mSelectedGroup != null && group.equals(mSelectedGroup)
                            ? View.VISIBLE
                            : View.GONE);
            return view;
        }
    }
}
