package com.example.android.booklistingapp;

import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Helper methods related to requesting and receiving book data from Google Books API.
 */
public class QueryUtils {
    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static final String KEY_ITEMS = "items";
    private static final String KEY_VOLUMEINFO = "volumeInfo";
    private static final String KEY_AUTHORS = "authors";
    private static final String KEY_TITLE = "title";

    public static String errorMessage = null;

    /**
     * Query the Google Books API and return an {@link ArrayList<Book>} object to represent a single book.
     */
    public static ArrayList<Book> fetchBookData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;

        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        ArrayList<Book> book = extractFeatureFromJson(jsonResponse);

        // Return the {@link Event}
        return book;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return an {@link ArrayList<Book>} object by parsing out information
     * about the first book from the input bookJSON string.
     */
    private static ArrayList<Book> extractFeatureFromJson(String bookJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        ArrayList<Book> books = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of Book objects with the corresponding data.
            JSONObject jsonObj = new JSONObject(bookJSON);
            // Getting JSON Array node
            JSONArray booksJSON = jsonObj.getJSONArray(KEY_ITEMS);
            // looping through all books
            for (int i = 0; i < booksJSON.length(); i++) {
                JSONObject c = booksJSON.getJSONObject(i);
                JSONObject volumeInfo = c.getJSONObject(KEY_VOLUMEINFO);
                String title = volumeInfo.getString(KEY_TITLE);
                String author = "";
                if (volumeInfo.has(KEY_AUTHORS)) {
                    JSONArray authorsJSON = volumeInfo.getJSONArray(KEY_AUTHORS);
                    for (int j = 0; j < authorsJSON.length(); j++) {
                        if ((j + 1) == authorsJSON.length()) {
                            author = author + authorsJSON.getString(j);
                        } else {
                            author = author + authorsJSON.getString(j) + ", ";
                        }
                    }
                } else {
                    author = "No authors";
                }
                Book oneBook = new Book(author, title);
                books.add(oneBook);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
            errorMessage = String.valueOf(e);
        }
        // Return the list of books
        return books;
    }
}
