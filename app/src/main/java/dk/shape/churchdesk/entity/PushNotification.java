package dk.shape.churchdesk.entity;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by steffenkarlsson on 07/04/15.
 */
@Parcel
public class PushNotification {

    @SerializedName("bookingUpdated")
    public boolean isBookingUpdated;

    @SerializedName("bookingCanceled")
    public boolean isBookingCanceled;

    @SerializedName("bookingCreated")
    public boolean isBookingCreated;

    @SerializedName("message")
    public boolean isMessage;
}
