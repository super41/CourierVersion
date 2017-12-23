package com.example.courierversion.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
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
import android.widget.LinearLayout;
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



    //二维码显示相关
    TextView tv_qrcode;

    //wifi显示相关
    TextView tv_status;
    ImageView img_wifi;

    //Socket显示相关
    LinearLayout ll_socket;
    TextView tv_socket;
    TextView tv_send;
    TextView tv_receive;

    //WIFI工具类
    WifiUtil wifiUtil;
    //wifi连接工作线程
    HandlerThread handlerThread;
    Handler handler;
    Handler mMainHanler;
    public final int MSG_CONNECT = 0;
    public final int MSG_DELAYSHOW = 1;
    //广播接收
    WifiReceiver wifiReceiver;

    //Socket工具类
    SocketUtil socketUtil;

    //最下面的重试按钮
    Button btn_retry;
    ConnectivityManager connec;

    //wifi 状态信息
    int wifi_close=R.string.wifi_close;
    int wifi_opening=R.string.wifi_opening;
    int wifi_already_open=R.string.wifi_already_open;
    int wifi_disconnecting=R.string.wifi_disconnecting;
    int wifi_open_not_connect=R.string.wifi_open_not_connected;
    int wifi_connected_wrong=R.string.wifi_connected_wrong;
    int wifi_connected_right=R.string.wifi_connected_right;
    int wifi_connected=R.string.wifi_connected;
    int wifi_connecting=R.string.wifi_connecting;

    ProgressDialog p;

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
        p=new ProgressDialog(this);
        p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        p.setCancelable(false);
        p.setMessage(getString(R.string.connect_package));
        connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        img_wifi = (ImageView) findViewById(R.id.img_wifi);
        tv_status = (TextView) findViewById(R.id.tv_status);
        ll_socket= (LinearLayout) findViewById(R.id.ll_socket);
        btn_retry= (Button) findViewById(R.id.btn_retry);
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

        mMainHanler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_DELAYSHOW:
                        btn_retry.setAlpha(1.0f);
                        btn_retry.setEnabled(true);
                        p.dismiss();
                        break;
                }
            }
        };

        socketUtil = new SocketUtil(this, this);

        tv_send = (TextView) findViewById(R.id.tv_send);
        tv_receive= (TextView) findViewById(R.id.tv_receive);
        tv_socket = (TextView) findViewById(R.id.tv_socket);
        tv_qrcode = (TextView) findViewById(R.id.tv_qrcode);
        String code = getIntent().getStringExtra("qr_code");
        if (code != null) {
            tv_qrcode.setText(code);
        }


        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果没有连接上目标wifi,请尝试重连
               /* if (!TextUtils.equals("\"brize_box\"",wifiUtil.getSSID())){
                    wifiUtil.removeNowConnectingID();
                    handler.sendMessage(handler.obtainMessage(MSG_CONNECT));
                }else {
                    //连上了，则建立连接
                    socketUtil.connect();
                }*/

                if ((connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
                        &&
                        TextUtils.equals("\"brize_box\"", wifiUtil.getSSID())
                        ){
                    p.setMessage(getString(R.string.connect_package));
                    p.show();
                    btn_retry.setAlpha(0.4f);
                    btn_retry.setEnabled(false);
                    mMainHanler.removeMessages(MSG_DELAYSHOW);
                    mMainHanler.sendMessageDelayed(handler.obtainMessage(MSG_DELAYSHOW), 5000);
                    socketUtil.connect();
                }else{
                    Log.d(TAG, "onClick: connecting wifi ...");
                    wifiUtil.removeNowConnectingID();
                    handler.removeMessages(MSG_CONNECT);
                    handler.sendMessageDelayed(handler.obtainMessage(MSG_CONNECT),300);
                }

            }
        });

    }

   void send() {
        SharedPreferences sharedPreference = getSharedPreferences(PublicDefine.SP_INFO, MODE_PRIVATE);
        String email = sharedPreference.getString("email", "");
        String code = getIntent().getStringExtra("qr_code");
        String s = code + "000000" + email;
        socketUtil.send(s);
        byte[] b = SocketUtil.getByte(s);
        tv_send.setText("");
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hex = hex.toUpperCase();
            tv_send.append(hex + " ");
        }
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
        socketUtil.setDone();
    }

    @Override
    public void onSuccess() {
        tv_socket.setText(R.string.connection_success);


        btn_retry.setEnabled(false);
        btn_retry.setAlpha(0.4f);
        p.setMessage(getString(R.string.loading));
        p.show();
        mMainHanler.removeMessages(MSG_DELAYSHOW);
        mMainHanler.sendMessageDelayed(handler.obtainMessage(MSG_DELAYSHOW), 5000);

        send();
    }

    @Override
    public void onTimeout() {
        tv_socket.setText(R.string.connection_timed_out);
    }

    @Override
    public void onFail() {
        tv_socket.setText(R.string.connection_failed);
    }

    @Override
    public void onResult(byte[] b) {
        tv_receive.setText("");
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hex = hex.toUpperCase();
            tv_receive.append(hex + " ");
        }
        if(checkReply(b)){
            //Toast.makeText(WifiConnectActivity.this, "yes", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(WifiConnectActivity.this,SuccessActivity.class);
            p.dismiss();
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(WifiConnectActivity.this, R.string.t_fail, Toast.LENGTH_SHORT).show();
        }
    }

    boolean checkReply(byte[] b){
        int length=23;
        if(b.length<23){
            return false;
        }
        byte[] trueByte=new byte[length];
        trueByte[0]= (byte) 0xFF;
        trueByte[1]= (byte) 0xAA;
        trueByte[2]= (byte)(length-3);
        trueByte[3]= (byte)0x08;
        String s = getIntent().getStringExtra("qr_code")+"000000";
        char[] c=s.toCharArray();
        for(int i=0;i<s.length();i++){
            trueByte[4+i]=(byte)c[i];
        }
        trueByte[s.length()+4]=(byte)0x4F;
        trueByte[s.length()+5]=(byte)0x4B;
        byte sum=0;
        for(int i=0;i<20;i++){
            sum+=trueByte[i];
        }
        trueByte[s.length()+6]=(byte)sum;
        trueByte[s.length()+7]=(byte)0xFF;
        trueByte[s.length()+8]=(byte)0x55;
        for(int i=0;i<23;i++){
            if(b[i]!=trueByte[i]){
                Log.d(TAG, "checkReply: b"+b[i]+" t "+trueByte[i]);
                return  false;
            }
        }
        return true;
    }

    class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            tv_socket.setText("");
            switch (intent.getAction()) {

                case WIFI_STATE_CHANGE_ACTION:
                    int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                    if (wifiState == WifiManager.WIFI_STATE_DISABLING) {
                        Log.i(TAG, "正在关闭");
                    } else if (wifiState == WifiManager.WIFI_STATE_ENABLING) {
                        Log.i(TAG, "正在打开");
                        tv_status.setText(wifi_opening);
                        img_wifi.setImageLevel(1);
                    } else if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
                        tv_status.setText(wifi_close);
                        img_wifi.setImageLevel(0);
                        Log.i(TAG, "已经关闭");
                    } else if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                        Log.i(TAG, "已经打开");
                        tv_status.setText(wifi_already_open);
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
                            tv_status.setText(wifi_disconnecting);
                            Log.i(TAG, "onReceive: 正在断开");
                            break;
                        case DISCONNECTED:
                            if (wifiUtil.isOpen()) {
                                tv_status.setText(wifi_open_not_connect);
                                Log.i(TAG, "onReceive: 已经打开,未连接");
                            }
                            break;
                        case CONNECTING:
                            tv_status.setText(wifi_connecting);
                            Log.i(TAG, "onReceive: 正在连接");
                            break;
                        case CONNECTED:
                            tv_status.setText(wifi_connected);
                            Log.i(TAG, "onReceive: " + wifiUtil.getSSID());
                            Log.i(TAG, "onReceive: 已连接");
                            if (TextUtils.equals("\"brize_box\"",wifiUtil.getSSID())){
                                tv_status.setText(wifi_connected_right);
                                tv_status.append( wifiUtil.getSSID());
                                img_wifi.setImageLevel(2);
                                ll_socket.setVisibility(View.VISIBLE);

                                btn_retry.setEnabled(false);
                                btn_retry.setAlpha(0.4f);
                                p.setMessage(getString(R.string.connect_package));
                                p.show();
                                mMainHanler.removeMessages(MSG_DELAYSHOW);
                                mMainHanler.sendMessageDelayed(handler.obtainMessage(MSG_DELAYSHOW), 5000);

                                socketUtil.connect();
                            } else {
                                tv_status.setText(wifi_connected_wrong);
                                tv_status.append( wifiUtil.getSSID());
                                img_wifi.setImageLevel(1);
                                //wifiUtil.removeNowConnectingID();
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
