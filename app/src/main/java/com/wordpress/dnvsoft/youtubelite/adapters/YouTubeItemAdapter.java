package com.wordpress.dnvsoft.youtubelite.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeThumbnailView;
import com.squareup.picasso.Picasso;
import com.wordpress.dnvsoft.youtubelite.R;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeItem;
import com.wordpress.dnvsoft.youtubelite.models.YouTubePlayList;

import java.util.ArrayList;

public class YouTubeItemAdapter<T extends YouTubeItem> extends ArrayAdapter<T> {

    private Context context;
    private ArrayList<T> objects;

    public YouTubeItemAdapter(Context context, ArrayList<T> objects) {
        super(context, R.layout.list_view_items, objects);
        this.context = context;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_view_items, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.listViewTitlePlayListItems);
        YouTubeThumbnailView youTubeThumbnailView = convertView.findViewById(R.id.listViewThumbnailPlayListItems);
        ImageView imageView = convertView.findViewById(R.id.listViewImage);
        TextView textViewItemCount = convertView.findViewById(R.id.listViewPlaylistItemCount);

        final YouTubeItem youTubeItem = objects.get(position);
        textView.setText(youTubeItem.getName());
        Picasso.with(context).load(youTubeItem.getThumbnailURL()).into(youTubeThumbnailView);
        if (youTubeItem instanceof YouTubePlayList) {
            imageView.setVisibility(View.VISIBLE);
            textViewItemCount.setText(youTubeItem.getItemCount());
        } else {
            imageView.setVisibility(View.GONE);
            textViewItemCount.setText("");
        }

        return convertView;
    }
}
