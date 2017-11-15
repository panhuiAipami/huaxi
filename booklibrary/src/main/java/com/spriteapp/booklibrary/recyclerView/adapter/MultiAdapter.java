package com.spriteapp.booklibrary.recyclerView.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spriteapp.booklibrary.recyclerView.decorate.Visitable;
import com.spriteapp.booklibrary.recyclerView.factory.ItemTypeFactory;
import com.spriteapp.booklibrary.recyclerView.viewholder.BaseViewHolder;
import com.spriteapp.booklibrary.util.CollectionUtil;

import java.util.List;

public class MultiAdapter extends RecyclerView.Adapter<BaseViewHolder<Visitable>> {

    private ItemTypeFactory typeFactory;
    private List<Visitable> mItems;
    private Context mContext;

    /**
     * 构造函数
     */
    public MultiAdapter(Context context, List<Visitable> mData) {
        this.mContext = context;
        //  item工厂类 生产viewHolder
        this.typeFactory = new ItemTypeFactory();
        mItems = mData;
    }

    @Override
    public BaseViewHolder<Visitable> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return typeFactory.createViewHolder(viewType, view, mContext);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<Visitable> holder, int position) {
        holder.bindViewData(mItems.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).type(typeFactory);
    }

    @Override
    public int getItemCount() {
        return CollectionUtil.isEmpty(mItems) ? 0 : mItems.size();
    }

}
