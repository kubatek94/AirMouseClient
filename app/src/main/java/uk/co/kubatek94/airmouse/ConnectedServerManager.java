package uk.co.kubatek94.airmouse;

import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.view.MenuItem;

/**
 * Created by kubatek94 on 18/03/16.
 */
public class ConnectedServerManager implements Server.ServerListener {
    private transient AirMouse activity;
    private transient ServersFragment serversFragment;

    private Server nextServer;
    private Server connectedServer;
    private ServerSettings connectedServerSettings;

    private transient MenuItem connectedServerItem;

    public ConnectedServerManager(AirMouse activity, ServersFragment serversFragment) {
        this.activity = activity;
        this.serversFragment = serversFragment;

        connectedServerItem = ((NavigationView) activity.findViewById(R.id.nav_view)).getMenu().findItem(R.id.item_connected_server);
    }

    public Server getConnectedServer() {
        return connectedServer;
    }

    public ServerSettings getConnectedServerSettings() { return connectedServerSettings; }

    public void pauseServer() {
        if (connectedServer != null) {
            connectedServer.setServerListener(null);
            connectedServer.disconnect();
        }
    }

    public void resumeServer() {
        if (connectedServer != null) {
            connectedServer.setServerListener(this);
            new Thread(() -> connectedServer.connect()).start();
        }
    }

    public void setServer(Server server) {
        server.setServerListener(this);

        new Thread(() -> {
            if (connectedServer != null) {
                nextServer = server;
                connectedServer.disconnect();
            } else {
                server.connect();
            }
        }).start();
    }

    @Override
    public void onServerConnected(Server server) {
        //if we haven't resumed
        if (connectedServer != server) {
            connectedServer = server;
            connectedServerSettings = new ServerSettings(activity, server);
            nextServer = null;

            activity.runOnUiThread(() -> {
                connectedServerItem.setTitle(server.getName() + " (" + server.getAddress() + ")");
                connectedServerItem.setVisible(true);

                Snackbar.make(activity.getCoordinatorLayout(), "Connected to " + server.getName(), Snackbar.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public void onServerConnectError(Server server) {
        connectedServer = null;
        connectedServerSettings = null;
        nextServer = null;

        activity.runOnUiThread(() -> {
            connectedServerItem.setVisible(false);
            activity.setCurrentMenuItem(serversFragment.itemId);
            activity.clearBackstack();

            Snackbar.make(activity.getCoordinatorLayout(), "Couldn't connect to " + server.getName(), Snackbar.LENGTH_SHORT).show();
            serversFragment.updateServers(null);
        });
    }

    @Override
    public void onServerDisconnected(Server server) {
        activity.runOnUiThread(() -> {
            connectedServerItem.setVisible(false);
            activity.setCurrentMenuItem(serversFragment.itemId);
            activity.clearBackstack();

            Snackbar.make(activity.getCoordinatorLayout(), "Disconnected from " + server.getName(), Snackbar.LENGTH_SHORT).show();
        });

        connectedServer = null;
        connectedServerSettings = null;

        if (nextServer != null) {
            nextServer.connect();
        }
    }
}
