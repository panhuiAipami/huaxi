package net.huaxi.reader.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.ViewUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.adapter.AdapterRechargeRecord;
import net.huaxi.reader.bean.ChargeRecord;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.dialog.RechargeRecordDetailDialog;
import net.huaxi.reader.https.GetRequest;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.statistic.ReportUtils;
import net.huaxi.reader.view.FooterView;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 充值记录
 */
public class RechargeRecordActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final int PAGENUM = 20;//一页的内容
    SwipeRefreshLayout frame;
    private ImageView ivBack;
    private ListView lvRechargeRecord;
    private AdapterRechargeRecord adapterRechargeRecord;
    private List<ChargeRecord> recordList = new ArrayList<ChargeRecord>();
    private View rlNoRecord, rlNoNet, vLoadding;
    private RelativeLayout rlTitle;
    private int pagenow;//当前页数
    private FooterView footerView;
    private int pagecount;//总条数

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
        setContentView(R.layout.activity_recharge_record);
        initView();
        initEvent();
        initData();
    }

    @Override
    public void onRefresh() {
        initData();
    }

    private void initData() {
        if (!NetUtils.checkNetworkUnobstructed()) {
            rlNoNet.setVisibility(View.VISIBLE);
            return;
        } else {
            rlNoNet.setVisibility(View.GONE);
        }

        pagenow = 1;
        GetRequest request = new GetRequest(String.format(URLConstants.PAY_ORDER_LIST, pagenow, PAGENUM) + CommonUtils
                .getPublicGetArgs(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                frame.setRefreshing(false);
                vLoadding.setVisibility(View.GONE);
                LogUtils.debug("response==" + response.toString());
                if (!ResponseHelper.isSuccess(response)) {
                    ViewUtils.toastShort("获取支付列表失败");
                    return;
                }

                Type type = new TypeToken<ArrayList<ChargeRecord>>() {
                }.getType();
                List myrecordList = new Gson().fromJson(ResponseHelper.getVdata(response).optJSONArray("list")
                                .toString()
                        , type);
                if (myrecordList == null) {
                    return;
                }
                recordList.clear();
                recordList.addAll(myrecordList);
                adapterRechargeRecord.notifyDataSetChanged();
                pagecount = ResponseHelper.getVdata(response).optInt("pagecount");
                if (pagecount <= pagenow) {
                    footerView.setViewState(FooterView.FOOTVIEW_STATE_NOMORE);
                } else {
                    footerView.setViewState(FooterView.FOOTVIEW_STATE_PRELOADDING);
                }
                if (myrecordList.size() == 0) {
                    rlNoRecord.setVisibility(View.VISIBLE);
                } else {
                    rlNoRecord.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                vLoadding.setVisibility(View.GONE);
                frame.setRefreshing(false);
                vLoadding.setVisibility(View.GONE);
                ReportUtils.reportError(error);
            }
        });
        RequestQueueManager.addRequest(request);
    }

    private void loadNextData() {
        if (!NetUtils.checkNetworkUnobstructed()) {
            frame.setRefreshing(false);
            rlNoNet.setVisibility(View.VISIBLE);

            return;
        } else {
            rlNoNet.setVisibility(View.GONE);
        }

        GetRequest request = new GetRequest(String.format(URLConstants.PAY_ORDER_LIST, pagenow++, PAGENUM) + CommonUtils
                .getPublicGetArgs(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                frame.setRefreshing(false);
                LogUtils.debug("response==" + response.toString());
                if (!ResponseHelper.isSuccess(response)) {
                    ViewUtils.toastShort("获取支付列表失败");

                    return;
                }

                Type type = new TypeToken<ArrayList<ChargeRecord>>() {
                }.getType();
                List myrecordList = new Gson().fromJson(ResponseHelper.getVdata(response)
                                .optJSONArray("list")
                                .toString()
                        , type);
                if (myrecordList == null) {
                    return;
                }
                recordList.addAll(myrecordList);
                adapterRechargeRecord.notifyDataSetChanged();
                pagecount = ResponseHelper.getVdata(response).optInt("pagecount");
                if (pagecount == 0) {
                    return;
                }
                if (pagecount <= pagenow) {
                    footerView.setViewState(FooterView.FOOTVIEW_STATE_NOMORE);
                } else {
                    footerView.setViewState(FooterView.FOOTVIEW_STATE_PRELOADDING);
                }
                if (myrecordList.size() == 0) {
                    rlNoRecord.setVisibility(View.VISIBLE);
                } else {
                    rlNoRecord.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                frame.setRefreshing(false);
                ReportUtils.reportError(error);
//                LogUtils.debug("出错了" + error.networkResponse.statusCode);
            }
        });
        RequestQueueManager.addRequest(request);
    }

    private void initView() {
        vLoadding = findViewById(R.id.recharge_loading_layout);
        ivBack = (ImageView) findViewById(R.id.recharge_record_back_imageview);
        lvRechargeRecord = (ListView) findViewById(R.id.recharge_record_listview);
        frame = (SwipeRefreshLayout) findViewById(R.id.recharge_record_ptrframeLayout);
        frame.setColorSchemeResources(R.color.c01_themes_color);
        frame.setOnRefreshListener(this);

        rlNoRecord = findViewById(R.id.recharge_record_no_layout);
        ((TextView) rlNoRecord.findViewById(R.id.bookshelf_nobook_textview)).setText
                ("您还没为当前帐户充过值");
        ((ImageView) rlNoRecord.findViewById(R.id.bookshelf_nobook_image)).setImageResource(R.mipmap.common_charge_nothing);
        rlNoNet = findViewById(R.id.recharge_record_net_layout);
        adapterRechargeRecord = new AdapterRechargeRecord(RechargeRecordActivity.this,
                recordList);

        lvRechargeRecord.setAdapter(adapterRechargeRecord);
        footerView = new FooterView(RechargeRecordActivity.this);
        lvRechargeRecord.addFooterView(footerView);
        footerView.setViewState(FooterView.FOOTVIEW_STATE_NONE);
        rlTitle = (RelativeLayout) findViewById(R.id.recharge_record_title_layout);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initEvent() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rlNoNet.setOnClickListener(new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetUtils.checkNetworkUnobstructed()) {
                    ViewUtils.toastShort(getString(R.string.not_available_network));
                    return;
                }
            }
        });
        lvRechargeRecord.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                    if (pagecount <= pagenow) {
                    } else {
                        LogUtils.debug("加载下一页");
                        loadNextData();
                        footerView.setViewState(FooterView.FOOTVIEW_STATE_LOADDING);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {

            }
        });

        rlTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvRechargeRecord.smoothScrollToPosition(0);
            }
        });
        lvRechargeRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (recordList.size() <= position) {
                    return;
                }
                selectedRecord = recordList.get(position);
                if (selectedRecord == null) {
                    return;
                }
                RechargeRecordDetailDialog _dailog = new RechargeRecordDetailDialog(RechargeRecordActivity.this,
                        AppContext.getInstance().getString(R.string.rechargedetail),
                        selectedRecord,
                        AppContext.getInstance().getString(R.string.recharge_detail_copy),
                        AppContext.getInstance().getString(R.string.recharge_detail_ignore));
                _dailog.setCommonDialogListener(new RechargeRecordDetailDialog.CommonDialogListener() {
                    @Override
                    public void ok(Dialog dialog) {
                        copy(selectedRecord.getOrsn(), RechargeRecordActivity.this);
                        dialog.dismiss();
                        toast("成功复制订单号到剪贴板");
                    }

                    @Override
                    public void cancel(Dialog dialog) {
                        dialog.dismiss();
                    }
                });
                _dailog.show();
            }
        });
    }


    private ChargeRecord selectedRecord;

    /**
     * 复制内容到剪切板
     *
     * @param content
     * @param context
     */
    public static void copy(String content, Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }


}
