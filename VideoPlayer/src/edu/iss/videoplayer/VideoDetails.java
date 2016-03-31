package edu.iss.videoplayer;

import activity.listview.app.AppController;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import roboguice.activity.RoboTabActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/3/26
 * Time: 10:16
 * Description: 视频详情页面,包括简介 笔记 评论 播放入口
 */
@ContentView(R.layout.video_details)
public class VideoDetails extends RoboTabActivity {
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private String playurl;
    private TabHost tabHost;
    @InjectView(R.id.back_live)
    private ImageButton backlive;
    @InjectView(R.id.mark)
    private ImageButton mark;
    @InjectView(R.id.video_play)
    private ImageButton videoplay;
    @InjectView(R.id.video_img)
    private NetworkImageView videoimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String imgurl = intent.getStringExtra("imgurl");
        String id = intent.getStringExtra("id");
        playurl = intent.getStringExtra("link");
        videoimg.setImageUrl(imgurl, imageLoader);
        tabHost = getTabHost();
        //章节tab
        Intent chaptersIntent = new Intent();
        chaptersIntent.setClass(VideoDetails.this, Chapters.class);
        chaptersIntent.putExtra("id", id);
        tabHost.addTab(tabHost.newTabSpec("chapter").setIndicator("章节").setContent(chaptersIntent));
        //评论tab
        Intent commentsIntent = new Intent();
        commentsIntent.setClass(VideoDetails.this, Comments.class);
        commentsIntent.putExtra("id", id);
        tabHost.addTab(tabHost.newTabSpec("comments").setIndicator("评论").setContent(commentsIntent));
        //初始化
        updateTab(tabHost);
        //绑定监听器
        tabHost.setOnTabChangedListener(new OnTabChangedListener());

        backlive.setOnClickListener(new BackLiveOnClickListener());
        videoplay.setOnClickListener(new VideoPlayOnClickListener());
        mark.setOnClickListener(new MarkOnClickListener());

    }

    private void updateTab(TabHost tabHost) {
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            View view = tabHost.getTabWidget().getChildAt(i);
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextSize(16);
            if (tabHost.getCurrentTab() == i) {//选中
                tv.setTextColor(this.getResources().getColorStateList(android.R.color.black));
            } else {//不选中
                tv.setTextColor(this.getResources().getColorStateList(android.R.color.darker_gray));
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

    private class BackLiveOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class VideoPlayOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO: 16/3/26 视频播放入口
            Intent intent = new Intent();
            intent.setClass(VideoDetails.this, VideoPlayer.class);
            intent.putExtra("Path", playurl);
            startActivity(intent);
        }
    }

    private class MarkOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO: 16/3/26 标记为喜欢
        }
    }
}
