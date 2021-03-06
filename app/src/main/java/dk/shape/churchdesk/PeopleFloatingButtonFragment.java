package dk.shape.churchdesk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import butterknife.InjectView;
import butterknife.OnClick;
import dk.shape.churchdesk.fragment.BaseFragment;
import dk.shape.churchdesk.view.BaseFrameLayout;
import dk.shape.churchdesk.view.SingleSelectDialog;
import dk.shape.churchdesk.view.SingleSelectListItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chirag on 17/02/2017.
 */
public abstract class PeopleFloatingButtonFragment extends BaseFragment {

    @InjectView(R.id.content)
    protected FrameLayout mContent;

    @InjectView(R.id.action_event)
    protected FloatingActionButton mActionAddPerson;

    @InjectView(R.id.action_message)
    protected FloatingActionButton mActionMessage;

    @InjectView(R.id.action_absence)
    protected FloatingActionButton mActionAbsence;

    @InjectView(R.id.multiple_actions)
    protected FloatingActionsMenu mActionsMenu;

    private Context mContext;
    private List<String> mMessagingOptions = new ArrayList<>();

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_floating;
    }

    @Override
    protected void onCreateView(View rootView) {
        BaseFrameLayout view = getContentView();
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null)
            parent.removeView(view);
        mContent.addView(view);
        mActionAbsence.setVisibility(View.INVISIBLE);
        mContext = view.getContext();

        //initialising messaging options list
        mMessagingOptions.add("Send an SMS");
        mMessagingOptions.add("Send an email");
    }

    @Override
    protected void onUserAvailable() {
        mActionAddPerson.setIconDrawable(resize(getActivity(),
                getResources().getDrawable(R.drawable.create_person_square)));
        mActionMessage.setIconDrawable(resize(getActivity(),
                getResources().getDrawable(R.drawable.create_message_square)));
    }


    public static Drawable resize(Context context, Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        int bWidth = b.getWidth();
        float factor = context.getResources().getDimension(R.dimen.fib_icon) / bWidth;
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, Math.round(bWidth * factor),
                Math.round(b.getHeight() * factor), false);
        return new BitmapDrawable(context.getResources(), bitmapResized);
    }

    @OnClick(R.id.action_message)
    void onClickActionMessage() {
        final SingleSelectDialog dialog = new SingleSelectDialog(mContext,
                new OptionsListAdapter(), R.string.people_choose_message_type);
        dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                Intent intent = new Intent(mContext, NewMessageActivity.class);
                if (position == 0)
                    intent.putExtra("EXTRA_MESSAGE_TYPE", "sms");
                else
                    intent.putExtra("EXTRA_MESSAGE_TYPE", "email");
                startActivity(intent);
            }
        });
        dialog.show();
        mActionsMenu.collapse();
    }

    @OnClick(R.id.action_event)
    void onClickActionAddPerson() {
        showActivity(NewPersonActivity.class, true, null);
        mActionsMenu.collapse();
    }

    private class OptionsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object getItem(int position) {
            return mMessagingOptions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            SingleSelectListItemView view = new SingleSelectListItemView(mContext);
            view.mItemTitle.setText(mMessagingOptions.get(position));
            view.mItemSelected.setVisibility(
                    View.GONE);
            return view;
        }
    }

    protected abstract BaseFrameLayout getContentView();


}

