package dk.shape.churchdesk.entity.resources;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by chirag on 22/11/2016.
 */
@Parcel
public class Field {
    @SerializedName("canEdit")
    public boolean canEdit;

    @SerializedName("allowedValues")
    public List<String> allowedValues;
}
