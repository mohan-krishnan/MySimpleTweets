package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.app.TwitterApplication;
import com.codepath.apps.mysimpletweets.net.TwitterClient;
import com.codepath.apps.mysimpletweets.util.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class ComposeActivity extends AppCompatActivity {
    private TwitterClient client;
    private EditText etMessage;
    private TextView tvCharCounter;
    private final TextWatcher tweetTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            tvCharCounter.setText(String.valueOf(140 - etMessage.getText().toString().length()) + "/140");
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etMessage = (EditText) findViewById(R.id.etMessage);
        tvCharCounter = (TextView) findViewById(R.id.tvCharCounter);

        etMessage.addTextChangedListener(tweetTextWatcher);

        // Show soft keyboard automatically
        etMessage.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // Get the client
        client = TwitterApplication.getRestClient(); //singleton client

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_tweet) {

            if (Utils.isNetworkAvailable(getBaseContext())) {
                // Post a tweet
                client.postTweet(etMessage.getText().toString(), 0, new JsonHttpResponseHandler() {
                    //SUCCESS
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                        // Tiny info, so we wait in this activity until we get response (good or bad)
                        setResult(RESULT_OK, null);
                        // Take back user to the timeline only if message was submitted successfully
                        finish();
                    }

                    //FAILURE
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    }
                });
            } else {
                // Show NO INTERNET message
                Utils.showNoInternet(this);
            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
