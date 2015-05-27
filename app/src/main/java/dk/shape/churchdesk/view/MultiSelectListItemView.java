package dk.shape.churchdesk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.widget.CustomTextView;

/**
 * Created by Martin on 21/05/2015.
 */
public class MultiSelectListItemView extends BaseFrameLayout {


    @InjectView(R.id.multi_item_dot)
    public CustomTextView mItemDot;

    @InjectView(R.id.multi_item_title)
    public CustomTextView mItemTitle;

    @InjectView(R.id.multi_item_selected)
    public ImageView mItemSelected;

    @InjectView(R.id.multi_item_image)
    public CircleImageView mItemImage;



    public MultiSelectListItemView(Context context) {
        super(context);
    }

    public MultiSelectListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.part_multi_select_item;
    }
}
