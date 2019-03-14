package uk.co.kubatek94.airmouse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import uk.co.kubatek94.airmouse.messages.KeyDownMessage;
import uk.co.kubatek94.airmouse.messages.KeyUpMessage;
import uk.co.kubatek94.airmouse.messages.MouseClickMessage;
import uk.co.kubatek94.airmouse.messages.MouseMoveMessage;
import uk.co.kubatek94.airmouse.messages.MouseScrollMessage;
import uk.co.kubatek94.airmouse.view.TouchPadView;

public class TouchPadFragment extends NavigationItem {
    private TouchPadView touchPad;

    private KeyboardManager keyboardManager;
    private MenuItem keyboardItem;

    private Server connectedServer;

    public TouchPadFragment() {
        itemId = R.id.item_connected_server_touchpad;
    }

    @Override
    public void onCreate(Bundle savedStateInstance) {
        super.onCreate(savedStateInstance);
        super.setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.air_mouse, menu);
        super.onCreateOptionsMenu(menu, inflater);
        keyboardItem = menu.findItem(R.id.action_toggle_keyboard);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_toggle_keyboard:
                keyboardManager.toggleKeyboard(touchPad);
                return true;

            case R.id.action_server_disconnect:
                connectedServer.disconnect();
                return true;
        }

        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_touch_pad, container, false);

        keyboardManager = listener.getKeyboardManager();

        touchPad = (TouchPadView) view.findViewById(R.id.touchPadView);

        touchPad.setOnKeyDownListener(key -> connectedServer.sendMessage(new KeyDownMessage(key)));

        touchPad.setOnKeyUpListener(key -> connectedServer.sendMessage(new KeyUpMessage(key)));

        touchPad.setOnMoveListener((dx, dy) -> connectedServer.sendMessage(new MouseMoveMessage(dx, dy)));

        touchPad.setOnScrollListener(value -> connectedServer.sendMessage(new MouseScrollMessage(value)));

        touchPad.setOnClickListener((TouchPadView.OnClickListener.Button button) -> connectedServer.sendMessage(new MouseClickMessage(button)));

        touchPad.setOnKeyboardHideListener(() -> keyboardManager.hideKeyboard());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        connectedServer = listener.getConnectedServerManager().getConnectedServer();

        //read server settings and update touchPad
        ServerSettings settings = listener.getConnectedServerManager().getConnectedServerSettings();
        touchPad.setSensitivity(settings.touchPadSensitivity.getValue());
        touchPad.setScrollDelay(settings.scrollDelay.getValue());

        //change keyboard icon, when keyboard opens or closes
        keyboardManager.setOnKeyboardShowListener(() -> keyboardItem.setIcon(R.drawable.keyboard_off));
        keyboardManager.setOnKeyboardHideListener(() -> keyboardItem.setIcon(R.drawable.keyboard));
    }

    @Override
    public void onStop() {
        super.onStop();
        connectedServer = null;
        keyboardManager.setOnKeyboardShowListener(null);
        keyboardManager.setOnKeyboardHideListener(null);
    }
}
