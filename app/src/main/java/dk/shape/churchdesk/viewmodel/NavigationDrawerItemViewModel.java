package dk.shape.churchdesk.viewmodel;

import dk.shape.churchdesk.util.NavigationDrawerMenuItem;
import dk.shape.churchdesk.view.NavigationDrawerItemView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 16/03/15.
 */
public class NavigationDrawerItemViewModel extends ViewModel<NavigationDrawerItemView> {

    private NavigationDrawerMenuItem mMenuItem;

    public NavigationDrawerItemViewModel(NavigationDrawerMenuItem menuItem) {
        this.mMenuItem = menuItem;
    }

    @Override
    public void bind(NavigationDrawerItemView navigationDrawerItemView) {
        navigationDrawerItemView.mTitle.setText(mMenuItem.mTitleRes);
        navigationDrawerItemView.mIcon.setBackgroundResource(mMenuItem.mIconRes);
    }
}
