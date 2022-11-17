package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class Contents extends AppCompatActivity {

    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contents);

        //리싸이클러 뷰 생성
        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        //리싸이클러뷰 출력
        recyclerView.setLayoutManager(layoutManager);

        //어댑터를 통해 리싸이클러 뷰의 타이틀 생성
        MyRecyclerAdapter adapter = new MyRecyclerAdapter(findViewById(R.id.edit_text));
        recyclerView.setAdapter(adapter);

//        EditText Et = (EditText) findViewById(R.id.ch1);
//        Et.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s,int start,int count,int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s,int start,int before,int count) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

        //클립보드 이미지 변수 생성
        ImageView img = findViewById(R.id.clip_add_recycler);
        //클립보드 버튼을 눌렀을 때 리싸이클러 뷰에 Item 추가하는 코드
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int currentIndex = adapter.getItems().size()-1;

                try {
                    String title = getTitleText();
                    String contents = getContentsText(currentIndex);

                    if (title.isEmpty()) {
                        Toast.makeText(Contents.this, "어떤 이벤트를 추가하시나요?", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    adapter.setItem(currentIndex,getTitleText(),getContentsText(currentIndex));

                    // 인덱스 0과 "같은제목"의 contents가 비어있는 리싸이클러 뷰 추가
                    adapter.addItem(getTitleText(),"");

                    //클립보드 버튼을 통한 리싸이클러뷰가 생성 될 때마다 토스트 메세지(디버그용 - 이후에 삭제)
                    Toast.makeText(Contents.this, "success. count : "+adapter.getItemCount(), Toast.LENGTH_SHORT).show();

                    // 가끔 클립보드 이미지를 여러번 눌렀을때 튕기는 현상을 토스트로 띄움. 앱 강제종료 방지
                } catch (NullPointerException ex) {
                    Toast.makeText(Contents.this, "추가에 실패하였습니다", Toast.LENGTH_SHORT).show();
                    return;
                }
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

