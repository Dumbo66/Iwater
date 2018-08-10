package com.app.dumbo.iwater.activity.pageOne;

import android.os.Bundle;
import android.view.KeyEvent;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.superClass.AnimFadeActivity;
import com.app.dumbo.iwater.activity.superClass.WithBackActivity;

/**
 * Created by dumbo on 2018/4/13
 **/

public class PolicyActivity extends AnimFadeActivity {
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_policy);
        super.onCreate(savedInstanceState);

        //控件初始化
        initView();

        //控件监听
        listenWidget();
    }

    /**控件初始化*/
    public void initView(){

    }

    /**控件监听*/
    private void listenWidget() {

    }
}
