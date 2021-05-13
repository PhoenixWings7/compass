package com.phoenixwings7.compass;

public interface CompassMVP {
    interface View {
        void setButtonAction();
        void animateCompassRotation(float azimuth);
    }
    interface Presenter {
        void onAzimuthChanged(int azimuth);
    }
}
