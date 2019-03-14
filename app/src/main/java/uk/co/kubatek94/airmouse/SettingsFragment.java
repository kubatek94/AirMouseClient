package uk.co.kubatek94.airmouse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.annimon.stream.Stream;

import uk.co.kubatek94.airmouse.view.CustomSeekBar;

public class SettingsFragment extends NavigationItem implements CustomSeekBar.OnCustomSeekBarChangeListener {
    private CustomSeekBar airRemoteSensitivityBar;
    private CustomSeekBar touchPadSensitivityBar;
    private CustomSeekBar scrollDelayBar;
    private CustomSeekBar moveDelayBar;

    private ServerSettings connectedServerSettings;

    public SettingsFragment() {
        itemId = R.id.item_connected_server_settings;
    }

    @Override
    public void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        super.setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.server_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_reset_settings:
                connectedServerSettings.resetToDefault();
                refreshUi();
                return true;
        }

        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        airRemoteSensitivityBar = (CustomSeekBar) view.findViewById(R.id.airremotesensitivity_bar);
        airRemoteSensitivityBar.setOnCustomSeekBarChangeListener(this);
        airRemoteSensitivityBar.setUserData("view", view.findViewById(R.id.airremotesensitivity_value));

        touchPadSensitivityBar = (CustomSeekBar) view.findViewById(R.id.touchpadsensitivity_bar);
        touchPadSensitivityBar.setOnCustomSeekBarChangeListener(this);
        touchPadSensitivityBar.setUserData("view", view.findViewById(R.id.touchpadsensitivity_value));

        scrollDelayBar = (CustomSeekBar) view.findViewById(R.id.scrolldelay_bar);
        scrollDelayBar.setOnCustomSeekBarChangeListener(this);
        scrollDelayBar.setUserData("view", view.findViewById(R.id.scrolldelay_value));

        moveDelayBar = (CustomSeekBar) view.findViewById(R.id.movedelay_bar);
        moveDelayBar.setOnCustomSeekBarChangeListener(this);
        moveDelayBar.setUserData("view", view.findViewById(R.id.movedelay_value));

        return view;
    }

    private void refreshUi() {
        //read in settings for the connected server and update ui
        if (connectedServerSettings != null) {
            Stream.of(airRemoteSensitivityBar, touchPadSensitivityBar, scrollDelayBar, moveDelayBar).forEach((seekBar) -> {
                ServerSettings.Setting<Float> setting = (ServerSettings.Setting<Float>) seekBar.getUserData("setting");
                seekBar.setProgressValue(setting.getValue());
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        connectedServerSettings = listener.getConnectedServerManager().getConnectedServerSettings();

        airRemoteSensitivityBar.setUserData("setting", connectedServerSettings.airRemoteSensitivity);
        touchPadSensitivityBar.setUserData("setting", connectedServerSettings.touchPadSensitivity);
        scrollDelayBar.setUserData("setting", connectedServerSettings.scrollDelay);
        moveDelayBar.setUserData("setting", connectedServerSettings.moveDelay);

        refreshUi();
    }

    @Override
    public void onStop() {
        super.onStop();
        connectedServerSettings = null;
        Stream.of(airRemoteSensitivityBar, touchPadSensitivityBar, scrollDelayBar, moveDelayBar).forEach(seekBar -> seekBar.clearUserData());
    }

    @Override
    public void onProgressChanged(CustomSeekBar seekBar, float progress, boolean fromUser) {
        TextView value = (TextView) seekBar.getUserData("view");
        value.setText(String.valueOf(progress));
    }

    @Override
    public void onTouchStart(CustomSeekBar seekBar) {}

    @Override
    public void onTouchStop(CustomSeekBar seekBar) {
        float value = seekBar.getProgressValue();

        //save current value to server settings
        ServerSettings.Setting<Float> setting = (ServerSettings.Setting<Float>) seekBar.getUserData("setting");
        setting.setValue(value);
    }
}
