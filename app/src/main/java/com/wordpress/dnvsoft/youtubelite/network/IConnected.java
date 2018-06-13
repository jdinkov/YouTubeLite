package com.wordpress.dnvsoft.youtubelite.network;

public interface IConnected {
    void onPreExecute();
    void onPostExecute();
    void onDisconnected();
    void onCanceled();
}
