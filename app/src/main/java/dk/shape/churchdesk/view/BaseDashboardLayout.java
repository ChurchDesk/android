package dk.shape.churchdesk.view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;

import butterknife.InjectView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomTextView;

/**
 * Created by steffenkarlsson on 31/03/15.
 */
public abstract class BaseDashboardLayout extends BaseFrameLayout {

    @InjectView(R.id.empty_text_view)
    protected CustomTextView mEmptyView;

    @InjectView(R.id.data_view)
    protected View mDataView;

    public BaseDashboardLayout(Context context) {
        super(context);
    }

    public BaseDashboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setEmpty(@StringRes int emptyRes) {
        mDataView.setVisibility(View.GONE);
        mEmptyView.setText(emptyRes);
        mEmptyView.setVisibility(View.VISIBLE);
    }
}
