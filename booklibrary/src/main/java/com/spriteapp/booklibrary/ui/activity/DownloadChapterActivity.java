package com.spriteapp.booklibrary.ui.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.database.ChapterDb;
import com.spriteapp.booklibrary.database.ContentDb;
import com.spriteapp.booklibrary.enumeration.ChapterEnum;
import com.spriteapp.booklibrary.model.UserBean;
import com.spriteapp.booklibrary.model.response.BookChapterResponse;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.model.response.GroupChapter;
import com.spriteapp.booklibrary.model.response.SubscriberContent;
import com.spriteapp.booklibrary.ui.adapter.second.DownLoadFirstAdapter;
import com.spriteapp.booklibrary.ui.adapter.second.DownLoadFirstAdapter.OnSelectList;
import com.spriteapp.booklibrary.ui.presenter.SubscriberContentPresenter;
import com.spriteapp.booklibrary.ui.view.SubscriberContentView;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.CXAESUtil;
import com.spriteapp.booklibrary.util.CollectionUtil;
import com.spriteapp.booklibrary.util.PreferenceHelper;
import com.spriteapp.booklibrary.util.ToastUtil;
import com.spriteapp.booklibrary.util.Util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 下载章节内容
 * https://s.hxdrive.net/book_content?&book_id=692&chapter_id=252674&auto_sub=0
 */
public class DownloadChapterActivity extends TitleActivity implements SubscriberContentView, OnSelectList {
    private final int ITEM_NUM = 20;
    private SubscriberContentPresenter contentPresenter;
    private ChapterDb mChapterDb = new ChapterDb(this);
    private ContentDb contentDb = new ContentDb(this);
    private List<GroupChapter> groupChapters = new ArrayList<>();
    private List<BookChapterResponse> selectChapter = new ArrayList<>();
    private List<BookChapterResponse> mChapterList;
    private RecyclerView downloadRecycleView;
    private DownLoadFirstAdapter adapter;

    private TextView downLoad_chapter;
    private TextView rightText;
    private TextView chapter_price;
    private TextView check_chapter;
    private TextView my_balance;
    private TextView tv_hint;
    private boolean all_select;
    private int book_id = 0;
    private int balance;
    private int total_price;
    int total_size = 0;

    @Override
    public void initData() throws Exception {
        book_id = getIntent().getIntExtra(ActivityUtil.BOOK_ID, 0);
        contentPresenter = new SubscriberContentPresenter();
        contentPresenter.attachView(this);
        if (!AppUtil.isLogin(this)) {
            return;
        } else {
            balance = UserBean.getInstance().getUser_real_point() + UserBean.getInstance().getUser_false_point();
            my_balance.setText("（余额：" + balance + "花贝/花瓣)");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppUtil.isLogin()) {
            balance = UserBean.getInstance().getUser_real_point() + UserBean.getInstance().getUser_false_point();
            my_balance.setText("（余额：" + balance + "花贝/花瓣)");
        }
    }

    @Override
    public void configViews() throws Exception {
        super.configViews();
        String content = "<font color=\"#aeacbd\">花贝为花溪小说网消费虚拟币，花瓣为花溪小说网赠送用户之体验币，" +
                "仅限于阅读消费当用作阅读时，</font><font color=\"#ff7050\">" + "1花瓣=1花贝，1元=100花贝" + "</font>";
        tv_hint.setText(Html.fromHtml(content));
        refreshUi(0, 0);
        contentPresenter.getChapter(book_id);
        setTitle("批量下载");
    }

    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_download_chapter, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
        rightText = new TextView(this);
        rightText.setText("全选");
        mRightLayout.addView(rightText);

