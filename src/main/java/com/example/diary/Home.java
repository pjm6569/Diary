package com.example.diary;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telecom.Call;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Home extends AppCompatActivity{
    int ss=0;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    ImageButton search; //검색 버튼
    ImageButton addc; //다이얼로그 여는 버튼
    ImageButton toCallender;
    int clicked = 0;
    String text;
    recycler adapter;
    Dialog cdlog; //커스텀 다이얼로그
    Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        sharedPref = getSharedPreferences("my_prefs", MODE_PRIVATE);
        editor= sharedPref.edit();

//        editor.clear().commit(); //카테고리 전부 지우기(실험용)

        //액션바 숨기기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //애니메이션 처리
        Animation blank_anim = AnimationUtils.loadAnimation(this, R.anim.blank);
        ImageView swipe = findViewById(R.id.swipe);
        swipe.startAnimation(blank_anim);
        search = findViewById(R.id.search_button);

        //RecyclerView 임의추가
        ArrayList<Data> list = new ArrayList<>();
        ArrayList<Data> searchlist =  new ArrayList<>();

        Map<String,?> keys = sharedPref.getAll();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            list.add(new Data(entry.getKey(), entry.getValue().toString()));
        }

        //검색창을 만든다.

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.searchbar, null);
        ll.setBackgroundColor(Color.parseColor("#99000000")); //투명도 조절
        LinearLayout.LayoutParams paramll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(ll, paramll);
        EditText ed = ll.findViewById(R.id.edit);
        ll.setVisibility(View.INVISIBLE);

        //검색창을 띄운다.(검색 클릭 시)
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clicked == 0) {
                    ll.setVisibility(View.VISIBLE);
                    clicked = 1;
                }
            }
        });

        //검색창을 끄기(검색을 마쳤을 때)
        ed.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                switch (i) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        ed.clearFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(ed.getWindowToken(), 0);
                        ll.setVisibility(View.INVISIBLE);
                        clicked = 0;
                        return false;
                }
                return true;
            }
        });

        ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            //검색과정
            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = ed.getText().toString();
                searchlist.clear();
                //아무것도 입력하지 않으면 원래대로
                if(searchText.equals("")){
                    adapter.setItems(list);
                }
                else {
                    // 검색 단어를 포함하는지 확인
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                            searchlist.add(list.get(i));
                        }
                        adapter.setItems(searchlist);
                    }
                }
            }
        });

        toCallender = findViewById(R.id.tocallender);
        toCallender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tocallender = new Intent(Home.this, Callender.class);
                startActivity(tocallender);
            }
        });


        //카테고리(recyclerview 띄우기)
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new recycler(list);
        recyclerView.setAdapter(adapter);

        //카테고리 롱 클릭했을 때
        adapter.setOnItemLongClickListener(new recycler.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View v, int pos) {
                //다이얼로그 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                builder.setMessage("카테고리를 지우시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), list.get(pos).getTitle()+" 카테고리가 삭제되었습니다", Toast.LENGTH_SHORT).show();
                        editor.remove(list.get(pos).getTitle()).apply();
                        adapter.deleteItems(pos);
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
        });

        //dialog 띄우기 버튼
        cdlog= new Dialog(Home.this);       // Dialog 초기화
        cdlog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 타이틀 제거
        cdlog.setContentView(R.layout.adddialog);             // xml 레이아웃 파일과 연결
        addc=findViewById(R.id.addCategory);
        addc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    ShowDialog();
            }
        });
    }

    //비트맵을 문자열로 바꾸는 함수
    public String getBase64String(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

//갤러리에서 사진을 불러오기 위한 Launcher 생성 및 불러온 비트맵 문자열로 바꾸어 text에 저장
    ActivityResultLauncher<Intent> Launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        uri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            text = getBase64String(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });





    public void ShowDialog() {
        //Spinner 배열과 이미지 설정
        String [] items = getResources().getStringArray(R.array.spinner);
        ArrayAdapter <String> spinner_adapter= new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, items);
        Spinner th = cdlog.findViewById(R.id.theme);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        th.setAdapter(spinner_adapter);
        th.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override   // position 으로 몇번째 것이 선택됬는지 값을 넘겨준다
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    text=items[position];
                    if(text.equals("기타(갤러리)")){//기타(갤러리)를 선택하면 갤러리에서 불러오기
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    Launcher.launch(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        cdlog.show(); //다이얼로그를 띄운다.
        Button yesBtt = cdlog.findViewById(R.id.YesButton);
        Button noBtt = cdlog.findViewById(R.id.NoButton);
        EditText edtdialog = cdlog.findViewById(R.id.plusCategory);
        Map<String,?> keys = sharedPref.getAll();
            noBtt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cdlog.dismiss();
                }
            });
            yesBtt.setOnClickListener(new View.OnClickListener() {
                Boolean End=false; //다이얼로그를 종료할지 말지 정하는 플래그(카테고리를 추가할지)
                @Override
                public void onClick(View view) {
                        int count = 0;
                        String addname = edtdialog.getText().toString(); //이미 있는 제목인지 검사
                        for (Map.Entry<String, ?> entry : keys.entrySet()) {
                            if (entry.getKey().equals(addname)) {
                                count++;
                            }
                        }
                        if(count>0){ //이미 존재하는 제목이라면 메시지 띄움
                            Toast.makeText(getApplicationContext(), "이미 존재하는 카테고리 입니다", Toast.LENGTH_SHORT).show();
                        }
                        else {  //존재하지 않는 제목이라면 추가 가능.
                            End = true;
                        }

                    if(End) {
                        editor.putString(addname, text).apply(); //키(제목) 밸류(이미지를 나타내는 문자열)로 SharedPreference 추가
                        adapter.addItems(addname, text);  //recyclerview 생성
                        edtdialog.setText(null); //edittext 초기화
                        cdlog.dismiss();  //다이얼로그를 닫는다.
                    }
                }
            });
    }

}
