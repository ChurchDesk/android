package dk.shape.churchdesk.util;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * Created by root on 6/3/15.
 */
public class MapUtils {

    public static <T, U> SortedMap<T, List<U>> merge(SortedMap<T, List<U>> map1,
                                                     Map<T, List<U>> map2) {
        for (Map.Entry<T, List<U>> entry : map2.entrySet()) {
            T key = entry.getKey();
            if (map1.containsKey(key))
                map1.get(key).addAll(entry.getValue());
            else
                map1.put(key, entry.getValue());
        }
        return map1;
    }
}
