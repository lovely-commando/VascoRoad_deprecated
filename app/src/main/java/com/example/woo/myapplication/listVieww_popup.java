package com.example.woo.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

public class listVieww_popup extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //맞나 모르겠는데 눈치껏 일단 짜봄.....
        setContentView(R.layout.activity_popup);
        ListViewItem selected = (ListViewItem)getIntent().getSerializableExtra("selecteditem");

        ImageView profile = (ImageView)findViewById(R.id.ImageView_person);
        TextView name = (TextView)findViewById(R.id.TextView_Name);
        TextView time = (TextView)findViewById(R.id.TextView_Time);
        TextView place = (TextView)findViewById(R.id.TextView_Place);
        TextView desc = (TextView)findViewById(R.id.TextView_Characteristic);

        //이름 장소 시간 사진 특징
        name.setText((CharSequence)selected.getName());
        time.setText((CharSequence)selected.getTimee());
        place.setText((CharSequence)selected.getPlace());
        desc.setText((CharSequence)selected.getDesc());


    }
}
