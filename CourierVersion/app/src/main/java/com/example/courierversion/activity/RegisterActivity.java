package com.example.courierversion.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.courierversion.PublicDefine;
import com.example.courierversion.R;
import com.example.courierversion.view.TopBar;

/**
 * Created by XJP on 2017/11/5.
 */
public class RegisterActivity extends AppCompatActivity {
    TopBar mTopbar;
    Button mBtnFinish;
    TextInputLayout mTLName;
    TextInputLayout mTLCompany;
    TextInputLayout mTLEmail;
    EditText et_name, et_company, et_email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();

    }

    public void initView() {
        mTopbar = (TopBar) findViewById(R.id.topBar);
        mTLName = (TextInputLayout) findViewById(R.id.tl_name);
        mTLCompany = (TextInputLayout) findViewById(R.id.tl_company);
        mTLEmail = (TextInputLayout) findViewById(R.id.tl_email);
        mBtnFinish = (Button) findViewById(R.id.btn_finish);
        et_company = (EditText) findViewById(R.id.et_company);
        et_name = (EditText) findViewById(R.id.et_name);
        et_email = (EditText) findViewById(R.id.et_email);

        mTopbar.setTitle("快递员注册");
        mTopbar.setLeftText("返回");
        //et_name
        et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    mTLName.setError("输入您的用户名");
                    mTLName.setErrorEnabled(true);
                } else {
                    mTLName.setErrorEnabled(false);
                }
            }
        });

        //et_company
        et_company.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    mTLCompany.setError("输入您的公司");
                    mTLCompany.setErrorEnabled(true);
                } else {
                    mTLCompany.setErrorEnabled(false);
                }
            }
        });
        //et_email
        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString().trim())) {
                    mTLEmail.setError("输入您的邮箱");
                    mTLEmail.setErrorEnabled(true);
                } else {
                    mTLEmail.setErrorEnabled(false);
                }
            }
        });

        mBtnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=et_name.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    mTLName.setError("输入您的姓名");
                    return;
                }

                String company=et_company.getText().toString().trim();
                if (TextUtils.isEmpty(company)) {
                    mTLCompany.setError("输入您的公司");
                    return;
                }

                String email=et_email.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    mTLEmail.setError("输入您的邮箱");
                    return;
                }
                Toast.makeText(RegisterActivity.this, "完善资料成功", Toast.LENGTH_SHORT).show();
                onSubmit(name,company,email);
            }
        });
    }

    public void onSubmit(String name,String company,String email) {
        SharedPreferences sharedPreferences = getSharedPreferences(PublicDefine.SP_INFO, Context.MODE_PRIVATE); //私有数据
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putString("name", name);
        editor.putString("company",company);
        editor.putString("email", email);
        editor.commit();//提交修改
    }

}
