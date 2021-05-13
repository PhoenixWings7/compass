package com.phoenixwings7.compass;

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
        mainView.setButtonAction();
    }
}
