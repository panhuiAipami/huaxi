package com.spriteapp.booklibrary.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.base.BaseTwo;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.MyApprenticeBean;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.adapter.MyApprenticeAdapter;
import com.spriteapp.booklibrary.util.NetworkUtil;
import com.spriteapp.booklibrary.widget.recyclerview.URecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.spriteapp.booklibrary.ui.activity.ApprenticeListActivity.WITHDRAWALS_TYPE;

/**
 * Created by userfirst on 2018/3/13.
 */

public class ApprenticeFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, URecyclerView.LoadingListener {
    private View mView;
    private URecyclerView recyclerView;
    private SwipeRefreshLayout refresh;
    private RelativeLayout null_layout;
    private TextView front_hint, behind_hint, gold_num, behind_hint2;
    private LinearLayout bottom_layout;
    private List<MyApprenticeBean.PupilDataBean> list = new ArrayList<>();
    private MyApprenticeAdapter myApprenticeAdapter, activationApprenticeAdapter;
    private int page = 1;
    private int lastPage = 0;
    private int list_type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.apprentice_fragment_layout, container, false);
        return mView;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.apprentice_fragment_layout;
    }

    @Override
    public void configViews() {

    }

    @Override
    public void findViewId() {
        recyclerView = (URecyclerView) mView.findViewById(R.id.recyclerView);
        refresh = (SwipeRefreshLayout) mView.findViewById(R.id.refresh);
        null_layout = (RelativeLayout) mView.findViewById(R.id.null_layout);
        front_hint = (TextView) mView.findViewById(R.id.front_hint);
        behind_hint = (TextView) mView.findViewById(R.id.behind_hint);
        gold_num = (TextView) mView.findViewById(R.id.gold_num);
        behind_hint2 = (TextView) mView.findViewById(R.id.behind_hint2);
        bottom_layout = (LinearLayout) mView.findViewById(R.id.bottom_layout);
        refresh.setColorSchemeResources(R.color.square_comment_selector);
        recyclerView.setLoadingListener(this);
        Bundle bundle = getArguments();
        list_type = bundle.getInt(WITHDRAWALS_TYPE, 0);
        if (list_type == 0) {
            front_hint.setText(R.string.front2_text);
        } else if (list_type == 1) {
            front_hint.setText(R.string.front_text);
            bottom_layout.setVisibility(View.GONE);
        }
        myApprenticeAdapter = new MyApprenticeAdapter(getActivity(), list, list_type);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(myApprenticeAdapter);
        goneOrShow();
        listener();
        getData();
    }

    private void listener() {
        refresh.setOnRefreshListener(this);
        front_hint.setOnClickListener(this);
        behind_hint.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == behind_hint) {
            BookDetailResponse bookDetailResponse = new BookDetailResponse();
            HuaXiSDK.getInstance().showShareDialog(getActivity(), bookDetailResponse, true, 2);
        }

    }

    private void goneOrShow() {
        if (list.size() == 0) {
            null_layout.setVisibility(View.VISIBLE);
            if (list_type == 0)
                bottom_layout.setVisibility(View.GONE);
        } else {
            null_layout.setVisibility(View.GONE);
            if (list_type == 0)
                bottom_layout.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void initData() {
        super.initData();
    }


    public void getData() {
        if (list_type == 0) {//我的徒弟
            getApprenticeList();
        } else if (list_type == 1) {//唤醒徒弟
            getActivateApprenticeList();
        }
    }

    public void getApprenticeList() {//得到我的徒弟列表
        if (!NetworkUtil.isAvailable(getActivity())) return;
        BookApi.getInstance()
                .service
                .user_mypupillist(Constant.JSON_TYPE, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<MyApprenticeBean>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base<MyApprenticeBean> myApprenticeResponse) {
                        if (myApprenticeResponse != null) {
                            int resultCode = myApprenticeResponse.getCode();
                            if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功
                                if (myApprenticeResponse.getData() != null) {
                                    if (myApprenticeResponse.getData().getTotal_data() != null) {
                                        gold_num.setText(myApprenticeResponse.getData().getTotal_data().getTotal_gold_coins() + "");
                                    }
                                    if (myApprenticeResponse.getData().getPupil_data() != null && myApprenticeResponse.getData().getPupil_data().size() != 0) {
                                        if (page == 1) list.clear();
                                        list.addAll(myApprenticeResponse.getData().getPupil_data());
                                        page++;
                                        myApprenticeAdapter.notifyDataSetChanged();
                                        goneOrShow();
                                    }
                                }


                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        refresh.setRefreshing(false);
                        dismissDialog();
                    }
                });
    }

    public void getActivateApprenticeList() {//得到待唤醒徒弟列表
        if (!NetworkUtil.isAvailable(getActivity())) return;
        BookApi.getInstance()
                .service
                .user_myawakepupillist(Constant.JSON_TYPE, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base<List<MyApprenticeBean.PupilDataBean>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base<List<MyApprenticeBean.PupilDataBean>> activateResponse) {
                        if (activateResponse != null) {
                            int resultCode = activateResponse.getCode();
                            if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功
                                if (activateResponse.getData() != null && activateResponse.getData().size() != 0) {
                                    if (page == 1) list.clear();
                                    list.addAll(activateResponse.getData());
                                    page++;
                                    myApprenticeAdapter.notifyDataSetChanged();
                                    goneOrShow();
                                }

                            }
                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        refresh.setRefreshing(false);
                        dismissDialog();
                    }
                });
    }

    @Override
    public void onRefresh() {//刷新
        page = 1;
        lastPage = 0;
        getData();

    }

    @Override
    public void onLoadMore() {
        if (page > lastPage) {
            lastPage = page;
            showDialog();
            getData();
        }

    }
}
