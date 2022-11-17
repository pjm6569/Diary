package com.example.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


    public class Callender extends AppCompatActivity implements OnDateLongClickListener, View.OnClickListener {
        TextView todayDate1;
        TextView todayDate2;
        MaterialCalendarView calendarView;
        LinearLayout tocontent;
        ImageView tohome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callender);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateLongClickListener(this);
        calendarView.setOnClickListener(this);
        todayDate1 = findViewById(R.id.todayDate1);
        todayDate2 = findViewById(R.id.todayDate2);
        todayDate1.setText(getDate(todayDate1));
        todayDate2.setText(getDate(todayDate2));
        calendarView.getSelectedDate();
        tocontent=findViewById(R.id.whole_contents);
        tocontent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tocontent = new Intent(Callender.this, Contents.class);
                startActivity(tocontent);
            }
        });
        tohome=findViewById(R.id.homebutton);
        tohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    private static String getDate(View view){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        String getTime="";
        switch (view.getId()){
            case R.id.todayDate1:
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN);
                getTime = dateFormat.format(date);
                break;
            case R.id.todayDate2:
                SimpleDateFormat dateFormat2 = new SimpleDateFormat("E요일, yyyy-MM-dd", Locale.KOREAN);
                getTime = dateFormat2.format(date);
                break;
        }
        return getTime;
    }

    @Override
    public void onDateLongClick(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date) {
        widget.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
    }
    @Override
    public void onClick(View view) {
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
    }
}