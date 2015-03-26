package dk.shape.churchdesk;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
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

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_floating;
    }

    @Override
    protected void onCreateView(View rootView) {
        mContent.addView(getContentView());
    }

    @Override
    protected void onUserAvailable() {
        mActionEvent.setIconDrawable(resize(getResources().getDrawable(R.drawable.create_event_square)));
        mActionMessage.setIconDrawable(resize(getResources().getDrawable(R.drawable.create_message_square)));
    }

    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        int bWidth = b.getWidth();
        float factor = getResources().getDimension(R.dimen.fib_icon) / bWidth;
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, Math.round(bWidth * factor),
                Math.round(b.getHeight() * factor), false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }

    @OnClick(R.id.action_message)
    void onClickActionMessage() {
        Log.d("ERRORERROR", "onClickActionMessage");
    }

    @OnClick(R.id.action_event)
    void onClickActionEvent() {
        Log.d("ERRORERROR", "onClickActionEvent");
    }

    protected abstract BaseFrameLayout getContentView();
}
