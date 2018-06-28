package com.example.incred_dev.booksearch;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import java.io.InputStream;
import java.util.List;

/**
 * Created by incred-dev on 18/5/18.
 */

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    List<BookModel> bookModelList;
    SparseArray<Bitmap> bitmapSparseArray=  new SparseArray<>();
    MyHelper myHelper;
    SQLiteDatabase sqLiteDatabase;


    public BookAdapter(List<BookModel> bookModelList) {
        this.bookModelList = bookModelList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BookAdapter.ViewHolder holder, int position) {

        BookModel bookModel = bookModelList.get(holder.getAdapterPosition());
        if (bookModel.getTitle() != null){
            holder.bookName.setText(bookModel.getTitle());
        }
        if (bookModel.getAuthor() != null){
            holder.autherName.setText(bookModel.getAuthor());
        }
        if (bookModel.getPublishDate() != null){
            holder.date.setText(bookModel.getPublishDate());
        }
        if (bookModel.getAmount() != null){
            holder.price.setText("€ "+Double.toString(bookModel.getAmount()));
            holder.price.setTextColor(Color.BLACK);
        } else {
            holder.price.setText("Not For Sales");
            holder.price.setTextColor(Color.RED);
        }
        if (bookModel.getImage() != null){

            if (bitmapSparseArray.get(holder.getAdapterPosition())!=null){
                holder.imageView.setImageBitmap(bitmapSparseArray.get(holder.getAdapterPosition()));
            }else{
                new DownloadImageTask(){
                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        bitmapSparseArray.append(holder.getAdapterPosition(),bitmap);
                        holder.imageView.setImageBitmap(bitmap);
                        Log.d("TAG","downloaded image");
                    }
                }.execute(bookModel.getImage());

            }
        }
    }

    @Override
    public int getItemCount() {
        return bookModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView price, bookName, autherName, date;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv);
            price = itemView.findViewById(R.id.tvPrice);
            bookName = itemView.findViewById(R.id.tvBookName);
            autherName = itemView.findViewById(R.id.tvAutherName);
            date = itemView.findViewById(R.id.tvDate);

        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e){
                e.printStackTrace();
            }
            return bitmap;
        }
    }
}

