package com.spriteapp.booklibrary.ui.fragment;

import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.UpdaterPayEnum;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.widget.ReaderWebView;

import de.greenrobot.event.EventBus;

/**
 * Created by kuangxiaoguo on 2017/7/7.
 */

public class MeFragment extends BaseFragment {

    private ReaderWebView mWebView;

    @Override
    public int getLayoutResId() {
        return R.layout.book_reader_fragment_me;
    }

    @Override
    public void initData() {
        EventBus.getDefault().register(this);
        mWebView.setWebChromeClient(mChromeClient);
        if (!AppUtil.isNetAvailable(getContext())) {
            return;
        }
        mWebView.loadPage(Constant.BOOK_ME_URL);

    }

    @Override
    public void configViews() {
    }

    @Override
    public void findViewId() {
        mWebView = (ReaderWebView) mParentView.findViewById(R.id.book_reader_me_web_view);
    }

    WebChromeClient mChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }
    };

    @Override
    protected void lazyLoad() {
        if (mHasLoadedOnce || !isPrepared) {
            return;
        }
        mHasLoadedOnce = true;
        initData();
    }

    public void onEventMainThread(UpdaterPayEnum updateEnum) {
        if (mWebView != null) {
            if (updateEnum == UpdaterPayEnum.UPDATE_LOGIN_INFO
                    || updateEnum == UpdaterPayEnum.UPDATE_LOGIN_OUT) {
                mWebView.loadPage(Constant.BOOK_ME_URL);
            } else if (updateEnum == UpdaterPayEnum.UPDATE_PAY_RESULT) {
                Log.d("me===", "url===" + Constant.BOOK_ME_URL);
                mWebView.reload();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
