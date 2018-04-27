package com.spriteapp.booklibrary.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.config.HuaXiSDK;
import com.spriteapp.booklibrary.model.UserBean;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.widget.readview.util.BitmapUtil;

/**
 * 分享文本截图
 * Created by panhui on 2018/4/26.
 */

public class ShareSelectTextDialog extends Dialog  {
    Context context;
    ScrollView scroll_view;
    RelativeLayout relative_center_bg;
    ImageView image_back, share_button;
    TextView share_book_title;
    TextView share_text;
    ImageView book_cover;
    TextView book_name;
    ImageView book_author_cover;
    TextView author_name;
    TextView user_nickName;
    ImageView user_avatar;

    ImageView image_share_bg1, image_share_bg2, image_share_bg3;

    public ShareSelectTextDialog(@NonNull Context context) {
        this(context, R.style.Dialog_Fullscreen);

    }

    public ShareSelectTextDialog(Context context, int style) {
        super(context, style);
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.reader_text_share_layout, null);
        setContentView(view);
        scroll_view = (ScrollView) findViewById(R.id.scroll_view);
        relative_center_bg = (RelativeLayout) view.findViewById(R.id.relative_center_bg);
        image_back = (ImageView) view.findViewById(R.id.image_back);
        share_button = (ImageView) view.findViewById(R.id.share_button);
        share_book_title = (TextView) view.findViewById(R.id.share_book_title);

        share_text = (TextView) view.findViewById(R.id.share_text);
        book_cover = (ImageView) view.findViewById(R.id.book_cover);
        book_name = (TextView) view.findViewById(R.id.book_name);
        book_author_cover = (ImageView) view.findViewById(R.id.book_author_cover);
        author_name = (TextView) view.findViewById(R.id.author_name);
        user_nickName = (TextView) view.findViewById(R.id.user_nickname);
        user_avatar = (ImageView) view.findViewById(R.id.user_avatar);

        image_share_bg1 = (ImageView) view.findViewById(R.id.image_share_bg1);
        image_share_bg2 = (ImageView) view.findViewById(R.id.image_share_bg2);
        image_share_bg3 = (ImageView) view.findViewById(R.id.image_share_bg3);

        onListener();
    }

    public void setData(BookDetailResponse data, String text) {
        share_book_title.setText(data.getBook_name());

        GlideUtils.loadImage(book_cover, data.getBook_image(), context);
        GlideUtils.loadImage(book_author_cover, data.getAuthor_avatar(), context);
        GlideUtils.loadImage(user_avatar, UserBean.getInstance().getUser_avatar(), context);
        share_text.setText(text);
        book_name.setText(data.getBook_name());
        author_name.setText(data.getAuthor_name());
        user_nickName.setText(UserBean.getInstance().getUser_nickname());


    }

    public void onListener() {
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imagePath = BitmapUtil.loadBitmapFromView(scroll_view);
                HuaXiSDK.getInstance().showShareDialog(context, null,imagePath ,false, 3);
            }
        });

        image_share_bg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBgImage(1);
            }
        });
        image_share_bg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBgImage(2);
            }
        });
        image_share_bg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBgImage(3);
            }
        });
    }

    public void setBgImage(int type) {
        image_share_bg1.setImageResource(0);
        image_share_bg2.setImageResource(0);
        image_share_bg3.setImageResource(0);
        switch (type) {
            case 1:
                image_share_bg1.setImageResource(R.mipmap.select_this_bg_icon);
                relative_center_bg.setBackgroundResource(R.mipmap.share_text_image_bg1);
                break;
            case 2:
                image_share_bg2.setImageResource(R.mipmap.select_this_bg_icon);
                relative_center_bg.setBackgroundResource(R.mipmap.share_text_image_bg2);
                break;
            case 3:
                image_share_bg3.setImageResource(R.mipmap.select_this_bg_icon);
                relative_center_bg.setBackgroundResource(R.mipmap.share_text_image_bg3);
                break;
        }
    }


}
