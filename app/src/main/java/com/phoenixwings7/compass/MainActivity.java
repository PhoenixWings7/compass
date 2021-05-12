package com.phoenixwings7.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor compassSensor;
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];
    private float rotationOffset = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        compassSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, compassSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);

        // this works perfectly when the device is parallel to the ground
        // why is data not reliable when the device isn't parallel to the ground?
        double angle = SensorManager.getOrientation(rotationMatrix, orientation)[0]; // in radians
        int azimuth = (int) Math.toDegrees(angle);
        animateCompassRotation(azimuth);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    private void animateCompassRotation(float azimuth) {
        ImageView compassImage = findViewById(R.id.compass);

        compassImage.setRotation(azimuth);
        compassImage.animate();
    }
}