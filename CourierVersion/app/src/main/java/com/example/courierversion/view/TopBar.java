package com.example.courierversion.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.telecom.Call;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.courierversion.R;

/**
 * Created by XJP on 2017/11/5.
 */
public class TopBar extends RelativeLayout {
    Button mBtnLeft;
    Button mBtnRight;
    Button mBtnTitle;
    Boolean mIsLeftbtnShow;
    Boolean mIsRightbtnShow;
    int mRightDrawable;
    String mTitle;

    Call call;

    public TopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        //findView
        LayoutInflater.from(context).inflate(R.layout.topbar, this);
        mBtnLeft = (Button) findViewById(R.id.btn_left);
        mBtnRight = (Button) findViewById(R.id.btn_right);
        mBtnTitle = (Button) findViewById(R.id.btn_title);
        //getAttrs
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                R.styleable.TopBar, 0, 0);
        mIsLeftbtnShow = typedArray.getBoolean(R.styleable.TopBar_isLeftShow, true);
        mIsRightbtnShow = typedArray.getBoolean(R.styleable.TopBar_isRightShow, true);
        mRightDrawable = typedArray.getResourceId(R.styleable.TopBar_rightDrawable, R.mipmap.ic_launcher);
        mTitle=typedArray.getString(R.styleable.TopBar_title);
        //setView
        mBtnTitle.setText(mTitle == null ? "" : mTitle);
        mBtnLeft.setVisibility( mIsLeftbtnShow  ? VISIBLE : GONE);
        mBtnRight.setVisibility(mIsRightbtnShow ? VISIBLE : GONE);
        mBtnRight.setBackgroundResource(mRightDrawable);
        mBtnLeft.setOnClickListener(mOnClickListener);
        mBtnRight.setOnClickListener(mOnClickListener);
        typedArray.recycle();
    }

    OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.btn_left:
                    if (call != null)
                        call.onLeftClick(getContext());
                    else
                        ((Activity) getContext()).finish();
                    break;
                case R.id.btn_right:
                    if (call != null)
                        call.onRightClick();
                default:
                    break;

            }
        }
    };

    public void setCall(Call call) {
        this.call = call;
    }

    public void setLeftText(String s) {
        mBtnLeft.setText(s);
    }

    public void setRightText(String s) {
        mBtnRight.setText(s);
    }

    public void setTitle(String s) {
        mBtnTitle.setText(s);
    }


    public static abstract class Call {
        public void onLeftClick(Context context) {
            ((Activity) context).finish();
        }

        public abstract void onRightClick();
    }
}
