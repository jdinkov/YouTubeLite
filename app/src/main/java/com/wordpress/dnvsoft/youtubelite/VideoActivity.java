package com.wordpress.dnvsoft.youtubelite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.wordpress.dnvsoft.youtubelite.menus.MissingServiceMenu;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeItemJsonHelper;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeVideo;
import com.wordpress.dnvsoft.youtubelite.views.LinearLayoutWithTouchListener;

import java.util.ArrayList;
import java.util.Calendar;

public class VideoActivity extends AppCompatActivity
        implements YouTubePlayer.OnInitializedListener,
        VideoFragmentDescription.OnVideoDescriptionResponse,
        VideoFragmentComments.OnCommentCountUpdate,
        LinearLayoutWithTouchListener.OnYouTubePlayerGoBackAndForward {

    private String videoID;
    private boolean isMinimized;
    private ArrayList<YouTubeVideo> items;
    private YouTubePlayer youTubePlayer;
    private int videoPosition;
    private String playlistID;
    private String videoTitle;
    private int currentVideoTime;
    private String commentCount;
    private String nextPageToken;
    private long lastOnBackClickedTime;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        items = new ArrayList<>();

        SharedPreferences.Editor editor = getSharedPreferences("COMMENT_THREAD_LIST", MODE_PRIVATE).edit();
        editor.remove("COMMENT_LIST");
        editor.apply();

        videoPosition = getIntent().getIntExtra("VIDEO_POSITION", Integer.MIN_VALUE);
        if (videoPosition != Integer.MIN_VALUE) {
            items.addAll(YouTubeItemJsonHelper.fromJson(YouTubeVideo.class, getIntent().getStringExtra("ITEMS")));
            playlistID = getIntent().getStringExtra("PLAYLIST_ID");
            nextPageToken = getIntent().getStringExtra("NEXT_PAGE_TOKEN");
            videoID = items.get(videoPosition).getId();
            videoTitle = items.get(videoPosition).getName();
        } else {
            videoID = getIntent().getStringExtra("VIDEO_ID");
            videoTitle = getIntent().getStringExtra("VIDEO_TITLE");
        }

        YouTubePlayerFragment youTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);

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

        youTubePlayerFragment.initialize(YoutubeInfo.DEVELOPER_KEY, this);
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
                youTubePlayer.cueVideo(videoID, currentVideoTime);
            }
        } else {
            if (videoPosition != Integer.MIN_VALUE) {
                youTubePlayer.loadPlaylist(playlistID, videoPosition, currentVideoTime);
            } else {
                youTubePlayer.loadVideo(videoID, currentVideoTime);
            }
        }
    }

    private void startNewActivity() {
        Intent intent = new Intent(VideoActivity.this, VideoActivity.class);
        intent.putExtra("PLAYLIST_ID", playlistID);
        intent.putExtra("VIDEO_POSITION", videoPosition);
        intent.putExtra("NEXT_PAGE_TOKEN", nextPageToken);
        intent.putExtra("ITEMS", YouTubeItemJsonHelper.toJson(items));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        if (result == YouTubeInitializationResult.SERVICE_MISSING) {
            MissingServiceMenu serviceMenu = new MissingServiceMenu(VideoActivity.this,
                    getString(R.string.service_missing), YoutubeInfo.YOUTUBE_PACKAGE);
            serviceMenu.ShowDialog();
        } else if (result == YouTubeInitializationResult.SERVICE_DISABLED) {
            Toast.makeText(VideoActivity.this, R.string.service_disabled, Toast.LENGTH_LONG).show();
        } else if (result == YouTubeInitializationResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            Toast.makeText(VideoActivity.this, R.string.service_version_update_required, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(VideoActivity.this, result.toString(), Toast.LENGTH_LONG).show();
        }
    }

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
            currentVideoTime = youTubePlayer.getCurrentTimeMillis();
        }
        isMinimized = true;
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
            youTubePlayer.seekRelativeMillis(-duration * 1000);
        }
    }

    @Override
    public void youtubePlayerGoForward() {
        if (youTubePlayer != null) {
            SharedPreferences preferences = getSharedPreferences("SEEK_DURATION", Context.MODE_PRIVATE);
            int duration = preferences.getInt("DURATION", 5);
            youTubePlayer.seekRelativeMillis(duration * 1000);
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
