package dk.shape.churchdesk.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

import dk.shape.churchdesk.R;


/**
 * Created by steffenkarlsson on 27/01/15.
 */
public class ButtonSwitch extends LinearLayout {

    public interface OnButtonSwitchClickListener {
        void onClick(int position);
    }

    private ArrayList<Button> buttons = new ArrayList<>();
    private OnButtonSwitchClickListener mOnButtonSwitchClickListener;
    private boolean enabled = false;
    private int selected = -1;

    private int iColorRes = getResources().getColor(R.color.blue);
    private int aLeftColorRes = getResources().getColor(R.color.caldroid_sky_blue);
    private int aRightColorRes = getResources().getColor(R.color.caldroid_sky_blue);

    public ButtonSwitch(Context context) {
        super(context);
    }

    public ButtonSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(Context context, int numButtons,
                     OnButtonSwitchClickListener listener, String... texts) {
        this.mOnButtonSwitchClickListener = listener;
        this.buttons.clear();
        this.removeAllViews();

        setWeightSum(6);
        setGravity(Gravity.CENTER_HORIZONTAL);

        float singleSum = (6f / numButtons);

        for (int i = 0; i < numButtons; i++) {
            Resources res = context.getResources();
            Button button = new Button(getContext());
            button.setText(texts[i]);
            button.setOnClickListener(onClickListener);
            button.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    res.getDimension(R.dimen.button_switch_textsize));
            changeState(button, false, i);

            LinearLayout.LayoutParams params = new LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT, singleSum);
            params.gravity = Gravity.CENTER_HORIZONTAL;

            if (i != numButtons - 1)
                params.setMargins(0, 0, 5, 0);

            button.setTransformationMethod(null);
            buttons.add(button);
            addView(button, params);
        }
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            enabled = true;

            for (int i = 0; i < buttons.size(); i++) {
                Button button = buttons.get(i);
                if (v == button) {
                    selected = i;
                    changeState(button, true, i);
                    if (mOnButtonSwitchClickListener != null) {
                        mOnButtonSwitchClickListener.onClick(i);
                    }
                } else {
                    changeState(button, false, i);
                }
            }
        }
    };

    private void changeState(Button button, boolean active, int idx) {
        button.setBackgroundColor(Color.WHITE);
        button.setTextColor(active
                ? idx == 0 ? aLeftColorRes : aRightColorRes
                : iColorRes);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
        this.enabled = true;
        changeState(buttons.get(selected), true, selected);
    }
    public void clickOnButton(int buttonClicked){
        selected = buttonClicked;
        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            if (i == buttonClicked) {
                changeState(button, true, i);
                if (mOnButtonSwitchClickListener != null) {
                    mOnButtonSwitchClickListener.onClick(i);
                }
            } else {
                changeState(button, false, i);
            }
        }
    }
}
