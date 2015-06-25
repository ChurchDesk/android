package dk.shape.churchdesk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;


/**
 * Created by steffenkarlsson on 16/03/15.
 */
public abstract class BaseActivity extends ActionBarActivity {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Optional
    @InjectView(R.id.toolbar)
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

        Crashlytics.start(this);
        ButterKnife.inject(this);
        if (showActionBar()) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");

            if (getTitleResource() > 0)
                setActionBarTitle(getTitleResource());

            if (showCancelButton()) {
                Drawable cancelButton = getResources().getDrawable(R.drawable.cross);
                assert cancelButton != null;
                cancelButton.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(cancelButton);
            }
            else
                getSupportActionBar().setDisplayShowHomeEnabled(true);

            getSupportActionBar().setHomeButtonEnabled(true);

            if (showBackButton()) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    public TextView getTitleView() {
        return (TextView)toolbar.findViewById(R.id.toolbar_title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (showCancelButton()) {
            finish();
            return true;
        } else if(showBackButton()){
            finish();
            return true;
        }
        return false;
    }

    protected void goToLoginScreen() {
        if (!(this instanceof StartActivity))
            startActivity(getActivityIntent(this, StartActivity.class));
    }

    public Intent getActivityIntent(Context context, Class clzz) {
        return getActivityIntent(context, clzz, null);
    }

    public Intent getActivityIntent(Context context, Class clzz, Bundle extras) {
        Intent intent = new Intent(context, clzz);
        if (extras != null)
            intent.putExtras(extras);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    protected boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("TIN", "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    @LayoutRes
    protected abstract int getLayoutResource();

    @StringRes
    protected abstract int getTitleResource();

    /**
     * This is the normal 'back' button on the ActionBar
     *
     * @return whether or not it should be shown
     */
    protected boolean showBackButton() {
        return true;
    }

    protected boolean showActionBar() {
        return true;
    }

    protected boolean showCancelButton() {
        return false;
    }

    public void setHasDrawable(final OnTitleClickListener onClickListener) {
        final TextView title = getTitleView();
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isSelected = !getTitleView().isSelected();
                title.setSelected(isSelected);
                if (onClickListener != null)
                    onClickListener.onClick(isSelected);
            }
        });
        title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.toolbar_rotator, 0);
        title.setCompoundDrawablePadding((int)getResources().getDimension(R.dimen.default_quarter_margin));
    }

    public interface OnTitleClickListener {
        void onClick(boolean isSelected);
    }

    public void setActionBarTitle(int titleId) {
        if (titleId != -1)
            getTitleView().setText(titleId);
    }

    public void setActionBarTitle(String title) {
        if (!getTitleView().getText().toString().equalsIgnoreCase(title)) {
            getTitleView().setText(title);
        }
    }
}
