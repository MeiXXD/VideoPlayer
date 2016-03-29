package edu.iss.videoplayer;

import activity.listview.adater.ChapterListAdapter;
import activity.listview.app.AppController;
import activity.listview.model.Chapter;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import edu.iss.videoplayer.utils.JsonUtils;
import edu.iss.videoplayer.utils.NetworkStateService;
import edu.iss.videoplayer.utils.NetworkUtils;
import org.json.JSONException;
import org.json.JSONObject;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/3/29
 * Time: 11:10
 * Description: 章节
 */
@ContentView(R.layout.chapters)
public class Chapters extends RoboActivity implements NetworkStateService.NetEventHandle {
    //网络状态常量
    private static final int NETWORK_MOBILE = 1;
    private static final int NETWORK_WIFI = 2;
    private static final int NO_NETWORK = 0;
    //章节接口地址
    private final String chaptersUrl = Constants.SERVER + Constants.CHAPTERS;
    //章节列表
    private ArrayList<Chapter> chapters = new ArrayList<Chapter>();
    private ChapterListAdapter adapter;
    private String id;
    @InjectView(R.id.chapter_list)
    private PullToRefreshListView chapterlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        //添加到网络状态通知列表
        NetworkStateService.ehList.add(this);
        //刷新设置
        if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
            chapterlist.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        } else {
            chapterlist.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        id = intent.getStringExtra("id");

        //章节列表初始化
        chapterListInit(Integer.valueOf(id));
        chapterlist.setOnItemClickListener(new ItemClickListener());
        chapterlist.setOnRefreshListener(new PullToRefreshListener());
    }

    private void chapterListInit(int id) {
        adapter = new ChapterListAdapter(this, chapters);
        chapterlist.setAdapter(adapter);

        final JsonObjectRequest request = new JsonObjectRequest(chaptersUrl + String.valueOf(id), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                JsonUtils.JSONObjectTOChapterList(response, chapters);
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(request);
    }

    @Override
    public void netState(int netCode) {
        switch (netCode) {
            case NETWORK_MOBILE:
            case NETWORK_WIFI:
                chapterlist.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                break;
            default://NO_NETWORK
                chapterlist.setMode(PullToRefreshBase.Mode.DISABLED);
                break;
        }
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Chapter chapter = chapters.get(position - 1);
            String link = chapter.getPlayUrl();
            String title = chapter.getTitle();
            String startAt = chapter.getStartTime();
            // TODO: 16/3/29 指定播放位置
            Intent intent = new Intent();
            intent.setClass(Chapters.this, VideoPlayer.class);
            intent.putExtra("Path", link);
            intent.putExtra("StartPosition", startAt);
            startActivity(intent);
        }
    }

    //下拉刷新,上滑刷新
    private class PullToRefreshListener implements PullToRefreshBase.OnRefreshListener2<ListView> {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                chapters.clear();
                final JsonObjectRequest request = new JsonObjectRequest(chaptersUrl + String.valueOf(id), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) throws JSONException {
                        JsonUtils.JSONObjectTOChapterList(response, chapters);
                        adapter.notifyDataSetChanged();
                        chapterlist.onRefreshComplete();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                AppController.getInstance().addToRequestQueue(request);
            } else {
                Toast.makeText(getApplicationContext(), "没有可用网络", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

        }
    }
}
