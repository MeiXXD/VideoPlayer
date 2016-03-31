package edu.iss.videoplayer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import edu.iss.videoplayer.recoder.AudioRecorder;
import edu.iss.videoplayer.recoder.RecordButton;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/3/26
 * Time: 10:22
 * Description: 笔记页面
 */
@ContentView(R.layout.notes)
public class Notes extends RoboActivity {
    @InjectView(R.id.btn_record)
    private RecordButton btnrecord;
    private String id;
    private String title;
    private String pos;
    private String playUrl;
    @InjectView(R.id.submit_notes)
    private Button submitnotes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 记录笔记
        btnrecord.setAudioRecord(new AudioRecorder());
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //获取添加笔记的课程的相关信息
        pos = String.valueOf(bundle.getLong("pos"));
        title = bundle.getString("title");
        id = bundle.getString("id");
        playUrl = bundle.getString("playUrl");
        Log.e("11111", String.valueOf(id));
        Log.e("11111", String.valueOf(pos));
        Log.e("11111", title);
        submitnotes.setOnClickListener(new SubmitNotesOnClickListener());
    }

    private class SubmitNotesOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
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
}
