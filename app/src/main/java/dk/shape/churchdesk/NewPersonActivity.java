package dk.shape.churchdesk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.parceler.Parcels;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import butterknife.InjectView;
import dk.shape.churchdesk.entity.Person;
import dk.shape.churchdesk.entity.Site;
import dk.shape.churchdesk.entity.Tag;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.HttpStatusCode;
import dk.shape.churchdesk.network.RequestHandler;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.CreatePersonRequest;
import dk.shape.churchdesk.request.EditPersonRequest;
import dk.shape.churchdesk.request.GetTags;
import dk.shape.churchdesk.request.UploadPeoplesPicture;
import dk.shape.churchdesk.util.Validators;
import dk.shape.churchdesk.view.NewPersonView;
import dk.shape.churchdesk.view.SingleSelectDialog;
import dk.shape.churchdesk.view.SingleSelectListItemView;
import dk.shape.churchdesk.viewmodel.NewPersonViewModel;

import static com.theartofdev.edmodo.cropper.CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE;
import static io.intercom.android.sdk.Bridge.getContext;

/**
 * Created by chirag on 22/02/2017.
 */
public class NewPersonActivity extends BaseLoggedInActivity {

    private MenuItem mMenuCreatePerosn;
    private MenuItem mMenuSavePerson;
    private String selectedOrganizationId;
    private CreatePersonRequest.PersonParameter mPersonParameter;
    private NewPersonViewModel viewModel;
    public static String KEY_PERSON_EDIT = "KEY_EDIT_Person";
    Person _person;

    private boolean isChosenCamera = false;
    private static int CAMERA_PIC_REQUEST = 56;
    private static Uri picUri = null;
    private String imagePath = "";
    private HashMap<String, String> mPicture;
    private String firstOrganizationId;

    private enum RequestTypes {
        TAGS, CREATE_PERSON, EDIT_PERSON
    }

