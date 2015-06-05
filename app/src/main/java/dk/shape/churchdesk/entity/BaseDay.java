package dk.shape.churchdesk.entity;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;

import dk.shape.churchdesk.util.DateAppearanceUtils;

/**
 * Created by root on 6/1/15.
 */

public abstract class BaseDay implements Comparable<BaseDay> {

    public abstract Date getDate();

    public Calendar reset(Calendar calendar) {
        return DateAppearanceUtils.reset(calendar);
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
