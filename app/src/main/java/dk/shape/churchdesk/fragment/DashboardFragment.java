package dk.shape.churchdesk.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TabHost;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;
import dk.shape.churchdesk.R;
import dk.shape.churchdesk.view.TabItemView;
import dk.shape.churchdesk.viewmodel.TabItemViewModel;

/**
 * Created by steffenkarlsson on 17/03/15.
 */
public class DashboardFragment extends BaseFragment {

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

    @InjectView(R.id.dashboardTabHost)
    protected TabHost tabHost;

    @InjectView(R.id.action_event)
    protected FloatingActionButton actionEvent;

    @InjectView(R.id.action_message)
    protected FloatingActionButton actionMessage;

    @Override
    protected int getTitleResource() {
        return R.string.menu_dashboard;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_dashboard;
    }

    @Override
    public void onStart() {
        super.onStart();
        actionEvent.setIconDrawable(resize(getResources().getDrawable(R.drawable.create_event_square)));
        actionMessage.setIconDrawable(resize(getResources().getDrawable(R.drawable.create_message_square)));
    }

    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        int bWidth = b.getWidth();
        float factor = getResources().getDimension(R.dimen.fib_icon) / bWidth;
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, Math.round(bWidth * factor),
                Math.round(b.getHeight() * factor), false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }

    @OnClick(R.id.action_message)
    void onClickActionMessage() {

    }

    @OnClick(R.id.action_event)
    void onClickActionEvent() {

    }

    @Override
    protected void onCreateView(View rootView) {
        tabHost.setup();
        setCurrentTab("events");
        tabHost.setOnTabChangedListener(mOnTabChangeListener);
    }

    private void addNewTab(String tag, @StringRes int nameRes, @DrawableRes int iconRes) {
        TabItemView view = new TabItemView(getActivity());
        TabItemViewModel viewModel = new TabItemViewModel(nameRes, iconRes);
        viewModel.bind(view);
        tabHost.addTab(tabHost.newTabSpec(tag).setIndicator(view).setContent(android.R.id.tabcontent));
    }

    private void setCurrentTab(String tabId) {
        for (TabElement tabElement : tabElements)
            addNewTab(tabId, tabElement.titleRes, tabElement.iconRes);
    }

    private TabHost.OnTabChangeListener mOnTabChangeListener = new TabHost.OnTabChangeListener() {
        @Override
        public void onTabChanged(String tabId) {

        }
    };
}
