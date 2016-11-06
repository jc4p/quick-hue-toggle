package com.kasra.quickhuetoggle.core;

import com.kasra.quickhuetoggle.MainActivity;
import com.kasra.quickhuetoggle.core.api.HueApiModule;
import com.kasra.quickhuetoggle.core.api.HueDiscoveryModule;

import javax.inject.Singleton;

@Singleton
@dagger.Component(modules = { AppModule.class, HueDiscoveryModule.class })
public interface DataComponent {
    class Initializer {
        public static DataComponent init(App app) {
            return DaggerDataComponent.builder()
                    .appModule(new AppModule(app))
                    .hueDiscoveryModule(new HueDiscoveryModule())
                    .build();
        }
        private Initializer() {
        }
    }

    BridgeComponent bridgeComponent(HueApiModule hueApiModule);
    void inject(MainActivity activity);
}
