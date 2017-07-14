package net.huaxi.reader.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tools.commonlibs.activity.BaseActivity;


import net.huaxi.reader.R;

/**
 * Created by ZMW on 2015/12/19.
 * 简单的显示提示信息用的activity
 */
public class SimpleContentActivity extends BaseActivity{
    public static final int TYPE_NO_ANY_RELATION=10001;//账户没有关联任何的信息
    private RelativeLayout rlPhonenum;
    private ImageView ivBack;
    private TextView tvContent,tvAttention;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_content);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        rlPhonenum= (RelativeLayout) findViewById(R.id.simple_phone_num_layout);
        ivBack= (ImageView) findViewById(R.id.simple_back_imageview);
        tvContent= (TextView) findViewById(R.id.simple_content_textview);
        tvAttention= (TextView) findViewById(R.id.simple_attention_textview);
    }

    private void initEvent() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rlPhonenum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("tel:400-636-9933");
                Intent intent = new Intent(Intent.ACTION_CALL, uri);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        int type=getIntent().getIntExtra("type",-1);
        if(type==TYPE_NO_ANY_RELATION){
            //初始化数据
            tvContent.setText(getResources().getText(R.string.account_no_any_relation));
            tvAttention.setText("*注:为了您的账号安全，密码找回后请您及时绑定安全信息。");
        }
    }
}
