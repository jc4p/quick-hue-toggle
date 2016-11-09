package com.kasra.quickhuetoggle.ui;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.jakewharton.rxbinding.widget.RxSeekBar;
import com.kasra.quickhuetoggle.R;
import com.kasra.quickhuetoggle.core.Utils;
import com.kasra.quickhuetoggle.databinding.LightsControllerListItemBinding;
import com.kasra.quickhuetoggle.ui.models.Light;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.subjects.PublishSubject;


public class LightsAdapter extends RecyclerView.Adapter<LightsAdapter.ViewHolder> {
    private ArrayList<Light> lights;
    private final PublishSubject<Pair<Light, Integer>> mLightSeekSubject = PublishSubject.create();

    public LightsAdapter(ArrayList<Light> lights) {
        this.lights = lights;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LightsControllerListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.lights_controller_list_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Light light = lights.get(position);

        holder.binding.setLight(light);
        RxSeekBar.changes(holder.binding.lightsControllerListItemSeekbar)
                .debounce(Utils.LIGHT_VALUE_CHANGE_DEBOUNCE_RATE, TimeUnit.MILLISECONDS)
                .subscribe(val -> {
                    int bri = val;
                    if (val < 20) {
                        bri = 0;
                        light.on = false;
                    }
                    light.setBrightness(val);
                    mLightSeekSubject.onNext(new Pair<>(light, bri));
                });
    }

    public Observable<Pair<Light,Integer>> getLightChanges() {
        return mLightSeekSubject.asObservable();
    }

    @Override
    public int getItemCount() {
        return lights.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private LightsControllerListItemBinding binding;
        ViewHolder(LightsControllerListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
