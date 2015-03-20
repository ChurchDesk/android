package dk.shape.churchdesk.util;

import android.text.Editable;
import android.text.TextWatcher;

import com.android.internal.util.Predicate;

/**
 * Created by steffenkarlsson on 26/01/15.
 */
public class OnTextChanged implements TextWatcher {

    private final Predicate<String> condition;
    private final OnConditionMetListener mOnConditionMetListener;

    public interface OnConditionMetListener {
        void onConditionMet(boolean met, String s);
    }

    public OnTextChanged(Predicate<String> condition, OnConditionMetListener onConditionMetListener) {
        this.condition = condition;
        this.mOnConditionMetListener = onConditionMetListener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        boolean met = false;
        if (condition.apply(s.toString())) {
            met = true;
        }
        mOnConditionMetListener.onConditionMet(met, s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) { }
}
