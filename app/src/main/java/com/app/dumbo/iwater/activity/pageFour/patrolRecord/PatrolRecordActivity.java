package com.app.dumbo.iwater.activity.pageFour.patrolRecord;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.superClass.WithBackActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dumbo on 2018/6/3
 */

public class PatrolRecordActivity extends WithBackActivity {
    private DatePicker datePicker;
    private ListView listView;

    private Map<String,Object> map;
    private List<Map<String,Object>> list;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_patrol_record);
        super.onCreate(savedInstanceState);

        datePicker=findViewById(R.id.date_picker);
        listView=findViewById(R.id.lv_patrol_record);

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_UP;
            }
        });

        list=new ArrayList<>();
        for(int i=0;i<10;i++){
            map=new HashMap<>();
            map.put("time","2018-6-2");
            map.put("image",R.drawable.aa);
            list.add(map);
        }

        String[] from={"time","image"};
        int[] to={R.id.tv_patrol_record_item,R.id.iv_patrol_record_item};
        SimpleAdapter adapter=new SimpleAdapter
                (PatrolRecordActivity.this,list,R.layout.item_pic_patrol_record,from,to);
        listView.setAdapter(adapter);

        hideDatePickerHeader(PatrolRecordActivity.this,datePicker);
    }

    /*隐藏日历头部*/
    private void hideDatePickerHeader(Context context, DatePicker datePicker) {
        ViewGroup rootView = (ViewGroup) datePicker.getChildAt(0);
        if (rootView == null) {
            return;
        }
        View headerView = rootView .getChildAt(0);
        if (headerView == null) {
            return;
        }
        //5.0+
        int headerId = context.getResources().getIdentifier("day_picker_selector_layout", "id", "android");
        if (headerId == headerView.getId()) {
            headerView.setVisibility(View.GONE);

            ViewGroup.LayoutParams layoutParamsRoot = rootView.getLayoutParams();
            layoutParamsRoot.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            rootView.setLayoutParams(layoutParamsRoot);

            ViewGroup animator = (ViewGroup) rootView.getChildAt(1);
            ViewGroup.LayoutParams layoutParamsAnimator = animator.getLayoutParams();
            layoutParamsAnimator.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            animator.setLayoutParams(layoutParamsAnimator);

            View child = animator.getChildAt(0);
            ViewGroup.LayoutParams layoutParamsChild = child.getLayoutParams();
            layoutParamsChild.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            child.setLayoutParams(layoutParamsChild );
            return;
        }
        //6.0+
        headerId = context.getResources().getIdentifier("date_picker_header", "id", "android");
        if (headerId == headerView.getId()) {
            headerView.setVisibility(View.GONE);
        }
    }

}
