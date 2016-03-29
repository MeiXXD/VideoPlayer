package edu.iss.videoplayer;

import activity.flashview.FlashView;
import activity.flashview.FlashViewVideo;
import activity.flashview.constants.EffectConstants;
import activity.flashview.listener.FlashViewListener;
import activity.listview.adater.CustomListAdapter;
import activity.listview.app.AppController;
import activity.listview.model.Video;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
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
import org.json.JSONArray;
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
 * Time: 17:29
 * Description: 课程界面
 */
@ContentView(R.layout.courses)
public class Courses extends RoboActivity implements NetworkStateService.NetEventHandle {
    //网络状态常量
    private static final int NETWORK_MOBILE = 1;
    private static final int NETWORK_WIFI = 2;
    private static final int NO_NETWORK = 0;
    // 用来打Log日志的TAG
    private static final String TAG = MainActivity.class.getSimpleName();
    // JSON地址
    private static final String videoListViewurl = Constants.SERVER + Constants.VIDEOLISTVIEW;
    private static final String flashViewUrl = Constants.SERVER + Constants.FLASHVIEW;
    //页面
    public static int page = 1;
    public boolean isEnd = false;
    public AppController controller = null;
    @InjectView(R.id.categories)
    private ImageButton categories;
    @InjectView(R.id.search_in_courses)
    private ImageButton search;
    @InjectView(R.id.flash_view)
    private FlashView flashview;
    private ArrayList<String> imageUrls = null;
    //用来存储video对象的list
    private ArrayList<Video> videoList = new ArrayList<Video>();
    private ArrayList<FlashViewVideo> flashViewVideoList = null;
    private CustomListAdapter adapter;
    @InjectView(R.id.list)
    private PullToRefreshListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //监听器
        categories.setOnClickListener(new CategoriesOnClickListener());
        search.setOnClickListener(new SearchOnClickListener());
        //添加到网络状态通知列表
        NetworkStateService.ehList.add(this);
        controller = AppController.getInstance();
        //轮播
        flashviewInit();

        //视频列表
        videoListViewInit(page);
        listView.setOnRefreshListener(new PullToRefreshListener());
        listView.setOnItemClickListener(new ItemClickListener());
    }

    // 轮播
    private void flashviewInit() {
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(flashViewUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "response" + response.toString());
                String flag;
                try {
                    flag = response.getString("flag");
                    if (flag.equalsIgnoreCase("Success")) {
                        JSONArray array = response.getJSONArray("data");
                        flashViewVideoList = new ArrayList<FlashViewVideo>(array.length());
                        imageUrls = new ArrayList<String>(array.length());
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            FlashViewVideo video = new FlashViewVideo();
                            video.setId(jsonObject.getString("id"));
                            video.setImage(Constants.SERVER + jsonObject.getString("image"));
                            video.setLink(jsonObject.getString("link"));
                            int index = Integer.valueOf(jsonObject.getString("image_sort"));
                            imageUrls.add(index - 1, Constants.SERVER + jsonObject.getString("image"));
                            flashViewVideoList.add(index - 1, video);
                        }
                        flashview.setImageUris(imageUrls);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cache cache = controller.getRequestQueue().getCache();
                Cache.Entry entry = cache.get(flashViewUrl);
                try {
                    String s = new String(entry.data, "UTF-8");
                    JSONObject response = new JSONObject(s);
                    if (response.getString("flag").equalsIgnoreCase("Success")) {
                        JSONArray array = response.getJSONArray("data");
                        flashViewVideoList = new ArrayList<FlashViewVideo>(array.length());
                        imageUrls = new ArrayList<String>(array.length());
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            FlashViewVideo video = new FlashViewVideo();
                            video.setId(jsonObject.getString("id"));
                            video.setImage(Constants.SERVER + jsonObject.getString("image"));
                            int index = Integer.valueOf(jsonObject.getString("image_sort"));
                            imageUrls.add(index - 1, Constants.SERVER + jsonObject.getString("image"));
                            flashViewVideoList.add(index - 1, video);
                        }
                        flashview.setImageUris(imageUrls);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        controller.addToRequestQueue(jsonObjectRequest);
        flashview.setEffect(EffectConstants.DEFAULT_EFFECT);//更改图片切换的动画效果
        flashview.setOnPageClickListener(new FlashViewPageClickListener());
    }

    // 视频listview
    private void videoListViewInit(int index) {
        //listview列表显示
        adapter = new CustomListAdapter(this, videoList);
        listView.setAdapter(adapter);

        final JsonObjectRequest request = new JsonObjectRequest(videoListViewurl + String.valueOf(index), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                isEnd = JsonUtils.JSONObjectTOVideoList(response, videoList);
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Cache cache = controller.getRequestQueue().getCache();
                Cache.Entry entry = cache.get(flashViewUrl + String.valueOf(1));
                try {
                    String s = new String(entry.data, "UTF-8");
                    JSONObject response = new JSONObject(s);
                    JsonUtils.JSONObjectTOVideoList(response, videoList);
                    adapter.notifyDataSetChanged();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        controller.getInstance().addToRequestQueue(request);
    }

    /**
     * 网络状态变更通知
     */
    @Override
    public void netState(int netInfo) {
        switch (netInfo) {
            case NETWORK_MOBILE:
            case NETWORK_WIFI:
                listView.setMode(PullToRefreshBase.Mode.BOTH);
                break;
            default://NO_NETWORK
                listView.setMode(PullToRefreshBase.Mode.DISABLED);
                break;
        }
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Video temp = videoList.get(position - 1);
            Intent intent = new Intent();
            intent.setClass(Courses.this, VideoDetails.class);
            intent.putExtra("id", temp.getId());
            intent.putExtra("imgurl", temp.getThumbnailUrl());
            intent.putExtra("link", temp.getPlayUrl());
            startActivity(intent);
        }
    }

    private class FlashViewPageClickListener implements FlashViewListener {
        @Override
        public void onClick(int position) {
            FlashViewVideo flashViewVideo = flashViewVideoList.get(position);
            Intent intent = new Intent();
            intent.setClass(Courses.this, VideoDetails.class);
            intent.putExtra("id", flashViewVideo.getId());
            intent.putExtra("imgurl", flashViewVideo.getImage());
            intent.putExtra("link", flashViewVideo.getLink());
            startActivity(intent);
        }
    }

    private class CategoriesOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(Courses.this, Categories.class);
            startActivity(intent);
        }
    }

    private class SearchOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(Courses.this, Search.class);
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
                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                videoList.clear();
                final JsonObjectRequest request = new JsonObjectRequest(videoListViewurl + String.valueOf(1), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) throws JSONException {
                        isEnd = JsonUtils.JSONObjectTOVideoList(response, videoList);
                        adapter.notifyDataSetChanged();
                        listView.onRefreshComplete();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
                controller.addToRequestQueue(request);
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
                    // Update the LastUpdatedLabel
                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                    ++page;
                    final JsonObjectRequest request = new JsonObjectRequest(videoListViewurl + String.valueOf(page), null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) throws JSONException {
                            isEnd = JsonUtils.JSONObjectTOVideoList(response, videoList);
                            adapter.notifyDataSetChanged();
                            listView.onRefreshComplete();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, error.toString());
                        }
                    });
                    controller.addToRequestQueue(request);
                    if (isEnd) {
                        listView.setMode(PullToRefreshBase.Mode.DISABLED);
                    }
                } else {
                    listView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listView.onRefreshComplete();
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
