package com.example.android.booklistingapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Loads a list of books by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class BookLoader extends AsyncTaskLoader<List<Book>> {

    private String mSearchQuote;

    /**
     * Constructs a new {@linkBookLoader}.
     *
     * @param context     of the activity
     * @param searchQuote to load data from
     */
    public BookLoader(Context context, String searchQuote) {
        super(context);
        mSearchQuote = searchQuote;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Book> loadInBackground() {
        if (mSearchQuote == null) {
            return null;
        }

        mSearchQuote = mSearchQuote.replace(" ", "+");

        /** URL for book data from the Google Book dataset */
        String BOOK_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=" + mSearchQuote + "&maxResults=30";
        // Perform the network request, parse the response, and extract a list of books.
        List<Book> books = QueryUtils.fetchBookData(BOOK_REQUEST_URL);
        return books;
    }
}
