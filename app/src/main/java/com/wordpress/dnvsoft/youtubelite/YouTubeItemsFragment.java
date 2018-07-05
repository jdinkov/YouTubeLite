package com.wordpress.dnvsoft.youtubelite;

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
import android.widget.TextView;

import com.wordpress.dnvsoft.youtubelite.adapters.YouTubeItemAdapter;
import com.wordpress.dnvsoft.youtubelite.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeItem;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

import java.util.ArrayList;

public abstract class YouTubeItemsFragment extends Fragment {

    private LinearLayout footer;
    private Button buttonLoadMore;
    private TextView textView;
    ListView listView;
    YouTubeItemAdapter<YouTubeItem> adapter;
    String nextPageToken;
    ArrayList<YouTubeItem> youTubeItems = new ArrayList<>();
    boolean responseHasReceived;

    enum YouTubeRequest {NOT_SEND, RECEIVED, RECEIVED_EMPTY}

    public YouTubeItemsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onCreateYouTubeItemsFragment();
    }

    abstract void onCreateYouTubeItemsFragment();

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        updateViewContentInfo();
        if (responseHasReceived) {
            updateViewFooter(YouTubeRequest.RECEIVED);
        } else {
            updateViewFooter(YouTubeRequest.NOT_SEND);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_items, container, false);

        listView = view.findViewById(R.id.listViewItem);

        textView = view.findViewById(R.id.textViewContentInfo);
        footer = (LinearLayout) inflater.inflate(R.layout.footer_main, listView, false);
        buttonLoadMore = footer.findViewById(R.id.buttonFooterMain);
        listView.addFooterView(footer, null, false);

        buttonLoadMore.setOnClickListener(buttonLoadMoreOnClickListener);
        listView.setOnItemClickListener(onItemClickListener);

        adapter = new YouTubeItemAdapter<>(getContext(), youTubeItems);
        listView.setAdapter(adapter);

        updateViewFooter(YouTubeRequest.NOT_SEND);
        adapter.notifyDataSetChanged();

        return view;
    }

    View.OnClickListener buttonLoadMoreOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            footer.setVisibility(View.GONE);
            onButtonLoadMoreClicked();
        }
    };

    void onButtonLoadMoreClicked() {
        getItemsFromYouTube();
    }

    abstract void getItemsFromYouTube();

    ListView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            onVideoClick(position);
        }
    };

    abstract void onVideoClick(int position);

    void updateViewContentInfo() {
        if (youTubeItems.isEmpty() && responseHasReceived) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(getContentString());
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    void updateViewFooter(YouTubeRequest request) {
        if (youTubeItems.size() != 0) {
            if (youTubeItems.size() % 20 == 0 && nextPageToken != null) {
                footer.setVisibility(View.VISIBLE);
                buttonLoadMore.setText(R.string.load_more);
            } else {
                footer.setVisibility(View.GONE);
            }
        } else {
            if (request == YouTubeRequest.NOT_SEND || request == YouTubeRequest.RECEIVED_EMPTY) {
                footer.setVisibility(View.GONE);
            } else if (request == YouTubeRequest.RECEIVED) {
                footer.setVisibility(View.VISIBLE);
                buttonLoadMore.setText(R.string.refresh);
            }
        }
    }

    public TaskCompleted asyncTaskCompleted = new TaskCompleted() {
        @Override
        public void onTaskComplete(YouTubeResult result) {
            if (!result.isCanceled() && result.getYouTubeVideos() != null) {
                nextPageToken = result.getNextPageToken();
                youTubeItems.addAll(result.getYouTubeVideos());
                adapter.notifyDataSetChanged();
            }

            responseHasReceived = true;

            updateViewContentInfo();
            updateViewFooter(YouTubeRequest.RECEIVED);
        }
    };

    abstract String getContentString();
}
