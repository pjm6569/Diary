package com.example.diary;

import android.graphics.Bitmap;

import io.realm.RealmObject;

public class Contents_Data extends RealmObject {
    private String Date;
    private String Title;
    private String Content1;
    private String Content2;

    public void setDate(String date) {
        Date=date;
    }

    public void setTitle(String title) {
        Title=title;
    }

    public void setContent1(String content1) {
        Content1=content1;
    }

    public void setContent2(String content2) {
        Content2=content2;
    }


}
