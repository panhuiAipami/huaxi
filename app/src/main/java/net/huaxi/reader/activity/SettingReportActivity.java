package net.huaxi.reader.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.tools.commonlibs.activity.BaseActivity;

import net.huaxi.reader.R;

import java.util.HashMap;
import java.util.Map;

public class SettingReportActivity extends BaseActivity {
    private ImageView ivBack;
    private EditText etReport;
    private TextView tvSubmit;

    //    FeedbackAgent fb;
//    private FeedbackFragment mFeedbackFragment;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //得到view视图窗口
        Window window = getActivity().getWindow();
        //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(getResources().getColor(R.color.c01_themes_color));
        setContentView(R.layout.activity_setting_report);
        initView();
        initEvent();
        initAliFeedBack();

    }

    private void initAliFeedBack() {
        FeedbackAPI.openFeedbackActivity(SettingReportActivity.this);
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
        map.put("enableAudio","1" );
        map.put("themeColor","#F9494D" );
        FeedbackAPI.setUICustomInfo(map);
    }

    private void initView() {
        ivBack = (ImageView) findViewById(R.id.setting_report_back_imageview);
//        etReport= (EditText) findViewById(R.id.setting_report_edittext);
//        tvSubmit= (TextView) findViewById(R.id.setting_report_textview);
    }

    private void initEvent() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        tvSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

}
