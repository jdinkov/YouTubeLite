package com.wordpress.dnvsoft.youtubelite;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.wordpress.dnvsoft.youtubelite.menus.SearchChannelVideosMenu;

public class ChannelActivity extends AppCompatActivity {

    private String channelId;
    private int screenX;
    private Menu menu;

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

        editor = getSharedPreferences("CHANNEL_FRAGMENT_VIDEOS", MODE_PRIVATE).edit();
        editor.remove("VIDEOS_PAGE_TOKEN");
        editor.remove("VIDEOS");
        editor.apply();

        setTitle(channelName);

        TabsAdapter mSectionsPagerAdapter = new TabsAdapter(getSupportFragmentManager());

        ViewPager mViewPager = findViewById(R.id.channels_container_video);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(onPageChangeListener);

        TabLayout tabLayout = findViewById(R.id.channel_tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

//        GetChannelBanner();
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            getSupportFragmentManager().popBackStack();
            if (position != 0) {
                menu.getItem(0).setVisible(false);
            } else {
                menu.getItem(0).setVisible(true);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    // It's deprecated
    /*private void GetChannelBanner() {
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
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.channel_videos_menu, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            SearchChannelVideosMenu menu = new SearchChannelVideosMenu(ChannelActivity.this, channelId);
            menu.Show();
        }

        return super.onOptionsItemSelected(item);
    }

    public class TabsAdapter extends FragmentPagerAdapter {

        private ChannelFragmentRootVideos fragmentVideos;
        private ChannelFragmentRootPlayLists fragmentPlayLists;
        private ChannelFragmentFeaturedChannels fragmentFeaturedChannels;
        private ChannelFragmentAbout fragmentAbout;

        TabsAdapter(FragmentManager fm) {
            super(fm);
            fragmentVideos = ChannelFragmentRootVideos.newInstance(channelId);
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
