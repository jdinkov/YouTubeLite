package com.wordpress.dnvsoft.youtubelite.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wordpress.dnvsoft.youtubelite.R;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeItem;
import com.wordpress.dnvsoft.youtubelite.models.YouTubePlayList;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeVideo;

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
        ImageView youTubeThumbnailView = convertView.findViewById(R.id.listViewThumbnailPlayListItems);
        ImageView imageView = convertView.findViewById(R.id.listViewImage);
        TextView textViewItemCount = convertView.findViewById(R.id.listViewPlaylistItemCount);
        TextView textViewVideoDuration = convertView.findViewById(R.id.textViewDuration);
        TextView textViewChannelTitle = convertView.findViewById(R.id.textViewChannelTitle);

        final YouTubeItem youTubeItem = objects.get(position);
        textView.setText(youTubeItem.getName());
        Picasso.with(context).load(youTubeItem.getThumbnailURL()).into(youTubeThumbnailView);
        if (youTubeItem instanceof YouTubePlayList) {
            imageView.setVisibility(View.VISIBLE);
            textViewItemCount.setText(youTubeItem.getItemCount());
            textViewVideoDuration.setVisibility(View.GONE);
            textViewVideoDuration.setText("");
            textViewChannelTitle.setText(((YouTubePlayList) youTubeItem).getChannelTitle());
        } else if (youTubeItem instanceof YouTubeVideo) {
            String duration = ((YouTubeVideo) youTubeItem).getDuration();
            if (duration != null) {
                textViewVideoDuration.setVisibility(View.VISIBLE);
                textViewVideoDuration.setText(duration);
            } else {
                textViewVideoDuration.setVisibility(View.GONE);
            }

            textViewChannelTitle.setText(((YouTubeVideo) youTubeItem).getChannelTitle());
            imageView.setVisibility(View.GONE);
            textViewItemCount.setText("");
        } else {
            imageView.setVisibility(View.GONE);
            textViewItemCount.setText("");
            textViewVideoDuration.setVisibility(View.GONE);
            textViewVideoDuration.setText("");
            textViewChannelTitle.setText("");
        }

        return convertView;
    }
}
