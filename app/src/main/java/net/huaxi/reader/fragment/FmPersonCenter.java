package net.huaxi.reader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.DateUtils;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.StringUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.activity.ConsumeRecordActivity;
import net.huaxi.reader.activity.LoginActivity;
import net.huaxi.reader.activity.MyInfoActivity;
import net.huaxi.reader.activity.NewMessageActivity;
import net.huaxi.reader.activity.RechargeRecordActivity;
import net.huaxi.reader.activity.SettingActivity;
import net.huaxi.reader.activity.SimpleWebViewActivity;
import net.huaxi.reader.bean.User;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.JavaScript;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.dialog.GiveCoinsDailog;
import net.huaxi.reader.dialog.RechargeDialog;
import net.huaxi.reader.https.GetRequest;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.https.XSKEY;
import net.huaxi.reader.util.EventBusUtil;
import net.huaxi.reader.util.ImageUtil;
import net.huaxi.reader.util.UMEventAnalyze;
import net.huaxi.reader.view.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//个人中心fragment
public class FmPersonCenter extends BaseFragment implements View.OnClickListener {

    private ImageView ivSetting;
    private CircleImageView ivHeadIcon;
    private TextView tvUsername, tvCharge, tvVip, tvLogin;
    private RelativeLayout rlUsername, rlCharge, rlVip, rlNews, rlConsume, rlSetting;
    private ImageView ivCrown;
    private RelativeLayout rlReport, rlHelpCenter;
    private RechargeDialog rechargeDialog;
    private JavaScript javaScript;
    private RelativeLayout rech;
    private TextView balance_coins_text;

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fm_person, container, false);
        initView(view);
        initEvent();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        if(!UserHelper.getInstance().isLogin())
        balance_coins_text.setText("");
        initData();
        ivSetting.setClickable(true);
        UMEventAnalyze.countEvent(getActivity(), UMEventAnalyze.USER_PAGE);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            setUserdesc();
        }
    }

    private void initView(View view) {
        //显示账户花贝花瓣余额
        balance_coins_text = (TextView) view.findViewById(R.id.balance_coins);
        rech= (RelativeLayout) view.findViewById(R.id.rech);
        //初始化充值dialog窗口
        rechargeDialog = new RechargeDialog(getActivity());
        //实例化JavaScript
        javaScript = new JavaScript(getActivity(),null);
        ivCrown = (ImageView) view.findViewById(R.id.usercenter_userhead_crown_imageview);
        ivSetting = (ImageView) view.findViewById(R.id.usercenter_setting_imageview);
        ivHeadIcon = (CircleImageView) view.findViewById(R.id.usercenter_userhead_imageview);
        tvUsername = (TextView) view.findViewById(R.id.usercenter_username_textview);
        tvCharge = (TextView) view.findViewById(R.id.usercenter_charge_textview);
        tvVip = (TextView) view.findViewById(R.id.usercenter_vip_textview);
        rlUsername = (RelativeLayout) view.findViewById(R.id.usercenter_username_layout);
        rlCharge = (RelativeLayout) view.findViewById(R.id.usercenter_charge_layout);

        rlVip = (RelativeLayout) view.findViewById(R.id.usercenter_vip_layout);
        rlVip.setVisibility(View.GONE);
        rlNews = (RelativeLayout) view.findViewById(R.id.usercenter_news_layout);
        rlNews.setVisibility(view.GONE);
        rlConsume = (RelativeLayout) view.findViewById(R.id.usercenter_consume_layout);

        rlSetting = (RelativeLayout) view.findViewById(R.id.usercenter_setting_layout);
        tvLogin = (TextView) view.findViewById(R.id.usercenter_please_login_textview);
        rlReport = (RelativeLayout) view.findViewById(R.id.setting_report_layout);
        rlReport.setVisibility(View.GONE);
        rlHelpCenter = (RelativeLayout) view.findViewById(R.id.setting_help_center_layout);
        rlHelpCenter.setVisibility(view.GONE);
    }

    private void initEvent() {
        ivSetting.setOnClickListener(this);
        rlUsername.setOnClickListener(this);
        rlCharge.setOnClickListener(this);
        rlVip.setOnClickListener(this);
        rlNews.setOnClickListener(this);
        rlConsume.setOnClickListener(this);
        rlSetting.setOnClickListener(this);
        rlReport.setOnClickListener(this);
        rlHelpCenter.setOnClickListener(this);
        rech.setOnClickListener(this);
    }


    public void initData() {

        if (!NetUtils.checkNetworkUnobstructed()) {
            LogUtils.debug("UserHelper.getInstance().isLogin()===" + UserHelper.getInstance().isLogin());
            if (UserHelper.getInstance().isLogin()) {
                balance_coins_text.setVisibility(View.VISIBLE);
                setUserdesc();
            } else {
                balance_coins_text.setVisibility(View.GONE);
                setUserNotLogin();
            }
            return;
        }

        GetRequest request = new GetRequest(URLConstants.USER_DETAIL + "?1=1" + CommonUtils
                .getPublicGetArgs(), new Response
                .Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.debug("response请求个人信息==" + response.toString());
                int errorid = ResponseHelper.getErrorId(response);
                if (errorid == -100005) {
                    setUserNotLogin();
                    return;
                }
                if (!ResponseHelper.isSuccess(response)) {
                    setUserdesc();
                    return;
                }
                tvLogin.setVisibility(View.GONE);
                User user = null;
                user = new Gson().fromJson(ResponseHelper.getVdata(response).toString(), User
                        .class);
                UserHelper.getInstance().setUser(user);
                EventBus.getDefault().post(new EventBusUtil.EventBean(EventBusUtil.EVENTMODEL_MIGRATE, EventBusUtil.EVENTTYPE_MIGRATE_SHOW_ONE));
                setUserdesc();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.debug(error.getMessage());
                setUserdesc();
            }
        });
        RequestQueueManager.addRequest(request);

        GetRequest payinforequest = new GetRequest(URLConstants.PAY_INFO + "?1=1" + CommonUtils
                .getPublicGetArgs(), new Response
                .Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int hua=0;
                LogUtils.debug("response==" + response);
                if (!ResponseHelper.isSuccess(response)) {
                    tvCharge.setText("");
                    tvVip.setText("");
                } else {
                    JSONObject jsonObject = ResponseHelper.getVdata(response).optJSONObject("info");
                    if (jsonObject == null) {
                        return;
                    }
                    String coins = jsonObject.optString(XSKEY.USER_INFO.COIN);
                    String petals = jsonObject.optString(XSKEY.USER_INFO.PETALS);
                    if(coins!=null&&petals!=null){
                        int coins_int = Integer.parseInt(coins);
                        int petals_int = Integer.parseInt(petals);
                        if(coins_int<petals_int){
                            coins_int=0;
                        }else {
                            hua=coins_int-petals_int;
                        }
                    }
//                    tvCharge.setText(coins + " 花贝");

                    if(UserHelper.getInstance().isLogin()){
                        balance_coins_text.setText("余额：花贝："+hua+"  花瓣："+petals);
                    }else {
                        balance_coins_text.setText("");
                        balance_coins_text.setVisibility(View.GONE);
                    }

                    long time = jsonObject.optLong(XSKEY.USER_INFO.EXPIRE);
                    long t = time * 1000;
                    long t1 = System.currentTimeMillis();//本地0区时间戳
                    if (t > t1) {
                        tvVip.setText(DateUtils.simpleDateFormat(new Date(time * 1000), "yyyy.MM.dd") + " 到期");
                        ivCrown.setImageResource(R.mipmap.usercenter_crown_pressed);
                    } else {
                        ivCrown.setImageResource(R.mipmap.usercenter_crown_normal);
                        tvVip.setText("未开启");
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.debug(error.getMessage());
            }
        }, "1.1");
        RequestQueueManager.addRequest(payinforequest);

    }

    /**
     * 设置用户信息
     */
    private void setUserdesc() {

        if (UserHelper.getInstance().isLogin()) {
            User user = UserHelper.getInstance().getUser();
            String username = user.getNickname();
            String imageUrl=user.getImgid();
            Log.i("imageurl", "setUserdesc: 用户头像路径"+imageUrl.toString());
            if (StringUtils.isNotEmpty(username)) {
                tvUsername.setText(username + "");
            }
//            if (StringUtils.isNotEmpty(user.getImgid())) {
//                ImageUtil.loadImage(getActivity(), user.getImgid(), ivHeadIcon, R.mipmap.usercenter_default_icon);
//            }
            if(StringUtils.isNotEmpty(imageUrl)){
                ImageUtil.loadImage(getActivity(),imageUrl,ivHeadIcon,R.mipmap.main_navigation_bookshelf_normal1);
            }
        } else {
            setUserNotLogin();
        }
    }

    private void setUserNotLogin() {
        LogUtils.debug("请先登录");
        ivCrown.setImageResource(R.mipmap.usercenter_crown_normal);
        ivHeadIcon.setImageResource(R.mipmap.usercenter_default_icon);
        tvUsername.setText("点击登录");
        tvCharge.setText("");
        tvVip.setText("");
        tvLogin.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == rlUsername.getId()) {
            if (!UserHelper.getInstance().isLogin()) {
                UMEventAnalyze.countEvent(getActivity(), UMEventAnalyze.USER_TO_LOGIN);
                startActivity(new Intent(getActivity(), LoginActivity.class));
                return;
            }
            Intent intent = new Intent(getActivity(), MyInfoActivity.class);
            startActivity(intent);
        } else if (v.getId() == rlCharge.getId()) {
            if (!UserHelper.getInstance().isLogin()) {
                UMEventAnalyze.countEvent(getActivity(), UMEventAnalyze.USER_TO_LOGIN);
                startActivity(new Intent(getActivity(), LoginActivity.class));

                return;
            }
            UMEventAnalyze.countEvent(getActivity(), UMEventAnalyze.USER_CHARGE);
//            Intent intent = new Intent(getActivity(), SimpleWebViewActivity.class);
//            intent.putExtra("webtype", SimpleWebViewActivity.WEBTYPE_RECHARGE);
//            startActivity(intent);
            //调用充值dialog窗口
            getRechargeDialog();
        } else if (v.getId() == rlVip.getId()) {
            if (!UserHelper.getInstance().isLogin()) {
                UMEventAnalyze.countEvent(getActivity(), UMEventAnalyze.USER_TO_LOGIN);
                startActivity(new Intent(getActivity(), LoginActivity.class));
                return;
            }
            UMEventAnalyze.countEvent(getActivity(), UMEventAnalyze.USER_VIP);
            Intent intent = new Intent(getActivity(), SimpleWebViewActivity.class);
            intent.putExtra("webtype", SimpleWebViewActivity.WEBTYPE_VIP);
            startActivity(intent);
        } else if (v.getId() == rlNews.getId()) {
            if (!UserHelper.getInstance().isLogin()) {
                UMEventAnalyze.countEvent(getActivity(), UMEventAnalyze.USER_TO_LOGIN);
                startActivity(new Intent(getActivity(), LoginActivity.class));
                return;
            }
            Intent intent = new Intent(getActivity(), NewMessageActivity.class);
            startActivity(intent);
        } else if (v.getId() == rlConsume.getId()) {
            if (!UserHelper.getInstance().isLogin()) {
                UMEventAnalyze.countEvent(getActivity(), UMEventAnalyze.USER_TO_LOGIN);
                startActivity(new Intent(getActivity(), LoginActivity.class));
                return;
            }
            UMEventAnalyze.countEvent(getActivity(), UMEventAnalyze.USER_COMSUME);
            Intent intent = new Intent(getActivity(), ConsumeRecordActivity.class);
            startActivity(intent);
        }
        Intent intent = null;
        switch (v.getId()) {
            case R.id.rech:
                if (!UserHelper.getInstance().isLogin()) {
                    UMEventAnalyze.countEvent(getActivity(), UMEventAnalyze.USER_TO_LOGIN);
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
                Intent intentrech = new Intent(getActivity(), RechargeRecordActivity.class);
                startActivity(intentrech);
                UMEventAnalyze.countEvent(getActivity(), UMEventAnalyze.RECHARGE_HISTORY);
                break;
            case R.id.usercenter_setting_layout:
                intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
                ivSetting.setClickable(false);
                UMEventAnalyze.countEvent(getActivity(), UMEventAnalyze.USER_SETTING);
                break;
            case R.id.setting_report_layout:
                initAliFeedBack();
                UMEventAnalyze.countEvent(getActivity(), UMEventAnalyze.USER_REPORT);
                break;
            case R.id.setting_help_center_layout:
                intent = new Intent(getActivity(), SimpleWebViewActivity.class);
                intent.putExtra(SimpleWebViewActivity.WEBTYPE, SimpleWebViewActivity.WEBTYPE_HELP_CENTER);
                startActivity(intent);
                UMEventAnalyze.countEvent(getActivity(), UMEventAnalyze.USER_HELP);
                break;

            default:
                break;
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Subscribe
    public void onEvent(EventBusUtil.EventBean eventBean) {
        if (EventBusUtil.EVENTMODEL_GIVE_COINS == eventBean.getModle() && EventBusUtil.EVENTTYPE_GIVE_COINS == eventBean.getType()) {
            String desc = eventBean.getDesc();
            showGiveCoinsDialog(desc);
        }
    }

    private void showGiveCoinsDialog(String desc) {
        GiveCoinsDailog giveCoinsDailog = new GiveCoinsDailog(getActivity(), desc);
        if (!giveCoinsDailog.isShowing()) {
            giveCoinsDailog.show();
        }
    }

    private void initAliFeedBack() {
        FeedbackAPI.openFeedbackActivity(getActivity());
        Map<String, String> map = new HashMap<String, String>();
        /*
        * 可以设置UI自定义参数，如主题色等,具体为：
        * enableAudio(是否开启语音 1：开启 0：关闭)
        * bgColor(消息气泡背景色 "#ffffff")，
        * color(消息内容文字颜色)，
        * avatar(当前登录账号的头像)，
        * toAvatar(客服账号的头像)
        * themeColor(标题栏自定义颜色 "#ffffff")
        */
        map.put("enableAudio", "1");
        map.put("themeColor", "#F9494D");
        if (UserHelper.getInstance().getUser().getImgid() != null) {
            LogUtils.debug("UserHelper.getInstance().getUser().getImgid()==" + UserHelper.getInstance().getUser().getImgid());
            map.put("avatar", UserHelper.getInstance().getUser().getImgid());
        }
        FeedbackAPI.setUICustomInfo(map);
    }
    //显示充值dialog
    public void getRechargeDialog(){
        rechargeDialog.show();
        rechargeDialog.getDialog().findViewById(R.id.dialgo_Recharge1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                javaScript.pay("1","2","","3000","充值","1111");
                rechargeDialog.cancel();
            }
        });
        rechargeDialog.getDialog().findViewById(R.id.dialgo_Recharge2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                javaScript.pay("1","2","","5000","充值","1111");
                rechargeDialog.cancel();
            }
        });
        rechargeDialog.getDialog().findViewById(R.id.dialgo_Recharge3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                javaScript.pay("1","2","","9800","充值","1111");
                rechargeDialog.cancel();
            }
        });
    }

}
