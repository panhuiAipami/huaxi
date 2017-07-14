package net.huaxi.reader.model;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import net.huaxi.reader.activity.BookDetailActivity;
import net.huaxi.reader.bean.Recommended;

import net.huaxi.reader.R;

import net.huaxi.reader.activity.SimpleWebViewActivity;
import net.huaxi.reader.adapter.AdapterBookShelf;
import net.huaxi.reader.util.ImageUtil;

/**
 * Function:书架顶部推荐栏
 * Author:zhumingwei
 * Create:
 */
public class ShelfTopRecommendedHelper implements View.OnClickListener {
    //顶部定时推荐位
    private View vTopShelfRecommended;
    private Activity activity;
    private RecyclerView rvBookshelf;
    private ImageView ivClose, ivRecommended;
    private AdapterBookShelf adapterBookShelf;

    public ShelfTopRecommendedHelper(Activity activity, RecyclerView rvBookshelf, AdapterBookShelf adapterBookShelf) {
        this.adapterBookShelf = adapterBookShelf;
        this.activity = activity;
        this.rvBookshelf = rvBookshelf;

        vTopShelfRecommended = LayoutInflater.from(activity).inflate(R.layout.item_bookshelf_head, null);
        ivClose = (ImageView) vTopShelfRecommended.findViewById(R.id.item_bookshelf_head_close);
        ivRecommended = (ImageView) vTopShelfRecommended.findViewById(R.id.item_bookshelf_head_recommended);
        ivClose.setOnClickListener(this);
        ivRecommended.setOnClickListener(this);
    }

    public void initData(Recommended recommended) {
        // TODO: 2016/4/26 初始化推荐页接口
        ImageUtil.loadImage(activity, recommended.getImage(), ivRecommended, 0);
        setOnclick(recommended);
        showTop();
    }

    private void setOnclick(final Recommended recommended) {
        if (recommended.getType() != null && recommended.getType().equals("0102")) {
            //书页面
            ivRecommended.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(activity, BookDetailActivity.class);
                    Intent intent = new Intent(activity, BookDetailActivity.class);
                    intent.putExtra("bookid", recommended.getBookid());
                    activity.startActivity(intent);
                }
            });
        } else if (recommended.getType() != null && recommended.getType().equals("0103")) {
            //活动页面
            ivRecommended.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, SimpleWebViewActivity.class);
                    intent.putExtra(SimpleWebViewActivity.WEBTYPE, SimpleWebViewActivity.WEBTYPE_ACTIVITY);
                    intent.putExtra("webtitle", recommended.getTitle());
                    intent.putExtra("weburl", recommended.getPageurl());
                    activity.startActivity(intent);
                }
            });

        }
    }

    public View getvTopShelfRecommended() {
        return vTopShelfRecommended;
    }


    public void showTop() {
        if (rvBookshelf != null && adapterBookShelf.getHeaderView() == null && vTopShelfRecommended != null) {
            adapterBookShelf.setHeaderView(vTopShelfRecommended);
            if(rvBookshelf.getScrollState()==RecyclerView.SCROLL_STATE_IDLE){
                rvBookshelf.smoothScrollToPosition(0);
            }
        }
    }

    public void hideTop() {
        if (rvBookshelf != null && adapterBookShelf.getHeaderView() != null) {
            adapterBookShelf.removeHeadView();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_bookshelf_head_close:
                hideTop();
                break;
        }
    }
}
