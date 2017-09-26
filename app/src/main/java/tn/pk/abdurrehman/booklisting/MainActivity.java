package tn.pk.abdurrehman.booklisting;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {
    private static final String TAG = "MainActivity";
    private static final int BOOKS_LOADER_ID = 1;
    private static String keyword = "";
    private ProgressBar progressBar;
    private BooksAdapter mBooksAdapter;
    private TextView mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing the required View
        ListView booksListView = (ListView) findViewById(R.id.result_list_view);
        mEmptyView = (TextView) findViewById(R.id.empty_view);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mBooksAdapter = new BooksAdapter(this, new ArrayList<Book>());

        // Setting List View properties
        booksListView.setAdapter(mBooksAdapter);
        booksListView.setEmptyView(findViewById(R.id.empty_view));
        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book currentBook = mBooksAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri bookUri = Uri.parse(currentBook.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Checking internet connection
        // Get a reference to the ConnectivityManager to check state of network connectivity
        final ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is no internet connection, simply return
        if (!QueryUtils.hasInternetConnection(this)) {
            mEmptyView.setText(R.string.no_internet);
            return;
        }

        if (keyword.isEmpty()) {
            mEmptyView.setText(R.string.no_book_name);
        }

        final Button searchButton = (Button) findViewById(R.id.query_search_button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide soft keyboard
                QueryUtils.hideSoftKeyboard(MainActivity.this);

                // Again check there is no internet connection, if so simply return
                if (!QueryUtils.hasInternetConnection(getApplicationContext())) {
                    mEmptyView.setText(R.string.no_internet);
                    return;
                }

                EditText editText = (EditText) findViewById(R.id.query_edit_text);
                String keywordEntered = editText.getText().toString();

                if (keywordEntered == null || keywordEntered.isEmpty()) {

                    Toast.makeText(MainActivity.this, R.string.no_book_name, Toast.LENGTH_SHORT).show();

                } else if (!keywordEntered.equals(keyword)) {
                    // Clear previous records
                    mBooksAdapter.clear();

                    // Clear Empty View
                    mEmptyView.setText("");

                    // Display progress bar
                    progressBar.setVisibility(View.VISIBLE);
                    // Update keyboard global variable
                    keyword = keywordEntered;
                    getLoaderManager().restartLoader(BOOKS_LOADER_ID, null, MainActivity.this);
                }
            }
        });

        getLoaderManager().initLoader(BOOKS_LOADER_ID, null, MainActivity.this);

    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        return new BooksLoader(this, keyword);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        progressBar.setVisibility(View.GONE);
        mBooksAdapter.clear();

        if (books != null && books.size() > 0) {
            mBooksAdapter.addAll(books);
        } else if (!keyword.isEmpty()) {
            mEmptyView.setText(R.string.no_result);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mBooksAdapter.clear();
        Log.d(TAG, "onLoaderReset: IN RESET");
    }
}
