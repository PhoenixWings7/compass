package com.phoenixwings7.compass;

import android.location.Location;

public interface CompassMVP {
    interface View {
        void setButtonsActions();
        void animateCompassRotation(float azimuth);
        void setUpLocationService();
        boolean checkLocationPermission();
        void updateLocation();
        void updateDistanceUI(int metersToTarget);
        void updateDirectionUI(int rotationDegrees);
    }
    interface Presenter {
        void onAzimuthChanged(int azimuth);
        void onDestinationChanged(float latitude, float longitude);
        void onUserLocationChanged(Location location, float destinationLat, float destinationLon);
    }
}
