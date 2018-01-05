package com.spriteapp.booklibrary.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.spriteapp.booklibrary.callback.WebViewCallback;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.constant.WebConstant;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;

/**
 * Created by kuangxiaoguo on 2017/7/13.
 */

public class WebViewUtil {

    private static final String TAG = "WebViewUtil";
    private WebViewCallback mWebViewCallback;
    private static WebViewUtil mWebViewUtil;
    private String readUrl = "";

    /*
    setting	设置	huaxi://app?action=setting
add_shelf	添加到书架，会带其他参数	huaxi://app?action=add_shelf&book_id=14&chapter_id=16422
book_read	阅读书籍，会带其他参数	huaxi://app?action=book_read&book_id=14&chapter_id=16422
book_detail	书籍详细，会带其他参数	huaxi://app?action=book_detail&book_id=14
book_catalog	书籍目录，会带其他参数	huaxi://app?action=book_catalog&book_id=14
add_comment	写评论，会带其他参数	huaxi://app?action=add_comment&book_id=14&chapter_id=16422
pay	支付，会带其他参数	huaxi://app?action=pay&product_id=net.huaxi.1yuan
openpage	通过app代理打开网页，参数url就是需要打开的网页	huaxi://app?action=openpage&url=https%3a%2f%2fw.huaxi.net%2f7
     */

    /**
     * huaxi://app?action=pay&product_id=com.spriteapp.baisibdj.hb12&type=appwechat||appalipay||appapple
     */

    public static WebViewUtil getInstance() {
        if (mWebViewUtil == null) {
            synchronized (WebViewUtil.class) {
                if (mWebViewUtil == null) {
                    mWebViewUtil = new WebViewUtil();
                }
            }
        }
        return mWebViewUtil;
    }

    /**
     * 获取跳转WebViewActivity传递的url
     *
     * @param url h5页面点击url
     * @return 跳转url
     */
    public boolean getJumpUrl(Context context, String url) {
        String jumpUrl = "";
        if (StringUtil.isEmpty(url)) {
            return false;
        }
        readUrl = url;
        Uri uri = Uri.parse(url);
        String scheme = uri.getScheme();
        if (StringUtil.isEmpty(scheme)) {
            return false;
        }
        Log.d("alipay11", "uri===" + uri.toString());
        switch (scheme) {
            case WebConstant.SCHEME_PROMISE:
                String action = uri.getQueryParameter(WebConstant.ACTION_AUTHORITY);
                if (StringUtil.isEmpty(action)) {
                    return false;
                }

                switch (action) {
                    case WebConstant.OPEN_PAGE_AUTHORITY://书籍详情界面
                        jumpUrl = uri.getQueryParameter(WebConstant.URL_QUERY);
                        Uri uri1 = Uri.parse(jumpUrl);//拦截url中的参数并装换为uri
                        Log.d("jumpread", "jumpUrl===" + jumpUrl);

                        String bookId1 = uri1.getQueryParameter(WebConstant.BOOK_ID_QUERY);
                        if (bookId1 != null && !bookId1.isEmpty()) {//看书
                            BookDetailResponse detail = new BookDetailResponse();
                            detail.setBook_id(Integer.parseInt(bookId1));
                            detail.setChapter_id(0);
                            ActivityUtil.toReadActivity(context, detail);
                        } else {//充值
                            ActivityUtil.toWebViewActivity(context, jumpUrl);//打开WebViewActivity
                        }

                        break;
                    case WebConstant.BOOK_READ_AUTHORITY://免费阅读按钮
                        String bookId = uri.getQueryParameter(WebConstant.BOOK_ID_QUERY);
                        String chapterId = uri.getQueryParameter(WebConstant.CHAPTER_ID_QUERY);
                        Log.d("jumpread", "bookId===" + bookId + "chapterId===" + chapterId);
                        if (mWebViewCallback != null) {
                            mWebViewCallback.freeRead(Integer.parseInt(bookId), Integer.parseInt(chapterId));
                        }
                        break;
                    case WebConstant.ADD_SHELF_AUTHORITY://加入书架按钮
                        String addBookId = uri.getQueryParameter(WebConstant.BOOK_ID_QUERY);
                        if (mWebViewCallback != null) {
                            mWebViewCallback.addBookToShelf(Integer.valueOf(addBookId));
                        }
                        break;
                    case WebConstant.ADD_COMMENT_AUTHORITY:
                        String commentBookId = uri.getQueryParameter(WebConstant.BOOK_ID_QUERY);
                        ActivityUtil.toPublishCommentActivity(context, Integer.parseInt(commentBookId));
                        break;
                    case WebConstant.PAY_AUTHORITY:
                        String productId = uri.getQueryParameter(WebConstant.PRODUCT_ID_QUERY);
                        String type = uri.getQueryParameter(WebConstant.PRODUCT_TYPE);
                        //区分微信或者支付宝支付
                        Log.d("alipay", "productId===" + productId + "type===" + type);
                        if (mWebViewCallback != null) {
                            if (type != null && type.equals("appalipay")) {
                                mWebViewCallback.getAliPay(productId);//支付宝
                            } else if (type != null && type.equals("appswiftpassg")) {
                                jumpUrl = uri.getQueryParameter(WebConstant.URL_QUERY);
                                Log.d("alipay", "jumpUrl===" + jumpUrl + "uri==" + uri.toString());
//                                mWebViewCallback.getWeChatPay(productId);//微信
//                                mWebViewCallback.getWeChatPay(productId);//微信网页
//                                ActivityUtil.toWebViewActivity(context, "http://wxpay.weixin.qq.com/pub_v2/pay/wap.v2.php");
                            }
//                            mWebViewCallback.getAliPay(productId);//支付宝
//                            mWebViewCallback.getWeChatPay(productId);//微信
                        }
                        break;
                    case WebConstant.SETTING_AUTHORITY:
                        ActivityUtil.toSettingActivity(context);
                        break;
                    case WebConstant.LOGIN_AUTHORITY:
                        HuaXiSDK.getInstance().toLoginPage(context);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return WebConstant.SCHEME_PROMISE.equals(scheme);
    }

    public void setWebViewCallback(WebViewCallback mWebViewCallback) {
        this.mWebViewCallback = mWebViewCallback;
    }
}
