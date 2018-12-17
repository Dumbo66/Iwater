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
class PeopleRecyclerAdapter extends RecyclerView.Adapter<PeopleRecyclerAdapter.MyHolder> {
    private List<String> list;
    private PeopleRecyclerAdapter.OnItemClickListener myItemClickListener;

    public PeopleRecyclerAdapter(List<String> list){
        this.list=list;
    }

    //item回调接口
    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    //定义一个设置点击监听的方法
    public void setOnItemClickListener(PeopleRecyclerAdapter.OnItemClickListener itemClickListener){
        this.myItemClickListener=itemClickListener;
    }

    @NonNull
    @Override
    /*重写onCreateViewHolder方法，返回一个自定义的ViewHolder*/
    public PeopleRecyclerAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view,null);
        return new PeopleRecyclerAdapter.MyHolder(view);
    }

    @Override
    /*填充onCreateViewHolder方法返回的holder中的控件*/
    public void onBindViewHolder(@NonNull final PeopleRecyclerAdapter.MyHolder holder, final int position) {
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
