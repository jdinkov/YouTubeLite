package com.wordpress.dnvsoft.youtubelite;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.wordpress.dnvsoft.youtubelite.adapters.YouTubeItemAdapter;
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetHomeScreenItems;
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetItems;
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetPlaylistItemCount;
import com.wordpress.dnvsoft.youtubelite.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.youtubelite.menus.SortMenu;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeChannel;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeItem;
import com.wordpress.dnvsoft.youtubelite.models.YouTubePlayList;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeVideo;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private boolean hasChanged;
    private boolean hasChangedAtAll;
    private String searchParameter = "";
    private ArrayList<YouTubeItem> youTubeItems = new ArrayList<>();
    private String nextPageToken;
    private LinearLayout footer;
    private ProgressBar progressBar;
    private YouTubeItemAdapter<YouTubeItem> youTubeItemAdapter;
    private AsyncGetHomeScreenItems homeScreenItems;

    private enum TaskStatus {FINISHED, RUNNING}

    public HomeFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ListView listView = view.findViewById(R.id.listViewHome);
        EditText editText = view.findViewById(R.id.searchText);
        ImageView imageViewSearch = view.findViewById(R.id.searchButton);
        progressBar = view.findViewById(R.id.progressBarHome);

        footer = (LinearLayout) inflater.inflate(R.layout.footer_main, listView, false);
        Button buttonLoadMore = footer.findViewById(R.id.buttonFooterMain);
        footer.setVisibility(View.GONE);
        listView.addFooterView(footer, null, false);

        youTubeItemAdapter = new YouTubeItemAdapter<>(getActivity(), youTubeItems);

        listView.setAdapter(youTubeItemAdapter);

        editText.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);
        editText.setOnKeyListener(onKeyListener);
        buttonLoadMore.setOnClickListener(onClickListener);
        imageViewSearch.setOnClickListener(onClickListener);
        listView.setOnItemClickListener(onItemClickListener);
        editText.setOnFocusChangeListener(onFocusChangeListener);
        editText.addTextChangedListener(textWatcher);

        hasChanged = false;
        if (youTubeItems.isEmpty()) {
            getHomeScreenVideos();
        }

        updateView(TaskStatus.FINISHED);

        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (hasChangedAtAll) {
                getYouTubeItems(getOrderFromPreferences());
            } else {
                getHomeScreenVideos();
            }
        }
    };

    ListView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (youTubeItems.get(position) instanceof YouTubeVideo) {
                Intent intent = new Intent(getActivity(), VideoActivity.class);
                intent.putExtra("VIDEO_ID", youTubeItems.get(position).getId());
                intent.putExtra("VIDEO_TITLE", youTubeItems.get(position).getName());
                startActivity(intent);
            } else if (youTubeItems.get(position) instanceof YouTubeChannel) {
                Intent intent = new Intent(getActivity(), ChannelActivity.class);
                intent.putExtra("CHANNEL_ID", youTubeItems.get(position).getId());
                intent.putExtra("CHANNEL_NAME", youTubeItems.get(position).getName());
                startActivity(intent);
            } else if (youTubeItems.get(position) instanceof YouTubePlayList) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,
                        PlayListItemsFragment.newInstance(youTubeItems.get(position)));
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        }
    };

    View.OnKeyListener onKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                getYouTubeItems(getOrderFromPreferences());
                hideKeyboard(v);
            }
            return false;
        }
    };

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!searchParameter.equals(s.toString())) {
                hasChanged = true;
                searchParameter = s.toString();
            }
        }
    };

    EditText.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        }
    };

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void getHomeScreenVideos() {
        homeScreenItems = new AsyncGetHomeScreenItems(
                getActivity(), nextPageToken,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled() && result.getYouTubeVideos() != null) {
                            nextPageToken = result.getNextPageToken();
                            youTubeItems.addAll(result.getYouTubeVideos());
                        }
                        updateView(TaskStatus.FINISHED);
                    }
                });

        homeScreenItems.execute();
        updateView(TaskStatus.RUNNING);
    }

    private void updateView(TaskStatus taskStatus) {
        if (taskStatus == TaskStatus.RUNNING) {
            progressBar.setVisibility(View.VISIBLE);
            footer.setVisibility(View.GONE);
        } else if (taskStatus == TaskStatus.FINISHED) {
            if (youTubeItems.size() != 0 && youTubeItems.size() % 20 == 0) {
                footer.setVisibility(View.VISIBLE);
            }
            if (nextPageToken == null) {
                footer.setVisibility(View.GONE);
            }
            youTubeItemAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void getYouTubeItems(String order) {
        setHasOptionsMenu(true);

        if (hasChanged) {
            youTubeItems.clear();
            nextPageToken = null;
        }
        hasChanged = false;
        hasChangedAtAll = true;

        if (homeScreenItems.getStatus() == AsyncTask.Status.RUNNING) {
            homeScreenItems.cancel(true);
        }

        AsyncGetItems getItems = new AsyncGetItems(getActivity(),
                searchParameter, order, nextPageToken,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled() && result.getYouTubeItems() != null) {
                            nextPageToken = result.getNextPageToken();
                            youTubeItems.addAll(result.getYouTubeItems());
                            getPlaylistItemCount();
                            if (youTubeItems.size() == 0) {
                                Toast.makeText(getActivity(), "No results found", Toast.LENGTH_LONG).show();
                            }
                        }
                        updateView(TaskStatus.FINISHED);
                    }
                });

        getItems.execute();
        updateView(TaskStatus.RUNNING);
    }

    private ArrayList<String> getPlaylistIds() {
        ArrayList<String> playlistIds = new ArrayList<>();
        for (YouTubeItem item : youTubeItems) {
            if (item instanceof YouTubePlayList) {
                if (item.getItemCount() == null) {
                    playlistIds.add(item.getId());
                }
            }
        }

        return playlistIds;
    }

    private void getPlaylistItemCount() {
        ArrayList<String> playlistIds = getPlaylistIds();
        if (!playlistIds.isEmpty()) {
            AsyncGetPlaylistItemCount itemCount = new AsyncGetPlaylistItemCount(
                    getActivity(), playlistIds,
                    new TaskCompleted() {
                        @Override
                        public void onTaskComplete(YouTubeResult result) {
                            if (!result.isCanceled() && result.getYouTubePlayLists() != null) {
                                for (int i = 0; i < youTubeItems.size(); i++) {
                                    for (YouTubePlayList playList : result.getYouTubePlayLists()) {
                                        if (youTubeItems.get(i).getId().equals(playList.getId())) {
                                            youTubeItems.get(i).setItemCount(playList.getItemCount());
                                        }
                                    }
                                }

                                youTubeItemAdapter.notifyDataSetChanged();
                            }
                        }
                    }
            );

            itemCount.execute();
        }
    }

    private String getOrderFromPreferences() {
        SharedPreferences preferences = getActivity().getSharedPreferences("MENU_ORDER", Context.MODE_PRIVATE);
        return preferences.getString("SELECTED_ORDER", "relevance");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            SortMenu menu = new SortMenu(getActivity(),
                    new SortMenu.SortMenuCallback() {
                        @Override
                        public void OnOrderSelected(String order) {
                            youTubeItems.clear();
                            nextPageToken = null;
                            getYouTubeItems(order);
                        }
                    });
            menu.SortItems();
        }
        return super.onOptionsItemSelected(item);
    }
}
