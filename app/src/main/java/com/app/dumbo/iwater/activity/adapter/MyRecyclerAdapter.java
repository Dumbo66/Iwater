package com.app.dumbo.iwater.activity.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.dumbo.iwater.R;

import java.util.List;

/**
 * Created by dumbo on 2018/9/6
 **/

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyHolder> {
    private List<String> dataList;
    private OnItemClickListener myItemClickListener;

    public MyRecyclerAdapter(List<String> dataList){
        this.dataList=dataList;
    }

    //item回调接口
    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    //定义一个设置点击监听的方法
    public void setOnItemClickListener(OnItemClickListener itemClickListener){
        this.myItemClickListener=itemClickListener;
    }

    @NonNull
    @Override
    /*重写onCreateViewHolder方法，返回一个自定义的ViewHolder*/
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view,null);
        return new MyHolder(view);
    }

    /**填充onCreateViewHolder方法返回的holder中的控件*/
    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        holder.tv_time.setText(dataList.get(position));
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
        return dataList==null ? 0 : dataList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        private TextView tv_time;

        public MyHolder(View itemView) {
            super(itemView);
            tv_time=itemView.findViewById(R.id.tv_time);
        }
    }
}