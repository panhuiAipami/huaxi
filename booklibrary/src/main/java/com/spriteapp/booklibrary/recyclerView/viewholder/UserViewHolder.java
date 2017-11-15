package com.spriteapp.booklibrary.recyclerView.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.LoginStateEnum;
import com.spriteapp.booklibrary.manager.NightModeManager;
import com.spriteapp.booklibrary.manager.StoreColorManager;
import com.spriteapp.booklibrary.model.UserModel;
import com.spriteapp.booklibrary.recyclerView.decorate.Visitable;
import com.spriteapp.booklibrary.recyclerView.model.StoreUser;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.AppUtil;

/**
 * 我的账户
 * Created by kuangxiaoguo on 2017/7/28.
 */

public class UserViewHolder extends BaseViewHolder<Visitable> {

    private TextView mRealPointTextView;
    private TextView mFalsePointTextView;
    private TextView mRechargeTextView;
    private Context mContext;
    private final View divideView;
    private String mLoginText;

    public UserViewHolder(View itemView, Context context) {
        super(itemView);
        mContext = context;
        mRealPointTextView = (TextView) itemView.findViewById(R.id.book_reader_real_point);
        mFalsePointTextView = (TextView) itemView.findViewById(R.id.book_reader_false_point);
        mRechargeTextView = (TextView) itemView.findViewById(R.id.book_reader_recharge_text_view);
        divideView = itemView.findViewById(R.id.book_reader_divide_view);
        mLoginText = mContext.getResources().getString(R.string.book_reader_is_loading);
    }

    @Override
    public void bindViewData(Visitable data) {
        if (!(data instanceof StoreUser)) {
            return;
        }
        StoreColorManager manager = NightModeManager.getManager();
        if (manager != null) {
            mRealPointTextView.setTextColor(manager.getPayTextColor());
            mFalsePointTextView.setTextColor(manager.getPayTextColor());
            mRechargeTextView.setTextColor(manager.getRechargeTextColor());
            divideView.setBackgroundColor(manager.getDivideViewBackground());
            mRechargeTextView.setBackgroundResource(manager.getRechargeTextSelector());
        }
        if (HuaXiSDK.mLoginState == LoginStateEnum.LOADING) {
            mRechargeTextView.setText(mLoginText);
        } else {
            if (HuaXiSDK.mLoginState == LoginStateEnum.FAILED) {
                mRechargeTextView.setText(mContext.getResources()
                        .getString(R.string.book_reader_reload_text));
            } else if (HuaXiSDK.mLoginState == LoginStateEnum.UN_LOGIN) {
                mRechargeTextView.setText(mContext.getResources()
                        .getString(R.string.book_reader_login_text));
            } else {
                mRechargeTextView.setText(mContext.getResources()
                        .getString(R.string.book_reader_recharge_text));
            }
        }
        mRechargeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoginText.equals(mRechargeTextView.getText().toString())) {
                    return;
                }
                if (AppUtil.isLogin()) {
                    ActivityUtil.toWebViewActivity(mContext, Constant.H5_PAY_URL);
                    return;
                }
                HuaXiSDK.getInstance().toLoginPage(mContext);
            }
        });
        StoreUser storeUser = (StoreUser) data;
        UserModel model = storeUser.getUserModel();
        if (model == null) {
            mRealPointTextView.setText("0");
            mFalsePointTextView.setText("0");
            return;
        }
        mRealPointTextView.setText(model.getUser_real_point() + "");
        mFalsePointTextView.setText(model.getUser_false_point() + "");
    }
}
