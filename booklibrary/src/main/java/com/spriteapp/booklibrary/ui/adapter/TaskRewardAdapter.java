package com.spriteapp.booklibrary.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.model.TaskRewardBean;
import com.spriteapp.booklibrary.util.GlideUtils;

import java.util.List;

/**
 * Created by userfirst on 2018/3/22.
 */

public class TaskRewardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int APPRENTICEITEM = 0;
    private final int TASKITEM = 1;
    private Context context;
    private List<TaskRewardBean> list;
    private int type;//来区分是任务奖励还是收徒奖励,0为收徒,1为任务

    public TaskRewardAdapter(Context context, List<TaskRewardBean> list, int type) {
        this.context = context;
        this.list = list;
        this.type = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = null;
        if (viewType == APPRENTICEITEM) {//收徒
            convertView = LayoutInflater.from(context).inflate(R.layout.new_shoutu_item_layout, parent, false);
            return new TaskRewardAdapter.ApprenticeRewardViewHolder(convertView);
        } else {//任务
            convertView = LayoutInflater.from(context).inflate(R.layout.new_task_item_layout, parent, false);
            return new TaskRewardAdapter.TaskRewardViewHolder(convertView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof TaskRewardViewHolder) {
            TaskRewardBean taskRewardBean = list.get(position);
            if (taskRewardBean == null) return;
            TaskRewardViewHolder taskRewardViewHolder = (TaskRewardViewHolder) holder;
            if (!TextUtils.isEmpty(taskRewardBean.getReward_name()))
                taskRewardViewHolder.task_name.setText(taskRewardBean.getReward_name());
            if (!TextUtils.isEmpty(taskRewardBean.getMemo()))
                taskRewardViewHolder.task_details.setText(taskRewardBean.getMemo());
            if (!TextUtils.isEmpty(taskRewardBean.getAddtime()))
                taskRewardViewHolder.task_time.setText(taskRewardBean.getAddtime());
            taskRewardViewHolder.gold_num.setText("+" + taskRewardBean.getGold_coins() + "金币");

        } else if (holder instanceof ApprenticeRewardViewHolder) {
            TaskRewardBean taskRewardBean = list.get(position);
            if (taskRewardBean == null) return;
            ApprenticeRewardViewHolder apprenticeRewardViewHolder = (ApprenticeRewardViewHolder) holder;
            if (!TextUtils.isEmpty(taskRewardBean.getPupil_user_name()))
                apprenticeRewardViewHolder.task_name.setText(taskRewardBean.getPupil_user_name());
            if (!TextUtils.isEmpty(taskRewardBean.getMemo()))
                apprenticeRewardViewHolder.task_details.setText(taskRewardBean.getMemo());
            if (!TextUtils.isEmpty(taskRewardBean.getAddtime()))
                apprenticeRewardViewHolder.task_time.setText(taskRewardBean.getAddtime());
            apprenticeRewardViewHolder.gold_num.setText("+" + taskRewardBean.getGold_coins() + "金币");
            GlideUtils.loadImage(apprenticeRewardViewHolder.apprentice_head, taskRewardBean.getPupil_user_avatar(), context);
        }

    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return type == 0 ? APPRENTICEITEM : TASKITEM;
    }

    private class TaskRewardViewHolder extends RecyclerView.ViewHolder {//任务奖励
        private TextView task_name, task_details, task_time, gold_num;

        public TaskRewardViewHolder(View itemView) {
            super(itemView);
            task_name = (TextView) itemView.findViewById(R.id.task_name);
            task_details = (TextView) itemView.findViewById(R.id.task_details);
            task_time = (TextView) itemView.findViewById(R.id.task_time);
            gold_num = (TextView) itemView.findViewById(R.id.gold_num);
        }
    }

    private class ApprenticeRewardViewHolder extends RecyclerView.ViewHolder {//收徒奖励
        private TextView task_name, task_details, task_time, gold_num;
        private ImageView apprentice_head;

        public ApprenticeRewardViewHolder(View itemView) {
            super(itemView);
            task_name = (TextView) itemView.findViewById(R.id.task_name);
            task_details = (TextView) itemView.findViewById(R.id.task_details);
            task_time = (TextView) itemView.findViewById(R.id.task_time);
            gold_num = (TextView) itemView.findViewById(R.id.gold_num);
            apprentice_head = (ImageView) itemView.findViewById(R.id.apprentice_head);
        }
    }
}
