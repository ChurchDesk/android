package dk.shape.churchdesk.util;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import org.parceler.apache.commons.lang.time.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    public static Spannable getEventTime(Context context, Event event) {
        if (event.isAllDay)
            return new SpannableString(context.getString(R.string.all_day));
        else {
            String eventTime = "";
            boolean isBefore = true;
            switch (event.getPartOfEvent()) {
                case SINGLE_DAY:
                    return new SpannableString(String.format("%s - %s",
                            hourMinFormatter.format(event.mStartDate),
                            hourMinFormatter.format(event.mEndDate)));
                case FIRST_DAY:
                    isBefore = false;
                    eventTime = String.format("%s - %s",
                            hourMinFormatter.format(event.mStartDate),
                            day2Formatter.format(event.mEndDate));
                    break;
                case INTERMEDIATE_DAY:
                    isBefore = false;
                    eventTime = String.format("%s - %s",
                            context.getString(R.string.all_day),
                            day2Formatter.format(event.mEndDate));
                    break;
                case LAST_DAY:
                    eventTime = String.format("%s - %s",
                            day2Formatter.format(event.mStartDate),
                            hourMinFormatter.format(event.mEndDate));
                    break;
            }
            int indexOfDash = eventTime.indexOf("-");
            int grey = context.getResources().getColor(R.color.foreground_grey);
            Spannable eventTimeSpan = new SpannableString(eventTime);
            if (isBefore)
                eventTimeSpan.setSpan(new ForegroundColorSpan(grey),
                        0, indexOfDash, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            else
                eventTimeSpan.setSpan(new ForegroundColorSpan(grey),
                        indexOfDash + 1, eventTime.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            return eventTimeSpan;
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

            Date dFst = fst.getTime();
            Date dSnd = snd.getTime();

            if (DateUtils.isSameDay(fst, snd)) {
                return String.format("%s, %s-%s",
                        allDayFormatter.format(dFst),
                        hourMinFormatter.format(dFst),
                        hourMinFormatter.format(dSnd));
            }
            if (fst.get(Calendar.YEAR) == snd.get(Calendar.YEAR)) {
                return String.format("%s, %s - %s, %s",
                        dayFormatter.format(dFst),
                        hourMinFormatter.format(dFst),
                        dayFormatter.format(dSnd),
                        hourMinFormatter.format(dSnd));
            }

            return String.format("%s, %s - %s, %s",
                    dayFormatter.format(dFst),
                    hourMinFormatter.format(dFst),
                    dayYearFormatter.format(dSnd),
                    hourMinFormatter.format(dSnd));
        }
    }

    public static Calendar reset(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
}
