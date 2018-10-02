package com.wordpress.dnvsoft.youtubelite.menus;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.wordpress.dnvsoft.youtubelite.R;

public class ChooseVideoPlayerMenu {

    private Context context;
    private int selection;
    private String[] singleChoiceItems = {
            "Default video player",
            "\"PierFrancescoSoffritti\" video player"
    };
    private String[] sharedPreferencesText = {
            "DEFAULT_PLAYER",
            "PIER_FRANCESCO"
    };

    public ChooseVideoPlayerMenu(Context context) {
        this.context = context;
        SharedPreferences preferences = context.getSharedPreferences("VIDEO_PLAYER_INSTANCE", Context.MODE_PRIVATE);
        String preferencesText = preferences.getString("PLAYER_INSTANCE", "DEFAULT_PLAYER");
        if (preferencesText.equals("DEFAULT_PLAYER")) {
            selection = 0;
        } else if (preferencesText.equals("PIER_FRANCESCO")) {
            selection = 1;
        }
    }

    public void Show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Video Player:");
        builder.setSingleChoiceItems(singleChoiceItems, selection, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selection = i;
            }
        });

        builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor editor = context.getSharedPreferences(
                        "VIDEO_PLAYER_INSTANCE", Context.MODE_PRIVATE).edit();
                editor.putString("PLAYER_INSTANCE", sharedPreferencesText[selection]);
                editor.apply();

                Toast.makeText(context, R.string.settings_saved, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(R.string.negative_button, null);

        builder.create().show();
    }
}
