package uk.co.kubatek94.airmouse;

import android.hardware.SensorManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

/**
 * Created by kubatek94 on 18/03/16.
 */
public interface NavigationItemListener {
    void setCurrentItem(NavigationItem item);
    void setCurrentMenuItem(int id);
    ConnectedServerManager getConnectedServerManager();
    CoordinatorLayout getCoordinatorLayout();
    void clearBackstack();
    KeyboardManager getKeyboardManager();
}
