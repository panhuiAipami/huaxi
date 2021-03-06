package com.spriteapp.booklibrary.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.CommentReply;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.TimeUtil;
import com.spriteapp.booklibrary.util.ToastUtil;
import com.spriteapp.booklibrary.util.Util;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.spriteapp.booklibrary.ui.activity.SquareDetailsActivity.PLATFORM_ID;

/**
 * Created by Administrator on 2018/1/11.
 */

public class CommentDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int COMMENTPOS = 0;
    private Activity context;
    private List<CommentReply> list;
    OnItemClickListener onItemClickListener;

    public CommentDetailsAdapter(Activity context, List<CommentReply> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = null;
        if (viewType == COMMENTPOS) {//评论
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_item_layout, parent, false);
            return new CommentViewHolder(convertView);
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_item_layout, parent, false);
            return new CommentViewHolder(convertView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof CommentViewHolder) {
            final CommentReply commentReply = list.get(position);
            if (commentReply == null) return;
            CommentViewHolder commentViewHolder = (CommentDetailsAdapter.CommentViewHolder) holder;
            GlideUtils.loadImage(commentViewHolder.user_head, commentReply.getUser_avatar(), context);
            commentViewHolder.user_name.setText(commentReply.getUsername());
            commentViewHolder.user_speak.setText(commentReply.getContent());
            commentViewHolder.send_time.setText(TimeUtil.getTimeFormatText(Long.parseLong(commentReply.getAddtime() + "000")));
            commentViewHolder.support_num.setText(Util.getFloat(commentReply.getSupportnum()));
            boolean isSuppor;
            if (commentReply.getIs_support() == 1)
                isSuppor = false;
            else
                isSuppor = true;
            commentViewHolder.support_num.setEnabled(isSuppor);
            if (commentReply.getReplay() != null && commentReply.getReplay().getData() != null && commentReply.getReplay().getData().size() != 0) {//此条评论有回复

                if (commentReply.getReplay().getTotal() == 1) {
                    commentViewHolder.comment_layout.setVisibility(View.VISIBLE);
                    commentViewHolder.reply_comment1.setVisibility(View.VISIBLE);
                    commentViewHolder.reply_comment2.setVisibility(View.GONE);
                    commentViewHolder.more_reply.setVisibility(View.GONE);
                    commentViewHolder.reply_comment1.setText(commentReply.getReplay().getData().get(0).getUsername() + ":" + commentReply.getReplay().getData().get(0).getContent());
                    Util.setTextStrColor(commentViewHolder.reply_comment1, commentViewHolder.reply_comment1.getText().toString(), commentReply.getReplay().getData().get(0).getContent());
                } else if (commentReply.getReplay().getTotal() == 2) {
                    commentViewHolder.comment_layout.setVisibility(View.VISIBLE);
                    commentViewHolder.reply_comment1.setVisibility(View.VISIBLE);
                    commentViewHolder.reply_comment2.setVisibility(View.VISIBLE);
                    commentViewHolder.more_reply.setVisibility(View.GONE);
                    commentViewHolder.reply_comment1.setText(commentReply.getReplay().getData().get(0).getUsername() + ":" + commentReply.getReplay().getData().get(0).getContent());
                    commentViewHolder.reply_comment2.setText(commentReply.getReplay().getData().get(1).getUsername() + ":" + commentReply.getReplay().getData().get(1).getContent());
                    Util.setTextStrColor(commentViewHolder.reply_comment1, commentViewHolder.reply_comment1.getText().toString(), commentReply.getReplay().getData().get(0).getContent());
                    Util.setTextStrColor(commentViewHolder.reply_comment2, commentViewHolder.reply_comment2.getText().toString(), commentReply.getReplay().getData().get(1).getContent());
                } else if (commentReply.getReplay().getTotal() > 2) {
                    commentViewHolder.comment_layout.setVisibility(View.VISIBLE);
                    commentViewHolder.reply_comment1.setVisibility(View.VISIBLE);
                    commentViewHolder.reply_comment2.setVisibility(View.VISIBLE);
                    commentViewHolder.more_reply.setVisibility(View.VISIBLE);
                    commentViewHolder.reply_comment1.setText(commentReply.getReplay().getData().get(0).getUsername() + ":" + commentReply.getReplay().getData().get(0).getContent());
                    commentViewHolder.reply_comment2.setText(commentReply.getReplay().getData().get(1).getUsername() + ":" + commentReply.getReplay().getData().get(1).getContent());
                    Util.setTextStrColor(commentViewHolder.reply_comment1, commentViewHolder.reply_comment1.getText().toString(), commentReply.getReplay().getData().get(0).getContent());
                    Util.setTextStrColor(commentViewHolder.reply_comment2, commentViewHolder.reply_comment2.getText().toString(), commentReply.getReplay().getData().get(1).getContent());
                    commentViewHolder.more_reply.setText("...共" + commentReply.getReplay().getTotal() + "条回复");
                    moreClick(commentViewHolder.more_reply, commentReply);//查看更多回复
                }
            } else {
                commentViewHolder.comment_layout.setVisibility(View.GONE);
                commentViewHolder.reply_comment1.setVisibility(View.GONE);
                commentViewHolder.reply_comment2.setVisibility(View.GONE);
                commentViewHolder.more_reply.setVisibility(View.GONE);
            }
            toSupport(commentViewHolder.support_num, commentReply);//评论点赞
            toCommentNo(commentViewHolder.comment_layout);//评论点赞
            if (onItemClickListener != null) {
                commentViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.setOnItemClickListener(position, commentReply);
                    }
                });
            }
        }
    }

    /**
     * @param view 点击事件布局  点击回复不弹出CommentDialog
     */
    public void toCommentNo(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * @param view  点赞布局
     * @param reply 回复实体类 对评论点赞
     */
    public void toSupport(final TextView view, final CommentReply reply) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportHttp(view, reply);
            }
        });

    }

    /**
     * @param view  点赞布局
     * @param reply 回复实体类 点赞网络请求
     */
    public void supportHttp(final TextView view, final CommentReply reply) {
        if (!AppUtil.isLogin(context)) {
            return;
        }
        BookApi.getInstance()
                .service
                .square_addcontentsupport(reply.getId(), PLATFORM_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Base squareBeanBase) {
                        int resultCode = squareBeanBase.getCode();
                        if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功
                            ToastUtil.showToast("点赞成功");
                            view.setEnabled(false);//图标颜色
                            reply.setSupportnum(reply.getSupportnum() + 1);//改变数量
                            view.setText(reply.getSupportnum() + "");
                        } else if (resultCode == ApiCodeEnum.FAILURE.getValue()) {//失败
                            ToastUtil.showToast("点赞失败");
                        } else if (resultCode == ApiCodeEnum.EVER.getValue()) {//曾经点过
                            ToastUtil.showToast("您已经点过");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * @param view  更多按钮布局
     * @param reply 回复实体类 显示更多pop
     */
    public void moreClick(View view, final CommentReply reply) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.toCommentReplyActivity(context, reply.getReplay().getTotal(), reply.getId(), reply.getSquareid(),reply.getReplyto());
            }
        });
    }

    @Override
    public int getItemCount() {//类表长度
        return list == null ? 0 : list.size();
    }

    @Override
    public int getItemViewType(int position) {//定义类型
        return COMMENTPOS;
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        private ImageView user_head;
        private LinearLayout comment_layout;
        private TextView user_name, user_label, build_num, send_time, support_num;
        private TextView user_speak, reply_comment1, reply_comment2, more_reply;

        public CommentViewHolder(View itemView) {
            super(itemView);
            user_head = (ImageView) itemView.findViewById(R.id.user_head);
            comment_layout = (LinearLayout) itemView.findViewById(R.id.comment_layout);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            user_label = (TextView) itemView.findViewById(R.id.user_label);
            build_num = (TextView) itemView.findViewById(R.id.build_num);
            send_time = (TextView) itemView.findViewById(R.id.send_time);
            support_num = (TextView) itemView.findViewById(R.id.support_num);
            user_speak = (TextView) itemView.findViewById(R.id.user_speak);
            reply_comment1 = (TextView) itemView.findViewById(R.id.reply_comment1);
            reply_comment2 = (TextView) itemView.findViewById(R.id.reply_comment2);
            more_reply = (TextView) itemView.findViewById(R.id.more_reply);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void setOnItemClickListener(int postion, CommentReply commentReply);
    }
}
