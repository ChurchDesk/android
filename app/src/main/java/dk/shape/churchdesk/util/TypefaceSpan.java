package dk.shape.churchdesk.util;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class TypefaceSpan extends MetricAffectingSpan {

    private Typeface _typeface;

    public TypefaceSpan(Context context, int fontType) {
        _typeface = MavenPro.getInstance().get(context, fontType);
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        p.setTypeface(_typeface);

        // Note: This flag is required for proper typeface rendering
        p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        tp.setTypeface(_typeface);

        // Note: This flag is required for proper typeface rendering
        tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
    }

}