package dk.shape.churchdesk.network;

import org.parceler.guava.collect.BiMap;
import org.parceler.guava.collect.HashBiMap;

/**
 * Created by steffenkarlsson on 23/12/14.
 */
public class RequestHandler {

    private static int mCounter = 0;
    private static BiMap<Enum, Integer> mRequestMap = HashBiMap.create();

    protected static int getIdFromRequestIdentifier(Enum requestIdentifier) {
        if (mRequestMap.containsKey(requestIdentifier))
            return mRequestMap.get(requestIdentifier);
        mRequestMap.put(requestIdentifier, ++mCounter);
        return mCounter;
    }

    public static <E extends Enum> E getRequestIdentifierFromId(int internalId) {
        BiMap<Integer, Enum> inverseRequestMap = mRequestMap.inverse();
        if (inverseRequestMap.containsKey(internalId)) {
            return (E)inverseRequestMap.get(internalId);
        }
        return null;
    }
}
