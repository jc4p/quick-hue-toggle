package com.kasra.quickhuetoggle;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import com.google.gson.Gson;
import com.kasra.quickhuetoggle.core.App;
import com.kasra.quickhuetoggle.core.services.HueApiService;
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

public class MainActivity extends AppCompatActivity {

    private View searchingView;
    private View instructionsView;
    private ImageView instructionsImage;
    private TextView instructionsLabel;

    private final BaseSpringSystem springSystem = SpringSystem.create();
    private final IconScaleSpringListener springListener = new IconScaleSpringListener();
    private Spring scaleSpring;
    private int imageScalePaddingMin;
    private int imageScalePaddingMax;

    @Inject SsdpSearchService ssdpSearchService;
    @Inject Gson gson;
    @Inject OkHttpClient httpClient;
    private HueApiService hueApi;

    @Inject SharedPreferences sharedPrefs;

    private PublishSubject<String> hueDiscoverySubject = PublishSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchingView = findViewById(R.id.activity_main_searching);
        instructionsView = findViewById(R.id.activity_main_instructions);
        instructionsImage = (ImageView) findViewById(R.id.activity_main_instructions_image);
        instructionsLabel = (TextView) findViewById(R.id.activity_main_instructions_label);

        App.component.inject(this);

        scaleSpring = springSystem.createSpring();

        SpringConfig springConfig = new SpringConfig(20, 6);
        scaleSpring.setSpringConfig(springConfig);
        scaleSpring.setOvershootClampingEnabled(true);

        imageScalePaddingMin = getResources().getDimensionPixelSize(R.dimen.setup_icon_padding_min);
        imageScalePaddingMax = getResources().getDimensionPixelSize(R.dimen.setup_icon_padding_max);

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

                    scaleSpring.setEndValue(1);

                    instructionsView.setOnClickListener(view -> {
                        attemptUserCreate(s);
                    });
                });

//        ssdpSearchService.start(getApplicationContext(), hueDiscoverySubject);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scaleSpring.addListener(springListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scaleSpring.removeListener(springListener);
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
                    Log.e("KasraTest", "Error: " + throwable.getMessage());
                    Log.e("KasraTest", Log.getStackTraceString(throwable));
                    return null;
                })
                .subscribe(res -> {
                    if (res == null) { return; }

                    if (res.get(0).isSuccess()) {
                        Snackbar.make(instructionsView, "Connected to Hue bridge", Snackbar.LENGTH_LONG).show();
                        Log.e("KasraTest", "Registered user: " + res.get(0).get());
                        sharedPrefs.edit()
                                .putString("username", res.get(0).get())
                                .putString("host", host)
                                .apply();
                    }
                    else {
                        String errorMessage = getString(R.string.setup_user_create_error, res.get(0).getErrrorMessage());
                        Snackbar.make(instructionsView, errorMessage, Snackbar.LENGTH_LONG).show();
                        Log.e("KasraTest", "Error: " + res.get(0).getErrrorMessage());
                    }
                });

    }

    private class IconScaleSpringListener extends SimpleSpringListener {
        @Override
        public void onSpringUpdate(Spring spring) {
            int padding = (int)Math.floor(imageScalePaddingMin +
                    (spring.getCurrentValue() * (imageScalePaddingMax - imageScalePaddingMin)));
            instructionsImage.setPadding(padding, padding, padding, padding);

            if (spring.getCurrentValue() == spring.getEndValue()) {
                spring.setEndValue(spring.getCurrentValue() == 1 ? 0 : 1);
            }
        }
    }


}
