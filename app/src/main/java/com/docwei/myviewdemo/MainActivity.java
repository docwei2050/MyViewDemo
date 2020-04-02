package com.docwei.myviewdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.docwei.myviewdemo.scroll.MyMultiView;
import com.docwei.myviewdemo.scroll.MyOriginalView;
import com.docwei.myviewdemo.scroll.MyView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyOriginalView containerView = findViewById(R.id.myview);
        addChild2Container(containerView, "单点无scroller/VelocityTracker");
        MyView containerView1 = findViewById(R.id.myview1);
        addChild2Container(containerView1, "单点带scroller/VelocityTracker");
        MyMultiView containerView2 = findViewById(R.id.myview2);
        addChild2Container(containerView2, "多点触控");




    }

    private void addChild2Container(ViewGroup containerView, String name) {
        TextView tv = new TextView(this);
        tv.setBackgroundColor(getResources().getColor(R.color.colorAccent2));
        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        TextView tv2 = new TextView(this);
        tv2.setBackgroundColor(getResources().getColor(R.color.colorAccent3));
        tv2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        TextView tv3 = new TextView(this);
        tv3.setBackgroundColor(getResources().getColor(R.color.colorAccent4));
        tv3.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        tv.setText(name);
        tv2.setText(name);
        tv3.setText(name);
        tv2.setGravity(Gravity.CENTER);
        tv3.setGravity(Gravity.CENTER);
        tv.setGravity(Gravity.CENTER);
        tv2.setTextSize(30);
        tv3.setTextSize(30);
        tv.setTextSize(30);
        tv.setTextColor(Color.WHITE);
        tv2.setTextColor(Color.WHITE);
        tv3.setTextColor(Color.WHITE);

        containerView.addView(tv);
        containerView.addView(tv2);
        containerView.addView(tv3);
    }


    public void draw(View view) {
        Intent intent=new Intent(MainActivity.this,SecondActivity.class);
        startActivity(intent);
    }
}
