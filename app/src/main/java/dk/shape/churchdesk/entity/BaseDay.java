package dk.shape.churchdesk.entity;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by root on 6/1/15.
 */

public abstract class BaseDay {

    public abstract Date getDate();

    public Calendar reset(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
}
