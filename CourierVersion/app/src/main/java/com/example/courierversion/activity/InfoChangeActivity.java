package com.example.courierversion.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.courierversion.PublicDefine;
import com.example.courierversion.R;
import com.example.courierversion.view.TopBar;

/**
 * Created by XJP on 2017/11/19.
 */
public class InfoChangeActivity extends AppCompatActivity {

    EditText et_name;
    EditText et_company;
    EditText et_email;
    ImageButton delete1;
    ImageButton delete2;
    ImageButton delete3;

    Button btn_change;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_change);
        initView();
    }

    public void initView() {

        et_company = (EditText) findViewById(R.id.et_company);
        et_name = (EditText) findViewById(R.id.et_name);
        et_email = (EditText) findViewById(R.id.et_email);
        delete1 = (ImageButton) findViewById(R.id.delete_1);
        delete2 = (ImageButton) findViewById(R.id.delete_2);
        delete3 = (ImageButton) findViewById(R.id.delete_3);
        btn_change = (Button) findViewById(R.id.btn_change);
        loadData();
        et_name.requestFocus();
        delete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_name.setText("");
            }
        });
        delete2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_company.setText("");
            }
        });
        delete3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_email.setText("");
            }
        });
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=et_name.getText().toString();
                String company=et_company.getText().toString();
                String email=et_email.getText().toString();
                if(TextUtils.isEmpty(name.trim())){
                    String hint=getResources().getString(R.string.input_name);
                    Toast.makeText(InfoChangeActivity.this,hint , Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(company.trim())){
                    String hint=getResources().getString(R.string.input_company);
                    Toast.makeText(InfoChangeActivity.this,hint, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(email.trim())){
                    String hint=getResources().getString(R.string.input_email);
                    Toast.makeText(InfoChangeActivity.this,hint, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!isEmail(email)){
                    //String hint=getResources().getString(R.string.input_email);
                    Toast.makeText(InfoChangeActivity.this, R.string.enter_right_email, Toast.LENGTH_SHORT).show();
                    return;
                }
                onSubmit(name,company,email);
                Toast.makeText(InfoChangeActivity.this, R.string.change_info_success, Toast.LENGTH_SHORT).show();
                finish();

            }
        });

    }

    public static boolean isEmail(String strEmail) {
        String strPattern = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        if (TextUtils.isEmpty(strPattern)) {
            return false;
        } else {
            return strEmail.matches(strPattern);
        }
    }

    public void loadData(){
        SharedPreferences sharedPreference=getSharedPreferences(PublicDefine.SP_INFO,MODE_PRIVATE);
        String name=sharedPreference.getString("name","");
        String company=sharedPreference.getString("company","");
        String email=sharedPreference.getString("email","");

        et_name.setText(name);
        et_email.setText(email);
        et_company.setText(company);
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
