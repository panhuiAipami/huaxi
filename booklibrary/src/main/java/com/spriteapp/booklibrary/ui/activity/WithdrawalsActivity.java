package com.spriteapp.booklibrary.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.UserBean;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.NetworkUtil;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.spriteapp.booklibrary.util.ToastUtil;
import com.spriteapp.booklibrary.util.Util;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.spriteapp.booklibrary.ui.activity.MangerAlipayActivity.ALIPAY_ACCOUNT;
import static com.spriteapp.booklibrary.ui.activity.MangerAlipayActivity.ALIPAY_NAME;

/**
 * 提现activity
 */
public class WithdrawalsActivity extends TitleActivity {
//    public static final String WITHDRAWALS_TYPE = "withdrawals_type";
//    private SlidingTabLayout tab_layout;
//    private ViewPager view_pager;
//    private ApprenticeFragment alipayFragment, wechatFragment;
//    private List<Fragment> fragmentList = new ArrayList<>();
//    private String[] mTitles = {"支付宝", "微信"};
//    private HomePageTabAdapter adapter;

    //修改过的
    private TextView setting_with, money_num, real_money, goto_with, setting_with_hint;
    private ImageView ali_wx_img, alipay_name;
    private RadioGroup radio_group;
    private View alipay_line, wechat_line;
    private LinearLayout to_with_layout;
    private int pos = 0;
    private TextView new_false_gold, new_real_money, apprentice_text, task_text, with_text;


    @Override
    public void initData() throws Exception {
        setTitle("提现收益");
//        initFragment();
        listener();
        setUserData();
        setNameText();

    }

    private void setUserData() {
        new_false_gold.setText("+" + UserBean.getInstance().getGold_coins());
        new_real_money.setText("+" + UserBean.getInstance().getRmb());
        int money = UserBean.getInstance().getGold_coins();
        if (money > 0) {
            double v = (float) money / 33333;
            money_num.setText(Util.keepOne(v));//计算出金币与人民币共同金额
            Log.d("real_rmb", "比例兑换===" + Util.keepOne(v));
        }

    }


    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_withdrawals, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
//        tab_layout = (SlidingTabLayout) findViewById(R.id.tab_layout);
//        view_pager = (ViewPager) findViewById(R.id.view_pager);


