package com.example.incred_dev.booksearch;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by incred-dev on 22/5/18.
 */

public class FetchBook extends AsyncTask<String, Void, List<BookModel>> {


    @Override
    protected List<BookModel> doInBackground(String... strings) {
        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;
        String bookJSONString = null;

        try {

            URL url = new URL(strings[0]);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            StringBuffer buffer = new StringBuffer();

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line ;

            while ((line= bufferedReader.readLine())!=null) {
                buffer.append(line + "\n");
            }
            bookJSONString = buffer.toString();
            Log.d("result bookJSONString", bookJSONString);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return getBookList(bookJSONString);
    }

    private List<BookModel> getBookList(String bookJSONString) {
        List<BookModel> modelList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(bookJSONString);

            JSONArray itemArray = jsonObject.getJSONArray("items");
            int i = 0;
            String title = null;
            String auther = null;
            String publishDate = null;
            String image = null;



            while (i < itemArray.length()) {
                final BookModel model = new BookModel();

                JSONObject book = itemArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                JSONObject salesInfo = book.getJSONObject("saleInfo");

                JSONObject imageInfo = volumeInfo.getJSONObject("imageLinks");
                image = imageInfo.getString("smallThumbnail");

                if (salesInfo.getString("saleability").equals("FOR_SALE")){
                    JSONObject retailPrice = salesInfo.getJSONObject("retailPrice");

                    final Double price = retailPrice.getDouble("amount");

                    new CurrencyConverter(){
                        @Override
                        protected void onPostExecute(Double aDouble) {
                            if (aDouble != null && !aDouble.equals("")){
                                model.setAmount(price/aDouble);
                                Log.d("amount",""+(price/aDouble));
                            }
                        }
                    }.execute(NetworkUtils.CURRENCY_URL);

                }
                title = volumeInfo.getString("title");
                auther = volumeInfo.getString("authors");
                publishDate = volumeInfo.getString("publishedDate");
                model.setImage(image);
                model.setTitle(title);
                model.setAuthor(auther);
                model.setPublishDate(publishDate);
                modelList.add(model);

                i++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return modelList;
    }

    private class CurrencyConverter extends AsyncTask<String, Void, Double>{
        HttpURLConnection connection;
        String result = null;
        @Override
        protected Double doInBackground(String... strings) {
            try {

                URL url = new URL(strings[0]);

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                 while ((line=reader.readLine()) != null){
                     buffer.append(line+"\n");
                 }
                 result = buffer.toString();

                 Log.d("result",result);

            } catch (Exception ex){
                ex.printStackTrace();
            }
            return getValue(result);
        }

        public Double getValue(String result){
            Double inrAmount = null;
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject  rates = jsonObject.getJSONObject("rates");
                inrAmount = rates.getDouble("INR");

            } catch (Exception ex){
                ex.printStackTrace();
            }
            return inrAmount;
        }

    }

}
