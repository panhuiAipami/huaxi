package net.huaxi.reader.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.common.CommonApp;
import com.tools.commonlibs.common.NetType;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.StringUtils;
import com.tools.commonlibs.tools.ViewUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.adapter.DownLoadExpandableListAdapter;
import net.huaxi.reader.appinterface.ChapterDownloadListener;
import net.huaxi.reader.appinterface.onCatalogLoadFinished;
import net.huaxi.reader.bean.DownLoadChild;
import net.huaxi.reader.bean.DownLoadGroup;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.Constants;
import net.huaxi.reader.common.EnterBookContent;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.common.XSErrorEnum;
import net.huaxi.reader.db.model.ChapterTable;
import net.huaxi.reader.https.DownLoadThreadLoader;
import net.huaxi.reader.https.GetRequest;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.https.XSKEY;
import net.huaxi.reader.model.DownloadBuyHelp;
import net.huaxi.reader.model.DownloadDailogHelp;
import net.huaxi.reader.model.DownloadDataFactory;
import net.huaxi.reader.util.UMEventAnalyze;
import net.huaxi.reader.util.XSFileUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Saud on 16/1/12.
 */
public class DownLoadActivity extends BaseActivity implements onCatalogLoadFinished, ChapterDownloadListener {
    private List<ChapterTable> mChapterTables;
    private List<List<ChapterTable>> lists;
    private List<DownLoadGroup> groupList;
    private DownLoadExpandableListAdapter adapter;
    private boolean isSelect = false;
    private String bookid;

    private List<String> chapterIds = new ArrayList();
    private Set<String> downloadChapters = new HashSet<>();
    private int freeNum;
    private DownloadDataFactory downloadDataFactory;
    private DownloadBuyHelp downloadBuyHelp;
    private DownloadDailogHelp downloadDailogHelp;
    private MaterialDialog progressDialog;
    private int downloadErrorNum = 0;
    private boolean isDestroy;
    private boolean isAnyoneNeedDownload;
    private boolean isChecked = false;
    private int coins = 0;

