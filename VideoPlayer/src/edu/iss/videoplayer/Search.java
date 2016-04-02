package edu.iss.videoplayer;

import activity.listview.adater.CustomListAdapter;
import activity.listview.app.AppController;
import activity.listview.model.Video;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import edu.iss.videoplayer.utils.JsonUtils;
import org.json.JSONException;
import org.json.JSONObject;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/3/15
 * Time: 10:18
 * Description: 搜索界面
 */
@ContentView(R.layout.search)
public class Search extends RoboActivity {
    //搜索接口地址
    private static final String URL_SEARCH = Constants.SERVER + Constants.SEARCH_VIDEO;
    @InjectView(R.id.back)
    private ImageButton back;
    @InjectView(R.id.search)
    private ImageButton search;
    @InjectView(R.id.search_edit_text)
    private EditText searchedittext;
    @InjectView(R.id.search_result_list)
    private PullToRefreshListView searchresultlist;
    //用来存储video对象的list
    private ArrayList<Video> searchresultArrayList = new ArrayList<Video>();
    private CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        back.setOnClickListener(new BackOnClickListener());
        search.setOnClickListener(new SearchOnClickListener());
        searchresultlist.setMode(PullToRefreshBase.Mode.DISABLED);
        searchresultlist.setOnItemClickListener(new ItemClickListener());
    }

    /**
     * 关键字搜索视频
     */
    private void getSearchResult(String key) {
        adapter = new CustomListAdapter(this, searchresultArrayList);
        searchresultlist.setAdapter(adapter);
        final JsonObjectRequest request = new JsonObjectRequest(URL_SEARCH + key, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                JsonUtils.JSONObjectTOCategoryResultList(response, searchresultArrayList);
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(request);
    }

    private class BackOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class SearchOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String key = searchedittext.getText().toString().trim();
            if (searchresultArrayList.size() != 0)
                searchresultArrayList.clear();
            getSearchResult(key);
        }
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Video temp = searchresultArrayList.get(position - 1);
            Intent intent = new Intent();
            intent.setClass(Search.this, VideoDetails.class);
            intent.putExtra("id", temp.getId());
            intent.putExtra("imgurl", temp.getThumbnailUrl());
            intent.putExtra("link", temp.getPlayUrl());
            startActivity(intent);
        }
    }
}
