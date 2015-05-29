package dk.shape.churchdesk.util;

/**
 * Created by Martin on 29/05/2015.
 */
public class CalendarUtils {

    public static String translateTime(int hour, int minute){
        String time = "";
        if(hour > 9){
            time += hour;
        } else {
            time += "0" + hour;
        }
        time += ":";
        if(minute > 9){
            time += minute;
        } else {
            time += "0" + minute;
        }
        return time;
    }


}
