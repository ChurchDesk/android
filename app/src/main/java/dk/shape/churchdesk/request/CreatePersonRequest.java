package dk.shape.churchdesk.request;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.RequestBody;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import dk.shape.churchdesk.entity.Tag;
import dk.shape.churchdesk.network.ParserException;
import dk.shape.churchdesk.network.PostRequest;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by chirag on 22/02/2017.
 */
public class CreatePersonRequest extends PostRequest<Object> {
    private PersonParameter mPersonObj;

    public CreatePersonRequest(PersonParameter person, String organizationId) {
        super(URLUtils.getCreatePersonUrl(organizationId));
        mPersonObj = person;
    }

    @NonNull
    @Override
    protected RequestBody getData() {
        String data = parse(mPersonObj);
        return RequestBody.create(json, data);
    }

    @Override
    protected Object parseHttpResponseBody(String body) throws ParserException {
        return null;
    }

    public static class PersonParameter {

        public PersonParameter(String firstName, String lastName, String email, String mobile, String homePhone, String workPhone, String jobTitle,
                              Calendar birthDay, String gender, String address, String city,
                              String postalCode, List<Tag> selectedTags) {
            if (firstName.isEmpty())
                this.mFirstName = "";
            else this.mFirstName = firstName;
            if (lastName.isEmpty())
                this.mLastName = "";
            else this.mLastName = lastName;
            if (!email.isEmpty())
                this.mEmail = email;
            HashMap<String, String> contact = new HashMap<>();
            if (!mobile.isEmpty())
                contact.put("phone", mobile);
            if (!homePhone.isEmpty())
                contact.put("homePhone", homePhone);
            else contact.put("homePhone", "");
            if (!workPhone.isEmpty())
                contact.put("workPhone", workPhone);
            else contact.put("workPhone", "");
            if (!address.isEmpty())
                contact.put("street", address);
            else contact.put("street", "");
            if (!city.isEmpty())
                contact.put("city", city);
            else contact.put("city", "");
            if (!postalCode.isEmpty())
                contact.put("zipcode", postalCode);
            else contact.put("zipcode", "");
            this.mContact = contact;
            if (jobTitle.isEmpty())
                this.mJobTitle = "";
            else this.mJobTitle = jobTitle;
            if (birthDay != null)
                this.mBirthday = birthDay.getTime();
            this.mGender = gender;
            this.mTags = selectedTags;

            HashMap<String, String> picture = new HashMap<>();
            picture.put("url","");
        }

        @SerializedName("firstName")
        public String mFirstName;

        @SerializedName("lastName")
        public String mLastName;

        @SerializedName("occupation")
        public String mJobTitle;

        @SerializedName("email")
        public String mEmail;

        @SerializedName("contact")
        public HashMap<String, String> mContact;

        @SerializedName("gender")
        public String mGender;

        @SerializedName("birthday")
        public Date mBirthday;

        @SerializedName("tags")
        public List<Tag> mTags;

        @SerializedName("picture")
        public HashMap<String, String> mPictureUrl;

    }
}
