package com.spriteapp.booklibrary.ui.fragment;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseFragment;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.database.BookDb;
import com.spriteapp.booklibrary.database.ChapterDb;
import com.spriteapp.booklibrary.database.ContentDb;
import com.spriteapp.booklibrary.enumeration.BookEnum;
import com.spriteapp.booklibrary.enumeration.UpdaterPayEnum;
import com.spriteapp.booklibrary.enumeration.UpdaterShelfEnum;
import com.spriteapp.booklibrary.listener.DelBookShelf;
import com.spriteapp.booklibrary.listener.DeleteBookListener;
import com.spriteapp.booklibrary.listener.ListenerManager;
import com.spriteapp.booklibrary.model.AddBookModel;
import com.spriteapp.booklibrary.model.RegisterModel;
import com.spriteapp.booklibrary.model.UpdateProgressModel;
import com.spriteapp.booklibrary.model.UserModel;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.model.response.BookStoreResponse;
import com.spriteapp.booklibrary.model.response.LoginResponse;
import com.spriteapp.booklibrary.ui.activity.HomeActivity;
import com.spriteapp.booklibrary.ui.activity.ReadActivity;
import com.spriteapp.booklibrary.ui.adapter.BookShelfAdapter;
import com.spriteapp.booklibrary.ui.presenter.BookShelfPresenter;
import com.spriteapp.booklibrary.ui.view.BookShelfView;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.BookUtil;
import com.spriteapp.booklibrary.util.CollectionUtil;
import com.spriteapp.booklibrary.util.DialogUtil;
import com.spriteapp.booklibrary.util.ScreenUtil;
import com.spriteapp.booklibrary.util.SharedPreferencesUtil;
import com.spriteapp.booklibrary.util.ToastUtil;
import com.spriteapp.booklibrary.widget.recyclerview.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.greenrobot.event.EventBus;

import static com.spriteapp.booklibrary.ui.activity.HomeActivity.BOOKSHELF_TO_BOOKSTORE;

/**
 * Created by kuangxiaoguo on 2017/7/7.
 */

public class BookshelfFragment extends BaseFragment implements BookShelfView, DelBookShelf {

    private static final String TAG = "BookshelfFragment";
    private static final int SHELF_SPAN_COUNT = 3;
    private static final int HORIZONTAL_SPACE = 2;
    private static final int VERTICAL_SPACE = 5;
    private BookDb mBookDb;
    private ContentDb mContentDb;
    private BookShelfPresenter mPresenter;
    private List<BookDetailResponse> mBookList;
    private List<BookDetailResponse> mFlagList;
    private RecyclerView mRecyclerView;
    private BookShelfAdapter mAdapter;
    private int mDeleteBookId;
    private AlertDialog mDeleteBookDialog;
    private ChapterDb mChapterDb;
    private boolean isRecommendData;
    private int mDeletePosition;
    private LinearLayout del_layout;
    private TextView is_del;
    private Map<Integer, Integer> del_book = new HashMap<>();
    private int last_num = 0;
    private List<Integer> bookIds = new ArrayList<>();
    private List<Integer> indexs = new ArrayList<>();
    public static int sort_type = 0;
    private LinearLayout null_layout;
    private TextView miaoshu;

    @Override
    public int getLayoutResId() {
        return R.layout.book_reader_fragment_bookshelf;
    }


    @Override
    public void initData() {
        try {
            SharedPreferencesUtil.getInstance().putBoolean(Constant.HAS_INIT_BOOK_SHELF, true);
            EventBus.getDefault().register(this);
            mBookDb = new BookDb(getContext());
            mContentDb = new ContentDb(getMyContext());
            mBookList = new ArrayList<>();
            mBookList = mBookDb.queryBookData();
//        Collections.reverse(mBookList);//倒叙
            mChapterDb = new ChapterDb(getMyContext());
            mPresenter = new BookShelfPresenter();
            DialogUtil.setDialogListener(mDeleteListener);
            mPresenter.attachView(this);
            initRecyclerView();
            synchronizeBookProgress();
            if (!AppUtil.isLogin()) {
                RegisterModel registerModel = HuaXiSDK.getInstance().getRegisterModel();
                if (registerModel != null) {
                    mPresenter.getLoginInfo(registerModel);
                    return;
                }
            }
            mPresenter.getBookShelf();
            ListenerManager.getInstance().setDelBookShelf(this);
            showOrGone();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 9, LinearLayoutManager.VERTICAL, false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0 && SharedPreferencesUtil.getInstance().getBoolean(ReadActivity.LAST_CHAPTER, false))
                    return 9;
                else
                    return 3;

            }
        });
        mRecyclerView.setLayoutManager(layoutManager);
