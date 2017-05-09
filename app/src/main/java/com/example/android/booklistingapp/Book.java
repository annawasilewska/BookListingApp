package com.example.android.booklistingapp;

public class Book {

    // Author
    private String mAuthor;

    // Title
    private String mTitle;


    public Book(String author, String title) {

        mAuthor = author;
        mTitle = title;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getTitle() {
        return mTitle;
    }
}
