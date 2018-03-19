package com.spriteapp.booklibrary.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.model.MyApprenticeBean;
import com.spriteapp.booklibrary.util.GlideUtils;

import java.util.List;

/**
 * Created by userfirst on 2018/3/16.
 */

public class MyApprenticeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int MYAPPRENTICE = 1;
    private final int ACTIVATIONAPPRENTICE = 2;
    private Context context;
    private List<MyApprenticeBean.PupilDataBean> list;
    private int type;//1为我的徒弟,2为激活徒弟

    public MyApprenticeAdapter(Context context, List<MyApprenticeBean.PupilDataBean> list, int type) {
        this.context = context;
        this.list = list;
        this.type = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = null;
        if (viewType == MYAPPRENTICE) {//我的徒弟列表
            convertView = LayoutInflater.from(context).inflate(R.layout.myapprentice_item_layout, parent, false);
            return new MyApprenticeAdapter.MyApprenticeListViewHolder(convertView);
        } else {//激活徒弟列表
            convertView = LayoutInflater.from(context).inflate(R.layout.activation_apprentice_item_layout, parent, false);
            return new MyApprenticeAdapter.ActivationApprenticeListViewHolder(convertView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyApprenticeListViewHolder) {
            MyApprenticeListViewHolder myApprenticeListViewHolder = (MyApprenticeListViewHolder) holder;
            MyApprenticeBean.PupilDataBean pupilDataBean = list.get(position);
            if (pupilDataBean == null) return;
            myApprenticeListViewHolder.number.setText(position + "");
            myApprenticeListViewHolder.apprentice_name.setText(pupilDataBean.getPupil_user_name());
            myApprenticeListViewHolder.gold_num.setText(pupilDataBean.getGold_coins());
            GlideUtils.loadImage(myApprenticeListViewHolder.apprentice_head,pupilDataBean.getPupil_user_avatar(),context);

        } else if (holder instanceof ActivationApprenticeListViewHolder) {
            ActivationApprenticeListViewHolder activationApprenticeListViewHolder = (ActivationApprenticeListViewHolder) holder;
        }

    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        if (list.size() == 0) return 0;
        return list.size();
    }

    private class MyApprenticeListViewHolder extends RecyclerView.ViewHolder {//我的徒弟
        private TextView number, apprentice_name, gold_num;
        private ImageView sex_img, apprentice_head;

        public MyApprenticeListViewHolder(View itemView) {
            super(itemView);
            number = (TextView) itemView.findViewById(R.id.number);
            apprentice_name = (TextView) itemView.findViewById(R.id.apprentice_name);
            gold_num = (TextView) itemView.findViewById(R.id.gold_num);
            sex_img = (ImageView) itemView.findViewById(R.id.sex_img);
            apprentice_head = (ImageView) itemView.findViewById(R.id.apprentice_head);
        }
    }

    private class ActivationApprenticeListViewHolder extends RecyclerView.ViewHolder {//激活徒弟


        public ActivationApprenticeListViewHolder(View itemView) {
            super(itemView);

        }
    }
}
