package com.example.android.booklistingapp;

        import android.app.LoaderManager;
        import android.content.Context;
        import android.content.Loader;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ListView;
        import android.widget.ProgressBar;
        import android.widget.TextView;

        import java.util.ArrayList;
        import java.util.List;

        import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final String STATE_BOOKSLIST = "StateOfBooksList";
    /**
     * Constant value for the book loader ID
     */
    private static final int BOOK_LOADER_ID = 1;
    private ArrayList<Book> mBookArrayList;
    private ListView mBookListView;
    /**
     * Adapter for the list of books
     */
    private BookAdapter mAdapter;
    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;
    /**
     * ProgressBar
     */
    private ProgressBar loadingIndicator;

    /* Save values */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(STATE_BOOKSLIST, mBookArrayList);
        super.onSaveInstanceState(savedInstanceState);
    }

    /* Restore saved values */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mAdapter.clear();
            mBookArrayList = savedInstanceState.getParcelableArrayList(STATE_BOOKSLIST);
            mAdapter.addAll(mBookArrayList);
            mBookListView.setAdapter(mAdapter);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingIndicator = (ProgressBar) findViewById(R.id.progress_bar);
        loadingIndicator.setVisibility(GONE);

        // Find a reference to the Search Button
        Button searchButton = (Button) findViewById(R.id.search_button);

        // Find a reference to the {@link ListView} in the layout
        mBookListView = (ListView) findViewById(R.id.list);

        // Find a reference to the 'empty view' textview
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        mBookListView.setEmptyView(mEmptyStateTextView);

        // Create a new ArrayList for books
        mBookArrayList = new ArrayList<>();

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(MainActivity.this, mBookArrayList);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        mBookListView.setAdapter(mAdapter);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(BOOK_LOADER_ID, null, MainActivity.this);

        //Set OnItemListener on Search Button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide empty state TextView and show loading indicator
                mEmptyStateTextView.setVisibility(GONE);
                loadingIndicator.setVisibility(View.VISIBLE);

                // Get a reference to the ConnectivityManager to check state of network connectivity
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);

                // Get details on the currently active default data network
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                // If there is a network connection, fetch data
                if (networkInfo != null && networkInfo.isConnected()) {

                    if (getLoaderManager().getLoader(BOOK_LOADER_ID).isStarted()) {
                        loadingIndicator.setVisibility(View.VISIBLE);
                        //restart it if there's one
                        getLoaderManager().restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
                    }
                } else {
                    // Otherwise, display error
                    // First, hide loading indicator so error message will be visible
                    loadingIndicator.setVisibility(GONE);

                    // Update empty state with no connection error message
                    mEmptyStateTextView.setText(R.string.no_internet);
                }
            }
        });
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        // Find a search quote
        EditText searchQuoteEditText = (EditText) findViewById(R.id.search_quote);
        String searchQuote = searchQuoteEditText.getText().toString();

        // Create a new loader for the given URL
        return new BookLoader(this, searchQuote);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        // Hide loading indicator
        loadingIndicator.setVisibility(GONE);

        // Clear the adapter of previous book data
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            mBookArrayList = new ArrayList<>();
            mBookArrayList.addAll(data);
            mAdapter.addAll(data);
        }

        // Set empty state text to display "No books found."
        mEmptyStateTextView.setText(R.string.no_books);
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
}
