package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;

import com.activeandroid.query.Delete;
import com.codepath.apps.mysimpletweets.app.TwitterApplication;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.net.TwitterClient;
import com.codepath.apps.mysimpletweets.util.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Created by mkrish4
 */
public class HomeTimelineFragment extends TweetsListFragment {
    private TwitterClient client;
    private boolean lastOnline = false;

    // Creation life cycle event
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the client
        client = TwitterApplication.getRestClient();

        populateTimeline(0);
    }


    // Send and API request to get the timeline json
    // Fill the listview by creating the tweet objects from the json
    @Override
    public void populateTimeline(long page) {

        if (Utils.isNetworkAvailable(getActivity())) {

            // First load or re-load
            if ((page == 0) || !lastOnline) {
                lastOnline = true;
                // Remove everything from the db
                new Delete().from(Tweet.class).execute();
                // Remove everything from the list
                removeAll();
            }

            // Get the tweets
            client.getHomeTimeline(page, new JsonHttpResponseHandler() {

                ProgressBarListener progressBarListener = (ProgressBarListener) getActivity();

                //SUCCESS
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                    // Deserialize JSON and add them to adapter
                    addAll(Tweet.fromJSONArray(json, true));
                    // notify swipecontained that update is finished
                    timelineUpdateFinished();
                    progressBarListener.hideProgressBar();
                }

                //FAILURE
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    // Just in case our request ends up here
                    // Notify swipe container that update is finished
                    timelineUpdateFinished();
                    progressBarListener.hideProgressBar();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    timelineUpdateFinished();
                    progressBarListener.hideProgressBar();
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    progressBarListener.showProgressBar();
                }

                @Override
                public void onStart() {
                    progressBarListener.showProgressBar();
                }

                @Override
                public void onFinish() {
                    progressBarListener.hideProgressBar();
                }
            });
        }
        else {
            // Show NO INTERNET message
            Utils.showNoInternet(getActivity());

            // Set to false, so once internet is back remove offline data from the list
            lastOnline = false;

            // Clear whatever we had, every time, then reload from db.
            removeAll();
            // Load offline data from the database
            addAll(Tweet.fromDB());
            // Notify the swiper container that update is finished
            timelineUpdateFinished();
        }
    }

    public void reloadTimeline() {
        populateTimeline(0);
    }
}
