package com.spriteapp.booklibrary.recyclerView.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.recyclerView.decorate.Visitable;
import com.spriteapp.booklibrary.util.ToastUtil;

/**
 * 分类
 * Created by kuangxiaoguo on 2017/7/28.
 */

public class TypeViewHolder extends BaseViewHolder<Visitable> {

    private ImageView mOnSellImageView;
    private ImageView mQualityImageView;
    private ImageView mGoodBookImageView;
    private ImageView mNewBookImageView;
    private Context mContext;

    public TypeViewHolder(View itemView, Context context) {
        super(itemView);
        mContext = context;
        mOnSellImageView = (ImageView) itemView.findViewById(R.id.book_reader_on_sell_image_view);
        mQualityImageView = (ImageView) itemView.findViewById(R.id.book_reader_quality_image_view);
        mGoodBookImageView = (ImageView) itemView.findViewById(R.id.book_reader_good_book_image_view);
        mNewBookImageView = (ImageView) itemView.findViewById(R.id.book_reader_new_book_image_view);
    }

    @Override
    public void bindViewData(Visitable data) {
        mOnSellImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/7/28 限时特价
                ToastUtil.showSingleToast("限时特价");
            }
        });
        mQualityImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/7/28 优质原创
                ToastUtil.showSingleToast("优质原创");
            }
        });
        mGoodBookImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/7/28 优质好书
                ToastUtil.showSingleToast("优质好书");
            }
        });
        mNewBookImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2017/7/28 新书抢先
                ToastUtil.showSingleToast("新书抢先");
            }
        });
    }
}
