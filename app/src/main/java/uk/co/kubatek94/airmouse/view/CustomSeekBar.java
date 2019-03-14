package uk.co.kubatek94.airmouse.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.SeekBar;

import java.math.BigDecimal;
import java.util.HashMap;

import uk.co.kubatek94.airmouse.R;

/**
 * Created by kubatek94 on 01/04/16.
 */
public class CustomSeekBar extends SeekBar {
    public interface OnCustomSeekBarChangeListener {
        void onProgressChanged(CustomSeekBar seekBar, float progress, boolean fromUser);
        void onTouchStart(CustomSeekBar seekBar);
        void onTouchStop(CustomSeekBar seekBar);
    }

    private HashMap<String, Object> userData = new HashMap<>(2);
    private CustomSeekBar self = null;
    private OnCustomSeekBarChangeListener seekBarChangeListener = null;
    private float mMin = 0;
    private float mMax = 0;
    private float mStep = 1;
    private float mProgress = 0;

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        //parse attributes
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomSeekBar, 0, 0);

        //get custom attributes
        try {
            mMin = attributes.getFloat(R.styleable.CustomSeekBar_minValue, mMin);
            mMax = attributes.getFloat(R.styleable.CustomSeekBar_maxValue, mMax);
            mProgress = attributes.getFloat(R.styleable.CustomSeekBar_progressValue, mMin);
            mStep = attributes.getFloat(R.styleable.CustomSeekBar_valueStep, mStep);
        } finally {
            attributes.recycle();
        }

        //calculate and set number of integer steps
        super.setMax((int) ((mMax - mMin) / mStep));

        //calculate the value of progress bar and set it
        super.setProgress((int) ((mProgress/mStep) - mMin));

        self = this;

        //subscribe to seekbar changes
        super.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBarChangeListener != null) {
                    //calculate the actual value based on the min and step attributes
                    mProgress = round((progress * mStep) + mMin, 3);
                    seekBarChangeListener.onProgressChanged(self, mProgress, fromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (seekBarChangeListener != null) {
                    seekBarChangeListener.onTouchStart(self);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBarChangeListener != null) {
                    seekBarChangeListener.onTouchStop(self);
                }
            }
        });
    }

    public void setUserData(String name, Object data) {
        if (!userData.containsKey(name)) {
            userData.put(name, data);
        }
    }

    public Object getUserData(String name) {
        return userData.get(name);
    }

    public void clearUserData() {
        userData.clear();
    }

    public float getProgressValue() {
        return mProgress;
    }

    public float getMaxValue() {
        return mMax;
    }

    public float getMinValue() {
        return mMin;
    }

    public float getValueStep() {
        return mStep;
    }

    public void setProgressValue(float progress) {
        if (progress >= mMin && progress <= mMax) {
            super.setProgress(Math.round(((progress - mMin)/mStep)));
        }
    }

    public void setMaxValue(float max) {
        if (mMax != max) {
            mMax = max;

            super.setMax((int) ((mMax - mMin) / mStep));
        }
    }

    public void setMin(float min) {
        if (mMin != min) {
            mMin = min;

            super.setMax((int) ((mMax - mMin) / mStep));
        }
    }

    public void setStep(float step) {
        if (mStep != step) {
            mStep = step;

            super.setMax((int) ((mMax - mMin) / mStep));
        }
    }

    public void setOnCustomSeekBarChangeListener(OnCustomSeekBarChangeListener l) {
        seekBarChangeListener = l;
    }

    /**
     * Round to certain number of decimals
     *
     * @param d
     * @param decimalPlace
     * @return
     */
    private static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
}
