package com.example.diary;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class Contents extends AppCompatActivity {
    EditText Title;
    ImageView Checkbt;
    ImageView tocallender;
    RecyclerView recyclerView;
    String filepath;
    String text;
    Bitmap bitmap;
    int size;
    ArrayList<CardItem> list = new ArrayList<>();
    MyRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contents);
        //Realm 초기화 및 생성
        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration.Builder().allowWritesOnUiThread(true).build();
        Realm.setDefaultConfiguration(config);
        Realm mRealm = Realm.getDefaultInstance();
        //날짜 불러오기
        String Date = getIntent().getStringExtra("Date");
        //액션바 지우기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //날짜에 맞는 데이터 검색


        Title = findViewById(R.id.edit_text);
        try {
            Contents_Data cd = mRealm.where(Contents_Data.class).equalTo("Date", Date).findFirst();
            Title.setText(cd.getTitle());
        }
        catch(Exception e){

        }
        /////
        filepath=getFilesDir().getAbsolutePath()+"/CThumbs";
        list.add(new CardItem("", ""));
        //리싸이클러 뷰 생성
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //어댑터를 통해 리싸이클러 뷰의 타이틀 생성
        adapter = new MyRecyclerAdapter(list);
        recyclerView.setAdapter(adapter);
        tocallender=findViewById(R.id.back_button);
        tocallender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Checkbt=findViewById(R.id.check_button);
        Checkbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = Title.getText().toString();;
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        try {
                            Contents_Data cd = mRealm.where(Contents_Data.class).equalTo("Date", Date).findFirst();
                            cd.setTitle(title);
                        }
                        catch(Exception e) {
                            Contents_Data CD = realm.createObject(Contents_Data.class);
                            CD.setTitle(title);
                            CD.setDate(Date);
                        }
                        Toast.makeText(getApplicationContext(), title, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //클립보드 이미지 변수 생성
        ImageView img = findViewById(R.id.clip_add_recycler);
        //클립보드 버튼을 눌렀을 때 리싸이클러 뷰에 Item 추가하는 코드
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                size=list.size();
                if(size<=2) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    Launcher.launch(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "사진은 2개까지입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    ActivityResultLauncher<Intent> Launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
                            text="cImage"+timeStamp+".png";
                            saveBitmapToJpeg(bitmap);
                            text=filepath+"/"+text;
                            int size= list.size()-1;
                            list.get(size).setContents(text);
                            list.add(new CardItem("", ""));
                            adapter.setItems(list);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    //사진을 저장함.
    public void saveBitmapToJpeg(Bitmap bitmap) {   // 선택한 이미지 내부 저장소에 저장
        File file = new File(filepath);
        if(!file.exists()){
            file.mkdir();
        }
        File tempFile = new File(filepath,text);   // 파일 경로와 이름 넣기
        try {
            tempFile.createNewFile();   // 자동으로 빈 파일을 생성하기
            FileOutputStream out = new FileOutputStream(tempFile);  // 파일을 쓸 수 있는 스트림을 준비하기
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);   // compress 함수를 사용해 스트림에 비트맵을 저장하기
            out.close();    // 스트림 닫아주기
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "파일 저장 실패", Toast.LENGTH_SHORT).show();
        }
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