package com.example.diary;

import android.graphics.Bitmap;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Contents_Data extends RealmObject {
    private String Date="";
    private String Title="";
    private String txt1="";
    private String txt2="";
    private String txt3="";
    private String img1="";
    private String img2="";


    public void setDate(String date) {
        Date = date;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setTxt1(String text) {
        txt1 = text;
    }

    public void setTxt2(String text) {
        txt2 = text;
    }

    public void setTxt3(String text) {
        txt3 = text;
    }

    public void setImg1(String text) {
        img1 = text;
    }

    public void setImg2(String text) {
        img2 = text;
    }

    public String getDate() {
        return Date;
    }

    public String getTitle() {
        return Title;
    }

    public String getTxt1() {
        return txt1;
    }

    public String getTxt2() {
        return txt2;
    }

    public String getTxt3() {
        return txt3;
    }

    public String getImg1() {
        return img1;
    }

    public String getImg2(){
        return img2;
    }

}
