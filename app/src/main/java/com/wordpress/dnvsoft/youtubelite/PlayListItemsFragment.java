package com.wordpress.dnvsoft.youtubelite;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncPlaylistThumbnail;
import com.wordpress.dnvsoft.youtubelite.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeItem;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

public class PlayListItemsFragment extends ChannelFragmentPlayListItems {

    private String playlistTitle;
    private ImageView imageViewPlaylistThumbnail;

    public PlayListItemsFragment() {
    }

    public static PlayListItemsFragment newInstance(YouTubeItem youTubeItem) {
        PlayListItemsFragment playListItemsFragment = new PlayListItemsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("PLAYLIST_ID", youTubeItem.getId());
        bundle.putString("PLAYLIST_TITLE", youTubeItem.getName());
        playListItemsFragment.setArguments(bundle);
        return playListItemsFragment;
    }

    @Override
    public void onCreateYouTubeItemsFragment() {
        super.onCreateYouTubeItemsFragment();
        playlistTitle = getArguments().getString("PLAYLIST_TITLE");
        getPlaylistMaxResThumbnail();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        LinearLayout header = (LinearLayout) inflater.inflate(R.layout.playlist_items_fragment_header, listView, false);
        imageViewPlaylistThumbnail = header.findViewById(R.id.playListItemsThumbnail);
        TextView textViewPlaylistTitle = header.findViewById(R.id.playListItemsTitle);
        textViewPlaylistTitle.setText(playlistTitle);

        listView.addHeaderView(header);

        listView.setOnItemClickListener(onItemClickListener);

        return view;
    }

    private void getPlaylistMaxResThumbnail() {
        AsyncPlaylistThumbnail playlistThumbnail = new AsyncPlaylistThumbnail(
                getActivity(), playlistId,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled() && result.getYouTubePlayLists() != null) {
                            Picasso.with(getActivity()).load(result.getYouTubePlayLists().get(0).getThumbnailURL()).into(imageViewPlaylistThumbnail);
                        }
                    }
                }
        );

        playlistThumbnail.execute();
    }

    @Override
    public void onVideoClick(int position) {
        position--;
        super.onVideoClick(position);
    }
}
