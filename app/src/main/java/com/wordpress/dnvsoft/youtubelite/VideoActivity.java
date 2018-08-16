package com.wordpress.dnvsoft.youtubelite;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.ui.PlayerUIController;
import com.pierfrancescosoffritti.androidyoutubeplayer.utils.YouTubePlayerTracker;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeItemJsonHelper;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeVideo;
import com.wordpress.dnvsoft.youtubelite.views.LinearLayoutWithTouchListener;

import java.util.ArrayList;
import java.util.Calendar;

public class VideoActivity extends AppCompatActivity implements
        VideoFragmentDescription.OnVideoDescriptionResponse,
        VideoFragmentComments.OnCommentCountUpdate,
        LinearLayoutWithTouchListener.OnYouTubePlayerGoBackAndForward {

    private String videoID;
    private ArrayList<YouTubeVideo> items;
    private YouTubePlayer youTubePlayer;
    private String playlistID;
    private String videoTitle;
    private String commentCount;
    private String nextPageToken;
    private long lastOnBackClickedTime;
    private Toast toast;
    private YouTubePlayerTracker youTubePlayerTracker;
    private YouTubePlayerView youtubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        items = new ArrayList<>();

        SharedPreferences.Editor editor = getSharedPreferences("COMMENT_THREAD_LIST", MODE_PRIVATE).edit();
        editor.remove("COMMENT_LIST");
        editor.apply();

        String videoDuration;
        int videoPosition = getIntent().getIntExtra("VIDEO_POSITION", Integer.MIN_VALUE);
        if (videoPosition != Integer.MIN_VALUE) {
            items.addAll(YouTubeItemJsonHelper.fromJson(YouTubeVideo.class, getIntent().getStringExtra("ITEMS")));
            playlistID = getIntent().getStringExtra("PLAYLIST_ID");
            nextPageToken = getIntent().getStringExtra("NEXT_PAGE_TOKEN");
            videoID = items.get(videoPosition).getId();
            videoTitle = items.get(videoPosition).getName();
            videoDuration = items.get(videoPosition).getDuration();
        } else {
            videoID = getIntent().getStringExtra("VIDEO_ID");
            videoTitle = getIntent().getStringExtra("VIDEO_TITLE");
            videoDuration = getIntent().getStringExtra("VIDEO_DURATION");
        }

        youtubePlayerView = findViewById(R.id.youtube_player_view);
        youtubePlayerView.initialize(youTubePlayerInitListener, true);

        PlayerUIController controller = youtubePlayerView.getPlayerUIController();
        controller.showFullscreenButton(false);
        controller.showYouTubeButton(false);
        if ("00:00".equals(videoDuration)) {
            controller.enableLiveVideoUI(true);
        }

        TabsAdapter mSectionsPagerAdapter = new TabsAdapter(getSupportFragmentManager());

        ViewPager mViewPager = findViewById(R.id.videos_container_video);
        if (videoID != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        } else {
            Toast.makeText(VideoActivity.this, R.string.video_unavailable, Toast.LENGTH_LONG).show();
        }

        mViewPager.addOnPageChangeListener(onPageChangeListener);

        TabLayout tabLayout = findViewById(R.id.video_tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    YouTubePlayerInitListener youTubePlayerInitListener = new YouTubePlayerInitListener() {
        @Override
        public void onInitSuccess(@NonNull final YouTubePlayer initializedYouTubePlayer) {
            initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady() {
                    youTubePlayer = initializedYouTubePlayer;
                    youTubePlayerTracker = new YouTubePlayerTracker();
                    youTubePlayer.addListener(youTubePlayerTracker);
                    youTubePlayer.loadVideo(videoID, 0);
                }
            });
        }
    };

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if (position != 2) {
                getSupportFragmentManager().popBackStack();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @Override
    protected void onPause() {
        if (youTubePlayer != null) {
            youTubePlayer.pause();
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            long currentTime = Calendar.getInstance().getTime().getTime();
            if (currentTime - lastOnBackClickedTime <= 2000) {
                if (toast != null) {
                    toast.cancel();
                }
                super.onBackPressed();
            } else {
                lastOnBackClickedTime = currentTime;
                toast = Toast.makeText(VideoActivity.this, "Press one more time to return.", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        youtubePlayerView.release();
        super.onDestroy();
    }

    @Override
    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    @Override
    public String getCommentCount() {
        return commentCount;
    }

    @Override
    public void youtubePlayerGoBack() {
        if (youTubePlayer != null) {
            SharedPreferences preferences = getSharedPreferences("SEEK_DURATION", Context.MODE_PRIVATE);
            int duration = preferences.getInt("DURATION", 5);
            youTubePlayer.seekTo(youTubePlayerTracker.getCurrentSecond() - duration);
        }
    }

    @Override
    public void youtubePlayerGoForward() {
        if (youTubePlayer != null) {
            SharedPreferences preferences = getSharedPreferences("SEEK_DURATION", Context.MODE_PRIVATE);
            int duration = preferences.getInt("DURATION", 5);
            youTubePlayer.seekTo(youTubePlayerTracker.getCurrentSecond() + duration);
        }
    }

    public class TabsAdapter extends FragmentPagerAdapter {

        private VideoFragmentDescription fragmentDescription;
        private VideoFragmentVideos fragmentVideos;
        private VideoFragmentRootComments fragmentRootComments;

        TabsAdapter(FragmentManager fm) {
            super(fm);
            fragmentDescription = VideoFragmentDescription.newInstance(videoID, videoTitle);
            fragmentVideos = VideoFragmentVideos.newInstance(items, playlistID, videoID, nextPageToken);
            fragmentRootComments = VideoFragmentRootComments.newInstance(videoID);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0: {
                    fragment = fragmentDescription;
                }
                break;
                case 1: {
                    fragment = fragmentVideos;
                }
                break;
                case 2: {
                    fragment = fragmentRootComments;
                }
                break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
