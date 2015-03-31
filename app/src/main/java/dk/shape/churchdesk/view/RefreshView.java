package dk.shape.churchdesk.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.widget.ListView;

import butterknife.InjectView;
import dk.shape.churchdesk.R;

/**
 * Created by steffenkarlsson on 24/03/15.
 */
public class RefreshView extends BaseDashboardLayout {

    @InjectView(R.id.data_view)
    public SwipeRefreshLayout swipeContainer;

    @InjectView(R.id.data_list)
    public ListView mMessageList;

    public RefreshView(Context context) {
        super(context);
    }

    public RefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_refresh_view;
    }
}
