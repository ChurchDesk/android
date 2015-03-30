package dk.shape.churchdesk.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import butterknife.ButterKnife;
import dk.shape.churchdesk.BaseActivity;
import dk.shape.churchdesk.BaseLoggedInActivity;
import dk.shape.churchdesk.entity.User;

/**
 * Created by steffenkarlsson on 17/03/15.
 */
public abstract class BaseFragment extends Fragment {

    private static final String KEY_USER = "KEY_USER";

    protected User _user;

    public static <T extends BaseFragment> BaseFragment initialize(Class<T> clzz, User user) {
        try {
            BaseFragment fragment = clzz.newInstance();
            Bundle args = new Bundle();
            args.putParcelable(KEY_USER, Parcels.wrap(user));
            fragment.setArguments(args);
            return fragment;
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (!args.containsKey(KEY_USER))
            return;

        _user = Parcels.unwrap(args.getParcelable(KEY_USER));
        ((BaseActivity) getActivity()).setActionBarTitle(getTitleResource());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutResource(), container, false);
        ButterKnife.inject(this, rootView);
        onCreateView(rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (_user != null)
            onUserAvailable();
    }

    @StringRes
    protected abstract int getTitleResource();

    @LayoutRes
    protected abstract int getLayoutResource();

    protected abstract void onUserAvailable();

    protected abstract void onCreateView(View rootView);

    protected void showActivity(Class clazz, boolean keepUser, Bundle extras) {
        BaseActivity activity = (BaseActivity) getActivity();
        if (keepUser) {
            if (extras == null)
                extras = new Bundle();
            extras.putParcelable(BaseLoggedInActivity.KEY_USER, Parcels.wrap(_user));
        }

        Intent intent;
        if (extras != null)
            intent = activity.getActivityIntent(activity, clazz, extras);
        else
            intent = activity.getActivityIntent(activity, clazz);

        activity.startActivity(intent);
    }
}
