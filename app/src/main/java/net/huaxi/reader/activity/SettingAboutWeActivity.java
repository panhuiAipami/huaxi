package net.huaxi.reader.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
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
