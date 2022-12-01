package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class example extends AppCompatActivity {

    SharedPreferences sharedPref; //리스트를 저장하는 sharedPref
    ArrayList<Data> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration.Builder().allowWritesOnUiThread(true).build();
        Realm.setDefaultConfiguration(config);
        Realm mRealm = Realm.getDefaultInstance();

        sharedPref = getSharedPreferences("my_prefs", MODE_PRIVATE);
        preftoArray();
        Toast.makeText(this,  list.get(0).getTitle(),Toast.LENGTH_SHORT).show();
    }

    private void preftoArray() {
        String json = sharedPref.getString("x", "");
        Gson gson = new GsonBuilder().create();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    Data orderData = gson.fromJson(a.get(i).toString(), Data.class);
                    list.add(orderData);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

