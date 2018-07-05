package com.wordpress.dnvsoft.youtubelite;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetChannelBanner;
import com.wordpress.dnvsoft.youtubelite.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

public class ChannelActivity extends AppCompatActivity {

    private String channelId;
    private int screenX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        channelId = getIntent().getStringExtra("CHANNEL_ID");
        String channelName = getIntent().getStringExtra("CHANNEL_NAME");

        SharedPreferences.Editor editor = getSharedPreferences("CHANNEL_FRAGMENT_PLAYLISTS", MODE_PRIVATE).edit();
        editor.remove("PLAYLISTS_PAGE_TOKEN");
        editor.remove("PLAYLISTS");
        editor.apply();

        setTitle(channelName);

        TabsAdapter mSectionsPagerAdapter = new TabsAdapter(getSupportFragmentManager());

        ViewPager mViewPager = findViewById(R.id.channels_container_video);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(onPageChangeListener);

        TabLayout tabLayout = findViewById(R.id.channel_tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        GetChannelBanner();
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if (position != 1) {
                getSupportFragmentManager().popBackStack();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private void GetChannelBanner() {
        AsyncGetChannelBanner getChannelBanner = new AsyncGetChannelBanner(
                ChannelActivity.this, channelId,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled() && result.getYouTubeChannel().getBannerUrl() != null) {
                            ImageView imageViewBanner = findViewById(R.id.imageViewBanner);

                            Picasso.with(ChannelActivity.this)
                                    .load(result.getYouTubeChannel().getBannerUrl())
                                    .into(imageViewBanner);

                            Display display = getWindowManager().getDefaultDisplay();
                            Point point = new Point();
                            display.getSize(point);
                            screenX = point.x;
                            double size = ((screenX / 48) * 13);

                            imageViewBanner.getLayoutParams().height = (int) size;
                        }
                    }
                });

        getChannelBanner.execute();
    }

    public class TabsAdapter extends FragmentPagerAdapter {

        private ChannelFragmentVideos fragmentVideos;
        private ChannelFragmentRootPlayLists fragmentPlayLists;
        private ChannelFragmentFeaturedChannels fragmentFeaturedChannels;
        private ChannelFragmentAbout fragmentAbout;

        TabsAdapter(FragmentManager fm) {
            super(fm);
            fragmentVideos = ChannelFragmentVideos.newInstance(channelId);
            fragmentPlayLists = ChannelFragmentRootPlayLists.newInstance(channelId);
            fragmentFeaturedChannels = ChannelFragmentFeaturedChannels.newInstance(channelId);
            fragmentAbout = ChannelFragmentAbout.newInstance(channelId);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0: {
                    fragment = fragmentVideos;
                }
                break;
                case 1: {
                    fragment = fragmentPlayLists;
                }
                break;
                case 2: {
                    fragment = fragmentFeaturedChannels;
                }
                break;
                case 3: {
                    fragment = fragmentAbout;
                }
                break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
