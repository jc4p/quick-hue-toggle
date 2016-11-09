package com.kasra.quickhuetoggle.core;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kasra.quickhuetoggle.core.api.models.AllLightsResponse;
import com.kasra.quickhuetoggle.core.api.models.LightResponse;
import com.kasra.quickhuetoggle.core.services.PrefsService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module()
public class AppModule {
    protected final App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    @Singleton
    App provideApplication() {
        return app;
    }

    @Provides
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(AllLightsResponse.class,
                new Utils.DictionaryOfItemsDeserializer<>(LightResponse.class, AllLightsResponse.class));
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
    }

    @Provides
    @Singleton
    PrefsService providePrefsService(App app) {
        return new PrefsService(app);
    }
}
