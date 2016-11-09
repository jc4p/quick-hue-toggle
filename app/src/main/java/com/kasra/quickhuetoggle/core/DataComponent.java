package com.kasra.quickhuetoggle.core;

import com.kasra.quickhuetoggle.LaunchActivity;
import com.kasra.quickhuetoggle.SetupActivity;
import com.kasra.quickhuetoggle.core.api.HueApiModule;
import com.kasra.quickhuetoggle.core.api.HueDiscoveryModule;

import javax.inject.Singleton;

import dagger.Component;
import dagger.Subcomponent;

@Singleton
@dagger.Component(modules = { AppModule.class, HueDiscoveryModule.class })
public interface DataComponent {
    @Component.Builder
    interface Builder {
        DataComponent.Builder appModule(AppModule module);
        DataComponent.Builder hueDiscoveryModule(HueDiscoveryModule module);
        DataComponent build();
    }

    BridgeComponent.Builder plusBridgeComponent();

    void inject(App app);
    void inject(LaunchActivity activity);
    void inject(SetupActivity activity);
}
