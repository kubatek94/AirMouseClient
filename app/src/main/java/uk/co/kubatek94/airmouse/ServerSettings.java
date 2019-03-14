package uk.co.kubatek94.airmouse;

import android.content.Context;
import android.content.SharedPreferences;

import com.annimon.stream.Stream;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kubatek94 on 01/04/16.
 */
public class ServerSettings implements Serializable {
    public static final float AIR_REMOTE_SENSITIVITY_DEFAULT = 50f;
    public static final float TOUCH_PAD_SENSITIVITY_DEFAULT = 1f;
    public static final float SCROLL_DELAY_DEFAULT = 10f;
    public static final float MOVE_DELAY_DEFAULT = 0.05f;

    private final static String PREFERENCE_TAG = "uk.co.kubatek94.airmouse.server.";

    private transient SharedPreferences sharedPreferences;
    private transient SettingChangeListener<Float> settingChangeListener;
    public transient final Setting<Float> airRemoteSensitivity;
    public transient final Setting<Float> touchPadSensitivity;
    public transient final Setting<Float> scrollDelay;
    public transient final Setting<Float> moveDelay;

    public ServerSettings(Context context, Server server) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_TAG + server.getAddress(), Context.MODE_PRIVATE);

        settingChangeListener = (setting) -> {
            //save setting to shared preferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat(setting.getTag(), setting.getValue());
            editor.commit();
        };

        airRemoteSensitivity = new Setting<>("AIR_REMOTE_SENSITIVITY", AIR_REMOTE_SENSITIVITY_DEFAULT, settingChangeListener);
        touchPadSensitivity = new Setting<>("TOUCH_PAD_SENSITIVITY", TOUCH_PAD_SENSITIVITY_DEFAULT, settingChangeListener);
        scrollDelay = new Setting<>("SCROLL_DELAY", SCROLL_DELAY_DEFAULT, settingChangeListener);
        moveDelay = new Setting<>("MOVE_DELAY", MOVE_DELAY_DEFAULT, settingChangeListener);

        //try to read in existing settings
        Stream.of(airRemoteSensitivity, touchPadSensitivity, scrollDelay, moveDelay).forEach((setting) -> {
            String tag = setting.getTag();

            if (sharedPreferences.contains(tag)) {
                //read setting from shared preferences
                setting.setValue(sharedPreferences.getFloat(tag, setting.getValue()));
            } else {
                //trigger setting change, to save default setting in shared preferences
                settingChangeListener.onSettingChange(setting);
            }
        });
    }

    public void resetToDefault() {
        airRemoteSensitivity.setValue(AIR_REMOTE_SENSITIVITY_DEFAULT);
        touchPadSensitivity.setValue(TOUCH_PAD_SENSITIVITY_DEFAULT);
        scrollDelay.setValue(SCROLL_DELAY_DEFAULT);
        moveDelay.setValue(MOVE_DELAY_DEFAULT);
    }

    public class Setting<T> {
        private List<SettingChangeListener<T>> changeListeners = new ArrayList<>(1);
        private String tag;
        private T value;

        public Setting(String tag, T value, SettingChangeListener<T> changeListener) {
            this.tag = tag;
            this.value = value;

            addChangeListener(changeListener);
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            if (this.tag != tag) {
                this.tag = tag;

                for (SettingChangeListener<T> listener : changeListeners) {
                    listener.onSettingChange(this);
                }
            }
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            if (this.value != value) {
                this.value = value;

                for (SettingChangeListener<T> listener : changeListeners) {
                    listener.onSettingChange(this);
                }
            }
        }

        public void addChangeListener(SettingChangeListener<T> changeListener) {
            changeListeners.add(changeListener);
        }

        public void removeChangeListener(SettingChangeListener changeListener) {
            changeListeners.remove(changeListener);
        }
    }

    public interface SettingChangeListener<T> {
        void onSettingChange(Setting<T> setting);
    }
}
