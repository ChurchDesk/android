package dk.shape.churchdesk.viewmodel;

import android.view.View;

import dk.shape.churchdesk.util.NavigationDrawerMenuItem;
import dk.shape.churchdesk.view.NavigationDrawerItemView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 16/03/15.
 */
public class NavigationDrawerItemViewModel extends ViewModel<NavigationDrawerItemView> {

    public interface OnDrawerItemClick {
        void onClick(int position);
    }

    private NavigationDrawerMenuItem mMenuItem;
    private int mId;
    private OnDrawerItemClick mOnDrawerItemClick;

    public NavigationDrawerItemViewModel(NavigationDrawerMenuItem menuItem, int id,
                                         OnDrawerItemClick onDrawerItemClick) {
        this.mMenuItem = menuItem;
        this.mId = id;
        this.mOnDrawerItemClick = onDrawerItemClick;
    }

    @Override
    public void bind(final NavigationDrawerItemView navigationDrawerItemView) {
        navigationDrawerItemView.mTitle.setText(mMenuItem.mTitleRes);
        navigationDrawerItemView.mIcon.setBackgroundResource(mMenuItem.mIconRes);
        navigationDrawerItemView.mIcon.setTag(mMenuItem.mIconRes);
        navigationDrawerItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnDrawerItemClick.onClick(mId);
            }
        });
    }
}
