package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.util.Utils;
import com.makeramen.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mkrish4
 */
// Taking the Tweet object and turning them into Views displayed in the list
public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, 0, tweets);
    }
    ViewHolder viewHolder;

    // Override and setup
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the tweet
        Tweet tweet = getItem(position);
        // Find or inflate the template
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);

            viewHolder = new ViewHolder();

            // Find subviews to fill with data in the template
            viewHolder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvCreatedAt = (TextView) convertView.findViewById(R.id.tvCreatedAt);
            viewHolder.tvRetweets = (TextView) convertView.findViewById(R.id.tvRetweets);
            viewHolder.tvFavorites = (TextView) convertView.findViewById(R.id.tvFavorites);
            viewHolder.ivDisplayImage = (ImageView) convertView.findViewById(R.id.ivDisplayImage);

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate data into the subviews
        Spannable str = new SpannableString(tweet.getUser().getName() + "\n@" + tweet.getUser().getScreenName());
        str.setSpan(new StyleSpan(Typeface.BOLD), 0, tweet.getUser().getName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewHolder.tvUserName.setText(str, TextView.BufferType.SPANNABLE);

        Spannable strBody = new SpannableString(tweet.getBody());
        Matcher matcherP = Pattern.compile("#([A-Za-z0-9_-]+)").matcher(strBody);
        while(matcherP.find()) strBody.setSpan(new ForegroundColorSpan(Color.rgb(0x5E, 0xB0, 0xED)),matcherP.start(), matcherP.end(), 0);
        Matcher matcherA = Pattern.compile("@([A-Za-z0-9_-]+)").matcher(strBody);
        while(matcherA.find()) strBody.setSpan(new ForegroundColorSpan(Color.rgb(0x5E, 0xB0, 0xED)),matcherA.start(), matcherA.end(), 0);
        viewHolder.tvBody.setText(strBody);
        viewHolder.tvCreatedAt.setText(Utils.getRelativeTimeAgo(tweet.getCreatedAt()));
        viewHolder.tvRetweets.setText(String.valueOf(tweet.getRetweetCount()));
        viewHolder.tvFavorites.setText(String.valueOf(tweet.getFavouritesCount()));

        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.rgb(0xDC, 0xDC, 0xDC))
                .borderWidthDp(3)
                .cornerRadiusDp(5)
                .oval(false)
                .build();

        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).fit().transform(transformation).into(viewHolder.ivProfileImage);
        viewHolder.ivDisplayImage.setImageResource(0);
        viewHolder.ivDisplayImage.setImageDrawable(null);
        viewHolder.ivDisplayImage.setMinimumHeight(0);
        viewHolder.ivDisplayImage.setMinimumWidth(0);
        if (tweet.getImage() != null) {
            viewHolder.ivDisplayImage.setMinimumHeight(tweet.getImage().get(0).getHeight());
            viewHolder.ivDisplayImage.setMinimumWidth(tweet.getImage().get(0).getWidth());
            //Toast.makeText(getBaseContext(), tweet.getImage().get(0).getUrl(), Toast.LENGTH_SHORT);
            Picasso.with(getContext()).load(tweet.getImage().get(0).getUrl()).into(viewHolder.ivDisplayImage);
        }
        // Return the view to be inserted into the list
        return convertView;
    }

    private static class ViewHolder {
        ImageView ivProfileImage;
        TextView tvUserName;
        TextView tvBody;
        TextView tvCreatedAt;
        TextView tvRetweets;
        TextView tvFavorites;
        ImageView ivDisplayImage;
    }
}
