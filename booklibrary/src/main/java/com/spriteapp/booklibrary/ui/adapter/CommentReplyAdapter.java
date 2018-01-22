package com.spriteapp.booklibrary.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
 * Created by Administrator on 2018/1/19.
 */

public class CommentReplyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int MAINCOMMENT = 0;
    private static final int TITLECOMMENT = 1;
    private static final int REPLYCOMMENT = 2;
    private Activity context;
    private List<CommentReplyBean> replyBean;

    public CommentReplyAdapter(Activity context, List<CommentReplyBean> replyBean) {
        this.context = context;
        this.replyBean = replyBean;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = null;
        if (viewType == MAINCOMMENT) {//评论
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_reply_layout, parent, false);
            return new CommentViewHolder(convertView);
        } else if (viewType == TITLECOMMENT) {
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_reply_title, parent, false);
            return new TitleViewHolder(convertView);
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_reply_layout, parent, false);
            return new CommentViewHolder(convertView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommentViewHolder) {
            CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
            if (position == 0) {
                CommentReply data = replyBean.get(0).getData().get(0);
                setData(commentViewHolder, data);
            } else if (position >= 2) {
                CommentReply reply = replyBean.get(0).getReplydata().getData().get(position - 2);
                setData(commentViewHolder, reply);

            }

        } else if (holder instanceof TitleViewHolder) {

        }


    }

    public void setData(CommentViewHolder commentViewHolder, CommentReply reply) {
        GlideUtils.loadImage(commentViewHolder.user_head, reply.getUser_avatar(), context);
        commentViewHolder.user_name.setText(reply.getUsername());
        commentViewHolder.send_time.setText(TimeUtil.getTimeFormatText(Long.parseLong(reply.getAddtime() + "000")));
        commentViewHolder.support_num.setText(Util.getFloat(reply.getSupportnum()));
        commentViewHolder.user_speak.setText(reply.getContent());
        toSupport(commentViewHolder.support_num,reply);
    }

    @Override
    public int getItemCount() {
        if (replyBean != null && replyBean.size() != 0 && replyBean.get(0).getData() != null &&
                replyBean.get(0).getReplydata().getData() != null && replyBean.get(0).getReplydata().getData().size() != 0) {
            return replyBean.get(0).getReplydata().getData().size() + 2;
        } else {
            Log.d("getItemCount", "getItemCount===" + 0);
            return 0;
        }

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


    @Override
    public int getItemViewType(int position) {//定义类型
        if (position == 1)
            return TITLECOMMENT;
        else
            return MAINCOMMENT;
    }

    public class TitleViewHolder extends RecyclerView.ViewHolder {

        public TitleViewHolder(View itemView) {
            super(itemView);
        }
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
}