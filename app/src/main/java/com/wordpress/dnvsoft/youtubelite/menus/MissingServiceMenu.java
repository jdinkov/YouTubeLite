package com.wordpress.dnvsoft.youtubelite.menus;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

import com.wordpress.dnvsoft.youtubelite.R;
import com.wordpress.dnvsoft.youtubelite.YoutubeInfo;

public class MissingServiceMenu {

    private Context context;
    private String message;
    private String servicePackage;

    public MissingServiceMenu(Context con, String mess, String pack) {
        this.context = con;
        this.message = mess;
        this.servicePackage = pack;
    }

    public void ShowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                            (YoutubeInfo.GOOGLE_PLAY + servicePackage)));
                } catch (android.content.ActivityNotFoundException a) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                            (YoutubeInfo.BROWSER_URL + servicePackage)));
                }
            }
        });
        builder.setNegativeButton(R.string.negative_button, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
