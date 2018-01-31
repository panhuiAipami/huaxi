package com.spriteapp.booklibrary.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/11.
 */

public class RankSortPop extends PopupWindow {
    private Activity context;
    private LayoutInflater mInflater;
    private View mContentView;
    private TextView lately_text, updata_text, collection_text, updata_book;
    private ImageView lately_img, updata_img, collection_img, updata_icon;
    private List<ImageView> images = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    int position;
    String name = "热销榜";

    //"last_read_time desc","last_update_book_datetime desc","book_add_shelf desc"
    public RankSortPop(Activity activity, View v) {
        this.context = activity;
        showpopWindow(v);

    }

    public void showpopWindow(View v) {
        //打气筒
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //打气
        mContentView = mInflater.inflate(R.layout.rank_paixu_pop_layout, null);
        //设置View
        setContentView(mContentView);
        //设置宽与高
        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(ContextCompat.getColor(context, R.color.pop_back));
        setBackgroundDrawable(dw);
        showAsDropDown(v, 30, 0, Gravity.LEFT);
        viewClick(mContentView);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }

    public void viewClick(View item) {
        lately_text = (TextView) item.findViewById(R.id.lately_text);
        updata_text = (TextView) item.findViewById(R.id.updata_text);
        collection_text = (TextView) item.findViewById(R.id.collection_text);
        updata_book = (TextView) item.findViewById(R.id.updata_book);
        lately_img = (ImageView) item.findViewById(R.id.lately_img);
        updata_img = (ImageView) item.findViewById(R.id.updata_img);
        collection_img = (ImageView) item.findViewById(R.id.collection_img);
        updata_icon = (ImageView) item.findViewById(R.id.updata_icon);
        images.add(lately_img);
        images.add(updata_img);
        images.add(collection_img);
        images.add(updata_icon);
        setState(position,name);
        lately_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(0,"热销榜");
                dismiss();
            }
        });

        updata_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(1,"人气榜");
                dismiss();
            }
        });


        collection_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(2,"评论榜");
                dismiss();
            }
        });

        updata_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(3,"更新榜");
                dismiss();
            }
        });
    }

    public void setState(int pos,String n) {
        for (int i = 0; i < images.size(); i++) {
            if (pos == i) {
                listener(i + 1,n);
                images.get(i).setVisibility(View.VISIBLE);
            } else {
                images.get(i).setVisibility(View.GONE);
            }
        }
        position = pos;
        name = n;
    }

    public void listener(int interval,String name) {
        if (onItemClickListener != null)
            onItemClickListener.refresh(interval,name);
    }

    public void setSortOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void refresh(int interval,String name);
    }
}
