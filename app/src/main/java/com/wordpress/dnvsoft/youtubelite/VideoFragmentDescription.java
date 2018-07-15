package com.wordpress.dnvsoft.youtubelite;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.squareup.picasso.Picasso;
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetChannelInfo;
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetRating;
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncGetVideoDescription;
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncVideosRate;
import com.wordpress.dnvsoft.youtubelite.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

public class VideoFragmentDescription extends Fragment {

    private String videoID;
    private String videoTitle;
    private String videoViewCount;
    private String publishedAt;
    private String description;
    private String videoRating = "none";
    private String likeCount;
    private String dislikeCount;
    private TextView textViewVideoViewCount;
    private TextView textViewPublishedAt;
    private TextView textViewDescription;
    private TextView textViewLikeCount;
    private TextView textViewDislikeCount;
    private RadioButton radioButtonLike;
    private RadioButton radioButtonDislike;
    private RadioGroup radioGroup;
    private final String RATING_NONE = "none";
    private final String RATING_LIKE = "like";
    private final String RATING_DISLIKE = "dislike";
    private OnVideoDescriptionResponse callback;
    private String channelId;
    private String channelTitle;
    private String channelThumbnailUrl;
    private YouTubeThumbnailView thumbnailViewChannel;
    private TextView textViewChannelTitle;

    interface OnVideoDescriptionResponse {
        void setCommentCount(String commentCount);
    }

    public VideoFragmentDescription() {
    }

    public static VideoFragmentDescription newInstance(String id, String title) {
        VideoFragmentDescription videoFragmentDescription = new VideoFragmentDescription();
        Bundle bundle = new Bundle();
        bundle.putString("VIDEO_ID", id);
        bundle.putString("VIDEO_TITLE", title);
        videoFragmentDescription.setArguments(bundle);
        return videoFragmentDescription;
    }

    private void checkRadioGroup(String rating) {
        if (getView() != null && rating != null) {
            switch (rating) {
                case RATING_NONE: {
                    radioGroup.clearCheck();
                }
                break;
                case RATING_LIKE: {
                    radioButtonLike.setChecked(true);
                }
                break;
                case RATING_DISLIKE: {
                    radioButtonDislike.setChecked(true);
                }
                break;
            }
        }
    }

