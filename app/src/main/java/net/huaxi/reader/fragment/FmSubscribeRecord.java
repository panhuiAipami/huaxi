package net.huaxi.reader.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import com.tools.commonlibs.cache.RequestQueueManager;
import com.tools.commonlibs.tools.DateUtils;
import com.tools.commonlibs.tools.LogUtils;
import com.tools.commonlibs.tools.NetUtils;
import com.tools.commonlibs.tools.ViewUtils;
import net.huaxi.reader.activity.BookDetailActivity;
import net.huaxi.reader.db.model.BookTable;
import net.huaxi.reader.https.ResponseHelper;
import net.huaxi.reader.util.UMEventAnalyze;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.huaxi.reader.R;

import net.huaxi.reader.common.URLConstants;
import net.huaxi.reader.common.UserHelper;
import net.huaxi.reader.https.GetRequest;
import net.huaxi.reader.view.divider.HorizontalDividerItemDecoration;

/**
 * function:    订阅记录
 * author:      ryantao
 * create:      16/8/24
 * modtime:     16/8/24
 */
public class FmSubscribeRecord extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, BaseQuickAdapter.RequestLoadMoreListener{

    private int pageCount = 0;
    private int pageNum = 1;
    private long fresh_time = 0;

    private View nullLayout;
    private View errorLayout;
    private ImageView ivNull;
    private TextView tvCommentNull;
    private boolean isLoadingMore = false;

    private RecyclerView recyclerView;

    private CommentAdapter adapterComment;

    private SwipeRefreshLayout swipeRefreshLayout;

    private List<BookTable> mCommentList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fm_subscribe,null);

        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.c01_themes_color);
        swipeRefreshLayout.setOnRefreshListener(this);

        nullLayout = (View)root.findViewById(R.id.commont_null);
        nullLayout.setOnClickListener(this);
        errorLayout = (View)root.findViewById(R.id.loading_error);
        errorLayout.setOnClickListener(this);
        ivNull = (ImageView) root.findViewById(R.id.iv_comment_null);
        ivNull.setImageResource(R.mipmap.common_charge_nothing);
        tvCommentNull = (TextView) root.findViewById(R.id.tv_comment_null);
        tvCommentNull.setText(R.string.sub_record_none);
        recyclerView = (RecyclerView) root.findViewById(R.id.lv_comment);
        return root;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    private void initData() {

        View footer = getActivity().getLayoutInflater().inflate(R.layout.item_splite_line, null);
        footer.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .color(getResources().getColor(R.color.c09_divider_color))
                .sizeResId(R.dimen.divider)
                .build());
        adapterComment = new CommentAdapter(R.layout.item_read_record, mCommentList);
//        adapterComment.addHeaderView(footer);
//        adapterComment.addFooterView(footer);
        adapterComment.setOnLoadMoreListener(this);
        adapterComment.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        adapterComment.openLoadMore(pageNum, true);
        final View customLoading = getActivity().getLayoutInflater().inflate(R.layout.custom_loading, (ViewGroup) recyclerView.getParent(), false);
        adapterComment.setLoadingView(customLoading);
        //添加item监听事件
        adapterComment.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                List<BookTable> datas = adapterComment.getData();
                if (datas != null && datas.size() > 0 && datas.get(i) != null) {
                    BookTable bookTable = datas.get(i);
                    if (bookTable != null) {
                        UMEventAnalyze.countEvent(getActivity(),UMEventAnalyze.BOOK_SHELF_ORDER_RECORD_CLICK);
                        Intent intent = new Intent(getActivity(), BookDetailActivity.class);
                        intent.putExtra("bookid", bookTable.getBookId());
                        getActivity().startActivity(intent);
                    }
                }else{
                    ViewUtils.toastShort("数据异常");
                }
            }
        });
        recyclerView.setAdapter(adapterComment);

        pageNum = 1;
        swipeRefreshLayout.setRefreshing(true);
        loadData(pageNum);
    }

    private void loadData(int page) {
        if (!NetUtils.checkNetworkUnobstructed()) {
            errorLayout.setVisibility(View.VISIBLE);
            nullLayout.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        if (!UserHelper.getInstance().isLogin()) {
            errorLayout.setVisibility(View.GONE);
            nullLayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        String url = String.format(URLConstants.SUB_SCRIBE_URL, 100, page, 20);
        GetRequest getRequest = new GetRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogUtils.debug(" 订阅记录 "+response.toString());
                swipeRefreshLayout.setRefreshing(false);
                if (ResponseHelper.isSuccess(response)) {

                    try {
                        JSONObject vdata = ResponseHelper.getVdata(response);
                        pageNum = vdata.getInt("u_page_current");
                        pageCount = vdata.getInt("u_page_count");
                        fresh_time = vdata.getLong("u_fresh_time");
                        Type type = new TypeToken<ArrayList<BookTable>>() {
                        }.getType();
                        JSONArray jsonArray = vdata.getJSONArray("u_booklist");
                        List<BookTable> bookTables =  new Gson().fromJson(jsonArray.toString(), type);
                        if (bookTables != null) {
                            if (isLoadingMore) {
                                isLoadingMore = false;
                                adapterComment.notifyDataChangedAfterLoadMore(bookTables, true);
                            } else {
                                adapterComment.setNewData(bookTables);
                            }
                        }
                        if(pageNum >= pageCount){
                            adapterComment.notifyDataChangedAfterLoadMore(false);
                        }
                        adapterComment.notifyDataSetChanged();
                        if (adapterComment.getData().size() == 0) {
                            nullLayout.setVisibility(View.VISIBLE);
                            swipeRefreshLayout.setVisibility(View.GONE);
                        } else {
                            nullLayout.setVisibility(View.GONE);
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    nullLayout.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.GONE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                errorLayout.setVisibility(View.VISIBLE);
                nullLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.GONE);
            }
        });
        RequestQueueManager.addRequest(getRequest);
    }


    @Override
    public void onLoadMoreRequested() {
            isLoadingMore = true;
            pageNum = pageNum + 1;
            LogUtils.debug("More Pages");
            loadData(pageNum);
    }

    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.commont_null || v.getId() == R.id.loading_error) {
//            pageNum =1;
//            loadData(pageNum);
//        }
    }

    @Override
    public void onRefresh() {
        pageNum = 1;
        loadData(pageNum);
    }

    /**
     * 列表适配器
     */
    private class CommentAdapter extends BaseQuickAdapter<BookTable> {
        public CommentAdapter(int layoutResId, List<BookTable> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, BookTable bookTable) {
            if (bookTable != null) {
                baseViewHolder.setText(R.id.title_book_name, bookTable.getName());
                String date = DateUtils.formatStr2(bookTable.getLastDate());
                baseViewHolder.setText(R.id.title_read_date, date);
            }
        }

    }



}
