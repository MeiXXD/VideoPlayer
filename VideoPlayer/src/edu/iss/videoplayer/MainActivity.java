package edu.iss.videoplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import edu.iss.videoplayer.utils.NetworkStateService;
import roboguice.activity.RoboTabActivity;
import roboguice.inject.ContentView;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/3/14
 * Time: 16:03
 * Description: 主界面
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends RoboTabActivity {
    private TabHost tabHost;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tabHost = getTabHost();

        /* 去除标签下方的白线 */
        tabHost.setPadding(tabHost.getLeft(), tabHost.getTop(), tabHost.getRight(), tabHost.getBottom() - 5);

        tabHost.addTab(tabHost.newTabSpec("courses").setIndicator("课程").setContent(new Intent(this, Courses.class)));
        tabHost.addTab(tabHost.newTabSpec("community").setIndicator("社区").setContent(new Intent(this, Community.class)));
        tabHost.addTab(tabHost.newTabSpec("live").setIndicator("直播").setContent(new Intent(this, Live.class)));
        tabHost.addTab(tabHost.newTabSpec("myinfo").setIndicator("我的").setContent(new Intent(this, MyInfo.class)));

        //初始化
        updateTab(tabHost);
        //绑定监听器
        tabHost.setOnTabChangedListener(new OnTabChangedListener());

        //网络状态广播
        intent = new Intent(MainActivity.this, NetworkStateService.class);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intent);
    }

    private void updateTab(TabHost tabHost) {
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            View view = tabHost.getTabWidget().getChildAt(i);
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextSize(16);
            if (tabHost.getCurrentTab() == i) {//选中
                tv.setTextColor(this.getResources().getColorStateList(
                        android.R.color.black));
            } else {//不选中
                tv.setTextColor(this.getResources().getColorStateList(
                        android.R.color.darker_gray));
            }
        }
    }

    //监听器
    private class OnTabChangedListener implements TabHost.OnTabChangeListener {
        @Override
        public void onTabChanged(String tabId) {
            updateTab(tabHost);
        }
    }
}