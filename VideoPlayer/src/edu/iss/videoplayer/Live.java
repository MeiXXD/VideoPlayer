package edu.iss.videoplayer;

import activity.listview.adater.CustomListAdapter;
import activity.listview.app.AppController;
import activity.listview.model.Video;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Cache;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/3/14
 * Time: 16:50
 * Description: 直播界面
 */
@ContentView(R.layout.live)
public class Live extends RoboActivity implements NetworkStateService.NetEventHandle {
    //网络状态常量
    private static final int NETWORK_MOBILE = 1;
    private static final int NETWORK_WIFI = 2;
    private static final int NO_NETWORK = 0;
    // 用来打Log日志的TAG
    private static final String TAG = MainActivity.class.getSimpleName();
    // JSON地址
    private static final String videoListViewUrl = Constants.SERVER + Constants.VIDEOLISTVIEW;
    //页面
    public static int page = 1;
    public static boolean isEnd = false;
    //用来存储video对象的list
    private ArrayList<Video> liveVideoList = new ArrayList<Video>();
    private CustomListAdapter adapter;
    @InjectView(R.id.livelist)
    private PullToRefreshListView livelist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //添加到网络状态通知列表
        NetworkStateService.ehList.add(this);
        //刷新设置
        if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
            livelist.setMode(PullToRefreshBase.Mode.BOTH);
        } else {
            livelist.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        //直播列表初始化
        liveListViewInit(page);
        //listview刷新监听器
        livelist.setOnRefreshListener(new PullToRefreshListener());
        //listview item点击监听器
        livelist.setOnItemClickListener(new ItemClickListener());
    }

    // 视频listview
    private void liveListViewInit(int index) {
        //listview列表显示
        adapter = new CustomListAdapter(this, liveVideoList);
        livelist.setAdapter(adapter);

        final JsonObjectRequest request = new JsonObjectRequest(videoListViewUrl + String.valueOf(index), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                isEnd = JsonUtils.JSONObjectTOVideoList(response, liveVideoList);
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cache cache = AppController.getInstance().getRequestQueue().getCache();
                Cache.Entry entry = cache.get(videoListViewUrl + String.valueOf(1));
                try {
                    String s = new String(entry.data, "UTF-8");
                    JSONObject response = new JSONObject(s);
                    JsonUtils.JSONObjectTOVideoList(response, liveVideoList);
                    adapter.notifyDataSetChanged();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        AppController.getInstance().addToRequestQueue(request);
    }

    @Override
    public void netState(int netInfo) {
        switch (netInfo) {
            case NETWORK_MOBILE:
            case NETWORK_WIFI:
                livelist.setMode(PullToRefreshBase.Mode.BOTH);
                break;
            default://NO_NETWORK
                livelist.setMode(PullToRefreshBase.Mode.DISABLED);
                break;
        }
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Video temp = liveVideoList.get(position - 1);
            Intent intent = new Intent();
            intent.setClass(Live.this, VideoDetails.class);
            intent.putExtra("id", temp.getId());
            intent.putExtra("imgurl", temp.getThumbnailUrl());
            intent.putExtra("link", temp.getPlayUrl());
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
                liveVideoList.clear();
                final JsonObjectRequest request = new JsonObjectRequest(videoListViewUrl + String.valueOf(1), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) throws JSONException {
                        isEnd = JsonUtils.JSONObjectTOVideoList(response, liveVideoList);
                        adapter.notifyDataSetChanged();
                        livelist.onRefreshComplete();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                AppController.getInstance().addToRequestQueue(request);
                page = 1;
            } else {
                Toast.makeText(getApplicationContext(), "没有可用网络", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
                String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                //加载更多
                if (!isEnd) {
                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                    ++page;
                    final JsonObjectRequest request = new JsonObjectRequest(videoListViewUrl + String.valueOf(page), null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) throws JSONException {
                            isEnd = JsonUtils.JSONObjectTOVideoList(response, liveVideoList);
                            adapter.notifyDataSetChanged();
                            livelist.onRefreshComplete();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    AppController.getInstance().addToRequestQueue(request);
                } else {
                    livelist.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            livelist.onRefreshComplete();
                            Toast.makeText(getApplicationContext(), "没有更多", Toast.LENGTH_SHORT).show();
                        }
                    }, 1000);
                }
            } else {
                Toast.makeText(getApplicationContext(), "没有可用网络", Toast.LENGTH_SHORT).show();
            }
        }
    }
}