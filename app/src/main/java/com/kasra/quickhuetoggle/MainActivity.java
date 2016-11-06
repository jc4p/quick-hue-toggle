package com.kasra.quickhuetoggle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.kasra.quickhuetoggle.core.App;
import com.kasra.quickhuetoggle.core.BridgeScope;
import com.kasra.quickhuetoggle.core.services.SsdpSearchService;

import javax.inject.Inject;
import javax.inject.Named;

import retrofit2.Retrofit;
import rx.subjects.PublishSubject;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    @Inject SsdpSearchService ssdpSearchService;
    @Inject Retrofit hueRetrofit;

    private PublishSubject<String> hueDiscoverySubject = PublishSubject.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.activity_main_recyclerview);

        App.component.inject(this);

        hueDiscoverySubject.asObservable()
                .subscribe(s -> {
                    ((App)getApplication()).createBridgeComponent(s);
                    App.bridgeComponent.inject(this);
//                    Log.i("Kasra", hueRetrofit == null ? "null" : "not null");
                });

        ssdpSearchService.start(getApplicationContext(), hueDiscoverySubject);
    }

}
