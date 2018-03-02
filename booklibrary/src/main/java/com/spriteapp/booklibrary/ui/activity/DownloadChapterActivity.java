package com.spriteapp.booklibrary.ui.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.database.ContentDb;
import com.spriteapp.booklibrary.enumeration.ChapterEnum;
import com.spriteapp.booklibrary.model.response.BookChapterResponse;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.model.response.GroupChapter;
import com.spriteapp.booklibrary.model.response.SubscriberContent;
import com.spriteapp.booklibrary.ui.adapter.second.DownLoadFirstAdapter;
import com.spriteapp.booklibrary.ui.presenter.SubscriberContentPresenter;
import com.spriteapp.booklibrary.ui.view.SubscriberContentView;
import com.spriteapp.booklibrary.util.CXAESUtil;
import com.spriteapp.booklibrary.util.CollectionUtil;
import com.spriteapp.booklibrary.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 下载章节内容
 * https://s.hxdrive.net/book_content?&book_id=692&chapter_id=252674&auto_sub=0
 */
public class DownloadChapterActivity extends TitleActivity implements SubscriberContentView {
    private final int ITEM_NUM = 20;
    private SubscriberContentPresenter contentPresenter;
    private ContentDb contentDb = new ContentDb(this);
    private List<GroupChapter> groupChapters = new ArrayList<>();
    private List<BookChapterResponse> mChapterList;
    private RecyclerView downloadRecycleView;
    private DownLoadFirstAdapter adapter;

    private TextView downLoad_chapter;

    private int book_id = 0;

    @Override
    public void initData() throws Exception {
        contentPresenter = new SubscriberContentPresenter();
        contentPresenter.attachView(this);
    }

    @Override
    public void configViews() throws Exception {
        super.configViews();
        contentPresenter.getContent(692, 252674, 0);
        contentPresenter.getChapter(692);
        setTitle("下载章节");
    }

    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_download_chapter, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
        TextView rightText = new TextView(this);
        rightText.setText("全选");
        mRightLayout.addView(rightText);

        adapter = new DownLoadFirstAdapter(this);
        downloadRecycleView = (RecyclerView) findViewById(R.id.downloadRecycleView);
        downloadRecycleView.setAdapter(adapter);
        downLoad_chapter = (TextView) findViewById(R.id.downLoad_chapter);

        mRightLayout.setOnClickListener(this);
        downLoad_chapter.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (i == R.id.book_reader_right_layout) {

        }
    }

    @Override
    public void onError(Throwable t) {

    }

    /**
     * 阅读内容
     *
     * @param result
     */
    @Override
    public void setData(Base<SubscriberContent> result) {
        try {
            String message = result.getMessage();
            SubscriberContent data = result.getData();
            if (data == null) {
                ToastUtil.showSingleToast(message);
                return;
            }
            String key = data.getChapter_content_key();
            String content = data.getChapter_content();

            contentDb.update(data.getBook_id(), data.getChapter_id(), data);

            String filecontent = CXAESUtil.encrypt("1", content);
            Log.i("download" + data.getChapter_title(), "----content-1-->" + content);


            SubscriberContent c = contentDb.queryContent(data.getBook_id(), data.getChapter_id());
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
        mChapterList = new ArrayList<>();
        mChapterList.addAll(catalogList);
        GroupChapter groupChapter;

        //分第一组--免费章节
        int first_item_num = 0;
        int size = mChapterList.size();
        for (int i = 0; i < size; i++) {
            if (ChapterEnum.CHAPTER_IS_VIP.getCode()
                    == mChapterList.get(i).getChapter_is_vip()) {
                first_item_num = i;
                Log.e("setChapter" + size, "------开始收费----" + i);
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
            groupChapter.setStart_chapter(s + first_item_num+1);
            groupChapter.setEnd_chapter(e + first_item_num);
            groupChapter.setPrice(mChapterList.get(s + first_item_num).getChapter_price() * ITEM_NUM);
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
}
