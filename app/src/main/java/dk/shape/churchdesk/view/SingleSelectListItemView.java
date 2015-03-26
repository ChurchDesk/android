package dk.shape.churchdesk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import butterknife.InjectView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomTextView;

/**
 * Created by steffenkarlsson on 26/03/15.
 */
public class SingleSelectListItemView extends BaseFrameLayout {

    @InjectView(R.id.item_title)
    public CustomTextView mItemTitle;

    @InjectView(R.id.item_selected)
    public ImageView mItemSelected;

    public SingleSelectListItemView(Context context) {
        super(context);
    }

    public SingleSelectListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.part_single_select_item;
    }
}