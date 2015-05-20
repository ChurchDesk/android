package dk.shape.churchdesk.fragment;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.apache.http.HttpStatus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import dk.shape.churchdesk.BaseActivity;
import dk.shape.churchdesk.BaseFloatingButtonFragment;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.resources.Resource;
import dk.shape.churchdesk.network.BaseRequest;
import dk.shape.churchdesk.network.ErrorCode;
import dk.shape.churchdesk.network.Result;
import dk.shape.churchdesk.request.GetEvents;
import dk.shape.churchdesk.view.BaseFrameLayout;
import dk.shape.churchdesk.view.CalendarView;
import dk.shape.churchdesk.viewmodel.CalendarViewModel;
import dk.shape.churchdesk.viewmodel.WeekViewModel;

/**
 * Created by steffenkarlsson on 17/03/15.
 */
public class CalendarFragment extends BaseFloatingButtonFragment {

    private enum RequestTypes {
        PREV, CURRENT, NEXT
    }

    private SimpleDateFormat formatter = new SimpleDateFormat("MMMM", Locale.getDefault());

    @Override
    protected int getTitleResource() {
        return -1;
    }

    private CalendarView mView;
    private CalendarViewModel mViewModel;
    private Calendar mSelectedDate;
    private BaseActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = (BaseActivity) getActivity();
        mActivity.setHasDrawable(mTitleClickListener);
        mSelectedDate = Calendar.getInstance();
        mActivity.setActionBarTitle(formatter.format(mSelectedDate.getTime()));
    }

    private BaseActivity.OnTitleClickListener mTitleClickListener
            = new BaseActivity.OnTitleClickListener() {
        @Override
        public void onClick(final boolean isSelected) {
            Resources res = mActivity.getResources();
            ColorDrawable[] color;

            final View view = mView.mCalendarView;
//            Animation animation;
            if (isSelected) {
//                animation = AnimationUtils.loadAnimation(getActivity(), R.anim.in_from_top);
//                view.setVisibility(View.VISIBLE);
//                view.invalidate();

                color = new ColorDrawable[] { new ColorDrawable(res.getColor(R.color.start_black)),
                        new ColorDrawable(res.getColor(R.color.end_black)) };
            } else {
//                animation = AnimationUtils.loadAnimation(getActivity(), R.anim.out_top);
//                animation.setAnimationListener(new Animation.AnimationListener() {
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                        mView.mCalendarView.setVisibility(View.GONE);
//                        mView.mCalendarView.invalidate();
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//                    }
//                });
//
                color = new ColorDrawable[] { new ColorDrawable(res.getColor(R.color.end_black)),
                        new ColorDrawable(res.getColor(R.color.start_black)) };
            }

            TransitionDrawable trans = new TransitionDrawable(color);
            mView.mDimmingLayer.setBackgroundDrawable(trans);
            trans.startTransition(150);

//            view.startAnimation(animation);
        }
    };

    @Override
    protected void onUserAvailable() {
        super.onUserAvailable();

        new GetEvents(mSelectedDate.get(Calendar.YEAR), mSelectedDate.get(Calendar.MONTH))
                .withContext(getActivity())
                .setOnRequestListener(listener)
                .runAsync(RequestTypes.CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        new GetEvents(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))
                .withContext(getActivity())
                .setOnRequestListener(listener)
                .runAsync(RequestTypes.NEXT);

        calendar.add(Calendar.MONTH, -2);
        new GetEvents(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))
                .withContext(getActivity())
                .setOnRequestListener(listener)
                .runAsync(RequestTypes.PREV);
    }

    private BaseRequest.OnRequestListener listener = new BaseRequest.OnRequestListener() {
        @Override
        public void onError(int id, ErrorCode errorCode) {

        }

        @Override
        public void onSuccess(int id, Result result) {
            if (result.statusCode == HttpStatus.SC_OK
                    && result.response != null) {

            }
        }

        @Override
        public void onProcessing() {

        }
    };

    private CaldroidListener mCaldroidListener = new CaldroidListener() {
        @Override
        public void onSelectDate(Calendar date, View view) {
            if (mSelectedDate != null)
                mViewModel.deselectDate(mSelectedDate);
            mSelectedDate = date;
            mViewModel.selectDate(mSelectedDate);
        }
    };

    private WeekViewModel.OnDateClick mOnDateClickListener = new WeekViewModel.OnDateClick() {
        @Override
        public void onDateClick(Calendar calendar) {
            // TODO:
        }
    };

    private CaldroidFragment.OnMonthChangedListener mOnMonthChangedListener
            = new CaldroidFragment.OnMonthChangedListener() {
        @Override
        public void onChanged(String month) {
            mActivity.setActionBarTitle(month);
        }
    };

    @Override
    protected BaseFrameLayout getContentView() {
        mView = new CalendarView(getActivity());
        mViewModel = new CalendarViewModel(mActivity, mOnMonthChangedListener,
                mOnDateClickListener, mCaldroidListener, mSelectedDate);
        mViewModel.bind(mView);
        return mView;
    }
}
