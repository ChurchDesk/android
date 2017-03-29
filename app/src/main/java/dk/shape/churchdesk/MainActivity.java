package dk.shape.churchdesk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.shape.churchdesk.entity.Site;
import dk.shape.churchdesk.fragment.BaseFragment;
import dk.shape.churchdesk.fragment.CalendarFragment;
import dk.shape.churchdesk.fragment.DashboardFragment;
import dk.shape.churchdesk.fragment.MessagesFragment;
import dk.shape.churchdesk.fragment.NavigationDrawerFragment;
import dk.shape.churchdesk.fragment.People;
import dk.shape.churchdesk.fragment.SettingsFragment;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.HttpStatusCode;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.SendPushNotificationTokenRequest;
import dk.shape.churchdesk.request.UploadPicture;
import dk.shape.churchdesk.util.NavigationDrawerMenuItem;

import dk.shape.churchdesk.view.MultiSelectDialog;
import dk.shape.churchdesk.view.MultiSelectListItemView;
import dk.shape.churchdesk.view.SingleSelectDialog;
import dk.shape.churchdesk.view.SingleSelectListItemView;
import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.identity.Registration;

import static io.intercom.android.sdk.Bridge.getContext;

/**
 * Created by steffenkarlsson on 17/03/15.
 */
public class MainActivity extends BaseLoggedInActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private Map<NavigationDrawerMenuItem, BaseFragment> _fragments = new HashMap<>();

    private boolean isOrganizationSelected = false;

    private boolean isChosenCamera = false;
    private static int CAMERA_PIC_REQUEST = 56;
    private static Uri picUri = null;
    private String userId = "";
    private String firstOrganizationId;
    private String imagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,  mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
       mNavigationDrawerFragment.mProfileImage.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                   ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,  }, 0);
               } else {
                   showChooseImageDialog();
               }
           }
       });
    }

    @Override
    protected void onUserAvailable() {
        super.onUserAvailable();
        mNavigationDrawerFragment.setUser(_user);
        userId = _user.mUserId;
        List<Site> listOfOrganizations = _user.mSites;
        firstOrganizationId = listOfOrganizations.get(0).mSiteUrl;
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
            if (result.statusCode == HttpStatusCode.SC_CREATED)
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
    public void onNavigationDrawerItemSelected(final NavigationDrawerMenuItem menuItem) {
        Boolean isFrag = true;
        Boolean isPeople = false;
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
                    case PEOPLE:
                        isPeople = true;
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
                    case PEOPLE:
                        isPeople = true;
                        break;
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
            if (isPeople) {
                if (_user.mSites.size() > 1) {
                    if (isOrganizationSelected) {
                        isOrganizationSelected = false;
                        fragment = People.initialize(People.class, _user);
                    } else {
                        final MultiSelectDialog dialog = new MultiSelectDialog(MainActivity.this,
                                new OrganizationsListAdapter(), R.string.new_event_parish_chooser);
                        dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Site site = _user.mSites.get(position);
                                if (site.mPermissions.get("canAccessPeople")) {
                                    isOrganizationSelected = true;
                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                                    prefs.edit().putString("selectedOrgaziationIdForPeople", site.mSiteUrl).commit();
                                    onNavigationDrawerItemSelected(menuItem);
                                    dialog.dismiss();
                                } else {
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle(R.string.you_dont_have_necessary_role)
                                            .setMessage(R.string.ask_your_admin_for_people_access)
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            })
                                            .show();
                                }
                            }
                        });
                        dialog.showCancelButton(false);
                        dialog.showOkayButton(false);
                        dialog.show();
                        isFrag = false;
                    }
                } else {
                    Site site = _user.mSites.get(0);
                    if (site.mPermissions.get("canAccessPeople")) {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        prefs.edit().putString("selectedOrgaziationIdForPeople", site.mSiteUrl).commit();
                        fragment = People.initialize(People.class, _user);
                    } else {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(R.string.you_dont_have_necessary_role)
                                .setMessage(R.string.ask_your_admin_for_people_access)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .show();
                    }
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

    private void showChooseImageDialog() {
        final SingleSelectDialog dialog = new SingleSelectDialog(this,
                new ChooseImageAdapter(), R.string.choose_image_from);
        dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    isChosenCamera = false;
                    Intent galleryIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(galleryIntent, CAMERA_PIC_REQUEST);
                    dialog.dismiss();
                } else if (position == 1) {
                    isChosenCamera = true;
                    Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/picture.jpg";
                    File imageFile = new File(imageFilePath);
                    picUri = Uri.fromFile(imageFile); // convert path to Uri
                    camera.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                    startActivityForResult(camera, CAMERA_PIC_REQUEST);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    showChooseImageDialog();

                } else {
                    Toast.makeText(getApplicationContext(), "You have not granted permissions", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult  result = CropImage.getActivityResult(data);
            if (result == null) {
                picUri = null;
            }
            else {
                int loggedUser = Integer.parseInt(userId);
                new UploadPicture(loggedUser, firstOrganizationId, result.getUri().getPath())
                        .withContext(this)
                        .setOnRequestListener(profileImageListener)
                        .run();
                imagePath = result.getUri().getPath();
                isChosenCamera = false;
            }
        }

        if (requestCode == CAMERA_PIC_REQUEST && resultCode == Activity.RESULT_OK) {
            if (isChosenCamera == true) {
                Uri newUri = picUri;
                startCropImageActivity(newUri);
            }
            else if (isChosenCamera == false) {
                Uri imageUri = CropImage.getPickImageResultUri(getContext(), data);
                startCropImageActivity(imageUri);

            }
        }
        if (requestCode == CAMERA_PIC_REQUEST && resultCode == Activity.RESULT_CANCELED) {
            isChosenCamera = false;
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setFixAspectRatio(true)
                .start(this);
    }
    private class ChooseImageAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SingleSelectListItemView view = new SingleSelectListItemView(getContext());
            if (position == 0) {
                view.mItemTitle.setText(R.string.choose_from_library);
            }
            else if (position == 1) {
                view.mItemTitle.setText(R.string.choose_from_camera);
            }
            view.mItemSelected.setVisibility(
                    View.GONE);
            return view;
        }
    }

    private BaseRequest.OnRequestListener profileImageListener = new BaseRequest.OnRequestListener() {
        @Override
        public void onError(int id, ErrorCode errorCode) {
        }

        @Override
        public void onSuccess(int id, Result result) {


            File pictureFile = new File(imagePath);
            Picasso.with(getApplicationContext())
                    .load(pictureFile)
                    .into(mNavigationDrawerFragment.mProfileImage);
            if (result.statusCode == HttpStatusCode.SC_OK
                    || result.statusCode == HttpStatusCode.SC_CREATED
                    || result.statusCode == HttpStatusCode.SC_NO_CONTENT) {
            }
            }


        @Override
        public void onProcessing() {
        }
    };

    private class OrganizationsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return _user.mSites.size();
        }

        @Override
        public Object getItem(int position) {
            return _user.mSites.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MultiSelectListItemView view = new MultiSelectListItemView(MainActivity.this);
            Site site = _user.mSites.get(position);
            view.mItemTitle.setText(site.mSiteName);
            view.mItemSelected.setVisibility(
                    View.GONE);
            return view;
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
