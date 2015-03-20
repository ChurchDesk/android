package dk.shape.churchdesk.fragment;

import android.view.View;

import dk.shape.churchdesk.R;

/**
 * Created by steffenkarlsson on 17/03/15.
 */
public class CalendarFragment extends BaseFragment {

    @Override
    protected int getTitleResource() {
        return R.string.menu_calendar;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_calendar;
    }

    @Override
    protected void onCreateView(View rootView) {

    }
}
