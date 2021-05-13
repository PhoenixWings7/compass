package com.phoenixwings7.compass;

public interface CompassMVP {
    interface View {
        void setButtonsActions();
        void animateCompassRotation(float azimuth);
    }
    interface Presenter {
        void onAzimuthChanged(int azimuth);
        void onDestinationChanged(float latitude, float longitude);
    }
}
