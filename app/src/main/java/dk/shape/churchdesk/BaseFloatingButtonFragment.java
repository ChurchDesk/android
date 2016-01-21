package dk.shape.churchdesk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;

import butterknife.InjectView;
import butterknife.OnClick;
import dk.shape.churchdesk.fragment.BaseFragment;
import dk.shape.churchdesk.view.BaseFrameLayout;

/**
 * Created by steffenkarlsson on 23/03/15.
 */
public abstract class BaseFloatingButtonFragment extends BaseFragment {

    @InjectView(R.id.content)
    protected FrameLayout mContent;

    @InjectView(R.id.action_event)
    protected FloatingActionButton mActionEvent;

    @InjectView(R.id.action_message)
    protected FloatingActionButton mActionMessage;

    @InjectView(R.id.action_absence)
    protected FloatingActionButton mActionAbsence;

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
    }

    @Override
    protected void onUserAvailable() {
        mActionEvent.setIconDrawable(resize(getActivity(),
                getResources().getDrawable(R.drawable.create_event_square)));
        mActionMessage.setIconDrawable(resize(getActivity(),
                getResources().getDrawable(R.drawable.create_message_square)));
        mActionAbsence.setIconDrawable(resize(getActivity(),
                getResources().getDrawable(R.drawable.create_absence_square)));
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
        showActivity(NewMessageActivity.class, true, null);
    }

    @OnClick(R.id.action_event)
    void onClickActionEvent() {
        showActivity(NewEventActivity.class, true, null);
    }

    @OnClick(R.id.action_absence)
    void onClickActionAbsence() {
        showActivity(NewAbsenceActivity.class, true, null);
    }

    protected abstract BaseFrameLayout getContentView();
}
