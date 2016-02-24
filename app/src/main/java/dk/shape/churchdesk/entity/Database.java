package dk.shape.churchdesk.entity;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

import dk.shape.churchdesk.entity.resources.Category;
import dk.shape.churchdesk.entity.resources.Group;
import dk.shape.churchdesk.entity.resources.OtherUser;
import dk.shape.churchdesk.entity.resources.Resource;

/**
 * Created by steffenkarlsson on 24/03/15.
 */

@Parcel
public class Database {

    @SerializedName("resources")
    public List<Resource> mResources;

    @SerializedName("groups")
    public List<Group> mGroups;

    @SerializedName("eventCategories")
    public List<Category> mCategories;

    @SerializedName("absenceCategories")
    public List<Category> mAbsenceCategories;

    @SerializedName("users")
    public List<OtherUser> mUsers;
}
