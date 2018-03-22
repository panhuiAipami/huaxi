package com.spriteapp.booklibrary.ui.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.luck.picture.lib.tools.Constant;
import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseTwo;
import com.spriteapp.booklibrary.config.HuaXiConfig;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.TaskRewardBean;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.adapter.HomePageTabAdapter;
import com.spriteapp.booklibrary.ui.adapter.TaskRewardAdapter;
import com.spriteapp.booklibrary.ui.fragment.ApprenticeFragment;
import com.spriteapp.booklibrary.ui.fragment.ProfixDetailsFragment;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.NetworkUtil;
import com.spriteapp.booklibrary.widget.recyclerview.URecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.spriteapp.booklibrary.util.ActivityUtil.REWARDTYPE;

public class ProfitDetailsActivity extends TitleActivity implements SwipeRefreshLayout.OnRefreshListener, URecyclerView.LoadingListener {
    public static final String Profit_TYPE = "profit_type";
    private SlidingTabLayout tab_layout;
    private ViewPager view_pager;
    private ProfixDetailsFragment alipayFragment, wechatFragment;
    private List<Fragment> fragmentList = new ArrayList<>();
    private String[] mTitles = {"收益", "提现"};
    private HomePageTabAdapter adapter1;
    //新
    private int type;

    private URecyclerView recyclerView;
    private SwipeRefreshLayout refresh;
    private RelativeLayout null_layout;
    private TextView front_hint, behind_hint;
    private List<TaskRewardBean> list = new ArrayList<>();
    private TaskRewardAdapter adapter;
    private int page = 1;
    private int lastPage = 0;


    @Override
    public void initData() throws Exception {
        Intent intent = getIntent();
        type = intent.getIntExtra(REWARDTYPE, 0);
        setTitle(type == 0 ? "收徒奖励" : "任务奖励");
        front_hint.setText(type == 0 ? R.string.front2_text : R.string.front5_text);
        behind_hint.setText(type == 0 ? R.string.behind_text : R.string.behin3_text);
//        initFragment();
        initAdapter();
        goneOrShow();
        dateType();
        listener();
    }

    public void dateType() {
        if (type == 0)
            getApprenticeList();
        else if (type == 1)
            getTaskList();
    }


    private void initAdapter() {
        adapter = new TaskRewardAdapter(this, list, type);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void goneOrShow() {
        if (list.size() == 0) {
            null_layout.setVisibility(View.VISIBLE);
        } else {
            null_layout.setVisibility(View.GONE);
        }

    }

    public void listener() {
        behind_hint.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == behind_hint) {
            if (type == 0) {
                BookDetailResponse bookDetailResponse = new BookDetailResponse();
                HuaXiSDK.getInstance().showShareDialog(this, bookDetailResponse, true, 2);
            } else if (type == 1) {
                ActivityUtil.toTaskActivity(this);
            }
        }
    }

    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_profit_details, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
        tab_layout = (SlidingTabLayout) findViewById(R.id.tab_layout);
        view_pager = (ViewPager) findViewById(R.id.view_pager);


        recyclerView = (URecyclerView) findViewById(R.id.recyclerView);
        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        null_layout = (RelativeLayout) findViewById(R.id.null_layout);
        front_hint = (TextView) findViewById(R.id.front_hint);
        behind_hint = (TextView) findViewById(R.id.behind_hint);
        refresh.setColorSchemeResources(R.color.square_comment_selector);
        refresh.setOnRefreshListener(this);
        recyclerView.setLoadingListener(this);
    }


    private void getApprenticeList() {

        if (!NetworkUtil.isAvailable(this)) return;
        showDialog();
        BookApi.getInstance()
                .service
                .user_coninspupillog(com.spriteapp.booklibrary.constant.Constant.JSON_TYPE, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<List<TaskRewardBean>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base<List<TaskRewardBean>> taskRewardResponse) {
                        if (taskRewardResponse != null) {
                            int resultCode = taskRewardResponse.getCode();
                            if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功
                                if (taskRewardResponse.getData() != null && taskRewardResponse.getData().size() != 0) {
                                    if (page == 1) list.clear();
                                    page++;
                                    list.addAll(taskRewardResponse.getData());
                                    adapter.notifyDataSetChanged();
                                    goneOrShow();
                                }

                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        dismissDialog();
                        refresh.setRefreshing(false);
                    }
                });
    }

    private void getTaskList() {

        if (!NetworkUtil.isAvailable(this)) return;
        showDialog();
        BookApi.getInstance()
                .service
                .user_coninsselflog(com.spriteapp.booklibrary.constant.Constant.JSON_TYPE, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<List<TaskRewardBean>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base<List<TaskRewardBean>> taskRewardResponse) {
                        if (taskRewardResponse != null) {
                            int resultCode = taskRewardResponse.getCode();
                            if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功
                                if (taskRewardResponse.getData() != null && taskRewardResponse.getData().size() != 0) {
                                    if (page == 1) list.clear();
                                    page++;
                                    list.addAll(taskRewardResponse.getData());
                                    adapter.notifyDataSetChanged();
                                    goneOrShow();
                                }

                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        dismissDialog();
                        refresh.setRefreshing(false);
                    }
                });
    }

    private void initFragment() {
        alipayFragment = new ProfixDetailsFragment();
        wechatFragment = new ProfixDetailsFragment();
        Bundle bundle = new Bundle();
        Bundle bundle2 = new Bundle();
        bundle.putInt(Profit_TYPE, 0);
        bundle2.putInt(Profit_TYPE, 1);
        alipayFragment.setArguments(bundle);
        wechatFragment.setArguments(bundle2);
        fragmentList.add(alipayFragment);
        fragmentList.add(wechatFragment);
        adapter1 = new HomePageTabAdapter(getSupportFragmentManager(), fragmentList);
        view_pager.setAdapter(adapter1);
        tab_layout.setViewPager(view_pager, mTitles);


    }

    @Override
    public void onRefresh() {
        page = 1;
        dateType();
    }

    @Override
    public void onLoadMore() {
        if (page > lastPage) {
            lastPage = page;
            dateType();
        }

    }
}
