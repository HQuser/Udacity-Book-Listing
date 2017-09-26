package tn.pk.abdurrehman.booklisting;

import java.util.List;

/**
 * Created by Abdur on 26-Sep-17.
 */

public class Book {
    private String mTitle;
    private List<String> mAuthorList;
    private String mUrl;

    public Book(String mTitle, List<String> mAuthor, String mUrl) {
        this.mTitle = mTitle;
        this.mAuthorList = mAuthor;
        this.mUrl = mUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public List<String> getAuthorList() {
        return mAuthorList;
    }

    public String getUrl() {
        return mUrl;
    }
}