        adapter = new DownLoadFirstAdapter(this, this);
        downloadRecycleView = (RecyclerView) findViewById(R.id.downloadRecycleView);
        downloadRecycleView.setAdapter(adapter);
        downLoad_chapter = (TextView) findViewById(R.id.downLoad_chapter);
        chapter_price = (TextView) findViewById(R.id.chapter_price);
        my_balance = (TextView) findViewById(R.id.my_balance);
        check_chapter = (TextView) findViewById(R.id.check_chapter);
        tv_hint = (TextView) findViewById(R.id.tv_hint);
        mRightLayout.setOnClickListener(this);
        downLoad_chapter.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (i == R.id.book_reader_right_layout) {//全选、反选
            selectChapter.clear();
            int total_price = 0;
            int group_price = 0;
            all_select = !all_select;
            rightText.setText(!all_select ? "全选" : "取消全选");
            for (GroupChapter g : groupChapters) {
                group_price = 0;
                g.setIs_check(all_select);
                for (BookChapterResponse c : g.getmChapterList()) {
                    c.setIs_check(all_select);
                    if (all_select) {
                        if (!c.getIs_download()) {
                            selectChapter.add(c);
                            total_price += c.getChapter_price();
                            group_price += c.getChapter_price();
                        }
                    } else {
                        total_price = 0;
                        group_price = 0;
                        selectChapter.remove(c);
                    }
                }
                g.setPrice(group_price);
            }
            refreshUi(selectChapter.size(), total_price);
            this.total_price = total_price;
            adapter.notifyNewData(groupChapters);
            adapter.allSelectListAndPrice(selectChapter, total_price);
        } else if (i == R.id.downLoad_chapter) {//下载
            if (!AppUtil.isLogin(this)) {
                return;
            }
            if (balance < total_price) {//余额不足，去充值
                ToastUtil.showSingleToast("余额不足，请先充值");
                ActivityUtil.toRechargeActivity(this);
                return;
            }
            if (selectChapter != null && selectChapter.size() > 0) {
                showDialog();
                loading = 0;
                downLoad_chapter.setText("下载中0%");
                downLoad_chapter.setEnabled(false);
                contentPresenter.getContent(book_id, selectChapter.get(loading).getChapter_id(), 1);
            }
        }

    }

    @Override
    public void onError(Throwable t) {
        ToastUtil.showSingleToast("连接出错，请检查网络！");
        adapter.notifyDataSetChanged();
    }

    /**
     * 下载阅读内容存储
     *
     * @param result
     */
    int loading = 0;

    @Override
    public void setData(Base<SubscriberContent> result) {
        try {
            String message = result.getMessage();
            SubscriberContent data = result.getData();
            if (data == null) {
                ToastUtil.showSingleToast(message);
                return;
            }
            if (data.getUsed_false_point() == 0 && data.getUsed_real_point() == 0) {//不需要购买不扣钱
                total_price -= data.getChapter_price();
            }

            String k = data.getChapter_content_key();
            String c1 = data.chapter_content;
            String c2 = CXAESUtil.encrypt("" + PreferenceHelper.getLong(PreferenceHelper.AES_KEY, 0l), c1);
            data.setChapter_content(c2);
            data.setIsAES(1);
            mChapterDb.updateDownLoadState(book_id, data.getChapter_id());
            if (contentDb.queryContent(book_id, data.getChapter_id()) != null) {
                contentDb.update(book_id, data.getChapter_id(), data);
            } else {
                contentDb.insert(data);
            }

            loading++;
            DecimalFormat df = new DecimalFormat("0.00");//格式化小数
            String pregress = df.format((float) loading / selectChapter.size() * 100);
            downLoad_chapter.setText("下载中" + pregress + "%");

            if (loading == selectChapter.size()) {
                ToastUtil.showSingleToast("下载完成！");
                for (BookChapterResponse bean : selectChapter) {
                    bean.setIs_download(1);
                }
                adapter.notifyDownLoadStatus();
                selectChapter.clear();
                balance -= total_price;
                total_price = 0;
                refreshUi(0, 0);
                Util.getUserInfo();
            } else {
                //下完一章接着下一章
                contentPresenter.getContent(book_id, selectChapter.get(loading).getChapter_id(), 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        return this;
    }

    /**
     * 章节列表
     *
     * @param catalogList
     */
    @Override
    public void setChapter(List<BookChapterResponse> catalogList) {
        if (CollectionUtil.isEmpty(catalogList)) {
            ToastUtil.showSingleToast("章节列表为空" + book_id);
            return;
        }
        total_size = catalogList.size();
        mChapterList = new ArrayList<>();
        mChapterList.addAll(catalogList);
        GroupChapter groupChapter;


        List<BookChapterResponse> chapterResponses = mChapterDb.queryCatalog(book_id);
        for (BookChapterResponse c : chapterResponses) {
            for (BookChapterResponse ch : mChapterList) {
                if (ch.getChapter_id() == c.getChapter_id() && c.getIs_download()) {
                    ch.setIs_download(1);
                    total_size--;
                }
            }
        }

        //分第一组--免费章节
        int first_item_num = 0;
        int size = mChapterList.size();
        for (int i = 0; i < size; i++) {
            if (ChapterEnum.CHAPTER_IS_VIP.getCode()
                    == mChapterList.get(i).getChapter_is_vip()) {
                first_item_num = i;
                Log.e("setChapter" + size, mChapterList.get(i).getChapter_price() + "------开始收费----" + i);
                break;
            }
        }
        if (first_item_num < size) {
            groupChapter = new GroupChapter();
            groupChapter.setIs_free(0);
            groupChapter.setStart_chapter(1);
            groupChapter.setEnd_chapter(first_item_num);
            groupChapter.setPrice(0);
            groupChapter.setmChapterList(mChapterList.subList(0, first_item_num));
            groupChapters.add(groupChapter);
            Log.e("setChapter" + size, 0 + "------第1组----" + first_item_num);
        } else {
            first_item_num = 0;
        }

        //第二组及以后
        int group_num = (size - first_item_num) / ITEM_NUM;
        if ((size - first_item_num) % ITEM_NUM != 0) {
            group_num += 1;
        }

        for (int i = 0; i < group_num; i++) {
            int s = i * ITEM_NUM;
            int e = (i + 1) * ITEM_NUM;
            if (e > (size - first_item_num))
                e = (size - first_item_num);

            groupChapter = new GroupChapter();
            groupChapter.setIs_free(1);
            groupChapter.setStart_chapter(s + first_item_num + 1);
            groupChapter.setEnd_chapter(e + first_item_num);
            groupChapter.setPrice(0);
            groupChapter.setmChapterList(mChapterList.subList(s + first_item_num, e + first_item_num));
            groupChapters.add(groupChapter);

            Log.e("setChapter" + size, (s + first_item_num) + "-------第" + (i + 2) + "组----" + (e + first_item_num));
        }

        adapter.setData(groupChapters);
    }

    @Override
    public void toChannelLogin() {

    }

    @Override
    public void setBookDetail(BookDetailResponse data) {

    }

    @Override
    public void onSelect(List<BookChapterResponse> mChapterList, int price) {
        total_price = price;
        selectChapter.clear();
        selectChapter.addAll(mChapterList);
        refreshUi(selectChapter.size(), price);
    }

    public void refreshUi(int select, int price) {
        int select_is_buy = select;
        for (BookChapterResponse chapter : selectChapter) {
            if (ChapterEnum.CHAPTER_IS_VIP.getCode()
                    != chapter.getChapter_is_vip()) {
                select_is_buy--;
            }
        }
        check_chapter.setText(Html.fromHtml("已选" + select + "章，需要付费章节<font color=\"#7d50ff\">" + select_is_buy + "</font>章"));
        chapter_price.setText(price + "花贝/花瓣");
        if (select > 0) {
            all_select = (select == total_size);
            rightText.setText(!all_select ? "全选" : "取消全选");
        }

        if (selectChapter != null && selectChapter.size() > 0) {
            downLoad_chapter.setEnabled(true);
            downLoad_chapter.setText(price > 0 ? "购买并下载" : "下载");
        } else {
            downLoad_chapter.setText("请选择章节");
            downLoad_chapter.setEnabled(false);
        }
        my_balance.setText("（余额：" + balance + "花贝/花瓣)");

    }
}
