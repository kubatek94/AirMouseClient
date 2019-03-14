package uk.co.kubatek94.airmouse;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.List;

public class ServersFragment extends NavigationItem {
    private Context context = null;
    private ListView serversListView = null;
    private ServersAdapter serversAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ConnectedServerManager connectedServerManager;

    private boolean refreshServers = true;

    public ServersFragment() {
        itemId = R.id.item_servers;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_servers, container, false);

        serversListView = (ListView) view.findViewById(R.id.servers_list);
        serversListView.setAdapter(serversAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            updateServers(() -> getActivity().runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false)));
        });

        serversListView.setOnItemClickListener((parent, v, position, id) -> {
            Server server = serversAdapter.getItem(position);

            connectedServerManager = listener.getConnectedServerManager();

            if (connectedServerManager != null) {
                connectedServerManager.setServer(server);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        refreshServers = true;
        refreshServers();
    }

    private void refreshServers() {
        if (refreshServers) {
            updateServers(() -> {
               refreshServers();
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshServers = false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;
        serversAdapter = new ServersAdapter(context);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateServers(null);
    }

    public void updateServers(TaskCallback callback) {
        new Thread(() -> {
            List<Server> servers = Server.discover(context);

            getActivity().runOnUiThread(() -> serversAdapter.set(servers));

            if (callback != null) {
                callback.onComplete();
            }
        }).start();
    }

    public interface TaskCallback {
        void onComplete();
    }
}
