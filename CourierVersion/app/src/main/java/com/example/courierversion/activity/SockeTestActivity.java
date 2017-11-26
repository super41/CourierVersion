package com.example.courierversion.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.courierversion.R;
import com.example.courierversion.Util.SocketUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketImpl;
import java.net.SocketTimeoutException;

/**
 * Created by XJP on 2017/11/16.
 */
public class SockeTestActivity extends AppCompatActivity implements SocketUtil.SocketImp{

    Button btn_send;
    TextView tv_result;
    EditText et_input;
    Button btn_connect;
    SocketUtil socketUtil;

    final String TAG = "xjp";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sk_test);
        btn_send = (Button) findViewById(R.id.btn_send);
        tv_result = (TextView) findViewById(R.id.tv_result);
        et_input = (EditText) findViewById(R.id.et_input);
        btn_connect = (Button) findViewById(R.id.btn_connect);
        socketUtil=new SocketUtil(this,this);


        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               socketUtil.connect();
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_input.getText().length()>0)
                socketUtil.send(et_input.getText().toString());
            }
        });

    }


    @Override
    public void onSuccess() {
            et_input.append("suc  ");
        Log.d(TAG, "onSuccess: ");
    }

    @Override
    public void onTimeout() {
        et_input.append("time out  ");
        Log.d(TAG, "onTimeout: ");
    }

    @Override
    public void onFail() {
        et_input.append("time fail  ");
        Log.d(TAG, "onFail: ");
    }

    @Override
    public void onResult(byte[] s) {

        //Log.d(TAG, "onResult: "+s);
    }
}
