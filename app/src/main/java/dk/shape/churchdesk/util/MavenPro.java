package dk.shape.churchdesk.util;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;

import java.util.Hashtable;

public class MavenPro {

    private static final Hashtable<Integer, Typeface> _cache = new Hashtable<>();

    public static final int MEDIUM = 0;
    public static final int REGULAR = 1;
    public static final int BOLD = 2;
    public static final int BLACK = 3;

    public static final String ASSET_FONT_PATH = "fonts/%s";

    public Typeface get(Context c, int fontType) {
        String fontName = getFontName(fontType);

        synchronized (_cache) {
            if (!_cache.containsKey(fontType)) {
                try {
                    Typeface t = Typeface.createFromAsset(c.getAssets(), String.format(ASSET_FONT_PATH, fontName));
                    _cache.put(fontType, t);
                } catch (Exception e) {
                    Log.e("Typefaces", "Could not get typeface '" + fontName + "' because " + e.getMessage());
                    return null;
                }
            }

            return _cache.get(fontType);
        }
    }

    private String getFontName(int fontType) {
        String fontName = "";

        switch (fontType) {
            case REGULAR:
                fontName = "MavenPro-Regular.ttf";
                break;
            case MEDIUM:
                fontName = "MavenPro-Medium.ttf";
                break;
            case BOLD:
                fontName = "MavenPro-Bold.ttf";
                break;
            case BLACK:
                fontName = "MavenPro-Black.ttf";
                break;
        }

        return fontName;
    }

    public SpannableString getStringWithCorrectFont(Context context, String text) {
        return getStringWithCorrectFont(context, MavenPro.REGULAR, text);
    }

    public SpannableString getStringWithCorrectFont(Context context, int fontType, String text) {
        SpannableString s = new SpannableString(text);
        s.setSpan(new TypefaceSpan(context, fontType), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }

    private static MavenPro _instance;

    public static MavenPro getInstance() {
        if (_instance == null) {
            _instance = new MavenPro();
        }
        return _instance;
    }

}