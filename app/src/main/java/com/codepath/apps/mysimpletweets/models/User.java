package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mkrish4
 */
@Table(name= "user")
public class User extends Model {
    // Attributes
    @Column(name= "name")
    private String name;

    @Column(name= "uid")
    private long uid;

    @Column(name= "screenname")
    private String screenName;

    @Column(name= "profileImageUrl")
    private String profileImageUrl;

    private String tagLine;
    private long tweetsCount;
    private long followersCount;
    private long followingCount;

    // Deserialize the user json =>user
    public static User fromJSON(JSONObject json) {
        User u = new User();
        // Extract and full the values
        try {
            u.name = json.getString("name");
            u.uid = json.getLong("id");
            u.screenName = json.getString("screen_name");
            u.profileImageUrl = json.getString("profile_image_url");

            // User class being too generic, we want to make sure these items exist in json
            // and if don't no exception is thrown but just not populated
            u.tagLine = hasJSONObject(json, "description") ? json.getString("description") : "";
            u.followersCount = hasJSONObject(json, "followers_count") ? json.getLong("followers_count") : 0;
            u.followingCount = hasJSONObject(json, "friends_count") ? json.getLong("friends_count") : 0;
            u.tweetsCount = hasJSONObject(json, "statuses_count") ? json.getLong("statuses_count") : 0;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return u;
    }

    //Check if items exist in the json object
    private static boolean hasJSONObject(JSONObject json, String item) throws JSONException {
        return json.get(item) != null;
    }

    // Getters
    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getTagLine() { return tagLine; }

    public long getTweetsCount() { return tweetsCount; }

    public long getFollowersCount() { return followersCount; }

    public long getFollowingCount() { return followingCount; }

    // Default Constructor
    public User() {
        super();
    }
}
