package net.huaxi.reader.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;

import com.tools.commonlibs.dialog.BaseDialog;
import net.huaxi.reader.book.datasource.DataSourceManager;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.db.model.BookTable;

import net.huaxi.reader.R;
import net.huaxi.reader.bean.ShareBean;
import net.huaxi.reader.db.dao.BookDao;
import net.huaxi.reader.util.LoginHelper;


/**
 * Created by Saud on 15/12/30.
 */
public class BookContentShareDialog extends BaseDialog {

    private Activity activity;
    private View ll_weixin_friend;
    private View ll_weixin_circle;
    private View ll_qq_qzone;
    private View llWeibo;
    private LoginHelper loginHelper;
    private ShareBean shareBean;

    public BookContentShareDialog(Activity activity) {
        this.activity = activity;
        initDialog(activity, null, R.layout.dialog_bookcontent_share, BaseDialog.TYPE_BOTTOM_CENTER_NOT_FOCUSABLE, true);
        mDialog.getWindow().setWindowAnimations(R.style.bottomPopupDialog);
        String bookId = DataSourceManager.getSingleton().getBookId();
        BookTable bookTable = BookDao.getInstance().findBook(bookId);
        shareBean = new ShareBean();
        shareBean.setShareUrl(bookTable.getWebsite());
        shareBean.setImgUrl(bookTable.getCoverImageId());
        shareBean.setTitle(bookTable.getName());
        shareBean.setDesc(bookTable.getBookDesc());
        ll_weixin_friend = mDialog.findViewById(R.id.ll_weixin_friend);
        ll_weixin_circle = mDialog.findViewById(R.id.ll_weixin_circle);
        ll_qq_qzone = mDialog.findViewById(R.id.ll_qq_qzone);
        llWeibo = mDialog.findViewById(R.id.ll_weibo);

        ll_weixin_friend.setOnClickListener(customListener);
        ll_weixin_circle.setOnClickListener(customListener);
        ll_qq_qzone.setOnClickListener(customListener);
        llWeibo.setOnClickListener(customListener);
    }

    //其他地方用的分享弹窗
    public BookContentShareDialog(Activity activity, ShareBean shareBean) {
        this.activity = activity;
        this.shareBean = shareBean;
        initDialog(activity, null, R.layout.dialog_bookcontent_share, BaseDialog.TYPE_BOTTOM, true);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.flags = lp.flags | (WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mDialog.getWindow().setAttributes(lp);
        mDialog.getWindow().setWindowAnimations(R.style.bottomPopupDialog);

        ll_weixin_friend = mDialog.findViewById(R.id.ll_weixin_friend);
        ll_weixin_circle = mDialog.findViewById(R.id.ll_weixin_circle);
        ll_qq_qzone = mDialog.findViewById(R.id.ll_qq_qzone);
        llWeibo = mDialog.findViewById(R.id.ll_weibo);
        ll_weixin_friend.setOnClickListener(customListener);
        ll_weixin_circle.setOnClickListener(customListener);
        ll_qq_qzone.setOnClickListener(customListener);
        llWeibo.setOnClickListener(customListener);
    }

    View.OnClickListener customListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (shareBean != null)
                switch (v.getId()) {
                    case R.id.ll_weixin_friend:
                        getLoginHelper(shareBean).shareWxWebPage(shareBean.getShareUrl(), true, shareBean.getImgUrl(), shareBean.getTitle(),
                                shareBean.getDesc());
                        shareBean.shareType=1;
                        mDialog.dismiss();
                        break;
                    case R.id.ll_weixin_circle:
                        getLoginHelper(shareBean).shareWxWebPage(shareBean.getShareUrl(), false, shareBean.getImgUrl(), shareBean.getTitle(),
                                shareBean.getDesc());
                        shareBean.shareType=2;
                        mDialog.dismiss();
                        break;
                    case R.id.ll_qq_qzone:
                        try {
                            getLoginHelper(shareBean).shareToQzone(shareBean.getShareUrl(), shareBean.getImgUrl(), shareBean.getTitle(), shareBean
                                    .getDesc());
                            shareBean.shareType=3;
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        mDialog.dismiss();
                        break;
                    case R.id.ll_weibo:
                        getLoginHelper(shareBean).shareWb(shareBean.getShareUrl(), shareBean.getImgUrl(), shareBean.getTitle(), shareBean.getDesc());
                        shareBean.shareType=4;
                        mDialog.dismiss();
                        break;
                    default:
                        break;
                }
        }
    };

    public Dialog getDialog() {
        return mDialog;
    }

    public void show() {
        mDialog.show();
    }


    private LoginHelper getLoginHelper() {
        if (loginHelper == null) {
            loginHelper = new LoginHelper(activity);
            AppContext.setLoginHelper(loginHelper);
        }
        return loginHelper;
    }

    private LoginHelper getLoginHelper(ShareBean bean) {
        if (loginHelper == null) {
            loginHelper = new LoginHelper(activity,bean);
            AppContext.setLoginHelper(loginHelper);
        }
        return loginHelper;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        getLoginHelper().ActivityResult(requestCode, resultCode, data);
    }
}
