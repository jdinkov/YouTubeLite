package com.wordpress.dnvsoft.youtubelite.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class YouTubeItemJsonHelper implements Serializable {

    public static String toJson(ArrayList<? extends YouTubeItem> youTubeItems) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < youTubeItems.size(); i++) {
                JSONObject item = new JSONObject();
                item.put("id", youTubeItems.get(i).getId());
                item.put("name", youTubeItems.get(i).getName());
                item.put("thumbnailURL", youTubeItems.get(i).getThumbnailURL());
                jsonArray.put(i, item);
            }
            jsonObject.put("items", jsonArray);
        } catch (JSONException e) {
            return null;
        }

        return jsonObject.toString();
    }

    public static ArrayList<YouTubeVideo> fromJson(String jsonString) {
        ArrayList<YouTubeVideo> arrayList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(new JSONObject(jsonString).getJSONArray("items").toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = new JSONObject(jsonArray.getString(i));
                YouTubeVideo tempCommentThread = new YouTubeVideo();
                tempCommentThread.setId(item.getString("id"));
                tempCommentThread.setName(item.getString("name"));
                tempCommentThread.setThumbnailURL(item.getString("thumbnailURL"));
                arrayList.add(tempCommentThread);
            }
        } catch (JSONException e) {
            return arrayList;
        }

        return arrayList;
    }
}
