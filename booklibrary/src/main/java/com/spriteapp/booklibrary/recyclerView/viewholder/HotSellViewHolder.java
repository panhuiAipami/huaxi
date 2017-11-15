package com.spriteapp.booklibrary.recyclerView.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.manager.NightModeManager;
import com.spriteapp.booklibrary.manager.StoreColorManager;
import com.spriteapp.booklibrary.model.store.HotSellResponse;
import com.spriteapp.booklibrary.recyclerView.decorate.Visitable;
import com.spriteapp.booklibrary.recyclerView.model.HotSellModel;
import com.spriteapp.booklibrary.util.CollectionUtil;
import com.spriteapp.booklibrary.util.WebViewUtil;

import java.util.List;

/**
 * 热销
 * Created by kuangxiaoguo on 2017/7/28.
 */

public class HotSellViewHolder extends BaseViewHolder<Visitable> {

    private RelativeLayout mHotSellLayout;
    private RelativeLayout mChoiceLayout;
    private RelativeLayout mSerializeLayout;
    private TextView mHotSellTextView;
    private TextView mChoiceTextView;
    private TextView mSerializeTextView;
    private Context mContext;
    private final View divideView;

    public HotSellViewHolder(View itemView, Context context) {
        super(itemView);
        mContext = context;
        mHotSellLayout = (RelativeLayout) itemView.findViewById(R.id.book_reader_hot_sell_layout);
        mChoiceLayout = (RelativeLayout) itemView.findViewById(R.id.book_reader_choice_layout);
        mSerializeLayout = (RelativeLayout) itemView.findViewById(R.id.book_reader_serialize_layout);
        mHotSellTextView = (TextView) itemView.findViewById(R.id.book_reader_hot_sell_text_view);
        mChoiceTextView = (TextView) itemView.findViewById(R.id.book_reader_choice_text_view);
        mSerializeTextView = (TextView) itemView.findViewById(R.id.book_reader_serialize_text_view);
        divideView = itemView.findViewById(R.id.book_reader_divide_view);
    }

    @Override
    public void bindViewData(Visitable data) {
        if (!(data instanceof HotSellModel)) {
            return;
        }
        HotSellModel model = (HotSellModel) data;
        final List<HotSellResponse> responseList = model.getResponseList();
        if (CollectionUtil.isEmpty(responseList)) {
            return;
        }
        int size = responseList.size();
        switch (size) {
            case 1:
                mChoiceLayout.setVisibility(View.INVISIBLE);
                mSerializeLayout.setVisibility(View.INVISIBLE);
                break;
            case 2:
                mSerializeLayout.setVisibility(View.INVISIBLE);
                mChoiceTextView.setText(responseList.get(1).getName());
                mChoiceLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WebViewUtil.getInstance().getJumpUrl(mContext, responseList.get(1).getUrl());
                    }
                });
                break;
            case 3:
                mChoiceTextView.setText(responseList.get(1).getName());
                mSerializeTextView.setText(responseList.get(2).getName());
                mChoiceLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WebViewUtil.getInstance().getJumpUrl(mContext, responseList.get(1).getUrl());
                    }
                });
                mSerializeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WebViewUtil.getInstance().getJumpUrl(mContext, responseList.get(2).getUrl());
                    }
                });
                break;
            default:
                break;
        }
        mHotSellTextView.setText(responseList.get(0).getName());
        mHotSellLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewUtil.getInstance().getJumpUrl(mContext, responseList.get(0).getUrl());
            }
        });
        StoreColorManager manager = NightModeManager.getManager();
        if (manager == null) {
            return;
        }
        divideView.setBackgroundColor(manager.getDivideLineColor());
    }

}
