package com.codepath.apps.mysimpletweets.net;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "XabcpGT4hHVWQgnUO3OruPYmf";       // Change this
	public static final String REST_CONSUMER_SECRET = "Id1toqe5oLn1CVVBKQCCLuuPREl8zXu4hA3gCHmE4kF6RfOQ89"; // Change this
	public static final String REST_CALLBACK_URL = "oauth://codepathtweets"; // Change this (here and in manifest)

	public static final int COUNT = 20;

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
	// HomeTimeline - Gets the user's home timeline data
	public void getHomeTimeline(long page, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", COUNT);
		// If not first request
		if (page > 0)
			params.put("max_id", page);
		// Execute the request
		getClient().get(apiUrl, params, handler);
	}

	// MentionsTimeline - Gets the user's mentions timeline data
	public void getMentionsTimeline(long page, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		RequestParams params = new RequestParams();
		params.put("count", COUNT);
		// If not first request
		if (page > 0)
			params.put("max_id", page);
		// Execute the request
		getClient().get(apiUrl, params, handler);
	}

	// Post Tweet - Post a message to user's home timeline
	public void postTweet(String message, long uid, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", message);
		if (uid != 0)
			params.put("in_reply_to_status_id", uid);
		getClient().post(apiUrl, params, handler);
	}

	// Show Tweet - Gets a tweet with additional details
	public void showTweet( long id, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/show.json");
		RequestParams params = new RequestParams();
		params.put("id", id);
		getClient().get(apiUrl, params, handler);
	}

	// User Timeline - Gets the user's posts' timeline
	public void getUserTimeline(String screenName, long page, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		RequestParams params = new RequestParams();
		params.put("screen_name", screenName);
		// If not first page
		if (page > 0)
			params.put("max_id", page);
		params.put("count", COUNT);
		// Execute the request
		getClient().get(apiUrl, params, handler);
	}

	// User Info - Gets the user's detailed information
	// !!!This works only for the signed user
	public void getUserCredentialInfo(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("account/verify_credentials.json");
		getClient().get(apiUrl, null, handler);
	}

	// User Info - Get detailed info of a user
	public void getUserInfo(String screenName, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("users/show.json");
		RequestParams params = new RequestParams();
		params.put("screen_name", screenName);
		getClient().get(apiUrl, params, handler);
	}
}