    @InjectView(R.id.content_view)
    protected NewPersonView mContentView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_add, menu);
        mMenuCreatePerosn = menu.findItem(R.id.menu_event_add);
        mMenuSavePerson = menu.findItem(R.id.menu_event_save);
        setEnabled(mMenuCreatePerosn, false);
        if (_person != null) {
            mMenuCreatePerosn.setVisible(false);
            mMenuSavePerson.setVisible(true);
        }

        mContentView.mProfileImage.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(NewPersonActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,  }, 0);
                } else {
                    showChooseImageDialog();
                }
                    }
                                               });
                    return true;
                 }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_event_add:
                createNewPerson();
                return true;
            case R.id.menu_event_save:
                editPerson();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createNewPerson() {
        if (mPersonParameter != null) {
            if ( mPersonParameter.mEmail != null && !mPersonParameter.mEmail.isEmpty() && !Validators.isValidEmail(mContentView.mPersonEmailChosen)){
                mContentView.mPersonEmailChosen.setError(getString(R.string.login_email_validation_error));
            }
            else {
                savePerson();
            }
        }
        Log.d("Person 1", "onClickAddPerson");
    }

    private void savePerson(){
        if (mPersonParameter != null) {
            if ( mPersonParameter.mEmail != null && !mPersonParameter.mEmail.isEmpty() && !Validators.isValidEmail(mContentView.mPersonEmailChosen)){
                mContentView.mPersonEmailChosen.setError(getString(R.string.login_email_validation_error));
            }
            else if (arePhoneNumbersValid()){
                mPersonParameter.mPictureUrl = mPicture;
        new CreatePersonRequest(mPersonParameter, selectedOrganizationId)
                .withContext(this)
                .setOnRequestListener(listener)
                .run(RequestTypes.CREATE_PERSON);
        setEnabled(mMenuCreatePerosn, false);
        showProgressDialog(R.string.new_person_create_progress, false);
            }
        }
    }

    private void editPerson() {
        if (mPersonParameter != null) {
            if ( mPersonParameter.mEmail != null && !mPersonParameter.mEmail.isEmpty() && !Validators.isValidEmail(mContentView.mPersonEmailChosen)){
                mContentView.mPersonEmailChosen.setError(getString(R.string.login_email_validation_error));
            }
            else if (arePhoneNumbersValid()){
                mPersonParameter.mPictureUrl = mPicture;
                new EditPersonRequest(_person.mPeopleId, selectedOrganizationId, mPersonParameter)
                        .withContext(this)
                        .setOnRequestListener(listener)
                        .run(RequestTypes.EDIT_PERSON);
                setEnabled(mMenuSavePerson, false);
                showProgressDialog(R.string.edit_person_edit_progress, false);
            }
        }
    }
    private boolean arePhoneNumbersValid(){
        boolean tempValid = true;
        if (mPersonParameter.mContact.get("phone") != null && !mPersonParameter.mContact.get("phone").isEmpty()){
            if (!mPersonParameter.mContact.get("phone").substring(0,1).equals("+")){
                tempValid = false;
                mContentView.mMobilePhoneChosen.setError(getString(R.string.phone_country_code_missing_error));
            }else if (!Validators.isValidPhone(mContentView.mMobilePhoneChosen)){
                tempValid = false;
                mContentView.mMobilePhoneChosen.setError(getString(R.string.phone_validation_error));
            }
        }
        if (mPersonParameter.mContact.get("homePhone") != null && !mPersonParameter.mContact.get("homePhone").isEmpty()){
            if (!mPersonParameter.mContact.get("homePhone").substring(0,1).equals("+")){
                tempValid = false;
                mContentView.mHomePhoneChosen.setError(getString(R.string.phone_country_code_missing_error));
            }else if (!Validators.isValidPhone(mContentView.mHomePhoneChosen)){
                tempValid = false;
                mContentView.mHomePhoneChosen.setError(getString(R.string.phone_validation_error));
            }
        }
        if (mPersonParameter.mContact.get("workPhone") != null && !mPersonParameter.mContact.get("workPhone").isEmpty()){
            if (!mPersonParameter.mContact.get("workPhone").substring(0,1).equals("+")){
                tempValid = false;
                mContentView.mWorkPhoneChosen.setError(getString(R.string.phone_country_code_missing_error));
            }else if (!Validators.isValidPhone(mContentView.mWorkPhoneChosen)){
                tempValid = false;
                mContentView.mWorkPhoneChosen.setError(getString(R.string.phone_validation_error));
            }
        }
        return tempValid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        selectedOrganizationId = prefs.getString("selectedOrgaziationIdForPeople", "");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(KEY_PERSON_EDIT)) {
                _person = Parcels.unwrap(extras.getParcelable(KEY_PERSON_EDIT));
            }
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onUserAvailable() {
        super.onUserAvailable();
        viewModel = new NewPersonViewModel(mSendOKListener);
        viewModel.bind(mContentView);
        if (_person != null) {
            viewModel.setDataToEdit(_person);
        }
        loadTags();

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

    private void loadTags() {
        new GetTags(selectedOrganizationId).withContext(this)
                .setOnRequestListener(listener)
                .runAsync(RequestTypes.TAGS);
    }

    private NewPersonViewModel.SendOkayListener mSendOKListener = new NewPersonViewModel.SendOkayListener() {
        @Override
        public void okay(boolean isOkay, CreatePersonRequest.PersonParameter parameter) {
            setEnabled(mMenuCreatePerosn, isOkay);
            setEnabled(mMenuSavePerson, isOkay);
            if (isOkay) {
                mPersonParameter = parameter;
            }
        }
    };

    private void setEnabled(MenuItem item, boolean enabled) {
        item.setEnabled(enabled);
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
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PIC_REQUEST && resultCode == Activity.RESULT_OK) {
            if (isChosenCamera == true) {
                Uri newUri = picUri;
                startCropImageActivity(newUri);
            } else if (isChosenCamera == false) {
                Uri imageUri = CropImage.getPickImageResultUri(getContext(), data);
                startCropImageActivity(imageUri);
            }
        }
        if (requestCode == CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            List<Site> listOfOrganizations = _user.mSites;
            firstOrganizationId = listOfOrganizations.get(0).mSiteUrl;
            CropImage.ActivityResult  result = CropImage.getActivityResult(data);
            if (result == null) {
                //do nothing
            } else {
                new UploadPeoplesPicture(firstOrganizationId, result.getUri().getPath())
                        .shouldReturnData()
                        .withContext(NewPersonActivity.this)
                        .setOnRequestListener(peopleImageListener)
                        .run();
                imagePath = result.getUri().getPath();
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
                .start(NewPersonActivity.this);
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


    private BaseRequest.OnRequestListener peopleImageListener = new BaseRequest.OnRequestListener() {
        @Override
        public void onError(int id, ErrorCode errorCode) {

        }

        @Override
        public void onSuccess(int id, Result result) {
            mPicture =  (HashMap<String, String>) result.response;

            File pictureFile = new File(imagePath);
            Picasso.with(getApplicationContext())
                    .load(pictureFile)
                    .into(mContentView.mProfileImage);
            if (result.statusCode == HttpStatusCode.SC_OK
                    || result.statusCode == HttpStatusCode.SC_CREATED
                    || result.statusCode == HttpStatusCode.SC_NO_CONTENT) {
            }
        }

        @Override
        public void onProcessing() {
        }
    };

    private BaseRequest.OnRequestListener listener = new BaseRequest.OnRequestListener() {
        @Override
        public void onError(int id, ErrorCode errorCode) {
                dismissProgressDialog();
            if (errorCode == ErrorCode.BOOKING_CONFLICT){
                Toast.makeText(getApplicationContext(), _person == null ? R.string.create_person_conflict_error : R.string.create_person_conflict_error, Toast.LENGTH_SHORT).show();
                setEnabled(mMenuCreatePerosn, true);
            }
            else {
                Toast.makeText(getApplicationContext(), _person == null ? R.string.new_person_create_error : R.string.edit_person_edit_error, Toast.LENGTH_SHORT).show();
                setEnabled(mMenuCreatePerosn, true);
            }

        }

        @Override
        public void onSuccess(int id, Result result) {
            switch (RequestHandler.<RequestTypes>getRequestIdentifierFromId(id)) {
                case TAGS: {
                    viewModel.tags = (List<Tag>) result.response;
                    break;
                }
                default:{
                    if (result.statusCode == HttpStatusCode.SC_OK
                            || result.statusCode == HttpStatusCode.SC_CREATED
                            || result.statusCode == HttpStatusCode.SC_NO_CONTENT) {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(NewPersonActivity.this);
                        prefs.edit().putBoolean("newPerson", true).commit();
                        finish();
                    }
                    dismissProgressDialog();
                }
            }
        }

        @Override
        public void onProcessing() {
        }
    };


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_new_person;
    }

    @Override
    protected int getTitleResource() {
        return _person == null ? R.string.new_person_title : R.string.edit_person_title;
    }

    @Override
    protected boolean showCancelButton() {
        return true;
    }

    @Override
    protected boolean showBackButton() {
        return false;
    }

}



