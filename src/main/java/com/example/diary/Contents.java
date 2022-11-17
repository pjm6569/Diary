package com.example.diary;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class Contents extends AppCompatActivity {
ImageView tocallender;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contents);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        ArrayList<CardItem> list = new ArrayList<>();
        list.add(new CardItem("", ""));
        //리싸이클러 뷰 생성
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //어댑터를 통해 리싸이클러 뷰의 타이틀 생성
        MyRecyclerAdapter adapter = new MyRecyclerAdapter(list);
        recyclerView.setAdapter(adapter);


        tocallender=findViewById(R.id.back_button);
        tocallender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        //클립보드 이미지 변수 생성
        ImageView img = findViewById(R.id.clip_add_recycler);
        //클립보드 버튼을 눌렀을 때 리싸이클러 뷰에 Item 추가하는 코드
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.addItems("","");
                Toast.makeText(getApplicationContext(), "yes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getTitleText() {
        EditText editText = findViewById(R.id.edit_text);
        return editText.getText().toString();
    }

    private String getContentsText(int currentIndex) throws NullPointerException{
        EditText editText = recyclerView.getLayoutManager().findViewByPosition(currentIndex).findViewById(R.id.rv_content);
        return editText.getText().toString();
    }
}

