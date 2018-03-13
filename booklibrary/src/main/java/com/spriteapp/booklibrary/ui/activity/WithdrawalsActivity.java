package com.spriteapp.booklibrary.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.ui.adapter.HomePageTabAdapter;
import com.spriteapp.booklibrary.ui.fragment.WithdrawalsFragment;
import com.spriteapp.booklibrary.util.GlideUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 提现activity
 */
public class WithdrawalsActivity extends TitleActivity {
    //    public static final String WITHDRAWALS_TYPE = "withdrawals_type";
//    private SlidingTabLayout tab_layout;
//    private ViewPager view_pager;
//    private WithdrawalsFragment alipayFragment, wechatFragment;
//    private List<Fragment> fragmentList = new ArrayList<>();
//    private String[] mTitles = {"支付宝", "微信"};
//    private HomePageTabAdapter adapter;
    //修改过的
    private TextView setting_with, false_gold, real_money, goto_with;
    private ImageView ali_wx_img;
    private RadioGroup radio_group;
    private View alipay_line, wechat_line;
    private int pos = 0;


    @Override
    public void initData() throws Exception {
        setTitle("提现收益");
//        initFragment();
        listener();

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
        false_gold = (TextView) findViewById(R.id.false_gold);
        real_money = (TextView) findViewById(R.id.real_money);
        goto_with = (TextView) findViewById(R.id.goto_with);
        ali_wx_img = (ImageView) findViewById(R.id.ali_wx_img);
        radio_group = (RadioGroup) findViewById(R.id.radio_group);
        alipay_line = findViewById(R.id.alipay_line);
        wechat_line = findViewById(R.id.wechat_line);
    }

    private void listener() throws Exception {
        switchAliPay_WeChat();
        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.alipay_btn) {
                    pos=0;
                    switchAliPay_WeChat();
                } else if (checkedId == R.id.wechat_btn) {
                    pos=0;
                    switchAliPay_WeChat();
                }
            }
        });
    }


    private void initFragment() {
//        alipayFragment = new WithdrawalsFragment();
//        wechatFragment = new WithdrawalsFragment();
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
}
