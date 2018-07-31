package com.wordpress.dnvsoft.youtubelite.menus;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wordpress.dnvsoft.youtubelite.R;

public class SeekDurationMenu {

    private Context context;

    public SeekDurationMenu(Context context) {
        this.context = context;
    }

    public void Show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Seek duration:");

        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.menu_seek_duration, null);
        final EditText editText = layout.findViewById(R.id.editTextSeekDuration);

        SharedPreferences preferences = context.getSharedPreferences("SEEK_DURATION", Context.MODE_PRIVATE);
        int duration = preferences.getInt("DURATION", 5);
        editText.setHint(String.valueOf(duration));

        builder.setView(layout);
        builder.setPositiveButton(R.string.positive_button, null);
        builder.setNegativeButton(R.string.negative_button, null);

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int duration = Integer.valueOf(editText.getText().toString());
                        if (duration < 1) {
                            Toast.makeText(context, "Number must be larger than 0.", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (duration > 30) {
                            Toast.makeText(context, "Number must be lower than 30.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SharedPreferences.Editor editor =
                                context.getSharedPreferences("SEEK_DURATION", Context.MODE_PRIVATE).edit();
                        editor.putInt("DURATION", duration);
                        editor.apply();

                        Toast.makeText(context, "Settings saved.", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                });
            }
        });

        dialog.show();
    }
}
