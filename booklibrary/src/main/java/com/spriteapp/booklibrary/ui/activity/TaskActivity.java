package com.spriteapp.booklibrary.ui.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseTwo;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.TaskBean;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.NetworkUtil;
import com.spriteapp.booklibrary.util.ToastUtil;
import com.youth.banner.Banner;

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
    private Banner banner;
    private RecyclerView recyclerView;


    @Override
    public void initData() throws Exception {
        setTitle("任务");
        listener();
//        getCode();
        getUser_task();
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
        banner = (Banner) findViewById(R.id.banner);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
    }

    public void listener() throws Exception {
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
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == other_hint) {//提现提醒

        } else if (v == money_num) {//可提现金额

        } else if (v == goto_apprentice) {//去收徒
            ActivityUtil.toBindPhoneActivity(this);
        } else if (v == success_apprentice) {//徒弟列表

        } else if (v == apprentice_profit) {//收徒总收益

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
}
