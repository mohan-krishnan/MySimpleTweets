package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;

import com.codepath.apps.mysimpletweets.app.TwitterApplication;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.net.TwitterClient;
import com.codepath.apps.mysimpletweets.util.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;

/**
 * Created by mkrish4
 */

public class UserTimelineFragment extends TweetsListFragment {
    private TwitterClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the client
        client = TwitterApplication.getRestClient();

        populateTimeline(0);
    }

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        userTimelineFragment.setArguments(args);
        return userTimelineFragment;
    }

    // Send and API request to get the timeline json
    // Fill the listview by creating the tweet objects from the json
    @Override
    public void populateTimeline(long page) {

        String screenName = getArguments().getString("screen_name");

        if (Utils.isNetworkAvailable(getActivity())) {
            // First load / re-load => clear all
            if (page == 0) {
                removeAll();
            }
            // Get the tweets
            client.getUserTimeline(screenName, page, new JsonHttpResponseHandler() {
                //SUCCESS
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                    // Deserialize JSON and add them to adapter
                    addAll(Tweet.fromJSONArray(json, false));
                    // Notify swipe container that update is finished
                    timelineUpdateFinished();
                }

                //FAILURE
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    // Just in case our request ends up here
                    // Notify swipe container that update is finished
                    timelineUpdateFinished();
                }

            });
        } else {
            // Show NO INTERNET message
            Utils.showNoInternet(getActivity());

            // Notify swipe container that update is finished
            timelineUpdateFinished();
        }
    }
}
