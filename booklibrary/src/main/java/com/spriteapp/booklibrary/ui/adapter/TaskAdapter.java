package com.spriteapp.booklibrary.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.listener.ListenerManager;
import com.spriteapp.booklibrary.model.TaskBean;
import com.spriteapp.booklibrary.ui.dialog.InvitationCodeDialog;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.GlideUtils;

import java.util.List;

/**
 * Created by userfirst on 2018/3/13.
 */

public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TITLEPOS = 0;
    private final int DETAILSPOS = 1;
    private Activity context;
    private List<TaskBean> list;
    private int num = 0;
    private boolean BEGINNERTASK = false;
    private boolean DAILYTASK = false;
    private int beginnerpos = 0;
    private int dailypos = 0;
    private String gotoText = null;

    public TaskAdapter(Activity context, List<TaskBean> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = null;
        if (viewType == TITLEPOS) {//任务标题
            convertView = LayoutInflater.from(context).inflate(R.layout.task_title_item, parent, false);
            return new TaskAdapter.TitleViewHolder(convertView);
        } else {//任务详情
            convertView = LayoutInflater.from(context).inflate(R.layout.task_details_item, parent, false);
            return new TaskAdapter.TaskViewHolder(convertView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TitleViewHolder) {
            TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
            if (position == 0 && BEGINNERTASK) {//下标为0并且新手任务存在返回新手任务
                titleViewHolder.title.setText("新手任务");
            } else if (position != 0 && BEGINNERTASK && DAILYTASK) {//下标不为0并且新手与每日任务同时存在返回每日任务
                titleViewHolder.title.setText("日常任务");
            } else if (position == 0 && !BEGINNERTASK && DAILYTASK) {//下标为0并且新手任务不存在切每日任务存在返回每日任务
                titleViewHolder.title.setText("日常任务");
            }
        } else if (holder instanceof TaskViewHolder) {
            TaskViewHolder taskViewHolder = (TaskViewHolder) holder;
            TaskBean.TasklistBean tasklist = list.get(0).getTasklist();
            if (tasklist == null) return;
            if (BEGINNERTASK && position < beginnerpos + 1) {
                TaskBean.TasklistBean.BeginnertaskBean beginnertaskBean = tasklist.getBeginnertask().get(position - 1);
                setData(taskViewHolder, beginnertaskBean.getRewardnum(), beginnertaskBean.getTitle(), beginnertaskBean.getMemo(), beginnertaskBean.getTasktype());
            } else if (BEGINNERTASK && DAILYTASK && position > beginnerpos + 1) {
                TaskBean.TasklistBean.DailytaskBean dailytaskBean = tasklist.getDailytask().get(position - (beginnerpos + 2));
                setData(taskViewHolder, dailytaskBean.getRewardnum(), dailytaskBean.getTitle(), dailytaskBean.getMemo(), dailytaskBean.getTasktype());
            } else if (!BEGINNERTASK && DAILYTASK) {
                TaskBean.TasklistBean.DailytaskBean dailytaskBean = tasklist.getDailytask().get(position - 1);
                setData(taskViewHolder, dailytaskBean.getRewardnum(), dailytaskBean.getTitle(), dailytaskBean.getMemo(), dailytaskBean.getTasktype());
            }

        }

    }

    public void setData(final TaskViewHolder taskViewHolder, String gold_num, String title, String memo, final int taskType) {
        taskViewHolder.task_gold.setText("+" + gold_num + "金币");
        taskViewHolder.task_name.setText(title);
        taskViewHolder.task_detail.setText(memo);
        taskViewHolder.task_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taskViewHolder.more_layout.getVisibility() == View.GONE) {
                    taskViewHolder.more_layout.setVisibility(View.VISIBLE);
                    taskViewHolder.arrow_img.setImageResource(R.drawable.book_reader_top_arrow_image);
                } else if (taskViewHolder.more_layout.getVisibility() == View.VISIBLE) {
                    taskViewHolder.more_layout.setVisibility(View.GONE);
                    taskViewHolder.arrow_img.setImageResource(R.drawable.book_reader_down_arrow_image);
                }
            }
        });
        switch (taskType) {
            case 1://绑定手机号
                gotoText = "去绑定";
                break;
            case 2://提现
                gotoText = "去提现";
                break;
            case 3://首次收徒
                gotoText = "去收徒";
                break;
            case 4://新手阅读
                gotoText = "去阅读";
                break;
            case 5://首次充值
                gotoText = "去充值";
                break;
            case 6://输入邀请码
                gotoText = "去填写";
                break;
            case 7://QQ收徒
//                gotoText = "去收徒";
                taskViewHolder.goto_finish_task.setVisibility(View.GONE);
                break;
            case 8://朋友圈收徒
//                gotoText = "去收徒";
                taskViewHolder.goto_finish_task.setVisibility(View.GONE);
                break;
            case 9://微信收徒
//                gotoText = "去收徒";
                taskViewHolder.goto_finish_task.setVisibility(View.GONE);
                break;
            case 10://评论
                gotoText = "去评论";
                break;
            case 11://小说分享
                gotoText = "去分享";
                break;
        }
        if (!TextUtils.isEmpty(gotoText)) taskViewHolder.goto_finish_task.setText(gotoText);
        //1:绑定手机号,2:提现,3:首次收徒 4:新手阅读 5:首次充值 6:输入邀请码 7：QQ收徒 8：朋友圈收徒 9：微信收徒 10:评论 11:小说分享
        taskViewHolder.goto_finish_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (taskType) {
                    case 1://绑定手机号
                        ActivityUtil.toBindPhoneActivity(context);
                        break;
                    case 2://提现
                        ActivityUtil.toWithdrawalsActivity(context);
                        break;
                    case 3://首次收徒
                        break;
                    case 4://新手阅读
                        gotoHomePage();
                        break;
                    case 5://首次充值
                        ActivityUtil.toRechargeActivity(context);
                        break;
                    case 6://输入邀请码
                        new InvitationCodeDialog(context);
                        break;
                    case 7://QQ收徒
                        break;
                    case 8://朋友圈收徒
                        break;
                    case 9://微信收徒
                        break;
                    case 10://评论
                        gotoHomePage();
                        break;
                    case 11://小说分享
                        gotoHomePage();
                        break;
                }
            }
        });
    }

    public void gotoHomePage() {
        if (ListenerManager.getInstance().getGotoHomePage() != null) {
            ListenerManager.getInstance().getGotoHomePage().gotoPage();
            context.finish();
        }

    }

    @Override
    public int getItemCount() {
        num = 0;
        BEGINNERTASK = false;
        DAILYTASK = false;
        beginnerpos = 0;
        dailypos = 0;
        if (list == null) return num;
        if (list.size() == 0) return num;
        TaskBean.TasklistBean tasklist = list.get(0).getTasklist();
        if (tasklist != null) {
            if (tasklist.getBeginnertask() != null && tasklist.getBeginnertask().size() != 0) {
                beginnerpos = tasklist.getBeginnertask().size();
                num = num + beginnerpos + 1;
                BEGINNERTASK = true;
            }
            if (tasklist.getDailytask() != null && tasklist.getDailytask().size() != 0) {
                dailypos = tasklist.getDailytask().size();
                num = num + dailypos + 1;
                DAILYTASK = true;
            }

        }
        return num;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {//下标为0返回标题
            return TITLEPOS;
        } else if (BEGINNERTASK && DAILYTASK && position == (beginnerpos + 1)) {//新手和每日任务共同存在返回第二个标题
            return TITLEPOS;
        }
        return DETAILSPOS;
    }

    private class TitleViewHolder extends RecyclerView.ViewHolder {
        private TextView title;

        public TitleViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.task_title);
        }
    }

    private class TaskViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout task_item, more_layout;
        private TextView task_name, task_gold, task_detail, goto_finish_task;
        private ImageView arrow_img;

        public TaskViewHolder(View itemView) {
            super(itemView);
            task_item = (LinearLayout) itemView.findViewById(R.id.task_item);
            more_layout = (LinearLayout) itemView.findViewById(R.id.more_layout);
            task_name = (TextView) itemView.findViewById(R.id.task_name);
            task_gold = (TextView) itemView.findViewById(R.id.task_gold);
            task_detail = (TextView) itemView.findViewById(R.id.task_detail);
            goto_finish_task = (TextView) itemView.findViewById(R.id.goto_finish_task);
            arrow_img = (ImageView) itemView.findViewById(R.id.arrow_img);
        }
    }
}
