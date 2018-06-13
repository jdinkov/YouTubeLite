package com.wordpress.dnvsoft.youtubelite;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class VideoFragmentRootComments extends Fragment {

    public VideoFragmentRootComments() {
    }

    public static VideoFragmentRootComments newInstance(String id) {
        VideoFragmentRootComments videoFragmentRootComments = new VideoFragmentRootComments();
        Bundle bundle = new Bundle();
        bundle.putString("VIDEO_ID", id);
        videoFragmentRootComments.setArguments(bundle);
        return videoFragmentRootComments;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_root_comments, container, false);

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.root_fragment,
                VideoFragmentComments.newInstance(getArguments().getString("VIDEO_ID")));
        fragmentTransaction.commit();

        return view;
    }
}
