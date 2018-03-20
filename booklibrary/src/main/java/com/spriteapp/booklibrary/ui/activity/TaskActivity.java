package com.spriteapp.booklibrary.ui.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseTwo;
import com.spriteapp.booklibrary.config.HuaXiConfig;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.TaskBean;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.adapter.TaskAdapter;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.NetworkUtil;
import com.spriteapp.booklibrary.util.ToastUtil;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 任务activity
 */
public class TaskActivity extends TitleActivity {
    private TextView other_hint, money_num, goto_apprentice, goto_invition_code,
            success_apprentice, apprentice_profit, apprentice_num, apprentice_profit_gold_num;
    private ImageView center_img, with_tip;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<TaskBean> taskBeanList = new ArrayList<>();


    @Override
    public void initData() throws Exception {
        setTitle("任务");
        listener();
//        getCode();
        initList();
        getUser_task();
    }

    private void initList() {

    }

    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_task, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
        other_hint = (TextView) findViewById(R.id.other_hint);
        money_num = (TextView) findViewById(R.id.money_num);
        goto_apprentice = (TextView) findViewById(R.id.goto_apprentice);
        goto_invition_code = (TextView) findViewById(R.id.goto_invition_code);
        success_apprentice = (TextView) findViewById(R.id.success_apprentice);
        apprentice_profit = (TextView) findViewById(R.id.apprentice_profit);
        apprentice_num = (TextView) findViewById(R.id.apprentice_num);
        apprentice_profit_gold_num = (TextView) findViewById(R.id.apprentice_profit_gold_num);
        center_img = (ImageView) findViewById(R.id.center_img);
        with_tip = (ImageView) findViewById(R.id.with_tip);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TaskAdapter(this, taskBeanList);
        recyclerView.setAdapter(taskAdapter);
    }

    public void listener() throws Exception {
        with_tip.setOnClickListener(this);
        other_hint.setOnClickListener(this);
        money_num.setOnClickListener(this);
        goto_apprentice.setOnClickListener(this);
        success_apprentice.setOnClickListener(this);
        apprentice_profit.setOnClickListener(this);
        goto_invition_code.setOnLongClickListener(new View.OnLongClickListener() {//复制邀请码到剪切板
            @Override
            public boolean onLongClick(View v) {
                if (!TextUtils.isEmpty(goto_apprentice.getText())) {
                    ClipboardManager cbm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    cbm.setText(goto_invition_code.getText());
                    ToastUtil.showToast("复制邀请码成功");
                }

                return true;
            }
        });
        mLeftLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == other_hint) {//提现提醒

        } else if (v == money_num) {//可提现金额

        } else if (v == goto_apprentice) {//去收徒
            BookDetailResponse bookDetailResponse = new BookDetailResponse();
            bookDetailResponse.setBook_name("期待");
            bookDetailResponse.setBook_intro("花都收徒");
            bookDetailResponse.setBook_image("http://img.zcool.cn/community/0142135541fe180000019ae9b8cf86.jpg@1280w_1l_2o_100sh.png");
            bookDetailResponse.setBook_share_url("http://baidu.com");
            HuaXiSDK.getInstance().showShareDialog(this, bookDetailResponse, true, 2);

        } else if (v == success_apprentice) {//徒弟列表
            ActivityUtil.toApprenticeListActivity(this);

        } else if (v == apprentice_profit) {//收徒总收益
            ActivityUtil.toProfitDetailsActivity(this);
        } else if (v == with_tip) {//提现规则
            ToastUtil.showToast("提现规则");
        }

    }

    /**
     * 得到用户邀请码
     */
    public void getCode() {
        if (!NetworkUtil.isAvailable(this)) return;
        showDialog();
        BookApi.getInstance()
                .service
                .invite_getcode()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseTwo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseTwo bookStoreResponse) {
                        if (bookStoreResponse != null) {
                            int resultCode = bookStoreResponse.getCode();
                            if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功
                                if (!TextUtils.isEmpty(bookStoreResponse.getInvitecode())) {
                                    goto_invition_code.setText(bookStoreResponse.getInvitecode());
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
                    }
                });
    }

    private void setUserData(TaskBean bean) {
        if (bean == null) return;
    }

    /**
     * 得到用户任务信息
     */
    public void getUser_task() {
        if (!NetworkUtil.isAvailable(this)) return;
        showDialog();

        BookApi.getInstance()
                .service
                .user_task(Constant.JSON_TYPE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TaskBean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TaskBean taskResponse) {
                        if (taskResponse != null) {
                            int resultCode = taskResponse.getCode();
                            if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功
                                if (taskBeanList.size() != 0) taskBeanList.clear();
                                taskBeanList.add(taskResponse);
                                setTask();
                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        dismissDialog();
                    }
                });
    }

    public void setTask() {

        try {
            if (taskBeanList != null && taskBeanList.size() != 0) {
                TaskBean taskBean = taskBeanList.get(0);
                if (taskBean == null) return;
                goto_invition_code.setText(taskBean.getUser_invite_code());
                apprentice_profit_gold_num.setText(taskBean.getApprentice_provide_money() + "金币");
                apprentice_num.setText(taskBean.getApprentice_num() + "人");
                GlideUtils.loadImage(center_img, taskBean.getTop_banner(), this);
                taskAdapter.notifyDataSetChanged();
                Log.d("setTask", "刷新适配器");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TaskActivity--", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("TaskActivity--", "onStop");
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }
}
