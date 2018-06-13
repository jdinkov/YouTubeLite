package com.wordpress.dnvsoft.youtubelite.async_tasks;

import com.wordpress.dnvsoft.youtubelite.models.YouTubeResult;

public interface TaskCompleted {

    void onTaskComplete(YouTubeResult result);
}
