package com.phoenixwings7.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener, CompassMVP.View {
    private CompassMVP.Presenter mainPresenter;
    private SensorManager sensorManager;
    private Sensor compassSensor;
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        compassSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mainPresenter = new MainPresenter(this);
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

        mainPresenter.onAzimuthChanged(azimuth);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    @Override
    public void animateCompassRotation(float azimuth) {
        ImageView compassImage = findViewById(R.id.compass);

        compassImage.setRotation(-azimuth);
        compassImage.animate();
    }

    @Override
    public void setButtonAction() {
        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetCoordinationBtnClicked(view);
            }
        });
    }

    private void onSetCoordinationBtnClicked(View view) {
        LayoutInflater inflater = this.getLayoutInflater();
        View popupView = inflater.inflate(R.layout.coordinates_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup to also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }
}