package dk.shape.churchdesk.util;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import dk.shape.churchdesk.R;

/**
 * Created by steffenkarlsson on 16/03/15.
 */
public enum NavigationDrawerMenuItem {
    DASHBOARD(R.string.menu_dashboard, R.drawable.menu_dashboard, R.drawable.menu_dashboard_passive),
    MESSAGES(R.string.menu_messages, R.drawable.menu_mail, R.drawable.menu_mail_passive),
    CALENDAR(R.string.menu_calendar, R.drawable.menu_event, R.drawable.menu_event_passive),
    SUPPORT(R.string.menu_help, R.drawable.menu_help, R.drawable.menu_help_passive),
    SETTINGS(R.string.menu_settings, R.drawable.menu_settings, R.drawable.menu_settings_passive);

    @StringRes
    public int mTitleRes;

    @DrawableRes
    public int mIconRes;

    @DrawableRes
    public int mPassiveIconRes;

    NavigationDrawerMenuItem(@StringRes int titleRes, @DrawableRes int iconRes, @DrawableRes int passiveIconRes) {
        this.mTitleRes = titleRes;
        this.mIconRes = iconRes;
        this.mPassiveIconRes = passiveIconRes;
    }
}
