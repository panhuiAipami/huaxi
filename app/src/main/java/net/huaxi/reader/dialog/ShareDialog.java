package net.huaxi.reader.dialog;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import net.huaxi.reader.R;
import net.huaxi.reader.bean.ShareBean;
import net.huaxi.reader.utils.MLog;
import net.huaxi.reader.utils.ToastUtil;

import static com.umeng.socialize.bean.SHARE_MEDIA.QQ;
import static com.umeng.socialize.bean.SHARE_MEDIA.QZONE;
import static com.umeng.socialize.bean.SHARE_MEDIA.SINA;
import static com.umeng.socialize.bean.SHARE_MEDIA.WEIXIN;
import static com.umeng.socialize.bean.SHARE_MEDIA.WEIXIN_CIRCLE;
import static com.umeng.socialize.bean.SHARE_MEDIA.WEIXIN_FAVORITE;


/**
 * Created by panhui on 17/8/8.
 */
public class ShareDialog extends BaseDialog {

    private Activity activity;
    private View ll_weixin_friend;
    private View ll_weixin_circle;
    private View ll_weixin_shoucang;
    private View ll_sina_weibo;
    private View ll_qq;
    private View ll_qq_qzone;
    private ShareBean shareBean;
    SHARE_MEDIA share_media = WEIXIN;
    private int share_type = 1;

    //其他地方用的分享弹窗
    public ShareDialog(Activity activity, ShareBean shareBean, int type) {
        initDialog(activity, shareBean, type);
    }

    public void initDialog(Activity activity, ShareBean shareBean, int type) {
        this.activity = activity;
        this.shareBean = shareBean;
        this.share_type = type;
        initDialog(activity, null, R.layout.book_share_layout, BaseDialog.TYPE_BOTTOM, true);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.flags = lp.flags | (WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mDialog.getWindow().setAttributes(lp);
        mDialog.getWindow().setWindowAnimations(R.style.bottomPopupDialog);


        ll_weixin_friend = mDialog.findViewById(R.id.ll_weixin_friend);
        ll_weixin_circle = mDialog.findViewById(R.id.ll_weixin_circle);
        ll_weixin_shoucang = mDialog.findViewById(R.id.ll_weixin_shoucang);
        ll_sina_weibo = mDialog.findViewById(R.id.ll_sina_weibo);
        ll_qq = mDialog.findViewById(R.id.ll_qq);
        ll_qq_qzone = mDialog.findViewById(R.id.ll_qq_qzone);
        ll_weixin_friend.setOnClickListener(customListener);
        ll_weixin_circle.setOnClickListener(customListener);
        ll_weixin_shoucang.setOnClickListener(customListener);
        ll_sina_weibo.setOnClickListener(customListener);
        ll_qq.setOnClickListener(customListener);
        ll_qq_qzone.setOnClickListener(customListener);
    }

    int type = 0;
    View.OnClickListener customListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = null;
            try {
                if (shareBean != null) {
                    switch (v.getId()) {
                        case R.id.ll_weixin_friend:
                            type = 1;
                            name = "微信";
                            share_media = WEIXIN;
                            share(WEIXIN, name);
                            break;
                        case R.id.ll_weixin_circle:
                            type = 2;
                            name = "微信";
                            share_media = WEIXIN_CIRCLE;
                            share(WEIXIN_CIRCLE, name);
                            break;
                        case R.id.ll_weixin_shoucang:
                            type = 3;
                            name = "微信";
                            share_media = WEIXIN_FAVORITE;
                            share(WEIXIN_FAVORITE, name);
                            break;
                        case R.id.ll_sina_weibo:
                            type = 0;
                            name = "新浪微博";
                            share(SINA, name);
                            break;
                        case R.id.ll_qq:
                            name = "QQ";
                            share(QQ, name);
                            break;
                        case R.id.ll_qq_qzone:
                            name = "QQ";
                            share(QZONE, name);
                            break;
                        default:
                            break;
                    }
                    mDialog.dismiss();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    public void show() {
        mDialog.show();
    }


    private void share(SHARE_MEDIA Platform, String name) throws Exception {
        boolean install = UMShareAPI.get(activity).isInstall(activity, Platform);
        if (!install) {
            ToastUtil.showLong("请先安装" + name + "客户端");
            return;
        }
        UMImage thumb = new UMImage(activity, shareBean.getImgUrl());
        MLog.d("thum", shareBean.getImgUrl());
        UMWeb web = new UMWeb(shareBean.getShareUrl());
        web.setThumb(thumb);
        web.setDescription(shareBean.getDesc());
        web.setTitle(shareBean.getTitle());

        new ShareAction(activity).withMedia(web).setPlatform(Platform).setCallback(new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                ToastUtil.showLong("分享成功");

            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                ToastUtil.showLong("分享失败");

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                ToastUtil.showLong("分享失败");

            }
        }).share();
    }

}