        setting_with = (TextView) findViewById(R.id.setting_with);
        money_num = (TextView) findViewById(R.id.money_num);
        real_money = (TextView) findViewById(R.id.real_money);
        goto_with = (TextView) findViewById(R.id.goto_with);
        setting_with_hint = (TextView) findViewById(R.id.setting_with_hint);
        alipay_name = (ImageView) findViewById(R.id.alipay_name);
        to_with_layout = (LinearLayout) findViewById(R.id.to_with_layout);
        ali_wx_img = (ImageView) findViewById(R.id.ali_wx_img);
        radio_group = (RadioGroup) findViewById(R.id.radio_group);
        alipay_line = findViewById(R.id.alipay_line);
        wechat_line = findViewById(R.id.wechat_line);
        new_false_gold = (TextView) findViewById(R.id.new_false_gold);
        new_real_money = (TextView) findViewById(R.id.new_real_money);
        apprentice_text = (TextView) findViewById(R.id.apprentice_text);
        task_text = (TextView) findViewById(R.id.task_text);
        with_text = (TextView) findViewById(R.id.with_text);

    }

    private void listener() throws Exception {
//        switchAliPay_WeChat();

        to_with_layout.setOnClickListener(this);
        new_false_gold.setOnClickListener(this);
        new_real_money.setOnClickListener(this);
        apprentice_text.setOnClickListener(this);
        task_text.setOnClickListener(this);
        with_text.setOnClickListener(this);
        goto_with.setOnClickListener(this);
        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.alipay_btn) {
                    pos = 0;
                    switchAliPay_WeChat();
                } else if (checkedId == R.id.wechat_btn) {
                    pos = 0;
                    switchAliPay_WeChat();
                }
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
        if (v == to_with_layout) {//管理支付宝
            ActivityUtil.toMangerAlipayActivity(this);
        } else if (v == goto_with) {//提现
            gotoWithHttp(UserBean.getInstance().getGold_coins());
        } else if (v == apprentice_text) {//徒弟奖励
            ActivityUtil.toProfitDetailsActivity(this, 0);
        } else if (v == task_text) {//任务奖励
            ActivityUtil.toProfitDetailsActivity(this, 1);
        } else if (v == with_text) {//提现明细
            ActivityUtil.toProfitDetailsActivity(this, 2);
        }
    }

    private void initFragment() {
//        alipayFragment = new ApprenticeFragment();
//        wechatFragment = new ApprenticeFragment();
//        Bundle bundle = new Bundle();
//        Bundle bundle2 = new Bundle();
//        bundle.putInt(WITHDRAWALS_TYPE, 0);
//        bundle2.putInt(WITHDRAWALS_TYPE, 1);
//        alipayFragment.setArguments(bundle);
//        wechatFragment.setArguments(bundle2);
//        fragmentList.add(alipayFragment);
//        fragmentList.add(wechatFragment);
//        adapter = new HomePageTabAdapter(getSupportFragmentManager(), fragmentList);
//        view_pager.setAdapter(adapter);
//        tab_layout.setViewPager(view_pager, mTitles);


    }

    public void switchAliPay_WeChat() {//修改提现状态支付宝或者微信
        try {
            if (pos == 0) {
                alipay_line.setVisibility(View.VISIBLE);
                wechat_line.setVisibility(View.INVISIBLE);
                GlideUtils.loadImage(ali_wx_img, R.mipmap.alipay_with, this);
                setting_with.setText(R.string.alipay_with);
            } else {
                wechat_line.setVisibility(View.VISIBLE);
                alipay_line.setVisibility(View.INVISIBLE);
                GlideUtils.loadImage(ali_wx_img, R.mipmap.wechat_with, this);
                setting_with.setText(R.string.wechat_with);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void gotoWithHttp(int amount) {
        if (!NetworkUtil.isAvailable(this)) return;
        String account = SharedPreferencesUtil.getInstance().getString(ALIPAY_ACCOUNT);
        String real_name = SharedPreferencesUtil.getInstance().getString(ALIPAY_NAME);
        if (amount == 0) {
            ToastUtil.showToast("没有金币不能提现呦");
            return;
        }
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(real_name)) {
            ToastUtil.showToast(R.string.alipay_with);
            return;
        }
        showDialog();
        BookApi.getInstance()
                .service
                .user_exchange(Constant.JSON_TYPE, amount + "", account, real_name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base bookStoreResponse) {
                        if (bookStoreResponse != null) {
                            int resultCode = bookStoreResponse.getCode();
                            if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功
                                if (!TextUtils.isEmpty(bookStoreResponse.getMessage())) {
                                    ToastUtil.showToast(bookStoreResponse.getMessage());
                                    finish();
                                }
                            } else {
                                if (!TextUtils.isEmpty(bookStoreResponse.getMessage())) {
                                    ToastUtil.showToast(bookStoreResponse.getMessage());
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setNameText();
        }
    }

    public void setNameText() {
        if (TextUtils.isEmpty(SharedPreferencesUtil.getInstance().getString(ALIPAY_NAME))) {
            alipay_name.setEnabled(true);
            setting_with_hint.setVisibility(View.VISIBLE);
        } else {
            alipay_name.setEnabled(false);
            setting_with_hint.setVisibility(View.GONE);
        }

//        if (!TextUtils.isEmpty(name) && length > 1) {
//            String substring = name.substring(0, 1) + (length == 2 ? "*" : (length == 3 ? "**" : (length >= 4 ? "***" : "***")));
//            alipay_name.setText(substring);
//        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }
}
