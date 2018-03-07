package com.spriteapp.booklibrary.ui.adapter.second;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.model.response.BookChapterResponse;
import com.spriteapp.booklibrary.model.response.GroupChapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 下载列表适配器
 * panhui
 */

public class DownLoadFirstAdapter extends DownLoadSecondAdapter<DownLoadFirstAdapter.GroupItemViewHolder, DownLoadFirstAdapter.SubItemViewHolder> {
    private Context context;
    private List<GroupChapter> list = new ArrayList<>();
    private List<BookChapterResponse> mChapterList = new ArrayList<>();
    private OnSelectList onSelectList;
    int total_price = 0;
    int group_price = 0;

    public DownLoadFirstAdapter(Context context, OnSelectList onSelectList) {
        this.context = context;
        this.onSelectList = onSelectList;
    }

    public void setData(List datas) {
        list = datas;
        notifyNewData(list);
    }

    public void notifyDownLoadStatus() {
        this.mChapterList.clear();
        notifyDataSetChanged();
    }

    public void allSelectListAndPrice(List<BookChapterResponse> mChapterList, int price) {
        total_price = price;
        this.mChapterList.clear();
        if (mChapterList != null && mChapterList.size() > 0) {
            this.mChapterList.addAll(mChapterList);
        }
    }

