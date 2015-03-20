package dk.shape.churchdesk.util;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import dk.shape.churchdesk.R;

/**
 * Created by steffenkarlsson on 16/03/15.
 */
public enum NavigationDrawerMenuItem {
    DASHBOARD(R.string.menu_dashboard, R.drawable.menu_dashboard),
    MESSAGES(R.string.menu_messages, R.drawable.menu_mail),
    CALENDAR(R.string.menu_calendar, R.drawable.menu_event),
    SETTINGS(R.string.menu_settings, R.drawable.menu_settings);

    @StringRes
    public int mTitleRes;

    @DrawableRes
    public int mIconRes;

    NavigationDrawerMenuItem(@StringRes int titleRes, @DrawableRes int iconRes) {
        this.mTitleRes = titleRes;
        this.mIconRes = iconRes;
    }
}
