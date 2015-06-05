package dk.shape.churchdesk.entity;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by root on 6/1/15.
 */

public abstract class BaseDay implements Comparable<BaseDay> {

    public abstract Date getDate();

    public Calendar reset(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    @Override
    public int compareTo(@NonNull BaseDay another) {
        return getDate().equals(another.getDate())
                ? 0
                : getDate().before(another.getDate())
                ? -1
                : 1;
    }
}
