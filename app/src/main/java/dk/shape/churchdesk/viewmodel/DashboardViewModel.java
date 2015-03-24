package dk.shape.churchdesk.viewmodel;

import android.widget.TabHost;

import dk.shape.churchdesk.view.DashboardView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 23/03/15.
 */
public class DashboardViewModel extends ViewModel<DashboardView> {

    private TabHost.OnTabChangeListener mOnTabChangeListener;

    public DashboardViewModel(TabHost.OnTabChangeListener onTabChangeListener) {
        this.mOnTabChangeListener = onTabChangeListener;
    }

    @Override
    public void bind(DashboardView dashboardView) {
        dashboardView.init(mOnTabChangeListener);
    }
}
