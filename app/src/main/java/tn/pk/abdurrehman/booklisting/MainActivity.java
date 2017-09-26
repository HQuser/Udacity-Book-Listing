package tn.pk.abdurrehman.booklisting;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {
    private static final String TAG = "MainActivity";
    private static final int BOOKS_LOADER_ID = 1;
    private static String query;

    private BooksAdapter mBooksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView booksListView = (ListView) findViewById(R.id.result_list_view);
        mBooksAdapter = new BooksAdapter(this, new ArrayList<Book>());

        booksListView.setAdapter(mBooksAdapter);

        Button searchButton = (Button) findViewById(R.id.query_search_button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.query_edit_text);
                String keyword = editText.getText().toString();

                if (keyword == null || keyword.isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.no_book_name, Toast.LENGTH_SHORT).show();
                } else {
                    query = QueryUtils.buildQuery(keyword);
                    getLoaderManager().initLoader(BOOKS_LOADER_ID, null, MainActivity.this);
                }

            }
        });

    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        return new BooksLoader(this, query);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        mBooksAdapter.clear();

        if (books != null) {
            mBooksAdapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mBooksAdapter.clear();
    }
}
