package dk.shape.churchdesk.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;

import dk.shape.churchdesk.R;
import dk.shape.churchdesk.util.MavenPro;

/**
 * Created by steffenkarlsson on 20/03/15.
 */
public class CustomButton extends Button {

    private Context _context;
    private int _fontType = MavenPro.REGULAR;

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        _context = context;

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomTextView, 0, 0);
        try {
            _fontType = a.getInt(R.styleable.CustomTextView_fontType, MavenPro.REGULAR);
        }
        finally {
            a.recycle();
        }

        setFont();
        setAllCaps(false);
        setBackgroundColor(_context.getResources().getColor(R.color.foreground_blue));
    }

    private void setFont() {
        setTypeface(MavenPro.getInstance().get(_context, _fontType));
    }
}
