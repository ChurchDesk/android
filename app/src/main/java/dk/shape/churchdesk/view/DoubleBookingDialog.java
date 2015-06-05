package dk.shape.churchdesk.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.StringRes;
import android.webkit.WebView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomTextView;

/**
 * Created by Martin on 05/06/2015.
 */
public class DoubleBookingDialog extends Dialog {


    @InjectView(R.id.dialog_double_booking_allow)
    protected CustomTextView mAllowButton;

    @InjectView(R.id.dialog_double_booking_cancel)
    protected CustomTextView mCancelButton;

    @InjectView(R.id.dialog_double_booking_web)
    protected WebView mWebView;


    public DoubleBookingDialog(Context context, String htmlToWeb, @StringRes int titleRes) {
        super(context);
        setContentView(R.layout.dialog_double_booking);
        setTitle(titleRes);
        ButterKnife.inject(this);
        mWebView.loadData(htmlToWeb, "text/html", "utf-8");
    }

    public void setOnAllowClickListener(CustomTextView.OnClickListener listener){
        mAllowButton.setOnClickListener(listener);
    }

    public void setOnCancelClickListener(CustomTextView.OnClickListener listener){
        mCancelButton.setOnClickListener(listener);
    }


}
