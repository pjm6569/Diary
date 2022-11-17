package com.example.diary;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import java.util.ArrayList;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {
    private ArrayList<CardItem> arrayList;
    private EditText editText;

    public MyRecyclerAdapter(EditText editText) {
        this.arrayList = new ArrayList<>();
        arrayList.add(new CardItem("",""));
        this.editText = editText;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view, this.editText);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardItem item = arrayList.get(position);
        holder.title.setText(item.getTitle());
        holder.contents.setText(item.getContents());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected EditText title;
        protected EditText contents;

        public ViewHolder(@NonNull View itemView, EditText editText) {
            super(itemView);
            this.title = editText;
            this.contents = itemView.findViewById(R.id.rv_content);
        }
    }

    public void addItem(String title, String contents) {
        arrayList.add(new CardItem(title,contents));
        notifyDataSetChanged();
    }

    public void setItem(int currentIndex, String title, String contents) {
        CardItem beforeItem = arrayList.get(currentIndex);
        beforeItem.setTitle(title);
        beforeItem.setContents(contents);
        notifyDataSetChanged();
    }
//    public void setItems(ArrayList<CardItem> itemList) {
//        this.arrayList = itemList;
//        notifyDataSetChanged();
//    }

    public ArrayList<CardItem> getItems() {
        return arrayList;
    }
}