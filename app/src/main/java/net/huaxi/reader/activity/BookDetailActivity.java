package net.huaxi.reader.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.tools.commonlibs.tools.DateUtils;
import com.tools.commonlibs.tools.ImageUtils;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.StringUtils;
import com.tools.commonlibs.tools.Utils;
import com.tools.commonlibs.tools.ViewUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import net.huaxi.reader.R;
import net.huaxi.reader.adapter.AdapterSameBook;
import net.huaxi.reader.adapter.BaseRecyclerAdapter;
import net.huaxi.reader.appinterface.onCatalogLoadFinished;
import net.huaxi.reader.bean.BookCommentBean;
import net.huaxi.reader.bean.BookDetailBean;
import net.huaxi.reader.bean.ShareBean;
import net.huaxi.reader.book.BookContentModel;
import net.huaxi.reader.book.ChapterClickListener;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.EnterBookContent;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.common.Utility;
import net.huaxi.reader.common.XSErrorEnum;
import net.huaxi.reader.db.dao.BookDao;
import net.huaxi.reader.db.model.BookTable;
import net.huaxi.reader.db.model.ChapterTable;
import net.huaxi.reader.dialog.BookContentShareDialog;
import net.huaxi.reader.dialog.BookDetailCatalogueDialog;
import net.huaxi.reader.dialog.DetailAboutDialog;
import net.huaxi.reader.https.DownLoadThreadLoader;
import net.huaxi.reader.https.GetRequest;
import net.huaxi.reader.https.PostRequest;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.https.XSKEY;
import net.huaxi.reader.statistic.ReportUtils;
import net.huaxi.reader.util.BookShelfDataUtil;
import net.huaxi.reader.util.EncodeUtils;
import net.huaxi.reader.util.ImageUtil;
import net.huaxi.reader.util.UMEventAnalyze;
import net.huaxi.reader.view.RoundImageView;
import net.huaxi.reader.view.divider.HorizontalDividerItemDecoration;


