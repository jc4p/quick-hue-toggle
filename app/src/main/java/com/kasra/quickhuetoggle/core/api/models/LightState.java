package com.kasra.quickhuetoggle.core.api.models;

import com.google.gson.annotations.SerializedName;

public class LightState {
    public boolean on;
    @SerializedName("bri")
    public int brightness;
    public long hue;
    @SerializedName("sat")
    public int saturation;
    public String effect;
    public double[] xy;
    @SerializedName("ct")
    public int colroTemperature;
    public String alert;
    public String colormode;
    public boolean reachable;
}
