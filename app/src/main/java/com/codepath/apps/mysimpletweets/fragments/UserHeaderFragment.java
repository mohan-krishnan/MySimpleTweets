package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.simpletwitterclient.R;
import com.codepath.apps.simpletwitterclient.TwitterApplication;
import com.codepath.apps.simpletwitterclient.TwitterClient;
import com.codepath.apps.simpletwitterclient.Utility;
import com.codepath.apps.simpletwitterclient.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by vincetulit on 2/25/15.
 */
public class UserHeaderFragment extends Fragment {
    ImageView ivProfileImage;
    TextView tvName;
    TextView tvScreenName;
    TextView tvTagline;
    TextView tvFollowersCount;
    TextView tvFollowingCount;
    TextView tvTweetsCount;

    private TwitterClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the client
        client = TwitterApplication.getRestClient();

        populateUserHeader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_header, container, false);

        ivProfileImage = (ImageView) v.findViewById(R.id.ivProfileImage);
        tvName = (TextView) v.findViewById(R.id.tvName);
        tvScreenName = (TextView) v.findViewById(R.id.tvScreenName);
        tvTagline = (TextView) v.findViewById(R.id.tvTagline);
        tvFollowersCount = (TextView) v.findViewById(R.id.tvFollowersCount);
        tvFollowingCount = (TextView) v.findViewById(R.id.tvFollowingCount);
        tvTweetsCount = (TextView) v.findViewById(R.id.tvTweetsCount);

        return v;
    }


    public static UserHeaderFragment newInstance(String screenName) {
        UserHeaderFragment userHeaderFragment = new UserHeaderFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        userHeaderFragment.setArguments(args);
        return userHeaderFragment;
    }

    // Send and API request to get the user information json
    // Fill the fragment view
    private void populateUserHeader() {

        String screenName = getArguments().getString("screen_name");

        if (Utility.isNetworkAvailable(getActivity())) {

            if (!screenName.isEmpty()) {
                // Get user info if screen_name was provided
                client.getUserInfo(screenName, new JsonHttpResponseHandler() {
                    //SUCCESS
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                        User user = User.fromJSON(json);

                        Picasso.with(getActivity().getBaseContext()).
                                load(user.getProfileImageUrl()).into(ivProfileImage);

                        tvName.setText(user.getName());
                        tvScreenName.setText("@" + user.getScreenName());
                        tvTagline.setText(user.getTagLine());
                        tvFollowersCount.setText(String.valueOf(user.getFollowersCount()) + " FOLLOWERS");
                        tvFollowingCount.setText(String.valueOf(user.getFollowingCount()) + " FOLLOWING");
                        tvTweetsCount.setText(String.valueOf(user.getTweetsCount()) + " TWEETS");

                    }

                    //FAILURE
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    }

                });
            } else {
                // Get user credential info if screen_name was NOT provided
                client.getUserCredentialInfo( new JsonHttpResponseHandler() {
                    //SUCCESS
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                        User user = User.fromJSON(json);

                        Picasso.with(getActivity().getBaseContext()).
                                load(user.getProfileImageUrl()).into(ivProfileImage);

                        tvName.setText(user.getName());
                        tvScreenName.setText("@" + user.getScreenName());
                        tvTagline.setText(user.getTagLine());
                        tvFollowersCount.setText(String.valueOf(user.getFollowersCount()) + " FOLLOWERS");
                        tvFollowingCount.setText(String.valueOf(user.getFollowingCount()) + " FOLLOWING");
                        tvTweetsCount.setText(String.valueOf(user.getTweetsCount()) + " TWEETS");

                    }

                    //FAILURE
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    }

                });
            }

        } else {
            // Show NO INTERNET message
            Utility.showNoInternet(getActivity());
        }
    }
}
