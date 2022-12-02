package com.example.diary;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class Home extends AppCompatActivity{
    int gall =0; //갤러리를 열었는지 확인
    String filepath;
    SharedPreferences sharedPref; //리스트를 저장하는 sharedPref
    SharedPreferences.Editor editor;
    ImageButton search; //검색 버튼
    ImageButton addc; //다이얼로그 여는 버튼
    ImageButton toCallender; //캘린더 버튼
    int clicked = 0; //클릭여부 확인
    String text; // 카테고리의 내용 해당하는 텍스트 / 이미지명을 겸함
    recycler adapter; //어댑터
    Dialog cdlog; //커스텀 다이얼로그
    Uri uri;
    String imgname="temp"; //이미지명에 쓰일 기본 제목
    ArrayList<Data> list = new ArrayList<>(); //리스트 미리 선언
    Bitmap bitmap; //이미지에 필요한 비트맵 미리 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        filepath=getFilesDir().getAbsolutePath()+"/Thumbs";
        sharedPref = getSharedPreferences("my_prefs", MODE_PRIVATE);
        editor= sharedPref.edit();

        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration.Builder().allowWritesOnUiThread(true).build();
        Realm.setDefaultConfiguration(config);
        Realm mRealm = Realm.getDefaultInstance();


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

        String json = sharedPref.getString("x", null);
        preftoArray();
        ArrayList<Data> searchlist =  new ArrayList<>();



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

        //캘린더 액티비티로 이동
        toCallender = findViewById(R.id.tocallender);
        toCallender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tocallender = new Intent(Home.this, Calendar.class);
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
                String Name = list.get(pos).getTitle();
                //다이얼로그 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                builder.setMessage("카테고리를 지우시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        File file = new File(filepath);  // 내부저장소 캐시 경로를 받아오기
                        File[] flist = file.listFiles();
                        String target=list.get(pos).getImage();
                        try {
                            String realtarget=target.substring(target.length()-22, target.length());
                            for (int j = 0; j < flist.length; j++) {    // 배열의 크기만큼 반복
                                if (flist[j].getName().equals(realtarget)) {   // 삭제하고자 하는 이름과 같은 파일명이 있으면 실행
                                    flist[j].delete();  // 파일 삭제
                                }
                            }
                        }
                        catch(Exception e){
                        }
                        RealmResults<Connection> results = mRealm.where(Connection.class).equalTo("Name", Name).findAll();
                        mRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                results.deleteAllFromRealm();
                            }
                        });
                        Toast.makeText(getApplicationContext(), list.get(pos).getTitle()+" 카테고리가 삭제되었습니다", Toast.LENGTH_SHORT).show();
                        list.remove(pos);
                        adapter.notifyDataSetChanged();
                        savepreference();
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

        adapter.setOnItemClickListener(new recycler.OnItemClickEventListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Intent tolist = new Intent(Home.this, CT_list.class);
                tolist.putExtra("Name", list.get(pos).getTitle());
                startActivity(tolist);
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

    //프리퍼런스 저장
    private void savepreference(){
        JSONArray a = new JSONArray();
        Gson gson =new GsonBuilder().create();
        for (int i = 0; i < list.size(); i++) {
            String string = gson.toJson(list.get(i), Data.class);
            a.put(string);
        }
        editor.putString("x", a.toString());
        editor.apply();
    }

    //프리퍼런스 불러오기
    private void preftoArray(){
        String json =  sharedPref.getString("x","");
        Gson gson = new GsonBuilder().create();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    Data orderData = gson.fromJson( a.get(i).toString() , Data.class);
                    list.add(orderData);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //갤러리에서 사진을 불러오기 위한 Launcher 생성
    ActivityResultLauncher<Intent> Launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        uri = result.getData().getData();
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
                            text=imgname+timeStamp+".png";
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
                    gall=0;
                    if(text.equals("기타(갤러리)")){//기타(갤러리)를 선택하면 갤러리에서 불러오기
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    Launcher.launch(intent);
                    gall=1;
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
                    for(int i=0;i<list.size();i++){
                        if(addname.equals(list.get(i).getTitle())){
                            count++;
                        }
                    }
                    if(count==0)
                        End=true;
                    else {
                        Toast.makeText(getApplicationContext(), "이미 있는 카테고리입니다.", Toast.LENGTH_SHORT).show();
                    }
                    if(End) {
                        if(gall==1) {
                            saveBitmapToJpeg(bitmap);
                            text = filepath + "/" + text;
                        }
                        list.add(new Data(addname, text));
                        adapter.notifyDataSetChanged();
                        savepreference();
                        edtdialog.setText(null); //edittext 초기화
                        cdlog.dismiss();  //다이얼로그를 닫는다.
                    }
                }
            });
    }

}
