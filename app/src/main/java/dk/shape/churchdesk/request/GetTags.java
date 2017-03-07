package dk.shape.churchdesk.request;

import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dk.shape.churchdesk.entity.Tag;
import dk.shape.churchdesk.network.GetRequest;
import dk.shape.churchdesk.network.ParserException;

import static dk.shape.churchdesk.network.RequestUtils.parse;

/**
 * Created by chirag on 06/03/2017.
 */
public class GetTags extends GetRequest<List<Tag>> {

    public GetTags(String organisationId) {
        super(URLUtils.getTagsUrl(organisationId));
    }

    @Override
    protected List<Tag> parseHttpResponseBody(String body) throws ParserException {
        List<Tag> sortedList = parse(new TypeToken<List<Tag>>() {}, body);
        Collections.sort(sortedList, new Comparator<Tag>() {
            public int compare(Tag t1, Tag t2) {
                if (t1.mTagName == null || t1.mTagName.length() == 0)
                    t1.mTagName = "unknown";
                if (t2.mTagName == null || t2.mTagName.length() == 0)
                    t2.mTagName = "unknown";

                return t1.mTagName.compareTo(t2.mTagName);
            }
        });
        return sortedList;
    }
}
