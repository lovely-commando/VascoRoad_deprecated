package com.example.woo.myapplication;

import java.io.Serializable;

public class ListViewItem implements Serializable {
//여기서 할 것: 사진, 이름, 정보가 들어있는 하나의 큰 LinearLayout을 하나의 클래스로 취급할 수 있도록 해준다.

    String name; //이름
    String place; //실종장소
    String timee; //실종시간
    int resId; // 실종자 사진
    String desc; //실종시 특징

    public ListViewItem(String name,String place, String timee) {
        //생성자 1: context 객체를 파라미터로 받는다.
        this.name = name;
        this.place = place;
        this.timee = timee;
    }

    public ListViewItem(String name, String place, String timee, int resId,String desc) {
        this.name = name;
        this.place = place;
        this.timee = timee;
        this.resId = resId;
        this.desc = desc;
    }

    public ListViewItem() {

    }

    public int getResId()
    {
        return resId;
    }

    public void setResId(int resId)
    {
        this.resId = resId;
    }

    public String getPlace()
    {
        return place;
    }
    public void setPlace(String place)
    {
        this.place= place;
    }
    public String getTimee()
    {
        return timee;
    }
    public void setTimee(String timee)
    {
        this.timee = timee;
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}



