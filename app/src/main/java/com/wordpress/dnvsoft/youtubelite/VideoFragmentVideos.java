package com.wordpress.dnvsoft.youtubelite;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.wordpress.dnvsoft.youtubelite.adapters.YouTubeItemAdapter;
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetVideos;
import com.wordpress.dnvsoft.youtubelite.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.youtubelite.models.VideoItemWrapper;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeVideo;
import com.wordpress.dnvsoft.youtubelite.network.Network;

import java.util.ArrayList;

public class VideoFragmentVideos extends Fragment {

    private LinearLayout footer;
    private Button buttonLoadMore;
    private YouTubeItemAdapter<YouTubeVideo> adapter;
    private String nextPageToken;
    private String playlistId;
    private String videoID;
    private ArrayList<YouTubeVideo> youTubeVideos = new ArrayList<>();

    public VideoFragmentVideos() {
    }

    public static VideoFragmentVideos newInstance(
            ArrayList<YouTubeVideo> items, String playlistId, String videoID) {
        VideoFragmentVideos videoFragmentVideos = new VideoFragmentVideos();
        Bundle bundle = new Bundle();
        bundle.putSerializable("ITEMS", new VideoItemWrapper(items));
        bundle.putString("PLAYLIST_ID", playlistId);
        bundle.putString("VIDEO_ID", videoID);
        videoFragmentVideos.setArguments(bundle);
        return videoFragmentVideos;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VideoItemWrapper wrapper = (VideoItemWrapper) getArguments().getSerializable("ITEMS");
        youTubeVideos.addAll(wrapper.getItems());
        playlistId = getArguments().getString("PLAYLIST_ID");
        videoID = getArguments().getString("VIDEO_ID");

        if (playlistId == null) {
            getVideosFromYouTube();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_videos, container, false);

        ListView listView = view.findViewById(R.id.listViewVideo);

        footer = (LinearLayout) inflater.inflate(R.layout.footer_main, listView, false);
        buttonLoadMore = footer.findViewById(R.id.buttonFooterMain);
        if (Network.IsDeviceOnline(getActivity())) {
            footer.setVisibility(View.GONE);
        } else {
            footer.setVisibility(View.VISIBLE);
            buttonLoadMore.setText(R.string.refresh);
        }
        listView.addFooterView(footer, null, false);

        buttonLoadMore.setOnClickListener(buttonLoadMoreOnClickListener);
        listView.setOnItemClickListener(onItemClickListener);

        adapter = new YouTubeItemAdapter<>(getContext(), youTubeVideos);
        listView.setAdapter(adapter);

        if (playlistId != null) {
            footer.setVisibility(View.GONE);
        }

        adapter.notifyDataSetChanged();

        return view;
    }

    View.OnClickListener buttonLoadMoreOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            footer.setVisibility(View.GONE);
            getVideosFromYouTube();
        }
    };

    private void getVideosFromYouTube() {
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
                                    youTubeVideos.add(video);
                                }
                            }

                            buttonLoadMore.setText(R.string.load_more);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

        getVideos.execute();
    }

    ListView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), VideoActivity.class);
            if (playlistId != null) {
                int videoPosition = youTubeVideos.size() - position - 1;

                intent.putExtra("PLAYLIST_ID", playlistId);
                intent.putExtra("VIDEO_POSITION", videoPosition);
                intent.putExtra("ITEMS", new VideoItemWrapper(youTubeVideos));
            } else {
                intent.putExtra("VIDEO_ID", youTubeVideos.get(position).getId());
                intent.putExtra("VIDEO_TITLE", youTubeVideos.get(position).getName());
            }

            startActivity(intent);
        }
    };
}
