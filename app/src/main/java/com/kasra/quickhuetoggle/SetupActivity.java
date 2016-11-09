package com.kasra.quickhuetoggle;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kasra.quickhuetoggle.core.App;
import com.kasra.quickhuetoggle.core.Utils;
import com.kasra.quickhuetoggle.core.services.HueApiService;
import com.kasra.quickhuetoggle.core.services.PrefsService;
import com.kasra.quickhuetoggle.core.services.SsdpSearchService;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class SetupActivity extends AppCompatActivity {

    private View searchingView;
    private View instructionsView;
    private TextView instructionsLabel;
    private Button instructionsButton;

    @Inject SsdpSearchService ssdpSearchService;
    @Inject Gson gson;
    @Inject OkHttpClient httpClient;
    private HueApiService hueApi;

    @Inject PrefsService prefs;

    private PublishSubject<String> hueDiscoverySubject = PublishSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        searchingView = findViewById(R.id.activity_main_searching);
        instructionsView = findViewById(R.id.activity_main_instructions);
        instructionsLabel = (TextView) findViewById(R.id.activity_main_instructions_label);
        instructionsButton = (Button) findViewById(R.id.activity_main_connect_button);

        App.component.inject(this);

        //hueDiscoverySubject.asObservable()
        Observable.just("192.168.1.111")
                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    setupApi(s);
                    searchingView.setVisibility(View.GONE);
                    instructionsView.setVisibility(View.VISIBLE);

                    // #TODO... Clean this up
                    instructionsLabel.setText(getText(R.string.setup_instructions_explain));
                    instructionsLabel.append("\n" + getText(R.string.press_link_then_tap));

                    instructionsButton.setOnClickListener(view -> {
                        attemptUserCreate(s);
                    });
                });

//        ssdpSearchService.start(getApplicationContext(), hueDiscoverySubject);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setupApi(String host) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + host + "/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(httpClient)
                .build();
        hueApi  = retrofit.create(HueApiService.class);
    }

    private void attemptUserCreate(String host) {
        HashMap<String, String> params = new HashMap<>();
        params.put("devicetype", getString(R.string.hue_user_devicetype));
        hueApi.createUser(params)
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

                    if (res.get(0).isSuccess()) {
                        Snackbar.make(instructionsView, R.string.setup_user_connected_to_bridge, Snackbar.LENGTH_LONG).show();
                        Utils.log("Registered user: " + res.get(0).get());
                        prefs.setLoginInfo(host, res.get(0).get());

                        Intent i = new Intent(this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        String errorMessage = getString(R.string.setup_user_create_error, res.get(0).getErrrorMessage());
                        Snackbar.make(instructionsView, errorMessage, Snackbar.LENGTH_LONG).show();
                        Utils.log("Error: " + res.get(0).getErrrorMessage());
                    }
                });

    }
}
