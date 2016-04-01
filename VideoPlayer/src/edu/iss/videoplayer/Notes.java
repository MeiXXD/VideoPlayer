package edu.iss.videoplayer;

import activity.listview.app.AppController;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.*;
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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
    //录音状态标识
    private static final int RECORE_BEFORE = 1;
    private static final int RECORE_AFTER = 2;
    //上传是否成功
    private static boolean isUploadSuccess = false;
    private static String filePath = "";
    //语音文件上传接口
    private final String URL_UPLOAD_VOICE_NOTE = Constants.SERVER + Constants.UPLOADAMRFILE;
    //笔记提交接口地址
    private final String URL_COMMIT_NOTE = Constants.SERVER + Constants.COMMITNOTES;
    //最新笔记名
    private String noteFileName;
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
    @InjectView(R.id.note_video_title)
    private TextView notevideotitle;
    @InjectView(R.id.note_time)
    private TextView notetime;
    @InjectView(R.id.voice_note_info)
    private TextView voicenoteinfo;
    private Dialog dialog;
    //录音前的笔记列表
    private ArrayList<String> beforeNoteList;
    //录音后的笔记列表
    private ArrayList<String> afterNoteList;
    //新增笔记的处理
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case RECORE_BEFORE:
                    //获取最开始的笔记列表
                    beforeNoteList = getNoteList();
                    break;
                case RECORE_AFTER:
                    afterNoteList = getNoteList();
                    ArrayList<String> newAddedNote = getDifferentFileList(afterNoteList, beforeNoteList);
                    if (newAddedNote.size() > 0) {
                        noteFileName = new File(newAddedNote.get(0)).getName();
                        voicenoteinfo.setText("语音笔记: " + noteFileName);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 根据流返回一个字符串信息         *
     *
     * @param is
     * @return
     * @throws IOException
     */
    private static String getStringFromInputStream(InputStream is)
            throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 模板代码 必须熟练
        byte[] buffer = new byte[1024];
        int len = -1;
        // 一定要写len=is.read(buffer)
        // 如果while((is.read(buffer))!=-1)则无法将数据写入buffer中
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        is.close();
        String state = os.toString();// 把流中的数据转换成字符串,采用的编码是utf-8(模拟器默认编码)
        os.close();
        return state;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(this);
        dialog.setTitle("上传中...");
        dialog.setOnDismissListener(new DismissListener());
        // 记录笔记
        btnrecord.setAudioRecord(new AudioRecorder(mHandler));
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //获取添加笔记的课程的相关信息
        pos = String.valueOf(bundle.getLong("pos"));
        title = bundle.getString("title");
        id = bundle.getString("id");
        playUrl = bundle.getString("playUrl");
        notevideotitle.setText(title);
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
            final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL_COMMIT_NOTE, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) throws JSONException {
                    isUploadSuccess = response.getString("flag").equalsIgnoreCase("Success");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_COMMIT_NOTE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) throws JSONException {
                JSONObject object = new JSONObject(response.toString());
                Log.e("11111", response.toString());
                isUploadSuccess = object.getString("flag").equalsIgnoreCase("Success");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isUploadSuccess = false;
                Log.e("11111", error.toString());
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
    }

    /**
     * 上传语音笔记
     */
    private void uploadVoiceFile(String noteFileName) {
        String srcPath = Environment.getExternalStorageDirectory().getPath() + Constants.VOICE_NOTE_DIRECTORY + "/" + noteFileName;
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(URL_UPLOAD_VOICE_NOTE);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            // 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
            // 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。
            httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
            // 允许输入输出流
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            // 使用POST方法
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(
                    httpURLConnection.getOutputStream());

            //添加文件
            dos.writeBytes(twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""
                    + srcPath.substring(srcPath.lastIndexOf("/") + 1)
                    + "\""
                    + end);
            dos.writeBytes(end);
            FileInputStream fis = new FileInputStream(srcPath);
            byte[] buffer = new byte[8192]; // 8k
            int count = 0;
            // 读取文件
            while ((count = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, count);
            }
            fis.close();

            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();
            InputStream is = httpURLConnection.getInputStream();
            String result = getStringFromInputStream(is);
            dos.close();
            is.close();
            //解析结果
            JSONObject jsonObject = new JSONObject(result);
            isUploadSuccess = jsonObject.getString("flag").equalsIgnoreCase("Success");
            if (isUploadSuccess) {
                filePath = jsonObject.getJSONObject("data").getString("message");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != httpURLConnection)
                httpURLConnection.disconnect();
        }
    }

    /**
     * 得到笔记列表
     */
    private ArrayList<String> getNoteList() {
        ArrayList<String> noteList = new ArrayList<String>();
        // 得到sd卡内路径
        String notePath = Environment.getExternalStorageDirectory().toString() + Constants.VOICE_NOTE_DIRECTORY;
        // 得到该路径文件夹下所有的文件
        File mfile = new File(notePath);
        File[] files = mfile.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            noteList.add(file.getPath());
        }
        return noteList;
    }

    /**
     * 获取新增的文件列表
     */
    private ArrayList<String> getDifferentFileList(ArrayList<String> newFileList, ArrayList<String> existFileList) {
        ArrayList<String> differentFileList = null;
        if (null == newFileList || newFileList.size() == 0) {
            return differentFileList;
        }
        differentFileList = new ArrayList<String>();
        boolean isExist = false;
        if (null == existFileList) {
            // 如果已存在文件为空，那肯定是全部加进来啦。
            for (String newFilePath : newFileList) {
                differentFileList.add(newFilePath);
            }
        } else {
            for (String newFilePath : newFileList) {
                isExist = false;
                for (String existFilePath : existFileList) {
                    if (existFilePath.equals(newFilePath)) {
                        isExist = true;
                        break;
                    }
                }
                if (!isExist) {
                    differentFileList.add(newFilePath);
                }
            }
        }
        return differentFileList;
    }

    //笔记提交
    private class SubmitNotesOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //笔记内容
            noteContentString = notecontent.getText().toString();
            Bundle bundle = new Bundle();
            bundle.putString("course_id", id);
            bundle.putString("user_id", "1");
            if (voicenoteinfo.getText().length() == 0) {
                bundle.putString("type", TYPE_NOTE_TEXT);
            } else {
                bundle.putString("type", TYPE_NOTE_VOICE);
            }
            new UpLoadNote().execute(bundle);
        }
    }

    private class DismissListener implements DialogInterface.OnDismissListener {
        @Override
        public void onDismiss(DialogInterface dialog) {
//            Intent intent = new Intent();
//            intent.setClass(Notes.this, VideoPlayer.class);
//            intent.putExtra("Path", playUrl);
//            intent.putExtra("StartPosition", pos);
//            intent.putExtra("Title", title);
//            intent.putExtra("Id", id);
//            startActivity(intent);
//            finish();
        }
    }

    private class UpLoadNote extends AsyncTask<Bundle, Void, Boolean> {
        private int type;

        @Override
        protected void onPostExecute(Boolean s) {
            super.onPostExecute(s);
            dialog.dismiss();
            if (type == 2) {
                voicenoteinfo.setText(voicenoteinfo.getText() + "  上传成功!");
            }
            isUploadSuccess = false;
            filePath = "";
        }

        @Override
        protected Boolean doInBackground(Bundle... params) {
            try {
                //通过post方式,发送map数据给服务器
                type = Integer.valueOf(params[0].getString("type"));
                switch (type) {
                    case 1:
                        params[0].putString("note", noteContentString);
                        uploadNoteByMap(params[0]);
                        break;
                    case 2:
                        uploadVoiceFile(noteFileName);
                        if (filePath.length() != 0) {
                            params[0].putString("note", filePath);
                        }
                        uploadNoteByMap(params[0]);
                        break;
                    default:
                        break;
                }
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
