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
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by steffenkarlsson on 16/03/15.
 */
public abstract class BaseActivity extends ActionBarActivity {

    @Optional
    @InjectView(R.id.toolbar)
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (showCancelButton()) {
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

    public void setActionBarTitle(int titleId) {
        TextView titleView = (TextView) toolbar.findViewById(R.id.toolbar_title);
        titleView.setText(titleId);
    }
}
