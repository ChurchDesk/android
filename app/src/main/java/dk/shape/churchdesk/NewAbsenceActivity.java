package dk.shape.churchdesk;

        import android.app.AlertDialog;
        import android.content.DialogInterface;
        import android.content.SharedPreferences;
        import android.graphics.Color;
        import android.graphics.drawable.ColorDrawable;
        import android.os.Bundle;
        import android.preference.PreferenceManager;
        import android.text.SpannableString;
        import android.text.style.ForegroundColorSpan;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.TextView;
        import android.widget.Toast;

        import org.apache.http.HttpStatus;
        import org.parceler.Parcels;

        import butterknife.InjectView;
        import dk.shape.churchdesk.entity.*;
        import dk.shape.churchdesk.fragment.DashboardFragment;
        import dk.shape.churchdesk.network.BaseRequest;
        import dk.shape.churchdesk.network.ErrorCode;
        import dk.shape.churchdesk.network.Result;
        import dk.shape.churchdesk.request.CreateEventRequest;
        import dk.shape.churchdesk.request.EditEventRequest;
        import dk.shape.churchdesk.view.DoubleBookingDialog;
        import dk.shape.churchdesk.view.NewEventView;
        import dk.shape.churchdesk.viewmodel.NewEventViewModel;

public class NewAbsenceActivity extends BaseLoggedInActivity {


    private MenuItem mMenuCreateEvent;
    private MenuItem mMenuSaveEvent;
    private CreateEventRequest.EventParameter mEventParameter;

    public static String KEY_EVENT_EDIT = "KEY_EDIT_EVENT";
    Event _event;

    @InjectView(R.id.content_view)
    protected NewEventView mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(KEY_EVENT_EDIT)) {
                _event = Parcels.unwrap(extras.getParcelable(KEY_EVENT_EDIT));
            }
        }
        super.onCreate(savedInstanceState);
    }
}
