package net.huaxi.reader.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.umeng.socialize.UMShareAPI;

import net.huaxi.reader.BaseActivity;
import net.huaxi.reader.MainActivity;
import net.huaxi.reader.R;
import net.huaxi.reader.bean.ShareBean;
import net.huaxi.reader.dialog.ShareDialog;

public class ShareActivity extends BaseActivity implements View.OnClickListener {
    private ShareDialog shareDialog;
    private ShareBean shareBean;
    private TextView share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//隐藏状态栏
        setContentView(R.layout.activity_share);
        initData();
        initView();
        initListener();
        initShareDialog();

    }

    public void initView() {
        share = (TextView) findViewById(R.id.share_text);
    }

    public void initData() {
        Intent intent = getIntent();
        shareBean = (ShareBean) intent.getSerializableExtra(MainActivity.SHAREDATA);
    }

    public void initListener() {
        share.setOnClickListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();//点击窗口外部区域 弹窗消失
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_text:
                initShareDialog();
                break;
        }
    }

    public void initShareDialog() {
        if (shareBean == null)
            return;
        if (shareDialog != null) {
            shareDialog.dismiss();
            shareDialog = null;
        }
        shareDialog = new ShareDialog(this, shareBean);
        shareDialog.show();
        shareDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

}
