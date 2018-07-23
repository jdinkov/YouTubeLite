package com.wordpress.dnvsoft.youtubelite.menus;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.wordpress.dnvsoft.youtubelite.ChannelActivity;
import com.wordpress.dnvsoft.youtubelite.ChannelFragmentSearchVideos;
import com.wordpress.dnvsoft.youtubelite.R;

public class SearchChannelVideosMenu {

    private Context context;
    private String channelId;

    public SearchChannelVideosMenu(Context context, String channelId) {
        this.context = context;
        this.channelId = channelId;
    }

    public void Show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Search:");

        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.menu_search_channel_videos, null);
        final EditText editText = layout.findViewById(R.id.editTextSearchChannelVideos);

        builder.setView(layout);
        builder.setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!editText.getText().toString().equals("")) {
                    FragmentTransaction fragmentTransaction =
                            ((ChannelActivity) context).getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.root_fragment_videos,
                            ChannelFragmentSearchVideos.newInstance(channelId, editText.getText().toString()));
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });

        builder.setNegativeButton(R.string.negative_button, null);

        builder.create().show();
    }
}
