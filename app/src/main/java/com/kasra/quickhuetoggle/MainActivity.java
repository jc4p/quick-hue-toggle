package com.kasra.quickhuetoggle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.kasra.quickhuetoggle.core.App;
import com.kasra.quickhuetoggle.core.Utils;
import com.kasra.quickhuetoggle.core.api.models.AllLightsResponse;
import com.kasra.quickhuetoggle.core.api.models.LightResponse;
import com.kasra.quickhuetoggle.core.services.HueApiService;
import com.kasra.quickhuetoggle.core.services.PrefsService;
import com.kasra.quickhuetoggle.ui.LightsAdapter;
import com.kasra.quickhuetoggle.ui.models.Light;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.xml.validation.Schema;

import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    @Inject PrefsService prefs;
    @Inject HueApiService api;

    private View loadingView;
    private RecyclerView recyclerView;

    private LightsAdapter adapter;

    private ArrayList<Light> lights;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        App app = (App)getApplication();

        if (!app.isUserLoggedIn()) {
            Utils.log("MainActivity - NO user!!!");
            finish();
        }

        app.createBridgeComponent(app.getLoggedInBridgeIp());
        App.bridgeComponent.inject(this);

        loadingView = findViewById(R.id.activity_main_loading);
        recyclerView = (RecyclerView)findViewById(R.id.activity_main_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        lights = new ArrayList<>();

        checkBridge();
        getLights();
    }

    private void checkBridge() {
        // idk, maybe check the bridge for a minimum supported API version?
        /*
        api.getConfig(prefs.getApiUsername())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> {
                    Utils.log("Error: " + throwable.getMessage());
                    Utils.log(Log.getStackTraceString(throwable));
                    return null;
                })
                .subscribe(c -> {
                    if (c == null) {
                        return;
                    }

                    Utils.log("idk");
                });
        */
    }

    private void getLights() {
        api.getLights(prefs.getApiUsername())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .onErrorReturn(throwable -> {
                    Utils.log("Error: " + throwable.getMessage());
                    Utils.log(Log.getStackTraceString(throwable));
                    return null;
                })
                .subscribe(res -> {
                    if (res == null) {
                        return;
                    }

                    lights.addAll(res.entrySet().stream()
                            .map(r -> Light.fromLightResponse(r.getKey(), r.getValue()))
                            .collect(Collectors.toList())
                            );

                    setupLights();
                    showUi();
                });
    }

    private void setupLights() {
        adapter = new LightsAdapter(lights);
        recyclerView.setAdapter(adapter);
        adapter.getLightChanges()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(p -> {
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("bri", p.second);
                    params.put("transitiontime", 0);

                    if (p.second == 0)
                        params.put("on", false);
                    else if (!p.first.on) {
                        params.put("on", true);
                        p.first.on = true;
                    }

                    api.setBrightness(p.first.id, prefs.getApiUsername(), params)
                            .subscribeOn(Schedulers.io())
                            .onErrorReturn(throwable -> {
                                Utils.log("Error: " + throwable.getMessage());
                                Utils.log(Log.getStackTraceString(throwable));
                                return null;
                            })
                            .subscribe(v -> { });
                });
    }

    private void showUi() {
        loadingView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}
