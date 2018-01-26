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

import static com.spriteapp.booklibrary.database.BookDb.SORT_DESC;
import static com.spriteapp.booklibrary.ui.fragment.BookshelfFragment.sort_type;

/**
 * Created by Administrator on 2018/1/11.
 */

public class SortPop extends PopupWindow {
    private Activity context;
    private LayoutInflater mInflater;
    private View mContentView;
    private TextView lately_text, updata_text, collection_text;
    private ImageView lately_img, updata_img, collection_img;
    private List<ImageView> images = new ArrayList<>();
    private String LAST_READ_TIME = "last_read_time desc";
    private String LAST_UPDATE_BOOK_DATETIME = "last_update_book_datetime desc";
    private String BOOK_ADD_SHELF = "last_read_time desc";
    private OnItemClickListener onItemClickListener;

    //"last_read_time desc","last_update_book_datetime desc","book_add_shelf desc"
    public SortPop(Activity activity, View v) {
        this.context = activity;
        showpopWindow(v);

    }

    public void showpopWindow(View v) {
        //打气筒
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //打气
        mContentView = mInflater.inflate(R.layout.paixu_pop_layout, null);
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
        setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }

    public void viewClick(View item) {
        lately_text = (TextView) item.findViewById(R.id.lately_text);
        updata_text = (TextView) item.findViewById(R.id.updata_text);
        collection_text = (TextView) item.findViewById(R.id.collection_text);
        lately_img = (ImageView) item.findViewById(R.id.lately_img);
        updata_img = (ImageView) item.findViewById(R.id.updata_img);
        collection_img = (ImageView) item.findViewById(R.id.collection_img);
        images.add(lately_img);
        images.add(updata_img);
        images.add(collection_img);
        setState(sort_type);
        lately_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SORT_DESC = LAST_READ_TIME;
                listener();
                setState(0);
                dismiss();
            }
        });

        updata_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SORT_DESC = BOOK_ADD_SHELF;
                listener();
                setState(1);
                dismiss();
            }
        });


        collection_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SORT_DESC = LAST_UPDATE_BOOK_DATETIME;
                listener();
                setState(2);
                dismiss();
            }
        });
    }

    public void setState(int pos) {
        for (int i = 0; i < images.size(); i++) {
            if (pos == i) {
                images.get(i).setVisibility(View.VISIBLE);
            } else {
                images.get(i).setVisibility(View.GONE);
            }

        }
        sort_type = pos;
    }

    public void listener() {
        if (onItemClickListener != null)
            onItemClickListener.refresh();
    }

    public void setSortOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void refresh();
    }
}
