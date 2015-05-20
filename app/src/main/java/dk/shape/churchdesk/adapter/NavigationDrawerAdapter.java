package dk.shape.churchdesk.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.HashMap;
import java.util.List;

import dk.shape.churchdesk.util.NavigationDrawerMenuItem;
import dk.shape.churchdesk.view.NavigationDrawerItemView;
import dk.shape.churchdesk.viewmodel.NavigationDrawerItemViewModel;

/**
 * Created by steffenkarlsson on 16/03/15.
 */
public class NavigationDrawerAdapter extends BaseAdapter {

    private Context mContext;
    private NavigationDrawerItemViewModel.OnDrawerItemClick mOnDrawerItemClick;
    private HashMap<Integer, NavigationDrawerItemView> mView = new HashMap<>();

    public NavigationDrawerAdapter(Context context,
                                   NavigationDrawerItemViewModel.OnDrawerItemClick onClick) {
        this.mContext = context;
        this.mOnDrawerItemClick = onClick;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        return mView.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NavigationDrawerItemView view = mView.containsKey(position)
                ? mView.get(position)
                : new NavigationDrawerItemView(mContext);

        NavigationDrawerItemViewModel viewModel = new NavigationDrawerItemViewModel(
                NavigationDrawerMenuItem.values()[position], position, mOnDrawerItemClick);
        viewModel.bind(view);
        mView.put(position, view);
        return view;
    }
}
