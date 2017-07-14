package net.huaxi.reader.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.tools.commonlibs.activity.BaseActivity;
import net.huaxi.reader.fragment.FmReadRecord;
import net.huaxi.reader.util.UMEventAnalyze;

import net.huaxi.reader.R;

import net.huaxi.reader.fragment.FmSubscribeRecord;

/**
 * function:    书籍记录Activity
 * author:      ryantao
 * create:      16/8/24
 * modtime:     16/8/24
 */
public class BookRecordActivity extends BaseActivity implements View.OnClickListener {

    ImageView goback;
    Button readRecordBtn;
    Button subScribeBtn;
    FrameLayout content;
    FmReadRecord fmReadRecord;
    FmSubscribeRecord fmSubscribeRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_record);
        init();

    }

    private void init() {
        goback = (ImageView) findViewById(R.id.record_back_imageview);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        readRecordBtn = (Button) findViewById(R.id.read_record_btn);
        readRecordBtn.setText(getString(R.string.read_record_title));
        readRecordBtn.setOnClickListener(this);
        subScribeBtn = (Button) findViewById(R.id.subscribe_record_btn);
        subScribeBtn.setText(getString(R.string.sub_scribe_title));
        subScribeBtn.setOnClickListener(this);
        setDefaultFragment();
    }

    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        fmReadRecord = new FmReadRecord();
        transaction.replace(R.id.content, fmReadRecord);
        transaction.commit();
        UMEventAnalyze.countEvent(getActivity(),UMEventAnalyze.BOOK_SHELF_READED_RECORD);
    }

    @Override
    public void onClick(View v) {
        FragmentManager fm = getSupportFragmentManager();
        // 开启Fragment事务
        FragmentTransaction transaction = fm.beginTransaction();
        if (v.getId() == R.id.read_record_btn) {
            readRecordBtn.setTextColor(ContextCompat.getColor(getActivity(),R.color.c01_themes_color));
            subScribeBtn.setTextColor(ContextCompat.getColor(getActivity(),R.color.c13_themes_color));

            readRecordBtn.setBackgroundResource(R.drawable.left_round_pressed);
            subScribeBtn.setBackgroundResource(R.drawable.right_round_normal);
            if (fmReadRecord == null) {
                fmReadRecord = new FmReadRecord();
            }
            transaction.replace(R.id.content, fmReadRecord);
            UMEventAnalyze.countEvent(getActivity(),UMEventAnalyze.BOOK_SHELF_READED_RECORD);
        } else if (v.getId() == R.id.subscribe_record_btn) {
            readRecordBtn.setTextColor(ContextCompat.getColor(getActivity(),R.color.c13_themes_color));
            subScribeBtn.setTextColor(ContextCompat.getColor(getActivity(),R.color.c01_themes_color));

            readRecordBtn.setBackgroundResource(R.drawable.left_round_normal);
            subScribeBtn.setBackgroundResource(R.drawable.right_round_pressed);
            if (fmSubscribeRecord == null) {
                fmSubscribeRecord = new FmSubscribeRecord();
            }
            transaction.replace(R.id.content, fmSubscribeRecord);
            UMEventAnalyze.countEvent(getActivity(),UMEventAnalyze.BOOK_SHELF_ORDER_RECORD);
        }
        transaction.commit();
    }
}
