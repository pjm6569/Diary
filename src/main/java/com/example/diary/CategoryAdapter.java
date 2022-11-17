package com.example.diary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{
    private ArrayList<Data> l;
    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageButton CategoryImage;
        private TextView CategoryText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            CategoryText=itemView.findViewById(R.id.contents);
            CategoryImage=itemView.findViewById(R.id.thumbnail);
        }
        public void oB(Data data){
            CategoryText.setText(data.getTitle());
        }

    }
    CategoryAdapter(ArrayList<Data> list) {
        l = list;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.oB(l.get(position));
    }

    @Override
    public int getItemCount() {
        return l.size();
    }
}
