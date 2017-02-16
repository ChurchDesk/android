package dk.shape.churchdesk.entity;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by chirag on 24/01/2017.
 */
public class Person {
    @SerializedName("fullName")
    public String mFullName;

    @SerializedName("occupation")
    public String mOccupation;

    @SerializedName("email")
    public String mEmail;

    @SerializedName("id")
    public String mPeopleId;

    @SerializedName("gender")
    public String mGender;

    @SerializedName("birthday")
    public Date mBirthday;

    @SerializedName("registered")
    public Date mRegistered;

}
