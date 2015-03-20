package dk.shape.churchdesk.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import dk.shape.churchdesk.BaseActivity;

/**
 * Created by steffenkarlsson on 17/03/15.
 */
public abstract class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BaseActivity) getActivity()).setTitle(getTitleResource());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutResource(), container, false);
        ButterKnife.inject(this, rootView);
        onCreateView(rootView);
        return rootView;
    }

    @StringRes
    protected abstract int getTitleResource();

    @LayoutRes
    protected abstract int getLayoutResource();

    protected abstract void onCreateView(View rootView);
}
