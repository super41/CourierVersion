package com.example.courierversion.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.courierversion.R;
import com.example.courierversion.view.TopBar;

/**
 * Created by XJP on 2017/11/26.
 */
public class SuccessActivity extends AppCompatActivity{

    Button btn_finish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        initView();
    }

    public void initView(){
        btn_finish= (Button) findViewById(R.id.btn_finish);
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
