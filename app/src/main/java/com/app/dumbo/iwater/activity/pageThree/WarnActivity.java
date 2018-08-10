package com.app.dumbo.iwater.activity.pageThree;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.superClass.WithBackActivity;

/**
 * Created by dumbo on 2018/6/24
 **/

public class WarnActivity extends WithBackActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_warn);
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
