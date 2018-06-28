package com.example.incred_dev.booksearch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FetchBook fetchBook;
    RecyclerView rv;
    BookAdapter bookAdapter;
    List<BookModel> bookModelList = new ArrayList<>();
    List<String> list = new ArrayList<>();
    SQLiteDatabase sqLiteDatabase;
    MyHelper myHelper;
    String sqlIv, sqlName, sqlAuther, sqlDate;
    Double sqlPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myHelper = new MyHelper(this, "BOOKDB",null,1);
        sqLiteDatabase = myHelper.getWritableDatabase();


        ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ) {

            // notify user you are online
            fetchBook = new FetchBook(){
                @Override
                protected void onPostExecute(List<BookModel> list) {
                    if (list != null){
                        super.onPostExecute(list);
                        bookModelList.addAll(list);
                        bookAdapter.notifyDataSetChanged();

                        if (bookModelList != null){

                            ContentValues cv = new ContentValues();
                            for (int i= 0; i<list.size(); i++){
                                cv.put("bookUrl",bookModelList.get(i).getImage());
                                cv.put("bookName",bookModelList.get(i).getTitle());
                                cv.put("bookAuther",bookModelList.get(i).getAuthor());
                                cv.put("bookDate",bookModelList.get(i).getPublishDate());
//                                cv.put("bookPrice",bookModelList.get(i).getAmount());
                            }
                            sqLiteDatabase.insert("book",null,cv);
                            sqLiteDatabase.close();
                        }
                    }

                }
            };
            fetchBook.execute(NetworkUtils.BOOK_BASE_URL);
        }

        rv = (RecyclerView)findViewById(R.id.rv);
        getDataFromTable();
        bookAdapter = new BookAdapter(bookModelList);
        setRecyclerView();

    }

    public void getDataFromTable(){
        BookModel model = new BookModel();
        List<BookModel> modelList = new ArrayList<>();
        myHelper = new MyHelper(this,"BOOKDB",null,1);
        sqLiteDatabase = myHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM book",null);
        while (cursor.moveToNext()){
                sqlIv = cursor.getString(cursor.getColumnIndex("bookUrl"));
                sqlName = cursor.getString(cursor.getColumnIndex("bookName"));
                sqlAuther = cursor.getString(cursor.getColumnIndex("bookAuther"));
                sqlDate = cursor.getString(cursor.getColumnIndex("bookDate"));
//                sqlPrice = cursor.getDouble(cursor.getColumnIndex("bookPrice"));

                model.setImage(sqlIv);
                model.setTitle(sqlName);
                model.setPublishDate(sqlDate);
                model.setAuthor(sqlAuther);
//                model.setAmount(sqlPrice);
                modelList.add(model);
            }
            bookModelList.addAll(modelList);


    }


    private void setRecyclerView() {
        //set the recycler view here
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        rv.setAdapter(bookAdapter);
    }
}
