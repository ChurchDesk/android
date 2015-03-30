package dk.shape.churchdesk.viewmodel;

import dk.shape.churchdesk.view.BaseFrameLayout;
import dk.shape.library.viewmodel.ViewModel;

/**
 * Created by steffenkarlsson on 30/03/15.
 */
public abstract class BaseDashboardViewModel<T extends BaseFrameLayout, D> extends ViewModel<T> {

    public abstract void setData(D data);
}
