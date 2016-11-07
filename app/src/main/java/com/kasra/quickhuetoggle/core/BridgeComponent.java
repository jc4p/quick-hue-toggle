package com.kasra.quickhuetoggle.core;


import com.kasra.quickhuetoggle.MainActivity;
import com.kasra.quickhuetoggle.core.api.HueApiModule;

import retrofit2.Retrofit;

@BridgeScope
@dagger.Subcomponent(modules = { HueApiModule.class })
public interface BridgeComponent {
    Retrofit retrofit();
}
