package uk.co.kubatek94.airmouse;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.HashMap;

public class AirMouse extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NavigationItemListener {
    private HashMap<Integer, NavigationItem> navigationItems = new HashMap<>();
    private NavigationItem currentNavigationItem = null;

    private KeyboardManager keyboardManager = null;
    private NavigationView navigationView = null;
    private Toolbar toolbar = null;

    private ConnectedServerManager connectedServerManager = null;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_mouse);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationItems.put(R.id.item_servers, new ServersFragment());
        navigationItems.put(R.id.item_connected_server_airremote, new AirRemoteFragment());
        navigationItems.put(R.id.item_connected_server_touchpad, new TouchPadFragment());
        navigationItems.put(R.id.item_connected_server_settings, new SettingsFragment());

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.air_mouse_activity);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {
            NavigationItem item = navigationItems.get(R.id.item_servers);

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            item.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, item).commit();
        }

        connectedServerManager = new ConnectedServerManager(this, (ServersFragment) navigationItems.get(R.id.item_servers));
        keyboardManager = new KeyboardManager(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        //pause connection to the server
        connectedServerManager.pauseServer();
    }

    @Override
    public void onResume() {
        super.onResume();

        //resume connection
        connectedServerManager.resumeServer();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        setCurrentMenuItem(id);

        return true;
    }

    public void setCurrentMenuItem(int id) {
        NavigationItem navItem = navigationItems.get(id);

        if (navItem != null && currentNavigationItem != navItem) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, navItem);

            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void setCurrentItem(NavigationItem item) {
        if (item != null && currentNavigationItem != item) {
            currentNavigationItem = item;
            navigationView.setCheckedItem(currentNavigationItem.itemId);
        }
    }

    public void clearBackstack() {
        try {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (IllegalStateException e) { }
    }

    @Override
    public ConnectedServerManager getConnectedServerManager() {
        return connectedServerManager;
    }

    public CoordinatorLayout getCoordinatorLayout() {
        return coordinatorLayout;
    }

    public KeyboardManager getKeyboardManager() {
        return keyboardManager;
    }
}
