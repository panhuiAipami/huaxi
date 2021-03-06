package com.spriteapp.booklibrary.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.SquareBean;
import com.spriteapp.booklibrary.ui.adapter.SquareAdapter;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.NetworkUtil;
import com.spriteapp.booklibrary.widget.recyclerview.URecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.spriteapp.booklibrary.ui.activity.SquareDetailsActivity.PLATFORM_ID;


/**
 * Created by Administrator on 2018/1/5.
 */

public class SquareFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, URecyclerView.LoadingListener {
    private View mView;
    private URecyclerView recyclerView;//list列表
    private SwipeRefreshLayout swipe_refresh;//刷新
    private ImageView send_square;//去发帖子
    private LinearLayoutManager linearLayoutManager;//管理者
    private SquareAdapter adapter;//适配器
    private int page = 0;//页数
    private int lastPage = 0;
    private LinearLayout null_layout;
    private TextView miaoshu;
    public int FOLLOW = 0;//社区接口添加来区分花溪与嘎吱
    private List<SquareBean> squareBeanList = new ArrayList<>();//帖子详情集合


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.new_square_layout, container, false);
        return mView;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.new_square_layout;
    }

    @Override
    public void initData() {

    }

    @Override
    public void configViews() {

    }

    @Override
    public void findViewId() {
        recyclerView = (URecyclerView) mView.findViewById(R.id.square_recycler_view);
        swipe_refresh = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh);
        send_square = (ImageView) mView.findViewById(R.id.send_square);
        null_layout = (LinearLayout) mView.findViewById(R.id.null_layout);
        miaoshu = (TextView) mView.findViewById(R.id.miaoshu);
        swipe_refresh.setColorSchemeResources(R.color.square_comment_selector);
        swipe_refresh.setOnRefreshListener(this);
        recyclerView.setLoadingListener(this);
        send_square.setOnClickListener(new View.OnClickListener() {//发帖子
            @Override
            public void onClick(View v) {
                if (!AppUtil.isLogin(getActivity())) {
                    return;
                }
                ActivityUtil.toCreateDynamicActivity(getActivity());
            }
        });
        adapter = new SquareAdapter(getActivity(), squareBeanList);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        Bundle bundle = getArguments();
        FOLLOW = bundle.getInt("follow", 0);
        if (FOLLOW == 0)
            miaoshu.setText("偌大的广场,空无一人～");
        else if (FOLLOW == 1)
            miaoshu.setText("亲,您还没有关注任何人哦～");
        setHttp();
        showOrGone();
    }

    public void showOrGone() {
        if (squareBeanList.size() == 0) {
//            swipe_refresh.setVisibility(View.GONE);
            null_layout.setVisibility(View.VISIBLE);
        } else {
            null_layout.setVisibility(View.GONE);
//            swipe_refresh.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void lazyLoad() {


    }

    public void setHttp() {//加载数据
        BookApi.getInstance()
                .service
                .square_index(page, PLATFORM_ID, FOLLOW)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<List<SquareBean>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base<List<SquareBean>> squareBeanBase) {
                        if (squareBeanBase != null) {
                            int resultCode = squareBeanBase.getCode();
                            if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功
                                if (squareBeanBase.getData().size() != 0) {
                                    if (page == 0) {
                                        squareBeanList.clear();//刷新清空
                                    }
                                    page++;
                                    squareBeanList.addAll(squareBeanBase.getData());
                                    showOrGone();
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        swipe_refresh.setRefreshing(false);
                    }
                });
    }


    @Override
    public void onRefresh() {//刷新
        lastPage = 0;
        page = 0;
        if (!NetworkUtil.isAvailable(getActivity()))
            swipe_refresh.setRefreshing(false);
        setHttp();
    }

    @Override
    public void onLoadMore() {//加载
        Log.d("onLoadMore", "加载");
        if (page > lastPage) {
            lastPage = page;
            setHttp();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
        switch (requestCode) {
            case ActivityUtil.TOCREATEDYNAMICACTIVITY://发帖子成功自动跳转
                Log.d("onActivityResult", "刷新");
                onRefresh();//调用刷新
                break;
//            }
        }
    }


}
