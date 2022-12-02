package com.example.diary;

import io.realm.RealmObject;

public class Connection extends RealmObject {
    String Name="";
    String Date="";

    public void setName(String Name){
        this.Name = Name;
    }

    public void setDate(String Date){
        this.Date= Date;
    }

    public String getName() {
        return Name;
    }

    public String getDate() {
        return Date;
    }
}
