package com.kasra.quickhuetoggle.core;

import android.app.Dialog;
import android.support.v7.app.AlertDialog;

import com.kasra.quickhuetoggle.R;

public class TileService extends android.service.quicksettings.TileService {
    @Override
    public void onClick() {
        super.onClick();
        // Right now this crashes with AppCompat saying the theme's not set
        Dialog d = new AlertDialog.Builder(getApplicationContext())
                .setView(R.layout.dialog_lights)
                .create();

        showDialog(d);
    }
}
