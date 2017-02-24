package dk.shape.churchdesk.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.StringRes;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dk.shape.churchdesk.R;

/**
 * Created by steffenkarlsson on 26/03/15.
 */
public class SingleSelectDialog extends Dialog {

    @InjectView(R.id.single_select_listview)
    protected ListView mSingleSelect;

    private final BaseAdapter mAdapter;

    public SingleSelectDialog(Context context, BaseAdapter adapter, @StringRes int titleRes) {
        super(context, R.style.CustomDialogWithTitle);
        setContentView(R.layout.dialog_single_select);
        setTitle(titleRes);
        ButterKnife.inject(this);
        this.mAdapter = adapter;
    }

    @Override
    protected void onStart() {
        super.onStart();

        mSingleSelect.setAdapter(mAdapter);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mSingleSelect.setOnItemClickListener(listener);
    }
}
