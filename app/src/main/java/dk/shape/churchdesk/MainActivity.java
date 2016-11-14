package dk.shape.churchdesk;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpStatus;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dk.shape.churchdesk.fragment.BaseFragment;
import dk.shape.churchdesk.fragment.CalendarFragment;
import dk.shape.churchdesk.fragment.DashboardFragment;
import dk.shape.churchdesk.fragment.MessagesFragment;
import dk.shape.churchdesk.fragment.NavigationDrawerFragment;
import dk.shape.churchdesk.fragment.SettingsFragment;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.SendPushNotificationTokenRequest;
import dk.shape.churchdesk.util.AccountUtils;
import dk.shape.churchdesk.util.NavigationDrawerMenuItem;

import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.identity.Registration;

/**
 * Created by steffenkarlsson on 17/03/15.
 */
public class MainActivity extends BaseLoggedInActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private Map<NavigationDrawerMenuItem, BaseFragment> _fragments = new HashMap<>();

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
        super.onUserAvailable();
        mNavigationDrawerFragment.setUser(_user);
        Intercom.client().registerIdentifiedUser(new Registration().withEmail(_user.mEmail));

        if (!handlePushNotification(getIntent().getExtras()))
            mNavigationDrawerFragment.onClickDefault();
        else
            getIntent().removeExtra("type");

        if (!((CustomApplication)getApplication()).hasSendRegistrationId && checkPlayServices())
            registerGCM();
        else
            Log.i("ChurchDesk", "No valid Google Play Services APK found.");
    }

    private boolean handlePushNotification(Bundle args) {
        if (args != null) {
            switch (args.getString("type", "")) {
                case "message":
                    startActivity(getActivityIntent(this, MessageActivity.class, args));
                    break;
                case "bookingCreated":
                case "getBookingCreated":
                case "bookingUpdate":
                case "bookingCanceled":
                    startActivity(getActivityIntent(this, EventDetailsActivity.class, args));
                    break;
                default:
                    return false;
            }
            return true;
        }
        return false;
    }

    private void registerGCM() {
        new AsyncTask<Void, Integer, Object>() {
            @Override
            protected Object doInBackground(Void... params) {
                try {
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    String regid = gcm.register(getString(R.string.gcm_project_number));
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    prefs.edit().putString("deviceToken", regid).commit();
                    Intercom.client().setupGCM(regid, R.drawable.login_logo);
                    new SendPushNotificationTokenRequest(regid, "prod")
                            .shouldReturnData()
                            .withContext(MainActivity.this)
                            .setOnRequestListener(listener)
                            .runAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(null, null, null);
    }

    private BaseRequest.OnRequestListener listener = new BaseRequest.OnRequestListener() {
        @Override
        public void onError(int id, ErrorCode errorCode) {
            if (errorCode == ErrorCode.INVALID_GRANT){
                goToLoginScreen();
            }
        }

        @Override
        public void onSuccess(int id, Result result) {
            if (result.statusCode == HttpStatus.SC_CREATED)
                ((CustomApplication)getApplication()).hasSendRegistrationId = true;
        }

        @Override
        public void onProcessing() { }
    };

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
        Boolean isFrag = true;
        if (mNavigationDrawerFragment != null) {
            BaseFragment fragment = null;

            if(_fragments.containsKey(menuItem)) {
                fragment = _fragments.get(menuItem);
                Date date = new Date();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                switch (menuItem) {
                    case CALENDAR:
                        boolean newEvent = prefs.getBoolean("newCalendarEvent", false);
                        long calendarMillis = prefs.getLong("calendarTimestamp", 0L);
                        Date eventsTimestamp = new Date(calendarMillis);

                        long mills = date.getTime() - eventsTimestamp.getTime();
                        long Mins = mills / (1000*60);
                        if (newEvent || (Mins > 0)){
                            prefs.edit().putBoolean("newCalendarEvent", false).apply();
                            prefs.edit().putBoolean("isLoaded", false).apply();
                            fragment = CalendarFragment.initialize(CalendarFragment.class, _user);
                        }
                        break;
                    case SUPPORT:
                        isFrag = false;
                        break;
                    /*case MESSAGES:
                        boolean newMessage = prefs.getBoolean("newMessage", false);
                        long messageMillis = prefs.getLong("messagesTimestamp", 0L);
                        Date messagesTimestamp = new Date(messageMillis);
                        mills = date.getTime() - messagesTimestamp.getTime();
                        long mins = mills / (1000*60);
                        if (newMessage || (mins > 10)){
                            prefs.edit().putBoolean("newMessage", false).apply();
                            fragment = MessagesFragment.initialize(MessagesFragment.class, _user);
                        }
                        break;
                    */
                }
            } else {
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
                    /*case PEOPLE:
                    {
                        if (_user.mSites.size() > 1)
                            fragment = SettingsFragment.initialize(SettingsFragment.class, _user);
                        else fragment = SettingsFragment.initialize(SettingsFragment.class, _user);
                        break;
                    }*/
                    case SUPPORT:
                        isFrag = false;
                        Intercom.client().displayConversationsList();
                        break;
                    case SETTINGS:
                        fragment = SettingsFragment.initialize(SettingsFragment.class, _user);
                        break;
                }
                if (isFrag) {
                    _fragments.put(menuItem, fragment);
                }
            }

            // update the main content by replacing fragments
            if (isFrag) {
                FragmentManager fragmentManager = getFragmentManager();
                try{
                    fragmentManager.beginTransaction().replace(R.id.container, fragment, menuItem.name()).commit();
                } catch (IllegalStateException e){
                    onNavigationDrawerItemSelected(menuItem);
                }
            }
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
