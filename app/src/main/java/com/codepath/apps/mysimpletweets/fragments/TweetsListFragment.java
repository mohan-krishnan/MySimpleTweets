package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.util.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mkrish4
 */
public abstract class TweetsListFragment extends Fragment {

    private SwipeRefreshLayout swipeContainer;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;


    // Inflation logic
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);

        // Find the listview
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);

        // Connect adapter to list view
        lvTweets.setAdapter(aTweets);

        // Swipe Container for refresh
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);

        // Endless Scroll Listener
        setupEndlessScrollListener();

        // On Click Listener
        setupOnClickListener();

        // Swipe Refresh Listener
        setupSwipeRefreshListener();

        return v;
    }

    // Creation lifecycle event
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create the arraylist (datasource)
        tweets = new ArrayList<>();
        // Construct the adapter from the source
        aTweets = new TweetsArrayAdapter(getActivity(),tweets);
    }

    public void addAll(List<Tweet> tweets) {
        aTweets.addAll(tweets);
    }

    public void removeAll() { aTweets.clear(); }

    public void timelineUpdateFinished() {
        if (swipeContainer != null)
        swipeContainer.setRefreshing(false); }


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

    // Setup On Click Listener for List View
    public void setupOnClickListener() {
        lvTweets.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Utils.isNetworkAvailable(getActivity())) {
                    // Request Detailed View on selected item from the calling activity
                    DetailedViewRequestListener listener = (DetailedViewRequestListener) getActivity();
                    listener.onDetailedViewRequest(aTweets.getItem(position).getUid());
                } else {
                    // Show NO INTERNET message
                    Utils.showNoInternet(getActivity());
                }
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

    // Interface to calling activity to request Detailed View on selected item
    public interface DetailedViewRequestListener {
        public void onDetailedViewRequest(long id);
    }

    public interface ProgressBarListener {
        public void showProgressBar();
        public void hideProgressBar();
    }

    // It is implemented by each child fragment,
    // takes care of network request and populating the listview
    public abstract void populateTimeline(long page);

}
