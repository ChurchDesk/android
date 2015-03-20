package dk.shape.churchdesk.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import dk.shape.churchdesk.util.NavigationDrawerMenuItem;
import dk.shape.churchdesk.view.NavigationDrawerItemView;
import dk.shape.churchdesk.viewmodel.NavigationDrawerItemViewModel;

/**
 * Created by steffenkarlsson on 16/03/15.
 */
public class NavigationDrawerAdapter extends BaseAdapter {

    private Context mContext;

    public NavigationDrawerAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return 4;
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
        NavigationDrawerItemView view = new NavigationDrawerItemView(mContext);
        NavigationDrawerItemViewModel viewModel = new NavigationDrawerItemViewModel(
                NavigationDrawerMenuItem.values()[position]);
        viewModel.bind(view);
        return view;
    }
}
