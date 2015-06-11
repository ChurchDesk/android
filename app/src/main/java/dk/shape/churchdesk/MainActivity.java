package dk.shape.churchdesk;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import dk.shape.churchdesk.fragment.BaseFragment;
import dk.shape.churchdesk.fragment.CalendarFragment;
import dk.shape.churchdesk.fragment.DashboardFragment;
import dk.shape.churchdesk.fragment.MessagesFragment;
import dk.shape.churchdesk.fragment.NavigationDrawerFragment;
import dk.shape.churchdesk.fragment.SettingsFragment;
import dk.shape.churchdesk.util.NavigationDrawerMenuItem;

/**
 * Created by steffenkarlsson on 17/03/15.
 */
public class MainActivity extends BaseLoggedInActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    protected void onUserAvailable() {
        mNavigationDrawerFragment.setUser(_user);
        mNavigationDrawerFragment.onClickDefault();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected int getTitleResource() {
        return -1;
    }

    @Override
    public void onNavigationDrawerItemSelected(NavigationDrawerMenuItem menuItem) {
        if (mNavigationDrawerFragment != null) {
            BaseFragment fragment = null;
            switch (menuItem) {
                case DASHBOARD:
                    fragment = DashboardFragment.initialize(DashboardFragment.class, _user);
                    break;
                case MESSAGES:
                    fragment = MessagesFragment.initialize(MessagesFragment.class, _user);
                    break;
                case CALENDAR:
                    fragment = CalendarFragment.initialize(CalendarFragment.class, _user);
                    break;
                case SETTINGS:
                    fragment = SettingsFragment.initialize(SettingsFragment.class, _user);
                    break;
            }

            // update the main content by replacing fragments
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mNavigationDrawerFragment.onOptionsItemSelected(item)) {
            getTitleView().setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean showBackButton() {
        return false;
    }
}
