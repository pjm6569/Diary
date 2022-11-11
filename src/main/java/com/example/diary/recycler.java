package com.example.diary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diary.R;

import java.util.ArrayList;


public class recycler extends RecyclerView.Adapter<recycler.ViewHolder> {
    private ArrayList<Data> mData;

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton img;
        TextView tv;


        public ViewHolder(View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.recycleimage);
            tv = itemView.findViewById(R.id.recyclertext);
        }

        public void onBind(Data data) {
            tv.setText(data.getTitle());
            switch (data.getImage()){
                case("테마(기본값)"):
                    break;
                case("Birthday 1"):
                    img.setImageResource(R.drawable.birthday_1);
                    break;
                case("Birthday 2"):
                    img.setImageResource(R.drawable.birthday_2);
                    break;
                case("Festival 1"):
                    img.setImageResource(R.drawable.festival_1);
                    break;
                case("Festival 2"):
                    img.setImageResource(R.drawable.festival_2);
                    break;
                case("Festival 3"):
                    img.setImageResource(R.drawable.festival_3);
                    break;
                case("Journey 1"):
                    img.setImageResource(R.drawable.journey_1);
                    break;
                case("Journey 2"):
                    img.setImageResource(R.drawable.journey_2);
                    break;
                default:
                    img.setImageBitmap(StringToBitmap(data.getImage()));
            }
        }
    }

    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    recycler(ArrayList<Data> list) {
        mData = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.recyclerview_item, parent, false);
        ViewHolder vh = new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.onBind(mData.get(position));
    }


    @Override
    public int getItemCount() {
        return mData.size();

    }

    public void setItems(ArrayList<Data> temp) {
        mData = temp;
        notifyDataSetChanged();
    }


    public void addItems(String title, String image) {
        mData.add(new Data(title, image));
        notifyDataSetChanged();
    }
}