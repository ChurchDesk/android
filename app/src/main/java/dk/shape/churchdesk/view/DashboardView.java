package dk.shape.churchdesk.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.viewpagerindicator.TabPageIndicator;

import butterknife.InjectView;
import dk.shape.churchdesk.R;

/**
 * Created by steffenkarlsson on 23/03/15.
 */
public class DashboardView extends BaseFrameLayout {

    @InjectView(R.id.pager)
    protected ViewPager mPager;

    @InjectView(R.id.indicator)
    protected TabPageIndicator mIndicator;

    public DashboardView(Context context) {
        super(context);
    }

    public DashboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private int _currentlySelectedPage = 0;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_dashboard;
    }

    public void init(PagerAdapter adapter) {
        mPager.setAdapter(adapter);
        mPager.setCurrentItem(_currentlySelectedPage);

        mIndicator.setViewPager(mPager);
        mIndicator.setCurrentItem(_currentlySelectedPage);

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                _currentlySelectedPage = position;
                mIndicator.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


}
