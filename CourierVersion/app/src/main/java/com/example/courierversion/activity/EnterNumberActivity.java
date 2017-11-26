package com.example.courierversion.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.courierversion.PublicDefine;
import com.example.courierversion.R;
import com.example.courierversion.view.TopBar;
import com.example.courierversion.zxing.android.CaptureActivity;
import com.example.courierversion.zxing.android.Intents;
import com.jungly.gridpasswordview.GridPasswordView;

/**
 * Created by XJP on 2017/11/10.
 */
public class EnterNumberActivity extends AppCompatActivity {

    private final  String TAG=EnterNumberActivity.this.getClass().getSimpleName();
    private static final int SCANNING_CODE = 1;
    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final String DECODED_BITMAP_KEY = "codedBitmap";



    GridPasswordView pswView;

    TextView tv_hint;
    Button btn_right;
    Handler handler;
    final int DELAY_SHOW=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_num);
        initView();
        handler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case DELAY_SHOW:
                        Log.d(TAG, "handleMessage: get");
                        String s=getResources().getString(R.string.OK);
                        int cur= (int) msg.obj;
                        Log.d(TAG, "handleMessage: obj"+cur);
                        if(cur>0){
                            btn_right.setText(s+"("+cur+")");
                            cur--;
                            handler.sendMessageDelayed(handler.obtainMessage(DELAY_SHOW,cur),1000);
                        }else{
                            btn_right.setText(s);
                            btn_right.setEnabled(true);
                            btn_right.setAlpha(1.0f);
                        }
                        break;
                    default:
                        Log.d(TAG, "handleMessage: defualt");
                }

            }
        };


    }

    public void beginScan() {
        Intent intent = new Intent(EnterNumberActivity.this,
                CaptureActivity.class);
        startActivityForResult(intent, SCANNING_CODE);

    }





    public void initView() {

        pswView= (GridPasswordView) findViewById(R.id.pswView);
        pswView.setPasswordVisibility(true);
        tv_hint= (TextView) findViewById(R.id.tv_hint);

        btn_right= (Button) findViewById(R.id.btn_rights);
        pswView.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
            //正在输入密码时执行此方法
            public void onTextChanged(String psw) {
                String hint_writing=getResources().getString(R.string.please_write);
                String s=getResources().getString(R.string.OK);
                tv_hint.setTextColor(getResources().getColor(R.color.gray));
                tv_hint.setText(hint_writing);
                btn_right.setEnabled(false);
                btn_right.setAlpha(0.5f);
                btn_right.setText(s);
                handler.removeMessages(DELAY_SHOW);
            }
            //输入密码完成时执行此方法
            public void onInputFinish(String psw) {
                String hint_ensure=getResources().getString(R.string.please_ensure);
                tv_hint.setTextColor(getResources().getColor(R.color.green));
                tv_hint.setText(R.string.please_ensure);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pswView.getWindowToken(), 0); //强制隐藏键盘
                handler.sendMessage(handler.obtainMessage(DELAY_SHOW,2));

            }
        });

        btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(pswView.getPassWord()) || pswView.getPassWord().length()<8 ) {
                    return;
                }
                Intent intent=new Intent(EnterNumberActivity.this, WifiConnectActivity.class);
                intent.putExtra("qr_code",pswView.getPassWord());
                startActivity(intent);
                finish();
            }
        });




    }
}