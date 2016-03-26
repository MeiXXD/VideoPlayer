package edu.iss.videoplayer;

import activity.listview.adater.CustomListAdapter;
import activity.listview.app.AppController;
import activity.listview.model.Movie;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
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
import java.util.LinkedList;

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
    //刷新方式
    private static final int PULLDOWNTOREFRESH = 1;
    private static final int PULLUPTOREFRESH = 2;
    // 用来打Log日志的TAG
    private static final String TAG = MainActivity.class.getSimpleName();
    // JSON地址
    private static final String url = "http://api.androidhive.info/json/movies.json";
    // 播放测试地址
    private static final String playUrl = "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8";
    //用来存储Movie对象的list
    private LinkedList<Movie> movieList = new LinkedList<Movie>();
    private CustomListAdapter adapter;
    @InjectView(R.id.livelist)
    private PullToRefreshListView livelist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //添加到网络状态通知列表
        NetworkStateService.ehList.add(this);
        //listview列表显示
        adapter = new CustomListAdapter(this, movieList);
        livelist.setAdapter(adapter);
        //listview刷新监听器
        livelist.setOnRefreshListener(new PullToRefreshListener());
        //listview item点击监听器
        livelist.setOnItemClickListener(new ItemClickListener());
        //刷新设置
        if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
            livelist.setMode(PullToRefreshBase.Mode.BOTH);
        } else {
            livelist.setMode(PullToRefreshBase.Mode.DISABLED);
        }

        // 发送一个Json请求
        final JsonArrayRequest movieReq = new JsonArrayRequest(url, new ResponseListener(), new ErrorResponseListener());
        // 将request添加到requestQueue中
        AppController.getInstance().addToRequestQueue(movieReq);
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

    //JSONArray转换为List
    private void JSONArrayToList(JSONArray response) {
        // 解析json数据
        for (int i = 0; i < response.length(); i++) {
            try {

                JSONObject obj = response.getJSONObject(i);
                Movie movie = new Movie();
                movie.setTitle(obj.getString("title"));
                movie.setThumbnailUrl(obj.getString("image"));
                movie.setRating(((Number) obj.get("rating"))
                        .doubleValue());
                movie.setYear(obj.getInt("releaseYear"));

                // TODO: 16/3/22 测试播放地址
                movie.setPlayUrl(playUrl);

                // Genre是一个json数组
                JSONArray genreArry = obj.getJSONArray("genre");
                ArrayList<String> genre = new ArrayList<String>();
                for (int j = 0; j < genreArry.length(); j++) {
                    genre.add((String) genreArry.get(j));
                }
                movie.setGenre(genre);

                // 将解析好的一个movie对象添加到list中
                movieList.add(movie);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Movie temp = movieList.get(position - 1);
            Intent intent = new Intent();
            intent.setClass(Live.this, VideoDetails.class);
            intent.putExtra("imgurl", temp.getThumbnailUrl());
            startActivity(intent);
        }
    }

    private class ResponseListener implements Response.Listener<JSONArray> {
        @Override
        public void onResponse(JSONArray response) {
            JSONArrayToList(response);
            adapter.notifyDataSetChanged();
        }
    }

    private class ErrorResponseListener implements Response.ErrorListener {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            movieList.clear();
            Cache cache = AppController.getInstance().getRequestQueue().getCache();
            Cache.Entry entry = cache.get(url);
            String s = null;
            try {
                s = new String(entry.data, "UTF-8");
                //JSON缓存了
                Log.e(TAG, s);
                JSONArray response = new JSONArray(s);
                JSONArrayToList(response);
                Log.e(TAG, String.valueOf(movieList.size()));
                adapter.notifyDataSetChanged();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                //执行异步任务
                new GetDataTask().execute(PULLDOWNTOREFRESH);
            } else {
                Toast.makeText(getApplicationContext(), "没有可用网络", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
                    DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
            // Update the LastUpdatedLabel
            refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
            //执行异步任务
            new GetDataTask().execute(PULLUPTOREFRESH);
        }
    }

    private class GetDataTask extends AsyncTask<Integer, Void, LinkedList<Movie>> {

        //后台处理部分
        @Override
        protected LinkedList<Movie> doInBackground(Integer... params) {
            switch (params[0]) {
                case 1:
                    movieList.addFirst(movieList.get(0));
                    break;
                case 2:
                    movieList.addLast(movieList.get(movieList.size() - 1));
            }
            return movieList;
        }

        @Override
        protected void onPostExecute(LinkedList<Movie> result) {
            super.onPostExecute(result);
            adapter.notifyDataSetChanged();
            livelist.onRefreshComplete();
        }
    }
}