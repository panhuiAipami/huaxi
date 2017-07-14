package net.huaxi.reader.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.huaxi.reader.common.SharePrefHelper;

import net.huaxi.reader.R;

/**
 * function:    男女偏好设置
 * author:      ryantao
 * create:      16/8/2
 * modtime:     16/8/2
 */
public class ContentPerferenceActivity extends Activity implements View.OnClickListener {

    ImageView man,women,goback,otherBtn;
    TextView title;
    RelativeLayout manLayout;
    RelativeLayout womenLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_preference_activity);

        goback = (ImageView) findViewById(R.id.toolbar_layout_back) ;
        goback.setOnClickListener(this);
        otherBtn = (ImageView) findViewById(R.id.toolbar_layout_other) ;
        otherBtn.setVisibility(View.GONE);

        title = (TextView) findViewById(R.id.toolbar_layout_title);
        title.setText(getString(R.string.setting_content_like));

        man = (ImageView) findViewById(R.id.setting_checked_man);
        man.setVisibility(View.GONE);
        women = (ImageView) findViewById(R.id.setting_checked_women);
        women.setVisibility(View.GONE);

        manLayout = (RelativeLayout) findViewById(R.id.man_layout);
        manLayout.setOnClickListener(this);
        womenLayout = (RelativeLayout) findViewById(R.id.women_layout);
        womenLayout.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean clickMan = SharePrefHelper.getSexClassify() == 2 ? true : false;
        toggleChecked(clickMan);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.man_layout) {
            toggleChecked(true);
        } else if (v.getId() == R.id.women_layout) {
            toggleChecked(false);
        } else if (v.getId() == R.id.toolbar_layout_back) {
            finish();
        }
    }


    /**
     * 是否点击男性偏好
     * @param clickMan true:男性，false:女。
     */
    private void toggleChecked(boolean clickMan) {
        if (clickMan) {
            SharePrefHelper.setSexClassify(2);
            man.setVisibility(View.VISIBLE);
            women.setVisibility(View.GONE);
        }else{
            SharePrefHelper.setSexClassify(1);
            women.setVisibility(View.VISIBLE);
            man.setVisibility(View.GONE);
        }
    }
}
