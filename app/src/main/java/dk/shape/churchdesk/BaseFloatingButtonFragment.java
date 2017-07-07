package dk.shape.churchdesk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import butterknife.InjectView;
import butterknife.OnClick;
import dk.shape.churchdesk.fragment.BaseFragment;
import dk.shape.churchdesk.view.BaseFrameLayout;

/**
 * Created by steffenkarlsson on 23/03/15.
 */
public abstract class BaseFloatingButtonFragment extends BaseFragment {
    private Context mContext;

    @InjectView(R.id.content)
    protected FrameLayout mContent;

    @InjectView(R.id.action_event)
    protected FloatingActionButton mActionEvent;

    @InjectView(R.id.action_message)
    protected FloatingActionButton mActionMessage;

    @InjectView(R.id.action_absence)
    protected FloatingActionButton mActionAbsence;

    @InjectView(R.id.multiple_actions)
    protected FloatingActionsMenu mActionsMenu;

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
        mContext = view.getContext();
    }

    @Override
    protected void onUserAvailable() {
        mActionEvent.setIconDrawable(resize(getActivity(),
                ContextCompat.getDrawable(getActivity(),R.drawable.create_event_square)));
        mActionMessage.setIconDrawable(resize(getActivity(),
                ContextCompat.getDrawable(getActivity(),R.drawable.create_message_square)));
        mActionAbsence.setIconDrawable(resize(getActivity(),
                ContextCompat.getDrawable(getActivity(),R.drawable.create_absence_square)));
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
        Intent intent = new Intent(mContext, NewMessageActivity.class);
            intent.putExtra("EXTRA_MESSAGE_TYPE", "message");
        startActivity(intent);
        mActionsMenu.collapse();
    }

    @OnClick(R.id.action_event)
    void onClickActionEvent() {
        showActivity(NewEventActivity.class, true, null);
        mActionsMenu.collapse();
    }

    @OnClick(R.id.action_absence)
    void onClickActionAbsence() {
        showActivity(NewAbsenceActivity.class, true, null);
        mActionsMenu.collapse();
    }

    protected abstract BaseFrameLayout getContentView();
}
