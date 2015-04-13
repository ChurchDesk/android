package dk.shape.churchdesk.viewmodel;

import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.List;

import dk.shape.churchdesk.view.RefreshLoadMoreView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 30/03/15.
 */
public abstract class BaseDashboardViewModel<T extends RefreshLoadMoreView, D extends List> extends ViewModel<T> {

    private final OnRefreshData mOnRefreshData;

    private RefreshLoadMoreView.OnLoadMoreDataListener mOnLoadMoreDataListener;

    public interface OnRefreshData {
        void onRefresh();
    }

    public BaseDashboardViewModel(OnRefreshData onRefreshData) {
        this.mOnRefreshData = onRefreshData;
    }

    public BaseDashboardViewModel(OnRefreshData onRefreshData,
                                  RefreshLoadMoreView.OnLoadMoreDataListener onLoadMoreDataListener) {
        this.mOnRefreshData = onRefreshData;
        this.mOnLoadMoreDataListener = onLoadMoreDataListener;
    }

    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mOnRefreshData.onRefresh();
                }
            };

    public void extBind(T view, D data) {
        if (data == null || data.isEmpty()) {
            view.setEmpty(getEmptyRes());
        } else {
            if (mOnLoadMoreDataListener != null)
                view.setLoadMoreListener(mOnLoadMoreDataListener);
            bind(view);
        }
    }

    public void newData(T view, D data) {
        view.setLoading(false);
        if (data == null || data.isEmpty()) {
            view.setLoadMoreListener(null);
        }
        // TO BE IMPLEMENTED IN CHILDREN
    }

    public abstract @StringRes int getEmptyRes();
}
