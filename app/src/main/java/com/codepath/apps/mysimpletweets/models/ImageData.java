package com.codepath.apps.mysimpletweets.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by mkrish4
 */
public class ImageData {
    private int width;
    private int height;
    private String url;

    public static ArrayList<ImageData> fromJSONArray(JSONArray jsonArray) {
        ArrayList<ImageData> imageData = new ArrayList<>();

        for(int i=0; i<jsonArray.length(); i++)
            try {
                JSONObject imgJSON = jsonArray.getJSONObject(i);
                ImageData img = new ImageData();
                img.setUrl(imgJSON.getString("media_url"));
                img.setWidth(imgJSON.getJSONObject("sizes").getJSONObject("large").getInt("w"));
                img.setHeight(imgJSON.getJSONObject("sizes").getJSONObject("large").getInt("h"));
                imageData.add(img);
            } catch (JSONException e) {

            }
        return imageData;
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getUrl() {
        return url;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
