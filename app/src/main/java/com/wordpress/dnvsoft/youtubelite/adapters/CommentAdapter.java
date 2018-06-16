package com.wordpress.dnvsoft.youtubelite.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wordpress.dnvsoft.youtubelite.OnCommentAddEditListener;
import com.wordpress.dnvsoft.youtubelite.R;
import com.wordpress.dnvsoft.youtubelite.menus.CommentOptionMenu;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeComment;

import java.util.ArrayList;

public class CommentAdapter<T extends YouTubeComment> extends ArrayAdapter<T> {

    protected Context context;
    protected int layout;
    private boolean isReply;
    OnCommentAddEditListener listener;
    String channelId;
    ArrayList<T> objects;

    public CommentAdapter(@NonNull Context context, int layout, ArrayList<T> objects, boolean isReply,
                          OnCommentAddEditListener listener) {
        super(context, layout, objects);
        this.context = context;
        this.layout = layout;
        this.objects = objects;
        this.isReply = isReply;
        SharedPreferences preferences =
                context.getSharedPreferences("CHANNEL_ID_PREFERENCES", Context.MODE_PRIVATE);
        channelId = preferences.getString("CHANNEL_ID", null);
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, parent, false);
        }

        if (isReply) {
            float scale = context.getResources().getDisplayMetrics().density;
            convertView.setPadding((int) scale * 50, 0, 0, 0);
        }

        ImageView imageViewProfilePic = convertView.findViewById(R.id.imageViewProfilePic);
        TextView textViewProfileName = convertView.findViewById(R.id.textViewProfileName);
        TextView textViewCommentText = convertView.findViewById(R.id.textViewCommentText);
        ImageView imageViewLike = convertView.findViewById(R.id.imageViewLike);
        TextView textViewLikeCount = convertView.findViewById(R.id.textViewLikeCount);
        ImageView imageViewDislike = convertView.findViewById(R.id.imageViewDislike);
        Button buttonEditComment = convertView.findViewById(R.id.buttonEditComment);

        final YouTubeComment youTubeComment = objects.get(position);
        Picasso.with(context).load(youTubeComment.getAuthorImageUrl()).into(imageViewProfilePic);
        textViewProfileName.setText(youTubeComment.getAuthorDisplayName());
        textViewCommentText.setText(youTubeComment.getCommentText());
        textViewLikeCount.setText(youTubeComment.getLikeCount());
        buttonEditComment.setTag(position);
        if (isReply && !youTubeComment.getAuthorChannelId().equals(channelId)) {
            buttonEditComment.setVisibility(View.GONE);
        } else {
            buttonEditComment.setVisibility(View.VISIBLE);
        }
        buttonEditComment.setOnClickListener(editOnClickListener);
        switch (youTubeComment.getViewerRating()) {
            case "like": {
                imageViewLike.setImageDrawable(convertView.getResources().getDrawable(R.drawable.liked_video));
            }
            break;
            case "dislike": {
                imageViewDislike.setImageDrawable(convertView.getResources().getDrawable(R.drawable.disliked_video));
            }
            break;
        }

        return convertView;
    }

    private View.OnClickListener editOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            SelectOption(position);
        }
    };

    protected void SelectOption(int position) {
        YouTubeComment comment = objects.get(position);
        Enum<CommentOptionMenu.OptionsToDisplay> option;
        if (comment.getAuthorChannelId().equals(channelId)) {
            option = CommentOptionMenu.OptionsToDisplay.EDIT;
        } else {
            option = CommentOptionMenu.OptionsToDisplay.NONE;
        }

        CommentOptionMenu menu = new CommentOptionMenu(
                context, comment.getID(), comment.getCommentText(), option, null, listener);
        menu.ShowDialog();
    }
}
