package net.huaxi.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.common.NetType;
import com.tools.commonlibs.tools.DateUtils;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.StringUtils;
import net.huaxi.reader.bean.BookCommentBean;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.statistic.ReportUtils;
import net.huaxi.reader.util.EncodeUtils;
import net.huaxi.reader.util.ImageUtil;
import net.huaxi.reader.util.UMEventAnalyze;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.huaxi.reader.R;

import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.https.GetRequest;
import net.huaxi.reader.https.XSKEY;
import net.huaxi.reader.view.divider.HorizontalDividerItemDecoration;

/**
 * @Description: [评论页面的activity]
 * @Author: [Saud]
 * @CreateDate: [16/8/2 11:28]
 * @UpDate: [16/8/2 11:28]
 * @Version: [v1.0]
 */

public class   BookCommentActivity extends BaseActivity implements View.OnClickListener, BaseQuickAdapter.RequestLoadMoreListener {

    private int PAGE_SIZE = 30;//一次加载评论的个数
    private int PAGE_INDEX = 1;//默认从第一页开始
    private View ivBack;
    private RecyclerView lvComment;
    private CommentAdapter adapterComment;
    private String bookId;
    private List<BookCommentBean> mCommentList = new ArrayList<>();
    private View ivWriteComment;
    private View commentNull;
    private View loadingError;
    private TextView tvCommentNull;
    private boolean isLoadingMore = false;
    private int mPageIndex;//分页页数的角标

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_comment);

        initView();
        initListener();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UMEventAnalyze.countEvent(this, UMEventAnalyze.COMMENT_PAGE);
        BookCommentActivity.this.mCommentList.clear();
        PAGE_INDEX = 1;
        syncComment(PAGE_INDEX);
    }

    private void initView() {
        ivBack = findViewById(R.id.iv_download_back);
        ivWriteComment = findViewById(R.id.iv_write_comment);
        commentNull = findViewById(R.id.commont_null);
        loadingError = findViewById(R.id.loading_error);
        tvCommentNull = (TextView) findViewById(R.id.tv_comment_null);
        tvCommentNull.setText(R.string.comment_no_count);
        lvComment = (RecyclerView) findViewById(R.id.lv_comment);

    }

    private void initListener() {
        ivBack.setOnClickListener(this);
        commentNull.setOnClickListener(this);
        loadingError.setOnClickListener(this);
        ivWriteComment.setOnClickListener(this);
    }

    private void initData() {
        bookId = getIntent().getStringExtra("bookid");
        if (StringUtils.isEmpty(bookId)) {
            bookId = "0";
        }
        List<BookCommentBean> commentList = (List<BookCommentBean>) getIntent().getSerializableExtra("list");
        if (commentList != null) {
            if (mCommentList.size() > 0) {
                mCommentList.clear();
            }
            mCommentList.addAll(commentList);
        }


        lvComment.setLayoutManager(new LinearLayoutManager(this));
        lvComment.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.c09_divider_color))
                .sizeResId(R.dimen.divider)
                .marginResId(R.dimen.leftmargin, R.dimen.rightmargin)
                .build());
        adapterComment = new CommentAdapter(R.layout.item_comment_detail, mCommentList);
        adapterComment.setOnLoadMoreListener(this);
        adapterComment.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        adapterComment.openLoadMore(PAGE_SIZE, true);
        final View customLoading = getLayoutInflater().inflate(R.layout.custom_loading, (ViewGroup) lvComment.getParent(), false);
        adapterComment.setLoadingView(customLoading);
        lvComment.setAdapter(adapterComment);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_download_back:
                finish();
                break;
            case R.id.loading_error:
                syncComment(PAGE_INDEX);
                break;
            case R.id.commont_null:
                startWriteActivity();
                break;
            case R.id.iv_write_comment:
                startWriteActivity();
                break;
            default:
                break;
        }
    }

    private void startWriteActivity() {
        Intent intent = new Intent(this, WriteCommentActivity.class);
        intent.putExtra("bookid", bookId);
        startActivityForResult(intent, 0);
    }

    /**
     * 请求评论的数据
     */
    private void syncComment(int index) {
        if (NetUtils.checkNet() == NetType.TYPE_NONE) {
            loadingError.setVisibility(View.VISIBLE);
            commentNull.setVisibility(View.GONE);
            lvComment.setVisibility(View.GONE);
            return;
        }
        String url = String.format(URLConstants.APP_BOOK_COMMENT_URL, EncodeUtils.encodeString_UTF8(bookId) + CommonUtils.getPublicGetArgs(), PAGE_SIZE, index);
        GetRequest request = new GetRequest(url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                LogUtils.debug("评论的数据==" + response.toString());
                if (ResponseHelper.isSuccess(response)) {
                    JSONObject object = ResponseHelper.getVdata(response);
                    if (object != null) {
                        JSONArray jsonArray = object.optJSONArray(XSKEY.KEY_LIST);
                        mPageIndex = object.optInt("p_num");
                        if (jsonArray != null) {
                            Type type = new TypeToken<ArrayList<BookCommentBean>>() {
                            }.getType();
                            List<BookCommentBean> commentBeans = new Gson().fromJson(jsonArray.toString(), type);
                            if (commentBeans != null) {
                                if (isLoadingMore) {
                                    isLoadingMore = false;
                                    adapterComment.notifyDataChangedAfterLoadMore(commentBeans, true);
                                } else {
                                    adapterComment.setNewData(commentBeans);
                                }
                            }
                            if (object.optInt("c_number") == adapterComment.getData().size()) {//加载到了最后一条
                                adapterComment.notifyDataChangedAfterLoadMore(false);
                            }
                            adapterComment.notifyDataSetChanged();
                            if (adapterComment.getData().size() == 0) {
                                commentNull.setVisibility(View.VISIBLE);
                                lvComment.setVisibility(View.GONE);
                            } else {
                                commentNull.setVisibility(View.GONE);
                                lvComment.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else {
                    commentNull.setVisibility(View.VISIBLE);
                    lvComment.setVisibility(View.GONE);
                    LogUtils.debug("错误描述=" + response.optString("errordesc"));
                }

                loadingError.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingError.setVisibility(View.VISIBLE);
                commentNull.setVisibility(View.GONE);
                lvComment.setVisibility(View.GONE);
                ReportUtils.reportError(new Throwable(error.getMessage()));
                LogUtils.debug("网络错误");
            }
        });
        RequestQueueManager.addRequest(request);
    }

    @Override
    public void onLoadMoreRequested() {
        isLoadingMore = true;
        mPageIndex = mPageIndex + 1;
        syncComment(mPageIndex);
    }


    /**
     * 列表适配器
     */
    private class CommentAdapter extends BaseQuickAdapter<BookCommentBean> {
        public CommentAdapter(int layoutResId, List<BookCommentBean> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, BookCommentBean bookCommentBean) {
            if (bookCommentBean != null) {
                holder.setText(R.id.tv_comment_nickname, bookCommentBean.getNickname());
                holder.setText(R.id.tv_comment_content, bookCommentBean.getContent());
                holder.setText(R.id.tv_comment_time, DateUtils.timeFormat(bookCommentBean.getCommentTime()));
                boolean isVisible = (bookCommentBean.getIsVip() == 1);
                holder.setVisible(R.id.iv_comment_vip, isVisible);
                ImageView imageView = holder.getView(R.id.riv_detail_comment_avatar);
                ImageUtil.loadImage(mContext.getApplicationContext(), bookCommentBean.getAvatar(), imageView, R.mipmap.usercenter_default_icon);
            }
        }
    }
}
