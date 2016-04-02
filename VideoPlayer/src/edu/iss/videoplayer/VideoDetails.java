package edu.iss.videoplayer;

import activity.listview.app.AppController;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import org.json.JSONException;
import org.json.JSONObject;
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
    //相关课程接口地址
    private final String URL_VIDEO_DETAILS = Constants.SERVER + Constants.DETAILS;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    //相关课程信息
    private Bundle relateCourses = null;
    private String playurl;
    private TabHost tabHost;
    @InjectView(R.id.back_live)
    private ImageButton backlive;
    @InjectView(R.id.mark)
    private ImageButton mark;
    @InjectView(R.id.video_img)
    private NetworkImageView videoimg;
    @InjectView(R.id.video_name)
    private TextView videoname;
    @InjectView(R.id.video_length)
    private TextView videolength;
    @InjectView(R.id.video_description)
    private TextView videodescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String imgurl = intent.getStringExtra("imgurl");
        String id = intent.getStringExtra("id");
        playurl = intent.getStringExtra("link");
        videoimg.setImageUrl(imgurl, imageLoader);
        //得到视频详情
        videoDetailsInit(id);
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
        //相关课程界面
        Intent relateCourseIntent = new Intent();
        relateCourseIntent.setClass(VideoDetails.this, RelateCourses.class);
        relateCourseIntent.putExtra("id", id);
        tabHost.addTab(tabHost.newTabSpec("relate_course").setIndicator("相关").setContent(relateCourseIntent));
        //初始化
        updateTab(tabHost);
        //绑定监听器
        tabHost.setOnTabChangedListener(new OnTabChangedListener());

        backlive.setOnClickListener(new BackLiveOnClickListener());
        mark.setOnClickListener(new MarkOnClickListener());

    }

    private void videoDetailsInit(String id) {
        relateCourses = new Bundle();
        final JsonObjectRequest request = new JsonObjectRequest(URL_VIDEO_DETAILS + id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                try {
                    if (response.getString("flag").equalsIgnoreCase("Success")) {
                        relateCourses.putString("relateCourses", response.getJSONObject("data").getJSONArray("relate_course").toString());
                        JSONObject temp = response.getJSONObject("data").getJSONArray("course").getJSONObject(0);
                        videoname.setText(temp.getString("title"));
                        videolength.setText(temp.getString("time"));
                        videodescription.setText(temp.getString("content"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(request);
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

    private class MarkOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO: 16/3/26 标记为喜欢
        }
    }
}
