package com.example.diary;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class CT_list extends AppCompatActivity {

    ImageView imv;
    TextView title;
    ArrayList <Connection> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ct_list);

        //액션바 숨기기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        String Name = getIntent().getStringExtra("Name");
        title=findViewById(R.id.CTTitle);
        title.setText(Name);
        imv=findViewById(R.id.backtohome);
        imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ArrayList<Data> list = new ArrayList<>();

        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration.Builder().allowWritesOnUiThread(true).build();
        Realm.setDefaultConfiguration(config);
        Realm mRealm = Realm.getDefaultInstance();
        RealmResults<Connection> results = mRealm.where(Connection.class).equalTo("Name", Name).findAll();
        String date = "";
        String image = "";

        for (int i = 0; i < results.size(); i++) {
            date = results.get(i).getDate();
            try {
                Contents_Data result2 = mRealm.where(Contents_Data.class).equalTo("Date", date).findFirst();
                image = result2.getImg1();
            }
            catch(Exception e){}
            list.add(new Data(date, image));
        }



        ListView listView = findViewById(R.id.listView1);
        ListAdapter adapter = new ListAdapter(this, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent tocontent = new Intent(CT_list.this, Contents.class);
                tocontent.putExtra("Date", list.get(i).getTitle());
                startActivity(tocontent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CT_list.this);
                builder.setMessage("카테고리를 지우시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int x) {
                        mRealm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Connection connection = results.get(i);
                                connection.deleteFromRealm();
                            }
                        });
                        list.remove(i);
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
                return true;
            }
        });

    }

    protected void onRestart() {
        super.onRestart();
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

}