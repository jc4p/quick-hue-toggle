package com.kasra.quickhuetoggle.core.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kasra.quickhuetoggle.core.BridgeScope;
import com.kasra.quickhuetoggle.core.services.HueApiService;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class HueApiModule {

    private String hueIp;

    public HueApiModule(String ip) {
        this.hueIp = ip;
    }

    @Provides @BridgeScope
    Retrofit provideHueRetrofit(Gson gson, OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + hueIp + "/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
        return retrofit;
    }

    @Provides @BridgeScope
    HueApiService provideHueApi(Retrofit retrofit) {
        return retrofit.create(HueApiService.class);
    }
}
