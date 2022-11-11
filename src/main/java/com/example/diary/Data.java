package com.example.diary;

public class Data {
    String title;
    String image;

    public Data(String title, String image) {
        this.title = title;
        this.image= image;
    }
    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }
}
