package dk.shape.churchdesk.util;

import android.app.Activity;

import org.apache.http.HttpStatus;

import dk.shape.churchdesk.entity.Database;
import dk.shape.churchdesk.entity.resources.OtherUser;
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
            if (result != null && result.statusCode == HttpStatus.SC_OK) {
                mDatabase = (Database) result.response;
            }
        }

        @Override
        public void onProcessing() {

        }
    };

    public OtherUser getUserFromId(int id) {
        if (mDatabase != null) {
            for (OtherUser mUser : mDatabase.mUsers) {
                if (mUser.equals(id))
                    return mUser;
            }
        }
        return null;
    }
}