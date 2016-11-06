package com.kasra.quickhuetoggle.core;

import android.app.Application;

import com.kasra.quickhuetoggle.core.api.HueApiModule;

public class App extends Application {
    public static DataComponent component;
    public static BridgeComponent bridgeComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DataComponent.Initializer.init(this);
    }

    public void createBridgeComponent(String host) {
        bridgeComponent = component.bridgeComponent(new HueApiModule(host));
    }
}
