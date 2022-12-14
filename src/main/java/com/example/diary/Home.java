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
    int gall =0; //???????????? ???????????? ??????
    String filepath;
    SharedPreferences sharedPref; //???????????? ???????????? sharedPref
    SharedPreferences.Editor editor;
    ImageButton search; //?????? ??????
    ImageButton addc; //??????????????? ?????? ??????
    ImageButton toCallender; //????????? ??????
    int clicked = 0; //???????????? ??????
    String text; // ??????????????? ?????? ???????????? ????????? / ??????????????? ??????
    recycler adapter; //?????????
    Dialog cdlog; //????????? ???????????????
    Uri uri;
    String imgname="temp"; //??????????????? ?????? ?????? ??????
    ArrayList<Data> list = new ArrayList<>(); //????????? ?????? ??????
    Bitmap bitmap; //???????????? ????????? ????????? ?????? ??????

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


//        editor.clear().commit(); //???????????? ?????? ?????????(?????????)

        //????????? ?????????
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //??????????????? ??????
        Animation blank_anim = AnimationUtils.loadAnimation(this, R.anim.blank);
        ImageView swipe = findViewById(R.id.swipe);
        swipe.startAnimation(blank_anim);
        search = findViewById(R.id.search_button);

        //RecyclerView ????????????

        String json = sharedPref.getString("x", null);
        preftoArray();
        ArrayList<Data> searchlist =  new ArrayList<>();



        //???????????? ?????????.

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.searchbar, null);
        ll.setBackgroundColor(Color.parseColor("#99000000")); //????????? ??????
        LinearLayout.LayoutParams paramll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(ll, paramll);
        EditText ed = ll.findViewById(R.id.edit);
        ll.setVisibility(View.INVISIBLE);

        //???????????? ?????????.(?????? ?????? ???)
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clicked == 0) {
                    ll.setVisibility(View.VISIBLE);
                    clicked = 1;
                }
            }
        });

        //???????????? ??????(????????? ????????? ???)
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
            //????????????
            @Override
            public void afterTextChanged(Editable editable) {
                String searchText = ed.getText().toString();
                searchlist.clear();
                //???????????? ???????????? ????????? ????????????
                if(searchText.equals("")){
                    adapter.setItems(list);
                }
                else {
                    // ?????? ????????? ??????????????? ??????
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                            searchlist.add(list.get(i));
                        }
                        adapter.setItems(searchlist);
                    }
                }
            }
        });

        //????????? ??????????????? ??????
        toCallender = findViewById(R.id.tocallender);
        toCallender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tocallender = new Intent(Home.this, Calendar.class);
                startActivity(tocallender);
            }
        });


        //????????????(recyclerview ?????????)
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new recycler(list);
        recyclerView.setAdapter(adapter);

        //???????????? ??? ???????????? ???
        adapter.setOnItemLongClickListener(new recycler.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View v, int pos) {
                String Name = list.get(pos).getTitle();
                //??????????????? ??????
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                builder.setMessage("??????????????? ??????????????????????");
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        File file = new File(filepath);  // ??????????????? ?????? ????????? ????????????
                        File[] flist = file.listFiles();
                        String target=list.get(pos).getImage();
                        try {
                            String realtarget=target.substring(target.length()-22, target.length());
                            for (int j = 0; j < flist.length; j++) {    // ????????? ???????????? ??????
                                if (flist[j].getName().equals(realtarget)) {   // ??????????????? ?????? ????????? ?????? ???????????? ????????? ??????
                                    flist[j].delete();  // ?????? ??????
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
                        Toast.makeText(getApplicationContext(), list.get(pos).getTitle()+" ??????????????? ?????????????????????", Toast.LENGTH_SHORT).show();
                        list.remove(pos);
                        adapter.notifyDataSetChanged();
                        savepreference();
                    }
                });

                builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
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

        //dialog ????????? ??????
        cdlog= new Dialog(Home.this);       // Dialog ?????????
        cdlog.requestWindowFeature(Window.FEATURE_NO_TITLE); // ????????? ??????
        cdlog.setContentView(R.layout.adddialog);             // xml ???????????? ????????? ??????
        addc=findViewById(R.id.addCategory);
        addc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    ShowDialog();
            }
        });
    }

    //??????????????? ??????
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

    //??????????????? ????????????
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

    //??????????????? ????????? ???????????? ?????? Launcher ??????
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

    //????????? ?????????.
    public void saveBitmapToJpeg(Bitmap bitmap) {   // ????????? ????????? ?????? ???????????? ??????
        File file = new File(filepath);
        if(!file.exists()){
            file.mkdir();
        }
        File tempFile = new File(filepath,text);   // ?????? ????????? ?????? ??????
        try {
            tempFile.createNewFile();   // ???????????? ??? ????????? ????????????
            FileOutputStream out = new FileOutputStream(tempFile);  // ????????? ??? ??? ?????? ???????????? ????????????
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);   // compress ????????? ????????? ???????????? ???????????? ????????????
            out.close();    // ????????? ????????????
            }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "?????? ?????? ??????", Toast.LENGTH_SHORT).show();
        }
    }



    public void ShowDialog() {
        //Spinner ????????? ????????? ??????
        String [] items = getResources().getStringArray(R.array.spinner);
        ArrayAdapter <String> spinner_adapter= new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, items);
        Spinner th = cdlog.findViewById(R.id.theme);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        th.setAdapter(spinner_adapter);
        th.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override   // position ?????? ????????? ?????? ??????????????? ?????? ????????????
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    text=items[position];
                    gall=0;
                    if(text.equals("??????(?????????)")){//??????(?????????)??? ???????????? ??????????????? ????????????
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

        cdlog.show(); //?????????????????? ?????????.
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
                Boolean End=false; //?????????????????? ???????????? ?????? ????????? ?????????(??????????????? ????????????)
                @Override
                public void onClick(View view) {
                    int count = 0;
                    String addname = edtdialog.getText().toString(); //?????? ?????? ???????????? ??????
                    for(int i=0;i<list.size();i++){
                        if(addname.equals(list.get(i).getTitle())){
                            count++;
                        }
                    }
                    if(count==0)
                        End=true;
                    else {
                        Toast.makeText(getApplicationContext(), "?????? ?????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                    }
                    if(End) {
                        if(gall==1) {
                            saveBitmapToJpeg(bitmap);
                            text = filepath + "/" + text;
                        }
                        list.add(new Data(addname, text));
                        adapter.notifyDataSetChanged();
                        savepreference();
                        edtdialog.setText(null); //edittext ?????????
                        cdlog.dismiss();  //?????????????????? ?????????.
                    }
                }
            });
    }

}
