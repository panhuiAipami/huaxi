package com.spriteapp.booklibrary.ui.adapter.second;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
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

    public DownLoadFirstAdapter(Context context) {
        this.context = context;
    }

    public void setData(List datas) {
        list = datas;
        notifyNewData(list);
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
        Log.e("GroupItem --->" + groupItemIndex, g.getPrice() + "<--price----------------check-->" + g.isIs_check());
        if (g.isIs_check()) {
            if (g.getIs_free()) {
                h.is_free.setText("免费");
            } else {
                h.is_free.setText(g.getPrice() + "花贝/花瓣");
            }
        } else {
            if (g.getIs_free()) {
                h.is_free.setText("免费");
            } else {
                h.is_free.setText("");
            }
        }
        //setSelected..setChecked
        h.check_box.setSelected(g.isIs_check());
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
                g.setIs_check(isChecked);
                List<BookChapterResponse> child_list = g.getmChapterList();
                int total_price = 0;
                for (int i = 0; i < child_list.size(); i++) {
                    total_price += child_list.get(i).getChapter_price();
                    child_list.get(i).setIs_check(isChecked);
                }
                Log.e("onCheckedChanged --->" + groupItemIndex, total_price + "<----price-------------isChecked--->" + isChecked);
                if (total_price > 0) {
                    g.setPrice(total_price);
                    h.is_free.setText(total_price + "花贝/花瓣");
                }

                g.setmChapterList(child_list);
                list.set(groupItemIndex, g);
//                notifyGroupItemChangedData(groupItemIndex);
//                notifyItemChanged(groupItemIndex);
                notifyDataSetChanged();


            }
        });

    }

    private void notifyGroupItemChangedData(final int p) {
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                notifyItemC(p);
            }
        };
        handler.postDelayed(r, 100);
    }


    @Override
    public void onSubItemBindViewHolder(final RecyclerView.ViewHolder holder, final int groupItemIndex, final int subItemIndex) {
        final BookChapterResponse c = list.get(groupItemIndex).getmChapterList().get(subItemIndex);
        final SubItemViewHolder s = (SubItemViewHolder) holder;
        s.group_title.setText(c.getChapter_title());
        int color = 0;
        if (groupItemIndex == 0) {
            s.is_free.setText("免费");
            color = R.color.green_color;
        } else {
            color = R.color.color_orange;
            s.is_free.setText(c.getChapter_price() + "花贝/花瓣");
        }
        s.is_free.setTextColor(ContextCompat.getColor(context, color));
        s.check_box.setSelected(c.isIs_check());

        //选中item
        s.check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                s.check_box.setSelected(isChecked);
                c.setIs_check(isChecked);
                list.get(groupItemIndex).getmChapterList().set(subItemIndex, c);
                notifyGroupItemChangedData(groupItemIndex);
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
        TextView is_free;
        CheckBox check_box;

        public GroupItemViewHolder(View itemView) {
            super(itemView);
            image_button_down = (ImageView) itemView.findViewById(R.id.image_button_down);
            group_title = (TextView) itemView.findViewById(R.id.group_title);
            is_free = (TextView) itemView.findViewById(R.id.is_free);
            check_box = (CheckBox) itemView.findViewById(R.id.check_box);
        }
    }

    public static class SubItemViewHolder extends RecyclerView.ViewHolder {

        TextView group_title;
        TextView is_free;
        CheckBox check_box;

        public SubItemViewHolder(View itemView) {
            super(itemView);
            group_title = (TextView) itemView.findViewById(R.id.group_title);
            is_free = (TextView) itemView.findViewById(R.id.is_free);
            check_box = (CheckBox) itemView.findViewById(R.id.check_box);
        }
    }
}

