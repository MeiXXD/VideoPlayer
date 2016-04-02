package edu.iss.videoplayer;

import activity.listview.adater.CustomListAdapter;
import activity.listview.app.AppController;
import activity.listview.model.Video;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
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
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/4/2
 * Time: 17:45
 * Description: 分类结果页面
 */
@ContentView(R.layout.category_result)
public class CategoryResult extends RoboActivity {
    //分类视频接口地址
    private static final String URL_CATEGORY_RESULT = Constants.SERVER + Constants.CATEGORY_RESULT;
    @InjectView(R.id.category_result_title)
    private TextView categoryresulttitle;
    @InjectView(R.id.category_result_list)
    private PullToRefreshListView categoryresultlist;
    //用来存储video对象的list
    private ArrayList<Video> categoryResultList = new ArrayList<Video>();
    private CustomListAdapter adapter;
    @InjectExtra("id")
    private String categoryId;
    @InjectExtra("category")
    private String category;
    @InjectView(R.id.back_categories)
    private ImageButton backcategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryresulttitle.setText(category);
        categoryresultlist.setMode(PullToRefreshBase.Mode.DISABLED);
        categoryresultlist.setOnItemClickListener(new ItemClickListener());
        backcategories.setOnClickListener(new backCategoriesOnClickListener());
        categoryresultlistInit();
    }

    private void categoryresultlistInit() {
        adapter = new CustomListAdapter(this, categoryResultList);
        categoryresultlist.setAdapter(adapter);
        final JsonObjectRequest request = new JsonObjectRequest(URL_CATEGORY_RESULT + categoryId, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                JsonUtils.JSONObjectTOCategoryResultList(response, categoryResultList);
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
            Video temp = categoryResultList.get(position - 1);
            Intent intent = new Intent();
            intent.setClass(CategoryResult.this, VideoDetails.class);
            intent.putExtra("id", temp.getId());
            intent.putExtra("imgurl", temp.getThumbnailUrl());
            intent.putExtra("link", temp.getPlayUrl());
            startActivity(intent);
        }
    }

    private class backCategoriesOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }
}
