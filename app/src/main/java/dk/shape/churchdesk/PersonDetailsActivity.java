package dk.shape.churchdesk;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import org.parceler.Parcels;

import butterknife.InjectView;
import dk.shape.churchdesk.entity.Person;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.HttpStatusCode;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.GetSinglePersonRequest;
import dk.shape.churchdesk.view.PersonDetailsView;
import dk.shape.churchdesk.viewmodel.PersonDetailsViewModel;

/**
 * Created by chirag on 23/02/2017.
 */
public class PersonDetailsActivity extends BaseLoggedInActivity {

    private MenuItem mMenuEditPerson;

    private int mPersonId;
    private String mOrganizationId;
    private Person _person;

    public static final String KEY_PERSON = "KEY_PERSON";

    @InjectView(R.id.content_view)
    protected PersonDetailsView mContentView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_edit, menu);
        mMenuEditPerson = menu.findItem(R.id.menu_event_edit);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_event_edit:
                //TODO: Link to edit event
                Bundle bundle = new Bundle();
                    bundle.putParcelable(NewPersonActivity.KEY_PERSON_EDIT, Parcels.wrap(_person));
                    Intent i = this.getActivityIntent(PersonDetailsActivity.this, NewPersonActivity.class, bundle);
                    startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        _person = Parcels.unwrap(extras.getParcelable(KEY_PERSON));
        mPersonId = _person.mPeopleId;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(PersonDetailsActivity.this);
        mOrganizationId = prefs.getString("selectedOrgaziationIdForPeople", "");

        mContentView.mHomePhoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PersonDetailsActivity.this, new String[] {Manifest.permission.CALL_PHONE  }, 0);
                } else {
                    makeCall(_person.mContact.get("homePhone"));
                }
            }
        });

        mContentView.mWorkPhoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PersonDetailsActivity.this, new String[] {Manifest.permission.CALL_PHONE  }, 1);
                } else {
                    makeCall(_person.mContact.get("workPhone"));
                }
            }
        });

        mContentView.mPhoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PersonDetailsActivity.this, new String[] {Manifest.permission.CALL_PHONE  }, 2);
                } else {
                    makeCall(_person.mContact.get("phone"));
                }
            }
        });
        return;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //make call
                    makeCall(_person.mContact.get("homePhone"));
                } else {
                    Toast.makeText(getApplicationContext(), "You have not granted permission", Toast.LENGTH_LONG).show();
                }

            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //make call
                    makeCall(_person.mContact.get("workPhone"));
                } else {
                    Toast.makeText(getApplicationContext(), "You have not granted permission", Toast.LENGTH_LONG).show();
                }

            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //make call
                    makeCall(_person.mContact.get("phone"));
                } else {
                    Toast.makeText(getApplicationContext(), "You have not granted permission", Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProgressDialog("Loading information", true);
        new GetSinglePersonRequest(mPersonId, mOrganizationId)
                .withContext(this)
                .setOnRequestListener(listener)
                .run();
    }

    private void makeCall(String phoneNumber) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(intent);
            return;
        }
        else {
            Toast.makeText(getApplicationContext(), "there is clearly a big problem", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onUserAvailable() {
        super.onUserAvailable();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_person_details;
    }

    @Override
    protected int getTitleResource() {
            return R.string.person_details_title;
    }

    @Override
    protected boolean showBackButton() {
        return true;
    }

    private BaseRequest.OnRequestListener listener = new BaseRequest.OnRequestListener() {
        @Override
        public void onError(int id, ErrorCode errorCode) {
            dismissProgressDialog();
        }

        @Override
        public void onSuccess(int id, Result result) {
            if (result.statusCode == HttpStatusCode.SC_OK
                    && result.response != null) {
                _person = (Person) result.response;
                mMenuEditPerson.setVisible(true);

                PersonDetailsViewModel viewModel = new PersonDetailsViewModel(_user, _person);
                viewModel.bind(mContentView);
            }
            dismissProgressDialog();
        }
        @Override
        public void onProcessing() {
        }
    };
}
