package net.huaxi.reader.dialog;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;

import net.huaxi.reader.R;
import net.huaxi.reader.activity.ShareActivity;
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
    public static boolean isFinish;
    public static boolean isWeChat;
    private String title = null;

    //其他地方用的分享弹窗
    public ShareDialog(Activity activity, ShareBean shareBean, int type) {
        isFinish = true;
        isWeChat = false;
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
//        if (share_type == 2) {
//            ll_qq_qzone.setVisibility(View.GONE);
//            ll_qq.setVisibility(View.VISIBLE);
//        }
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
                            isFinish = false;
                            isWeChat = true;
                            break;
                        case R.id.ll_weixin_circle:
                            type = 2;
                            name = "微信";
                            share_media = WEIXIN_CIRCLE;
                            share(WEIXIN_CIRCLE, name);
                            isFinish = false;
                            break;
                        case R.id.ll_weixin_shoucang:
                            type = 3;
                            name = "微信";
                            share_media = WEIXIN_FAVORITE;
                            share(WEIXIN_FAVORITE, name);
                            isFinish = false;
                            break;
                        case R.id.ll_sina_weibo:
                            type = 0;
                            name = "新浪微博";
                            share(SINA, name);
                            isFinish = false;
                            break;
                        case R.id.ll_qq:
                            name = "QQ";
                            share(QQ, name);
                            isFinish = false;
                            break;
                        case R.id.ll_qq_qzone:
                            name = "QQ";
                            share(QZONE, name);
                            isFinish = false;
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
        UMWeb web;
        if (share_type == 1) {
            UMImage thumb = new UMImage(activity, shareBean.getImgUrl());
            MLog.d("thum", shareBean.getImgUrl());
            web = new UMWeb(shareBean.getShareUrl());
            web.setThumb(thumb);
            web.setDescription(shareBean.getDesc());
            web.setTitle(shareBean.getTitle());
        } else{
            UMImage thumb = new UMImage(activity, "http://img.zcool.cn/community/0142135541fe180000019ae9b8cf86.jpg@1280w_1l_2o_100sh.png");
            web = new UMWeb("http://baidu.com");
            web.setThumb(thumb);
            if (type == 1) {
                title = "你想要的,我都懂,看小说也能赚钱的神器!";
            } else if (type == 2) {
                title = "划时代小说阅读神器,看小说也能赚钱!";
            } else if (type == 3) {
                title = "全新模式,读小说赚现金!";
            }
            web.setDescription(TextUtils.isEmpty(title) ? "划时代小说阅读神器,看小说也能赚钱!" : title);
//            web.setTitle(shareBean.getTitle());
        }


        new ShareAction(activity).withMedia(web).setPlatform(Platform).setCallback(new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                Log.d("share_qq", "成功");
                if (net.huaxi.reader.callback.ListenerManager.getInstance().getResult() != null)
                    net.huaxi.reader.callback.ListenerManager.getInstance().getResult().success();
                finishShareActivity();

            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                Log.d("share_qq", "失败");
                if (net.huaxi.reader.callback.ListenerManager.getInstance().getResult() != null)
                    net.huaxi.reader.callback.ListenerManager.getInstance().getResult().faild();
                finishShareActivity();

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                Log.d("share_qq", "取消");
                if (net.huaxi.reader.callback.ListenerManager.getInstance().getResult() != null)
                    net.huaxi.reader.callback.ListenerManager.getInstance().getResult().cancel();
                finishShareActivity();

            }
        }).setShareboardclickCallback(new ShareBoardlistener() {
            @Override
            public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
                Log.d("share_qq", "setShareboardclickCallback");
            }
        }).share();
    }

    private void finishShareActivity() {
        if (activity != null && activity instanceof ShareActivity) {
            ShareActivity shareActivity = (ShareActivity) activity;
            shareActivity.qq_finish();
        }
    }

}
