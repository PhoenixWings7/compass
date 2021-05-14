package com.phoenixwings7.compass;

import android.location.Location;

public class MainPresenter implements CompassMVP.Presenter {
    private int currentAzimuth = 0;
    private int currentDirectionAngle = 0;
    private int currentBearing = 0;

    CompassMVP.View mainView;
    public MainPresenter(CompassMVP.View mainActivity) {
        this.mainView = mainActivity;
        setUpViewActions();
    }

    @Override
    public void onAzimuthChanged(int azimuth) {
        this.currentAzimuth = azimuth;
        mainView.animateCompassRotation(azimuth);
        mainView.updateDirectionUI(recalculatePointerRotationAngle());
    }

    private void setUpViewActions() {
        mainView.setButtonsActions();
        mainView.setUpLocationService();
    }

    @Override
    public void onDestinationChanged(float latitude, float longitude) {
        boolean locationPermissionGranted = mainView.checkLocationPermission();
        if (locationPermissionGranted) {
            mainView.updateLocation(); // update user location data to calculate distance to dest.
        }
        else {
            // todo: display error message
        }
    }

    @Override
    public void onUserLocationChanged(Location location, float destinationLat, float destinationLon) {
        if (location == null) {
            // if something went wrong while establishing user location, do nothing
            return;
        }

        Location targetLocation = new Location(""); // provider is not important here
        targetLocation.setLatitude(destinationLat);
        targetLocation.setLongitude(destinationLon);

        // recalculate distance to destination
        float distance = location.distanceTo(targetLocation); // distance in meters

        // recalculate bearing to destination
        int bearing = Math.round(location.bearingTo(targetLocation));
        int pointerRotation = recalculatePointerRotationAngle(bearing);
        currentBearing = bearing;

        // update UI
        mainView.updateDistanceUI(Math.round(distance));
        mainView.updateDirectionUI(pointerRotation);
    }

    @Override
    public void setUpLocationUpdates() {
        boolean permissionsGranted = mainView.checkLocationPermission();
        if (permissionsGranted) {
            mainView.startLocationUpdates();
        }
    }

    private int recalculatePointerRotationAngle() {
        return recalculatePointerRotationAngle(currentBearing);
    }

    private int recalculatePointerRotationAngle(int bearing) {
        int newPointerDirection = currentAzimuth+bearing;
        int rotationAngle = newPointerDirection-currentDirectionAngle;
        currentDirectionAngle = newPointerDirection;

        return rotationAngle;

    }
}
