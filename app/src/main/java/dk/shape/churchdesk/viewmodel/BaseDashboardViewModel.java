package dk.shape.churchdesk.viewmodel;

import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.List;

import dk.shape.churchdesk.view.BaseDashboardLayout;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 30/03/15.
 */
public abstract class BaseDashboardViewModel<T extends BaseDashboardLayout, D extends List> extends ViewModel<T> {

    private final OnRefreshData mOnRefreshData;

    public interface OnRefreshData {
        void onRefresh();
    }

    public BaseDashboardViewModel(OnRefreshData onRefreshData) {
        this.mOnRefreshData = onRefreshData;
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
        } else
            bind(view);
    }

    public abstract @StringRes int getEmptyRes();
}
