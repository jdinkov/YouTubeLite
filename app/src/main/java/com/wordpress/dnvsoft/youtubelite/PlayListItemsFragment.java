package com.wordpress.dnvsoft.youtubelite;

import android.content.Intent;
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
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetPlaylistItems;
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncPlaylistThumbnail;
import com.wordpress.dnvsoft.youtubelite.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeItem;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeItemJsonHelper;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

public class PlayListItemsFragment extends YouTubeItemsFragment {

    String playlistId;
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
        playlistId = getArguments().getString("PLAYLIST_ID");
        playlistTitle = getArguments().getString("PLAYLIST_TITLE");
        getPlaylistMaxResThumbnail();
        getItemsFromYouTube();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        LinearLayout header = (LinearLayout) inflater.inflate(R.layout.playlist_items_fragment_header, listView, false);
        imageViewPlaylistThumbnail = header.findViewById(R.id.playListItemsThumbnail);
        TextView textViewPlaylistTitle = header.findViewById(R.id.playListItemsTitle);
        textViewPlaylistTitle.setText(playlistTitle);

        listView.addHeaderView(header, null, false);

        listView.setOnItemClickListener(onItemClickListener);

        return view;
    }

    @Override
    void getItemsFromYouTube() {
        AsyncGetPlaylistItems getPlaylistItems = new AsyncGetPlaylistItems(
                getActivity(), playlistId, nextPageToken,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        asyncTaskCompleted.onTaskComplete(result);
                    }
                });

        getPlaylistItems.execute();
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
        Intent intent = new Intent(getActivity(), VideoActivity.class);
        intent.putExtra("PLAYLIST_ID", playlistId);
        intent.putExtra("VIDEO_POSITION", position);
        intent.putExtra("NEXT_PAGE_TOKEN", nextPageToken);
        intent.putExtra("ITEMS", YouTubeItemJsonHelper.toJson(youTubeItems));
        startActivity(intent);
    }

    @Override
    String getContentString() {
        return "This playlist is empty.";
    }
}
