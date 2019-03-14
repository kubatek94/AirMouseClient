package uk.co.kubatek94.airmouse;

import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by kubatek94 on 22/03/16.
 */
public class KeyboardManager {
    private OnKeyboardShowListener onKeyboardShowListener;
    private OnKeyboardHideListener onKeyboardHideListener;

    private InputMethodManager inputMethodManager;
    private View keyboardAttachView = null;

    private boolean keyboardVisible = false;

    public KeyboardManager(Activity activity) {
        inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public boolean isKeyboardVisible() {
        return keyboardVisible;
    }

    public void showKeyboard() {
        if (keyboardAttachView != null) {
            inputMethodManager.showSoftInput(keyboardAttachView, InputMethodManager.SHOW_FORCED);

            keyboardVisible = true;

            if (onKeyboardShowListener != null) {
                onKeyboardShowListener.onKeyboardShow();
            }
        }
    }

    public void hideKeyboard() {
        if (keyboardAttachView != null) {
            inputMethodManager.hideSoftInputFromWindow(keyboardAttachView.getWindowToken(), 0);

            keyboardVisible = false;

            if (onKeyboardHideListener != null) {
                onKeyboardHideListener.onKeyboardHide();
            }
        }
    }

    public void toggleKeyboard(View keyboardAttachView) {
        if (keyboardAttachView != null) {
            this.keyboardAttachView = keyboardAttachView;

            if (keyboardVisible) {
                hideKeyboard();
            } else {
                showKeyboard();
            }
        } else {
            if (isKeyboardVisible()) {
                hideKeyboard();
            }
        }
    }

    public void setOnKeyboardHideListener(OnKeyboardHideListener listener) {
        onKeyboardHideListener = listener;
    }

    public void setOnKeyboardShowListener(OnKeyboardShowListener listener) {
        onKeyboardShowListener = listener;
    }

    public interface OnKeyboardHideListener {
        void onKeyboardHide();
    }

    public interface OnKeyboardShowListener {
        void onKeyboardShow();
    }
}
