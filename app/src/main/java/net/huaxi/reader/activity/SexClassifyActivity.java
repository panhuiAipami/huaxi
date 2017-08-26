package net.huaxi.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.tools.commonlibs.activity.BaseActivity;
import net.huaxi.reader.util.UMEventAnalyze;
import net.huaxi.reader.util.listener.ScreenUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.common.SharePrefHelper;

/**
 * function:    引导男女分类页面。
 * author:      ryantao
 * create:      16/7/25
 * modtime:     16/7/25
 */
public class SexClassifyActivity extends BaseActivity {

    Button startBtn;

    LinearLayout manLayout, womenLayout;

    boolean isMan = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenUtils.fullScreen(this);
        setContentView(R.layout.activity_sex_classify);
        init();
    }


    private void init() {
        manLayout = (LinearLayout) findViewById(R.id.sex_classify_man);
        womenLayout = (LinearLayout) findViewById(R.id.sex_classify_women);
        setItemState(true);
        startBtn = (Button) findViewById(R.id.sex_classify_start);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharePrefHelper.setIsInitSexClassify(true);
                Intent it = new Intent(SexClassifyActivity.this, MainActivity.class);
                startActivity(it);
                finish();
            }
        });
        manLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemState(true);
            }
        });
        womenLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setItemState(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        UMEventAnalyze.countEvent(this, UMEventAnalyze.SPLASH_SELECT);
    }

    @Override
    protected void onPause() {
        if (isMan) {
            UMEventAnalyze.countEvent(this, UMEventAnalyze.SPLASH_BOY);
        } else {
            UMEventAnalyze.countEvent(this, UMEventAnalyze.SPLASH_GIRL);
        }
        super.onPause();
    }

    /**
     * 设置男女状态
     *
     * @param man true:男；false:女；
     */
    private void setItemState(boolean man) {
        isMan = man;
        if (man) {
            manLayout.setBackgroundResource(R.drawable.sex_classify_pressed_bg);
            womenLayout.setBackgroundResource(R.drawable.sex_classify_normal_bg);
        } else {
            manLayout.setBackgroundResource(R.drawable.sex_classify_normal_bg);
            womenLayout.setBackgroundResource(R.drawable.sex_classify_pressed_bg);
        }
        //本地记录性别分类。
        SharePrefHelper.setSexClassify(isMan ? 2 : 1);
    }

}
