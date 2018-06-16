package com.wordpress.dnvsoft.youtubelite;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class ChannelActivity extends AppCompatActivity {

    private String channelId;
    private String channelName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        channelId = getIntent().getStringExtra("CHANNEL_ID");
        channelName = getIntent().getStringExtra("CHANNEL_NAME");

        TabsAdapter mSectionsPagerAdapter = new TabsAdapter(getSupportFragmentManager());

        ViewPager mViewPager = findViewById(R.id.channels_container_video);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.channel_tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    public class TabsAdapter extends FragmentPagerAdapter {

        private YouTubeItemsFragment fragmentVideos;

        TabsAdapter(FragmentManager fm) {
            super(fm);
            fragmentVideos = ChannelFragmentVideos.newInstance(channelId);
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
                    fragment = new ChannelFragmentPlayLists();
                }
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
