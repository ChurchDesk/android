package dk.shape.churchdesk.util;

import android.app.Activity;

import org.apache.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import dk.shape.churchdesk.entity.Database;
import dk.shape.churchdesk.entity.resources.Category;
import dk.shape.churchdesk.entity.resources.Group;
import dk.shape.churchdesk.entity.resources.OtherUser;
import dk.shape.churchdesk.entity.resources.Resource;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.GetDatabaseRequest;

/**
 * Created by steffenkarlsson on 24/03/15.
 */
public class DatabaseUtils {

    private static DatabaseUtils ourInstance;
    public static DatabaseUtils getInstance() {
        if (ourInstance == null)
            ourInstance = new DatabaseUtils();
        return ourInstance;
    }
    private DatabaseUtils() {
    }

    private static Database mDatabase;

    public void init(Activity activity) {
        if (mDatabase == null) {
            new GetDatabaseRequest()
                    .withContext(activity)
                    .setOnRequestListener(listener)
                    .runAsync();
        }
    }

    private BaseRequest.OnRequestListener listener = new BaseRequest.OnRequestListener() {
        @Override
        public void onError(int id, ErrorCode errorCode) {
        }

        @Override
        public void onSuccess(int id, Result result) {
            if (result.statusCode == HttpStatus.SC_OK
                    && result.response != null) {
                mDatabase = (Database) result.response;
            }
        }

        @Override
        public void onProcessing() {

        }
    };

    public OtherUser getUserById(int id) {
        if (mDatabase != null) {
            for (OtherUser user : mDatabase.mUsers) {
                if (user.equals(id))
                    return user;
            }
        }
        return null;
    }

    public Group getGroupById(int id) {
        if (mDatabase != null) {
            for (Group group : mDatabase.mGroups) {
                if (group.equals(id))
                    return group;
            }
        }
        return null;
    }

    public List<Group> getGroupsBySiteId(String id) {
        List<Group> groups = new ArrayList<>();
        if (mDatabase != null) {
            for (Group group : mDatabase.mGroups) {
                if (group.mSiteUrl.equals(id))
                    groups.add(group);
            }
        }
        return groups;
    }

    public List<Resource> getResourcesBySiteId(String id) {
        List<Resource> resources = new ArrayList<>();
        if (mDatabase != null) {
            for (Resource resource : mDatabase.mResources) {
                if (resource.mSiteUrl.equals(id))
                    resources.add(resource);
            }
        }
        return resources;
    }

    public List<Category> getCategoriesBySiteId(String id) {
        List<Category> categories = new ArrayList<>();
        if (mDatabase != null) {
            for (Category category : mDatabase.mCategories) {
                if (category.mSiteUrl.equals(id))
                    categories.add(category);
            }
        }
        return categories;
    }
}
