package tn.pk.abdurrehman.booklisting;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

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
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Abdur on 26-Sep-17.
 */

public class QueryUtils {

    ////////////////////////////////////////////////
    // This section deals with the getting the   //
    // data from the internet and sending back  //
    // the received data in the form of string //
    ////////////////////////////////////////////

    /**
     * This method takes in a URL and convert it into the URL Object
     *
     * @param urlString is the url passed
     * @return the URL object
     */
    public static URL createURL(String urlString) {
        if (urlString == null || urlString.isEmpty()) {
            return null;
        }

        URL url = null;

        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            Log.e(TAG, "createURL: Invalid URL passed", e);
        }
        return url;
    }

    /**
     * This method takes in a URL object and process it and return an InputStream object
     * from the data received from the URL
     *
     * @param url is the URL to fetch data from
     * @return InputStream from URL data
     */
    public static InputStream createHttpConnection(URL url) {
        if (url == null) {
            return null;
        }

        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                return urlConnection.getInputStream();
            } else {
                Log.e(TAG, "createHttpConnection: Fetch failed, response code"
                        + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(TAG, "createHttpConnection: " + e.getMessage(), e);
        }

        // Try block return failed, hence nothing to send
        return null;
    }

    /**
     * This method processes InputStream and uses BufferedReader to return the
     * the data in the stream in the form of String
     *
     * @param inputStream containing the data
     * @return String of data received from the InputStream
     * @throws IOException
     */
    public static String processInputStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        StringBuilder response = new StringBuilder();

        String line = bufferedReader.readLine();

        while (line != null) {
            response.append(line);
            line = bufferedReader.readLine();
        }

        return response.toString();
    }

    //////////////////////////////////////////////
    // This section deals with the getting the //
    // String JSON data and processing it to  //
    // extract the desired contents          //
    //////////////////////////////////////////

    public static List<Book> extractContentFromJSON(String json) {

        if (json == null || json.isEmpty()) {
            return null;
        }

        ArrayList<Book> books = new ArrayList<>();

        try {
            // Top level view of the JSON
            JSONObject root = new JSONObject(json);

            // Get the items array from the JSON
            JSONArray itemsArray = root.getJSONArray("items");

            for (int i = 0; i < itemsArray.length(); i++) {
                // Get the attributes inside the items array
                JSONObject itemAttributes = itemsArray.getJSONObject(i);


                // Get the volume info JSON inside the items array
                JSONObject volumeInfo = itemAttributes.getJSONObject("volumeInfo");

                // Extarct the infoLink
                String infoLink = volumeInfo.getString("infoLink");

                // Extract the title
                String title = volumeInfo.getString("title");

                // Get the JSON authors array
                JSONArray authorsArray = volumeInfo.getJSONArray("authors");

                // List of authors to add to the Book object instance
                List<String> authorsList = new ArrayList<>();

                for (int j = 0; j < authorsArray.length(); j++) {
                    // Add each author in the list of authors
                    authorsList.add(authorsArray.get(j).toString());
                }

                // Finally add a new Book instance containing the title and authors list
                books.add(new Book(title, authorsList, infoLink));
            }

        } catch (JSONException e) {
            Log.e(TAG, "extractContentFromJSON: Extraction from JSON failed", e);
        }

        // In case try block failed, simply send the empty books list
        return books;
    }

    public static String buildQuery(String argument) {
        return "https://www.googleapis.com/books/v1/volumes?q=" + argument;
    }

    public static boolean hasInternetConnection(Context context) {
        // Checking internet connection
        // Get a reference to the ConnectivityManager to check state of network connectivity
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is no internet connection, simply return
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
