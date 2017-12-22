package com.spriteapp.booklibrary.ui.view;

import com.spriteapp.booklibrary.base.BaseView;
import com.spriteapp.booklibrary.model.response.PayResponse;

/**
 * Created by kuangxiaoguo on 2017/7/8.
 */

public interface WebViewView extends BaseView<Void> {

    void setAliPayResult(PayResponse result);
    void setWechatPayResult(PayResponse result);
}
