package com.wordpress.dnvsoft.youtubelite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetVideos;
import com.wordpress.dnvsoft.youtubelite.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeItemJsonHelper;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeVideo;

import java.util.ArrayList;

public class VideoFragmentVideos extends YouTubeItemsFragment {

    private String videoID;

    public static VideoFragmentVideos newInstance(
            ArrayList<YouTubeVideo> items, String playlistId, String videoID) {
        VideoFragmentVideos videoFragmentVideos = new VideoFragmentVideos();
        Bundle bundle = new Bundle();
        bundle.putString("ITEMS", YouTubeItemJsonHelper.toJson(items));
        bundle.putString("PLAYLIST_ID", playlistId);
        bundle.putString("VIDEO_ID", videoID);
        videoFragmentVideos.setArguments(bundle);
        return videoFragmentVideos;
    }

    @Override
    public void onCreateYouTubeItemsFragment() {
        youTubeItems.addAll(YouTubeItemJsonHelper.fromJson(getArguments().getString("ITEMS")));
        playlistId = getArguments().getString("PLAYLIST_ID");
        videoID = getArguments().getString("VIDEO_ID");

        if (youTubeItems.isEmpty()) {
            getItemsFromYouTube();
        }
    }

    @Override
    void getItemsFromYouTube() {
        AsyncGetVideos getVideos = new AsyncGetVideos(getContext(), videoID, nextPageToken,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled() && result.getYouTubeVideos() != null) {
                            if (result.getYouTubeVideos().size() % 20 == 0) {
                                footer.setVisibility(View.VISIBLE);
                            }

                            nextPageToken = result.getNextPageToken();

                            for (YouTubeVideo video : result.getYouTubeVideos()) {
                                if (!video.getId().equals(videoID)) {
                                    youTubeItems.add(video);
                                }
                            }

                            buttonLoadMore.setText(R.string.load_more);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

        getVideos.execute();
    }

    @Override
    public void onVideoClick(int position) {
        Intent intent = new Intent(getActivity(), VideoActivity.class);
        if (playlistId != null) {
            int videoPosition = youTubeItems.size() - position - 1;

            intent.putExtra("PLAYLIST_ID", playlistId);
            intent.putExtra("VIDEO_POSITION", videoPosition);
            intent.putExtra("ITEMS", YouTubeItemJsonHelper.toJson(youTubeItems));
        } else {
            intent.putExtra("VIDEO_ID", youTubeItems.get(position).getId());
            intent.putExtra("VIDEO_TITLE", youTubeItems.get(position).getName());
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
