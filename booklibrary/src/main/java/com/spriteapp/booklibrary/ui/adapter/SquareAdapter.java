package com.spriteapp.booklibrary.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
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
import com.spriteapp.booklibrary.base.BaseActivity;
import com.spriteapp.booklibrary.enumeration.ApiCodeEnum;
import com.spriteapp.booklibrary.model.CommentBean;
import com.spriteapp.booklibrary.model.SquareBean;
import com.spriteapp.booklibrary.model.UserBean;
import com.spriteapp.booklibrary.ui.dialog.CommentDialog;
import com.spriteapp.booklibrary.ui.dialog.FollowPop;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.TimeUtil;
import com.spriteapp.booklibrary.util.ToastUtil;
import com.spriteapp.booklibrary.util.Util;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.spriteapp.booklibrary.ui.activity.SquareDetailsActivity.PLATFORM_ID;

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
    FollowPop popupWindow;
    private CommentDialog commentDialog;
    private int commentPos = -1;

    public SquareAdapter(Activity context, List<SquareBean> list) {
        this.context = context;
        this.list = list;
        commentDialog = new CommentDialog(context);
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
                    Util.ImageClick(viewHolder.image1, squareBean.getPic_url(), 0, context);
                } else if (squareBean.getPic_url().size() == 2) {//两张图片
                    viewHolder.image2.setVisibility(View.VISIBLE);
                    viewHolder.image_recyclerview.setVisibility(View.GONE);
                    viewHolder.image1.setVisibility(View.VISIBLE);
                    GlideUtils.loadImage(viewHolder.image1, squareBean.getPic_url().get(0), context);
                    GlideUtils.loadImage(viewHolder.image2, squareBean.getPic_url().get(1), context);
                    Util.ImageClick(viewHolder.image1, squareBean.getPic_url(), 0, context);
                    Util.ImageClick(viewHolder.image2, squareBean.getPic_url(), 1, context);

                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewHolder.image1.getLayoutParams();
                    layoutParams.height = (BaseActivity.deviceWidth - Util.dp2px(context, 30)) / 3;
                    layoutParams.width = layoutParams.height;
                    layoutParams.rightMargin = Util.dp2px(context, 5);
                    viewHolder.image1.setLayoutParams(layoutParams);
                    viewHolder.image2.setLayoutParams(layoutParams);
                } else if (squareBean.getPic_url().size() > 2) {//两张图片以上
                    viewHolder.image1.setVisibility(View.GONE);
                    viewHolder.image2.setVisibility(View.GONE);
                    viewHolder.image_recyclerview.setVisibility(View.VISIBLE);
                    viewHolder.image_recyclerview.setLayoutManager(new GridLayoutManager(context, squareBean.getPic_url().size() == 4 ? 2 : 3));
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
            viewHolder.support_num.setEnabled(true);
            viewHolder.read_num.setText(Util.getFloat(squareBean.getReadnum()) + "次预览");
            viewHolder.comment_num.setText(Util.getFloat(squareBean.getCommentnum()));
            viewHolder.support_num.setText(Util.getFloat(squareBean.getSupportnum()));
            itemClick(viewHolder, squareBean);//item点击
            moreImageClick(viewHolder.more);//更多点击
            goSupport(viewHolder.support_num, squareBean);//点赞
            goComment(viewHolder.comment_num, squareBean, position);
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

    public void itemClick(SquareViewHolder holder, final SquareBean bean) {
        if (bean == null) return;
        holder.item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//帖子id
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
//                showpopWindow(more);//更多操作
                popupWindow = new FollowPop(context, more);
            }
        });
    }

    //    public void showpopWindow(View v) {
//        View layout = View.inflate(context, R.layout.square_pop_layout, null);
//        popupWindow = new PopupWindow(layout, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
//        popupWindow.setFocusable(true);
//        popupWindow.setOutsideTouchable(true);
//        ColorDrawable dw = new ColorDrawable(ContextCompat.getColor(context, R.color.pop_back));
//        popupWindow.setBackgroundDrawable(dw);
//        popupWindow.showAsDropDown(v, 30, 0, Gravity.LEFT);
//        viewClick(layout);
//        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//            }
//        });
//    }
//
//    public void viewClick(View item) {
//        TextView guanzhu, jubao, pingbi;
//        guanzhu = (TextView) item.findViewById(R.id.guanzhu);
//        jubao = (TextView) item.findViewById(R.id.jubao);
//        pingbi = (TextView) item.findViewById(R.id.pingbi);
//
//        guanzhu.setOnClickListener(new View.OnClickListener() {//关注
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//                ToastUtil.showToast("关注");
//
//
//            }
//        });
//        pingbi.setOnClickListener(new View.OnClickListener() {//屏蔽
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//                ToastUtil.showToast("屏蔽");
//            }
//        });
//        jubao.setOnClickListener(new View.OnClickListener() {//举报
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//                ToastUtil.showToast("举报");
//            }
//        });
//
//    }
    public void sendComment(final String content, final SquareBean bean, final int pos) {//添加评论
        BookApi.getInstance()
                .service
                .square_addcomment(content, bean.getId(), PLATFORM_ID)
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
                            CommentBean reply = new CommentBean();
                            reply.setContent(content);
                            if (UserBean.getInstance().getUser_nickname() != null)
                                reply.setUsername(UserBean.getInstance().getUser_nickname());
                            bean.getComments().add(0, reply);
                            notifyItemChanged(pos);

                            ToastUtil.showToast("评论成功");
                        } else if (resultCode == ApiCodeEnum.FAILURE.getValue()) {//失败
                            ToastUtil.showToast("评论失败");
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

    public void showCommentDialog(final SquareBean squareBean, final int pos) {
//        commentDialog = new CommentDialog(context, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String content = commentDialog.getContent();
//                if (content != null && !content.isEmpty()) {
//                    commentDialog.dismiss();
//                    sendComment(content, squareBean, pos);
//                } else {
//                    ToastUtil.showToast("请输入内容");
//                }
//            }
//        });

        if (commentDialog != null) {
            commentDialog.show();
            commentDialog.getSendText().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String content = commentDialog.getContent();
                    if (content != null && !content.isEmpty()) {
                        commentDialog.clearText();
                        commentDialog.dismiss();
                        sendComment(content, squareBean, pos);
                    } else {
                        ToastUtil.showToast("请输入内容");
                    }
                }
            });
        }

    }

    public void goComment(View view, final SquareBean squareBean, final int pos) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentDialog(squareBean, pos);
            }
        });
    }

    public void goSupport(final TextView view, final SquareBean squareBean) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!AppUtil.isLogin(context)) {
                    return;
                }
                BookApi.getInstance()
                        .service
                        .square_actcmt(squareBean.getId(), "supportnum", PLATFORM_ID)
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
                                    squareBean.setSupportnum(squareBean.getSupportnum() + 1);//改变数量
                                    view.setText(squareBean.getSupportnum() + "");
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
        private TextView follow_btn;

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
            follow_btn = (TextView) itemView.findViewById(R.id.follow_btn);
            line = itemView.findViewById(R.id.line);
        }
    }
}
