package net.huaxi.reader.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.StringUtils;
import com.tools.commonlibs.tools.ViewUtils;
import net.huaxi.reader.book.SharedPreferenceUtil;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.statistic.ReportUtils;
import net.huaxi.reader.util.UMEventAnalyze;

import org.json.JSONObject;

import java.util.Map;

import net.huaxi.reader.R;

import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.common.XSNetEnum;
import net.huaxi.reader.https.PostRequest;

public class WriteCommentActivity extends BaseActivity implements View.OnClickListener {

    private View ivBack;
    private String bookId;
    private EditText etCount;
    private SharedPreferences sp;
    private TextView tvCommentTag;
    private View ivSendComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);
        initView();
        initListener();
        initData();

    }


    private void initView() {
        ivBack = findViewById(R.id.iv_download_back);
        etCount = (EditText) findViewById(R.id.et_commet_count);
        tvCommentTag = (TextView) findViewById(R.id.tv_comment_tag);
        ivSendComment = findViewById(R.id.iv_send_comment);

    }

    private void initListener() {
        ivBack.setOnClickListener(this);
        ivSendComment.setOnClickListener(this);
        etCount.addTextChangedListener(new EditChangedListener());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialogLoading != null) {
            dialogLoading.cancel();
        }
        String commentCount = etCount.getText().toString().trim();
        if (sp == null) {
            sp = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getShaPreferencesInstance(this);
        }
        SharedPreferenceUtil.getInstanceSharedPreferenceUtil().saveString(bookId, commentCount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sp == null) {
            sp = SharedPreferenceUtil.getInstanceSharedPreferenceUtil().getShaPreferencesInstance(this);
        }
        SharedPreferenceUtil instanceSharedPreferenceUtil = SharedPreferenceUtil.getInstanceSharedPreferenceUtil();
        String text = instanceSharedPreferenceUtil.getString(bookId);
        if (!"".equals(text)) {
            etCount.setText(text);
        }
        UMEventAnalyze.countEvent(this, UMEventAnalyze.COMMENT_WRITE);
    }

    private void initData() {
        bookId = getIntent().getStringExtra("bookid");
        if (StringUtils.isEmpty(bookId)) {
            bookId = "0";
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_download_back:
                finish();
                break;
            case R.id.iv_send_comment:
                if (UserHelper.getInstance().isLogin()) {
                    ivSendComment.setClickable(false);
                    sentComment();
                    UMEventAnalyze.countEvent(this, UMEventAnalyze.COMMENT_SEND);
                } else {
                    new MaterialDialog.Builder(this)
                            .content(R.string.need_login)
                            .positiveText(R.string.login)
                            .negativeText(R.string.cancel)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog dialog, DialogAction which) {
                                    Intent intent = new Intent(WriteCommentActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .show();
                }

                break;

            default:
                break;
        }
    }

    /**
     * 发送评论
     */
    private void sentComment() {
        String commentCount = etCount.getText().toString().trim();
        if (StringUtils.isBlank(commentCount) || commentCount.length() < 10) {
            ivSendComment.setClickable(true);
            ViewUtils.toastShort(getString(R.string.comment_text_too_short));
            return;
        }
        dialogLoading = ViewUtils.showProgressDialog(this);
        Map<String, String> map = CommonUtils.getPublicPostArgs();
        map.put("bk_mid", bookId);
        map.put("cmt_content", commentCount);
        PostRequest request = new PostRequest(URLConstants.APP_SEND_COMMENT_URL, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (dialogLoading != null) {
                    dialogLoading.cancel();
                }
                LogUtils.debug("response====" + response.toString());
                ivSendComment.setClickable(true);
                int errorid = ResponseHelper.getErrorId(response);
                if (ResponseHelper.isSuccess(response)) {
                    etCount.setText("");
                    ViewUtils.toastShort(getString(R.string.comment_success));
                    finish();
                } else if (errorid == XSNetEnum._VDATAERRORCODE_ERROR_NOT_LOGIN.getCode()) {
                    LogUtils.debug("需要登录");
                    Intent intent = new Intent(WriteCommentActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else if (errorid == XSNetEnum._DC_CODE_ERROR_READ_CHAPTER_NO_CPT.getCode()) {
                    LogUtils.debug("请求错误=" + errorid);
                } else if (errorid == XSNetEnum._DC_CODE_ERROR_COMMENT_FAIL.getCode()) {
                    ViewUtils.toastShort(getString(R.string.please_waiting_5m));
                } else if (errorid == XSNetEnum._DC_CODE_ERROR_COMMENT_FAIL_SENSITIVE_WORDS.getCode()) {
                    ViewUtils.toastShort(XSNetEnum._DC_CODE_ERROR_COMMENT_FAIL_SENSITIVE_WORDS.getMsg());
                    System.out.println(XSNetEnum._DC_CODE_ERROR_COMMENT_FAIL_SENSITIVE_WORDS.getMsg());
                } else {
                    String erroriMSE = String.format(getString(R.string.comment_error), errorid);
                    ViewUtils.toastShort(erroriMSE);
                    ReportUtils.reportError(new Throwable("评论发送失败(" + errorid + ")"));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ViewUtils.toastShort(getResources().getString(R.string.network_server_error));
                ReportUtils.reportError(new Throwable(error.toString()));
            }
        }, map, 10);
        RequestQueueManager.addRequest(request);

    }

    class EditChangedListener implements TextWatcher {
        private CharSequence temp;//监听前的文本
        private int editStart;//光标开始位置
        private int editEnd;//光标结束位置
        private final int charMaxNum = 500;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

//            LogUtils.debug("输入文本之前的状态");
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            LogUtils.debug("输入文字中的状态，count是一次性输入字符数");
//            ViewUtils.toastShort("还能输入" + (charMaxNum - s.length()) + "字符");
            tvCommentTag.setText(s.length() + "/" + charMaxNum);

        }

        @Override
        public void afterTextChanged(Editable s) {
            LogUtils.debug("输入文字后的状态");
            /** 得到光标开始和结束位置 ,超过最大数后记录刚超出的数字索引进行控制 */
            editStart = etCount.getSelectionStart();
            editEnd = etCount.getSelectionEnd();
            if (temp.length() > charMaxNum) {
                ViewUtils.toastShort("您输入的字数已经超过了限制！");
                s.delete(editStart - 1, editEnd);
                int tempSelection = editStart;
                etCount.setText(s);
                etCount.setSelection(tempSelection);
            }

        }
    }

}
