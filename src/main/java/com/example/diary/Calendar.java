package com.example.diary;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import com.example.diary.SundayDecorator;
import com.example.diary.SaturdayDecorator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import io.realm.Realm;
import io.realm.RealmConfiguration;


public class Calendar extends AppCompatActivity implements View.OnClickListener{
    SharedPreferences sharedPref; //리스트를 저장하는 sharedPref
    ArrayList<String> list_ct = new ArrayList<>();
    List<String> dates = new ArrayList<>();
    TextView todayDate1;
    TextView todayDate2;
    TextView today;
    MaterialCalendarView calendarView;
    LinearLayout tocontent;
    ImageView tohome;
    Button dateselect;
    Button exitSelecting;
    Button plus;
    public static Context context;
    public TextView title;
    public TextView content;
    public ImageView thumbnail;
    boolean inSelectionMode = false;
    String selectedDate = CalendarDay.today().getDate().toString();//전역변수 날짜. DB에 사용

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        context = this;

        sharedPref = getSharedPreferences("my_prefs", MODE_PRIVATE);
        preftoArray();
        int arrListSize = list_ct.size();
        String arr[] = list_ct.toArray(new String[arrListSize]);

        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration.Builder().allowWritesOnUiThread(true).build();
        Realm.setDefaultConfiguration(config);
        Realm mRealm = Realm.getDefaultInstance();

        calendarView = findViewById(R.id.calendarView);
        todayDate1 = findViewById(R.id.todayDate1);
        todayDate2 = findViewById(R.id.todayDate2);
        today = findViewById(R.id.moveToday);
        plus = findViewById(R.id.plusCategory);
        tocontent = findViewById(R.id.whole_contents); //내용 표시 뷰
        tohome=findViewById(R.id.homebutton);
        dateselect = findViewById(R.id.dateSelect);
        exitSelecting = findViewById(R.id.exitSelect);
        title = findViewById(R.id.content1);
        content = findViewById(R.id.content2);
        thumbnail = findViewById(R.id.content3);

        todayDate1.setText(selectedDate);
        todayDate2.setText(selectedDate);

