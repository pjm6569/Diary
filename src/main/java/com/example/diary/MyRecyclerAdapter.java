package com.example.diary;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
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
import java.util.List;


public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

    // 커스텀 리스너 인터페이스 (롱클릭)
    public interface OnItemLongClickListener
    {
        void onItemLongClick(View v, int pos);
    }

    // 리스너 객체 참조를 저장하는 변수(롱클릭)
    private recycler.OnItemLongClickListener mLongListener = null;

    public void setOnItemLongClickListener(recycler.OnItemLongClickListener listener)
    {
        this.mLongListener = listener;
    }
    private ArrayList<CardItem> mycontents;
    public class ViewHolder extends RecyclerView.ViewHolder {
        EditText edt;
        ImageView IV;
        public ViewHolder(View itemView) {
            super(itemView);
            edt = itemView.findViewById(R.id.rv_content);
            edt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    mycontents.get(getAdapterPosition()).setTitle(editable.toString());
                }
            });

            IV = itemView.findViewById(R.id.insert_image);

            IV.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION)
                    {
                        mLongListener.onItemLongClick(v, pos);
                    }
                    return true;
                }
            });

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
        Bitmap bm = BitmapFactory.decodeFile(mycontents.get(position).getContents());
        ImageView IV = holder.IV;
        IV.setImageBitmap(bm);
        EditText edt = holder.edt;
        edt.setText(mycontents.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mycontents.size();
    }

    public void setItems(ArrayList<CardItem> temp) {
        mycontents = temp;
        notifyDataSetChanged();
    }

    public ArrayList<CardItem>mytexts(){
        return mycontents;
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