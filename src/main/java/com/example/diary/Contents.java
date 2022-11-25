package com.example.diary;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
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
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class Contents extends AppCompatActivity {
    private ImageView save;
    private ImageView edit;
    private ImageView add;
    private boolean saved;
    EditText Title;
    ImageView Checkbt;
    ImageView tocallender;
    RecyclerView recyclerView;
    String filepath;
    String text;
    Bitmap bitmap;
    int size;
    int photo_count=0;
    ArrayList <String> AddedFiles = new ArrayList<>();
    ArrayList <String> DeleteFiles = new ArrayList<>();
    ArrayList<CardItem> list = new ArrayList<>();
    MyRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contents);
        saved=false;
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

        save = findViewById(R.id.check_button);
        edit = findViewById(R.id.edit_button);
        add = findViewById(R.id.add_button);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == edit){
                    edit.setVisibility(View.INVISIBLE);
                    add.setVisibility(View.INVISIBLE);
                    save.setVisibility(View.VISIBLE);
                }
            }
        });
        //제목 아이디 불러오기
        Title = findViewById(R.id.edit_text);
        //날짜에 맞는 데이터 검색
        try {
            Contents_Data cd = mRealm.where(Contents_Data.class).equalTo("Date", Date).findFirst();
            Title.setText(cd.getTitle());
            if(!(cd.getTxt1().equals("")&&cd.getImg1().equals("")))
                list.add(new CardItem(cd.getTxt1(),cd.getImg1()));
            if(!(cd.getTxt2().equals("")&&cd.getImg2().equals("")))
                list.add(new CardItem(cd.getTxt2(),cd.getImg2()));
            if(!(cd.getTxt3().equals("")))
                list.add(new CardItem(cd.getTxt3(),""));
            if(!cd.getImg2().equals("")) photo_count++;
            if(!cd.getImg1().equals("")) {
                photo_count++;
            }
        }
        catch(Exception e){

        }
        /////
        filepath=getFilesDir().getAbsolutePath()+"/CThumbs";
        if(list.size()<3)
            list.add(new CardItem("",""));
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
                edit.setVisibility(View.VISIBLE);
                add.setVisibility(View.VISIBLE);
                save.setVisibility(View.INVISIBLE);
                String title = Title.getText().toString();
                saved=true;
                for(int i=0;i<DeleteFiles.size();i++) {
                    removeImageFiles(DeleteFiles.get(i));
                }
                mRealm.executeTransaction(new Realm.Transaction() {
                    ArrayList <CardItem> temp = adapter.mytexts();
                    @Override
                    public void execute(Realm realm) {
                        try {
                            Contents_Data cd = mRealm.where(Contents_Data.class).equalTo("Date", Date).findFirst();
                            cd.setTitle(title);
                            if(temp.size()>=1){
                                cd.setTxt1(temp.get(0).getTitle());
                                cd.setImg1(temp.get(0).getContents());
                                if(temp.size()>=2){
                                    cd.setTxt2(temp.get(1).getTitle());
                                    cd.setImg2(temp.get(1).getContents());
                                    if(temp.size()==3){
                                        cd.setTxt3(temp.get(2).getTitle());
                                    }
                                }
                            }
                        }
                        catch(Exception e) {
                            Contents_Data CD = realm.createObject(Contents_Data.class);
                            CD.setTitle(title);
                            CD.setDate(Date);
                            if(temp.size()>=1){
                                CD.setTxt1(temp.get(0).getTitle());
                                CD.setImg1(temp.get(0).getContents());
                                if(temp.size()>=2){
                                    CD.setTxt2(temp.get(1).getTitle());
                                    CD.setImg2(temp.get(1).getContents());
                                    if(temp.size()==3){
                                        CD.setTxt3(temp.get(2).getTitle());
                                    }
                                }
                            }
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
                if(photo_count<2) {
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

        //카테고리 롱 클릭했을 때
        adapter.setOnItemLongClickListener(new recycler.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View v, int pos) {
                if ((pos == 1 && photo_count == 2) || (pos == 0 && photo_count == 1)) {
                    //다이얼로그 생성
                    AlertDialog.Builder builder = new AlertDialog.Builder(Contents.this);
                    builder.setMessage("이미지를 지우시겠습니까?");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            photo_count--;
                            DeleteFiles.add(list.get(pos).getContents());
                            list.get(pos).setContents("");
                            adapter.notifyDataSetChanged();
                        }
                    });

                    builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog Alt_d = builder.create();
                    Alt_d.show();
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
                            list.get(photo_count).setContents(text);
                            if(list.size()==photo_count+1)
                                list.add(new CardItem("", ""));
                            adapter.setItems(list);
                            AddedFiles.add(text);
                            photo_count++;
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

    public void removeImageFiles(String target){
        File file = new File(filepath);  // 내부저장소 사진 경로를 받아오기
        File[] flist = file.listFiles();
        try {
            String realtarget=target.substring(target.length()-24, target.length());
            for (int j = 0; j < flist.length; j++) {    // 배열의 크기만큼 반복
                if (flist[j].getName().equals(realtarget)) {   // 삭제하고자 하는 이름과 같은 파일명이 있으면 실행
                    flist[j].delete();  // 파일 삭제
                }
            }
        }
        catch(Exception e){
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!saved){
            for(int i=0;i<AddedFiles.size();i++){
                removeImageFiles(AddedFiles.get(i));
            }
        }
    }
}

