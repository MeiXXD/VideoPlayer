package edu.iss.videoplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/3/15
 * Time: 10:17
 * Description: 分类界面
 */
@ContentView(R.layout.categories)
public class Categories extends RoboActivity {

    @InjectView(R.id.back)
    private ImageButton back;
    @InjectView(R.id.search_in_categories)
    private ImageButton searchincategories;
    @InjectView(R.id.all_courses)
    private Button allcourses;
    @InjectView(R.id.htmlcss)
    private Button htmlcss;
    @InjectView(R.id.jquery)
    private Button jquery;
    @InjectView(R.id.html5)
    private Button html5;
    @InjectView(R.id.nodejs)
    private Button nodejs;
    @InjectView(R.id.webapp)
    private Button webapp;
    @InjectView(R.id.php)
    private Button php;
    @InjectView(R.id.javascript)
    private Button javascript;
    @InjectView(R.id.java)
    private Button java;
    @InjectView(R.id.c)
    private Button c;
    @InjectView(R.id.cpp)
    private Button cpp;
    @InjectView(R.id.go)
    private Button go;
    @InjectView(R.id.csharp)
    private Button csharp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        back.setOnClickListener(new BackOnClickListener());
        searchincategories.setOnClickListener(new SearchInCategoriesOnClickListener());
        setBtnOnClickListener();
    }

    private void setBtnOnClickListener() {
        allcourses.setOnClickListener(new ButtonOnClickListener());
        allcourses.setTag(1);
        htmlcss.setOnClickListener(new ButtonOnClickListener());
        htmlcss.setTag(2);
        jquery.setOnClickListener(new ButtonOnClickListener());
        jquery.setTag(3);
        html5.setOnClickListener(new ButtonOnClickListener());
        html5.setTag(4);
        nodejs.setOnClickListener(new ButtonOnClickListener());
        nodejs.setTag(5);
        webapp.setOnClickListener(new ButtonOnClickListener());
        webapp.setTag(6);
        php.setOnClickListener(new ButtonOnClickListener());
        php.setTag(7);
        javascript.setOnClickListener(new ButtonOnClickListener());
        javascript.setTag(8);
        java.setOnClickListener(new ButtonOnClickListener());
        java.setTag(9);
        c.setOnClickListener(new ButtonOnClickListener());
        c.setTag(10);
        cpp.setOnClickListener(new ButtonOnClickListener());
        cpp.setTag(11);
        go.setOnClickListener(new ButtonOnClickListener());
        go.setTag(12);
        csharp.setOnClickListener(new ButtonOnClickListener());
        csharp.setTag(13);
    }

    class BackOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    class SearchInCategoriesOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(Categories.this, Search.class);
            startActivity(intent);
        }
    }

    class ButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int tag = (Integer) v.getTag();
            switch (tag) {
                case 1://allcourses
                    Toast.makeText(Categories.this, "全部课程", Toast.LENGTH_SHORT).show();
                    break;
                case 2://htmlcss
                    Toast.makeText(Categories.this, "HTML/CSS", Toast.LENGTH_SHORT).show();
                    break;
                case 3://jquery
                    Toast.makeText(Categories.this, "JQuery", Toast.LENGTH_SHORT).show();
                    break;
                case 4://html5
                    Toast.makeText(Categories.this, "HTML5", Toast.LENGTH_SHORT).show();
                    break;
                case 5://nodejs
                    Toast.makeText(Categories.this, "Node.js", Toast.LENGTH_SHORT).show();
                    break;
                case 6://webapp
                    Toast.makeText(Categories.this, "WebApp", Toast.LENGTH_SHORT).show();
                    break;
                case 7://php
                    Toast.makeText(Categories.this, "PHP", Toast.LENGTH_SHORT).show();
                    break;
                case 8://javascript
                    Toast.makeText(Categories.this, "JavaScript", Toast.LENGTH_SHORT).show();
                    break;
                case 9://java
                    Toast.makeText(Categories.this, "JAVA", Toast.LENGTH_SHORT).show();
                    break;
                case 10://c
                    Toast.makeText(Categories.this, "C", Toast.LENGTH_SHORT).show();
                    break;
                case 11://cpp
                    Toast.makeText(Categories.this, "C++", Toast.LENGTH_SHORT).show();
                    break;
                case 12://go
                    Toast.makeText(Categories.this, "GO", Toast.LENGTH_SHORT).show();
                    break;
                case 13://csharp
                    Toast.makeText(Categories.this, "C#", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
