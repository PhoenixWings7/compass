package com.phoenixwings7.compass;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements SensorEventListener, CompassMVP.View {
    public static final int INTERVAL_LENGTH = 30; // seconds
    public static final int FASTEST_INTERVAL_LENGTH = 5; // seconds

    private CompassMVP.Presenter mainPresenter;
    private SensorManager sensorManager;
    private Sensor compassSensor;
    private final float[] rotationMatrix = new float[9];
    private final float[] orientation = new float[3];

    // Google's API for location services
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private float destinationLatitude;
    private float destinationLongitude;


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
    public void setUpLocationService() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000 * INTERVAL_LENGTH); // milliseconds
        locationRequest.setFastestInterval(1000 * FASTEST_INTERVAL_LENGTH); // milliseconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public boolean checkLocationPermission() {
        int checkResult = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return (checkResult == PackageManager.PERMISSION_GRANTED);
    }

    @SuppressLint("MissingPermission") // checking permission in MainPresenter before calling this method
    @Override
    public void updateLocation() {
        Task<Location> locationTask = fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null);
        locationTask.addOnSuccessListener(this, location -> {
            mainPresenter.onUserLocationChanged(location, destinationLatitude, destinationLongitude);
        });
    }

    @Override
    public void updateDistanceUI(int metersToTarget) {
        TextView distance = findViewById(R.id.distance_to_destination_tv);
        distance.setText(getString(R.string.distance_to_destination, metersToTarget));
    }

    @Override
    public void updateDirectionUI(int rotationDegrees) {

    }

    @Override
    public void setButtonsActions() {
        Button setDestinationBtn = findViewById(R.id.popup_widow_button);

        setDestinationBtn.setOnClickListener(this::onSetDestinationBtnClicked);
    }

    private void onSaveDestinationBtnClicked(View view) {
        View rootView = view.getRootView();
        EditText latitudeEditText = rootView.findViewById(R.id.latitude);
        EditText longitudeEditText = rootView.findViewById(R.id.longitude);

        if ((latitudeEditText == null) || (longitudeEditText == null)) {
            return;
        }

        float latitude = Float.parseFloat(latitudeEditText.getText().toString());
        float longitude = Float.parseFloat(longitudeEditText.getText().toString());

        this.destinationLatitude = latitude;
        this.destinationLongitude = longitude;
    }

    private void onSetDestinationBtnClicked(View view) {
        LayoutInflater inflater = this.getLayoutInflater();
        View popupView = inflater.inflate(R.layout.coordinates_popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup to also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // set save button action
        Button saveDestBtn = popupView.findViewById(R.id.set_destination);
        saveDestBtn.setOnClickListener(v -> {
            onSaveDestinationBtnClicked(v);
            popupWindow.dismiss();
            mainPresenter.onDestinationChanged(destinationLatitude, destinationLongitude);
        });


        // show the popup window
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }
}