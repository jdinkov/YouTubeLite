package com.wordpress.dnvsoft.youtubelite.menus;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.wordpress.dnvsoft.youtubelite.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ChooseRegionCodeMenu {

    private int selection;
    private Context context;
    private ArrayList<String> countryCodes;
    private ArrayList<String> countryNames;

    public ChooseRegionCodeMenu(Context context) {
        this.context = context;
        countryCodes = getCountryInfoFromFile().get(0);
        countryNames = getCountryInfoFromFile().get(1);
        SharedPreferences preferences = context.getSharedPreferences("COUNTRY_REGION_CODE", Context.MODE_PRIVATE);
        selection = preferences.getInt("REGION_INDEX", -1);
    }

    public void Show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose location:");
        builder.setSingleChoiceItems(countryNames.toArray(new CharSequence[0]), selection, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selection = i;
            }
        });

        builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (selection != -1) {
                    SharedPreferences.Editor editor = context.getSharedPreferences(
                            "COUNTRY_REGION_CODE", Context.MODE_PRIVATE).edit();
                    editor.putInt("REGION_INDEX", selection);
                    editor.putString("REGION_CODE", countryCodes.get(selection));
                    editor.apply();

                    Toast.makeText(context, R.string.settings_saved, Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton(R.string.negative_button, null);

        builder.create().show();
    }

    private ArrayList<ArrayList<String>> getCountryInfoFromFile() {
        ArrayList<String> code = new ArrayList<>();
        ArrayList<String> name = new ArrayList<>();
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.countries);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                code.add(line.substring(0, 2));
                name.add(line.substring(3, line.length()));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<ArrayList<String>> result = new ArrayList<>();
        result.add(code);
        result.add(name);

        return result;
    }
}
