package dk.shape.churchdesk.viewmodel;

import android.content.Context;

import java.util.List;

import dk.shape.churchdesk.R;
import dk.shape.churchdesk.entity.Person;
import dk.shape.churchdesk.entity.User;
import dk.shape.churchdesk.fragment.People;
import dk.shape.churchdesk.view.PersonItemView;
import dk.shape.churchdesk.view.RefreshLoadMoreView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by chirag on 20/09/16.
 */
public class PeopleViewModel extends BaseDashboardViewModel<RefreshLoadMoreView, List<Person>> {

    private final User mCurrentUser;
    private final PeopleItemViewModel.OnPersonClickListener mOnPersonClickListener;

    private List<Person> mPeople;
    private List<PeopleItemViewModel> mAllPeopleModels;
    private Context mContext;

    public PeopleViewModel(User currentUser, OnRefreshData onRefreshData,
                           PeopleItemViewModel.OnPersonClickListener onPersonClickListener) {
        super(onRefreshData);
        this.mCurrentUser = currentUser;
        this.mOnPersonClickListener = onPersonClickListener;
    }

    @Override
    public void extBind(RefreshLoadMoreView view, List<Person> data)
    {
        this.mPeople = data;
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
        refreshView.mDataList.setAdapter(new PeopleAdapter());
    }

    private class PeopleAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mPeople != null) {
                return mPeople.size();
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
            PersonItemView view = new PersonItemView(mContext);
            PeopleItemViewModel viewModel = new PeopleItemViewModel(
            mPeople.get(position), mOnPersonClickListener);

            viewModel.bind(view);
            return view;
        }
    }
}

