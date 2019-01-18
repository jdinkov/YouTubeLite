package com.wordpress.dnvsoft.youtubelite.video_player_mappers;

public abstract class VideoPlayerMapper {

    public abstract void initialize(String videoId);

    public abstract void seekSeconds(int seconds);

    public abstract void pause();

    public abstract void release();

    public abstract void setFullscreen(boolean fullscreen);
}
