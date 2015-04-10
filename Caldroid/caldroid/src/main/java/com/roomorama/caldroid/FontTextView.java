package com.roomorama.caldroid;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.caldroid.R;

public class FontTextView extends TextView {

    private static Typeface typeface;

    public FontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public FontTextView(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attrs) {
        if (!isInEditMode() && attrs != null) {
            if(typeface == null) {
                TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FontTextView);
                String fontName = a.getString(R.styleable.FontTextView_fontName);
                if (fontName!=null) {
                    typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);
                    setTypeface(typeface);
                }
                a.recycle();
            } else {
                setTypeface(typeface);
            }
        }
    }

    public void setFont(String fontName) {
        Typeface typeFace = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);
        setTypeface(typeFace);
    }

}
