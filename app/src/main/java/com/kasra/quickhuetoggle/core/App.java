package com.kasra.quickhuetoggle.core;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.kasra.quickhuetoggle.core.api.HueApiModule;
import com.kasra.quickhuetoggle.core.api.HueDiscoveryModule;
import com.kasra.quickhuetoggle.core.services.PrefsService;

import javax.inject.Inject;

public class App extends Application {
    public static DataComponent component;
    public static BridgeComponent bridgeComponent;

    @Inject PrefsService prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerDataComponent.builder()
                .appModule(new AppModule(this))
                .hueDiscoveryModule(new HueDiscoveryModule())
                .build();
        component.inject(this);

        Stetho.initializeWithDefaults(this);
    }

    public void createBridgeComponent(String host) {
        bridgeComponent = App.component.plusBridgeComponent()
                .hueApiModule(new HueApiModule(host))
                .build();
    }

    public void releaseBridgeComponent() {
        bridgeComponent = null;
    }

    public boolean isUserLoggedIn() {
        return prefs.isUserLoggedIn();
    }

    public String getLoggedInBridgeIp() {
        return prefs.getBridgeIp();
    }
}
