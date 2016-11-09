package com.kasra.quickhuetoggle.ui.models;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.kasra.quickhuetoggle.BR;
import com.kasra.quickhuetoggle.core.api.models.LightResponse;

public class Light extends BaseObservable {
    public String name;
    public String id;

    public boolean on;
    public int brightness;

    public Light() {
    }

    public static Light fromLightResponse(String id, LightResponse l) {
        Light light = new Light();
        light.id = id;
        light.name = l.name;
        light.on = l.state.on;

        if (light.on)
            light.brightness = l.state.brightness;
        else
            light.brightness = 0;

        return light;
    }

    public void setBrightness(int bri) {
        this.brightness = bri;
        notifyPropertyChanged(BR.brightness);
    }

    @Bindable
    public int getBrightness() {
        return this.brightness;
    }
}
