package tn.pk.abdurrehman.booklisting;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Abdur on 26-Sep-17.
 */

public class BooksLoader extends AsyncTaskLoader<List<Book>> {

    private static String query;

    public BooksLoader(Context context, String query) {
        super(context);
        BooksLoader.query = query;
    }

    @Override
    public List<Book> loadInBackground() {
        URL url = QueryUtils.createURL(query);

        if (url == null) {
            return null;
        }

        InputStream inputStream = QueryUtils.createHttpConnection(url);

        if (inputStream == null) {
            return null;
        }

        try {
            String json = QueryUtils.processInputStream(inputStream);
            return QueryUtils.extractContentFromJSON(json);
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: ", e);
        }
        return null;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


}
