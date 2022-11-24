package com.example.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Calendar extends AppCompatActivity {
    TextView todayDate1;
    TextView todayDate2;
    TextView today;
    MaterialCalendarView calendarView;
    LinearLayout tocontent;
    ImageView tohome;
    Button dateselect;
    Button exitSelecting;
    Button showSelectedDates;
    Button plus;
    boolean inSelectionMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        calendarView = findViewById(R.id.calendarView);
        todayDate1 = findViewById(R.id.todayDate1);
        todayDate2 = findViewById(R.id.todayDate2);
        today = findViewById(R.id.moveToday);
        plus = findViewById(R.id.plusCategory);
        todayDate1.setText(getDate(todayDate1));
        todayDate2.setText(getDate(todayDate2));
        List<CalendarDay> selectedDates = calendarView.getSelectedDates();
        String Date=selectedDates.toString();
        Toast.makeText(getApplicationContext(), Date, Toast.LENGTH_LONG).show();
        tocontent=findViewById(R.id.whole_contents);
        tocontent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tocontent = new Intent(Calendar.this, Contents.class);
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
        dateselect = findViewById(R.id.test_date);
        exitSelecting = findViewById(R.id.exitSelect);
        showSelectedDates = findViewById(R.id.showSelectedDate);
        DialogInterface.OnClickListener choiceListener = new DialogInterface.OnClickListener() { //날짜 선택모드. 여러개 선택과 범위 선택
            @Override
            public void onClick(DialogInterface dialog, int which) {
                calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
                if(which != 0) //'여러 개 선택' 선택시
                    calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
                inSelectionMode = true;
                dateselect.setVisibility(View.GONE);
                exitSelecting.setVisibility(View.VISIBLE);
                showSelectedDates.setVisibility(View.VISIBLE);
                plus.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        };
        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() { //날짜 선택모드 다이얼로그에서 취소버튼
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
            }
        };
        exitSelecting.setOnClickListener(new View.OnClickListener(){ //날짜 선택모드에서 나가기
            @Override
            public void onClick(View view){
                exitSelecting.setVisibility(View.GONE);
                showSelectedDates.setVisibility(View.GONE);
                dateselect.setVisibility(View.VISIBLE);
                plus.setVisibility(View.GONE);
                inSelectionMode = false;
                calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
            }
        });
        showSelectedDates.setOnClickListener(new View.OnClickListener() { //날짜 선택모드에서 선택된 날짜들 토스트로 표시
            @Override
            public void onClick(View view) {
                final List<CalendarDay> selectedDates = calendarView.getSelectedDates();
                if (!selectedDates.isEmpty()) {
                    Toast.makeText(getApplicationContext(), selectedDates.toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "No Selection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dateselect.setOnClickListener(new View.OnClickListener() { //날짜 선택버튼, 다이얼로그 표시
            @Override
            public void onClick(View view){
                AlertDialog.Builder dateSelectionMode = new AlertDialog.Builder(Calendar.this);
                dateSelectionMode.setTitle("날짜 선택모드");
                dateSelectionMode.setSingleChoiceItems(R.array.date_selection, -1, choiceListener);
                dateSelectionMode.setNegativeButton("취소", cancelListener);
                dateSelectionMode.show();
            }
        });

        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarView.setSelectedDate(CalendarDay.today());
                calendarView.setCurrentDate(CalendarDay.today());
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
}