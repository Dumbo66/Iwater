package com.app.dumbo.iwater.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.pageThree.NoticeActivity;
import com.app.dumbo.iwater.activity.pageThree.PeopleActivity;
import com.app.dumbo.iwater.activity.pageThree.WarnActivity;
import com.app.dumbo.iwater.util.CommonUtil;

/**
 * Created by dumbo on 2018/8/10
 **/

public class PageThreeFragment extends Fragment implements View.OnClickListener{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View pageThree = inflater.inflate(R.layout.fragement_page_three, null);
        RelativeLayout warn=pageThree.findViewById(R.id.rl_warn);
        RelativeLayout notice= pageThree.findViewById(R.id.rl_notice);
        RelativeLayout peopleComplain= pageThree.findViewById(R.id.rl_people_complain);

        warn.setOnClickListener(this);
        notice.setOnClickListener(this);
        peopleComplain.setOnClickListener(this);

        return pageThree;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //“超标警报”监听
            case R.id.rl_warn:
                CommonUtil.skipActivityBySlide(getActivity(),WarnActivity.class);
                break;

            //“消息通知"监听
            case R.id.rl_notice:
                CommonUtil.skipActivityBySlide(getActivity(),NoticeActivity.class);
                break;

            //“群众投诉”监听
            case R.id.rl_people_complain:
                CommonUtil.skipActivityBySlide(getActivity(),PeopleActivity.class);
                break;
        }
    }
}