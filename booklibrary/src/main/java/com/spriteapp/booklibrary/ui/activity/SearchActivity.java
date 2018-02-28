package com.spriteapp.booklibrary.ui.activity;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseTwo;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.HotBean;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.adapter.FlowAdapter;
import com.spriteapp.booklibrary.ui.adapter.SearchAdapter;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.ColorUtils;
import com.spriteapp.booklibrary.util.FileHelper;
import com.spriteapp.booklibrary.util.NetworkUtil;
import com.spriteapp.booklibrary.util.ToastUtil;
import com.spriteapp.booklibrary.widget.recyclerview.URecyclerView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * 搜索
 */
public class SearchActivity extends TitleActivity implements SwipeRefreshLayout.OnRefreshListener {
    public EditText searsh_edit;
    private TextView searsh_text;
    private ImageView clear_history;
    private FrameLayout fr;
    private InputMethodManager inputmanager;
    private int page = 1;
    private String content = "";
    private SwipeRefreshLayout mRefresh;
    private URecyclerView search_list;
    private LinearLayout null_layout;
    //新
    private LinearLayout hot_history_layout;
    private TextView history_text, hot_text;
    private TagFlowLayout history_flowlayout, hot_flowlayout;
    private List<String> history = new ArrayList<>();
    private List<String> hot = new ArrayList<>();
    private FlowAdapter historyFlowAdapter, hotFlowAdapter;
    private Map<String, String> list = new LinkedHashMap<>();
    private List<String> bookIds = new ArrayList<>();
    private List<BookDetailResponse> mDetailResponseList = new ArrayList<>();
    private SearchAdapter adapter;
    private TextView miaoshu, search_title;


    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_search, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }


    @Override
    public void initData() throws Exception {
        setTitle("搜索");
        initListerer();
        gethistory();
        getHttp();

    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
        ColorUtils.setColor();
        mTitleLayout.setVisibility(View.GONE);
        inputmanager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        searsh_edit = (EditText) findViewById(R.id.send_searsh_edit);
        searsh_text = (TextView) findViewById(R.id.searsh_text);
        search_title = (TextView) findViewById(R.id.search_title);
        hot_history_layout = (LinearLayout) findViewById(R.id.hot_history_layout);
        history_text = (TextView) findViewById(R.id.history_text);
        hot_text = (TextView) findViewById(R.id.hot_text);
        mRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        clear_history = (ImageView) findViewById(R.id.clear_history);
        history_flowlayout = (TagFlowLayout) findViewById(R.id.history_flowlayout);
        hot_flowlayout = (TagFlowLayout) findViewById(R.id.hot_flowlayout);
        search_list = (URecyclerView) findViewById(R.id.search_list);
        null_layout = (LinearLayout) findViewById(R.id.null_layout);
        miaoshu = (TextView) findViewById(R.id.miaoshu);
        miaoshu.setText("没有符合的书籍，换个关键字试试");
        null_layout.setVisibility(View.GONE);
        mRefresh.setOnRefreshListener(this);
        mRefresh.setColorSchemeResources(R.color.square_comment_selector);
        historyFlowAdapter = new FlowAdapter(history, this);
        hotFlowAdapter = new FlowAdapter(hot, this);
        history_flowlayout.setAdapter(historyFlowAdapter);
        hot_flowlayout.setAdapter(hotFlowAdapter);
        search_list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchAdapter(this, mDetailResponseList);
        search_list.setAdapter(adapter);
        gone_or_visibility();
    }

    public void initListerer() {
        searsh_text.setOnClickListener(this);
        clear_history.setOnClickListener(this);
        hot_flowlayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                searsh_edit.setText(hot.get(position));
                searsh_edit.setSelection(searsh_edit.getText().length());
                content = hot.get(position);
                getBook();//点击历史记录直接搜索
                return false;
            }
        });
        search_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                        inputmanager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                }
                return false;
            }
        });
        history_flowlayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                searsh_edit.setText(history.get(position));
                searsh_edit.setSelection(searsh_edit.getText().length());
                content = history.get(position);
                getBook();//点击历史记录直接搜索
                return false;
            }
        });
        searsh_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searsh_edit.getText().toString().trim().length() != 0) {
                    searsh_text.setText("搜索");

                } else {
                    searsh_text.setText("取消");
                    hot_history_layout.setVisibility(View.VISIBLE);
                    if (mDetailResponseList.size() != 0) {
                        mDetailResponseList.clear();
                        adapter.notifyDataSetChanged();
                        gone_or_visibility();
                    }
                    gone_or_visibility();

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searsh_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                    searchBtn(2);
                return true;
            }
        });
    }

    /**
     * 得到热门搜索与搜索历史记录
     */
    public void gethistory() {
        try {
            if (history.size() == 0) {//banner轮播文件
                HotBean listBean = FileHelper.readObjectFromJsonFile(this, Constant.HOT_LIST, HotBean.class);
                if (listBean != null && listBean.getHot() != null && listBean.getHot().size() != 0) {
                    Map<String, String> map = listBean.getHot();
                    Set<Map.Entry<String, String>> set = map.entrySet();
                    for (Map.Entry<String, String> me : set) {
                        // 根据键值对对象获取键和值
                        bookIds.add(me.getKey());
                    }
//                Collections.reverse(bookIds);//倒叙
                    Log.d("history11", "history.size===" + history.size());
                    history.addAll(bookIds);
                    historyFlowAdapter.notifyDataChanged();
                    gone_or_visibility();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == searsh_text) {
            searchBtn(1);
        } else if (v == clear_history) {
            history.clear();
            gone_or_visibility();
        }
    }

    public void searchBtn(int type) {
        content = searsh_edit.getText().toString().trim();
        if ("取消".equals(searsh_text.getText()) && type == 1)
            finish();
        else if ("搜索".equals(searsh_text.getText())) {
            if (!TextUtils.isEmpty(searsh_edit.getText())) {
                getBook();
            } else {
                ToastUtil.showToast("请输入关键字");
            }
        }
    }

    public void gone_or_visibility() {
        if (mDetailResponseList.size() == 0) {
            hot_history_layout.setVisibility(View.VISIBLE);
            mRefresh.setVisibility(View.GONE);
            search_title.setVisibility(View.GONE);
            if (TextUtils.isEmpty(searsh_edit.getText()) && "取消".equals(searsh_text.getText()))
                null_layout.setVisibility(View.GONE);
        } else {
            hot_history_layout.setVisibility(View.GONE);
            null_layout.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(content))
                search_title.setText("搜索“" + content + "”的结果:");
            search_title.setVisibility(View.VISIBLE);
            mRefresh.setVisibility(View.VISIBLE);
            return;
        }
        if (history.size() == 0) {//显示隐藏recyclerview
            history_flowlayout.setVisibility(View.GONE);
            history_text.setVisibility(View.GONE);
            clear_history.setVisibility(View.GONE);
        } else {
            history_flowlayout.setVisibility(View.VISIBLE);
            history_text.setVisibility(View.VISIBLE);
            clear_history.setVisibility(View.VISIBLE);
        }
        if (hot.size() == 0) {//显示隐藏recyclerview
            hot_flowlayout.setVisibility(View.GONE);
            hot_text.setVisibility(View.GONE);
        } else {
            hot_flowlayout.setVisibility(View.VISIBLE);
            hot_text.setVisibility(View.VISIBLE);
        }
    }

    public void setNullLayout() {
        hot_history_layout.setVisibility(View.GONE);
        mRefresh.setVisibility(View.GONE);
        null_layout.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(content))
            search_title.setText("搜索“" + content + "”的结果:");
        search_title.setVisibility(View.VISIBLE);
    }

    public void getHttp() {
        BookApi.getInstance()
                .service
                .book_search(Constant.JSON_TYPE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseTwo<List<String>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseTwo<List<String>> bookStoreResponse) {
                        if (bookStoreResponse != null) {
                            int resultCode = bookStoreResponse.getCode();
                            if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功
                                if (bookStoreResponse.getHot() != null && bookStoreResponse.getHot().size() != 0) {
                                    hot.clear();
                                    hot.addAll(bookStoreResponse.getHot());
                                    hotFlowAdapter.notifyDataChanged();
                                }
                                gone_or_visibility();
                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        mRefresh.setRefreshing(false);
                    }
                });
    }

    public void getBook() {
        if (TextUtils.isEmpty(content)) return;
        searsh_text.setText("取消");
        toSearch();
        for (int i = 0; i < history.size(); i++) {
            if (history.get(i).equals(content)) {
                history.remove(i);
                break;
            }
        }
        history.add(0, content);
        historyFlowAdapter.notifyDataChanged();
        if (history.size() >= 10) {
            history.remove(history.size() - 1);
        }
    }

    public void toSearch() {
        if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            inputmanager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        BookApi.getInstance()
                .service
                .book_search(Constant.JSON_TYPE, content)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<List<BookDetailResponse>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base<List<BookDetailResponse>> bookDetailResponse) {
                        if (bookDetailResponse != null) {
                            int resultCode = bookDetailResponse.getCode();
                            if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功
                                if (bookDetailResponse.getData() != null && bookDetailResponse.getData().size() != 0 && bookDetailResponse.getCommand() == 0) {//关键字
                                    mDetailResponseList.clear();
                                    mDetailResponseList.addAll(bookDetailResponse.getData());
                                    adapter.notifyDataSetChanged();
                                    gone_or_visibility();
                                } else if (bookDetailResponse.getData() != null && bookDetailResponse.getData().size() != 0 && bookDetailResponse.getCommand() == 1) {//识别码
                                    //识别码直接跳转到阅读界面
                                    ActivityUtil.toReadActivityPassword(SearchActivity.this, bookDetailResponse.getData().get(0).getBook_id(), bookDetailResponse.getData().get(0).getChapter_id());
                                    searsh_text.setText("搜索");
                                    if (history.size() != 0 && history.get(0).startsWith("hx")) {//判断如果是口令则删除本条历史记录
                                        history.remove(0);
                                        historyFlowAdapter.notifyDataChanged();
                                    }

                                } else {//无数据
                                    setNullLayout();
                                }

                            }

                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        mRefresh.setRefreshing(false);
                    }
                });
    }

    @Override
    protected void onDestroy() {

        try {
            if (history != null) {
                list.clear();
                for (int i = 0; i < history.size(); i++) {
                    list.put(history.get(i), history.get(i));
                }
                HotBean bean = new HotBean();
                bean.setHot(list);
                FileHelper.writeObjectToJsonFile(this, Constant.HOT_LIST, bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        if (!NetworkUtil.isAvailable(this))
            mRefresh.setRefreshing(false);
        if (TextUtils.isEmpty(content)) return;
        toSearch();
    }
}
