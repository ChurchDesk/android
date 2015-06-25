package dk.shape.churchdesk;

import android.support.multidex.MultiDexApplication;
import io.intercom.android.sdk.Intercom;
/**
 * Created by steffenkarlsson on 6/11/15.
 */
public class CustomApplication extends MultiDexApplication {

    public boolean hasSendRegistrationId = false;

    @Override public void onCreate() {
        super.onCreate();
        Intercom.initialize(this, "android_sdk-fd4beea862d811c345062196a4367457a03efb02", "ybr6de25");
    }
}
