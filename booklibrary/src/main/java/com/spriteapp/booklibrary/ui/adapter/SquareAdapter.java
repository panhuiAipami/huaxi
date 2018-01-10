package com.spriteapp.booklibrary.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.model.SquareBean;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.TimeUtil;
import com.spriteapp.booklibrary.util.ToastUtil;
import com.spriteapp.booklibrary.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/6.
 */

public class SquareAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int IMAGE0 = 0;//没有图片
    private final int IMAGE1 = 1;//一张图片
    private final int IMAGE2 = 2;//两张图片
    private final int IMAGE3 = 3;//三张及以上图片
    private Activity context;
    private List<SquareBean> list;
    PopupWindow popupWindow;

    public SquareAdapter(Activity context, List<SquareBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = null;
        if (viewType == IMAGE0) {//左边文字
            convertView = LayoutInflater.from(context).inflate(R.layout.follow_item_layout, parent, false);
            return new SquareViewHolder(convertView);
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.follow_item_layout, parent, false);
            return new SquareViewHolder(convertView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SquareViewHolder) {
            SquareBean squareBean = list.get(position);
            if (squareBean == null) return;
            SquareViewHolder viewHolder = (SquareViewHolder) holder;
            if (squareBean.getPic_url() != null) {
                if (position == list.size() - 1) viewHolder.line.setVisibility(View.GONE);
                if (squareBean.getPic_url().size() == 1) {//一张图片
                    viewHolder.image2.setVisibility(View.GONE);
                    viewHolder.image_recyclerview.setVisibility(View.GONE);
                    viewHolder.image1.setVisibility(View.VISIBLE);
                    GlideUtils.loadImage(viewHolder.image1, squareBean.getPic_url().get(0), context);
                } else if (squareBean.getPic_url().size() == 2) {//两张图片
                    viewHolder.image2.setVisibility(View.GONE);
                    viewHolder.image_recyclerview.setVisibility(View.GONE);
                    viewHolder.image1.setVisibility(View.VISIBLE);
                    GlideUtils.loadImage(viewHolder.image1, squareBean.getPic_url().get(0), context);
                    GlideUtils.loadImage(viewHolder.image2, squareBean.getPic_url().get(1), context);
                } else if (squareBean.getPic_url().size() > 2) {//两张图片以上
                    viewHolder.image1.setVisibility(View.GONE);
                    viewHolder.image2.setVisibility(View.GONE);
                    viewHolder.image_recyclerview.setVisibility(View.VISIBLE);
                    viewHolder.image_recyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    viewHolder.image_recyclerview.setAdapter(new SquareImageAdapter(context, squareBean.getPic_url()));//嵌套列表
                } else {//没有图片
                    viewHolder.image1.setVisibility(View.GONE);
                    viewHolder.image2.setVisibility(View.GONE);
                    viewHolder.image_recyclerview.setVisibility(View.GONE);
                }
            }
            GlideUtils.loadImage(viewHolder.user_head, squareBean.getUser_avatar(), context);
            viewHolder.user_name.setText(squareBean.getUsername());
            viewHolder.send_time.setText(TimeUtil.getTimeFormatText(Long.parseLong(squareBean.getAddtime() + "000")) + "  来自" + squareBean.getLocation());
            viewHolder.user_speak.setText(squareBean.getSubject());
            viewHolder.read_num.setText(Util.getFloat(squareBean.getReadnum()) + "次预览");
            viewHolder.comment_num.setText(Util.getFloat(squareBean.getCommentnum()));
            viewHolder.support_num.setText(Util.getFloat(squareBean.getSupportnum()));
            itemClick(viewHolder, squareBean);//item点击
            moreImageClick(viewHolder.more);//更多点击
            if (squareBean.getReadhistory() != null) {
                List<ImageView> imageView = new ArrayList<>();
                imageView.add(viewHolder.head1);
                imageView.add(viewHolder.head2);
                imageView.add(viewHolder.head3);
                if (squareBean.getReadhistory().size() == 1) {//一个人浏览
                    viewHolder.head1.setVisibility(View.VISIBLE);
                    viewHolder.head2.setVisibility(View.GONE);
                    viewHolder.head3.setVisibility(View.GONE);
                    GlideUtils.loadImage(viewHolder.head1, squareBean.getReadhistory().get(0).getUser_avatar(), context);
                } else if (squareBean.getReadhistory().size() == 2) {//两个人浏览
                    viewHolder.head1.setVisibility(View.VISIBLE);
                    viewHolder.head2.setVisibility(View.VISIBLE);
                    viewHolder.head3.setVisibility(View.GONE);
                    GlideUtils.loadImage(viewHolder.head1, squareBean.getReadhistory().get(0).getUser_avatar(), context);
                    GlideUtils.loadImage(viewHolder.head2, squareBean.getReadhistory().get(1).getUser_avatar(), context);
                } else if (squareBean.getReadhistory().size() > 2) {//两个人以上浏览
                    viewHolder.head1.setVisibility(View.VISIBLE);
                    viewHolder.head2.setVisibility(View.VISIBLE);
                    viewHolder.head3.setVisibility(View.VISIBLE);
                    GlideUtils.loadImage(viewHolder.head1, squareBean.getReadhistory().get(0).getUser_avatar(), context);
                    GlideUtils.loadImage(viewHolder.head2, squareBean.getReadhistory().get(1).getUser_avatar(), context);
                    GlideUtils.loadImage(viewHolder.head3, squareBean.getReadhistory().get(2).getUser_avatar(), context);
                } else {//没有人浏览
                    viewHolder.head1.setVisibility(View.GONE);
                    viewHolder.head2.setVisibility(View.GONE);
                    viewHolder.head3.setVisibility(View.GONE);
                }
            }
            if (squareBean.getComments() != null) {
                if (squareBean.getComments().size() == 1) {//一条评论
                    viewHolder.comment_layout.setVisibility(View.VISIBLE);
                    viewHolder.comment1.setVisibility(View.VISIBLE);
                    viewHolder.comment2.setVisibility(View.GONE);
                    viewHolder.comment1.setText(squareBean.getComments().get(0).getUsername() + ":" + squareBean.getComments().get(0).getContent());
                } else if (squareBean.getComments().size() >= 2) {//两条及以上评论
                    viewHolder.comment_layout.setVisibility(View.VISIBLE);
                    viewHolder.comment1.setVisibility(View.VISIBLE);
                    viewHolder.comment2.setVisibility(View.VISIBLE);
                    viewHolder.comment1.setText(squareBean.getComments().get(0).getUsername() + ":" + squareBean.getComments().get(0).getContent());
                    viewHolder.comment2.setText(squareBean.getComments().get(1).getUsername() + ":" + squareBean.getComments().get(1).getContent());
                } else {//无评论
                    viewHolder.comment_layout.setVisibility(View.GONE);
                    viewHolder.comment1.setVisibility(View.GONE);
                    viewHolder.comment2.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * @param holder SquareViewHolder
     * @param bean   SquareBean
     */
    public void itemClick(SquareViewHolder holder, final SquareBean bean) {
        if (bean == null) return;
        holder.item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//帖子id
                Log.d("square_id", bean.getId() + "");
                ActivityUtil.toSquareDetailsActivity(context, bean.getId());
            }
        });
    }

    /**
     * @param more ImageView
     */
    public void moreImageClick(final ImageView more) {
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpopWindow(more);//更多操作
            }
        });
    }

    public void showpopWindow(View v) {
        View layout = View.inflate(context, R.layout.square_pop_layout, null);
        popupWindow = new PopupWindow(layout, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(ContextCompat.getColor(context, R.color.pop_back));
        popupWindow.setBackgroundDrawable(dw);
        popupWindow.showAsDropDown(v, 30, 0, Gravity.LEFT);
        viewClick(layout);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }

    public void viewClick(View item) {
        TextView guanzhu, jubao, pingbi;
        guanzhu = (TextView) item.findViewById(R.id.guanzhu);
        jubao = (TextView) item.findViewById(R.id.jubao);
        pingbi = (TextView) item.findViewById(R.id.pingbi);

        guanzhu.setOnClickListener(new View.OnClickListener() {//关注
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                ToastUtil.showToast("关注");


            }
        });
        pingbi.setOnClickListener(new View.OnClickListener() {//屏蔽
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                ToastUtil.showToast("屏蔽");
            }
        });
        jubao.setOnClickListener(new View.OnClickListener() {//举报
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                ToastUtil.showToast("举报");
            }
        });

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return IMAGE0;
    }

    public class SquareViewHolder extends RecyclerView.ViewHolder {
        private ImageView user_head, more, image1, image2;
        private TextView user_name, send_time, user_speak, read_num, support_num, comment_num;
        private RecyclerView image_recyclerview;
        private LinearLayout image_layout, comment_layout, item_layout;
        private ImageView head1, head2, head3;
        private TextView comment1, comment2;
        private View line;

        public SquareViewHolder(View itemView) {
            super(itemView);
            //图片
            user_head = (ImageView) itemView.findViewById(R.id.user_head);
            head1 = (ImageView) itemView.findViewById(R.id.head1);
            head2 = (ImageView) itemView.findViewById(R.id.head2);
            head3 = (ImageView) itemView.findViewById(R.id.head3);
            more = (ImageView) itemView.findViewById(R.id.more);
            image1 = (ImageView) itemView.findViewById(R.id.image1);
            image2 = (ImageView) itemView.findViewById(R.id.image2);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            send_time = (TextView) itemView.findViewById(R.id.send_time);
            user_speak = (TextView) itemView.findViewById(R.id.user_speak);
            read_num = (TextView) itemView.findViewById(R.id.read_num);
            support_num = (TextView) itemView.findViewById(R.id.support_num);
            comment_num = (TextView) itemView.findViewById(R.id.comment_num);
            image_recyclerview = (RecyclerView) itemView.findViewById(R.id.image_recyclerview);
            image_layout = (LinearLayout) itemView.findViewById(R.id.image_layout);
            comment_layout = (LinearLayout) itemView.findViewById(R.id.comment_layout);
            item_layout = (LinearLayout) itemView.findViewById(R.id.item_layout);
            comment1 = (TextView) itemView.findViewById(R.id.comment1);
            comment2 = (TextView) itemView.findViewById(R.id.comment2);
            line = itemView.findViewById(R.id.line);
        }
    }
}
