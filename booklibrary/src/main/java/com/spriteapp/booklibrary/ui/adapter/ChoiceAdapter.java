package com.spriteapp.booklibrary.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseActivity;
import com.spriteapp.booklibrary.model.ChoiceBean;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.Util;

import java.util.List;

/**
 * TODO: Replace the implementation with code for your data type.
 */
public class ChoiceAdapter extends RecyclerView.Adapter<ChoiceAdapter.ViewHolder> {

    private final List<ChoiceBean> mValues;
    private Activity c;

    public ChoiceAdapter(List<ChoiceBean> items, Activity c) {
        mValues = items;
        this.c = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.choice_fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ChoiceBean bean = mValues.get(position);
        holder.title.setText(bean.getName());
        holder.details.setText(bean.getIntro());
        holder.reader.setText(bean.getExtend() + "人追读");
        GlideUtils.loadImage(holder.mContentView, bean.getImages(), c);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.toReadActivity(c, bean.getBook_id(), 0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, details, reader, look_detail;
        public ImageView mContentView;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            details = (TextView) view.findViewById(R.id.details);
            look_detail = (TextView) view.findViewById(R.id.look_details);
            reader = (TextView) view.findViewById(R.id.reader);
            mContentView = (ImageView) view.findViewById(R.id.cover);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
            layoutParams.height = (BaseActivity.deviceWidth - Util.dp2px(c, 24)) / 2;
            layoutParams.weight = LinearLayout.LayoutParams.MATCH_PARENT;
            mContentView.setLayoutParams(layoutParams);
        }
    }
}
