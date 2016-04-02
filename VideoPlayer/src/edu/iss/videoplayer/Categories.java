package edu.iss.videoplayer;

import activity.listview.adater.CategoryListAdapter;
import activity.listview.app.AppController;
import activity.listview.model.Category;
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
import roboguice.inject.InjectView;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/3/15
 * Time: 10:17
 * Description: 分类界面
 */
@ContentView(R.layout.categories)
public class Categories extends RoboActivity {
    //分类的网址
    private static final String URL_CATEGORIES = Constants.SERVER + Constants.CATEGORIES;

    @InjectView(R.id.back)
    private ImageButton back;
    @InjectView(R.id.search_in_categories)
    private ImageButton searchincategories;
    @InjectView(R.id.categorieslist)
    private PullToRefreshListView categorieslist;
    private ArrayList<Category> categoryArrayList = new ArrayList<Category>();
    private CategoryListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        back.setOnClickListener(new BackOnClickListener());
        searchincategories.setOnClickListener(new SearchInCategoriesOnClickListener());
        categorieslist.setMode(PullToRefreshBase.Mode.DISABLED);
        categorieslist.setOnItemClickListener(new ItemClickListener());
        categorieslistInit();
    }

    /**
     * 分类列表初始化
     */
    private void categorieslistInit() {
        adapter = new CategoryListAdapter(this, categoryArrayList);
        categorieslist.setAdapter(adapter);
        final JsonObjectRequest request = new JsonObjectRequest(URL_CATEGORIES, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                JsonUtils.JSONObjectTOCategoriesList(response, categoryArrayList);
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(request);
    }

    class BackOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    class SearchInCategoriesOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(Categories.this, Search.class);
            startActivity(intent);
        }
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView textView = (TextView) view.findViewById(R.id.category);
            Category category = categoryArrayList.get(position - 1);
            Intent intent = new Intent();
            intent.putExtra("id", category.getId());
            intent.putExtra("category", textView.getText());
            intent.setClass(Categories.this, CategoryResult.class);
            startActivity(intent);
        }
    }
}
