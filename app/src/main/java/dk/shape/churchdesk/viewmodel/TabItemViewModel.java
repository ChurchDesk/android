package dk.shape.churchdesk.viewmodel;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import dk.shape.churchdesk.view.TabItemView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 17/03/15.
 */
public class TabItemViewModel extends ViewModel<TabItemView> {

    @StringRes
    private int mTitleRes;

    @DrawableRes
    private int mIconRes;

    public TabItemViewModel(int titleRes, int iconRes) {
        this.mTitleRes = titleRes;
        this.mIconRes = iconRes;
    }

    @Override
    public void bind(TabItemView tabItemView) {
        tabItemView.tabIcon.setBackgroundDrawable(
                tabItemView.getResources().getDrawable(mIconRes));
//        tabItemView.tabTitle.setText(mTitleRes);
    }
}
