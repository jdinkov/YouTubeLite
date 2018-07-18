package com.wordpress.dnvsoft.youtubelite.menus;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.wordpress.dnvsoft.youtubelite.R;

public class AboutMenu {

    private Context context;

    public AboutMenu(Context context) {
        this.context = context;
    }

    public void Show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("\nMade by Yordan Dinkov.\n");
        builder.setPositiveButton(R.string.positive_button, null);
        builder.create().show();
    }
}
