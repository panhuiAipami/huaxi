package com.spriteapp.booklibrary.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.enumeration.UpdaterPayEnum;
import com.spriteapp.booklibrary.model.UserBean;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.GlideUtils;

import de.greenrobot.event.EventBus;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;
import static com.spriteapp.booklibrary.util.ActivityUtil.LOGIN_BACK;

/**
 * Created by Administrator on 2018/1/22.
 */

public class PersonCenterFragment extends BaseFragment implements View.OnClickListener {
    private View mView;
    private TextView user_name, hua_bei, hua_ban, user_share, user_follow, user_fans,
            recharge, bookshelf, comment, recharge_record, records_of_consumption,
            award_record, remind, phone, setting;
    private ImageView user_bg, user_head;


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
        EventBus.getDefault().register(this);
        listener();
    }

    @Override
    public void configViews() {

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
        comment = (TextView) mView.findViewById(R.id.comment);
        recharge_record = (TextView) mView.findViewById(R.id.recharge_record);
        records_of_consumption = (TextView) mView.findViewById(R.id.records_of_consumption);
        award_record = (TextView) mView.findViewById(R.id.award_record);
        remind = (TextView) mView.findViewById(R.id.remind);
        phone = (TextView) mView.findViewById(R.id.phone);
        setting = (TextView) mView.findViewById(R.id.setting);
        user_bg = (ImageView) mView.findViewById(R.id.user_bg);
        user_head = (ImageView) mView.findViewById(R.id.user_head);
    }

    private void listener() {
        user_name.setOnClickListener(this);
        hua_bei.setOnClickListener(this);
        hua_ban.setOnClickListener(this);
        user_share.setOnClickListener(this);
        user_follow.setOnClickListener(this);
        user_fans.setOnClickListener(this);
        recharge.setOnClickListener(this);
        bookshelf.setOnClickListener(this);
        comment.setOnClickListener(this);
        recharge_record.setOnClickListener(this);
        records_of_consumption.setOnClickListener(this);
        award_record.setOnClickListener(this);
        remind.setOnClickListener(this);
        phone.setOnClickListener(this);
        setting.setOnClickListener(this);
        user_bg.setOnClickListener(this);
        user_head.setOnClickListener(this);

    }

    @Override
    protected void lazyLoad() {

    }


    private void IsLogin() {
        if (!AppUtil.isLogin(getActivity())) return;
    }

    @Override
    public void onClick(View v) {
        IsLogin();
        Log.d("bus","");
        if (v == user_name) {

        } else if (v == hua_bei) {

        } else if (v == hua_ban) {

        } else if (v == user_share) {

        } else if (v == user_follow) {

        } else if (v == user_fans) {

        } else if (v == recharge) {

        } else if (v == bookshelf) {

        } else if (v == comment) {

        } else if (v == recharge_record) {

        } else if (v == records_of_consumption) {

        } else if (v == award_record) {

        } else if (v == remind) {

        } else if (v == phone) {

        } else if (v == setting) {
            ActivityUtil.toSettingActivity(getActivity());
        } else if (v == user_bg) {

        } else if (v == user_head) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case LOGIN_BACK://登录返回
                    getUserData();
                    break;
            }
        }
    }

    public void onEventMainThread(UpdaterPayEnum updateEnum) {
        if (mView != null) {
            if (updateEnum == UpdaterPayEnum.UPDATE_LOGIN_INFO) {//登录成功

            } else if (updateEnum == UpdaterPayEnum.UPDATE_LOGIN_OUT) {//退出登录

            } else if (updateEnum == UpdaterPayEnum.UPDATE_PAY_RESULT) {//

            }
        }
    }

    public void getUserData() {
        try {
            Log.d("userInfo", "用户信息");
            if (!AppUtil.isLogin(getActivity()))
                return;
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
                                Log.d("userInfo", userModelBase.getData().toString());
                                UserBean user = userModelBase.getData();
                                setUserData();

                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUserData() {
        Log.d("userr", "用户");
        GlideUtils.loadImage(user_head, UserBean.getInstance().getUser_avatar(), getActivity());
        hua_ban.setText(UserBean.getInstance().getUser_false_point() + "\n花瓣");
        hua_bei.setText(UserBean.getInstance().getUser_real_point() + "\n花贝");
        user_name.setText(UserBean.getInstance().getUser_nickname());
    }
}
