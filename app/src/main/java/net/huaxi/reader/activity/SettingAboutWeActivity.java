package net.huaxi.reader.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.tools.PhoneUtils;

import net.huaxi.reader.R;

public class SettingAboutWeActivity extends BaseActivity {
    private TextView tvPackageName;
    private TextView tvTitleRight;
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
        setContentView(R.layout.activity_setting_about_we);
        tvPackageName= (TextView) findViewById(R.id.setting_aboutwe_packagename_textview);
        //测试用
//        tvPackageName.setText("V " + PhoneUtils.getVersionName() + "_" + PhoneUtils.getVersionCode());
        tvPackageName.setText("V " + PhoneUtils.getVersionName());
        findViewById(R.id.setting_aboutwe_back_imageview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvTitleRight= (TextView) findViewById(R.id.setting_aboutwe_right_textview);
        tvTitleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingAboutWeActivity.this,SimpleWebViewActivity.class);
                intent.putExtra("webtype",SimpleWebViewActivity.WEBTYPE_USER_PROTOCOL);
                startActivity(intent);
            }
        });
    }
}
