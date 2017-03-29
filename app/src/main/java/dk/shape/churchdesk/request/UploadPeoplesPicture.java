package dk.shape.churchdesk.request;

import android.support.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;
import java.io.File;
import java.util.HashMap;

import dk.shape.churchdesk.network.ParserException;
import dk.shape.churchdesk.network.PostRequest;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by Edgaras on 28/03/2017.
 */

public class UploadPeoplesPicture extends PostRequest<HashMap<String, String>> {

    static String pathToImage;

    public UploadPeoplesPicture(String site, String path) {
        super(URLUtils.getPeoplesImage(site));
        pathToImage = path;
    }

    @NonNull
    @Override
    protected RequestBody getData() {
        File file = new File(pathToImage);
        MultipartBuilder formBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("file", "image.jpg",
                        RequestBody.create(MediaType.parse("image/jpg"), file));

        return  formBody.build();
    }

    @Override
    protected HashMap<String, String> parseHttpResponseBody(String body) throws ParserException {
        HashMap<String, String> hash = parse(new TypeToken<HashMap<String, String>>() {}, body);
        return hash;
    }
}
