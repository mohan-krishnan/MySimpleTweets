package com.codepath.apps.mysimpletweets.fragments;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.app.TwitterApplication;
import com.codepath.apps.mysimpletweets.net.TwitterClient;
import com.codepath.apps.mysimpletweets.util.Utils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;


public class ReplyDialogFragment extends DialogFragment {
    TwitterClient client;
    EditText etReplyMessage;
    long inReplyToStatusId;
    String screenName;


    public ReplyDialogFragment() {
        // Required empty public constructor
    }

    public static ReplyDialogFragment newInstance(String title, long inReplyToStatusId, String screenName) {
        ReplyDialogFragment frag = new ReplyDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putLong("in_reply_to_status_id", inReplyToStatusId);
        args.putString("screen_name", screenName);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reply_dialog, container, false);

        etReplyMessage = (EditText) view.findViewById(R.id.etReplyMessage);

        screenName = getArguments().getString("screen_name");
        inReplyToStatusId = getArguments().getLong("in_reply_to_status_id");
        String title = getArguments().getString("title");
        getDialog().setTitle(title);

        etReplyMessage.setText("RT @"+screenName);
        etReplyMessage.setSelection(etReplyMessage.length());
        etReplyMessage.requestFocus();

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        client = TwitterApplication.getRestClient();

        setupSaveButtonListener(view);

        return view;
    }

    public void setupSaveButtonListener(View view) {
        Button btnSave = (Button) view.findViewById(R.id.btnSubmitReply);
        btnSave.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Utils.isNetworkAvailable(getActivity())) {
                            client.postTweet(etReplyMessage.getText().toString(), inReplyToStatusId,
                                    new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    dismiss();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    super.onFailure(statusCode, headers, responseString, throwable);
                                }
                            });
                        } else {
                            // Show NO INTERNET message
                            Utils.showNoInternet(getActivity());
                        }
                    }
                }
        );
    }
}
