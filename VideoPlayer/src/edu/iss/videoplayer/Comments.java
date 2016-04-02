package edu.iss.videoplayer;

import activity.listview.adater.CommentListAdapter;
import activity.listview.app.AppController;
import activity.listview.model.Comment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import edu.iss.videoplayer.utils.JsonUtils;
import edu.iss.videoplayer.utils.NetworkStateService;
import edu.iss.videoplayer.utils.NetworkUtils;
import org.json.JSONException;
import org.json.JSONObject;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/3/26
 * Time: 10:22
 * Description: 评论页面
 */
@ContentView(R.layout.comments)
public class Comments extends RoboActivity implements NetworkStateService.NetEventHandle {
    //网络状态常量
    private static final int NETWORK_MOBILE = 1;
    private static final int NETWORK_WIFI = 2;
    private static final int NO_NETWORK = 0;
    //获取评论地址
    private static final String GET_COMMENTS = Constants.SERVER + Constants.COMMENTS;
    //提交评论地址
    // TODO: 16/4/2  地址不完整
    private static final String URL_SUBMIT_COMMENT = Constants.SERVER;
    //提交是否成功
    private static boolean isSubmitSuccess = false;
    @InjectView(R.id.commentslist)
    private PullToRefreshListView commentslist;
    @InjectView(R.id.comment)
    private EditText comment;
    @InjectView(R.id.comment_commit)
    private Button commentcommit;
    @InjectExtra("id")
    private String video_id;
    private CommentListAdapter adapter;
    private ArrayList<Comment> comments = new ArrayList<Comment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //添加到网络状态通知列表
        NetworkStateService.ehList.add(this);
        //刷新设置
        if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
            commentslist.setMode(PullToRefreshBase.Mode.BOTH);
        } else {
            commentslist.setMode(PullToRefreshBase.Mode.DISABLED);
        }
        commentcommit.setOnClickListener(new CommentCommitOnClickListerner());
        commentslistInit(video_id);
        commentslist.setOnRefreshListener(new PullToRefreshListener());
    }

    /**
     * 评论列表初始化
     */
    private void commentslistInit(String id) {
        adapter = new CommentListAdapter(this, comments);
        commentslist.setAdapter(adapter);
        final JsonObjectRequest request = new JsonObjectRequest(GET_COMMENTS + id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) throws JSONException {
                JsonUtils.JSONObjectTOCommentList(response, comments);
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
                commentslist.setMode(PullToRefreshBase.Mode.BOTH);
                break;
            default:
                commentslist.setMode(PullToRefreshBase.Mode.DISABLED);
                break;
        }
    }

    /**
     * post上传评论,map数据
     */
    private void submitNoteByMap(final Bundle bundle) {
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SUBMIT_COMMENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) throws JSONException {
                JSONObject object = new JSONObject(response.toString());
                isSubmitSuccess = object.getString("flag").equalsIgnoreCase("Success");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isSubmitSuccess = false;
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", bundle.getString("id"));
                map.put("user_id", bundle.getString("user_id"));
                map.put("comment", bundle.getString("comment"));
                return map;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    /**
     * 添加评论
     */
    private class CommentCommitOnClickListerner implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String temp = comment.getText().toString().trim();
            // TODO: 16/4/2 评论提交
            Bundle bundle = new Bundle();
            bundle.putString("id", video_id);
            bundle.putString("comment", temp);
            bundle.putString("user_id", "1");
            //异步任务执行
            new CommitComment().execute(bundle);
        }
    }

    //异步任务提交笔记
    private class CommitComment extends AsyncTask<Bundle, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (isSubmitSuccess == true) {
                if (comments.size() != 0) {
                    comments.clear();
                }
                commentslistInit(video_id);
            }
            isSubmitSuccess = false;
        }

        @Override
        protected Void doInBackground(Bundle... params) {
            submitNoteByMap(params[0]);
            return null;
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
                comments.clear();
                final JsonObjectRequest request = new JsonObjectRequest(GET_COMMENTS + video_id, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) throws JSONException {
                        JsonUtils.JSONObjectTOCommentList(response, comments);
                        adapter.notifyDataSetChanged();
                        commentslist.onRefreshComplete();
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
