package com.wordpress.dnvsoft.youtubelite.video_player_mappers;

import android.support.annotation.NonNull;
import android.view.View;

import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.ui.PlayerUIController;
import com.pierfrancescosoffritti.androidyoutubeplayer.utils.YouTubePlayerTracker;

public class PierFrancescoViewMapper extends VideoPlayerMapper {

    private String videoId;
    private YouTubePlayer youTubePlayer;
    private YouTubePlayerTracker youTubePlayerTracker;
    private PierFrancescoPlayerView youtubePlayerView;

    public PierFrancescoViewMapper(VideoPlayerView playerView, String videoDuration) {
        youtubePlayerView = (PierFrancescoPlayerView) playerView;
        youtubePlayerView.setVisibility(View.VISIBLE);

        PlayerUIController controller = youtubePlayerView.getPlayerUIController();
        controller.showFullscreenButton(false);
        controller.showYouTubeButton(false);
        if ("00:00".equals(videoDuration)) {
            controller.enableLiveVideoUI(true);
        }
    }

    private YouTubePlayerInitListener youTubePlayerInitListener = new YouTubePlayerInitListener() {
        @Override
        public void onInitSuccess(@NonNull final YouTubePlayer initializedYouTubePlayer) {
            initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady() {
                    youTubePlayer = initializedYouTubePlayer;
                    youTubePlayerTracker = new YouTubePlayerTracker();
                    youTubePlayer.addListener(youTubePlayerTracker);
                    youTubePlayer.loadVideo(videoId, 0);
                }
            });
        }
    };

    @Override
    public void initialize(String videoId) {
        this.videoId = videoId;
        youtubePlayerView.initialize(youTubePlayerInitListener, true);
    }

    @Override
    public void seekSeconds(int seconds) {
        youTubePlayer.seekTo(youTubePlayerTracker.getCurrentSecond() + seconds);
    }

    @Override
    public void pause() {
        if (youTubePlayer != null) {
            youTubePlayer.pause();
        }
    }

    @Override
    public void release() {
        if (youtubePlayerView != null) {
            youtubePlayerView.release();
        }
    }
}
