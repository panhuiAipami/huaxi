package com.spriteapp.booklibrary.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.model.MyApprenticeBean;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.util.GlideUtils;

import java.util.List;

/**
 * Created by userfirst on 2018/3/16.
 */

public class MyApprenticeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int MYAPPRENTICE = 1;
    private final int ACTIVATIONAPPRENTICE = 2;
    private final int BUTTON = 3;
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
        } else if (viewType == ACTIVATIONAPPRENTICE) {//激活徒弟列表
            convertView = LayoutInflater.from(context).inflate(R.layout.activation_apprentice_item_layout, parent, false);
            return new MyApprenticeAdapter.ActivationApprenticeListViewHolder(convertView);
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.activation_apprentice_button_layout, parent, false);
            return new MyApprenticeAdapter.ButtonViewHolder(convertView);
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
            GlideUtils.loadImage(myApprenticeListViewHolder.apprentice_head, pupilDataBean.getPupil_user_avatar(), context);
            if (pupilDataBean.getGender() == 1) {//性别标识
                myApprenticeListViewHolder.sex_img.setEnabled(true);
            } else {
                myApprenticeListViewHolder.sex_img.setEnabled(false);
            }

        } else if (holder instanceof ActivationApprenticeListViewHolder) {
            ActivationApprenticeListViewHolder activationApprenticeListViewHolder = (ActivationApprenticeListViewHolder) holder;
        } else if (holder instanceof ButtonViewHolder) {
            ButtonViewHolder buttonViewHolder = (ButtonViewHolder) holder;
            buttonViewHolder.goto_activation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BookDetailResponse detailResponse = new BookDetailResponse();
                    HuaXiSDK.getInstance().showShareDialog(context, detailResponse, true, 2);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        if (list.size() == 0) return 0;
        return type == 0 ? list.size() : list.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == list.size() && position != 0 ? BUTTON : MYAPPRENTICE;
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

    private class ButtonViewHolder extends RecyclerView.ViewHolder {//激活徒弟按钮
        private TextView goto_activation;


        public ButtonViewHolder(View itemView) {
            super(itemView);
            goto_activation = (TextView) itemView.findViewById(R.id.goto_activation);

        }
    }
}