    @Override
    public RecyclerView.ViewHolder groupItemViewHolder(ViewGroup parent) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_list_item_head_layout, parent, false);

        return new GroupItemViewHolder(v);
    }

    @Override
    public RecyclerView.ViewHolder subItemViewHolder(ViewGroup parent) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_list_item_child_layout, parent, false);

        return new SubItemViewHolder(v);
    }

    @Override
    public void onGroupItemBindViewHolder(final RecyclerView.ViewHolder holder, final int groupItemIndex) {
        final GroupChapter g = list.get(groupItemIndex);
        final GroupItemViewHolder h = (GroupItemViewHolder) holder;
        h.group_title.setText("第" + g.getStart_chapter() + "章-第" + g.getEnd_chapter() + "章");
        h.check_box.setSelected(g.isIs_check());

        for (BookChapterResponse bc : g.getmChapterList()) {
            if (!bc.getIs_download()) {
                g.setIs_download(0);
                break;
            } else {
                g.setIs_download(1);
            }
        }

        int free_color = R.color.color_orange;
        if (g.getIs_free()) {
            h.is_free.setText("免费");
            free_color = R.color.green_color;
        } else if (g.isIs_check()) {
            h.is_free.setText(g.getPrice() > 0 ? g.getPrice() + "花贝/花瓣" : "");
        } else {
            h.is_free.setText("");
        }
        h.is_free.setTextColor(ContextCompat.getColor(context, free_color));

        if (g.getIs_download()) {
            h.is_download.setVisibility(View.VISIBLE);
            h.linear_check_box.setVisibility(View.GONE);
        } else {
            h.is_download.setVisibility(View.GONE);
            h.linear_check_box.setVisibility(View.VISIBLE);
        }

        int color = 0;
        if (groupItemIndex == 0)
            color = R.color.color_orange;
        else
            color = R.color.two_font_color;
        h.group_title.setTextColor(ContextCompat.getColor(context, color));


        //选中分组
        h.check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isChecked = !g.isIs_check();
                g.setIs_check(isChecked);
                h.check_box.setSelected(isChecked);

                List<BookChapterResponse> child_list = g.getmChapterList();
                List<BookChapterResponse> select_child_list = new ArrayList<>();
                group_price = 0;
                for (BookChapterResponse chapter : child_list) {
                    if (isChecked) {
                        //选中时，已勾选和已下载不重复加钱
                        if (!chapter.isIs_check()) {
                            if (!chapter.getIs_download())
                                group_price += chapter.getChapter_price();
                        }
                        if (!chapter.getIs_download())//没下载的才能选中
                            if (!select_child_list.contains(chapter))//已选择的不重复添加
                                select_child_list.add(chapter);
                        Log.e("---isC--" + isChecked, chapter.isIs_check() + "--------aaaa----------" + chapter.getIs_download());
                    } else if (!isChecked) {//取消时
                        if (!chapter.getIs_download())
                            group_price += chapter.getChapter_price();
                    }
                    chapter.setIs_check(isChecked);
                }

                if (isChecked) {
                    total_price += group_price;
                    g.setPrice(group_price);
                    if (group_price > 0)
                        h.is_free.setText(group_price + "花贝/花瓣");
                    mChapterList.removeAll(g.getSelect_list());
                    mChapterList.addAll(select_child_list);
                } else {
                    total_price -= group_price;
                    if (g.getSelect_list().size() > 0)
                        mChapterList.removeAll(g.getSelect_list());
                    else//删除全选添加的数据
                        mChapterList.removeAll(g.getmChapterList());
                }
                g.setmChapterList(child_list);
                g.setSelect_list(select_child_list);
                list.set(groupItemIndex, g);
                notifyDataSetChanged();
                if (onSelectList != null)
                    onSelectList.onSelect(mChapterList, total_price);

            }
        });

    }


    @Override
    public void onSubItemBindViewHolder(final RecyclerView.ViewHolder holder, final int groupItemIndex, final int subItemIndex) {
        final BookChapterResponse c = list.get(groupItemIndex).getmChapterList().get(subItemIndex);
        final SubItemViewHolder s = (SubItemViewHolder) holder;
        s.group_title.setText(c.getChapter_title());
        s.check_box.setSelected(c.isIs_check());

        int color = 0;
        if (groupItemIndex == 0) {
            s.is_free.setText("免费");
            color = R.color.green_color;
        } else {
            color = R.color.color_orange;
            s.is_free.setText(c.getChapter_price() + "花贝/花瓣");
        }
        s.is_free.setTextColor(ContextCompat.getColor(context, color));


        if (c.getIs_download()) {
            s.is_download.setVisibility(View.VISIBLE);
            s.linear_check_box.setVisibility(View.GONE);
        } else {
            s.is_download.setVisibility(View.GONE);
            s.linear_check_box.setVisibility(View.VISIBLE);
        }

        //选中item
        s.check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isChecked = !c.isIs_check();
                s.check_box.setSelected(isChecked);
                c.setIs_check(isChecked);
                int price = 0;
                GroupChapter g = list.get(groupItemIndex);

                g.getmChapterList().set(subItemIndex, c);
                price = c.getChapter_price();
                if (!isChecked) {
                    g.setIs_check(isChecked);
                    mChapterList.remove(c);
                    g.setPrice(total_price -= price);
                } else {//选中
                    if (price > 0) {
                        total_price += price;
                    }
                    g.setPrice(g.getPrice() + price);
                    g.getSelect_list().add(c);
                    list.set(groupItemIndex, g);
                    mChapterList.add(c);
                }
                notifyItemChanged(groupItemIndex);

                if (onSelectList != null)
                    onSelectList.onSelect(mChapterList, total_price);
            }
        });
    }

    @Override
    public void onGroupItemClick(Boolean isExpand, final GroupItemViewHolder holder, final int groupItemIndex) {
        if (isExpand) {
            holder.image_button_down.setImageResource(R.mipmap.image_button_right);
        } else {
            holder.image_button_down.setImageResource(R.mipmap.image_button_down);
        }
    }

    @Override
    public void onSubItemClick(SubItemViewHolder holder, int groupItemIndex, int subItemIndex) {

    }

    public static class GroupItemViewHolder extends RecyclerView.ViewHolder {
        ImageView image_button_down;
        TextView group_title;
        LinearLayout linear_check_box;
        TextView is_free, is_download;
        CheckBox check_box;

        public GroupItemViewHolder(View itemView) {
            super(itemView);
            image_button_down = (ImageView) itemView.findViewById(R.id.image_button_down);
            group_title = (TextView) itemView.findViewById(R.id.group_title);
            linear_check_box = (LinearLayout) itemView.findViewById(R.id.linear_check_box);
            is_download = (TextView) itemView.findViewById(R.id.is_download);
            is_free = (TextView) itemView.findViewById(R.id.is_free);
            check_box = (CheckBox) itemView.findViewById(R.id.check_box);
        }
    }

    public static class SubItemViewHolder extends RecyclerView.ViewHolder {

        TextView group_title;
        LinearLayout linear_check_box;
        TextView is_free, is_download;
        CheckBox check_box;

        public SubItemViewHolder(View itemView) {
            super(itemView);
            group_title = (TextView) itemView.findViewById(R.id.group_title);
            linear_check_box = (LinearLayout) itemView.findViewById(R.id.linear_check_box);
            is_download = (TextView) itemView.findViewById(R.id.is_download);
            is_free = (TextView) itemView.findViewById(R.id.is_free);
            check_box = (CheckBox) itemView.findViewById(R.id.check_box);
        }
    }

    public interface OnSelectList {
        public void onSelect(List<BookChapterResponse> mChapterList, int price);
    }
}

