package com.phoenixwings7.compass;

import android.location.Location;

public class MainPresenter implements CompassMVP.Presenter {
    CompassMVP.View mainView;
    public MainPresenter(CompassMVP.View mainActivity) {
        this.mainView = mainActivity;
        setUpViewActions();
    }

    @Override
    public void onAzimuthChanged(int azimuth) {
        mainView.animateCompassRotation(azimuth);
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

        // todo: update UI
        System.out.println("Distance: " + distance);

    }
}
