package dk.shape.churchdesk.fragment;

import android.widget.TabHost;

import dk.shape.churchdesk.BaseFloatingButtonFragment;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.view.BaseFrameLayout;
import dk.shape.churchdesk.view.DashboardView;
import dk.shape.churchdesk.viewmodel.DashboardViewModel;

/**
 * Created by steffenkarlsson on 17/03/15.
 */
public class DashboardFragment extends BaseFloatingButtonFragment {

    @Override
    protected int getTitleResource() {
        return R.string.menu_dashboard;
    }

    @Override
    protected BaseFrameLayout getContentView() {
        DashboardView view = new DashboardView(getActivity());
        DashboardViewModel viewModel = new DashboardViewModel(mOnTabChangeListener);
        viewModel.bind(view);
        return view;
    }

    private TabHost.OnTabChangeListener mOnTabChangeListener = new TabHost.OnTabChangeListener() {
        @Override
        public void onTabChanged(String tabId) {

        }
    };
}
