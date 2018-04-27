package com.spriteapp.booklibrary.ui.activity;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.util.ScreenUtil;

public class BookCommentActivity extends TitleActivity {
    RecyclerView recycler_view_comment;


    @Override
    public void initData() throws Exception {
        setTitle("作品评论");
    }

    @Override
    public void findViewId() throws Exception {
        super.findViewId();
        recycler_view_comment = (RecyclerView) findViewById(R.id.recycler_view_comment);

    }


    @Override
    public void addContentView() {
        View view = getLayoutInflater().inflate(R.layout.activity_book_comment, null);
        mContainerLayout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainerLayout.setPadding(0, ScreenUtil.dpToPxInt(47),0,0);
    }


}
