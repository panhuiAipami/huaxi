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

public class ApprenticeFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private View mView;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refresh;
    private RelativeLayout null_layout;
    private TextView front_hint, behind_hint;
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
        recyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);
        refresh = (SwipeRefreshLayout) mView.findViewById(R.id.refresh);
        null_layout = (RelativeLayout) mView.findViewById(R.id.null_layout);
        front_hint = (TextView) mView.findViewById(R.id.front_hint);
        behind_hint = (TextView) mView.findViewById(R.id.behind_hint);
        refresh.setColorSchemeResources(R.color.square_comment_selector);
        Bundle bundle = getArguments();
        list_type = bundle.getInt(WITHDRAWALS_TYPE, 0);
        if (list_type == 0) {
            front_hint.setText(R.string.front2_text);
        } else if (list_type == 1) {
            front_hint.setText(R.string.front_text);
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
            bookDetailResponse.setBook_name("期待");
            bookDetailResponse.setBook_intro("花都收徒");
            bookDetailResponse.setBook_image("http://img.zcool.cn/community/0142135541fe180000019ae9b8cf86.jpg@1280w_1l_2o_100sh.png");
            bookDetailResponse.setBook_share_url("http://baidu.com");
            HuaXiSDK.getInstance().showShareDialog(getActivity(), bookDetailResponse, true, 2);
        }

    }

    private void goneOrShow() {
        if (list.size() == 0) {
            null_layout.setVisibility(View.VISIBLE);
        } else {
            null_layout.setVisibility(View.GONE);
        }

    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void onRefresh() {//刷新
        refresh.setRefreshing(false);
    }


    public void getData() {
        if (list_type == 0) {//我的徒弟
            getApprenticeList();
        } else if (list_type == 1) {//唤醒徒弟
            getActivateApprenticeList();
        }
    }

    public void getApprenticeList() {
        if (!NetworkUtil.isAvailable(getActivity())) return;
        showDialog();
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
                                if (myApprenticeResponse.getData() != null && myApprenticeResponse.getData().getPupil_data() != null && myApprenticeResponse.getData().getPupil_data().size() != 0) {
                                    if (page == 1) list.clear();
                                    list.addAll(myApprenticeResponse.getData().getPupil_data());
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
                        dismissDialog();
                    }
                });
    }

    public void getActivateApprenticeList() {
        if (!NetworkUtil.isAvailable(getActivity())) return;
        showDialog();
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
                        dismissDialog();
                    }
                });
    }


}
