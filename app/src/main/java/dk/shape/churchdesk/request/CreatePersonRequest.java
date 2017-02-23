package dk.shape.churchdesk.request;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.RequestBody;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

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
                              Date birthDay, String gender, String address, String city,
                              String postalCode) {
            this.mFirstName = firstName;
            this.mLastName = lastName;
            this.mEmail = email;
            HashMap<String, String> contact = new HashMap<>();
            if (!mobile.isEmpty())
                contact.put("phone", mobile);
            if (!homePhone.isEmpty())
                contact.put("homePhone", homePhone);
            if (!workPhone.isEmpty())
                contact.put("workPhone", workPhone);
            if (!address.isEmpty())
                contact.put("street", address);
            if (!city.isEmpty())
                contact.put("city", city);
            if (!postalCode.isEmpty())
                contact.put("zipcode", postalCode);
            this.mJobTitle = jobTitle;
            this.mBirthday = birthDay;
            this.mGender = gender;


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


    }
}
