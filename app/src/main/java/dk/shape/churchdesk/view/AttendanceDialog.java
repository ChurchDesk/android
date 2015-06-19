package dk.shape.churchdesk.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomTextView;

/**
 * Created by Martin on 08/06/2015.
 */
public class AttendanceDialog extends Dialog {

    protected CustomTextView mAttendanceText;

    @InjectView(R.id.dialog_attendance_button_going)
    protected CustomTextView mGoingButton;

    @InjectView(R.id.dialog_attendance_button_maybe)
    protected CustomTextView mMaybeButton;

    @InjectView(R.id.dialog_attendance_button_declined)
    protected CustomTextView mDeclinedButton;

    @InjectView(R.id.dialog_attendance_button_cancel)
    protected CustomTextView mCancelButton;

    String mSite;
    int mEventId;
    Context mContext;

    public AttendanceDialog(Context context, String text, int eventId, String site) {
        super(context);
        mContext = context;
        setContentView(R.layout.dialog_attendance);
        ButterKnife.inject(this);
        setTitle(R.string.event_details_attendance_title);
        mAttendanceText = (CustomTextView) findViewById(R.id.dialog_attendance_text);
        mAttendanceText.setText(text);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mSite = site;
        mEventId = eventId;
    }

    public void addOnClickListeners(final CustomTextView.OnClickListener listener){
        mGoingButton.setOnClickListener(listener);
        mMaybeButton.setOnClickListener(listener);
        mDeclinedButton.setOnClickListener(listener);
    }
}
