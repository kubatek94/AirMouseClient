package uk.co.kubatek94.airmouse.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import uk.co.kubatek94.airmouse.ServerSettings;

/**
 * Created by kubatek94 on 22/03/16.
 */
public class TouchPadView extends EditText {
    protected OnKeyDownListener onKeyDownListener;
    protected OnKeyUpListener onKeyUpListener;

    protected OnKeyboardHideListener onKeyboardHideListener;
    protected OnMoveListener onMoveListener;
    protected OnScrollListener onScrollListener;
    protected OnClickListener onClickListener;

    protected boolean isMove = false;
    protected boolean isLongClick = false;
    protected boolean isSinglePointer = true;

    protected float sensitivity = ServerSettings.TOUCH_PAD_SENSITIVITY_DEFAULT;
    protected float scrollDelay = ServerSettings.SCROLL_DELAY_DEFAULT;

    protected float x = 0;
    protected float y = 0;

    protected float verticalScroll = 0;


    public TouchPadView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        init();
    }

    public TouchPadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouchPadView(Context context) {
        super(context);
        init();
    }

    private void init() {
        super.setOnEditorActionListener((v, actionCode, event) -> {
            if ((actionCode & EditorInfo.IME_MASK_ACTION) == actionCode) {
                return true;
            } else {
                return false;
            }
        });

        super.setOnLongClickListener(v -> {
            if (!isMove) {
                isLongClick = true;

                if (onClickListener != null) {
                    onClickListener.onClick(OnClickListener.Button.RIGHT);
                }

                return true;
            }
            return false;
        });

        super.setOnTouchListener((view, event) -> onTouch(view, event));
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }

    public void setScrollDelay(float scrollDelay) {
        this.scrollDelay = scrollDelay;
    }

    public boolean onTouch(View view, MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            //POINTER UP + DOWN are only multitouch
            case MotionEvent.ACTION_POINTER_DOWN:
                isSinglePointer = false;
                break;

            case MotionEvent.ACTION_MOVE:
                float currentX = event.getX();
                float currentY = event.getY();

                float dx = -(x - currentX);
                float dy = -(y - currentY);

                x = currentX;
                y = currentY;

                isMove = true;

                if (isSinglePointer) {
                    if (onMoveListener != null) {
                        onMoveListener.onMove(dx * sensitivity, dy * sensitivity);
                    }
                } else {
                    verticalScroll += dy;

                    if (Math.abs(verticalScroll) > scrollDelay) {
                        if (onScrollListener != null) {
                            onScrollListener.onScroll(verticalScroll < 0 ? 1 : -1); //scroll one up or down
                        }
                        verticalScroll = 0;
                    }
                }
                break;


            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (event.getPointerCount() == 1) {
                    isSinglePointer = true;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (!isMove && !isLongClick) {
                    if (onClickListener != null) {
                        onClickListener.onClick(OnClickListener.Button.LEFT);
                    }
                }

                isSinglePointer = true;
                isMove = false;
                verticalScroll = 0;
                isLongClick = false;

                break;
        }

        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (onKeyUpListener != null) {
            onKeyUpListener.onKeyUp(keyCode);
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (onKeyDownListener != null) {
            onKeyDownListener.onKeyDown(keyCode);
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        System.out.println("Multiple: " + event);
        /*if(keyCode == KeyEvent.KEYCODE_UNKNOWN) {
            String s = event.getCharacters();
            for(int i = 0; i < s.length(); i++) {
                onKeyPressListener.onKeyPress(s.charAt(i));
            }
        } else {
            for(int i = 0; i < repeatCount; ++i)
                onKeyDown(keyCode, event);
        }*/

        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    /* override to hide keyboard when back key is pressed */
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (onKeyboardHideListener != null) {
                onKeyboardHideListener.onKeyboardHide();
            }
        }

        return super.onKeyPreIme(keyCode, event);
    }

    /** Set listeners here */
    public void setOnKeyDownListener(OnKeyDownListener listener) {
        onKeyDownListener = listener;
    }

    public void setOnKeyUpListener(OnKeyUpListener listener) {
        onKeyUpListener = listener;
    }

    public void setOnMoveListener(OnMoveListener listener) {
        onMoveListener = listener;
    }

    public void setOnScrollListener(OnScrollListener listener) {
        onScrollListener = listener;
    }

    public void setOnClickListener(OnClickListener listener) {
        onClickListener = listener;
    }

    public void setOnKeyboardHideListener(OnKeyboardHideListener listener) { onKeyboardHideListener = listener; }
    /**
     *  Functional interfaces for all events.
     *  Single event per interface, so that lambda functions can be used.
     */
    public interface OnKeyDownListener {
        void onKeyDown(int keyCode);
    }

    public interface OnKeyUpListener {
        void onKeyUp(int keyCode);
    }

    public interface OnMoveListener {
        void onMove(float dx, float dy);
    }

    public interface  OnScrollListener {
        void onScroll(float value);
    }

    public interface OnClickListener {
        enum Button {LEFT, RIGHT};
        void onClick(Button button);
    }

    public interface OnKeyboardHideListener {
        void onKeyboardHide();
    }
}
