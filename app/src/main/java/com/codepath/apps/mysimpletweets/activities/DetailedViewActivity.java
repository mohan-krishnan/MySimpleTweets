package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.app.TwitterApplication;
import com.codepath.apps.mysimpletweets.fragments.ReplyDialogFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.net.TwitterClient;
import com.codepath.apps.mysimpletweets.util.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetailedViewActivity extends AppCompatActivity {

    long uid;

    // Need to access user profiles
    String screen_name;

    ImageView ivProfileImage;
    TextView tvUser;
    TextView tvBody;
    ImageView ivDisplayImage;
    TextView tvDateTime;
    TextView tvFollowersAndFavourites;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);

        uid = (long) getIntent().getLongExtra("postId", 0);

        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvUser = (TextView) findViewById(R.id.tvUser);
        tvBody = (TextView) findViewById(R.id.tvBody);
        ivDisplayImage = (ImageView) findViewById(R.id.ivDisplayImage);
        tvDateTime = (TextView) findViewById(R.id.tvDateTime);
        tvFollowersAndFavourites = (TextView) findViewById(R.id.tvFollowersAndFavourites);

        client = TwitterApplication.getRestClient();
        populateDetailedView();
    }

    public void populateDetailedView() {
        if (Utils.isNetworkAvailable(getBaseContext())) {
            client.showTweet(uid, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Tweet tweet = Tweet.fromJSON(response);

                    screen_name = tweet.getUser().getScreenName();

                    // Top TextView - User Name and Screen Name
                    Spannable strName = new SpannableString(
                            tweet.getUser().getName() +
                                    "\n@" +
                                    tweet.getUser().getScreenName());

                    strName.setSpan(new StyleSpan(Typeface.BOLD), 0, tweet.getUser().getName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvUser.setText(strName, TextView.BufferType.SPANNABLE);

                    // Bottom TextView - Re-tweets and Favourites
                    String strRetweetCount = String.valueOf(tweet.getRetweetCount());
                    String strFavouritesCount = String.valueOf(tweet.getFavouritesCount());
                    Spannable strRetweetAndFavourites = new SpannableString(
                            strRetweetCount + " RETWEETS    " +
                                    strFavouritesCount + " FAVOURITES");
                    strRetweetAndFavourites.setSpan(new StyleSpan(Typeface.BOLD), 0, strRetweetCount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    strRetweetAndFavourites.setSpan(new StyleSpan(Typeface.BOLD),
                            (strRetweetCount + " RETWEETS    ").length(),
                            (strRetweetCount + " RETWEETS    " + strFavouritesCount).length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvFollowersAndFavourites.setText(strRetweetAndFavourites, TextView.BufferType.SPANNABLE);

                    Spannable strBody = new SpannableString(tweet.getBody());
                    Matcher matcherP = Pattern.compile("#([A-Za-z0-9_-]+)").matcher(strBody);
                    while (matcherP.find())
                        strBody.setSpan(new ForegroundColorSpan(Color.rgb(0x5E, 0xB0, 0xED)), matcherP.start(), matcherP.end(), 0);
                    Matcher matcherA = Pattern.compile("@([A-Za-z0-9_-]+)").matcher(strBody);
                    while (matcherA.find())
                        strBody.setSpan(new ForegroundColorSpan(Color.rgb(0x5E, 0xB0, 0xED)), matcherA.start(), matcherA.end(), 0);
                    tvBody.setText(strBody);

                    tvDateTime.setText(Utils.getRelativeTimeAgo(tweet.getCreatedAt()));

                    ivProfileImage.setImageResource(android.R.color.transparent);
                    Transformation transformation = new RoundedTransformationBuilder()
                            .borderColor(Color.rgb(0xDC, 0xDC, 0xDC))
                            .borderWidthDp(3)
                            .cornerRadiusDp(5)
                            .oval(false)
                            .build();
                    Picasso.with(getBaseContext()).load(tweet.getUser().getProfileImageUrl()).
                            fit().transform(transformation).into(ivProfileImage);

                    if (tweet.getImage() != null) {
                        ivDisplayImage.setMinimumHeight(tweet.getImage().get(0).getHeight());
                        ivDisplayImage.setMinimumWidth(tweet.getImage().get(0).getWidth());
                        //Toast.makeText(getBaseContext(), tweet.getImage().get(0).getUrl(), Toast.LENGTH_SHORT);
                        Picasso.with(getBaseContext()).load(tweet.getImage().get(0).getUrl()).into(ivDisplayImage);
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                }
            });
        } else {
            // Show NO INTERNET message
            Utils.showNoInternet(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detailed_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reply) {
            showReplyDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showReplyDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ReplyDialogFragment replyDialog = ReplyDialogFragment.newInstance("Send Reply", uid, screen_name);
        replyDialog.show(fm, "fragment_reply_dialog");
    }

    // Start Profile Activity when clicked on the profile image
    public void onProfileImageClick(View view) {
        if (Utils.isNetworkAvailable(this)) {
            Intent i = new Intent(this, ProfileActivity.class);
            i.putExtra("screen_name", screen_name);
            startActivity(i);
        }
        else {
            // Show NO INTERNET message
            Utils.showNoInternet(this);
        }
    }
}
