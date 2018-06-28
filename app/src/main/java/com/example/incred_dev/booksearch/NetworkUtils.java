package com.example.incred_dev.booksearch;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by incred-dev on 22/5/18.
 */

public class NetworkUtils {

    public static final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=python";

    public static final String CURRENCY_URL = "http://data.fixer.io/api/latest?access_key=45c81bff16907b1ad7a0cbbc48b33ba6";

}
