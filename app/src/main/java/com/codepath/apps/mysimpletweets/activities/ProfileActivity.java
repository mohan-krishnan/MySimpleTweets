package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.simpletwitterclient.fragments.TweetsListFragment;
import com.codepath.apps.simpletwitterclient.fragments.UserHeaderFragment;
import com.codepath.apps.simpletwitterclient.fragments.UserTimelineFragment;

public class ProfileActivity extends ActionBarActivity implements TweetsListFragment.DetailedViewRequestListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Get screen name from the activity that launches this
        String screenName = getIntent().getStringExtra("screen_name");

        if (savedInstanceState == null) {
            FragmentTransaction ft;

            // Load User Header fragment
            UserHeaderFragment fragmentUserHeader = UserHeaderFragment.newInstance(screenName);

            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flUserHeader, fragmentUserHeader);
            ft.commit();

            // Load User Timeline fragment
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(screenName);

            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fragmentUserTimeline);
            ft.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    // This request is made by the fragment who displays the list
    // and it is called when an item is clicked on.
    public void onDetailedViewRequest(long id) {
        if (Utility.isNetworkAvailable(this)) {
            Intent i = new Intent(this, DetailedViewActivity.class);
            i.putExtra("postId", id);
            startActivity(i);
        } else {
            // Show NO INTERNET message
            Utility.showNoInternet(this);
        }
    }
}
