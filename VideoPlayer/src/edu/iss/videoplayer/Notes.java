package edu.iss.videoplayer;

import activity.listview.app.AppController;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import edu.iss.videoplayer.recoder.AudioRecorder;
import edu.iss.videoplayer.recoder.RecordButton;
import io.vov.vitamio.utils.Log;
import org.json.JSONException;
import org.json.JSONObject;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/3/26
 * Time: 10:22
 * Description: 笔记页面
 */
@ContentView(R.layout.notes)
public class Notes extends RoboActivity {
    //笔记类型定义
    private static final String TYPE_NOTE_TEXT = "1";
    private static final String TYPE_NOTE_VOICE = "2";
    //笔记上传接口地址
    private static final String uploadNotesUrl = Constants.SERVER + Constants.UPLOADNOTES;
    //上传是否成功
    private static boolean isUploadSuccess = false;
    @InjectView(R.id.btn_record)
    private RecordButton btnrecord;
    private String noteContentString;
    private String id;
    private String title;
    private String pos;
    private String playUrl;
    @InjectView(R.id.submit_notes)
    private Button submitnotes;
    @InjectView(R.id.note_content)
    private EditText notecontent;
    @InjectView(R.id.note_video_itle)
    private TextView notevideoitle;
    @InjectView(R.id.note_time)
    private TextView notetime;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(this);
        dialog.setTitle("上传中...");
        dialog.setOnDismissListener(new DismissListener());
        // 记录笔记
        btnrecord.setAudioRecord(new AudioRecorder());
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //获取添加笔记的课程的相关信息
        pos = String.valueOf(bundle.getLong("pos"));
        title = bundle.getString("title");
        id = bundle.getString("id");
        playUrl = bundle.getString("playUrl");

        notevideoitle.setText(title);
        notetime.setText("于 " + pos + " 毫秒添加笔记");
        submitnotes.setOnClickListener(new SubmitNotesOnClickListener());
    }

    /**
     * post上传文字笔记,JSONObject数据
     *
     * @param bundle
     */
    private void uploadNoteByJSONObject(Bundle bundle) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("note", bundle.getString("note"));
            jsonObject.put("course_id", bundle.getString("course_id"));
            jsonObject.put("user_id", bundle.getString("user_id"));
            jsonObject.put("type", bundle.getString("type"));
            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, uploadNotesUrl, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) throws JSONException {
                    // TODO: 16/3/31 解析服务器post响应
                    isUploadSuccess = response.getString("flag").equalsIgnoreCase("Success");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("11111", error.toString());
                    isUploadSuccess = false;
                }
            });
            AppController.getInstance().addToRequestQueue(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * post上传文字笔记,map数据
     *
     * @param bundle
     */
    private void uploadNoteByMap(final Bundle bundle) {
        try {
            final StringRequest stringRequest = new StringRequest(Request.Method.POST, uploadNotesUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) throws JSONException {
                    // TODO: 16/3/31 解析服务器post响应
                    Log.e("11111", bundle.toString());
                    Log.e("11111", response.toString());
                    JSONObject object = new JSONObject(response.toString());
                    isUploadSuccess = object.getString("flag").equalsIgnoreCase("Success");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("11111", error.toString());
                    isUploadSuccess = false;
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("note", bundle.getString("note"));
                    map.put("course_id", bundle.getString("course_id"));
                    map.put("user_id", bundle.getString("user_id"));
                    map.put("type", bundle.getString("type"));
                    return map;
                }
            };
            AppController.getInstance().addToRequestQueue(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SubmitNotesOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //笔记内容
            noteContentString = notecontent.getText().toString();
            Bundle bundle = new Bundle();
            bundle.putString("note", noteContentString);
            bundle.putString("course_id", id);
            bundle.putString("user_id", "1");
            bundle.putString("type", TYPE_NOTE_TEXT);
            //bundle.putString("type", TYPE_NOTE_VOICE);
            new UpLoadNote().execute(bundle);
        }
    }

    private class DismissListener implements DialogInterface.OnDismissListener {
        @Override
        public void onDismiss(DialogInterface dialog) {
            Intent intent = new Intent();
            intent.setClass(Notes.this, VideoPlayer.class);
            intent.putExtra("Path", playUrl);
            intent.putExtra("StartPosition", pos);
            intent.putExtra("Title", title);
            intent.putExtra("Id", id);
            startActivity(intent);
            finish();
        }
    }

    private class UpLoadNote extends AsyncTask<Bundle, Void, Boolean> {
        private String flag;

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            dialog.dismiss();
            notecontent.setText(String.valueOf(isUploadSuccess));
            isUploadSuccess = false;
        }

        @Override
        protected Boolean doInBackground(Bundle... params) {
            try {
                //通过post方式,发送map数据给服务器
                uploadNoteByMap(params[0]);
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return isUploadSuccess;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }
    }
}
