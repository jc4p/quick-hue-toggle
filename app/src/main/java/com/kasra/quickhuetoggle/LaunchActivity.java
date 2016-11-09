package com.kasra.quickhuetoggle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.kasra.quickhuetoggle.core.App;
import com.kasra.quickhuetoggle.core.services.PrefsService;

import javax.inject.Inject;

public class LaunchActivity extends AppCompatActivity {
    @Inject PrefsService prefs;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        App.component.inject(this);
        Intent i;
        if (prefs.isUserLoggedIn()) {
            i = new Intent(this, MainActivity.class);
        } else {
            i = new Intent(this, SetupActivity.class);
        }
        startActivity(i);
        finish();
    }
}
