package edu.iss.videoplayer;

import android.os.Bundle;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/3/14
 * Time: 17:31
 * Description: "我的"界面
 */
@ContentView(R.layout.myinfo)
public class MyInfo extends RoboActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
