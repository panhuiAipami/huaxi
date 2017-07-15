package net.huaxi.reader.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.StringUtils;
import com.tools.commonlibs.tools.Utils;
import com.tools.commonlibs.tools.ViewUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.adapter.AdapterAboutRecommend;
import net.huaxi.reader.adapter.AdapterSearchKey;
import net.huaxi.reader.adapter.AdapterSearchRecord;
import net.huaxi.reader.adapter.BaseRecyclerAdapter;
import net.huaxi.reader.adapter.XSAdapter;
import net.huaxi.reader.adapter.XSViewHolder;
import net.huaxi.reader.appinterface.SearchPresenter;
import net.huaxi.reader.appinterface.SearchViewListener;
import net.huaxi.reader.bean.SearchBean;
import net.huaxi.reader.bean.SearchKeyBean;
import net.huaxi.reader.common.Utility;
import net.huaxi.reader.presenter.SearchPresenterImpl;
import net.huaxi.reader.util.UMEventAnalyze;
import net.huaxi.reader.view.divider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.lankton.flowlayout.FlowLayout;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class SearchActivity extends BaseActivity implements SearchViewListener {
    // 定义TextView的Padding属性
    private static final int PADING_TOP_BOTTOM = 5;
    private static final int PADING_LEFT_RIGHT = 10;
    // 定义TextView的Margin属性
    private static final int MARGIN_LEFT_RIGHT = 4;
    private static final int MARGIN_TOP_BOTTOM = 8;
    private List<SearchBean> searchList = new ArrayList<>();
    private List<SearchBean> aboutList = new ArrayList<>();
    private AdapterSearchRecord adapterSearchRecord;
    private XSAdapter<String> adapterComment;
    private LinkedList<String> historyList = new LinkedList<>();
    private List<SearchKeyBean> searchKeys = new ArrayList<>();
    private AdapterAboutRecommend adapterAboutRecommed;
    private boolean isShowKeyList = true;
    private AdapterSearchKey mAdapter;
    private List<String> hotWords = new ArrayList<>();
    @BindView(R.id.search_before_search_imageview)
    ImageView ivSearch;

    @BindView(R.id.search_before_search_edittext)
    EditText etSearch;

    @BindView(R.id.iv_search_clear)
    ImageView ivClear;

    @BindView(R.id.search_recyclerview)
    RecyclerView rvSearch;

    @BindView(R.id.flv_group_hotkey)
    FlowLayout mFLview;

    @BindView(R.id.lv_history)
    ListView lvHistory;

    @BindView(R.id.ll_history_view)
    LinearLayout llHistoryView;

    @BindView(R.id.ll_history_hotkey)
    LinearLayout llHistory;

    @BindView(R.id.ll_hotkey)
    LinearLayout llHotKey;

    @BindView(R.id.lv_key)
    StickyListHeadersListView lvKey;

    @BindView(R.id.rv_about_recommend)
    RecyclerView rvRecommend;

    @BindView(R.id.ll_about_recommend)
    LinearLayout llRecommend;

    @BindView(R.id.bookshelf_nobook_image)
    ImageView imageError;

    @BindView(R.id.bookshelf_nobook_textview)
    TextView textError;

    @BindView(R.id.search_neterror_layout)
    View netErrorLayout;

    @BindView(R.id.search_datanull_layout)
    View dataNullLayout;
    private SearchPresenter mPresenter;
    private boolean isCompress = false;


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
        setContentView(R.layout.activity_search);
        ButterKnife.bind(SearchActivity.this);
        initViewAdapter();
        initEvent();
        mPresenter = new SearchPresenterImpl(this);
        mPresenter.getHistoryList("hotKey");
        mPresenter.searchHotKey();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            doEnterAnim();
        }
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UMEventAnalyze.countEvent(SearchActivity.this, UMEventAnalyze.SEARCH_PAGE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.saveHistoryList(historyList);
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }

    }

    @Override
    protected void onDestroy() {
        mPresenter.destroy();
        super.onDestroy();
    }


    @Override
    public void onKeyBack() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            doExitAnim();
        } else {
            ActivityCompat.finishAfterTransition(this);
        }
    }

    @OnClick({R.id.tv_search_send, R.id.iv_search_clear, R.id.tv_clear_history})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_search_send:
                onKeyBack();
                break;
            case R.id.iv_search_clear:
                etSearch.setText("");
                ivClear.setVisibility(View.GONE);
                break;
            case R.id.tv_clear_history:
                historyList.clear();
                llHistoryView.setVisibility(View.GONE);
                break;

            default:
                break;
        }
    }

    @Override
    public void onHistory(List<String> data) {
        if (data != null && data.size() > 0) {
            historyList.addAll(data);
            adapterComment.notifyDataSetChanged();
            llHistoryView.setVisibility(View.VISIBLE);
        } else {
            llHistoryView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onAboutBook(List<SearchBean> sList) {
        if (dialogLoading != null) {
            dialogLoading.cancel();
        }
        dataShowNull(false, R.mipmap.bookshelf_nobook_nothing, R.string.not_available_key);
        if (sList != null && sList.size() > 0) {
            aboutList.clear();
            aboutList.addAll(sList);
            llRecommend.setVisibility(View.VISIBLE);
            adapterAboutRecommed.notifyDataSetChanged();
            adapterAboutRecommed.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {

                @Override
                public void onItemClick(int position, Object data) {
                    if (data instanceof SearchBean) {
                        SearchBean searchBean = (SearchBean) data;
//                        Intent intent = new Intent(SearchActivity.this, BookDetailActivity.class);
                        Intent intent = new Intent(SearchActivity.this, BookDetailActivity.class);
                        intent.putExtra("bookid", searchBean.getBid());
                        startActivity(intent);
                        UMEventAnalyze.countEvent(SearchActivity.this, UMEventAnalyze.SEARCH_ABOUT);
                    }
                }
            });
        }

    }

    @Override
    public void onSearchData(List<SearchBean> datas) {
        if (datas != null && datas.size() > 0) {
            if (dialogLoading != null) {
                dialogLoading.cancel();
            }
            isShowKeyList = true;
            searchList.addAll(datas);
            adapterSearchRecord.notifyDataSetChanged();
            llRecommend.setVisibility(View.GONE);
            netError(true);
            dataShowNull(true, 0, 0);
        }
    }

    @Override
    public void onHotKey(List<String> keys) {
        if (keys != null && keys.size() > 0) {
            llHotKey.setVisibility(View.VISIBLE);
            hotWords.clear();
            hotWords.addAll(keys);
            inintSearchHot();
        }
    }


    @Override
    public void onSearchDB(List<SearchKeyBean> datas) {
        //查询到了提示的关键字
        if (datas != null && datas.size() > 0) {
            searchKeys.clear();
            searchKeys.addAll(datas);
            mAdapter.notifyDataSetChanged();
           // lvKey.setVisibility(View.VISIBLE);
            netError(true);
        } else {
            lvKey.setVisibility(View.GONE);
        }

    }


    private void initViewAdapter() {
        rvSearch.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        rvRecommend.setLayoutManager(new LinearLayoutManager(SearchActivity.this, LinearLayoutManager.HORIZONTAL, false));
        adapterSearchRecord = new AdapterSearchRecord(SearchActivity.this, searchList);
        adapterAboutRecommed = new AdapterAboutRecommend(SearchActivity.this, aboutList);
        rvSearch.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(getResources().getColor(R.color.c09_divider_color))
                .sizeResId(R.dimen.divider)
                .marginResId(R.dimen.leftmargin, R.dimen.rightmargin)
                .build());
        rvSearch.setAdapter(adapterSearchRecord);
        rvRecommend.setAdapter(adapterAboutRecommed);

        //历史记录的adapter
        adapterComment = new XSAdapter<String>(getApplicationContext(), R.layout.item_search_history, historyList) {

            @Override
            public void convert(XSViewHolder holder, String s) {
                holder.setText(R.id.tv_history_key, s);
            }
        };

        lvHistory.setAdapter(adapterComment);
        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isShowKeyList = false;
                llHistory.setVisibility(View.GONE);
                etSearch.setText(historyList.get(position).trim());
                etSearch.setSelection(etSearch.length());
                goSearch(historyList.get(position));
                isShowKeyList = true;
                UMEventAnalyze.countEvent(SearchActivity.this, UMEventAnalyze.SEARCH_HISTORY);
            }
        });


        mAdapter = new AdapterSearchKey(this, searchKeys);
        lvKey.setAdapter(mAdapter);
        lvKey.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isShowKeyList = false;
                etSearch.setText(searchKeys.get(position).getKey().trim());
                etSearch.setSelection(etSearch.length());
                lvKey.setVisibility(View.GONE);
                goSearch(searchKeys.get(position).getKey().trim());
                isShowKeyList = true;
                UMEventAnalyze.countEvent(SearchActivity.this, UMEventAnalyze.SEARCH_SMART);
            }
        });

    }


    private void initEvent() {
        etSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        if (goSearch(etSearch.getText().toString())) {
                            return true;
                        }
                    }
                    LogUtils.debug("keycode==" + keyCode);
                }
                return false;
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isShowKeyList) {
                    mAdapter.setKey(s.toString());
                    mPresenter.searchSimpleKey(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    ivSearch.setVisibility(View.GONE);
                    ivClear.setVisibility(View.VISIBLE);
                    llHistory.setVisibility(View.GONE);
                    if (historyList.size() > 0) {
                        llHistoryView.setVisibility(View.VISIBLE);
                        adapterComment.notifyDataSetChanged();
                    }
                    //查关键字
                } else {
                    ivClear.setVisibility(View.GONE);
                    ViewUtils.setViewVisbility(ivSearch, View.VISIBLE);
                    llHistory.setVisibility(View.VISIBLE);
                    if (historyList.size() > 0) {
                        llHistoryView.setVisibility(View.VISIBLE);
                    } else {
                        llHistoryView.setVisibility(View.GONE);
                    }
                    llRecommend.setVisibility(View.GONE);
                    adapterComment.notifyDataSetChanged();
                    rvSearch.setVisibility(View.GONE);
                    dataNullLayout.setVisibility(View.GONE);
                }
            }
        });

        adapterSearchRecord.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object data) {
                if (data instanceof SearchBean) {
                    SearchBean bean = (SearchBean) data;
//                    Intent intent = new Intent(SearchActivity.this, BookDetailActivity.class);
                    Intent intent = new Intent(SearchActivity.this, BookDetailActivity.class);
                    intent.putExtra("bookid", bean.getBid());
                    startActivity(intent);
                    UMEventAnalyze.countEvent(SearchActivity.this, UMEventAnalyze.SEARCH_RESULT);
                }
            }
        });
    }


    private boolean goSearch(String s) {
        llHistory.setVisibility(View.GONE);
        if (!StringUtils.isBlank(s)) {//保存历史记录
            if (historyList.contains(s)) {
                historyList.remove(s);
            }
            if (historyList.size() >= 6) {
                historyList.removeLast();
            }
            historyList.addFirst(s);
        }

        if (!StringUtils.isBlank(s)) {
            searchList.clear();
            lvKey.setVisibility(View.GONE);
            if (NetUtils.checkNetworkUnobstructed()) {//判断网络
                dialogLoading = ViewUtils.showProgressDialog(SearchActivity.this);
                if (!StringUtils.isBlank(s)) {
                    mPresenter.searchFromNet(s);
                }
//                doSearch(s);
            } else {
                netError(false);
            }
            Utils.hideSoftKeyboard(etSearch);
            return true;
        }
        return false;
    }


    /**
     * 网络是否正常的界面设置
     */
    private void netError(boolean isSuccess) {

        if (dataNullLayout.getVisibility() == View.VISIBLE) {
            dataNullLayout.setVisibility(View.GONE);
        }

        if (isSuccess) {
            ViewUtils.setViewVisbility(netErrorLayout, View.INVISIBLE);
            ViewUtils.setViewVisbility(rvSearch, View.VISIBLE);
        } else {
            netErrorLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goSearch(etSearch.getText().toString());
                }
            });
            ViewUtils.setViewVisbility(netErrorLayout, View.VISIBLE);
            ViewUtils.setViewVisbility(rvSearch, View.INVISIBLE);
        }
    }

    /**
     * 数据为空时候的界面提示
     *
     * @param isSuccess
     * @param imageId
     * @param str
     */
    private void dataShowNull(boolean isSuccess, int imageId, int str) {

        if (netErrorLayout.getVisibility() == View.VISIBLE) {
            netErrorLayout.setVisibility(View.GONE);
        }
        if (isSuccess) {
            dataNullLayout.setVisibility(View.GONE);
            ViewUtils.setViewVisbility(rvSearch, View.VISIBLE);
            UMEventAnalyze.countEvent(SearchActivity.this, UMEventAnalyze.SEARCH_SHOW_DATA);
        } else {
            UMEventAnalyze.countEvent(SearchActivity.this, UMEventAnalyze.SEARCH_SHOW_NULL);
            imageError.setImageResource(imageId);
            textError.setText(getResources().getString(str));
            ViewUtils.setViewVisbility(dataNullLayout, View.VISIBLE);
            ViewUtils.setViewVisbility(rvSearch, View.GONE);
        }
    }


    private void inintSearchHot() {
        hotWords = Utility.randomList((ArrayList<String>) hotWords);//乱序list
        mFLview.removeAllViews();
        int ranHeight = Utils.dip2px(this, 30);
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ranHeight);
        lp.setMargins(Utils.dip2px(this, 10), 0, Utils.dip2px(this, 10), 0);
        for (int i = 0; i < hotWords.size(); i++) {
            String str = hotWords.get(i);
            TextView textView = createTextView(str);
            mFLview.addView(textView, lp);
            mFLview.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (!isCompress) {
                        mFLview.specifyLines(3);
//                        mFLview.relayoutToCompress();
                        mFLview.relayoutToAlign();
                        isCompress = true;
                    }
                    return true;
                }
            });
        }
    }

    /*
    * 创建一新的热词View
    */
    private TextView createTextView(String text) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = Utils.dip2px(this, MARGIN_LEFT_RIGHT);
        lp.rightMargin = Utils.dip2px(this, MARGIN_LEFT_RIGHT);
        lp.topMargin = Utils.dip2px(this, MARGIN_TOP_BOTTOM);
        lp.bottomMargin = Utils.dip2px(this, MARGIN_TOP_BOTTOM);
        TextView view = new TextView(getApplicationContext());
        view.setText(text);
        view.setTextSize(13);
        if (Math.random() * 10 > 2) {//随机设置颜色为红色
            view.setTextColor(Color.parseColor("#555555"));
        } else {
            view.setTextColor(Color.parseColor("#F9494D"));
        }
        view.setLayoutParams(lp);
        view.setGravity(Gravity.CENTER_VERTICAL);
        view.setPadding(Utils.dip2px(this, PADING_LEFT_RIGHT), Utils.dip2px(this, PADING_TOP_BOTTOM), Utils.dip2px(this, PADING_LEFT_RIGHT), Utils.dip2px(this, PADING_TOP_BOTTOM));
        view.setFocusableInTouchMode(false);
        view.setGravity(Gravity.CENTER);
        view.setBackgroundResource(R.drawable.selector_common_white_round_background);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowKeyList = false;
                goSearch(((TextView) v).getText().toString().trim());
                etSearch.setText(((TextView) v).getText().toString().trim());
                etSearch.setSelection(etSearch.length());
                UMEventAnalyze.countEvent(SearchActivity.this, UMEventAnalyze.SEARCH_HOT);
            }
        });
        return view;
    }


    /**
     * On Lollipop+ perform a circular reveal animation (an expanding circular mask) when showing
     * the search panel.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void doEnterAnim() {
        // Fade in a background scrim as this is a floating window. We could have used a
        // translucent window background but this approach allows us to turn off window animation &
        // overlap the fade with the reveal animation – making it feel snappier.
        View scrim = findViewById(R.id.scrim);
        scrim.animate()
                .alpha(1f)
                .setDuration(800L)
                .setInterpolator(
                        AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in))
                .start();

        // Next perform the circular reveal on the search panel
        final View searchPanel = findViewById(R.id.scrim);
        if (searchPanel != null) {
            // We use a view tree observer to set this up once the view is measured & laid out
            searchPanel.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            searchPanel.getViewTreeObserver().removeOnPreDrawListener(this);
                            // As the height will change once the initial suggestions are delivered by the
                            // loader, we can't use the search panels height to calculate the final radius
                            // so we fall back to it's parent to be safe
                            int revealRadius = ((ViewGroup) searchPanel.getParent()).getHeight();
                            // Center the animation on the top right of the panel i.e. near to the
                            // search button which launched this screen.
                            Animator show = ViewAnimationUtils.createCircularReveal(searchPanel,
                                    searchPanel.getRight(), searchPanel.getTop(), 0f, revealRadius);
                            show.setDuration(800L);
                            show.setInterpolator(AnimationUtils.loadInterpolator(SearchActivity.this,
                                    android.R.interpolator.fast_out_slow_in));
                            show.start();
                            return false;
                        }
                    });
        }
    }

    /**
     * On Lollipop+ perform a circular animation (a contracting circular mask) when hiding the
     * search panel.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void doExitAnim() {
        final View searchPanel = findViewById(R.id.scrim);
        // Center the animation on the top right of the panel i.e. near to the search button which
        // launched this screen. The starting radius therefore is the diagonal distance from the top
        // right to the bottom left
        int revealRadius = (int) Math.sqrt(Math.pow(searchPanel.getWidth(), 2)
                + Math.pow(searchPanel.getHeight(), 2));
        // Animating the radius to 0 produces the contracting effect
        Animator shrink = ViewAnimationUtils.createCircularReveal(searchPanel,
                searchPanel.getRight(), searchPanel.getTop(), revealRadius, 0f);
        shrink.setDuration(800L);
        shrink.setInterpolator(AnimationUtils.loadInterpolator(SearchActivity.this,
                android.R.interpolator.fast_out_slow_in));
        shrink.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                searchPanel.setVisibility(View.INVISIBLE);
                ActivityCompat.finishAfterTransition(SearchActivity.this);
            }
        });
        shrink.start();

        // We also animate out the translucent background at the same time.
        findViewById(R.id.scrim).animate()
                .alpha(0f)
                .setDuration(800L)
                .setInterpolator(
                        AnimationUtils.loadInterpolator(SearchActivity.this,
                                android.R.interpolator.fast_out_slow_in))
                .start();
    }


}
