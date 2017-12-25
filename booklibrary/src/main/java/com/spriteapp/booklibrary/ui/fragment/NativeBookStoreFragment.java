package com.spriteapp.booklibrary.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.database.BookDb;
import com.spriteapp.booklibrary.enumeration.BookEnum;
import com.spriteapp.booklibrary.enumeration.UpdateNightMode;
import com.spriteapp.booklibrary.enumeration.UpdateTextStateEnum;
import com.spriteapp.booklibrary.enumeration.UpdaterPayEnum;
import com.spriteapp.booklibrary.manager.CacheManager;
import com.spriteapp.booklibrary.manager.NightModeManager;
import com.spriteapp.booklibrary.manager.StoreColorManager;
import com.spriteapp.booklibrary.model.AddBookModel;
import com.spriteapp.booklibrary.model.BookList;
import com.spriteapp.booklibrary.model.RegisterModel;
import com.spriteapp.booklibrary.model.UpdateProgressModel;
import com.spriteapp.booklibrary.model.UserModel;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.model.response.BookStoreResponse;
import com.spriteapp.booklibrary.model.response.LoginResponse;
import com.spriteapp.booklibrary.model.store.BookTypeResponse;
import com.spriteapp.booklibrary.model.store.HotSellResponse;
import com.spriteapp.booklibrary.recyclerView.adapter.MultiAdapter;
import com.spriteapp.booklibrary.recyclerView.decorate.Visitable;
import com.spriteapp.booklibrary.recyclerView.model.HotSellModel;
import com.spriteapp.booklibrary.recyclerView.model.StoreUser;
import com.spriteapp.booklibrary.ui.presenter.BookShelfPresenter;
import com.spriteapp.booklibrary.ui.view.BookShelfView;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.BookUtil;
import com.spriteapp.booklibrary.util.CollectionUtil;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.spriteapp.booklibrary.util.StringUtil;
import com.spriteapp.booklibrary.util.ToastUtil;
import com.spriteapp.booklibrary.widget.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import de.greenrobot.event.EventBus;

/**
 * Created by kuangxiaoguo on 2017/7/27.
 */

public class NativeBookStoreFragment extends BaseFragment implements BookShelfView {

    private static final String TAG = "NativeBookStoreFragment";

