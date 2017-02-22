package com.example.nopesal.projectmoviesudacity.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nopesal on 19/02/2017.
 */

public class Review implements Parcelable {
    public String content;
    public String author;

    public Review(String content, String author) {
        this.content = content;
        this.author = author;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeString(this.author);
    }

    protected Review(Parcel in) {
        this.content = in.readString();
        this.author = in.readString();
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
