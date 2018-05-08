package com.spriteapp.booklibrary.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.api.BookApi;
import com.spriteapp.booklibrary.base.Base;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.BookCommentBean;
import com.spriteapp.booklibrary.model.BookRepyBean;
import com.spriteapp.booklibrary.model.UserBean;
import com.spriteapp.booklibrary.ui.activity.SquareDetailsActivity;
import com.spriteapp.booklibrary.ui.dialog.CommentDialog;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.TimeUtil;
import com.spriteapp.booklibrary.util.ToastUtil;
import com.spriteapp.booklibrary.util.Util;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by userfirst on 2018/5/3.
 */

public class NewBookCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int COMMENTPOS = 0;
    private final int NODATAPOS = 1;
    private Activity context;
    private List<BookCommentBean> list;
    private CommentDialog commentDialog;

    public NewBookCommentAdapter(Activity context, List<BookCommentBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = null;
        if (viewType == COMMENTPOS) {
            convertView = LayoutInflater.from(context).inflate(R.layout.new_book_comment_item, parent, false);
            return new CommentViewHolder(convertView);
        } else if (viewType == NODATAPOS) {
            convertView = LayoutInflater.from(context).inflate(R.layout.follow_null_layout, parent, false);
            return new NoDataViewHolder(convertView);
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.new_book_comment_item, parent, false);
            return new CommentViewHolder(convertView);
        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof CommentViewHolder) {
            final BookCommentBean bookCommentBean = list.get(position);
            if (bookCommentBean == null) return;
            CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
            //设置数据到布局item
            GlideUtils.loadImage(commentViewHolder.user_head, bookCommentBean.getUser_avatar(), context);
            commentViewHolder.user_name.setText(bookCommentBean.getUser_nickname());
            Util.getSystem(bookCommentBean.getSource(), commentViewHolder.phone_system);
            commentViewHolder.comment_time.setText(TimeUtil.getTimeFormatText(Long.parseLong(bookCommentBean.getComment_replydatetime() + "000")));
            commentViewHolder.user_comment.setText(bookCommentBean.getComment_content());
            commentViewHolder.user_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    replyComment(bookCommentBean, position);
                }
            });
            if (bookCommentBean.getChildren() == null) {
                commentViewHolder.reply_layout.setVisibility(View.GONE);
                return;
            }


            final int size = bookCommentBean.getChildren().size();
            if (bookCommentBean.getReply_count() != 0)
                commentViewHolder.comment_reply_num.setText(bookCommentBean.getReply_count() + "条回复");
            commentViewHolder.comment_reply_num.setVisibility(View.VISIBLE);
            switch (size) {
                case 0:
                    commentViewHolder.reply_layout.setVisibility(View.GONE);
                    commentViewHolder.comment_reply_num.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    commentViewHolder.reply_layout.setVisibility(View.VISIBLE);
                    commentViewHolder.comment1.setVisibility(View.VISIBLE);
                    commentViewHolder.comment2.setVisibility(View.GONE);
                    commentViewHolder.comment3.setVisibility(View.GONE);
                    commentViewHolder.line.setVisibility(View.GONE);
                    commentViewHolder.goto_comment_details.setVisibility(View.GONE);
                    commentViewHolder.comment1.setText(bookCommentBean.getChildren().get(0).getReplyUserName() + ":" + bookCommentBean.getChildren().get(0).getReplyContent());
                    Util.setTextStrColor(commentViewHolder.comment1, commentViewHolder.comment1.getText().toString(), bookCommentBean.getChildren().get(0).getReplyContent());
                    break;
                case 2:
                    commentViewHolder.reply_layout.setVisibility(View.VISIBLE);
                    commentViewHolder.comment1.setVisibility(View.VISIBLE);
                    commentViewHolder.comment2.setVisibility(View.VISIBLE);
                    commentViewHolder.comment3.setVisibility(View.GONE);
                    commentViewHolder.line.setVisibility(View.GONE);
                    commentViewHolder.goto_comment_details.setVisibility(View.GONE);
                    commentViewHolder.comment1.setText(bookCommentBean.getChildren().get(0).getReplyUserName() + ":" + bookCommentBean.getChildren().get(0).getReplyContent());
                    commentViewHolder.comment2.setText(bookCommentBean.getChildren().get(1).getReplyUserName() + ":" + bookCommentBean.getChildren().get(1).getReplyContent());
                    Util.setTextStrColor(commentViewHolder.comment1, commentViewHolder.comment1.getText().toString(), bookCommentBean.getChildren().get(0).getReplyContent());
                    Util.setTextStrColor(commentViewHolder.comment2, commentViewHolder.comment2.getText().toString(), bookCommentBean.getChildren().get(1).getReplyContent());
                    break;
//                case 3:
//                    commentViewHolder.reply_layout.setVisibility(View.VISIBLE);
//                    commentViewHolder.comment1.setVisibility(View.VISIBLE);
//                    commentViewHolder.comment2.setVisibility(View.VISIBLE);
//                    commentViewHolder.comment3.setVisibility(View.VISIBLE);
//                    commentViewHolder.line.setVisibility(View.GONE);
//                    commentViewHolder.goto_comment_details.setVisibility(View.GONE);
//                    commentViewHolder.comment1.setText(bookCommentBean.getChildren().get(0).getReplyUserName() + ":" + bookCommentBean.getChildren().get(0).getReplyContent());
//                    commentViewHolder.comment2.setText(bookCommentBean.getChildren().get(1).getReplyUserName() + ":" + bookCommentBean.getChildren().get(1).getReplyContent());
//                    commentViewHolder.comment3.setText(bookCommentBean.getChildren().get(2).getReplyUserName() + ":" + bookCommentBean.getChildren().get(2).getReplyContent());
//                    Util.setTextStrColor(commentViewHolder.comment1, commentViewHolder.comment1.getText().toString(), bookCommentBean.getChildren().get(0).getReplyContent());
//                    Util.setTextStrColor(commentViewHolder.comment2, commentViewHolder.comment2.getText().toString(), bookCommentBean.getChildren().get(1).getReplyContent());
//                    Util.setTextStrColor(commentViewHolder.comment3, commentViewHolder.comment3.getText().toString(), bookCommentBean.getChildren().get(2).getReplyContent());
//                    break;
                default:
                    commentViewHolder.reply_layout.setVisibility(View.VISIBLE);
                    commentViewHolder.comment1.setVisibility(View.VISIBLE);
                    commentViewHolder.comment2.setVisibility(View.VISIBLE);
                    commentViewHolder.comment3.setVisibility(View.VISIBLE);
                    commentViewHolder.line.setVisibility(View.VISIBLE);
                    commentViewHolder.goto_comment_details.setVisibility(View.VISIBLE);
                    commentViewHolder.comment1.setText(bookCommentBean.getChildren().get(0).getReplyUserName() + ":" + bookCommentBean.getChildren().get(0).getReplyContent());
                    commentViewHolder.comment2.setText(bookCommentBean.getChildren().get(1).getReplyUserName() + ":" + bookCommentBean.getChildren().get(1).getReplyContent());
                    commentViewHolder.comment3.setText(bookCommentBean.getChildren().get(2).getReplyUserName() + ":" + bookCommentBean.getChildren().get(2).getReplyContent());
                    Util.setTextStrColor(commentViewHolder.comment1, commentViewHolder.comment1.getText().toString(), bookCommentBean.getChildren().get(0).getReplyContent());
                    Util.setTextStrColor(commentViewHolder.comment2, commentViewHolder.comment2.getText().toString(), bookCommentBean.getChildren().get(1).getReplyContent());
                    Util.setTextStrColor(commentViewHolder.comment3, commentViewHolder.comment3.getText().toString(), bookCommentBean.getChildren().get(2).getReplyContent());
                    commentViewHolder.goto_comment_details.setText("…共用" + bookCommentBean.getReply_count() + "条回复");
                    commentViewHolder.goto_comment_details.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityUtil.toCommentReplyActivity(context, bookCommentBean.getReply_count(), bookCommentBean.getComment_id(), bookCommentBean.getBook_id(), bookCommentBean.getUser_id(), 2);
                        }
                    });
                    break;
            }

        } else if (holder instanceof NoDataViewHolder) {

        }
    }

    private void replyComment(final BookCommentBean bean, final int pos) {
        if (!AppUtil.isLogin(context)) return;
        if (bean == null) return;
        if (commentDialog == null) commentDialog = new CommentDialog(context);
        if (commentDialog.isShowing()) commentDialog.dismiss();
        commentDialog.show();
        commentDialog.setUserName("回复:" + bean.getUser_nickname());
        commentDialog.getEditText().setHint("请输入回复内容");
        commentDialog.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendBtn(bean, pos);
                }
                return true;
            }
        });
        commentDialog.getSendText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//发送按钮
                sendBtn(bean, pos);
            }
        });

    }

    private void sendBtn(BookCommentBean bean, int pos) {
        String content = commentDialog.getContent();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showToast("请输入内容");
            return;
        }
        sendReplyComment(bean, content, pos);
    }

    private void sendReplyComment(BookCommentBean bean, final String content, final int pos) {
        BookApi.getInstance()
                .service
                .book_reply(bean.getBook_id(), bean.getComment_id(), content)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Base<List<BookCommentBean>>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Base<List<BookCommentBean>> listBase) {
                if (listBase != null) {
                    int resultCode = listBase.getCode();
                    if (resultCode == ApiCodeEnum.SUCCESS.getValue()) {//成功

                        if (list.size() > pos) {
                            BookRepyBean bookRepyBean = new BookRepyBean();
                            bookRepyBean.setReplyUserName(UserBean.getInstance().getUser_nickname());
                            bookRepyBean.setReplyContent(content);
                            list.get(pos).getChildren().add(0, bookRepyBean);
                            list.get(pos).setReply_count(list.get(pos).getReply_count() + 1);
                            notifyDataSetChanged();
                        }
                        if (commentDialog != null) {
                            commentDialog.clearText();
                            commentDialog.dismiss();
                        }
                        ToastUtil.showToast("回复成功");
                    } else {
                        ToastUtil.showToast(listBase.getMessage());
                    }

                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size() == 0 ? 1 : list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list != null && list.size() == 0) return NODATAPOS;
        return COMMENTPOS;
    }

    private class CommentViewHolder extends RecyclerView.ViewHolder {
        private ImageView user_head;
        private TextView user_name, user_comment, phone_system, comment_time, comment_reply_num,
                support_num, comment1, comment2, comment3, goto_comment_details;
        private View line;
        private LinearLayout reply_layout;

        public CommentViewHolder(View itemView) {
            super(itemView);
            user_head = (ImageView) itemView.findViewById(R.id.user_head);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            user_comment = (TextView) itemView.findViewById(R.id.user_comment);
            phone_system = (TextView) itemView.findViewById(R.id.phone_system);
            comment_time = (TextView) itemView.findViewById(R.id.comment_time);
            comment_reply_num = (TextView) itemView.findViewById(R.id.comment_reply_num);
            support_num = (TextView) itemView.findViewById(R.id.support_num);
            comment1 = (TextView) itemView.findViewById(R.id.comment1);
            comment2 = (TextView) itemView.findViewById(R.id.comment2);
            comment3 = (TextView) itemView.findViewById(R.id.comment3);
            goto_comment_details = (TextView) itemView.findViewById(R.id.goto_comment_details);
            line = itemView.findViewById(R.id.line);
            reply_layout = (LinearLayout) itemView.findViewById(R.id.reply_layout);
        }
    }

    private class NoDataViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout null_layout;
        private TextView miaoshu;

        public NoDataViewHolder(View itemView) {
            super(itemView);
            null_layout = (LinearLayout) itemView.findViewById(R.id.null_layout);
            miaoshu = (TextView) itemView.findViewById(R.id.miaoshu);
            miaoshu.setText("暂时没有评论");
        }
    }
}
