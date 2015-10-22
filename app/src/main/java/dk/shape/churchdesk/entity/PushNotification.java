package dk.shape.churchdesk.entity;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.HashMap;

/**
 * Created by steffenkarlsson on 07/04/15.
 */
@Parcel
public class PushNotification {

    @SerializedName("bookingUpdatedNotifcation")
    public HashMap<String, Boolean> isBookingUpdated;

    @SerializedName("bookingCanceledNotifcation")
    public HashMap<String, Boolean> isBookingCanceled;

    @SerializedName("bookingCreatedNotifcation")
    public HashMap<String, Boolean> isBookingCreated;

    @SerializedName("groupMessageNotifcation")
    public HashMap<String, Boolean> isMessage;
}
