package com.spriteapp.booklibrary.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
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
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.listener.ListenerManager;
import com.spriteapp.booklibrary.listener.LoginSuccess;
import com.spriteapp.booklibrary.model.UserBean;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.activity.HomeActivity;
import com.spriteapp.booklibrary.ui.dialog.GuessYouLikeDialog;
import com.spriteapp.booklibrary.ui.dialog.InvitationCodeDialog;
import com.spriteapp.booklibrary.ui.presenter.BookShelfPresenter;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.NetworkUtil;
import com.spriteapp.booklibrary.util.Util;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;
import static com.spriteapp.booklibrary.ui.activity.HomeActivity.PERSON_TO_BOOKSHELF;
import static com.spriteapp.booklibrary.util.ActivityUtil.BACKREFRESH;
import static com.spriteapp.booklibrary.util.ActivityUtil.LOGIN_BACK;

/**
 * Created by Administrator on 2018/1/22.
 */

public class PersonCenterFragment extends BaseFragment implements View.OnClickListener, LoginSuccess {

    private View mView;
    private TextView user_name, hua_bei, hua_ban, user_share, user_follow, user_fans,
            recharge, bookshelf, recharge_record, records_of_consumption,
            award_record, remind, phone, setting, user_id;
    private LinearLayout invitation_code_layout, task_layout, withdrawals_layout;
    private TextView invitation_code, task, withdrawals, huaban_hint, gold_hint, user_sign;
    private ImageView user_bg, user_head;
    private BookShelfPresenter mPresenter;
    public static int toSetting = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.person_center_layout, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listener();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.person_center_layout;
    }

    @Override
    public void initData() {
        listener();
    }

    @Override
    public void configViews() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && AppUtil.isLogin() && getActivity() != null)
            getUserData();
    }

    @Override
    public void findViewId() {
        user_name = (TextView) mView.findViewById(R.id.user_name);
        hua_bei = (TextView) mView.findViewById(R.id.hua_bei);
        hua_ban = (TextView) mView.findViewById(R.id.hua_ban);
        user_share = (TextView) mView.findViewById(R.id.user_share);
        user_follow = (TextView) mView.findViewById(R.id.user_follow);
        user_fans = (TextView) mView.findViewById(R.id.user_fans);
        recharge = (TextView) mView.findViewById(R.id.recharge);
        bookshelf = (TextView) mView.findViewById(R.id.bookshelf);

        recharge_record = (TextView) mView.findViewById(R.id.recharge_record);
        records_of_consumption = (TextView) mView.findViewById(R.id.records_of_consumption);
        award_record = (TextView) mView.findViewById(R.id.award_record);
        remind = (TextView) mView.findViewById(R.id.remind);
        phone = (TextView) mView.findViewById(R.id.phone);
        setting = (TextView) mView.findViewById(R.id.setting);
        user_bg = (ImageView) mView.findViewById(R.id.user_bg);
        user_head = (ImageView) mView.findViewById(R.id.user_head);
        user_id = (TextView) mView.findViewById(R.id.user_id);
        //花都
        invitation_code = (TextView) mView.findViewById(R.id.invitation_code);
        task = (TextView) mView.findViewById(R.id.task);
        withdrawals = (TextView) mView.findViewById(R.id.withdrawals);
        user_sign = (TextView) mView.findViewById(R.id.user_sign);
        huaban_hint = (TextView) mView.findViewById(R.id.huaban_hint);
        gold_hint = (TextView) mView.findViewById(R.id.gold_hint);
        invitation_code_layout = (LinearLayout) mView.findViewById(R.id.invitation_code_layout);
        task_layout = (LinearLayout) mView.findViewById(R.id.task_layout);
        withdrawals_layout = (LinearLayout) mView.findViewById(R.id.withdrawals_layout);
        if (HomeActivity.ISHAUDU) {
            task_layout.setVisibility(View.VISIBLE);
            invitation_code_layout.setVisibility(View.VISIBLE);
            withdrawals_layout.setVisibility(View.VISIBLE);
            huaban_hint.setVisibility(View.GONE);
            gold_hint.setVisibility(View.VISIBLE);
            hua_ban.setTextColor(getResources().getColor(R.color.code_btn_bg_color));
        }


    }

    private void listener() {
        ListenerManager.getInstance().setLoginSuccess(this);
        user_name.setOnClickListener(this);
        hua_bei.setOnClickListener(this);
        hua_ban.setOnClickListener(this);
        user_share.setOnClickListener(this);
        user_follow.setOnClickListener(this);
        user_fans.setOnClickListener(this);
        recharge.setOnClickListener(this);
        bookshelf.setOnClickListener(this);
        recharge_record.setOnClickListener(this);
        records_of_consumption.setOnClickListener(this);
        award_record.setOnClickListener(this);
        remind.setOnClickListener(this);
        phone.setOnClickListener(this);
        setting.setOnClickListener(this);
        user_bg.setOnClickListener(this);
        user_head.setOnClickListener(this);
        user_sign.setOnClickListener(this);
        //花都
        task.setOnClickListener(this);
        invitation_code.setOnClickListener(this);
        withdrawals.setOnClickListener(this);
        if (AppUtil.isLogin())
            getUserData();


    }

    @Override
    protected void lazyLoad() {

    }


    private void IsLogin() {
        if (!AppUtil.isLogin(getActivity())) return;
    }

    @Override
    public void onClick(View v) {

        if (v == user_name) {
            if (!AppUtil.isLogin(getActivity())) return;
        } else if (v == hua_bei) {
            if (!AppUtil.isLogin(getActivity())) return;
        } else if (v == hua_ban) {
            if (!AppUtil.isLogin(getActivity())) return;

        } else if (v == user_share) {
            if (!AppUtil.isLogin(getActivity())) return;

        } else if (v == user_follow) {
            if (!AppUtil.isLogin(getActivity())) return;

        } else if (v == user_fans) {
            if (!AppUtil.isLogin(getActivity())) return;

        } else if (v == recharge) {
            if (!AppUtil.isLogin(getActivity())) return;
//            ActivityUtil.toWebViewActivityBack(getActivity(), Constant.H5_PAY_URL, false);
            ActivityUtil.toRechargeActivity(getActivity());
        } else if (v == bookshelf) {
            if (getActivity() instanceof HomeActivity) {
                HomeActivity activity = (HomeActivity) getActivity();
                activity.setSelectView(PERSON_TO_BOOKSHELF);
            }
        } else if (v == recharge_record) {
            if (!AppUtil.isLogin(getActivity())) return;
            toWebView(Constant.USER_RECHARGE_LOG);
        } else if (v == records_of_consumption) {
            if (!AppUtil.isLogin(getActivity())) return;
            toWebView(Constant.USER_CONSUME_LOG);
        } else if (v == award_record) {
            if (!AppUtil.isLogin(getActivity())) return;
            toWebView(Constant.USER_GIVE_LOG);
        } else if (v == remind) {
            toSetting = 1;
            ActivityUtil.toSettingActivity(getActivity());
        } else if (v == phone) {
            if (!AppUtil.isLogin(getActivity())) return;
        } else if (v == setting) {
            toSetting = 2;
            ActivityUtil.toSettingActivity(getActivity());
        } else if (v == user_bg) {
            if (!AppUtil.isLogin(getActivity())) return;
//            ActivityUtil.toNativeActivity(getActivity());
//            new GuessYouLikeDialog(getActivity(),new BookDetailResponse(),1).show();
        } else if (v == user_head) {
            if (!AppUtil.isLogin(getActivity())) return;
//            new GuessYouLikeDialog(getActivity(),new BookDetailResponse(),2).show();
        } else if (v == task) {//任务
            if (!AppUtil.isLogin(getActivity())) return;
            ActivityUtil.toTaskActivity(getActivity());//去任务
//            CustomToastUtils toastUtils = new CustomToastUtils(getActivity(), R.layout.toast, "领取成功  +200");
//            toastUtils.show();
        } else if (v == invitation_code) {//填写邀请码
            if (!AppUtil.isLogin(getActivity())) return;
            InvitationCodeDialog dialog = new InvitationCodeDialog(getActivity());
        } else if (v == withdrawals) {
            if (!AppUtil.isLogin(getActivity())) return;
            ActivityUtil.toWithdrawalsActivity(getActivity());//去提现
        } else if (v == user_sign) {
            if (!AppUtil.isLogin(getActivity())) return;
            ActivityUtil.toWebViewActivity(getActivity(), Constant.CHECK_IN_URL);//去签到
        }
    }

    public void toWebView(String url) {
        if (url != null && !url.isEmpty())
            ActivityUtil.toWebViewActivity(getActivity(), url);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case LOGIN_BACK://登录返回
                    getUserData();
                    break;
                case BACKREFRESH:
                    Log.d("user_go", "充值返回");
                    getUserData();
                    break;
            }
        }
    }

    private void setEndColor(TextView view, String str) {
        if (str == null) return;
        if (str.isEmpty()) return;
        if (str.length() < 2) return;
        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        ForegroundColorSpan square_small = new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.square_small));
        builder.setSpan(square_small, str.length() - 2, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new AbsoluteSizeSpan(Util.dp2px(getActivity(), 14)), str.length() - 2, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(builder);
    }


    public void getUserData() {
        try {
            Log.d("userInfo", "用户信息11");
            if (!AppUtil.isLogin(getActivity()))
                return;
            if (!NetworkUtil.isAvailable(getActivity())) {
                setUserData();
            }
            BookApi.getInstance().
                    service
                    .getUserBean(Constant.JSON_TYPE)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Base<UserBean>>() {
                        @Override
                        public void onComplete() {
                        }

                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onNext(Base<UserBean> userModelBase) {
                            if (userModelBase.getCode() == ApiCodeEnum.SUCCESS.getValue()) {
                                if (userModelBase.getData() != null) {
                                    UserBean user = userModelBase.getData();
                                    user.commit();
                                    setUserData();
                                }


                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUserData() {
        if (!UserBean.getInstance().getUser_avatar().isEmpty())
            GlideUtils.loadImage(user_head, UserBean.getInstance().getUser_avatar(), getActivity());
        else
            GlideUtils.loadImage(user_head, R.mipmap.deafultheadicon, getActivity());
        if (HomeActivity.ISHAUDU) {//是花都显示用户金币
            hua_ban.setText(Util.getString(UserBean.getInstance().getGold_coins() + ""));
        } else {//不是花都显示花瓣
            hua_ban.setText(Util.getString(UserBean.getInstance().getUser_false_point() + ""));
        }

        hua_bei.setText(Util.getString(UserBean.getInstance().getUser_real_point() + ""));
        user_name.setText(UserBean.getInstance().getUser_nickname());
        user_id.setText("uid:" + UserBean.getInstance().getUser_id());
        setEndColor(user_share, "100\n分享");
        setEndColor(user_follow, "0\n关注");
        setEndColor(user_fans, "0\n粉丝");
    }

    public void setUserDataNull() {
        UserBean user = new UserBean();
        user.commit();
        GlideUtils.loadImage(user_head, R.mipmap.deafultheadicon, getActivity());
        hua_ban.setText("0");
        hua_bei.setText("0");
        user_name.setText("登录");
        user_id.setText("");
        setEndColor(user_share, "0\n分享");
        setEndColor(user_follow, "0\n关注");
        setEndColor(user_fans, "0\n粉丝");
    }


    @Override
    public void loginState(int state) {
        if (getActivity() == null) {
            Log.d("loginState", "getActivity为空");
            return;
        }
        Log.d("loginState", "getActivity不为空");
        if (state == 1) {
            getUserData();
        } else if (state == 2) {
            setUserDataNull();
        }

    }

}
