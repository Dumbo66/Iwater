package com.app.dumbo.iwater.activity.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.dumbo.iwater.R;
import com.app.dumbo.iwater.activity.pageOne.AddMomentActivity;
import com.app.dumbo.iwater.activity.pageTwo.Moment;
import com.app.dumbo.iwater.constant.Common;
import com.app.dumbo.iwater.retrofit2.Retrofit2;
import com.app.dumbo.iwater.retrofit2.entity.MomentsReception;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.bingoogolapple.baseadapter.BGABaseAdapterUtil;
import cn.bingoogolapple.baseadapter.BGAOnRVItemClickListener;
import cn.bingoogolapple.baseadapter.BGAOnRVItemLongClickListener;
import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity;
import cn.bingoogolapple.photopicker.imageloader.BGAImage;
import cn.bingoogolapple.photopicker.imageloader.BGARVOnScrollListener;
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by dumbo on 2018/8/10
 **/

public class PageTwoFragment  extends Fragment
        implements BGANinePhotoLayout.Delegate,BGAOnRVItemClickListener, BGAOnRVItemLongClickListener {
    private static final int PRC_PHOTO_PREVIEW = 1;
    private static final int RC_ADD_MOMENT = 2;

    private RecyclerView rvMoment;
    private BGANinePhotoLayout bganpl;
    private MomentAdapter momentAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View pageTwo = inflater.inflate(R.layout.fragement_page_two, null);
        rvMoment=pageTwo.findViewById(R.id.rv_moment);
        RefreshLayout refreshLayout=pageTwo.findViewById(R.id.refresh_layout);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
            }
        });
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(2000);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

            }
        });

        momentAdapter=new MomentAdapter(rvMoment);
        momentAdapter.setOnRVItemClickListener(this);
        momentAdapter.setOnRVItemLongClickListener(this);

        //设置适配器
        rvMoment.addOnScrollListener(new BGARVOnScrollListener(getActivity()));
        rvMoment.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvMoment.setAdapter(momentAdapter);

        //添加自定义分隔线
        DividerItemDecoration divider=new DividerItemDecoration
                (getActivity(),DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(
                getActivity().getDrawable(R.drawable.divider_rv_moment)));
        rvMoment.addItemDecoration(divider);

        addNetMoments();

        return pageTwo;
    }

    @Override
    public void onClickNinePhotoItem(BGANinePhotoLayout ninePhotoLayout, View view,
                                     int position, String model, List<String> models) {
        bganpl = ninePhotoLayout;
        photoPreviewWrapper();

    }

    @Override
    public void onRVItemClick(ViewGroup parent, View itemView, int position) {
        Toast.makeText(getActivity(), "点击了item " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onRVItemLongClick(ViewGroup parent, View itemView, int position) {
        Toast.makeText(getActivity(), "长按了item " + position, Toast.LENGTH_SHORT).show();
        return true;
    }

    private class MomentAdapter extends BGARecyclerViewAdapter<Moment> {

        public MomentAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.item_moment);
        }

        @Override
        protected void fillData(BGAViewHolderHelper helper, int position, Moment moment) {
            helper.setText(R.id.tv_item_moment_user_name,moment.userName);
            helper.setText(R.id.tv_item_moment_time,moment.time);
            helper.setText(R.id.tv_item_moment_content, moment.content);
            helper.setText(R.id.tv_item_moment_address,moment.address);

            BGAImage.display(helper.getImageView(R.id.iv_item_moment_avatar),
                    R.mipmap.bga_pp_ic_holder_light,moment.avatar, BGABaseAdapterUtil.dp2px(40));

            BGANinePhotoLayout ninePhotoLayout = helper.getView(R.id.npl_moment_item_photos);
            ninePhotoLayout.setDelegate((BGANinePhotoLayout.Delegate) this);
            ninePhotoLayout.setData(moment.photos);
        }
    }

    private void addNetMoments() {
        Call<MomentsReception> momentsCall= Retrofit2.getService().getMoments(20);
        momentsCall.enqueue(new Callback<MomentsReception>() {
            @Override
            public void onResponse(Call<MomentsReception> call, Response<MomentsReception> response) {
                System.out.println("请求Moments成功！");

                int count=response.body().getData().size();
                List<Moment> moments = new ArrayList<>();
                String[] time=new String[count];
                String[] desc=new String[count];
                //图片url="http://ip地址"+picturesUrl
                String[] pathStr=new String[count];
                String[] address=new String[count];
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                for(int i=0;i<count;i++){
                    time[i]=sdf.format(response.body().getData().get(i).getRecordTime());
                    desc[i]=response.body().getData().get(i).getDescription();

                    ArrayList<String> urlList=new ArrayList<>();
                    pathStr[i]=response.body().getData().get(i).getPicturesUrl();
                    //拆分路径
                    String[] path=pathStr[i].split("&");
                    for(int j=0;j<path.length;j++){
                        urlList.add("http://"+ Common.HOST_IP +path[j]);
                    }

                    address[i]=response.body().getData().get(i).getAddress();

                    moments.add(new Moment("http://7xk9dj.com1.z0.glb.clouddn.com/refreshlayout/images/staggered1.png",
                            "zxr",time[i],desc[i], urlList, address[i]));
                }
                momentAdapter.setData(moments);
            }

            @Override
            public void onFailure(Call<MomentsReception> call, Throwable t) {

            }
        });
    }

    /**
     * 图片预览，兼容6.0动态权限
     */
    @AfterPermissionGranted(PRC_PHOTO_PREVIEW)
    private void photoPreviewWrapper() {
        if (bganpl == null) {
            return;
        }

        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            File downloadDir = new File(Common.TAKE_PHOTO_DIR);
            // 保存图片的目录，如果传 null，则没有保存图片功能
            BGAPhotoPreviewActivity.IntentBuilder photoPreviewIntentBuilder =
                    new BGAPhotoPreviewActivity.IntentBuilder(getActivity()).saveImgDir(downloadDir);
            if (bganpl.getItemCount() == 1) {
                // 预览单张图片
                photoPreviewIntentBuilder.previewPhoto(bganpl.getCurrentClickItem());
            } else if (bganpl.getItemCount() > 1) {
                // 预览多张图片
                photoPreviewIntentBuilder.previewPhotos(bganpl.getData())
                        .currentPosition(bganpl.getCurrentClickItemPosition()); // 当前预览图片的索引
            }
            startActivity(photoPreviewIntentBuilder.build());
        } else {
            EasyPermissions.requestPermissions(this, "图片预览需要以下权限:\n\n1.访问设备上的照片", PRC_PHOTO_PREVIEW, perms);
        }
    }
}
