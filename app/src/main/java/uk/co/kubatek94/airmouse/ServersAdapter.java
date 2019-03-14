package uk.co.kubatek94.airmouse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by kubatek94 on 21/03/16.
 */
public class ServersAdapter extends HashMapAdapter<Server> {

    public ServersAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Server server = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_server, parent, false);
        }

        // Lookup view for data population
        TextView serverName = (TextView) convertView.findViewById(R.id.server_name);
        TextView serverAddress = (TextView) convertView.findViewById(R.id.server_address);

        // Populate the data into the template view using the data object
        serverName.setText(server.getName());
        serverAddress.setText(server.getAddress());

        // Return the completed view to render on screen
        return convertView;
    }
}
