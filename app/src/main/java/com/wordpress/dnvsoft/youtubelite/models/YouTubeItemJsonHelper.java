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
                item.put("itemCount", youTubeItems.get(i).getItemCount());
                jsonArray.put(i, item);
            }
            jsonObject.put("items", jsonArray);
        } catch (JSONException e) {
            return null;
        }

        return jsonObject.toString();
    }

    public static <T extends YouTubeItem> ArrayList<T> fromJson(Class<T> c, String jsonString) {
        ArrayList<T> arrayList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(new JSONObject(jsonString).getJSONArray("items").toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = new JSONObject(jsonArray.getString(i));
                T youTubeItem = c.newInstance();
                youTubeItem.setId(item.getString("id"));
                youTubeItem.setName(item.getString("name"));
                youTubeItem.setThumbnailURL(item.getString("thumbnailURL"));
                try {
                    youTubeItem.setItemCount(item.getString("itemCount"));
                } catch (JSONException e) {
                    arrayList.add(youTubeItem);
                    continue;
                }
                arrayList.add(youTubeItem);
            }
        } catch (JSONException e) {
            return arrayList;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return arrayList;
    }
}
