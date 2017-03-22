package dk.shape.churchdesk.request;

import android.support.annotation.NonNull;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.util.Objects;

import dk.shape.churchdesk.network.ParserException;
import dk.shape.churchdesk.network.PostRequest;

/**
 * Created by chirag on 22/03/2017.
 */
public class UploadPicture extends PostRequest<Object> {
    //path to image
    static String pathToImage;
    public UploadPicture(int userId, String site, String path) {
        super(URLUtils.getImage(userId, site));
        this.pathToImage = path;
    }
    @NonNull
    @Override
    protected RequestBody getData() {
        File file = new File(pathToImage);
        RequestBody formBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("file", "image.jpg",
                        RequestBody.create(MediaType.parse("image/jpg"), file))
                .build();

        return formBody;
    }

    @Override
    protected Object parseHttpResponseBody(String body) throws ParserException {
        return null;
    }
}