    @BindView(R.id.tv_download_select_all)
    TextView mTvDownloadSelectAll;
    @BindView(R.id.bookshelf_nonet_textview)
    TextView mBookshelfNonetTextview;
    @BindView(R.id.xlv_download)
    ExpandableListView mXlvDownload;
    @BindView(R.id.tv_chapter_num)
    TextView mTvChapterNum;
    @BindView(R.id.tv_chapter_pay)
    TextView mTvChapterPay;
    @BindView(R.id.btn_download_buy)
    Button mBtnDownloadBuy;
    @BindView(R.id.tv_user_coin)
    TextView mTvUserCoin;
    @BindView(R.id.ll_download_listgroup)
    LinearLayout mLlDownloadListgroup;
    @BindView(R.id.search_neterror_layout)
    View netErrorLayout;
    @BindView(R.id.loading_error)
    View loading_error;
    @BindView(R.id.loading_dialog)
    View loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        bookid = getIntent().getStringExtra(EnterBookContent.BOOK_ID);
        if (StringUtils.isEmpty(bookid)) {
            bookid = "0";
        }
        ButterKnife.bind(this);
        mBtnDownloadBuy.setTextColor(getResources().getColor(R.color.c14_themes_color));
        initListener();
        initData();
        isDestroy = false;
    }

    private void initListener() {
        mXlvDownload.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                UMEventAnalyze.countEvent(DownLoadActivity.this, UMEventAnalyze.DOWNLOAD_CHILD_SELECT);
                DownLoadChild child = groupList.get(groupPosition).getChildList().get(childPosition);
                if (!chapterIsExist(child.getChapterId())) {//判断是否已经下载，
                    child.changeChecked();
                    adapter.notifyDataSetChanged();
                    countCheck();
                } else {//已下载
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ViewUtils.toastShort(getResources().getString(R.string.has_download));
                        }
                    });

                }
                return true;
            }
        });
        mBtnDownloadBuy.setClickable(false);
    }

    private void initData() {
        new DownLoadThreadLoader(this, bookid, this).start();
        syncUserCoin();
    }

    /**
     * 获取阅读币
     */
    private void syncUserCoin() {
        if (UserHelper.getInstance().isLogin()) {
            GetRequest payinforequest = new GetRequest(URLConstants.PAY_INFO + "?1=1" + CommonUtils
                    .getPublicGetArgs(), new Response
                    .Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    LogUtils.debug("response==" + response);
                    if (ResponseHelper.isSuccess(response)) {
                        JSONObject jsonObject = ResponseHelper.getVdata(response).optJSONObject("info");
                        if (jsonObject != null) {
                            String coins = jsonObject.optString(XSKEY.USER_INFO.COIN);
                            if (!StringUtils.isBlank(coins)) {
                                DownLoadActivity.this.coins = Integer.parseInt(coins);
                                String coNum = String.format(getString(R.string.download_user_coin), coins);
                                mTvUserCoin.setText(coNum);
                            }
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    LogUtils.debug(error.getMessage());
                }
            }, "1.1");
            RequestQueueManager.addRequest(payinforequest);
        }

    }


    @OnClick({R.id.tv_download_select_all, R.id.fl_download_back, R.id.loading_error, R.id.btn_download_buy})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_download_select_all:
                selectAll();
                break;
            case R.id.fl_download_back:
                finish();
                break;
            case R.id.loading_error:
                initData();
                break;
            case R.id.btn_download_buy:
                if (!isChecked) {
                    isChecked = true;
                    UMEventAnalyze.countEvent(DownLoadActivity.this, UMEventAnalyze.DOWNLOAD_BUY);
                    download();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 网络加载目录的回调，
     *
     * @param chapterTables
     * @param resultCode
     */
    @Override
    public void onFinished(List<ChapterTable> chapterTables, int resultCode) {
        loadingDialog.setVisibility(View.GONE);
        if (chapterTables != null && chapterTables.size() > 0 && resultCode == XSErrorEnum.SUCCESS.getCode()) {
            this.mChapterTables = chapterTables;
            Data();
            addDownloadList();
            ViewUtils.setViewVisbility(loading_error, View.GONE);
            mTvDownloadSelectAll.setClickable(true);
        } else {
            //网络错误或者加载失败的错误提示
            if (lists == null || lists.size() == 0) {
                mTvDownloadSelectAll.setClickable(false);
                ViewUtils.setViewVisbility(loading_error, View.VISIBLE);
                mBookshelfNonetTextview.setText(getResources().getString(R.string.download_error)+"("+resultCode+")");
            }
        }
    }

    @Override
    protected void onDestroy() {
        isDestroy = true;
        super.onDestroy();
    }

    /**
     * 现在
     */
    private void download() {

        boolean isNetOk = NetUtils.checkNet() != NetType.TYPE_NONE;
        netError(isNetOk);
        if (isNetOk) {

            if (downloadDailogHelp == null || (downloadBuyHelp != null && !downloadBuyHelp.isDownloading())) {//后台是否有下载任务

                downloadErrorNum = 0;
                if (chapterIds.isEmpty()) {//已经全部下载
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ViewUtils.toastShort(getString(R.string.all_download));
                        }
                    });
                    return;
                }

                downloadDailogHelp = new DownloadDailogHelp(DownLoadActivity.this);
                progressDialog = downloadDailogHelp.showProgressDeterminateDialog(chapterIds.size());
                progressDialog.getBuilder().onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog dialog, DialogAction which) {

                        if (dialog != null) {
                            downloadBuyHelp.stopDownload();
                        }
                        countCheck();
                    }
                });
                progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isChecked = false;
                    }
                });

                //购买下载
                downloadBuyHelp = new DownloadBuyHelp(this, chapterIds, bookid);
                downloadBuyHelp.setDownloadListener(this);
                downloadBuyHelp.start();

            } else {
                if (!isDestroy && downloadDailogHelp != null) {
                    countCheck();
                    downloadDailogHelp.showProgressDeterminateDialog();
                }
            }
        }
    }


    @Override
    public void needLogin() {
        if (downloadDailogHelp != null && !isDestroy) {
            downloadDailogHelp.dissmissProgressDeterminateDialog();
        }
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }


    @Override
    public void needCoin(int errorid) {
        if (downloadDailogHelp != null && !isDestroy) {
            downloadDailogHelp.dissmissProgressDeterminateDialog();
            downloadDailogHelp.showBuyDialog(errorid);
        }
    }


    @Override
    public void error() {
        if (downloadDailogHelp != null && !isDestroy) {
            downloadDailogHelp.dissmissProgressDeterminateDialog();
        }
        netError(false);
    }

    @Override
    public void success() {
        netError(true);
    }

    @Override
    public void downloadOneFinish(String chepterId) {
        if (progressDialog != null && !isDestroy) {
            downloadChapters.add(chepterId);
            progressDialog.incrementProgress(1);
        }
    }

    @Override
    public void downloadOneError(String chepterId) {
        downloadErrorNum++;
    }

    @Override
    public void dataError() {
        if (downloadDailogHelp != null && !isDestroy) {
            downloadDailogHelp.dissmissProgressDeterminateDialog();
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewUtils.toastShort(getString(R.string.data_something));
            }
        });

    }


    @Override
    public void downloadAllFinish(final int errorid) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isDestroy) {
                    progressDialog.dismiss();
                }
                netError(true);
                if (downloadErrorNum > 0) {//有下载失败的
                    String str = String.format(CommonApp.context().getString(R.string.download_failure), downloadErrorNum)+"("+errorid+")";
                    MaterialDialog downloadErrorDialog = downloadDailogHelp.showDownloadErrorDiolog(str);
                    downloadErrorDialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            download();
                        }
                    });
                } else {
                    changeButton(0, 0);
                    //全部下载成功
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ViewUtils.toastShort(getString(R.string.download_over));
                        }
                    });
                }
                adapter.notifyDataSetChanged();
            }
        });

        syncUserCoin();

    }


    private void Data() {
        downloadDataFactory = new DownloadDataFactory();
        freeNum = downloadDataFactory.getFreeNum(mChapterTables);
        lists = downloadDataFactory.split(mChapterTables, freeNum, Constants.DOWNLOAD_LIST_CHILD_NUM);
        if (groupList != null && groupList.size() > 0) {
            groupList.clear();
        }
        groupList = downloadDataFactory.dataFactory(freeNum, lists);
        adapter = new DownLoadExpandableListAdapter(DownLoadActivity.this, groupList);
        mXlvDownload.setAdapter(adapter);
    }

    /**
     * 全选或者不选
     */
    private void selectAll() {
        isSelect = !isSelect;
        if (groupList == null) {
            return;
        }
        for (DownLoadGroup downLoadGroup : groupList) {
            boolean isGroupDownloaded = true;
            for (DownLoadChild downLoadChild : downLoadGroup.getChildList()) {
                if (!chapterIsExist(downLoadChild.getChapterId())) {//文件不存在
                    downLoadChild.setChecked(isSelect);
                    isGroupDownloaded = false;
                }
            }
            if (!isGroupDownloaded) {//没有下载完全的组
                downLoadGroup.setChecked(isSelect);
            }
        }
        adapter.notifyDataSetChanged();
        countCheck();
    }


    /**
     * adapter中的groug的checkbox选择改变时会调用该方法
     */
    public void countCheck() {
        chapterIds.clear();
        int coinNum = 0;
        for (int i = 0; i < groupList.size(); i++) {
            List<DownLoadChild> childList = groupList.get(i).getChildList();
            for (int j = 0; j < childList.size(); j++) {
                DownLoadChild child = childList.get(j);
                if (child.isChecked()) {
                    String chapterId = child.getChapterId();
                    if (!chapterIsExist(chapterId)) {//本地不存在文件
                        chapterIds.add(chapterId);//添加到下载的list，准备下载
                        if (child.getIsSubscribe() == 0) {
                            coinNum += child.getPrice();
                        }
                    }
                }
            }
        }
        isAnyoneNeedDownload = (coinNum == 0 && chapterIds.size() == 0 && isSelect);
        if (isAnyoneNeedDownload) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ViewUtils.toastShort(getResources().getString(R.string.all_download));
                }
            });

        } else {
            if (isSelect) {
                mTvDownloadSelectAll.setText(getResources().getString(R.string.cancel_all));
                //全选
                UMEventAnalyze.countEvent(DownLoadActivity.this, UMEventAnalyze.DOWNLOAD_SELECT_ALL);
            } else {
                //取消全选
                mTvDownloadSelectAll.setText(getResources().getString(R.string.select_all));
                UMEventAnalyze.countEvent(DownLoadActivity.this, UMEventAnalyze.BOOKINFO_SELECT_NULL);
            }
        }
        changeButton(coinNum, chapterIds.size());
    }

    /**
     * 根据下载的个数改变下载按键的状态
     *
     * @param coinNum
     * @param chapterNum
     */
    private void changeButton(int coinNum, int chapterNum) {
        String chNum = String.format(getString(R.string.download_chapter_num), chapterNum);
        String coNum = String.format(getString(R.string.download_chapter_pay), coinNum);
        mTvChapterNum.setText(chNum);
        mTvChapterPay.setText(coNum);

        if (coinNum > coins) {//余额不足
            mBtnDownloadBuy.setText(getString(R.string.download_coin_not_enough));
        } else {
            mBtnDownloadBuy.setText(getString(R.string.download_ok));
        }

        if (chapterNum == 0 || coinNum > coins) {
            mBtnDownloadBuy.setClickable(false);
            mBtnDownloadBuy.setTextColor(getResources().getColor(R.color.c14_themes_color));
        } else {
            mBtnDownloadBuy.setClickable(true);
            mBtnDownloadBuy.setTextColor(getResources().getColor(R.color.c13_themes_color));
        }

    }

    /**
     * 判断章节是否已经下载，如果下载，加入一下子的集合中
     */

    private void addDownloadList() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                for (DownLoadGroup downLoadGroup : groupList) {
                    List<DownLoadChild> list = downLoadGroup.getChildList();
                    for (DownLoadChild downLoadChild : list) {
                        if (!downloadChapters.contains(downLoadChild.getChapterId())) {
                            if (XSFileUtils.ChapterExists(downLoadChild.getBookId(), downLoadChild.getChapterId())) {//本地不存在文件
                                downloadChapters.add(downLoadChild.getChapterId());
                                DownLoadActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * 章节是否已经下载。
     *
     * @return
     */
    public boolean chapterIsExist(String chapterId) {

        return downloadChapters.contains(chapterId);
    }

    /**
     * 网络是否正常的界面设置
     */
    private void netError(boolean netIsOk) {
        if (netIsOk) {
            ViewUtils.setViewVisbility(netErrorLayout, View.GONE);
            ViewUtils.setViewVisbility(mLlDownloadListgroup, View.VISIBLE);
        } else {
            netErrorLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    download();
                }
            });
            ViewUtils.setViewVisbility(netErrorLayout, View.VISIBLE);
            ViewUtils.setViewVisbility(mLlDownloadListgroup, View.GONE);
        }
    }

}

