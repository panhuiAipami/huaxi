package net.huaxi.reader.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.ViewUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.book.BookContentSettings;
import net.huaxi.reader.book.SharedPreferenceUtil;
import net.huaxi.reader.book.datasource.DataSourceManager;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.https.GetRequest;
import net.huaxi.reader.https.PostRequest;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.util.UMEventAnalyze;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MoreSettingActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {


    RelativeLayout lineSpaceLayout;
    LinearLayout autoLayout;
    ToggleButton autoOrderTb;
    ToggleButton turnPageTb;
    ImageView goback;
    Dialog progressDailog;
    TextView lineSpaceStyle;
    private int lineStyle;
    private boolean isInit = true;

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
        setContentView(R.layout.activity_more_setting);
        autoLayout = (LinearLayout) findViewById(R.id.autosub_layout);
        lineSpaceLayout = (RelativeLayout) findViewById(R.id.line_space_layout);
        lineSpaceLayout.setOnClickListener(this);
        autoOrderTb = (ToggleButton) findViewById(R.id.tb_order_chapter);
        autoOrderTb.setOnCheckedChangeListener(this);
        turnPageTb = (ToggleButton) findViewById(R.id.tb_lift_next);
        turnPageTb.setOnCheckedChangeListener(this);
        goback = (ImageView) findViewById(R.id.back_imageview);
        goback.setOnClickListener(this);
        lineSpaceStyle = (TextView) findViewById(R.id.line_space_style);
        lineSpaceStyle.setText("");
//        lineSpaceStyle.setOnClickListener(this);
        if (UserHelper.getInstance().isLogin()) {
            autoLayout.setVisibility(View.VISIBLE);
            autoOrderTb.setChecked(false);
            getAutoSub();
        } else {
            autoLayout.setVisibility(View.GONE);
        }
        lineStyle = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getLineSpaceStyle();
        isInit = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean leftClicked = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().isLeftClicked();
        turnPageTb.setChecked(leftClicked);
        if (leftClicked) {
            UMEventAnalyze.countEvent(this, UMEventAnalyze.READPAGE_LIFT_NEXTPAGE_OPEN);
        } else {
            UMEventAnalyze.countEvent(this, UMEventAnalyze.READPAGE_LIFT_NEXTPAGE_CLOSE);
        }

        int style = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getLineSpaceStyle();
        String[] buttonTexts = AppContext.context().getResources().getStringArray(R.array.line_space_style);
        if (buttonTexts != null && buttonTexts.length > 0 && style < buttonTexts.length) {
            lineSpaceStyle.setText(buttonTexts[style]);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_imageview) {
            close();
        } else if (v.getId() == R.id.line_space_layout) {
            Intent it = new Intent(getActivity(), ListStringActivity.class);
            startActivity(it);
        }
    }

    @Override
    public void onKeyBack() {
        close();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.tb_order_chapter) {
            postAutoSub(isChecked);
        } else if (buttonView.getId() == R.id.tb_lift_next) {
            SharedPreferenceUtil.getInstanceSharedPreferenceUtil().saveLeftToTurnNext(isChecked);
        }
    }

    public void getAutoSub() {
            String url = URLConstants.USER_AUTO_SUB_URL + "?bk_mid=" + DataSourceManager.getSingleton().getBookId() + CommonUtils
                .getPublicGetArgs();
        if (!NetUtils.checkNetworkUnobstructed()) {
            toast(getString(R.string.not_available_network));
            return;
        }
        progressDailog = ViewUtils.showProgressDialog(getActivity());
        GetRequest getRequest = new GetRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (ResponseHelper.isSuccess(response)) {
                    autoOrderTb.setChecked(true);
                    UMEventAnalyze.countEvent(MoreSettingActivity.this,UMEventAnalyze.READPAGE_AUTOORDER_OPEN);
                } else {
                    autoOrderTb.setChecked(false);
                    UMEventAnalyze.countEvent(MoreSettingActivity.this,UMEventAnalyze.READPAGE_AUTOORDER_CLOSE);
                }
                if (progressDailog != null && progressDailog.isShowing()) {
                    progressDailog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDailog != null && progressDailog.isShowing()) {
                    progressDailog.dismiss();
                }
                autoOrderTb.setChecked(false);
                toast(getString(R.string.network_server_error));
            }
        });
        RequestQueueManager.addRequest(getRequest);
    }

    public synchronized void postAutoSub(final boolean autosub) {
        if (isInit) {
            LogUtils.debug("ignore the aciton in moresettingactivity is initing chekcbox ");
            return;
        }
        final String action = autosub ? "b_add" : "b_del";
        Map<String, String> allParams = new HashMap<String, String>();
        allParams.put("u_action", action);
        allParams.put("bk_mid", DataSourceManager.getSingleton().getBookId());
        allParams.putAll(CommonUtils.getPublicPostArgs());
        PostRequest postRequest = new PostRequest(URLConstants.USER_AUTO_SUB_URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (ResponseHelper.isSuccess(response)) {
                    LogUtils.debug("post autosub action return success");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, allParams);
        RequestQueueManager.addRequest(postRequest);
    }

    private void close() {
        boolean changed = false;
        if (lineStyle != SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getLineSpaceStyle()) {
            changed = true;
        }
        if (changed) {
            BookContentSettings.getInstance().changeLineSpace();
            BookContentSettings.getInstance().changeParagraphSpace();
            BookContentSettings.getInstance().changeTitleSpace();
            setResult(RESULT_OK, getIntent());
        } else {
            setResult(RESULT_CANCELED, getIntent());
        }
        finish();
    }
}
