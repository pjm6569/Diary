package com.example.diary;

public class Connection {
    String Name;
    String Date;

    public Connection(String Name, String Date) {
        this.Name = Name;
        this.Date= Date;
    }
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
