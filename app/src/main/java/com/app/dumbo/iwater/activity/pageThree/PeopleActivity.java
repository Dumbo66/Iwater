package com.app.dumbo.iwater.activity.pageThree;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.superClass.AnimFadeActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dumbo on 2018/4/13
 **/

public class PeopleActivity extends AnimFadeActivity {
    private ListView listView;

    private SimpleAdapter adapter;

    private List<Map<String ,Object>> list;

    private Map<String ,Object> map;

    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_people_complain);
        super.onCreate(savedInstanceState);

        //数据源初始化
        list=new ArrayList<>();
        for(int i=0;i<20;i++){
            map=new HashMap<>();
            map.put("nick_name","dumbo");
            map.put("time","2018-6-2 19:43:23");
            map.put("content","世界和平世界和平世界和平世界和平世界和平世界和平世界和平世界和平世界和平世界界和平世界和平世界和平世界界和平世界界和平世界和世界和平世界和平世界和平世界和平世界和平世界和平世界和平世界和平世界和平……");
            map.put("image1" ,R.drawable.one);
            map.put("image2" ,R.drawable.one);
            map.put("image3" ,R.drawable.one);
            map.put("one","待阅");
            map.put("two","待处理");
            map.put("three","XXX");
            list.add(map);
        }

        //初始化控件adapter
        String[] from={"nick_name" ,"time" ,"content","image1","image2","image3","one","two","three"};
        int[] to={R.id.tv_nick_name ,R.id.tv_time_people ,R.id.tv_content,R.id.image_one,R.id.image_two,
                  R.id.image_three,R.id.tv_one,R.id.tv_two,R.id.tv_three};
        adapter=new SimpleAdapter(this,list,R.layout.item_pic_adapter,from,to);
        //设置适配器
        listView.setAdapter(adapter);

    }

    @Override
    public void initView() {
        super.initView();
        //初始化适配器View
        listView=findViewById(R.id.lv_people);
    }

    @Override
    public void setListener() {
        super.setListener();

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

    }
}

