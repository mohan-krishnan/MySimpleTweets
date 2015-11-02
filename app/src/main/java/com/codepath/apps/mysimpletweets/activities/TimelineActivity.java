package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.activeandroid.ActiveAndroid;
import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.MentionsTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.TweetsListFragment;
import com.codepath.apps.mysimpletweets.util.Utils;


public class TimelineActivity extends AppCompatActivity implements TweetsListFragment.DetailedViewRequestListener, TweetsListFragment.ProgressBarListener {

    TweetsPagerAdapter tweetsPagerAdapter;
    ViewPager vpPager;

    HomeTimelineFragment homeTimelineFragment;
    MentionsTimelineFragment mentionsTimelineFragment;

    // We need this key for response, to refresh home timeline
    public static final int TWEET_COMPOSE_ID = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        ActiveAndroid.initialize(this);

        /* Did not work out
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.rgb(94,176,237)));
        bar.setDisplayShowTitleEnabled(false);
        bar.setDisplayShowTitleEnabled(true); */

        homeTimelineFragment = new HomeTimelineFragment();
        mentionsTimelineFragment = new MentionsTimelineFragment();

        // Get the viewpager
        vpPager = (ViewPager) findViewById(R.id.viewpager);

        // Set the viewpager adapter to the pager
        tweetsPagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(tweetsPagerAdapter);


        // Find the pager sliding tabstrip
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        // Attach the tabscript to the viewpager
        tabStrip.setViewPager(vpPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((resultCode == RESULT_OK) && (requestCode == TWEET_COMPOSE_ID)) {
            homeTimelineFragment.reloadTimeline();
            mentionsTimelineFragment.reloadTimeline();
        }
    }

    // return the order of fragments in viewpager
    public class TweetsPagerAdapter extends FragmentPagerAdapter {

        final int PAGE_COUNT = 2;
        private String tabTitles[] = {"Home", "Mentions"};

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        // The order and creation of fragments within the pager
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return homeTimelineFragment;
            } else if (position == 1) {
                return mentionsTimelineFragment;
            }
            else {
                return null;
            }
        }

        // Return the tab title
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        // How many fragments are to swipe between?
        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }

    // Profile View menu item (see onClick)
    public void onProfileView(MenuItem mi) {
        if (Utils.isNetworkAvailable(this)) {
            // Launch the profile view
            Intent i = new Intent(this, ProfileActivity.class);
            i.putExtra("screen_name", "");
            startActivity(i);
        } else {
            // Show NO INTERNET message
            Utils.showNoInternet(this);
        }
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

    // This request is made by the fragment who displays the list
    // and it is called when an item is clicked on.
    public void onDetailedViewRequest(long id) {
        if (Utils.isNetworkAvailable(this)) {
            Intent i = new Intent(this, DetailedViewActivity.class);
            i.putExtra("postId", id);
            startActivity(i);
        } else {
            // Show NO INTERNET message
            Utils.showNoInternet(this);
        }
    }

    public void showProgressBar() {
        setProgressBarIndeterminateVisibility(true);
    }

    public void hideProgressBar() {
        setProgressBarIndeterminateVisibility(false);
    }
}
