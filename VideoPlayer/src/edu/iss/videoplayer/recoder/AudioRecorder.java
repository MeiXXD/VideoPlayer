package edu.iss.videoplayer.recoder;

import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/3/14
 * Time: 09:39
 */
public class AudioRecorder implements RecordStrategy {
    private MediaRecorder recorder;
    private String fileName;
    private String fileFolder = Environment.getExternalStorageDirectory()
            .getPath() + "/TestRecord";

    private boolean isRecording = false;

    //录音的准备工作
    @Override
    public void ready() {
        File file = new File(fileFolder);
        if (!file.exists()) {
            file.mkdir();
        }
        fileName = getCurrentDate();
        recorder = new MediaRecorder();
        recorder.setOutputFile(fileFolder + "/" + fileName + ".amr");
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置MediaRecorder的音频源为麦克风
        recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);// 设置MediaRecorder录制的音频格式
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 设置MediaRecorder录制音频的编码为amr
    }

    // 以当前时间作为文件名
    private String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    //开始录音
    @Override
    public void start() {
        if (!isRecording) {
            try {
                recorder.prepare();
                recorder.start();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            isRecording = true;
        }
    }

    //结束录音
    @Override
    public void stop() {
        if (isRecording) {
            recorder.stop();
            recorder.release();
            isRecording = false;
        }
    }

    //录音失败则删除无用文件
    @Override
    public void deleteOldFile() {
        File file = new File(fileFolder + "/" + fileName + ".amr");
        file.deleteOnExit();
    }

    //获取录音音量的大小
    @Override
    public double getAmplitude() {
        if (!isRecording) {
            return 0;
        }
        return recorder.getMaxAmplitude();
    }

    //返回文件路径
    @Override
    public String getFilePath() {
        return fileFolder + "/" + fileName + ".amr";
    }
}
