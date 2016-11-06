package com.kasra.quickhuetoggle.core.api;

import com.kasra.quickhuetoggle.core.services.SsdpSearchService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
public class HueDiscoveryModule {

    public HueDiscoveryModule() {
    }

    @Provides
    @Singleton
    SsdpSearchService provideSsdpSearchService(OkHttpClient client) {
        return new SsdpSearchService(client);
    }

}
