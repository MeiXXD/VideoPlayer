package edu.iss.videoplayer;

import android.os.Bundle;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/3/26
 * Time: 10:22
 * Description: 笔记页面
 */
@ContentView(R.layout.comments)
public class Comments extends RoboActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
