package edu.iss.videoplayer.utils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA
 * Author: lifeng
 * Date: 16/3/18
 * Time: 10:33
 * Description: 网络状态广播
 */
public class NetworkStateService extends Service {
    private static final String TAG = "NetworkState";
    //网络状态常量
    private static final int NETWORK_MOBILE = 1;
    private static final int NETWORK_WIFI = 2;
    private static final int NO_NETWORK = 0;

    //状态变化时,通知各个activity
    public static List<NetEventHandle> ehList = new ArrayList<NetEventHandle>();
    private ConnectivityManager connectivityManager;
    private NetworkInfo info;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int netState;
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                Log.d(TAG, "网络状态已经改变");
                connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    String name = info.getTypeName();
                    if (name.equalsIgnoreCase("MOBILE")) {
                        netState = NETWORK_MOBILE;
                    } else {
                        netState = NETWORK_WIFI;
                    }
                    Log.d(TAG, "当前网络名称：" + name);
                    for (NetEventHandle e : ehList) {
                        e.netState(netState);
                    }
                } else {
                    Log.d(TAG, "没有可用网络");
                    Toast.makeText(context, "没有可用网络", Toast.LENGTH_SHORT).show();
                    for (NetEventHandle e : ehList) {
                        e.netState(NO_NETWORK);
                    }
                }
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public interface NetEventHandle {
        /**
         * 网络状态码
         */
        void netState(int netCode);
    }
}