//        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), SHELF_SPAN_COUNT,
//                LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(ScreenUtil.dpToPxInt(HORIZONTAL_SPACE),
                ScreenUtil.dpToPxInt(VERTICAL_SPACE)));
        setAdapter();
    }

    private void setAdapter() {
        if (mBookList == null) {
            mBookList = new ArrayList<>();
        }
        if (mAdapter == null) {
            mAdapter = new BookShelfAdapter(getContext(), mBookList,
                    SHELF_SPAN_COUNT, HORIZONTAL_SPACE, false, del_layout, is_del);
            mAdapter.setIsRecommendData(isRecommendData);
            Log.d("IsLong", "isRecommendData===" + isRecommendData);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setDeleteBook(false);
            mAdapter.setDeleteListener(mDeleteListener);
            return;
        }
        mAdapter.setDeleteBook(false);
        mAdapter.setIsRecommendData(isRecommendData);
        Log.d("IsLong", "isRecommendData===" + isRecommendData);
        mAdapter.notifyDataSetChanged();
        showOrGone();
    }

    public boolean isDeleteBook() {
        return mAdapter != null && mAdapter.isDeleteBook();
    }

    public void setDeleteBook() {
        if (mAdapter == null) {
            return;
        }
        mAdapter.setDeleteBook(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void configViews() {
    }

    @Override
    public void findViewId() {
        mRecyclerView = (RecyclerView) mParentView.findViewById(R.id.book_reader_recycler_view);
        del_layout = (LinearLayout) mParentView.findViewById(R.id.del_layout);
        null_layout = (LinearLayout) mParentView.findViewById(R.id.null_layout);
        miaoshu = (TextView) mParentView.findViewById(R.id.miaoshu);
        miaoshu.setText("一本书也没有,去书城看看吧～");
        is_del = (TextView) mParentView.findViewById(R.id.is_del);
        is_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//确认删除
                StringBuilder builder = new StringBuilder();
                if (del_book.size() == 0) return;
                // 获取所有键值对对象的集合
                Set<Map.Entry<Integer, Integer>> set = del_book.entrySet();
                // 遍历键值对对象的集合，得到每一个键值对对象
                for (Map.Entry<Integer, Integer> me : set) {
                    // 根据键值对对象获取键和值
                    bookIds.add(me.getKey());
                    indexs.add(me.getValue());
                    builder.append(me.getKey() + ",");
                }
                mPresenter.deleteBook(builder.toString());
            }
        });
        null_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof HomeActivity) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.setSelectView(BOOKSHELF_TO_BOOKSTORE);
                }
            }
        });
    }

    @Override
    protected void lazyLoad() {
        if (mHasLoadedOnce || !isPrepared)
            return;
        mHasLoadedOnce = true;
        initData();
    }

    @Override
    public void onError(Throwable t) {
    }

    @Override
    public void setData(Base<List<BookDetailResponse>> result) {
        List<BookDetailResponse> dataList = result.getData();
        if (CollectionUtil.isEmpty(dataList)) {
            return;
        }
        if (mBookList == null) {
            mBookList = new ArrayList<>();
        }
        int code = result.getCode();
        BookEnum typeEnum = null;
        if (code == BookEnum.RECOMMEND_DATA.getValue()) {
            typeEnum = BookEnum.RECOMMEND_BOOK;
            isRecommendData = true;
        } else if (code == BookEnum.MY_SHELF_DATA.getValue()) {
            typeEnum = BookEnum.MY_BOOK;
            isRecommendData = false;
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
        setAdapter();
    }

    private DeleteBookListener mDeleteListener = new DeleteBookListener() {
        @Override
        public void deleteBook() {
            mPresenter.deleteBook(mDeleteBookId);
        }

        @Override
        public void showDeleteDialog(int bookId, int position) {
            mDeletePosition = position;
            mDeleteBookId = bookId;
            if (mDeleteBookDialog == null) {
                mDeleteBookDialog = DialogUtil.getDeleteBookDialog(mContext);
            }
            mDeleteBookDialog.show();
        }
    };

    @Override
    public void showNetWorkProgress() {
        showDialog();
    }

    @Override
    public void disMissProgress() {
        dismissDialog();
    }

    @Override
    public Context getMyContext() {
        return getContext();
    }

    @Override
    public void setLoginInfo(LoginResponse response) {
        ToastUtil.showSingleToast("登录成功");
        mPresenter.getBookShelf();
//        getUserInfo();
        EventBus.getDefault().post(UpdaterPayEnum.UPDATE_LOGIN_INFO);
        if (ListenerManager.getInstance().getLoginSuccess() != null) {
            ListenerManager.getInstance().getLoginSuccess().loginState(1);
        }
    }

    public void getUserInfo() {
//        Util.getUserInfo();
    }


    @Override
    public void setDeleteBookResponse() {
        try {
            if (del_book.size() == 0) return;
            List<Integer> pos = new ArrayList<>();

            // 获取所有键值对对象的集合
            Set<Map.Entry<Integer, Integer>> set = del_book.entrySet();
            // 遍历键值对对象的集合，得到每一个键值对对象
            for (Map.Entry<Integer, Integer> me : set) {
                // 根据键值对对象获取键和值
                int key = me.getKey();
                int value = me.getValue();
                pos.add(key);
                if (CollectionUtil.isEmpty(mBookList)) {
                    return;
                }
                Log.d("Entry", "key===" + key + "---" + "value===" + value);
//                System.out.println(key + "---" + value);
                mBookDb.deleteBook(key);
                mChapterDb.deleteChapter(key);
                mContentDb.deleteContent(key);
//                mAdapter.notifyItemRemoved(value);
//                SettingManager.getInstance().removeReadProgress(String.valueOf(value));
            }
            Iterator<BookDetailResponse> list = mBookList.iterator();
            while (list.hasNext()) {
                int book_id = list.next().getBook_id();
                for (int i = 0; i < pos.size(); i++) {
                    if (book_id == pos.get(i)) {
                        list.remove();
                    }
                }
            }
//            mBookList = list;
//        mAdapter.notifyDataSetChanged();
//        del_layout.setVisibility(View.GONE);
            mAdapter.setNum(1);
            del_layout.setVisibility(View.GONE);
            ListenerManager.getInstance().getDelBookShelf().del_book(0, 0, 0, 0);
            if (CollectionUtil.isEmpty(mBookList)) {
                Log.d("UPDATE_SHELF", "更新书架");
                onEventMainThread(UpdaterShelfEnum.UPDATE_SHELF);
            }
            showOrGone();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
            onEventMainThread(UpdaterShelfEnum.UPDATE_SHELF);
        }
    }

    @Override
    public void setBookStoreData(BookStoreResponse storeData) {
    }

    @Override
    public void setUserInfo(UserModel userInfo) {
    }

    public void onEventMainThread(UpdaterPayEnum updaterPayEnum) {
        if (updaterPayEnum == UpdaterPayEnum.UPDATE_LOGIN_OUT) {
            mBookList.clear();
            mPresenter.getBookShelf();
        }
    }

    public void onEventMainThread(UpdaterShelfEnum updateEnum) {
        List<BookDetailResponse> dataList = mBookDb.queryBookData();
        if (CollectionUtil.isEmpty(dataList)) {
            mPresenter.getBookShelf();
            return;
        }
        if (mBookList == null) {
            mBookList = new ArrayList<>();
        }
        BookDetailResponse bookDetailResponse = dataList.get(0);
        isRecommendData = bookDetailResponse.getIs_recommend_book()
                == BookEnum.RECOMMEND_BOOK.getValue();
        mBookList.clear();
        mBookList.addAll(dataList);
        setAdapter();
        showOrGone();
    }

    public void onEventMainThread(RegisterModel model) {
        if (model == null) {
            return;
        }
        mPresenter.getLoginInfo(model);
    }

    /**
     * 阅读页加入书架
     *
     * @param model
     */
    public void onEventMainThread(AddBookModel model) {
        if (!AppUtil.isLogin()) {
            HuaXiSDK.getInstance().toLoginPage(mContext);
            return;
        }
        int mAddBookId = model.getBookId();
        BookDetailResponse bookDetail = mBookDb.queryBook(mAddBookId);
        if (BookUtil.isBookAddShelf(bookDetail)) {
            mBookDb.deleteRecommendBook();
            mAdapter.setDeleteBook(false);
            List<BookDetailResponse> bookDetailList = mBookDb.queryBookData();
            mBookList.clear();
            mBookList.addAll(bookDetailList);
            mAdapter.notifyDataSetChanged();
            Log.d("addBook", "添加书架中1");
            if (model != null && model.isAddShelf()) {//添加书架

            } else {
                if (model.isClean()) {//移除书架
                    mPresenter.deleteBook(mAddBookId);
                } else {
                    ToastUtil.showSingleToast("书架中已存在");
                }
                return;
            }

        }
        if (bookDetail != null &&
                bookDetail.getIs_recommend_book() == BookEnum.RECOMMEND_BOOK.getValue()) {
            mBookDb.updateRecommendTag(mAddBookId, BookEnum.MY_BOOK.getValue());
        }
        mAdapter.setDeleteBook(false);
        mPresenter.addToShelf(mAddBookId, "add", bookDetail == null ? 0 : bookDetail.getChapter_id());
        if (bookDetail != null) {
            mBookDb.deleteRecommendBook();
            if (!model.isAddShelf()) {
                ToastUtil.showSingleToast("加入书架成功");
            }
            mBookDb.updateAddShelfTag(mAddBookId, BookEnum.ADD_SHELF);
            onEventMainThread(UpdaterShelfEnum.UPDATE_SHELF);
            return;
        }
        mPresenter.getBookDetail(mAddBookId);
    }

    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            EventBus.getDefault().post(UpdaterShelfEnum.UPDATE_SHELF);
        }
    };

    public void onEventMainThread(UpdateProgressModel model) {
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
        mHandler.sendEmptyMessageDelayed(0, 1000);
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

    private void synchronizeMyBook(List<BookDetailResponse> serverData) {
        //服务器比本地数据多
        if (mBookList.size() < serverData.size()) {
            Log.d("messageOne", "服务器比本地数据多");
            List<BookDetailResponse> diffBookList = CollectionUtil.getDiffBook(mBookList, serverData);
            mBookList.addAll(diffBookList);
            mBookDb.insert(diffBookList, BookEnum.ADD_SHELF, BookEnum.MY_BOOK);
        } else if (mBookList.size() > serverData.size()) {
            //本地数据比服务器数据多
            List<BookDetailResponse> differList = new ArrayList<>();
            differList.addAll(mBookList);
            Log.d("messageOne", "本地数据比服务器数据多");
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

    private void synchronizeBookProgress() {
        if (CollectionUtil.isEmpty(mBookList)) {
            return;
        }
        BookDetailResponse bookDetailResponse = mBookList.get(0);
        //推荐数据不同步进度
        if (bookDetailResponse.getIs_recommend_book() == BookEnum.RECOMMEND_BOOK.getValue()) {
            return;
        }
        Log.d("messageOne", "添加书籍");
        Log.d("messageOne", BookUtil.getBookJson(mBookList));
        mPresenter.addOneMoreBookToShelf(BookUtil.getBookJson(mBookList));
    }

    @Override
    public void onStop() {
        super.onStop();
//        synchronizeBookProgress();
    }

    public TextView getIs_del() {
        return is_del;
    }

    public LinearLayout getDel_layout() {
        return del_layout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        SharedPreferencesUtil.getInstance().putBoolean(Constant.HAS_INIT_BOOK_SHELF, false);
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    public void del_book(int book_id, int pos, int num, int act) {
        if (book_id == 0)
            goneFinish();
        else {
            Log.d("del_book", "book_id===" + book_id + "act===" + act);
            if (book_id == 1 && (act == 3 || act == 4 || act == 5)) {
                showFinish(act);
                return;
            } else {
                showFinish(act);
            }
        }


        if (num <= 0) {
            del_book.clear();
            is_del.setEnabled(false);
            is_del.setText("请选择要删除的书籍");
        } else {
            if (act == 1)
                del_book.put(book_id, pos);
            else if (act == 2)
                del_book.remove(book_id);
            is_del.setEnabled(true);
            is_del.setText("确认删除(" + num + ")");
            Log.d("del_book", "del_book===size===" + del_book.size());
        }
    }

    /**
     * @param type 1为完成编辑,2为全选,3为取消全选
     */
    public void setFinish(int type) {
        if (mAdapter != null) {
            mAdapter.setNum(type);
//            if (type == 1) {
//                mAdapter.setNum(type);
//            } else if (type == 2) {
//
//            } else if (type == 3) {
//
//            }
        }

    }

    public void showFinish(int type) {
        if (getActivity() instanceof HomeActivity) {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.showLeftView(type);
        }
    }

    public void goneFinish() {
        if (getActivity() instanceof HomeActivity) {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.goneLeftView();
        }
    }

    public void refreshSort() {
        if (mBookDb != null && mBookList != null && mAdapter != null) {
            List<BookDetailResponse> temp = new ArrayList<>();
            temp = mBookDb.queryBookData();
            if (temp != null && temp.size() != 0) {
                mBookList.clear();
                mBookList.addAll(temp);
                mAdapter.notifyDataSetChanged();
            }
        }

    }

    public void showOrGone() {
        if (mBookList != null && mBookList.size() != 0) {
            null_layout.setVisibility(View.GONE);
        } else {
            null_layout.setVisibility(View.VISIBLE);
        }
    }

    public int getIs_del1() {
        if (mAdapter != null && mAdapter.isDeleteBook()) {
            if (mAdapter.getNum()) return 1;
            return 2;
        } else
            return 0;
    }
}
