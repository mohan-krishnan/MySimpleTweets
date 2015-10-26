package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.app.TwitterApplication;
import com.codepath.apps.mysimpletweets.net.TwitterClient;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.util.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.util.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class TimelineActivity extends AppCompatActivity {
    // We need this key for response, to refresh home timeline
    public static final int TWEET_COMPOSE_ID = 10;
    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        client = TwitterApplication.getRestClient();
        // Find the listview
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(this, tweets);
        lvTweets = (ListView) findViewById(R.id.lvTweets);

        // Connect adapter to list view
        lvTweets.setAdapter(aTweets);

        populateTimeline(0);
        // Swipe Container for refresh
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        // Endless Scroll Listener
        setupEndlessScrollListener();

        // Swipe Refresh Listener
        setupSwipeRefreshListener();

    }

    // Setup Endless Scroll for List View
    public void setupEndlessScrollListener() {
        // Endless scrolling
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            // Triggered only when new data needs to be appended to the list
            // Add whatever code is needed to append new items to your AdapterView
            @Override
            public void onLoadMore(int totalItemCount) {
                populateTimeline(getNextMaxId(totalItemCount));
            }
        });
    }

    // Setup on Swipe Refresh Listener
    public void setupSwipeRefreshListener() {
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Clear current list and populate latest tweets,
                populateTimeline(0);
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);

            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    // Return 0 if list is empty, otherwise the smallest id - 1 in the list
    public long getNextMaxId(int totalItemCount) {
        if (tweets.isEmpty()) {
            return 0;
        } else {
            return tweets.get(totalItemCount-1).getUid() - 1;
        }
    }

    private void populateTimeline(long page) {
        // First load or re-load
        if (page == 0) {
            // Remove everything from the list
            aTweets.clear();
        }
        // Get the tweets
        client.getHomeTimeline(page, new JsonHttpResponseHandler() {

            //SUCCESS
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                // Deserialize JSON and add them to adapter
                aTweets.addAll(Tweet.fromJSONArray(json, false));
            }

            //FAILURE
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  JSONObject jsonObject) {
                Log.d("DEBUG", "Failed to fetch data : " + statusCode + " : " + jsonObject.toString(), throwable);
                Toast.makeText(TimelineActivity.this, "Failed to fetch data : " + statusCode + " : " + jsonObject.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserException(Throwable error) {
                Log.d("DEBUG", "User exception : " + error.getMessage(), error);
                Toast.makeText(TimelineActivity.this, "User exception : " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    // Compose menu item (see onClick)
    public void onCompose(MenuItem mi) {
        if (Utils.isNetworkAvailable(this)) {
            // Launch the compose activity
            Intent i = new Intent(this, ComposeActivity.class);
            startActivityForResult(i, TWEET_COMPOSE_ID);
        } else {
            // Show NO INTERNET message
            Utils.showNoInternet(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((resultCode == RESULT_OK) && (requestCode == TWEET_COMPOSE_ID)) {
            populateTimeline(0);
        }
    }


}
