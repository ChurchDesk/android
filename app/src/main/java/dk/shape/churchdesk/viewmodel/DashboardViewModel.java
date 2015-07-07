package dk.shape.churchdesk.viewmodel;

import android.support.v4.view.PagerAdapter;

import dk.shape.churchdesk.view.DashboardView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 23/03/15.
 */
public class DashboardViewModel extends ViewModel<DashboardView> {

    private PagerAdapter mPagerAdapter;

    public DashboardViewModel(PagerAdapter pagerAdapter) {
        this.mPagerAdapter = pagerAdapter;
    }

    @Override
    public void bind(DashboardView dashboardView) {
        dashboardView.init(mPagerAdapter);
    }

}