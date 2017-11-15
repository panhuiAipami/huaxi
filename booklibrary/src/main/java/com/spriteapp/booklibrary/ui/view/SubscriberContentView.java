package com.spriteapp.booklibrary.ui.view;

import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.base.BaseView;
import com.spriteapp.booklibrary.model.response.BookChapterResponse;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.model.response.SubscriberContent;

import java.util.List;

/**
 * Created by kuangxiaoguo on 2017/7/10.
 */

public interface SubscriberContentView extends BaseView<SubscriberContent> {

    void setChapter(List<BookChapterResponse> catalogList);

    void toChannelLogin();

    void setBookDetail(BookDetailResponse data);
}