public class BookDetailActivity extends BaseActivity implements ChapterClickListener, onCatalogLoadFinished, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.iv_detail_h1)
    ImageView mIvDetailH1;
    @BindView(R.id.riv_detail_img)
    RoundImageView mRivDetailImg;
    @BindView(R.id.tv_detail_book_name)
    TextView mTvDetailBookName;
    @BindView(R.id.tv_detail_star)
    TextView mTvDetailStar;
    @BindView(R.id.tv_detail_author)
    TextView mTvDetailAuthor;
    @BindView(R.id.tv_detail_attent)
    TextView mTvDetailAttent;
    @BindView(R.id.tv_detail_classify)
    TextView mTvDetailClassify;
    @BindView(R.id.tv_detail_end)
    TextView mTvDetailEnd;
    @BindView(R.id.tv_detail_num)
    TextView mTvDetailNum;
    @BindView(R.id.tv_detail_price)
    TextView mTvDetailPrice;
    @BindView(R.id.tv_detail_about)
    TextView mTvDetailAbout;
    @BindView(R.id.tv_detail_comment)
    TextView mTvDetailComment;
    @BindView(R.id.rv_detail_similar)
    RecyclerView mRvDetailSimilar;
    @BindView(R.id.rv_detail_comment)
    RecyclerView mRvDetailComment;
    @BindView(R.id.rv_detail_other)
    RecyclerView mRvDetailOther;
    @BindView(R.id.ll_similar)
    View mLlSimilar;
    @BindView(R.id.rl_status_bar_height)
    View statusBarHeight;
    @BindView(R.id.ll_other)
    View mLlOther;
    @BindView(R.id.iv_detail_addshelf)
    ImageView mIvDetailAddshelf;
    @BindView(R.id.srl_bookinfo_refresh)
    SwipeRefreshLayout mRefresh;
    private String bookid;
    private String tag;
    private BookDetailBean mBookDetail;
    private BookDetailCatalogueDialog detailCatalogueDialog;
    private AdapterSameBook mAdapterSameBook;
    private AdapterSameBook adapterAuthorOther;
    private static final int PAGE_SIZE = 2;
    private static final int PAGE_INDEX = 1;
    private List<BookCommentBean> commentList = new ArrayList<>();
    private BaseQuickAdapter mCommentAdapter;
    private DetailAboutDialog detailAboutDialog;
    private int WRITHE_REQUEST_CODE = 1;
    private int COMMENT_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);
        ButterKnife.bind(this);
        bookid = getIntent().getStringExtra("bookid");
        if (StringUtils.isEmpty(bookid)) {
            bookid = "0";
        }
        tag = getIntent().getStringExtra("tag");
        initView();
        initAdapter();
        syncBookDetail();
        syncComment();
        syncSameBook();
        syncAuthorOther();
        tintManager.setStatusBarTintEnabled(false);
    }

    private void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup.LayoutParams params = statusBarHeight.getLayoutParams();
            params.height = Utils.getStatusBarHeight();
            statusBarHeight.setLayoutParams(params);
            statusBarHeight.invalidate();
        }
        mRefresh.setOnRefreshListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        setIsOnShelf();
        UMEventAnalyze.countEvent(BookDetailActivity.this, UMEventAnalyze.BOOKINFO_PAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WRITHE_REQUEST_CODE || requestCode == COMMENT_REQUEST_CODE) {
            syncComment();
        }
    }


    @Override
    public void onRefresh() {
        syncBookDetail();
        syncComment();
        syncSameBook();
        syncAuthorOther();
    }

    private void initAdapter() {
        mRvDetailSimilar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mAdapterSameBook = new AdapterSameBook(this, new ArrayList<BookTable>());
        mRvDetailSimilar.setAdapter(mAdapterSameBook);
        mRvDetailOther.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapterAuthorOther = new AdapterSameBook(this, new ArrayList<BookTable>());
        mRvDetailOther.setAdapter(adapterAuthorOther);
        mRvDetailComment.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRvDetailComment.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.c09_divider_color))
                .sizeResId(R.dimen.divider)
                .marginResId(R.dimen.leftmargin, R.dimen.rightmargin)
                .build());
        mCommentAdapter = new BaseQuickAdapter<BookCommentBean>(R.layout.item_comment_detail, commentList) {
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
        };
        mRvDetailComment.setAdapter(mCommentAdapter);
    }

    private void syncBookDetail() {
        GetRequest request = new GetRequest(String.format(URLConstants.GET_BOOKDETAIL,
                EncodeUtils.encodeString_UTF8(bookid) + CommonUtils.getPublicGetArgs()),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mRefresh.setRefreshing(false);
                        if (!ResponseHelper.isSuccess(response)) {
                            LogUtils.debug("获取详情失败==errorid==" + ResponseHelper.getErrorId
                                    (response));
//                            ViewUtils.toastShort("获取详情失败");
                            return;
                        }
                        JSONObject jsonObject = ResponseHelper.getVdata(response);
                        if (jsonObject != null) {


                            Type type = new TypeToken<ArrayList<BookDetailBean>>() {
                            }.getType();
                            List<BookDetailBean> bookTables = new Gson().fromJson(jsonObject
                                    .optJSONArray("list").toString(), type);
                            Iterator it1 = bookTables.iterator();
                            while (it1.hasNext()){
                                LogUtils.debug("booktables: "+it1.next());
                            }
                            if (bookTables != null && bookTables.size() > 0) {
                                mBookDetail = bookTables.get(0);
                                LogUtils.debug("mBookDetail"+mBookDetail.toString());
                                mBookDetail.setBookDesc(mBookDetail.getBookDesc().replaceAll("\\t", ""));
                                setDesc();
                                mRivDetailImg.setClickable(true);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mRefresh.setRefreshing(false);
                ReportUtils.reportError(error);
            }
        });
        RequestQueueManager.addRequest(request);
    }

    /**
     * 请求评论的数据
     */
    private void syncComment() {
        GetRequest request = new GetRequest(String.format(URLConstants.APP_BOOK_COMMENT_URL, EncodeUtils.encodeString_UTF8(bookid) +
                CommonUtils.getPublicGetArgs(), PAGE_SIZE, PAGE_INDEX) + CommonUtils.getPublicGetArgs(), new Response
                .Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                LogUtils.debug("评论的数据==" + response.toString());
                mRefresh.setRefreshing(false);
                if (ResponseHelper.isSuccess(response)) {
                    JSONObject object = ResponseHelper.getVdata(response);
                    if (object != null) {
                        JSONArray jsonArray = object.optJSONArray(XSKEY.KEY_LIST);
                        if (jsonArray != null) {
                            Type type = new TypeToken<ArrayList<BookCommentBean>>() {
                            }.getType();
                            List<BookCommentBean> commentBeans = new Gson().fromJson(jsonArray.toString(), type);
                            if (commentBeans != null && commentBeans.size() > 0) {
                                commentList.clear();
                                if (commentBeans.size() < 2) {
                                    commentList.addAll(commentBeans);
                                } else {
                                    for (int i = 0; i < 2; i++) {
                                        BookCommentBean commentBean = commentBeans.get(i);
                                        if (commentBean != null) {
                                            commentList.add(commentBean);
                                        }
                                    }
                                }
                                View view = getLayoutInflater().inflate(R.layout.item_footer_detail_comment, (ViewGroup) mRvDetailComment.getParent(), false);
                                view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startCommentActivity();
                                    }
                                });
                                if (!mCommentAdapter.hasFooterLayout()) {
                                    mCommentAdapter.addFooterView(view);
                                }
                                mCommentAdapter.setNewData(commentList);
                                mCommentAdapter.notifyDataSetChanged();
                                mTvDetailComment.setVisibility(View.GONE);
                                mRvDetailComment.setVisibility(View.VISIBLE);
                            } else {
                                mTvDetailComment.setVisibility(View.VISIBLE);
                                mRvDetailComment.setVisibility(View.GONE);
                            }
                        }
                    }
                } else {
                    LogUtils.debug("错误描述=" + response.optString("errordesc"));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mRefresh.setRefreshing(false);
                ReportUtils.reportError(new Throwable(error.getMessage()));
                LogUtils.debug("网络错误");
            }
        });
        RequestQueueManager.addRequest(request);
    }


    private void syncSameBook() {
        GetRequest request = new GetRequest(String.format(URLConstants.GET_CAT_LIST, bookid, "10") + CommonUtils.getPublicGetArgs(), new
                Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        LogUtils.debug("response==" + response.toString());
                        mRefresh.setRefreshing(false);
                        if (ResponseHelper.isSuccess(response)) {
                            JSONObject object = ResponseHelper.getVdata(response);
                            if (object != null) {
                                try {
                                    JSONArray array = object.getJSONArray(XSKEY.KEY_LIST);
                                    if (array != null && array.length() > 0) {
                                        Type type = new TypeToken<ArrayList<BookTable>>() {
                                        }.getType();
                                        List<BookTable> bookTables = new Gson().fromJson(array.toString(), type);
                                        if (bookTables != null && bookTables.size() > 0) {
                                            mLlSimilar.setVisibility(View.VISIBLE);
                                            mAdapterSameBook.mDatas = bookTables;
                                            mAdapterSameBook.notifyDataSetChanged();
                                            mAdapterSameBook.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(int position, Object data) {
                                                    if (data instanceof BookTable) {
                                                        BookTable bookTable = (BookTable) data;
                                                        Intent intent = new Intent(BookDetailActivity.this, BookDetailActivity.class);
                                                        intent.putExtra("bookid", bookTable.getBookId());
                                                        startActivity(intent);
                                                        UMEventAnalyze.countEvent(BookDetailActivity.this, UMEventAnalyze.BOOKINFO_SAME);
                                                    }
                                                }
                                            });
                                        } else {
                                            mLlSimilar.setVisibility(View.GONE);
                                        }
                                    } else {
                                        mLlSimilar.setVisibility(View.GONE);
                                    }
                                } catch (Exception e) {
                                    mLlSimilar.setVisibility(View.GONE);
                                }
                            } else {
                                mLlSimilar.setVisibility(View.GONE);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ReportUtils.reportError(error);
                mRefresh.setRefreshing(false);
                mLlSimilar.setVisibility(View.GONE);
                LogUtils.debug("网络错误");
            }
        });
        RequestQueueManager.addRequest(request);
    }

    private void syncAuthorOther() {
        Map<String, String> param = CommonUtils.getPublicPostArgs();
        param.put("bookid", bookid);

        PostRequest request = new PostRequest(URLConstants.GET_AUTHOR_OTHER, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.debug("response==" + response.toString());
                mRefresh.setRefreshing(false);
                if (ResponseHelper.isSuccess(response)) {
                    JSONObject object = ResponseHelper.getVdata(response);
                    if (object != null) {
                        try {
                            object = object.optJSONObject(XSKEY.KEY_LIST);
                            JSONArray array = object.getJSONArray(bookid);
                            if (array != null && array.length() > 0) {
                                Type type = new TypeToken<ArrayList<BookTable>>() {
                                }.getType();
                                List<BookTable> bookTables = new Gson().fromJson(array.toString()
                                        , type);
                                if (bookTables != null && bookTables.size() > 0) {
                                    mLlOther.setVisibility(View.VISIBLE);
                                    adapterAuthorOther.mDatas = bookTables;
                                    adapterAuthorOther.notifyDataSetChanged();
                                    adapterAuthorOther.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(int position, Object data) {
                                            if (data instanceof BookTable) {
                                                BookTable bookTable = (BookTable) data;
                                                Intent intent = new Intent
                                                        (BookDetailActivity.this, BookDetailActivity.class);
                                                intent.putExtra("bookid", bookTable.getBookId());
                                                startActivity(intent);
                                                UMEventAnalyze.countEvent(BookDetailActivity.this, UMEventAnalyze.BOOKINFO_OTHER);
                                            }
                                        }
                                    });
                                    return;
                                } else {
                                    mLlOther.setVisibility(View.GONE);
                                }
                            } else {
                                mLlOther.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            mLlOther.setVisibility(View.GONE);
                        }
                    } else {
                        mLlOther.setVisibility(View.GONE);
                    }
                } else {
                    mLlOther.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mLlOther.setVisibility(View.GONE);
                mRefresh.setRefreshing(false);
                ReportUtils.reportError(error);
                LogUtils.debug("网络错误");
            }
        }, param);
        RequestQueueManager.addRequest(request);
    }


    /**
     * 获取目录
     */
    private void syncCatalog() {
        if (detailCatalogueDialog != null) {
            if (detailCatalogueDialog.getData() == null || detailCatalogueDialog.getData().size() == 0) {
                new DownLoadThreadLoader(this, bookid, this).start();
            }
        }

    }

    private void setDesc() {
        LogUtils.debug("mBookDetail1"+mBookDetail.toString());
        mTvDetailBookName.setText(mBookDetail.getName());
        mTvDetailAuthor.setText(mBookDetail.getAuthorName());
        long totalReaders = mBookDetail.getTotalReaders();
        String readers = totalReaders <= 1000 ? getString(R.string.detail_about_1000_chase) : totalReaders
                > 10000 ? totalReaders /
                10000 + getString(R.string.detail_ten_thousand_chase) : totalReaders
                + getString(R.string.detail_one_chase);
        mTvDetailAttent.setText(readers);
        String score = "" + Utility.formatScene(mBookDetail.getTotalScore());
        mTvDetailStar.setText(score);
        mTvDetailClassify.setText(mBookDetail.getCatName());
        String isEnd = mBookDetail.getIsFinish() == 0 ? getString(R.string.detail_serial) : mBookDetail.getIsFinish() == 1
                ? getString(R.string.detail_finsh) : getString(R.string.detail_unknow);
        mTvDetailEnd.setText(isEnd);
        String num = "";
        if (mBookDetail.getTotalwords() > 10000) {
            num = Utility.formatOnePoint(mBookDetail.getTotalwords() / 10000.00) + getString(R.string.detail_ten_thousand_word);
        } else {
            num = mBookDetail.getTotalwords() + getString(R.string.detail_word);
        }
        mTvDetailNum.setText(num);
        mTvDetailPrice.setText(String.format(getString(R.string.detail_book_text_price), mBookDetail.getPrice()));
        mTvDetailAbout.setText(mBookDetail.getBookDesc().replaceAll("\\t", ""));
        ImageUtil.loadImage(this, mBookDetail.getCoverImageId(), mRivDetailImg, R.mipmap.detail_book_default);
        ImageUtil.getImageListener(this, mBookDetail.getCoverImageId(), new ImageUtil.BitmapCallBackListener() {
            @Override
            public void callBack(final Bitmap bitmap) {
                if (bitmap != null) {
                    //模糊渐显
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap bluBitMap = ImageUtils.BoxBlurFilter(bitmap);
                            setImgDrawble(bluBitMap, mIvDetailH1);
                        }
                    }).start();
                } else {

                }
            }
        });

    }

    private void setImgDrawble(final Bitmap bitmap, final ImageView view) {
        if (bitmap != null) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TransitionDrawable mTransitionDrawable = new TransitionDrawable(new Drawable[]{getResources().getDrawable(R.mipmap.bookinfo_title_bg),
                            new BitmapDrawable(getResources(), bitmap)
                    });

                    mTransitionDrawable.setCrossFadeEnabled(true);
                    mTransitionDrawable.startTransition(800);
                    view.setImageDrawable(mTransitionDrawable);
                }
            });
        } else {
            System.out.println("bitmap====>>>null");
        }
    }


    @OnClick({R.id.iv_detail_back, R.id.iv_detail_share, R.id.tv_detail_catalogue,
            R.id.ll_detail_about, R.id.ll_detail_comment, R.id.iv_detail_download,
            R.id.btn_detail_read, R.id.iv_detail_addshelf})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_detail_back:
                finish();
                break;
            case R.id.iv_detail_share:
                ShareBean shareBean = new ShareBean();
                shareBean.setShareUrl(mBookDetail.getWebsite());
                shareBean.setImgUrl(mBookDetail.getCoverImageId());
                shareBean.setTitle(mBookDetail.getName());
                shareBean.setDesc(mBookDetail.getBookDesc());
                new BookContentShareDialog(this, shareBean).show();
                UMEventAnalyze.countEvent(BookDetailActivity.this, UMEventAnalyze.BOOKINFO_SHARE);
                break;
            case R.id.tv_detail_catalogue:
                //目录
                if (detailCatalogueDialog == null) {
                    detailCatalogueDialog = new BookDetailCatalogueDialog(this);
                    detailCatalogueDialog.setChapterClickListener(this);
                }
                detailCatalogueDialog.show();
                syncCatalog();
                UMEventAnalyze.countEvent(BookDetailActivity.this, UMEventAnalyze.BOOKINFO_CATALOGUE);
                break;
            case R.id.ll_detail_about:
                //简介
                if (detailAboutDialog == null && mBookDetail != null) {
                    detailAboutDialog = new DetailAboutDialog(this, mBookDetail.getBookDesc());
                }
                //设置全屏
                if (detailAboutDialog != null) {
                    detailAboutDialog.show();
                }
                UMEventAnalyze.countEvent(BookDetailActivity.this, UMEventAnalyze.BOOKINFO_ABOUT);
                break;
            case R.id.ll_detail_comment:
                //写评论

                if (commentList == null || commentList.size() == 0) {

//                    startWriteActivity();
                    LogUtils.debug("写评论");
                }
                break;
            case R.id.iv_detail_download:
                Intent intent = new Intent(this, DownLoadActivity.class);
                intent.putExtra("bookid", bookid);
                startActivity(intent);
                UMEventAnalyze.countEvent(BookDetailActivity.this, UMEventAnalyze.BOOKINFO_DOWNLOAD);
                break;
            case R.id.btn_detail_read:
                if (BookContentModel.PARENT_ACTIVITY.equals(tag)) {
                    finish();
                } else {
                    if (mBookDetail != null && BookDao.getInstance().hasKey(mBookDetail.getBookId()
                    ) == 0) {
                        BookDao.getInstance().addBook(mBookDetail);
                    }
                    EnterBookContent.openBookContent(this, bookid);
                }
                UMEventAnalyze.countEvent(BookDetailActivity.this, UMEventAnalyze.BOOKINFO_READ);
                break;

            case R.id.iv_detail_addshelf:
                addBook();
                UMEventAnalyze.countEvent(BookDetailActivity.this, UMEventAnalyze.BOOKINFO_ADDSHELF);
                break;

        }
    }


    @Override
    public void onCatalogItemClick(String chapterId) {
        System.out.println("BookInfoActivity.onCatalogItemClick>>>>>>>");
        UMEventAnalyze.countEvent(BookDetailActivity.this, UMEventAnalyze.BOOKINFO_CATALOGUE_START);
        BookTable bookTable = BookDao.getInstance().findBook(bookid);
        if (bookTable == null && mBookDetail != null) {
            BookDao.getInstance().addBook(mBookDetail);
        }
        EnterBookContent.openBookContent(this, bookid, chapterId);
        if (BookContentModel.PARENT_ACTIVITY.equals(tag)) {//判断打开该activity的入口
            finish();
        }
    }


    @Override
    public void onFinished(List<ChapterTable> chapterTables, int resultCode) {
        mRefresh.setRefreshing(false);
        if (chapterTables != null && chapterTables.size() > 0 && resultCode == XSErrorEnum.SUCCESS.getCode()) {
            if (detailCatalogueDialog != null) {
                detailCatalogueDialog.setData(chapterTables);
            }
        }
    }

    /**
     * 启动写评论activity
     */
    private void startWriteActivity() {
        Intent intent = new Intent(this, WriteCommentActivity.class);
        intent.putExtra("bookid", bookid);
        startActivityForResult(intent, WRITHE_REQUEST_CODE);
        UMEventAnalyze.countEvent(BookDetailActivity.this, UMEventAnalyze.BOOKINFO_COMMENT);
    }

    /**
     * 评论的activity
     */
    private void startCommentActivity() {
        Intent intent = new Intent(this, BookCommentActivity.class);
        intent.putExtra("bookid", bookid);
        startActivityForResult(intent, COMMENT_REQUEST_CODE);
        UMEventAnalyze.countEvent(BookDetailActivity.this, UMEventAnalyze.BOOKINFO_COMMENT);
    }


    private void addBook() {
        if (mBookDetail != null && !"".equals(mBookDetail.getBookId())) {
            mBookDetail.setIsOnShelf(1);
            mBookDetail.setAddToShelfTime(System.currentTimeMillis());
            BookTable book = BookDao.getInstance().findBook(bookid);

            if (book != null) {
                mBookDetail.setLastReadChapter(book.getLastReadChapter());
                mBookDetail.setLastReadDate(book.getLastReadDate());
                mBookDetail.setLastReadLocation(book.getLastReadLocation());
                mBookDetail.setCatalogUpdateTime(book.getCatalogUpdateTime());
                mBookDetail.setReadPercentage(book.getReadPercentage());
            }
            LogUtils.debug("book==========mid=" + mBookDetail.getBookId() + "=====" + mBookDetail);
            BookDao.getInstance().addBook((BookTable) mBookDetail);
        }

        if (UserHelper.getInstance().isLogin()) {
            BookShelfDataUtil.addBook(bookid, new BookShelfDataUtil.BookShelfDataListener() {
                @Override
                public void back(int response) {
                    if (10014 == response) {
                        LogUtils.debug("没有书");
                    }
                    if (10020 == response) {
                        ViewUtils.toastShort(getString(R.string.detail_this_book_is_on_shelf));
                        mIvDetailAddshelf.setImageResource(R.mipmap.detail_addshelf_normal);
                        mIvDetailAddshelf.setClickable(false);
                        return;
                    }
                    if (0 != response) {
                        ViewUtils.toastShort(getString(R.string.detail_add_fail));
                        return;
                    }
                    ViewUtils.toastShort(getString(R.string.detail_add_success));
                    mIvDetailAddshelf.setImageResource(R.mipmap.detail_addshelf_normal);
                    mIvDetailAddshelf.setClickable(false);
                }
            });
        } else {
            ViewUtils.toastShort(getString(R.string.detail_add_success));
            mIvDetailAddshelf.setImageResource(R.mipmap.detail_addshelf_normal);
            mIvDetailAddshelf.setClickable(false);
        }
    }

    /**
     * 判断是否在数据，设置“添加书架”的显示样式
     */
    private void setIsOnShelf() {
        //设置是否可以加入书架
        BookTable bookTable = BookDao.getInstance().findBook(bookid);
        LogUtils.debug("根据id查询出的书。。。" + bookTable);
        if (bookTable == null || bookTable.getIsOnShelf() == 0) {
            LogUtils.debug("不在书架");
            mIvDetailAddshelf.setImageResource(R.mipmap.detail_addshelf_select);
            mIvDetailAddshelf.setClickable(true);
        } else {
            LogUtils.debug("在书架");
            mIvDetailAddshelf.setImageResource(R.mipmap.detail_addshelf_normal);
            mIvDetailAddshelf.setClickable(false);
        }
    }

}
