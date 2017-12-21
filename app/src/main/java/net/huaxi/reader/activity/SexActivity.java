package net.huaxi.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.huaxi.reader.BaseActivity;
import net.huaxi.reader.MainActivity;
import net.huaxi.reader.R;
import net.huaxi.reader.utils.PreferenceHelper;

import static com.spriteapp.booklibrary.ui.activity.HomeActivity.SEX;

public class SexActivity extends BaseActivity implements View.OnClickListener {
    private ImageView close_sex;
    private TextView sex_man, sex_woman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sex);
        findViewId();
    }

    public void findViewId() {
        close_sex = (ImageView) findViewById(R.id.close_sex);
        sex_man = (TextView) findViewById(R.id.sex_man);
        sex_woman = (TextView) findViewById(R.id.sex_woman);
        close_sex.setOnClickListener(this);
        sex_man.setOnClickListener(this);
        sex_woman.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_sex:
                jumpMain(0);
                break;
            case R.id.sex_man://男生小说
                jumpMain(1);
                break;
            case R.id.sex_woman://女生小说
                jumpMain(2);
                break;
        }
    }

    public void jumpMain(int type) {
        PreferenceHelper.putInt(SEX, type);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