    private XRecyclerView mRecyclerView;
    private BookShelfPresenter mPresenter;
    private Context mContext;
    private MultiAdapter mMultiAdapter;
    private List<Visitable> mDataList;
    private BookList mMyBook;
    private HotSellModel mHotSellModel;
    private StoreUser mUserModel;
    private BookDb mBookDb;
    private List<BookDetailResponse> mBookList;
    private View mView;
    private boolean isFirstInitData;
    //热销榜 精选 连载
    private List<HotSellResponse> mClasses;
    private String mStoreJson;
    private List<BookTypeResponse> mTypeList;
    private List<BookList> mTypeBookList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView != null) {
            if (mView.getParent() != null) {
                ((ViewGroup) mView.getParent()).removeView(mView);
            }
            if (mPresenter == null) {
                mPresenter = new BookShelfPresenter();
            }
            mPresenter.attachView(this);
            EventBus.getDefault().register(this);
            RegisterModel registerModel;
            if (!AppUtil.isLogin() && (registerModel = HuaXiSDK.getInstance().getRegisterModel()) != null) {
                mPresenter.getLoginInfo(registerModel, false);
            } else {
                mPresenter.getUserInfo();
            }
            return mView;
        }
        mView = inflater.inflate(R.layout.book_reader_fragment_native_book_store, container, false);
        findViewId();
        configViews();
        initData();
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public int getLayoutResId() {
        return R.layout.book_reader_fragment_native_book_store;
    }

    @Override
    public void initData() {
        EventBus.getDefault().register(this);
        mContext = getContext();
        mPresenter = new BookShelfPresenter();
        mPresenter.attachView(this);
        mBookDb = new BookDb(mContext);
        mBookList = new ArrayList<>();
        isFirstInitData = true;
        mBookList = mBookDb.queryBookData();
        initSourceData();
        if (!AppUtil.isLogin()) {
            RegisterModel registerModel = HuaXiSDK.getInstance().getRegisterModel();
            if (registerModel != null) {
                mPresenter.getLoginInfo(registerModel, false);
                return;
            }
        }
        if (!StringUtil.isEmpty(mStoreJson)) {
            mPresenter.getUserInfo();
        }
        mPresenter.getBookShelf();
    }

    private void initSourceData() {
        mStoreJson = CacheManager.readNativeBookStore();
        if (!StringUtil.isEmpty(mStoreJson)) {
            try {
                BookStoreResponse cacheBookStore = new Gson().fromJson(mStoreJson, BookStoreResponse.class);
                if (cacheBookStore != null) {
                    mClasses = cacheBookStore.getClasses();
                    mTypeList = cacheBookStore.getLists();
                }
            } catch (Exception e) {
            }
        }
        mDataList = new ArrayList<>();
        mUserModel = new StoreUser();
        mDataList.add(mUserModel);
        mMyBook = new BookList();
        mMyBook.setMyShelf(true);
        mMyBook.setDetailResponseList(mBookList);
        mDataList.add(mMyBook);
        mHotSellModel = new HotSellModel();
        mHotSellModel.setResponseList(mClasses);
        mDataList.add(mHotSellModel);
        if (mTypeList == null) {
            mTypeList = new ArrayList<>();
        }
        mTypeBookList = new ArrayList<>();
        if (!CollectionUtil.isEmpty(mTypeList)) {
            for (BookTypeResponse response : mTypeList) {
                BookList bookList = new BookList();
                bookList.setTypeResponse(response);
                mTypeBookList.add(bookList);
                mDataList.add(bookList);
            }
        }
        if (!StringUtil.isEmpty(mStoreJson)) {
            setAdapter();
            mPresenter.getBookStoreData();
        } else {
            mRecyclerView.refresh();
        }
    }

    private void setAdapter() {
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }
        if (mMultiAdapter == null) {
            mMultiAdapter = new MultiAdapter(getContext(), mDataList);
            mRecyclerView.setAdapter(mMultiAdapter);
            return;
        }
        mMultiAdapter.notifyDataSetChanged();
    }

    public XRecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public void configViews() {
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setLoadingMoreEnabled(false);
        mRecyclerView.setPullRefreshEnabled(true);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                mPresenter.getUserInfo();
                if (CollectionUtil.isEmpty(mBookList)) {
                    mPresenter.getBookShelf();
                }
                mPresenter.getBookStoreData();
            }

            @Override
            public void onLoadMore() {
            }
        });
        StoreColorManager manager = NightModeManager.getManager();
        if (manager == null) {
            return;
        }
        mRecyclerView.setBackgroundColor(manager.getContainerBackground());
    }

    @Override
    public void findViewId() {
        mRecyclerView = (XRecyclerView) mView.findViewById(R.id.book_reader_store_recycler_view);
    }

    @Override
    protected void lazyLoad() {
    }

    @Override
    public void onError(Throwable t) {
        mRecyclerView.refreshComplete();
    }

    @Override
    public void setData(Base<List<BookDetailResponse>> result) {
        mRecyclerView.refreshComplete();

        List<BookDetailResponse> dataList = result.getData();
        if (CollectionUtil.isEmpty(dataList)) {
            if (CollectionUtil.isEmpty(mBookList) && mDataList.contains(mMyBook)) {
                mDataList.remove(mMyBook);
                setAdapter();
            }
            return;
        }
        if (mBookList == null) {
            mBookList = new ArrayList<>();
        }
        int code = result.getCode();
        BookEnum typeEnum = null;
        if (code == BookEnum.RECOMMEND_DATA.getValue()) {
            typeEnum = BookEnum.RECOMMEND_BOOK;
        } else if (code == BookEnum.MY_SHELF_DATA.getValue()) {
            typeEnum = BookEnum.MY_BOOK;
        }
        BookEnum bookEnum = BookEnum.ADD_SHELF;
        //服务器同步逻辑
        //本地无缓存
        if (CollectionUtil.isEmpty(mBookList)) {
            if (mBookList == null) {
                mBookList = new ArrayList<>();
            }
            if (typeEnum != null) {
                dataList.get(0).setIs_recommend_book(typeEnum.getValue());
            }
            mBookList.addAll(dataList);
            mBookDb.insert(mBookList, bookEnum, typeEnum);
        } else {
            //本地有缓存
            BookDetailResponse bookDetailResponse = mBookList.get(0);
            //之前缓存数据为推荐数据
            if (bookDetailResponse.getIs_recommend_book() == BookEnum.RECOMMEND_BOOK.getValue()) {
                //服务器返回为我的数据
                if (code == BookEnum.MY_SHELF_DATA.getValue()) {
                    mBookDb.deleteRecommendNotReadBook();
                    mBookList.clear();
                    mBookList.addAll(mBookDb.queryBookData());
                    mBookDb.deleteRecommendBook();
                    mBookList.addAll(dataList);
                    synchronizeLoginBook();
                } else if (code == BookEnum.RECOMMEND_DATA.getValue()) {
                    //返回为推荐数据
                    synchronizeRecommendBook(dataList);
                }
            } else {
                //之前缓存为我的书籍，但请求回来的是推荐数据，就是之前所有的书籍都没有和同步到服务器
                if (code == BookEnum.RECOMMEND_DATA.getValue()) {
                    dataList.clear();
                }
                synchronizeMyBook(dataList);
            }
        }
        if (!CollectionUtil.isEmpty(mBookList)) {
            mMyBook.setDetailResponseList(mBookList);
        }
        if (!CollectionUtil.isEmpty(mBookList) && !mDataList.contains(mMyBook)) {
            mDataList.add(1, mMyBook);
        }
        setAdapter();
    }

    /**
     * 登录后同步图书(此时mBookList为未登录前本地已读缓存书籍)
     */
    private void synchronizeLoginBook() {
        //通过set去除重复的书籍
        Set<BookDetailResponse> detailSet = new LinkedHashSet<>();
        detailSet.addAll(mBookList);
        mBookList.clear();
        mBookList.addAll(detailSet);
        if (CollectionUtil.isEmpty(mBookList)) {
            return;
        }
        mBookDb.insert(mBookList, BookEnum.ADD_SHELF, BookEnum.MY_BOOK);
        mBookList.get(0).setIs_recommend_book(BookEnum.MY_BOOK.getValue());
        synchronizeBookProgress();
    }

    private void synchronizeBookProgress() {
        if (CollectionUtil.isEmpty(mBookList)) {
            return;
        }
        BookDetailResponse bookDetailResponse = mBookList.get(0);
        //推荐数据不同步进度
        if (bookDetailResponse.getIs_recommend_book() == BookEnum.RECOMMEND_BOOK.getValue()) {
            return;
        }
        mPresenter.addOneMoreBookToShelf(BookUtil.getBookJson(mBookList));
    }

    private void synchronizeMyBook(List<BookDetailResponse> serverData) {
        //服务器比本地数据多
        if (mBookList.size() < serverData.size()) {
            List<BookDetailResponse> diffBookList = CollectionUtil.getDiffBook(mBookList, serverData);
            mBookList.addAll(diffBookList);
            mBookDb.insert(diffBookList, BookEnum.ADD_SHELF, BookEnum.MY_BOOK);
        } else if (mBookList.size() > serverData.size()) {
            //本地数据比服务器数据多
            List<BookDetailResponse> differList = new ArrayList<>();
            differList.addAll(mBookList);
            differList = CollectionUtil.getDiffBook(serverData, differList);
            mPresenter.addOneMoreBookToShelf(BookUtil.getBookJson(differList));
        }
    }

    private void synchronizeRecommendBook(List<BookDetailResponse> serverData) {
        List<BookDetailResponse> tempList = new ArrayList<>();
        List<BookDetailResponse> serverTempList = new ArrayList<>();
        tempList.addAll(mBookList);
        serverTempList.addAll(serverData);
        //获取本地与服务器差异数据
        serverTempList = CollectionUtil.getDiffBook(tempList, serverTempList);
        mBookList.addAll(serverTempList);

        if (mBookList.size() > serverData.size()) {
            tempList.clear();
            tempList.addAll(mBookList);
            //获取本地需要删除的数据
            tempList = CollectionUtil.getDiffBook(serverData, tempList);
            mBookDb.deleteBook(tempList);
            //获取最终显示的推荐数据
            mBookList = CollectionUtil.getDiffBook(tempList, mBookList);
        }
        mBookDb.insert(mBookList, BookEnum.NOT_ADD_SHELF, BookEnum.RECOMMEND_BOOK);
    }

    public void onEventMainThread(RegisterModel model) {
        if (model == null) {
            return;
        }
        mPresenter.getLoginInfo(model, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstInitData) {
            isFirstInitData = false;
            return;
        }
        queryBookData();
    }

    private void queryBookData() {
        List<BookDetailResponse> dataList = mBookDb.queryBookData();
        if (mBookList == null) {
            mBookList = new ArrayList<>();
        }
        mBookList.clear();
        mBookList.addAll(dataList);
        mMyBook.setDetailResponseList(mBookList);
        if (CollectionUtil.isEmpty(mBookList) && mDataList.contains(mMyBook)) {
            mDataList.remove(mMyBook);
        }
        if (CollectionUtil.isEmpty(mBookList) && mPresenter != null) {
            mPresenter.getBookShelf();
        }
        if (!CollectionUtil.isEmpty(mBookList) && !mDataList.contains(mMyBook)) {
            mDataList.add(1, mMyBook);
        }
        setAdapter();
    }

    public void onEventMainThread(UpdateNightMode updateNightMode) {
        StoreColorManager manager = NightModeManager.getManager();
        if (manager == null) {
            return;
        }
        mRecyclerView.setBackgroundColor(manager.getContainerBackground());
        if (mMultiAdapter == null) {
            return;
        }
        mMultiAdapter.notifyDataSetChanged();
    }

    public void onEventMainThread(AddBookModel model) {
        //已经初始化BookShelfFragment，逻辑让BookShelfFragment处理
        if (SharedPreferencesUtil.getInstance().getBoolean(Constant.HAS_INIT_BOOK_SHELF)) {
            return;
        }
        if (!AppUtil.isLogin()) {
            HuaXiSDK.getInstance().toLoginPage(mContext);
            return;
        }
        int mAddBookId = model.getBookId();
        BookDetailResponse bookDetail = mBookDb.queryBook(mAddBookId);
        if (BookUtil.isBookAddShelf(bookDetail)) {
            mBookDb.deleteRecommendBook();
            List<BookDetailResponse> bookDetailList = mBookDb.queryBookData();
            mBookList.clear();
            mBookList.addAll(bookDetailList);
            mMyBook.setDetailResponseList(mBookList);
            setAdapter();
            Log.d("addBook","添加书架中2");
            ToastUtil.showSingleToast("书架中已存在");
            return;
        }
        if (bookDetail != null &&
                bookDetail.getIs_recommend_book() == BookEnum.RECOMMEND_BOOK.getValue()) {
            mBookDb.updateRecommendTag(mAddBookId, BookEnum.MY_BOOK.getValue());
        }
        mPresenter.addToShelf(mAddBookId, "add", bookDetail == null ? 0 : bookDetail.getChapter_id());
        if (bookDetail != null) {
            mBookDb.deleteRecommendBook();
            ToastUtil.showSingleToast("加入书架成功");
            mBookDb.updateAddShelfTag(mAddBookId, BookEnum.ADD_SHELF);
            return;
        }
        mPresenter.getBookDetail(mAddBookId);
    }

    public void onEventMainThread(UpdaterPayEnum updateEnum) {
        mPresenter.getUserInfo();
        if (updateEnum == UpdaterPayEnum.UPDATE_LOGIN_INFO
                || updateEnum == UpdaterPayEnum.UPDATE_LOGIN_OUT) {
            if (updateEnum == UpdaterPayEnum.UPDATE_LOGIN_OUT && mBookList != null) {
                mBookList.clear();
            }
            mPresenter.getBookShelf();
        }
    }

    public void onEventMainThread(UpdateTextStateEnum updateTextStateEnum) {
        if (mMultiAdapter == null) {
            return;
        }
        mMultiAdapter.notifyDataSetChanged();
    }

    public void onEventMainThread(UpdateProgressModel model) {
        if (SharedPreferencesUtil.getInstance().getBoolean(Constant.HAS_INIT_BOOK_SHELF)) {
            return;
        }
        int bookId = model.getBookId();
        int chapterId = model.getChapterId();
        BookDetailResponse bookDetail = mBookDb.queryBook(bookId);
        if (bookDetail == null) {
            return;
        }
        bookDetail.setChapter_id(chapterId);
        bookDetail.setBook_chapter_total(model.getChapterTotal());
        bookDetail.setLast_chapter_index(model.getChapterIndex());
        mBookDb.updateReadProgress(bookDetail);
    }

    @Override
    public void showNetWorkProgress() {
    }

    @Override
    public void disMissProgress() {
    }

    @Override
    public Context getMyContext() {
        return mContext;
    }

    @Override
    public void setLoginInfo(LoginResponse response) {
        EventBus.getDefault().post(UpdaterPayEnum.UPDATE_LOGIN_INFO);
    }

    @Override
    public void setDeleteBookResponse() {
    }

    @Override
    public void setAddShelfResponse() {
    }

    @Override
    public void setBookDetail(BookDetailResponse data) {
        if (data != null) {
            ToastUtil.showSingleToast("加入书架成功");
            mBookDb.insert(data, BookEnum.ADD_SHELF);
            mBookDb.deleteRecommendBook();
        }
    }

    @Override
    public void setBookStoreData(BookStoreResponse storeData) {
        mRecyclerView.refreshComplete();
        if (storeData == null) {
            return;
        }
        mClasses = storeData.getClasses();
        mHotSellModel.setResponseList(mClasses);

        List<BookTypeResponse> typeList = storeData.getLists();
        if (CollectionUtil.isEmpty(typeList)) {
            return;
        }
        if (mTypeList == null) {
            mTypeList = new ArrayList<>();
        }
        mTypeList.clear();
        mTypeList.addAll(typeList);

        if (!CollectionUtil.isEmpty(mTypeBookList)) {
            for (BookList bookList : mTypeBookList) {
                if (mDataList.contains(bookList)) {
                    mDataList.remove(bookList);
                }
            }
        }
        if (mTypeBookList == null) {
            mTypeBookList = new ArrayList<>();
        }
        mTypeBookList.clear();
        for (BookTypeResponse response : mTypeList) {
            BookList bookList = new BookList();
            bookList.setTypeResponse(response);
            mTypeBookList.add(bookList);
            mDataList.add(bookList);
        }
        setAdapter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    public void setUserInfo(UserModel userInfo) {
        mUserModel.setUserModel(userInfo);
        setAdapter();
    }
}
