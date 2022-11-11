package com.example.diary;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class list2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list2);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        ArrayList<Data> test = new ArrayList<>();
        for(int i=0;i<8;i++) {
            if(i%2==0)
                test.add(new Data("Title"+(i+1), ""));
            else
                test.add(new Data("Title"+(i+1), ""));
        }

        RecyclerView cat= findViewById(R.id.recycler2) ;
        cat.setLayoutManager(new LinearLayoutManager(this)) ;

        CategoryAdapter catAdapter = new CategoryAdapter(test) ;
        cat.setAdapter(catAdapter) ;
    }
}