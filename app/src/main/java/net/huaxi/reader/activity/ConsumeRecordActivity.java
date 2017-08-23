package net.huaxi.reader.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
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
import com.tools.commonlibs.tools.DateUtils;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.ViewUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.adapter.AdapterConsumeRecord;
import net.huaxi.reader.bean.ConsumeRecord;
import net.huaxi.reader.bean.ConsumeRecordCustom;
import net.huaxi.reader.common.CommonUtils;
import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.XSNetEnum;
import net.huaxi.reader.https.GetRequest;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.view.FooterView;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 消费记录
 */
public class ConsumeRecordActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private TextView consume_today_before;
    private ListView lvConsume;
    private ImageView ivBack;
    private int cantoday = 0;
    private AdapterConsumeRecord adapterConsumeRecord;
    SwipeRefreshLayout frame;
    private RelativeLayout rlTitle;
    private View rlNoConsume, rlNoNet;
    List<ConsumeRecordCustom> consumelist = new ArrayList<ConsumeRecordCustom>();
    List<ConsumeRecordCustom> consumelisttoday = new ArrayList<ConsumeRecordCustom>();
    private int pagenow;//当前页数
    private static final int PAGENUM = 20;//一页的内容
    private FooterView footerView;
    private int pagecount;//总条数
    Type type = new TypeToken<ArrayList<ConsumeRecordCustom>>() {
    }.getType();
    private View vLoadding;
    private long cdate;
    private String s;
    private boolean b;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consume_record);
        initView();
        initData(false);
        initEvent();
    }

    @Override
    public void onRefresh() {
        initData(false);
    }

    private void initData(boolean loaddingShow) {
        if (loaddingShow) {
            vLoadding.setVisibility(View.VISIBLE);
        }
        if (NetUtils.checkNetworkUnobstructed()) {
            rlNoNet.setVisibility(View.GONE);
        } else {
            rlNoNet.setVisibility(View.VISIBLE);
            frame.setRefreshing(false);
            return;

        }

        pagenow = 1;
        GetRequest request = new GetRequest(String.format(URLConstants.PAY_CONSUME_LSIT, pagenow, PAGENUM, cantoday) + CommonUtils
                .getPublicGetArgs(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                frame.setRefreshing(false);
                vLoadding.setVisibility(View.GONE);
//                Log.i("dongyongjie", "onResponse: " + response.toString());
                LogUtils.debug("response==" + response.toString());
                int errorid = ResponseHelper.getErrorId(response);
                if (XSNetEnum._VDATAERRORCODE_ERROR_CONSUME.getCode() == errorid) {
                    ViewUtils.toastShort(XSNetEnum._VDATAERRORCODE_ERROR_CONSUME.getMsg());
                    return;
                }
                if (!ResponseHelper.isSuccess(response)) {
                    ViewUtils.toastShort("获取消费列表失败");
                    return;
                }
                List<ConsumeRecordCustom> myconsumelist = new Gson().fromJson(ResponseHelper
                        .getVdata(response).optJSONArray("list")
                        .toString(), type);
                initConsumeRecord(myconsumelist);
                adapterConsumeRecord.notifyDataSetChanged();
                pagecount = ResponseHelper.getVdata(response).optInt("pagecount");
                if (pagecount <= pagenow) {
                    footerView.setViewState(FooterView.FOOTVIEW_STATE_NOMORE);
                } else {
                    footerView.setViewState(FooterView.FOOTVIEW_STATE_PRELOADDING);
                }
                if (myconsumelist.size() == 0) {
                    rlNoConsume.setVisibility(View.VISIBLE);
                } else {
                    rlNoConsume.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                vLoadding.setVisibility(View.GONE);
                frame.setRefreshing(false);
                LogUtils.debug("出错了");
            }
        }
        );

        RequestQueueManager.addRequest(request);
    }

    //初始化一个列表
    private void initConsumeRecord(List<ConsumeRecordCustom> myconsumelist) {
        consumelist.clear();
        ConsumeRecord preConsumeRecord = null;
        for (int i = 0; i < myconsumelist.size(); i++) {
            if (preConsumeRecord == null) {
                ConsumeRecordCustom consumeRecordCustom = new ConsumeRecordCustom();
                consumeRecordCustom.setType(ConsumeRecordCustom.VIEW_TYPE_MONTH);
                consumeRecordCustom.setMonthString(DateUtils.simpleDateFormat(new Date(myconsumelist.get(i).getCdate() * 1000),
                        "yyyy年MM月"));
                consumelist.add(consumeRecordCustom);
            } else {
                String preItemMothstring = DateUtils.simpleDateFormat(new Date(preConsumeRecord.getCdate() * 1000),
                        "yyyy年MM月");
                String monthstring = DateUtils.simpleDateFormat(new Date(myconsumelist.get(i).getCdate() * 1000),
                        "yyyy年MM月");
                if (null != preConsumeRecord && !preItemMothstring.equals(monthstring)) {
                    ConsumeRecordCustom consumeRecordCustom = new ConsumeRecordCustom();
                    consumeRecordCustom.setType(ConsumeRecordCustom.VIEW_TYPE_MONTH);
                    consumeRecordCustom.setMonthString(monthstring);
                    consumelist.add(consumeRecordCustom);
                }
            }
            consumelist.add(myconsumelist.get(i));
            preConsumeRecord = myconsumelist.get(i);
        }
    }

    private void addConsumeRecord(List<ConsumeRecordCustom> myconsumelist) {
        if (consumelist == null || consumelist.size() == 0) {
            return;
        }
        ConsumeRecord preConsumeRecord = consumelist.get(consumelist.size() - 1);
        for (int i = 0; i < myconsumelist.size(); i++) {
            String preItemMothstring = DateUtils.simpleDateFormat(new Date(preConsumeRecord.getCdate() * 1000),
                    "yyyy年MM月");
            String monthstring = DateUtils.simpleDateFormat(new Date(myconsumelist.get(i).getCdate() * 1000),
                    "yyyy年MM月");
            if (null != preConsumeRecord && !preItemMothstring.equals(monthstring)) {
                ConsumeRecordCustom consumeRecordCustom = new ConsumeRecordCustom();
                consumeRecordCustom.setType(ConsumeRecordCustom.VIEW_TYPE_MONTH);
                consumeRecordCustom.setMonthString(monthstring);
                consumelist.add(consumeRecordCustom);
            }
            consumelist.add(myconsumelist.get(i));
            preConsumeRecord = myconsumelist.get(i);
        }
    }

    private void loadNextData() {
        if (NetUtils.checkNetworkUnobstructed()) {
            rlNoNet.setVisibility(View.GONE);
        } else {
            rlNoNet.setVisibility(View.VISIBLE);
            return;

        }
        GetRequest request = new GetRequest(String.format(URLConstants.PAY_CONSUME_LSIT, ++pagenow, PAGENUM, cantoday) + CommonUtils
                .getPublicGetArgs(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                LogUtils.debug("response==" + response.toString());
                int errorid = ResponseHelper.getErrorId(response);
                if (XSNetEnum._VDATAERRORCODE_ERROR_CONSUME.getCode() == errorid) {
                    ViewUtils.toastShort(XSNetEnum._VDATAERRORCODE_ERROR_CONSUME.getMsg());
                    return;
                }
                if (!ResponseHelper.isSuccess(response)) {
                    ViewUtils.toastShort("获取消费列表失败");
                    return;
                }

                List<ConsumeRecordCustom> myconsumelist = new Gson().fromJson(ResponseHelper
                        .getVdata(response).optJSONArray("list")
                        .toString(), type);
                if (myconsumelist == null) {
                    return;
                }
                addConsumeRecord(myconsumelist);
                adapterConsumeRecord.notifyDataSetChanged();
                pagecount = ResponseHelper.getVdata(response).optInt("pagecount");
                if (pagecount == 0) {
                    return;
                }
                if (pagecount <= pagenow) {
                    footerView.setViewState(FooterView.FOOTVIEW_STATE_NOMORE);
                } else {
                    footerView.setViewState(FooterView.FOOTVIEW_STATE_PRELOADDING);
                }
                if (consumelist.size() == 0) {
                    rlNoConsume.setVisibility(View.VISIBLE);
                } else {
                    rlNoConsume.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.debug("出错了");
            }
        }
        );

        RequestQueueManager.addRequest(request);
    }

    private void initView() {
        //
        consume_today_before = (TextView) findViewById(R.id.consume_today_before);
        consume_today_before.setText("之前");
        //选择器
        consume_today_before.setOnClickListener(this);
        vLoadding = findViewById(R.id.consume_loading_layout);
        ivBack = (ImageView) findViewById(R.id.consume_back_imageview);
        lvConsume = (ListView) findViewById(R.id.consume_listview);
        adapterConsumeRecord = new AdapterConsumeRecord(ConsumeRecordActivity.this, consumelist);
        lvConsume.setAdapter(adapterConsumeRecord);
        frame = (SwipeRefreshLayout) findViewById(R.id.consume_ptrframeLayout);
        frame.setColorSchemeResources(R.color.c01_themes_color);
        frame.setOnRefreshListener(this);
        rlNoNet = findViewById(R.id.consume_net_layout);
        rlNoConsume = findViewById(R.id.consume_no_layout);
        ((ImageView) rlNoConsume.findViewById(R.id.bookshelf_nobook_image)).setImageResource(R.mipmap.common_charge_nothing);
        ((TextView) rlNoConsume.findViewById(R.id.bookshelf_nobook_textview)).setText("您还没购买过任何一本书");
        footerView = new FooterView(ConsumeRecordActivity.this);
        lvConsume.addFooterView(footerView);
        footerView.setViewState(FooterView.FOOTVIEW_STATE_NONE);
        rlTitle = (RelativeLayout) findViewById(R.id.consume_title_layout);
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
                if (!frame.isRefreshing()) {
                    rlNoNet.setVisibility(View.GONE);
                    frame.post(new Runnable() {
                        @Override
                        public void run() {
                            frame.setRefreshing(true);
                        }
                    });
                }
            }
        });

        lvConsume.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                lvConsume.smoothScrollToPosition(0);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.consume_today_before:
                if (consume_today_before.getText().equals("今日")) {
                    consume_today_before.setText("之前");
                    cantoday=0;
                    initData(false);
                    adapterConsumeRecord.notifyDataSetChanged();

                } else {
                    consume_today_before.setText("今日");
                    cantoday=1;
                    initData(false);
                    adapterConsumeRecord.notifyDataSetChanged();
                }
                break;
        }
    }


}
