package dk.shape.churchdesk.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Person;
import dk.shape.churchdesk.entity.Segment;
import dk.shape.churchdesk.entity.Site;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.entity.resources.Group;
import dk.shape.churchdesk.request.CreateMessageRequest;
import dk.shape.churchdesk.request.CreatePeopleMessageRequest;
import dk.shape.churchdesk.request.CreatePersonRequest;
import dk.shape.churchdesk.request.GetPeople;
import dk.shape.churchdesk.util.DatabaseUtils;
import dk.shape.churchdesk.view.MultiSelectDialog;
import dk.shape.churchdesk.view.MultiSelectListItemView;
import dk.shape.churchdesk.view.NewMessageView;
import dk.shape.churchdesk.view.SingleSelectDialog;
import dk.shape.churchdesk.view.SingleSelectListItemView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 26/03/15.
 */
public class NewMessageViewModel extends ViewModel<NewMessageView> {

    public interface SendOkayListener {
        void okay(boolean isOkay, CreateMessageRequest.MessageParameter parameter);
    }
    public interface SendPeopleMessageOkayListener {
        void okay(boolean isOkay, CreatePeopleMessageRequest.MessageParameter parameter);
    }
    private final SendOkayListener mSendOkayListener;
    private final SendPeopleMessageOkayListener mSendPeopleMessageOkayListener;
    private final User mCurrentUser;
    private final boolean isSingleUser;

    private Context mContext;
    private List<Group> mGroups;
    private List<String> mFromOptions;
    private NewMessageView mNewMessageView;
    public String mMessageType;
    public String mSelectedOrganizationId;
    public List<Person> mPeopleList = new ArrayList<>();
    public List<Segment> mSegmentsList = new ArrayList<>();
    public List<Integer> mSelectedPeople = new ArrayList<>();
    public Boolean isPeople = false;

    private static Group mSelectedGroup;
    private static Site mSelectedSite;

    public NewMessageViewModel(User currentUser, SendOkayListener listener, SendPeopleMessageOkayListener peopleListener) {
        this.mCurrentUser = currentUser;
        this.isSingleUser = mCurrentUser.isSingleUser();
        this.mSendOkayListener = listener;
        this.mSendPeopleMessageOkayListener = peopleListener;
        mSelectedGroup = null;
        mSelectedSite = null;
    }

    @Override
    public void bind(NewMessageView newMessageView) {
        mContext = newMessageView.getContext();
        mNewMessageView = newMessageView;
        newMessageView.setState(isSingleUser);

        if (mMessageType.equals("message"))
            updateText(mCurrentUser.mSites.get(0));
        else updateTextforPeopleMessage();
    }

    private void updateText(Site site) {
        mSelectedSite = site;
        //Log.d("ERRORERROR", "updateText site: " + mSelectedSite + ", Group: " + mSelectedGroup);
        mGroups = DatabaseUtils.getInstance().getGroupsBySiteId(site.mSiteUrl, mCurrentUser);

        mNewMessageView.mMessageTitle.addTextChangedListener(mTextWatcher);
        mNewMessageView.mMessageBody.addTextChangedListener(mTextWatcher);

        if (mSelectedGroup == null)
            mNewMessageView.mGroupTitle.setText("");
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

    private void updateTextforPeopleMessage (){
        mNewMessageView.mSiteText.setText(R.string.new_people_message_To);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        mSelectedOrganizationId = prefs.getString("selectedOrgaziationIdForPeople", "");
        if (mMessageType.equals("sms")){
            mNewMessageView.mWrapperGroupItem.setVisibility(View.GONE);
            mNewMessageView.mMessageTitle.setVisibility(View.GONE);
            //updating top section height when there is no "From" section
            mNewMessageView.mWrapperSite.getLayoutParams().height = 176;
            mNewMessageView.mWrapperSite.requestLayout();
        } else {
            //mGroupText = From
            mNewMessageView.mGroupText.setText(R.string.new_people_email_From);
            mNewMessageView.mSiteGroupTitle.setText(mCurrentUser.mName);
            mFromOptions = new ArrayList<>();
            mFromOptions.add(mCurrentUser.mName);
            Site selectedSite = mCurrentUser.getSiteById(mSelectedOrganizationId);
            mFromOptions.add(selectedSite.mSiteName);
            mNewMessageView.mMessageTitle.setHint(R.string.new_people_email_Subject);
            mNewMessageView.mMessageTitle.addTextChangedListener(mTextWatcher);
            mNewMessageView.mWrapperGroupItem.setOnClickListener(mOnFromClickListener);
        }
        setToText();
        mNewMessageView.mMessageBody.addTextChangedListener(mTextWatcher);

        mNewMessageView.mWrapperSiteItem.setOnClickListener(mOnToClickListener);
    }
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mMessageType.equals("message"))
                validate("mTextWatcher");
            else validatePeopleMessage();
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

