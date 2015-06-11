package dk.shape.churchdesk.util;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import dk.shape.library.collections.adapters.RecyclerAdapter;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 6/5/15.
 */
public class OnStateScrollListener extends RecyclerView.OnScrollListener {

    public interface StateScrollListener<T extends ViewModel> {
        void onFirstItemChanged(T model, int position);
        void onLastItemChanged(T model, int position);
    }

    private final StateScrollListener mListener;

    private int mPosition = 0;
    private LinearLayoutManager mManager;
    private RecyclerAdapter mAdapter;

    public OnStateScrollListener(StateScrollListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (mManager == null) {
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof StaggeredGridLayoutManager)
                Log.e("OnStateScrollListener", "StaggeredGridLayoutManager is not supported");
            mManager = (LinearLayoutManager) manager;
        }

        if (mAdapter == null) {
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (!(adapter instanceof RecyclerAdapter))
                Log.e("OnStateScrollListener", "Only RecyclerAdapter is accepted as adapter");
            mAdapter = (RecyclerAdapter)adapter;
        }

        if (dy == 0)
            return;

        int position;
        if (dy < 0) {
            position = mManager.findFirstVisibleItemPosition();
            if (mPosition != position)
                mListener.onFirstItemChanged(mAdapter.getItem(position), position);
        } else {
            position = mManager.findLastVisibleItemPosition();
            if (mPosition != position)
                mListener.onLastItemChanged(mAdapter.getItem(position), position);
        }
        mPosition = position;
    }
}
