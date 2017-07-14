package net.huaxi.reader.dialog;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.tools.commonlibs.dialog.BaseDialog;

import net.huaxi.reader.R;
import net.huaxi.reader.activity.SimpleWebViewActivity;

/**
 * Created by ZMW on 2016/3/23.
 */
public class UserAnnouncementDialog extends BaseDialog implements View.OnClickListener {
    Activity activity;
    private ImageView ivClose,ivButton;
    public UserAnnouncementDialog(Activity activity){
        this.activity=activity;
        initDialog(activity, null, R.layout.dialog_announcement, BaseDialog.TYPE_CENTER, true);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.flags = lp.flags | (WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mDialog.getWindow().setAttributes(lp);
        mDialog.getWindow().setWindowAnimations(R.style.bottomPopupDialog);
        ivButton= (ImageView) mDialog.findViewById(R.id.announcement_dialog_button_imageview);
        ivButton.setOnClickListener(this);
        ivClose= (ImageView) mDialog.findViewById(R.id.announcement_dialog_close_imageview);
        ivClose.setOnClickListener(this);
        mDialog.findViewById(R.id.announcement_dialog_close_layout).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.announcement_dialog_button_imageview:
                Intent intent = new Intent(activity, SimpleWebViewActivity.class);
                intent.putExtra("webtype", SimpleWebViewActivity.WEBTYPE_REWARDS);
                activity.startActivity(intent);
                mDialog.cancel();
                break;
            case R.id.announcement_dialog_close_imageview:
            case R.id.announcement_dialog_close_layout:
                mDialog.cancel();
                break;
        }
    }
}