    private void populateViews() {
        if (getView() != null) {
            textViewVideoViewCount.setText(videoViewCount);
            textViewPublishedAt.setText(publishedAt);
            textViewDescription.setText(description);
            textViewLikeCount.setText(likeCount);
            textViewDislikeCount.setText(dislikeCount);
            checkRadioGroup(videoRating);
            textViewChannelTitle.setText(channelTitle);
            Picasso.with(getActivity()).load(channelThumbnailUrl).into(thumbnailViewChannel);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        populateViews();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        callback = (OnVideoDescriptionResponse) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        videoID = getArguments().getString("VIDEO_ID");
        videoTitle = getArguments().getString("VIDEO_TITLE");

        getVideoDescription().execute();
        if (GoogleSignIn.getLastSignedInAccount(getActivity()) != null) {
            getVideoRating().execute();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_video_description, container, false);

        TextView textViewVideoTitle = fragment.findViewById(R.id.textViewDescTitle);
        textViewVideoTitle.setText(videoTitle);

        textViewVideoViewCount = fragment.findViewById(R.id.textViewDescViewCount);
        textViewPublishedAt = fragment.findViewById(R.id.textViewDescPublishedAt);
        textViewDescription = fragment.findViewById(R.id.textViewDescription);
        textViewLikeCount = fragment.findViewById(R.id.likeCount);
        textViewDislikeCount = fragment.findViewById(R.id.dislikeCount);
        radioButtonLike = fragment.findViewById(R.id.radioButtonLike);
        radioButtonDislike = fragment.findViewById(R.id.radioButtonDislike);
        radioGroup = fragment.findViewById(R.id.radioGroupLikedVideos);
        radioButtonLike.setOnClickListener(onRatingChangedClickListener);
        radioButtonDislike.setOnClickListener(onRatingChangedClickListener);
        thumbnailViewChannel = fragment.findViewById(R.id.channelThumbnail);
        textViewChannelTitle = fragment.findViewById(R.id.channelTitle);
        RelativeLayout layoutChannelInfo = fragment.findViewById(R.id.layoutChannelInfo);
        layoutChannelInfo.setOnClickListener(onChannelClickListener);

        populateViews();

        return fragment;
    }

    View.OnClickListener onRatingChangedClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (GoogleSignIn.getLastSignedInAccount(getActivity()) != null) {
                final String tempRating = videoRating;
                switch (v.getId()) {
                    case R.id.radioButtonLike: {
                        if (videoRating.equals(RATING_LIKE)) {
                            videoRating = RATING_NONE;
                        } else {
                            videoRating = RATING_LIKE;
                        }
                    }
                    break;
                    case R.id.radioButtonDislike: {
                        if (videoRating.equals(RATING_DISLIKE)) {
                            videoRating = RATING_NONE;
                        } else {
                            videoRating = RATING_DISLIKE;
                        }
                    }
                    break;
                }

                AsyncVideosRate videosRate = new AsyncVideosRate(getActivity(), videoID, videoRating,
                        new TaskCompleted() {
                            @Override
                            public void onTaskComplete(YouTubeResult result) {
                                if (result.isCanceled()) {
                                    videoRating = tempRating;
                                } else {
                                    ratingChangedToast(videoRating);
                                }

                                checkRadioGroup(videoRating);
                            }
                        });

                videosRate.execute();
            } else {
                Toast.makeText(getActivity(), R.string.unauthorized, Toast.LENGTH_LONG).show();
            }

            checkRadioGroup(videoRating);
        }
    };

    View.OnClickListener onChannelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (channelTitle != null) {
                Intent intent = new Intent(getActivity(), ChannelActivity.class);
                intent.putExtra("CHANNEL_ID", channelId);
                intent.putExtra("CHANNEL_NAME", channelTitle);
                startActivity(intent);
            }
        }
    };

    private AsyncGetVideoDescription getVideoDescription() {
        return new AsyncGetVideoDescription(getActivity(), videoID,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled() && result.getYouTubeVideos() != null) {
                            channelId = result.getYouTubeVideos().get(0).getChannelId();
                            getChannelInfo();

                            publishedAt = "Published on " + result.getYouTubeVideos().get(0).getPublishedAt();
                            description = result.getYouTubeVideos().get(0).getDescription();
                            likeCount = result.getYouTubeVideos().get(0).getLikeCount();
                            dislikeCount = result.getYouTubeVideos().get(0).getDislikeCount();
                            videoViewCount = result.getYouTubeVideos().get(0).getViewCount() + " views";
                            callback.setCommentCount(result.getYouTubeVideos().get(0).getCommentCount());

                            populateViews();
                        }
                    }
                });
    }

    private AsyncGetRating getVideoRating() {
        return new AsyncGetRating(getActivity(), videoID,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled()) {
                            videoRating = result.getYouTubeVideos().get(0).getRating();
                            checkRadioGroup(videoRating);
                        }
                    }
                });
    }

    private void getChannelInfo() {
        AsyncGetChannelInfo channelInfo = new AsyncGetChannelInfo(
                getActivity(), channelId,
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled() && result.getYouTubeChannels() != null) {
                            channelTitle = result.getYouTubeChannels().get(0).getName();
                            channelThumbnailUrl = result.getYouTubeChannels().get(0).getThumbnailURL();

                            populateViews();
                        }
                    }
                }
        );

        channelInfo.execute();
    }

    private void ratingChangedToast(String rating) {
        String toastText = null;
        switch (rating) {
            case RATING_NONE: {
                toastText = getString(R.string.removed_rating);
            }
            break;
            case RATING_LIKE: {
                toastText = getString(R.string.liked_video);
            }
            break;
            case RATING_DISLIKE: {
                toastText = getString(R.string.disliked_video);
            }
            break;
        }

        Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT).show();
    }
}
