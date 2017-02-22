package com.example.nopesal.projectmoviesudacity.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.nopesal.projectmoviesudacity.R;
import com.example.nopesal.projectmoviesudacity.utils.Review;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by nopesal on 19/02/2017.
 */

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ViewHolder> {
    public Context mContext;
    public ArrayList<Review> mReviewArrayList;

    public ReviewListAdapter(Context context, ArrayList<Review> reviewArrayList) {
        mContext = context;
        mReviewArrayList = reviewArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.review_list_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        String reviewContent = mReviewArrayList.get(position).content;
        try {
            viewHolder.mFirstLineTextView.setText(reviewContent.substring(0, reviewContent.indexOf(".")));
            viewHolder.mReviewTextView.setText(reviewContent.substring(reviewContent.indexOf(".") + 2, reviewContent.length()));
        } catch (IndexOutOfBoundsException e) {
            viewHolder.mFirstLineTextView.setText(reviewContent);
            viewHolder.mReviewTextView.setVisibility(View.GONE);
        }
        viewHolder.mAuthorTextView.setText(mReviewArrayList.get(position).author);

        Typeface nunitoBold = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Nunito-Bold.ttf");
        Typeface nunitoRegular = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/Nunito-Regular.ttf");

        viewHolder.mFirstLineTextView.setTypeface(nunitoBold);
        viewHolder.mReviewTextView.setTypeface(nunitoRegular);
        viewHolder.mAuthorTextView.setTypeface(nunitoBold);
        viewHolder.mAuthorBy.setTypeface(nunitoRegular);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mReviewArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.review_item_first_line_text_view) TextView mFirstLineTextView;
        @BindView(R.id.review_item_text_view) TextView mReviewTextView;
        @BindView(R.id.review_item_author) TextView mAuthorTextView;
        @BindView(R.id.review_item_author_by_text_view) TextView mAuthorBy;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
