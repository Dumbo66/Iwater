package com.app.dumbo.iwater.activity.pageThree;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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

        //控件初始化
        initView();

        //控件监听
        listenWidget();

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

    /**控件初始化*/
    public void initView(){
        //初始化适配器View
        listView=findViewById(R.id.lv_people);
    }

    /**控件监听*/
    private void listenWidget() {

    }
}

class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyHolder> {
    private List<String> list;
    private MyRecyclerAdapter.OnItemClickListener myItemClickListener;

    public MyRecyclerAdapter(List<String> list){
        this.list=list;
    }

    //item回调接口
    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    //定义一个设置点击监听的方法
    public void setOnItemClickListener(MyRecyclerAdapter.OnItemClickListener itemClickListener){
        this.myItemClickListener=itemClickListener;
    }

    @NonNull
    @Override
    /*重写onCreateViewHolder方法，返回一个自定义的ViewHolder*/
    public MyRecyclerAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view,null);
        return new MyRecyclerAdapter.MyHolder(view);
    }

    @Override
    /*填充onCreateViewHolder方法返回的holder中的控件*/
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        holder.tv_time.setText(list.get(position));
        //如果设置了回调，则设置点击事件
        if(myItemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myItemClickListener.onItemClick(holder.itemView,position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list==null ? 0 : list.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        private TextView tv_time;

        public MyHolder(View itemView) {
            super(itemView);
            tv_time=itemView.findViewById(R.id.tv_time);
        }
    }
}