package edu.iss.videoplayer;

import activity.listview.adater.CustomListAdapter;
import activity.listview.app.AppController;
import activity.listview.model.Video;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
 * Date: 16/4/2
 * Time: 13:44
 * Description: 相关课程
 */
@ContentView(R.layout.relate_course)
public class RelateCourses extends RoboActivity {
    //相关课程接口地址
    private final String URL_VIDEO_DETAILS = Constants.SERVER + Constants.DETAILS;
    @InjectView(R.id.relate_course_list)
    private PullToRefreshListView relatecourselist;
    private String id;

    //用来存储video对象的list
    private ArrayList<Video> relatedVideoList = new ArrayList<Video>();
    private CustomListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        relatedVideoListInit(id);
        relatecourselist.setMode(PullToRefreshBase.Mode.DISABLED);
        relatecourselist.setOnItemClickListener(new ItemClickListener());
    }

    private void relatedVideoListInit(String id) {
        adapter = new CustomListAdapter(this, relatedVideoList);
        relatecourselist.setAdapter(adapter);

        final JsonObjectRequest request = new JsonObjectRequest(URL_VIDEO_DETAILS + id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                JsonUtils.JSONObjectTORelatedCourseList(response, relatedVideoList);
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(request);
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Video temp = relatedVideoList.get(position - 1);
            Intent intent = new Intent();
            intent.setClass(RelateCourses.this, VideoDetails.class);
            intent.putExtra("id", temp.getId());
            intent.putExtra("imgurl", temp.getThumbnailUrl());
            intent.putExtra("link", temp.getPlayUrl());
            startActivity(intent);
            finish();
        }
    }
}