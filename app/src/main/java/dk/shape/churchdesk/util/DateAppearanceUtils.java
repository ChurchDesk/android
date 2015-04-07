package dk.shape.churchdesk.util;

import android.content.Context;

import org.parceler.apache.commons.lang.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Event;

/**
 * Created by steffenkarlsson on 07/04/15.
 */
public class DateAppearanceUtils {

    private static SimpleDateFormat hourMinFormatter = new SimpleDateFormat(
            "HH:mm", Locale.getDefault());

    private static SimpleDateFormat dayFormatter = new SimpleDateFormat(
            "dd MMM", Locale.getDefault());

    private static SimpleDateFormat day2Formatter = new SimpleDateFormat(
            "dd'.' MMM", Locale.getDefault());

    private static SimpleDateFormat dayYearFormatter = new SimpleDateFormat(
            "dd MMM yyyy", Locale.getDefault());

    private static SimpleDateFormat allDayFormatter = new SimpleDateFormat(
            "EEEE dd MMM", Locale.getDefault());

    public static String getEventTime(Context context, Event event) {
        if (event.isAllDay)
            return context.getString(R.string.all_day);
        else {
            if (DateUtils.isSameDay(event.mStartDate, event.mEndDate)) {
                return String.format("%s - %s",
                        hourMinFormatter.format(event.mStartDate),
                        hourMinFormatter.format(event.mStartDate));
            } else {
                return String.format("%s - %s",
                        hourMinFormatter.format(event.mStartDate),
                        day2Formatter.format(event.mEndDate));
            }
        }
    }

    public static String getEventInvitationTime(Event event) {
        if (event.isAllDay)
            return allDayFormatter.format(event.mStartDate);
        else {
            Calendar fst = Calendar.getInstance();
            fst.setTime(event.mStartDate);

            Calendar snd = Calendar.getInstance();
            snd.setTime(event.mEndDate);

            if (DateUtils.isSameDay(fst, snd)) {
                return String.format("%s, %s-%s",
                        allDayFormatter.format(fst),
                        hourMinFormatter.format(fst),
                        hourMinFormatter.format(snd));
            }
            if (fst.get(Calendar.YEAR) == snd.get(Calendar.YEAR)) {
                return String.format("%s, %s - %s, %s",
                        dayFormatter.format(fst),
                        hourMinFormatter.format(fst),
                        dayFormatter.format(snd),
                        hourMinFormatter.format(snd));
            }

            return String.format("%s, %s - %s, %s",
                    dayFormatter.format(fst),
                    hourMinFormatter.format(fst),
                    dayYearFormatter.format(snd),
                    hourMinFormatter.format(snd));
        }
    }
}
