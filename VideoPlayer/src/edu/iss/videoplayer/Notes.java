package edu.iss.videoplayer;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 记录笔记
        btnrecord.setAudioRecord(new AudioRecorder());
    }
}
