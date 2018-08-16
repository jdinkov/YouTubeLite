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
                if (youTubeItems.get(i) instanceof YouTubeVideo) {
                    item.put("videoDuration", ((YouTubeVideo) youTubeItems.get(i)).getDuration());
                    item.put("channelTitle", ((YouTubeVideo) youTubeItems.get(i)).getChannelTitle());
                }
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
                JsonObjectNullableString item = new JsonObjectNullableString(jsonArray.getString(i));
                T youTubeItem = c.newInstance();
                youTubeItem.setId(item.getNullableString("id"));
                youTubeItem.setName(item.getNullableString("name"));
                youTubeItem.setThumbnailURL(item.getNullableString("thumbnailURL"));
                youTubeItem.setItemCount(item.getNullableString("itemCount"));
                if (youTubeItem instanceof YouTubeVideo) {
                    ((YouTubeVideo) youTubeItem).setDuration(item.getNullableString("videoDuration"));
                    ((YouTubeVideo) youTubeItem).setChannelTitle(item.getNullableString("channelTitle"));
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

    private static class JsonObjectNullableString extends JSONObject {

        JsonObjectNullableString(String json) throws JSONException {
            super(json);
        }

        String getNullableString(String name) {
            try {
                return getString(name);
            } catch (JSONException e) {
                return null;
            }
        }
    }
}
