package com.kasra.quickhuetoggle.core;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
        // #TODO: we probably have some field naming policies to set here
        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder().build();
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPrefs(App app) {
        return app.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }
}
