package dk.shape.churchdesk.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomTextView;

/**
 * Created by Martin on 21/05/2015.
 */
public class MultiSelectDialog extends Dialog {



    @InjectView(R.id.multi_select_listview)
    protected ListView mMultiSelect;

    @InjectView(R.id.multi_item_button_ok)
    protected CustomTextView mOKButton;

    @InjectView(R.id.multi_item_button_cancel)
    protected CustomTextView mCancelButton;

    @InjectView(R.id.multi_item_text)
    protected CustomTextView mText;


    private final BaseAdapter mAdapter;

    public MultiSelectDialog(Context context, BaseAdapter adapter, @StringRes int titleRes) {
        super(context, R.style.CustomDialogWithTitle);
        setContentView(R.layout.dialog_multi_select);
        setTitle(titleRes);
        ButterKnife.inject(this);
        this.mAdapter = adapter;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAdapter != null) {
            mMultiSelect.setAdapter(mAdapter);
        }
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mMultiSelect.setOnItemClickListener(listener);
        if(listener == null){
            mMultiSelect.setEnabled(false);
        }
    }

    public void showCancelButton(Boolean show){
        if(show){
            mCancelButton.setVisibility(View.VISIBLE);
        } else {
            mCancelButton.setVisibility(View.GONE);
        }
    }

    public void setOnOKClickListener(CustomTextView.OnClickListener listener){
        mOKButton.setOnClickListener(listener);
    }

    public void setOnCancelClickListener(CustomTextView.OnClickListener listener){
        mCancelButton.setOnClickListener(listener);
    }

    public void showOnlyText(String text){
        mText.setText(text);
        mText.setVisibility(View.VISIBLE);
        mMultiSelect.setVisibility(View.GONE);
    }


}
