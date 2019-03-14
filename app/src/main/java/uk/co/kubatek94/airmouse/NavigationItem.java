package uk.co.kubatek94.airmouse;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.Menu;

/**
 * Created by kubatek94 on 18/03/16.
 */
public abstract class NavigationItem extends Fragment {
    public int itemId = -1;
    protected NavigationItemListener listener;

    public NavigationItem() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof NavigationItemListener) {
            listener = (NavigationItemListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement NavigationItemListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (listener != null) {
            listener.setCurrentItem(this);
        }
    }
}
