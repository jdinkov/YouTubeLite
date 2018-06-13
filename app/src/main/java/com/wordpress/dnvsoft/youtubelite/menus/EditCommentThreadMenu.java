package com.wordpress.dnvsoft.youtubelite.menus;

import android.content.Context;
import android.widget.Toast;

import com.wordpress.dnvsoft.youtubelite.OnCommentAddEditListener;
import com.wordpress.dnvsoft.youtubelite.async_tasks.AsyncEditCommentThread;
import com.wordpress.dnvsoft.youtubelite.async_tasks.TaskCompleted;
import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

public class EditCommentThreadMenu extends EditCommentMenu {

    EditCommentThreadMenu(Context context, String Id, String commentText, OnCommentAddEditListener listener) {
        super(context, Id, commentText, listener);
    }

    @Override
    protected void updateOnClick() {
        AsyncEditCommentThread editCommentThread = new AsyncEditCommentThread(
                context, Id, editText.getText().toString(),
                new TaskCompleted() {
                    @Override
                    public void onTaskComplete(YouTubeResult result) {
                        if (!result.isCanceled()) {
                            Toast.makeText(context, "Comment updated.", Toast.LENGTH_SHORT).show();
                            onCommentEditListener.onFinishEdit();
                        }
                    }
                });

        editCommentThread.execute();
    }
}
