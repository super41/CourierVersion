package com.example.courierversion.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.courierversion.R;
import com.example.courierversion.Util.PermissionUtils;
import com.example.courierversion.view.TopBar;
import com.example.courierversion.zxing.android.CaptureActivity;

/**
 * Created by XJP on 2017/11/10.
 */
public class ScanActivity extends AppCompatActivity {

    private final  String TAG=ScanActivity.this.getClass().getSimpleName();
    private static final int SCANNING_CODE = 1;
    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final String DECODED_BITMAP_KEY = "codedBitmap";
    TextView tv_scanning_result;
    Button btn_scanning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        initView();

    }

    public void beginScan() {
        Intent intent = new Intent(ScanActivity.this,
                CaptureActivity.class);
        startActivityForResult(intent, SCANNING_CODE);

    }





    public void initView() {
        tv_scanning_result = (TextView) findViewById(R.id.tv_scanning_result);
        beginScan();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == SCANNING_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(DECODED_CONTENT_KEY);
                Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);
                tv_scanning_result.setText("扫描结果： " + content);
            }
        }
    }
}