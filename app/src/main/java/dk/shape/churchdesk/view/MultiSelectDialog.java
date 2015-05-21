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
 * Created by Martin on 21/05/2015.
 */
public class MultiSelectDialog extends Dialog {



    @InjectView(R.id.multi_select_listview)
    protected ListView mMultiSelect;

    private final BaseAdapter mAdapter;

    public MultiSelectDialog(Context context, BaseAdapter adapter, @StringRes int titleRes) {
        super(context);
        setContentView(R.layout.dialog_multi_select);
        setTitle(titleRes);
        ButterKnife.inject(this);
        this.mAdapter = adapter;
    }

    @Override
    protected void onStart() {
        super.onStart();

        mMultiSelect.setAdapter(mAdapter);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mMultiSelect.setOnItemClickListener(listener);
    }
}
