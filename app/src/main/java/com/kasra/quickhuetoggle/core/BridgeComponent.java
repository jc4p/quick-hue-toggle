package com.kasra.quickhuetoggle.core;


import com.kasra.quickhuetoggle.MainActivity;
import com.kasra.quickhuetoggle.core.api.HueApiModule;

import dagger.Subcomponent;

@BridgeScope
@dagger.Subcomponent(modules = { HueApiModule.class })
public interface BridgeComponent {
    void inject(MainActivity activity);

    @Subcomponent.Builder
    interface Builder {
        Builder hueApiModule(HueApiModule module);
        BridgeComponent build();
    }

}
