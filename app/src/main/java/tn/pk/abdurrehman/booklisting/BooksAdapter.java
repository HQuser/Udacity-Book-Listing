package tn.pk.abdurrehman.booklisting;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Abdur on 26-Sep-17.
 */

public class BooksAdapter extends ArrayAdapter<Book> {
    /**
     * Constructor to initialize the custom array adapter
     *
     * @param context of the application
     * @param objects list containing books
     */
    private static Context mContext;

    public BooksAdapter(@NonNull Context context, @NonNull List<Book> objects) {
        super(context, 0, objects);
        mContext = context;
    }

    private static String listToString(List<String> booksList) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < booksList.size(); i++) {
            builder.append(booksList.get(i));
        }

        return mContext.getResources().getString(R.string.by) + builder.toString();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View rootView, @NonNull ViewGroup parent) {
        if (rootView == null) {
            rootView = LayoutInflater.from(getContext()).inflate(R.layout.books_list_item, parent, false);
        }

        Book currentBook = getItem(position);

        TextView titleTextView = (TextView) rootView.findViewById(R.id.book_title_text_view);
        titleTextView.setText(currentBook.getTitle());

        TextView authorsTextView = (TextView) rootView.findViewById(R.id.book_authors_text_view);
        authorsTextView.setText(listToString(currentBook.getAuthorList()));

        return rootView;
    }
}
