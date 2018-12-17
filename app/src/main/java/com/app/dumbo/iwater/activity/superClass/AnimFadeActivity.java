package com.app.dumbo.iwater.activity.superClass;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.app.dumbo.iwater.R;

/**
 * 退出淡入淡出效果
 *
 * Created by dumbo on 2018/6/23
 **/

@SuppressLint("Registered")
public class AnimFadeActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            }
        });
    }

    /**退出淡入淡出效果*/
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            finish();
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }
        return super.onKeyDown(keyCode, event);
    }
}