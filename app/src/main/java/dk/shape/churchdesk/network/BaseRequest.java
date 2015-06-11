package dk.shape.churchdesk.network;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;

import static dk.shape.churchdesk.network.ErrorCode.NO_NETWORK;
import static dk.shape.churchdesk.network.ErrorCode.PARSER_FAIL;
import static dk.shape.churchdesk.network.ErrorCode.NETWORK_ERROR;
import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by steffenkarlsson on 22/12/14.
 */
public abstract class BaseRequest<T> {

    private static final String TAG = "BaseRequest";

    protected final MediaType json = MediaType.parse("application/json; charset=utf-8");

    public interface OnRequestListener {
        void onError(int id, ErrorCode errorCode);
        void onSuccess(int id, Result result);
        void onProcessing();
    }

    private static final OkHttpClient mClient = new OkHttpClient();

    private String mUrl;
    private boolean mIsAsync;
    private Activity mActivity;
    private int mRequestId = -1;

    protected OnRequestListener mOnRequestListener;

    protected BaseRequest(String url) {
        this.mUrl = url;
    }

    public void runAsync(Enum requestIdentifier) {
        run(requestIdentifier, true);
    }

    public void runAsync() {
        run(null, true);
    }

    public void run(Enum requestIdentifier) {
        run(requestIdentifier, false);
    }

    public void run() {
        run(null, false);
    }

    private void run(Enum requestIdentifier, boolean isAsync) {
        if (mActivity == null) {
            Log.e(TAG, "No context defined");
            return;
        }

        if (mOnRequestListener == null) {
            Log.e(TAG, "No listener defined: OnRequestListener");
            return;
        }

        if (requestIdentifier != null)
            this.mRequestId = RequestHandler.getIdFromRequestIdentifier(requestIdentifier);
        this.mIsAsync = isAsync;

        if (isNetworkConnected(mActivity)) {
            new Downloader().execute(buildRequest());
        } else {
            reportError(NO_NETWORK);
        }
    }

    private Callback mHttpCallBack = new Callback() {
        @Override
        public void onFailure(Request request, IOException e) {
            boolean x = true;
            // TODO:
        }

        @Override
        public void onResponse(Response response) throws IOException {
            try {
                handleResponse(response);
            } catch (ParserException ignore) {
                reportError(PARSER_FAIL);
            }
        }
    };

    private void handleResponse(Response response) throws ParserException {
        int statusCode = response.code();
        try {
            String body = response.body().string();
            Log.d("ERRORERROR", body);
            if (response.isSuccessful()) {
                final Result<T> result = handleResponse(statusCode, body);
                postToMain(new Runnable() {
                    @Override
                    public void run() {
                        mOnRequestListener.onSuccess(mRequestId, result);
                    }
                });
            } else {
                if (mOnRequestListener != null) {
                    try {
                        Error error = parse(Error.class, body);
                        ErrorCode code = error.getErrorCode();
                        code.dec = error.errorDesc;
                        reportError(code);
                    } catch (IllegalArgumentException e){
                        if(response.code() == 406){
                            CustomError error = parse(CustomError.class, body);
                            if(error.mHtml) {
                                ErrorCode code = ErrorCode.NOT_ACCEPTABLE;
                                code.dec = error.mError;
                                reportError(code);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
           reportError(NETWORK_ERROR);
        }
    }

    private class Error {

        @SerializedName("error")
        protected String errorCode;

        @SerializedName("error_description")
        public String errorDesc;

        public ErrorCode getErrorCode() {
            if(errorCode != null) {
                Log.d("ERRORERROR", errorCode);
            }
            return ErrorCode.valueOf(errorCode.toUpperCase());
        }
    }

    public class CustomError {

        @SerializedName("html")
        public boolean mHtml;

        @SerializedName("error")
        public String mError;
    }

    public abstract Result<T> handleResponse(int statusCode, String body) throws ParserException;

    protected Request buildRequest() {
        Request.Builder builder = new Request.Builder();
        builder.url(mUrl);
        builder.addHeader("Accept", "application/json");
        builder.addHeader("Content-Type", "application/json");
        Log.d("ERRORERROR", mUrl);
        return finalizeRequest(builder);
    }

    /**
     * If overriding, do not call super, but call builder.build() as last part of the
     * implementation instead.
     * @param builder
     * @return Request
     */
    protected Request finalizeRequest(Request.Builder builder) {
        return builder.build();
    }

    /**
     * Call method once.
     * @param cacheDirectory
     * @param size
     * @throws IOException
     */
    public static void useCache(File cacheDirectory, long size) throws IOException {
        mClient.setCache(new Cache(cacheDirectory, size));
    }

    public BaseRequest setOnRequestListener(OnRequestListener onRequestListener) {
        this.mOnRequestListener = onRequestListener;
        return this;
    }

    public BaseRequest withContext(Activity activity) {
        this.mActivity = activity;
        return this;
    }

    /**
     * Using GSON parser as default
     * @param body
     * @return
     */
    protected abstract T parseHttpResponseBody(String body) throws ParserException;

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    private class Downloader extends AsyncTask<Request, Integer, Object> {

        @Override
        protected Object doInBackground(Request... params) {
            try {
                Call call = mClient.newCall(params[0]);
                postToMain(new Runnable() {
                    @Override
                    public void run() {
                        mOnRequestListener.onProcessing();
                    }
                });
                if (mIsAsync) {
                    call.enqueue(mHttpCallBack);
                } else {
                    try {
                        Response response = call.execute();
                        handleResponse(response);
                    } catch (ParserException ignore) {
                        reportError(PARSER_FAIL);
                    }
                }
            } catch (IOException e) {
                reportError(NETWORK_ERROR);
            }
            return null;
        }
    }

    protected void reportError(final ErrorCode errorCode) {
        postToMain(new Runnable() {
            @Override
            public void run() {
                mOnRequestListener.onError(mRequestId, errorCode);
            }
        });
    }

    protected void postToMain(Runnable runnable) {
        new Handler(mActivity.getMainLooper()).post(runnable);
    }
}
