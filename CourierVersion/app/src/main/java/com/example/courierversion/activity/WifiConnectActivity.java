package com.example.courierversion.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.courierversion.PublicDefine;
import com.example.courierversion.R;
import com.example.courierversion.Util.SocketUtil;
import com.example.courierversion.Util.WifiConnect;
import com.example.courierversion.Util.WifiUtil;

/**
 * Created by XJP on 2017/11/14.
 */
public class WifiConnectActivity extends AppCompatActivity implements SocketUtil.SocketImp {
    WifiReceiver wifiReceiver;
    WifiUtil wifiUtil;

    TextView tv_status;
    ImageView img_wifi;

    HandlerThread handlerThread;
    Handler handler;
    public final int MSG_CONNECT = 0;


    //Socket
    SocketUtil socketUtil;
    Button btn_send;
    Button btn_connect;
    TextView tv_socket;
    TextView tv_send;
    TextView tv_qrcode;

    public final String TAG = this.getClass().getSimpleName();
    public final String WIFI_STATE_CHANGE_ACTION = WifiManager.WIFI_STATE_CHANGED_ACTION;
    public final String NETWORK_STATE_CHANGED_ACTION = WifiManager.NETWORK_STATE_CHANGED_ACTION;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        initView();

    }

    public void connect() {
        wifiUtil.connect(wifiUtil.createWifiInfo(PublicDefine.SSID, PublicDefine.PASSWORD, PublicDefine.TYPE));
    }

    public void initView() {
        img_wifi = (ImageView) findViewById(R.id.img_wifi);
        tv_status = (TextView) findViewById(R.id.tv_status);
        wifiReceiver = new WifiReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WIFI_STATE_CHANGE_ACTION);
        intentFilter.addAction(NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiReceiver, intentFilter);
        wifiUtil = new WifiUtil(WifiConnectActivity.this);
        handlerThread = new HandlerThread("connect");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_CONNECT:
                        connect();
                        break;
                }
            }
        };

        socketUtil = new SocketUtil(this, this);
        btn_connect = (Button) findViewById(R.id.btn_connect);
        btn_send = (Button) findViewById(R.id.btn_send);
        tv_send = (TextView) findViewById(R.id.tv_send);
        tv_socket = (TextView) findViewById(R.id.tv_socket);
        tv_qrcode = (TextView) findViewById(R.id.tv_qrcode);
        String code = getIntent().getStringExtra("qr_code");
        if (code != null) {
            tv_qrcode.setText("二维码:" + code);
        }

        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.equals("\"brize_box\"",wifiUtil.getSSID())) {
                    Toast.makeText(WifiConnectActivity.this, "请先连接wifi brize_box", Toast.LENGTH_SHORT).show();
                    return;
                }
                socketUtil.connect();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreference = getSharedPreferences(PublicDefine.SP_INFO, MODE_PRIVATE);
                String email = sharedPreference.getString("email", "");
                int length = email.length() + 14 + 4;
                String code = getIntent().getStringExtra("qr_code");
                String s = 0xFF + "" + 0xAA + "" + Integer.toHexString(length) + code + "000000" + email + "" + 0x06 + "" + 0xFF + "" + 0x55;
                socketUtil.send(s);
                byte[] b = s.getBytes();
                tv_send.setText("");
                for (int i = 0; i < b.length; i++) {
                    tv_send.append(b[i] + " ");
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.sendMessage(handler.obtainMessage(MSG_CONNECT));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiReceiver);
    }

    @Override
    public void onSuccess() {
        tv_socket.setText("连接服务端成功");
    }

    @Override
    public void onTimeout() {
        tv_socket.setText("连接超时");
    }

    @Override
    public void onFail() {
        tv_socket.setText("连接失败");
    }

    @Override
    public void onResult(String s) {

    }

    class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {

                case WIFI_STATE_CHANGE_ACTION:
                    int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                    if (wifiState == WifiManager.WIFI_STATE_DISABLING) {
                        Log.i(TAG, "正在关闭");
                    } else if (wifiState == WifiManager.WIFI_STATE_ENABLING) {
                        Log.i(TAG, "正在打开");
                        tv_status.setText("正在打开");
                        img_wifi.setImageLevel(1);
                    } else if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
                        tv_status.setText("关闭");
                        img_wifi.setImageLevel(0);
                        Log.i(TAG, "已经关闭");
                    } else if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                        Log.i(TAG, "已经打开");
                        tv_status.setText("已经打开");
                        img_wifi.setImageLevel(1);
                    } else {
                        Log.i(TAG, "未知状态");
                    }
                    break;
                case NETWORK_STATE_CHANGED_ACTION:
                    NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if (info == null) {
                        return;
                    }
                    NetworkInfo.State state = info.getState();
                    //CONNECTING, CONNECTED, SUSPENDED, DISCONNECTING, DISCONNECTED, UNKNOWN
                    switch (state) {
                        case DISCONNECTING:
                            tv_status.setText("正在断开...");
                            Log.i(TAG, "onReceive: 正在断开");
                            break;
                        case DISCONNECTED:
                            if (wifiUtil.isOpen()) {
                                tv_status.setText("已经打开，未连接");
                                Log.i(TAG, "onReceive: 已经打开,未连接");
                            }
                            break;
                        case CONNECTING:
                            tv_status.setText("正在连接");
                            Log.i(TAG, "onReceive: 正在连接");
                            break;
                        case CONNECTED:
                            tv_status.setText("已连接");
                            Log.i(TAG, "onReceive: " + wifiUtil.getSSID());
                            Log.i(TAG, "onReceive: 已连接");
                            if (TextUtils.equals("\"brize_box\"",wifiUtil.getSSID())){
                                tv_status.setText("已经连接上" + wifiUtil.getSSID());
                                img_wifi.setImageLevel(2);
                            } else {
                                tv_status.setText("连接上了错误的wifi" + wifiUtil.getSSID());
                                img_wifi.setImageLevel(1);
                            }
                            Log.i("xjp", "onReceive: "+wifiUtil.getSSID());
                            break;
                        default:
                            break;
                    }

                default:
                    break;

            }
        }
    }

}