        calendarView.state().edit()
                .setMinimumDate(CalendarDay.from(2020, 1, 1)) // 달력의 시작
                .setMaximumDate(CalendarDay.from(2030, 12, 31)) // 달력의 끝
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        calendarView.addDecorators(new SundayDecorator(), new SaturdayDecorator());
        calendarView.invalidateDecorators();
        DialogInterface.OnClickListener choiceListener = new DialogInterface.OnClickListener() { //날짜 선택모드. 여러개 선택과 범위 선택
            @Override
            public void onClick(DialogInterface dialog, int which) {
                calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
                if(which != 0) //'여러 개 선택' 선택시
                    calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);
                inSelectionMode = true;
                dateselect.setVisibility(View.GONE);
                exitSelecting.setVisibility(View.VISIBLE);
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


        exitSelecting.setOnClickListener(this);
        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarView.setSelectedDate(CalendarDay.today());
                calendarView.setCurrentDate(CalendarDay.today());
                selectedDate = CalendarDay.today().getDate().toString();
                todayDate1.setText(selectedDate);
                todayDate2.setText(selectedDate);
                try{
                    Contents_Data cd = mRealm.where(Contents_Data.class).equalTo("Date", selectedDate).findFirst();
                    if(cd.isValid()){
                        title.setText(cd.getTitle());
                        content.setText(cd.getTxt1());
                        //Bitmap bp = BitmapFactory.decodeFile(cd.getImg1());
                        //thumbnail.setImageBitmap(bp);
                    }
                } catch (Exception e){

                }
            }
        });
        tohome.setOnClickListener(this);
        tocontent.setOnClickListener(this);
        plus.setOnClickListener(this);

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



        calendarView.setOnDateChangedListener(new OnDateSelectedListener() { //날짜를 클릭할 때마다(날짜가 바뀔때마다) 이벤트
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView calendarView, @NonNull CalendarDay date, boolean selected) {
                if(!inSelectionMode){
                    title.setText("");
                    content.setText("");
                    thumbnail.setImageResource(0);
                    selectedDate = date.getDate().toString();
                    todayDate1.setText(selectedDate);
                    todayDate2.setText(selectedDate);
                    //Toast.makeText(getApplicationContext(), selectedDate, Toast.LENGTH_SHORT).show();
                    //EventDecorator eventDecorator = new EventDecorator(date);
                    //calendarView.invalidateDecorators();
                    try {
                        Contents_Data cd = mRealm.where(Contents_Data.class).equalTo("Date", selectedDate).findFirst();
                        if(cd.isValid()){
                            title.setText(cd.getTitle());
                            content.setText(cd.getTxt1());
                            Bitmap bp = BitmapFactory.decodeFile(cd.getImg1());
                            thumbnail.setImageBitmap(bp);
                        }
                    } catch (Exception e){

                    }
//                    try{
//                        if(cd.getTitle().equals("")) {
//                            title.setText(cd.getTitle());
//                            calendarView.removeDecorators();
//                        } else if(cd.isValid()) {
//                            title.setText(cd.getTitle());
//                            calendarView.addDecorator(eventDecorator);
//                        }
//                    } catch (Exception e){
//                    }
                }
            }
        });

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<CalendarDay> calendarDays = new ArrayList<>(calendarView.getSelectedDates());
                dates.clear();
                for(int i=0; i<calendarDays.size(); i++){
                    dates.add(calendarDays.get(i).getDate().toString());
                }
                ArrayList <String> array2 = new ArrayList<>();
                AlertDialog.Builder builder = new AlertDialog.Builder(plus.getContext());
                builder.setTitle("카테고리를 선택하세요");
                builder.setMultiChoiceItems(arr, null, new DialogInterface.OnMultiChoiceClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int index, boolean isChecked) {
                        if (isChecked){
                            array2.add(arr[index]);
                        }
                        else if(array2.contains(index)) {
                        array2.remove(arr[index]);
                        }
                    }
                });

                //OK이벤트
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    String ct;
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        for(int i=0; i<array2.size(); i++){
                            ct = array2.get(i);
                            for(int x=0; x<dates.size(); x++) {
                                String Date = dates.get(x);

                                mRealm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {

                                        Connection D2C = realm.createObject(Connection.class);
                                        D2C.setDate(Date);
                                        D2C.setName(ct);
                                    }
                                });
                            }
                        }
                        Toast.makeText(getApplicationContext(),ct+"카테고리에 추가되었습니다" , Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    private void preftoArray() {
        String json = sharedPref.getString("x", "");
        Gson gson = new GsonBuilder().create();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    Data orderData = gson.fromJson(a.get(i).toString(), Data.class);
                    list_ct.add(orderData.getTitle());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view){ //클릭 이벤트 정리.
        if(view == exitSelecting) {
            exitSelecting.setVisibility(View.GONE);
            dateselect.setVisibility(View.VISIBLE);
            plus.setVisibility(View.GONE);
            inSelectionMode = false;
            calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        } else if(view == tohome) {
            finish();
        } else if(view == tocontent) {
            Intent tocontent = new Intent(Calendar.this, Contents.class);
            tocontent.putExtra("Date", selectedDate);
            startActivity(tocontent);
        }
    }
//    public static class EventDecorator implements DayViewDecorator {
//        private final int color = Color.parseColor("#FF3366");
//        private final CalendarDay dates;
//
//        public EventDecorator(CalendarDay dates) {
//            this.dates = dates;
//        }
//
//        @Override
//        public boolean shouldDecorate(CalendarDay day) {
//            return dates==day;
//        }
//
//        @Override
//        public void decorate(DayViewFacade view) {
//            view.addSpan(new DotSpan(8, color));
//        }
//    }

}