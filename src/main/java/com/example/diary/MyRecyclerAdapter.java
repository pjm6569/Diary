package com.example.diary;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diary.R;

import java.util.ArrayList;


public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {
    private ArrayList<CardItem> mycontents;

    public class ViewHolder extends RecyclerView.ViewHolder {
        EditText edt;
        ImageView IV;

        public ViewHolder(View itemView) {
            super(itemView);
            edt = itemView.findViewById(R.id.rv_content);
            IV = itemView.findViewById(R.id.insert_image);
        }
        public void onBind(CardItem data) {
        }
    }

    MyRecyclerAdapter(ArrayList<CardItem> list) {
        mycontents = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_card, parent, false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBind(mycontents.get(position));
    }


    @Override
    public int getItemCount() {
        return mycontents.size();

    }

    public void setItems(ArrayList<CardItem> temp) {
        mycontents = temp;
        notifyDataSetChanged();
    }

    public void deleteItems(int position){
        mycontents.remove(position);
        notifyDataSetChanged();
    }


    public void addItems(String title, String image) {
        mycontents.add(new CardItem(title, image));
        notifyDataSetChanged();
    }
}