    private View.OnClickListener mOnGroupClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final SingleSelectDialog dialog = new SingleSelectDialog(mContext,
                    new GroupListAdapter(), R.string.new_message_group);
            dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dialog.dismiss();
                    mSelectedGroup = mGroups.get(position);
                    mNewMessageView.mSiteGroupTitle.setText(mSelectedGroup.mName);
                    mNewMessageView.mGroupTitle.setText(mSelectedGroup.mName);
                    validate("mOnGroupClickListener");
                }
            });
            dialog.show();
        }
    };

    private View.OnClickListener mOnFromClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final SingleSelectDialog dialog = new SingleSelectDialog(mContext,
                    new FromListAdapter(), R.string.new_people_email_From);
            dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dialog.dismiss();
                    mNewMessageView.mSiteGroupTitle.setText(mFromOptions.get(position));
                }
            });
            dialog.show();
        }
    };

    private View.OnClickListener mOnToClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mSelectedPeople.size() > 0) {
                if (isPeople)
                    showSelectPeopleDialog();
                else showSelectSegmentsDialog();
            } else {
                final SingleSelectDialog dialog = new SingleSelectDialog(mContext,
                        new SendToAdapter(), R.string.new_people_send_to_title);
                dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dialog.dismiss();
                        if (position == 0) {
                            //send to people selected
                            isPeople = true;
                            showSelectPeopleDialog();
                        } else {
                            //send to segments selected
                            isPeople = false;
                            showSelectSegmentsDialog();
                        }
                    }
                });
                dialog.show();
            }
        }
    };

    private void showSelectPeopleDialog(){
        final MultiSelectDialog dialog = new MultiSelectDialog(mContext,
                new PeopleListAdapter(), R.string.people);
        dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mSelectedPeople == null) {
                    mSelectedPeople = new ArrayList<>();
                }
                if (mSelectedPeople.contains(mPeopleList.get(position).mPeopleId)) {
                    mSelectedPeople.remove((Integer) mPeopleList.get(position).mPeopleId);
                } else {
                    mSelectedPeople.add(mPeopleList.get(position).mPeopleId);
                }
                setToText();
                ((MultiSelectListItemView) view).mItemSelected.setVisibility(
                        mSelectedPeople != null && mSelectedPeople.contains(mPeopleList.get(position).mPeopleId)
                                ? View.VISIBLE
                                : View.GONE);
                }
        });
        dialog.showCancelButton(false);
        dialog.setOnOKClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showSelectSegmentsDialog(){
        final MultiSelectDialog dialog = new MultiSelectDialog(mContext,
                new SegmentsListAdapter(), R.string.segments);
        dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mSelectedPeople == null) {
                    mSelectedPeople = new ArrayList<>();
                }
                if (mSelectedPeople.contains(mSegmentsList.get(position).mSegmentId)) {
                    mSelectedPeople.remove((Integer) mSegmentsList.get(position).mSegmentId);
                } else {
                    mSelectedPeople.add(mSegmentsList.get(position).mSegmentId);
                }
                setToText();
                ((MultiSelectListItemView) view).mItemSelected.setVisibility(
                        mSelectedPeople != null && mSelectedPeople.contains(mSegmentsList.get(position).mSegmentId)
                                ? View.VISIBLE
                                : View.GONE);
            }
        });
        dialog.showCancelButton(false);
        dialog.setOnOKClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void setToText(){
        if (mSelectedPeople.size() > 0)
            mNewMessageView.mSiteTitle.setText(String.valueOf(mSelectedPeople.size()));
        else mNewMessageView.mSiteTitle.setText("");
        validatePeopleMessage();
    }
    private View.OnClickListener mOnSiteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final SingleSelectDialog dialog = new SingleSelectDialog(mContext,
                    new SiteListAdapter(), R.string.new_message_parish);
            dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dialog.dismiss();
                    mSelectedGroup = null;
                    updateText(mCurrentUser.mSites.get(position));
                    validate("mOnSiteClickListener");
                }
            });
            dialog.show();
        }
    };
    private void validate(String tag) {
        //Log.d("ERRORERROR", tag + " site: " + mSelectedSite + ", Group: " + mSelectedGroup);
        String messageTitle = mNewMessageView.mMessageTitle.getText().toString();
        String messageBody = mNewMessageView.mMessageBody.getText().toString();
        boolean isOkay = mSelectedGroup != null
                && mSelectedSite != null
                && !messageBody.isEmpty()
                && !messageTitle.isEmpty();
        CreateMessageRequest.MessageParameter parameter = null;
        if (isOkay)
            parameter = new CreateMessageRequest.MessageParameter(
                    mSelectedSite.mSiteUrl, mSelectedGroup.id,
                    messageTitle, messageBody);
        mSendOkayListener.okay(isOkay, parameter);
    }

    private void validatePeopleMessage() {
        //Log.d("ERRORERROR", tag + " site: " + mSelectedSite + ", Group: " + mSelectedGroup);
        String messageTitle = mNewMessageView.mMessageTitle.getText().toString();
        String messageBody = String.valueOf(mNewMessageView.mMessageBody.getText());
        boolean isOkay = false;
        String from = "";
        if (mMessageType.equals("email")){
            isOkay = mSelectedPeople != null
                    && mSelectedPeople.size() != 0
                    && !messageBody.isEmpty()
                    && !messageTitle.isEmpty();
            if (mNewMessageView.mSiteGroupTitle.getText().toString().equals(mCurrentUser.mName))
                from = "user";
            else from = "church";
        }
        else {
            isOkay = mSelectedPeople != null
                    && mSelectedPeople.size() != 0
                    && !messageBody.isEmpty();
        }
        CreatePeopleMessageRequest.MessageParameter parameter = null;
        if (isOkay) {
            List<HashMap<String, String>> recepientArray = new ArrayList<>();
            for (int numberOfRecepients = 0; numberOfRecepients < mSelectedPeople.size(); numberOfRecepients ++){
                HashMap<String, String> tempDictionary = new HashMap<>();
                if (isPeople)
                    tempDictionary.put("group", "people");
                else
                    tempDictionary.put("group", "segments");
                tempDictionary.put("id",Integer.toString(mSelectedPeople.get(numberOfRecepients)));
                recepientArray.add(tempDictionary);
            }
            parameter = new CreatePeopleMessageRequest.MessageParameter(mSelectedOrganizationId, messageTitle, messageBody, from, recepientArray, mMessageType
                    );
        }
        mSendPeopleMessageOkayListener.okay(isOkay, parameter);
    }

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

    //for people message
    private class SendToAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0)
                return R.string.people;
            else return R.string.segments;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SingleSelectListItemView view = new SingleSelectListItemView(mContext);
            if (position == 0)
                view.mItemTitle.setText(R.string.people);
            else view.mItemTitle.setText(R.string.segments);
            view.mItemSelected.setVisibility(View.GONE);
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

    //for people message
    private class FromListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mFromOptions != null ? mFromOptions.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mFromOptions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String from = mFromOptions.get(position);

            SingleSelectListItemView view = new SingleSelectListItemView(mContext);
            view.mItemTitle.setText(from);
            view.mItemSelected.setVisibility(
                    View.GONE);
            return view;
        }
    }

    private class PeopleListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mPeopleList != null ? mPeopleList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mPeopleList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Person person = mPeopleList.get(position);
            MultiSelectListItemView view = new MultiSelectListItemView(mContext);
            view.mItemTitle.setText(person.mFullName);
            view.mItemSelected.setVisibility(
                    mSelectedPeople != null && mSelectedPeople.contains(person.mPeopleId)
                            ? View.VISIBLE
                            : View.GONE);
            if (mMessageType.equals("sms") && (person.mContact.get("phone") == null || person.mContact.get("phone").isEmpty())){
                view.mItemTitle.setTextColor(mContext.getResources().getColor(android.R.color.darker_gray));
                view.setClickable(true);
            }
            else if (mMessageType.equals("email") && (person.mEmail == null || person.mEmail.isEmpty())){
                view.mItemTitle.setTextColor(mContext.getResources().getColor(android.R.color.darker_gray));
                view.setClickable(true);
            }
            else view.setClickable(false);
            return view;
        }
    }

    private class SegmentsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mSegmentsList != null ? mSegmentsList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mSegmentsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Segment segment = mSegmentsList.get(position);
            MultiSelectListItemView view = new MultiSelectListItemView(mContext);
            view.mItemTitle.setText(segment.mName);
            view.mItemSelected.setVisibility(
                    mSelectedPeople != null && mSelectedPeople.contains(segment.mSegmentId)
                            ? View.VISIBLE
                            : View.GONE);
            return view;
        }
    }
}
