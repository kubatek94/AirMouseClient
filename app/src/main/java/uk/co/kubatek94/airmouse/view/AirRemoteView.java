package uk.co.kubatek94.airmouse.view;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import uk.co.kubatek94.airmouse.ServerSettings;
import uk.co.kubatek94.airmouse.view.TouchPadView;

/**
 * Created by kubatek94 on 22/03/16.
 */
public class AirRemoteView extends TouchPadView implements SensorEventListener {
    protected SensorManager mSensorManager;
    protected Sensor mSensor;

    protected float[] orientationVector = new float[3];
    protected float[] rotationMatrixCurrent = new float[9];
    protected float[] rotationMatrixOld = new float[9];

    protected float moveDelay = ServerSettings.MOVE_DELAY_DEFAULT;

    public AirRemoteView(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        setSensitivity(ServerSettings.AIR_REMOTE_SENSITIVITY_DEFAULT);
    }

    public AirRemoteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSensitivity(ServerSettings.AIR_REMOTE_SENSITIVITY_DEFAULT);
    }

    public AirRemoteView(Context context) {
        super(context);
        setSensitivity(ServerSettings.AIR_REMOTE_SENSITIVITY_DEFAULT);
    }

    public void setMoveDelay(float moveDelay) {
        this.moveDelay = moveDelay;
    }

    public void startSensor() {
        if (mSensorManager == null) {
            mSensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        }

        if (mSensor == null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        }

        mSensorManager.registerListener(this, mSensor, 10000); //16.5 ms -> ~60fps
    }

    public void stopSensor() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            isMove = true;

            float currentY = event.getY();
            float dy = -(y - currentY);
            y = currentY;

            verticalScroll += dy;

            if (Math.abs(verticalScroll) > scrollDelay) {
                if (onScrollListener != null) {
                    onScrollListener.onScroll(verticalScroll < 0 ? 1 : -1); //scroll one up or down
                }
                verticalScroll = 0;
            }

            return true;
        } else {
            return super.onTouch(view, event);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        SensorManager.getRotationMatrixFromVector(rotationMatrixCurrent, event.values);
        SensorManager.getAngleChange(orientationVector, rotationMatrixCurrent, rotationMatrixOld);

        double zDelta = Math.toDegrees(orientationVector[0]); //z corresponds to change in X world coordinate (left and right movement)
        double xDelta = Math.toDegrees(orientationVector[1]); //x corresponds to change in Y world coordinate (up and down movement)

        if (Math.abs(xDelta) > moveDelay || Math.abs(zDelta) > moveDelay) {
            if (onMoveListener != null) {
                onMoveListener.onMove((float) (zDelta * sensitivity), (float) (xDelta * sensitivity));
            }
        }

        float[] temp = rotationMatrixOld;
        rotationMatrixOld = rotationMatrixCurrent;
        rotationMatrixCurrent = temp;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
