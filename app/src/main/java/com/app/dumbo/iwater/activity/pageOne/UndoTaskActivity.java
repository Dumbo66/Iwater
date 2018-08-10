package com.app.dumbo.iwater.activity.pageOne;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.superClass.AnimFadeActivity;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static android.support.constraint.Constraints.TAG;

/**
 * Created by dumbo on 2017/10/27.
 */

public class UndoTaskActivity extends AnimFadeActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_undo_task);
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
