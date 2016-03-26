package edu.iss.videoplayer;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.google.inject.Inject;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/3/26
 * Time: 13:27
 * Description: 视频播放
 */
@ContentView(R.layout.layout_videoplayer)
@SuppressLint("HandlerLeak")
public class VideoPlayer extends RoboActivity implements OnCompletionListener, OnInfoListener {
    private CalThread calThread;
    @InjectView(R.id.surface_view)
    private VideoView mVideoView;
    @InjectView(R.id.operation_bg)
    private ImageView mOperationBg;
    @InjectView(R.id.operation_percent)
    private ImageView mOperationPercent;
    @InjectView(R.id.operation_volume_brightness)
    private FrameLayout mVolumeBrightnessLayout;
    @InjectView(R.id.video_loading)
    private View mLoadingView;
    @InjectExtra("Path")
    private String mPath;
    //    @InjectExtra("StartPosition")
//    private long mStartPosition;
    private
    @Inject
    AudioManager mAudioManager;
    /**
     * 声音
     */
    private int mMaxVolume;
    /**
     * 当前声音
     */
    private int mVolume = -1;
    /**
     * 当前亮度
     */
    private float mBrightness = -1f;
    /**
     * 当前缩放模式
     */
    private int mLayout = VideoView.VIDEO_LAYOUT_ZOOM;
    private GestureDetector mGestureDetector;
    private MediaController mMediaController;
    /**
     * 是否自动恢复播放，用于自动暂停，恢复播放
     */
    private boolean needResume;
    /**
     * 定时隐藏
     */
    private Handler mDismissHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mVolumeBrightnessLayout.setVisibility(View.GONE);

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Vitamio.isInitialized(this);

        //获取播放地址和标
        mPath = getIntent().getStringExtra("Path");

        //绑定事件
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnInfoListener(this);

        //绑定数据
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        calThread = new CalThread();
        // 启动新线程
        calThread.start();

        //设置显示名称
        mMediaController = new MediaController(this);
        mVideoView.setMediaController(mMediaController);
        mMediaController.setVideoPlaperHandler(calThread.mHandler);

        if (mPath.startsWith("http:")) {
            mVideoView.setVideoURI(Uri.parse(mPath));
        } else {
            mVideoView.setVideoPath(mPath);
        }

//        //判断是否从头开始播放
//        if (0 != mStartPosition) {
//            mVideoView.seekTo(mStartPosition);
//            mVideoView.requestFocus();
//        } else {
//            mVideoView.requestFocus();
//            startPlayer();
//        }
        mVideoView.requestFocus();
        startPlayer();
        mGestureDetector = new GestureDetector(this, new MyGestureListener());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null)
            mVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null)
            mVideoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null)
            mVideoView.stopPlayback();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event))
            return true;

        // 处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                endGesture();
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 手势结束
     */
    private void endGesture() {
        mVolume = -1;
        mBrightness = -1f;

        // 隐藏
        mDismissHandler.removeMessages(0);
        mDismissHandler.sendEmptyMessageDelayed(0, 500);
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0)
                mVolume = 0;

            // 显示
            mOperationBg.setImageResource(R.drawable.video_volumn_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }

        int index = (int) (percent * mMaxVolume) + mVolume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        // 变更进度
        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = findViewById(R.id.operation_full).getLayoutParams().width * index / mMaxVolume;
        mOperationPercent.setLayoutParams(lp);
    }

    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        if (mBrightness < 0) {
            mBrightness = getWindow().getAttributes().screenBrightness;
            if (mBrightness <= 0.00f)
                mBrightness = 0.50f;
            if (mBrightness < 0.01f)
                mBrightness = 0.01f;

            // 显示
            mOperationBg.setImageResource(R.drawable.video_brightness_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }
        WindowManager.LayoutParams lpa = getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f)
            lpa.screenBrightness = 1.0f;
        else if (lpa.screenBrightness < 0.01f)
            lpa.screenBrightness = 0.01f;
        getWindow().setAttributes(lpa);

        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = (int) (findViewById(R.id.operation_full).getLayoutParams().width * lpa.screenBrightness);
        mOperationPercent.setLayoutParams(lp);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mVideoView != null)
            mVideoView.setVideoLayout(mLayout, 0);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCompletion(MediaPlayer player) {
        Log.e("tet", "播放完成");
    }

    private void stopPlayer() {
        if (mVideoView != null)
            mVideoView.pause();
    }

    private void startPlayer() {
        if (mVideoView != null)
            mVideoView.start();
    }

    private boolean isPlaying() {
        return mVideoView != null && mVideoView.isPlaying();
    }

    @Override
    public boolean onInfo(MediaPlayer arg0, int arg1, int down_rate) {
        switch (arg1) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                //缓存，暂停播放
                if (isPlaying()) {
                    stopPlayer();
                    needResume = true;
                }
                mLoadingView.setVisibility(View.VISIBLE);
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                //缓存完成，继续播放
                if (needResume)
                    startPlayer();
                mLoadingView.setVisibility(View.GONE);
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                //显示 下载速度
                Log.e("test", "download rate:" + down_rate);
                //mLoadingPerce.setText("正在缓冲.."+"缓冲完成"+down_rate);
                //mListener.onDownloadRateChanged(arg2);
                break;
        }
        return true;
    }

    // 定义一个线程类
    class CalThread extends Thread {
        public Handler mHandler;

        public void run() {
            Looper.prepare();
            mHandler = new Handler() {
                // 定义处理消息的方法
                @Override
                public void handleMessage(Message msg) {
                    // TODO: 16/3/6  处理视频播放页面的一些按钮时间,包括返回,普清,高清
                    switch (msg.what) {
                        case 0x1:
                            finish();
                            mVideoView.stopPlayback();
                            break;
                        case 0x2:
                            // TODO: 16/3/7 视频清晰度切换问题
//                            mStartPosition = (Long) msg.obj;
//                            long position = mStartPosition + 10000;
//                            Log.d(">>>>>", String.valueOf(position));
//                            mVideoView.changeDefinitionAndResume(position);
                            break;
                        case 0x3:
                            break;
                    }
                }
            };
            Looper.loop();
        }
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

//        /** 双击 */
//        @Override
//        public boolean onDoubleTap(MotionEvent e) {
//            mMediaController.hide();
//            if (mLayout == VideoView.VIDEO_LAYOUT_ZOOM)
//                mLayout = VideoView.VIDEO_LAYOUT_ORIGIN;
//            else
//                mLayout++;
//            if (mVideoView != null)
//                mVideoView.setVideoLayout(mLayout, 0);
//            return true;
//        }

        /**
         * 滑动
         */
        @SuppressWarnings("deprecation")
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //调节屏幕亮度和声音时,隐藏mediacontroller
            mMediaController.hide();
            float mOldX = e1.getX(), mOldY = e1.getY();
            int y = (int) e2.getRawY();
            Display disp = getWindowManager().getDefaultDisplay();
            int windowWidth = disp.getWidth();
            int windowHeight = disp.getHeight();

            if (mOldX > windowWidth * 4.0 / 5)// 右边滑动
                onVolumeSlide((mOldY - y) / windowHeight);
            else if (mOldX < windowWidth / 5.0)// 左边滑动
                onBrightnessSlide((mOldY - y) / windowHeight);

            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }
}