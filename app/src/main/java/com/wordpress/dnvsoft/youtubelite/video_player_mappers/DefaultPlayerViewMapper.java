package com.wordpress.dnvsoft.youtubelite.video_player_mappers;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.wordpress.dnvsoft.youtubelite.R;
import com.wordpress.dnvsoft.youtubelite.VideoActivity;
import com.wordpress.dnvsoft.youtubelite.YoutubeInfo;
import com.wordpress.dnvsoft.youtubelite.menus.MissingServiceMenu;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeItemJsonHelper;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeVideo;

import java.util.ArrayList;

public class DefaultPlayerViewMapper extends VideoPlayerMapper
        implements YouTubePlayer.OnInitializedListener {

    private String videoId;
    private String nextPageToken;
    private String playlistID;
    private boolean isMinimized;
    private int videoPosition;
    private int currentVideoTime;
    private ArrayList<YouTubeVideo> youTubeVideos;
    private Context context;
    private YouTubePlayer youTubePlayer;
    private YouTubePlayerFragment youtubePlayerView;

    public DefaultPlayerViewMapper(DefaultPlayerView playerView, Context context, String nextPageToken,
                                   String playlistID, int videoPosition, ArrayList<YouTubeVideo> youTubeVideos) {
        youtubePlayerView = playerView;
        LinearLayout linearLayout = ((VideoActivity) context).findViewById(R.id.default_player_view_layout);
        linearLayout.setVisibility(View.VISIBLE);

        this.context = context;
        this.nextPageToken = nextPageToken;
        this.playlistID = playlistID;
        this.videoPosition = videoPosition;
        this.youTubeVideos = youTubeVideos;
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean b) {
        youTubePlayer = player;
        youTubePlayer.setShowFullscreenButton(false);
        youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);

        youTubePlayer.setPlaylistEventListener(new YouTubePlayer.PlaylistEventListener() {
            @Override
            public void onPrevious() {
                videoPosition--;
                startNewActivity();
            }

            @Override
            public void onNext() {
                videoPosition++;
                startNewActivity();
            }

            @Override
            public void onPlaylistEnded() {

            }
        });

        if (isMinimized) {
            if (videoPosition != Integer.MIN_VALUE) {
                youTubePlayer.cuePlaylist(playlistID, videoPosition, currentVideoTime);
            } else {
                youTubePlayer.cueVideo(videoId, currentVideoTime);
            }
        } else {
            if (videoPosition != Integer.MIN_VALUE) {
                youTubePlayer.loadPlaylist(playlistID, videoPosition, currentVideoTime);
            } else {
                youTubePlayer.loadVideo(videoId, currentVideoTime);
            }
        }
    }

    private void startNewActivity() {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra("PLAYLIST_ID", playlistID);
        intent.putExtra("VIDEO_POSITION", videoPosition);
        intent.putExtra("NEXT_PAGE_TOKEN", nextPageToken);
        intent.putExtra("ITEMS", YouTubeItemJsonHelper.toJson(youTubeVideos));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        if (result == YouTubeInitializationResult.SERVICE_MISSING) {
            MissingServiceMenu serviceMenu = new MissingServiceMenu(context,
                    context.getString(R.string.service_missing), YoutubeInfo.YOUTUBE_PACKAGE);
            serviceMenu.ShowDialog();
        } else if (result == YouTubeInitializationResult.SERVICE_DISABLED) {
            Toast.makeText(context, R.string.service_disabled, Toast.LENGTH_LONG).show();
        } else if (result == YouTubeInitializationResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            Toast.makeText(context, R.string.service_version_update_required, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, result.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void initialize(String videoId) {
        this.videoId = videoId;
        youtubePlayerView.initialize(YoutubeInfo.DEVELOPER_KEY, this);
    }

    @Override
    public void seekSeconds(int seconds) {
        youTubePlayer.seekRelativeMillis(seconds * 1000);
    }

    @Override
    public void pause() {
        if (youTubePlayer != null) {
            currentVideoTime = youTubePlayer.getCurrentTimeMillis();
        }
        isMinimized = true;
    }

    @Override
    public void release() {
        if (youTubePlayer != null) {
            youTubePlayer.release();
        }
    }
}
