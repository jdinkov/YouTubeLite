package com.wordpress.dnvsoft.youtubelite.menus;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;

import com.wordpress.dnvsoft.youtubelite.R;

import java.util.Arrays;

public class SortMenu {

    private String order;
    private Context context;
    private SortMenuCallback callback;

    public interface SortMenuCallback {
        void OnOrderSelected(String order);
    }

    public SortMenu(Context context, SortMenuCallback sortMenuCallback) {
        this.context = context;
        this.callback = sortMenuCallback;
        order = orderParameter[getOrderFromPreferences()];
    }

    private String[] orderParameter = {
            "date",
            "rating",
            "relevance",
            "title",
            "viewCount"
    };

    private String[] menuItems = {
            " Date ",
            " Rating ",
            " Relevance ",
            " Title ",
            " View Count "
    };

    public void SortItems() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose Order:");
        builder.setSingleChoiceItems(menuItems, getOrderFromPreferences(),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        order = orderParameter[which];
                    }
                });

        builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setOrderInPreferences(Arrays.asList(orderParameter).indexOf(order));
                callback.OnOrderSelected(order);
            }
        });

        builder.setNegativeButton(R.string.negative_button, null);

        builder.create().show();
    }

    private void setOrderInPreferences(int index) {
        SharedPreferences.Editor editor =
                context.getSharedPreferences("MENU_INDEX", Context.MODE_PRIVATE).edit();
        editor.putInt("ORDER_INDEX", index);
        editor.putString("SELECTED_ORDER", orderParameter[index]);
        editor.apply();
    }

    private int getOrderFromPreferences() {
        SharedPreferences preferences = context.getSharedPreferences("MENU_INDEX", Context.MODE_PRIVATE);
        return preferences.getInt("ORDER_INDEX", 2);
    }
}
