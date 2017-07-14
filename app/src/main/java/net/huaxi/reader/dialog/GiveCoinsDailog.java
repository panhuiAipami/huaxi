package net.huaxi.reader.dialog;

import android.app.Activity;
import android.os.Handler;
import android.view.View;

import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;
import com.tools.commonlibs.dialog.BaseDialog;
import net.huaxi.reader.activity.MainActivity;

import net.huaxi.reader.R;


/**
 * @Description: [ 迁移老用户后，弹出活动的赠币对话框 ]
 * @Author: [ Saud ]
 * @CreateDate: [ 16/8/5 17:21 ]
 * @UpDate: [ 16/8/5 17:21 ]
 * @Version: [ v1.0 ]
 */
public class GiveCoinsDailog extends BaseDialog implements View.OnClickListener {
    private static final char[] NUMBER_LIST = TickerUtils.getDefaultNumberList();
    private final Activity activity;
    private final String desc;
    private final View mClose;
    private final View mStart;
    private final TickerView ticker;
    private int index;

    public GiveCoinsDailog(Activity activity, final String desc) {
        this.activity = activity;
        this.desc = desc;
        initDialog(activity, null, R.layout.dialog_give_coins, TYPE_CENTER, true);
        mDialog.setCancelable(false);
        mClose = mDialog.findViewById(R.id.give_close);
        mStart = mDialog.findViewById(R.id.give_coins);
        ticker = (TickerView) mDialog.findViewById(R.id.tv_tickerView);
        ticker.setCharacterList(NUMBER_LIST);
        ticker.setAnimationDuration(2500);
        ticker.setText("0");
        initListener();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ticker.setText(desc);
            }
        },200);

    }

    private void initListener() {
        mClose.setOnClickListener(this);
        mStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.give_close:
                mDialog.dismiss();
                break;
            case R.id.give_coins:
                ((MainActivity) activity).getVpMain().setCurrentItem(1);
                mDialog.dismiss();
                break;
            default:
                break;
        }
    }
}
