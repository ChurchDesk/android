package dk.shape.churchdesk.viewmodel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Segment;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.view.RefreshLoadMoreView;
import dk.shape.churchdesk.view.SegmentItemView;

/**
 * Created by chirag on 16/02/2017.
 */
public class SegmentsViewModel extends BaseDashboardViewModel<RefreshLoadMoreView, List<Segment>> {

    private final User mCurrentUser;
    private final SegmentItemViewModel.OnSegmentClickListener mOnSegmentClickListener;

    private List<Segment> mSegments;
    private Context mContext;

    public SegmentsViewModel(User currentUser, OnRefreshData onRefreshData,
                           SegmentItemViewModel.OnSegmentClickListener onSegmentClickListener) {
        super(onRefreshData);
        this.mCurrentUser = currentUser;
        this.mOnSegmentClickListener = onSegmentClickListener;
    }

    @Override
    public void extBind(RefreshLoadMoreView view, List<Segment> data)
    {
        this.mSegments = data;
        super.extBind(view, data);
    }

    @Override
    public int getEmptyRes() {
        return R.string.no_people;
    }

    @Override
    public void bind(RefreshLoadMoreView refreshView) {
        mContext = refreshView.getContext();
        refreshView.swipeContainer.setRefreshing(false);
        refreshView.swipeContainer.setColorSchemeResources(R.color.foreground_blue);
        refreshView.swipeContainer.setOnRefreshListener(mOnRefreshListener);
        refreshView.mDataList.setAdapter(new SegmentAdapter());
    }

    private class SegmentAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mSegments != null) {
                return mSegments.size();
            } else return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SegmentItemView view = new SegmentItemView(mContext);
            SegmentItemViewModel viewModel = new SegmentItemViewModel(
                    mSegments.get(position), mCurrentUser, mOnSegmentClickListener);
            viewModel.bind(view);
            return view;
        }
    }
}