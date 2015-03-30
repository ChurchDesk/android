package dk.shape.churchdesk.view;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TabHost;

import java.util.ArrayList;

import butterknife.InjectView;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.viewmodel.TabItemViewModel;

/**
 * Created by steffenkarlsson on 23/03/15.
 */
public class DashboardView extends BaseFrameLayout {

    private class TabElement {

        @StringRes
        private int titleRes;

        @DrawableRes
        private int iconRes;

        public TabElement(int titleRes, int iconRes) {
            this.titleRes = titleRes;
            this.iconRes = iconRes;
        }
    }

    private ArrayList<TabElement> tabElements = new ArrayList<TabElement>() {{
        add(new TabElement(R.string.dashboard_tab_1, R.drawable.tab_calendar));
        add(new TabElement(R.string.dashboard_tab_2, R.drawable.tab_invitation));
        add(new TabElement(R.string.dashboard_tab_3, R.drawable.tab_mail));
    }};

    @InjectView(android.R.id.tabcontent)
    protected FrameLayout content;

    @InjectView(R.id.dashboardTabHost)
    protected TabHost tabHost;

    public DashboardView(Context context) {
        super(context);
    }

    public DashboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_dashboard;
    }

    public void init(TabHost.OnTabChangeListener onTabChangeListener) {
        tabHost.setup();
        addTabs();
        tabHost.setOnTabChangedListener(onTabChangeListener);
    }

    private void addNewTab(@StringRes int nameRes, @DrawableRes int iconRes) {
        TabItemView view = new TabItemView(getContext());
        TabItemViewModel viewModel = new TabItemViewModel(nameRes, iconRes);
        viewModel.bind(view);
        tabHost.addTab(tabHost.newTabSpec(getContext().getString(nameRes).toLowerCase())
                .setIndicator(view).setContent(android.R.id.tabcontent));
    }

    public void setContent(BaseFrameLayout content) {
        if (content == null)
            this.content.removeAllViews();
        else
            this.content.addView(content);
        this.content.invalidate();
    }

    private void addTabs() {
        for (TabElement tabElement : tabElements)
            addNewTab(tabElement.titleRes, tabElement.iconRes);
    }
}
