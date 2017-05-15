package com.example.android.booklistingapp;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {

    // Author
    private String mAuthor;

    // Title
    private String mTitle;


    public Book(String author, String title) {

        mAuthor = author;
        mTitle = title;
    }

    protected Book(Parcel in) {
        mAuthor = in.readString();
        mTitle = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public String getAuthor() {
        return mAuthor;
    }

    public String getTitle() {
        return mTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAuthor);
        dest.writeString(mTitle);
    }
}
