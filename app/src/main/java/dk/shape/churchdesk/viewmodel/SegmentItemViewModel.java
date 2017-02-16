package dk.shape.churchdesk.viewmodel;

import android.graphics.Color;
import android.view.View;

import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Segment;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.view.SegmentItemView;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by chirag on 16/02/2017.
 */
public class SegmentItemViewModel extends ViewModel<SegmentItemView> {
    public interface OnSegmentClickListener {
        void onClick(Segment segment);
    }

    private final OnSegmentClickListener mListener;
    private final User mCurrentUser;
    private final Segment mSegment;

    public SegmentItemViewModel(Segment segment, User currentUser,
                               OnSegmentClickListener listener) {
        this.mSegment = segment;
        this.mCurrentUser = currentUser;
        this.mListener = listener;
    }

    @Override
    public void bind(SegmentItemView segmentItemView) {
        //to display elements in person cell
        segmentItemView.mDivider.setVisibility(View.VISIBLE);
        segmentItemView.mContentView.setVisibility(View.VISIBLE);
        segmentItemView.mName.setTextColor(Color.rgb(0, 0, 0));
        segmentItemView.mAbsenceIcon.setVisibility(View.GONE);
        segmentItemView.mName.setText(mSegment.mName);
        segmentItemView.mLocationWrapper.setVisibility(View.INVISIBLE);
        segmentItemView.mSegmentColor.setVisibility(View.INVISIBLE);
        segmentItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClick(mSegment);
            }
        });
    }
}

