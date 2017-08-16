package net.huaxi.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.StringUtils;
import com.tools.commonlibs.tools.ViewUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.adapter.AdapterLikeBook;
import net.huaxi.reader.adapter.BaseRecyclerAdapter;
import net.huaxi.reader.bean.ShareBean;
import net.huaxi.reader.book.datasource.DataSourceManager;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.EnterBookContent;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.common.XSNetEnum;
import net.huaxi.reader.db.dao.BookDao;
import net.huaxi.reader.db.model.BookTable;
import net.huaxi.reader.dialog.BookContentShareDialog;
import net.huaxi.reader.https.GetRequest;
import net.huaxi.reader.https.PostRequest;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.https.XSKEY;
import net.huaxi.reader.statistic.ReportUtils;
import net.huaxi.reader.util.UMEventAnalyze;
import net.huaxi.reader.view.ExRecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ryantao
 * 16/5/10.
 */
public class LastPageActivity extends BaseActivity implements View.OnClickListener, IWeiboHandler.Response {


    ImageView goback = null;
    ImageView shareBtn = null;
    TextView title = null;

    ExRecyclerView mRecyclerView;
    AdapterLikeBook mAdapterLikeBook;
    LinearLayout likebooks;
    EditText content;
    Button mSendCommentBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readpage_lastpage);

        initView();

        syncSameBook(DataSourceManager.getSingleton().getBookId());

    }

    private void initView() {
        goback = (ImageView) findViewById(R.id.toolbar_layout_back);
        goback.setOnClickListener(this);
        title = (TextView) findViewById(R.id.toolbar_layout_title);
        title.setText(getResources().getString(R.string.comment_write));

        content = (EditText) findViewById(R.id.et_commet_count);
        content.setText("");

        shareBtn = (ImageView) findViewById(R.id.toolbar_layout_other);
        shareBtn.setVisibility(View.VISIBLE);
        shareBtn.setOnClickListener(this);


        mSendCommentBtn = (Button) findViewById(R.id.sendcommentbtn);
        mSendCommentBtn.setOnClickListener(this);

        mRecyclerView = (ExRecyclerView) findViewById(R.id.listview);
//        mRecyclerView.setNestedScrollingEnabled(false);

//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapterLikeBook = new AdapterLikeBook(this, new ArrayList<BookTable>());
        mRecyclerView.setAdapter(mAdapterLikeBook);
        likebooks = (LinearLayout) findViewById(R.id.likebooks);
        likebooks.setVisibility(View.GONE);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toolbar_layout_back) {
            finish();
        } else if (v.getId() == R.id.toolbar_layout_other) {  //分享
            UMEventAnalyze.countEvent(this,UMEventAnalyze.READPAGE_LASTPAGE_SHARE);
            BookTable _bookTable = BookDao.getInstance().findBook(DataSourceManager.getSingleton().getBookId());
            if (_bookTable != null && StringUtils.isNotEmpty(_bookTable.getWebsite())) {
                ShareBean shareBean = new ShareBean();
                shareBean.setShareUrl(_bookTable.getWebsite());
                shareBean.setImgUrl(_bookTable.getCoverImageId());
                shareBean.setTitle(_bookTable.getName());
                shareBean.setDesc(_bookTable.getBookDesc());
                new BookContentShareDialog(LastPageActivity.this, shareBean).show();
            } else {
                toast(getString(R.string.detail_book_not_found));
            }
            return;
        } else if (v.getId() == R.id.sendcommentbtn) {
            if (UserHelper.getInstance().isLogin()) {
                mSendCommentBtn.setClickable(false);
                sentComment(DataSourceManager.getSingleton().getBookId());
                UMEventAnalyze.countEvent(this,UMEventAnalyze.READPAGE_LASTPAGE_COMMENT);
            } else {
                new MaterialDialog.Builder(this)
                        .content(R.string.need_login)
                        .positiveText(R.string.login)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                Intent intent = new Intent(LastPageActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }

    }

    private void sentComment(String bookId) {
        {
            String commentCount = content.getText().toString().trim();
            if (StringUtils.isBlank(commentCount) || commentCount.length() < 10) {
                mSendCommentBtn.setClickable(true);
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
                    mSendCommentBtn.setClickable(true);
                    int errorid = ResponseHelper.getErrorId(response);
                    if (ResponseHelper.isSuccess(response)) {
                        content.setText("");
                        ViewUtils.toastShort(getString(R.string.comment_success));
//                        finish();
                    } else if (errorid == XSNetEnum._VDATAERRORCODE_ERROR_NOT_LOGIN.getCode()) {
                        LogUtils.debug("需要登录");
                        Intent intent = new Intent(LastPageActivity.this, LoginActivity.class);
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
    }

    /**
     * 猜你喜欢
     *
     * @param bookid
     */
    private void syncSameBook(final String bookid) {
        if (StringUtils.isEmpty(bookid)) {
            finish();
        }
        GetRequest request = new GetRequest(String.format(URLConstants.GET_CAT_LIST, bookid, "3") + CommonUtils.getPublicGetArgs(), new
                Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LogUtils.debug("response==" + response.toString());
                        if (ResponseHelper.isSuccess(response)) {
                            JSONObject object = ResponseHelper.getVdata(response);
                            if (object != null) {
                                try {
                                    JSONArray array = object.getJSONArray(XSKEY.KEY_LIST);
                                    if (array != null && array.length() > 0) {
                                        likebooks.setVisibility(View.VISIBLE);
                                        Type type = new TypeToken<ArrayList<BookTable>>() {
                                        }.getType();
                                        List<BookTable> bookTables = new Gson().fromJson(array.toString()
                                                , type);
                                        if (bookTables != null && bookTables.size() > 0) {
                                            mAdapterLikeBook.mDatas = bookTables;
                                            mAdapterLikeBook.notifyDataSetChanged();
                                            mAdapterLikeBook.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(int position, Object data) {
                                                    if (data instanceof BookTable) {
                                                        BookTable bookTable = (BookTable) data;
                                                        EnterBookContent.openBookDetail(LastPageActivity.this,bookTable.getBookId());
                                                    }
                                                    UMEventAnalyze.countEvent(LastPageActivity.this,UMEventAnalyze.READPAGE_LASTPAGE_LIKE);
                                                }
                                            });
                                        }
                                    }
                                } catch (Exception e) {
                                }
                            }
                        } else {
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ReportUtils.reportError(error);
                LogUtils.debug("网络错误");
            }
        });
        RequestQueueManager.addRequest(request);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (AppContext.getLoginHelper() != null) {
            AppContext.getLoginHelper().ActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (AppContext.getLoginHelper() != null) {
            AppContext.getLoginHelper().NewIntent(intent, this);
        }
    }

    @Override
    public void onResponse(BaseResponse arg0) {
        if (AppContext.getLoginHelper() != null) {
            AppContext.getLoginHelper().onResponse(arg0);
        }
    }


}
