package dk.shape.churchdesk.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;

import butterknife.InjectView;
import dk.shape.churchdesk.R;

/**
 * Created by steffenkarlsson on 24/03/15.
 */
public class RefreshLoadMoreView extends BaseDashboardLayout {

    private boolean mIsLoading;

    public interface OnLoadMoreDataListener {
        void onLoadMore();
    }

    @InjectView(R.id.data_view)
    public SwipeRefreshLayout swipeContainer;

    @InjectView(R.id.data_list)
    public ListView mDataList;

    public RefreshLoadMoreView(Context context) {
        super(context);
    }

    public RefreshLoadMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_refresh_view;
    }

    public void setLoadMoreListener(final OnLoadMoreDataListener onLoadMoreDataListener) {
        if (onLoadMoreDataListener == null) {
            mDataList.setOnScrollListener(null);
        } else {
            mDataList.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
                    boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount - 5;
                    if (loadMore && !mIsLoading) {
                        Log.d("ERRORERROR", "Load more data");
                        mIsLoading = true;
                        onLoadMoreDataListener.onLoadMore();
                    }
                }
            });
        }
    }

    public void setLoading(boolean isLoading) {
        this.mIsLoading = isLoading;
        swipeContainer.setRefreshing(isLoading);
    }
}
