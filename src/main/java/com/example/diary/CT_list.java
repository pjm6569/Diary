package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class CT_list extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ct_list);

        ArrayList<Data> test = new ArrayList<>();
        test.add(new Data("x", ""));
        ListView listView = findViewById(R.id.listView1);
        ListAdapter adapter = new ListAdapter(this, test);
        listView.setAdapter(adapter);
    }
}