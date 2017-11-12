package com.example.courierversion.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.courierversion.PublicDefine;
import com.example.courierversion.R;

import com.example.courierversion.Util.PermissionUtils;
import com.example.courierversion.Util.WifiConnect;
import com.example.courierversion.Util.WifiUtil;
import com.example.courierversion.view.TopBar;

public class MainActivity extends AppCompatActivity {

    TopBar mTopbar;
    Button mBtnScan;
    Button mBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();


    }

    public void initView() {
        mTopbar = (TopBar) findViewById(R.id.topBar);
        mBtnScan= (Button) findViewById(R.id.btn_scan);
        mBtnRegister= (Button) findViewById(R.id.btn_register);
        mTopbar.setTitle("快递员端");
        mTopbar.setCall(new TopBar.Call() {
           @Override
           public void onRightClick() {
               WifiUtil wifiUtil=new WifiUtil(MainActivity.this);
               wifiUtil.connect(wifiUtil.createWifiInfo(PublicDefine.SSID, PublicDefine.PASSWORD,PublicDefine.TYPE));
           }
       });


        //***
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        mBtnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             PermissionUtils.requestPermission(MainActivity.this, PermissionUtils.CODE_CAMERA, mPermissionGrant);

            }
        });
    }

    public void checkIsHaveData(){
        SharedPreferences sharedPreference=getSharedPreferences(PublicDefine.SP_INFO,MODE_PRIVATE);
        String name=sharedPreference.getString("name","");
        String company=sharedPreference.getString("company","");
        String email=sharedPreference.getString("email","");
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(company) || TextUtils.isEmpty(email)){
            mBtnScan.setEnabled(false);
            mBtnScan.setAlpha(0.6f);
        }else{
            mBtnRegister.setVisibility(View.GONE);
            mBtnScan.setAlpha(1.0f);
            mBtnScan.setEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIsHaveData();
    }

    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode){
                case PermissionUtils.CODE_CAMERA:
                    Intent intent=new Intent(MainActivity.this,ScanActivity.class);
                    startActivity(intent);
                    Log.d("xjp", "onPermissionGranted: 1");
                    break;

            }
        }
    };

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }


}
