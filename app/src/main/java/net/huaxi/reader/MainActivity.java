package net.huaxi.reader;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.spriteapp.booklibrary.config.HuaXiConfig;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.listener.ChannelListener;
import com.spriteapp.booklibrary.model.WeChatBean;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.activity.HomeActivity;
import com.spriteapp.booklibrary.ui.activity.WebViewActivity;
import com.switfpass.pay.MainApplication;
import com.switfpass.pay.activity.PayPlugin;
import com.switfpass.pay.bean.RequestMsg;

import net.huaxi.reader.activity.ShareActivity;
import net.huaxi.reader.bean.ShareBean;
import net.huaxi.reader.dialog.ShareDialog;
import net.huaxi.reader.utils.LoginHelper;
import net.huaxi.reader.utils.PreferenceHelper;

import static com.spriteapp.booklibrary.ui.activity.HomeActivity.ADVERTISEMENT;
import static com.spriteapp.booklibrary.ui.activity.HomeActivity.BOOK_ID;
import static com.spriteapp.booklibrary.ui.activity.HomeActivity.CHAPTER_ID;
import static com.spriteapp.booklibrary.ui.activity.HomeActivity.SEX;
import static com.spriteapp.booklibrary.ui.activity.HomeActivity.libActivity;

public class MainActivity extends AppCompatActivity {
    public static final String SEXTIME = "sextime";
    private static final String TAG = "MainActivity";


//    public static final String SIGN_SECRET = "fygopf7cixub8cpkh1oruik2byt2ykvkh81sy6";
//    public static final int CLIENT_ID = 40;


    private static final int CHANNEL_ID = 2;
    public static final String SHAREDATA = "share_data";
    public static final String SHARETYPE = "share_type";
    private ShareDialog shareDialog;

    private ShareBean shareBean;
    private int toJump = 0;//0默认打开阅读页， 1打开广告的H5, 2打开推送的H5


    private String book_id = null;
    private String chapter_id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PreferenceHelper.putBoolean(SEXTIME, false);//存入本地性别选择
        Intent intent = getIntent();

        String action = intent.getAction();
        String push_url = null;
        if (Intent.ACTION_VIEW.equals(action)) {//从网页跳转过来
            Uri uri = intent.getData();
            if (uri != null) {
                Log.d("MainActivity--", "从网页跳转过来" + uri);
                book_id = uri.getQueryParameter(BOOK_ID);
                chapter_id = uri.getQueryParameter(CHAPTER_ID);
            }
        } else {//推送
            book_id = intent.getStringExtra(BOOK_ID);
            chapter_id = intent.getStringExtra(CHAPTER_ID);
            push_url = intent.getStringExtra(WebViewActivity.LOAD_URL_TAG);
        }

        toJump = intent.getIntExtra(ADVERTISEMENT, 0);
        HuaXiConfig config = new HuaXiConfig.Builder().setContext(this)
                .setChannelListener(new ChannelListener() {
                    @Override
                    public void toLoginPage(Context context) {
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivityForResult(intent, LoginHelper.login_back);
                    }

                    @Override
                    public void showShareDialog(Context context, BookDetailResponse shareDetail, String imagePath, boolean isNightMode, int type) {
//                        Toast.makeText(context, "分享", Toast.LENGTH_SHORT).show();
                        try {
                            if (shareDetail != null && type == 1) {
                                shareBean = new ShareBean();
                                shareBean.setTitle(TextUtils.isEmpty(shareDetail.getBook_name()) ? "" : shareDetail.getBook_name());
                                shareBean.setDesc(TextUtils.isEmpty(shareDetail.getBook_intro()) ? "" : shareDetail.getBook_intro());
                                shareBean.setImgUrl(TextUtils.isEmpty(shareDetail.getBook_image()) ? "" : shareDetail.getBook_image());
                                shareBean.setShareUrl(TextUtils.isEmpty(shareDetail.getBook_share_url()) ? "" : shareDetail.getBook_share_url());
                                shareBean.setNid(shareDetail.getBook_id());

//                        ListenerManager.getInstance().getShareBeanCallBack().getShareBean(shareBean);
                                Intent intent = new Intent(MainActivity.this, ShareActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(SHAREDATA, shareBean);
                                bundle.putInt(SHARETYPE, type);
                                intent.putExtras(bundle);
                                Log.d("showShareDialog", "showShareDialog");
                                startActivity(intent);
                            } else if (shareDetail != null && type == 2) {//收徒分享
                                shareBean = new ShareBean();
                                Intent intent = new Intent(MainActivity.this, ShareActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(SHAREDATA, shareBean);
                                bundle.putInt(SHARETYPE, type);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            } else if (type == 3) {//图片分享
                                shareBean = new ShareBean();
                                shareBean.setImagePath(imagePath);
                                Intent intent = new Intent(MainActivity.this, ShareActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(SHAREDATA, shareBean);
                                bundle.putInt(SHARETYPE, type);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void toWXPay(WeChatBean response, String push_id) {
                        if (response == null && push_id != null && !push_id.isEmpty()) {//回调友盟
                            MyApplication.umengNotificClick(push_id);
                        } else if (response != null && push_id == null) {//微信支付
                            wxPay(response);
                        }
                    }
                })
                .setChannelId(CHANNEL_ID)
                .setClientId(HomeActivity.CLIENT_ID)
                .setSex(PreferenceHelper.getInt(SEX, 0))//性别
                .setSignSecret(HomeActivity.SIGN_SECRET).build();
        HuaXiSDK.getInstance().init(config);
        Intent intent1 = new Intent(this, HomeActivity.class);
        intent1.putExtra(ADVERTISEMENT, toJump);
        intent1.putExtra(WebViewActivity.LOAD_URL_TAG, push_url);
        intent1.putExtra(BOOK_ID, book_id);
        intent1.putExtra(CHAPTER_ID, chapter_id);
        startActivity(intent1);
        finish();

    }


    public void wxPay(WeChatBean response) {//微信支付
//        IWXAPI WXApi = WXAPIFactory.createWXAPI(this, LoginHelper.WX_APP_ID, true);
//        if (WXApi.isWXAppInstalled()) {
        RequestMsg msg = new RequestMsg();
        msg.setTokenId(response.getToken_id());
        msg.setTradeType(MainApplication.WX_APP_TYPE);
        msg.setAppId(LoginHelper.WX_APP_ID);
        PayPlugin.unifiedAppPay(libActivity, msg);
//        } else {
//            showToast("请先安装微信");
//        }

    }
}
