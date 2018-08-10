package com.app.dumbo.iwater.activity.superClass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;

import com.app.dumbo.iwater.R;

/**
 * 带返回按钮并监听，点击按钮返回
 *
 * Created by dumbo on 2017/10/27.
 */

@SuppressLint("Registered")
public class WithBackActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
