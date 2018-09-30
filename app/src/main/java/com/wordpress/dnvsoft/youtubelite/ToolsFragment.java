package com.wordpress.dnvsoft.youtubelite;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.wordpress.dnvsoft.youtubelite.menus.AboutMenu;
import com.wordpress.dnvsoft.youtubelite.menus.ChooseVideoPlayerMenu;
import com.wordpress.dnvsoft.youtubelite.menus.SeekDurationMenu;

public class ToolsFragment extends Fragment {

    String[] aboutEntities = {
            "Choose video player",
            "Video player seek duration",
            "About"
    };

    public ToolsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tools, container, false);

        ListView listViewAbout = view.findViewById(R.id.listViewAbout);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, aboutEntities);
        listViewAbout.setAdapter(arrayAdapter);
        listViewAbout.setOnItemClickListener(onItemClickListener);

        return view;
    }

    ListView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0: {
                    ChooseVideoPlayerMenu chooseVideoPlayerMenu = new ChooseVideoPlayerMenu(getActivity());
                    chooseVideoPlayerMenu.Show();
                }
                break;
                case 1: {
                    SeekDurationMenu seekDurationMenu = new SeekDurationMenu(getActivity());
                    seekDurationMenu.Show();
                }
                break;
                case 2: {
                    AboutMenu aboutMenu = new AboutMenu(getActivity());
                    aboutMenu.Show();
                }
                break;
            }
        }
    };
}
