package com.spriteapp.booklibrary.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseActivity;
import com.spriteapp.booklibrary.callback.RoundImageLoader;
import com.spriteapp.booklibrary.constant.WebConstant;
import com.spriteapp.booklibrary.model.NewBookStoreResponse;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.adapter.second.FreeAdapter;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.Util;
import com.youth.banner.Banner;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/30.
 */

public class NewBookStoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int BANNERPOS = 0;//轮播
    private static final int FREELIMITPOS = 3;//标题
    private static final int TITLEPOS = 1;//限时免费
    private static final int DETAILSPOS = 2;//内容
    private Activity context;
    private List<NewBookStoreResponse> list;
    private int num = 0;
    private boolean HAVERECOMMAND = false;
    private boolean FREELIMITBOOKLIST = false;
    private boolean HAVECLASSICAL = false;
    private boolean HAVEFREENEW = false;
    private boolean HAVERECENT = false;
    private int recommandSize = 0;
    private int freelimitSize = 0;
    private int classicalSize = 0;
    private int freenewSize = 0;
    private int recentSize = 0;
    private List<String> images = new ArrayList<>();
    private int titlePos = -1;
    private String jumpUrl = "";

    public NewBookStoreAdapter(Activity context, List<NewBookStoreResponse> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = null;
        if (viewType == BANNERPOS) {//左边文字
            convertView = LayoutInflater.from(context).inflate(R.layout.store_banner_layout, parent, false);
            return new BannerViewHolder(convertView);
        } else if (viewType == TITLEPOS) {
            convertView = LayoutInflater.from(context).inflate(R.layout.store_title_layout, parent, false);
            return new TitleViewHolder(convertView);
        } else if (viewType == FREELIMITPOS) {
            convertView = LayoutInflater.from(context).inflate(R.layout.store_free_layout, parent, false);
            return new FreeViewHolder(convertView);
        } else if (viewType == DETAILSPOS) {
            convertView = LayoutInflater.from(context).inflate(R.layout.store_details_layout, parent, false);
            return new DetailsViewHolder(convertView);
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.store_details_layout, parent, false);
            return new DetailsViewHolder(convertView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (list.size() == 0) return;
        NewBookStoreResponse newBookStoreResponse = list.get(0);

        if (holder instanceof BannerViewHolder) {
            if (newBookStoreResponse.getTop_banner_lists() != null && newBookStoreResponse.getTop_banner_lists().size() != 0) {
                BannerViewHolder bannerViewHolder = (BannerViewHolder) holder;
                List<NewBookStoreResponse.TopBannerListsBean> top_banner_lists = newBookStoreResponse.getTop_banner_lists();
                images.clear();
                for (NewBookStoreResponse.TopBannerListsBean banner : top_banner_lists
                        ) {
                    images.add(banner.getImages());
                }
                initBanner(bannerViewHolder.banner, top_banner_lists);
            }
        } else if (holder instanceof TitleViewHolder) {
            if (titlePos == -1) return;
            TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
            switch (titlePos) {
                case 0:
                    setText(titleViewHolder.cate_title, R.string.tuijian, newBookStoreResponse.getRecommandBookUrl());
                    break;
                case 1:
                    setText(titleViewHolder.cate_title, R.string.jingdian, newBookStoreResponse.getClassicalBookUrl());
                    break;
                case 2:
                    setText(titleViewHolder.cate_title, R.string.mianfei, newBookStoreResponse.getFreeNewBookUrl());
                    break;
                case 3:
                    setText(titleViewHolder.cate_title, R.string.zuijin, newBookStoreResponse.getRecentBookUrl());
                    break;
            }


        } else if (holder instanceof DetailsViewHolder) {
            DetailsViewHolder detailsViewHolder = (DetailsViewHolder) holder;
            if (HAVERECOMMAND && position - 2 < recommandSize) {
                List<BookDetailResponse> recommandBookList = newBookStoreResponse.getRecommandBookList();
                setData(detailsViewHolder, recommandBookList, position, position - 2);

            } else if (HAVECLASSICAL && position - (recommandSize + freelimitSize + (HAVERECOMMAND ? 3 : 2)) < classicalSize) {
                List<BookDetailResponse> classicalBookList = newBookStoreResponse.getClassicalBookList();
                setData(detailsViewHolder, classicalBookList, position, position - (recommandSize + freelimitSize + (HAVERECOMMAND ? 3 : 2)));

            } else if (HAVEFREENEW && position - (recommandSize + freelimitSize + classicalSize + (HAVERECOMMAND && HAVECLASSICAL ? 4 : (HAVERECOMMAND || HAVECLASSICAL) ? 3 : 2)) < freenewSize) {
                List<BookDetailResponse> freeNewBookList = newBookStoreResponse.getFreeNewBookList();
                setData(detailsViewHolder, freeNewBookList, position, position - (recommandSize + freelimitSize + classicalSize + (HAVERECOMMAND && HAVECLASSICAL ? 4 : (HAVERECOMMAND || HAVECLASSICAL) ? 3 : 2)));

            } else if (HAVERECENT && position - (recommandSize + freelimitSize + classicalSize + freenewSize + (HAVERECOMMAND && HAVECLASSICAL && HAVEFREENEW ? 5 : (HAVERECOMMAND && HAVECLASSICAL) ? 4 : (HAVERECOMMAND && HAVEFREENEW) ? 4 : (HAVECLASSICAL && HAVEFREENEW) ? 4 : (HAVERECOMMAND || HAVECLASSICAL || HAVEFREENEW) ? 3 : 2)) < recentSize) {
                List<BookDetailResponse> recentBookList = newBookStoreResponse.getRecentBookList();
                setData(detailsViewHolder, recentBookList, position, position - (recommandSize + freelimitSize + classicalSize + freenewSize + (HAVERECOMMAND && HAVECLASSICAL && HAVEFREENEW ? 5 : (HAVERECOMMAND && HAVECLASSICAL) ? 4 : (HAVERECOMMAND && HAVEFREENEW) ? 4 : (HAVECLASSICAL && HAVEFREENEW) ? 4 : (HAVERECOMMAND || HAVECLASSICAL || HAVEFREENEW) ? 3 : 2)));

            }

        } else if (holder instanceof FreeViewHolder) {
            FreeViewHolder freeViewHolder = (FreeViewHolder) holder;
            List<BookDetailResponse> freelimitBookList = newBookStoreResponse.getFreelimitBookList();
            if (FREELIMITBOOKLIST && freelimitBookList != null && freelimitBookList.size() != 0) {
                freeViewHolder.recyclerView.setLayoutManager(new GridLayoutManager(context,freelimitBookList.size()));
                freeViewHolder.recyclerView.setAdapter(new FreeAdapter(context, freelimitBookList));
            }

        }


    }

    public void setData(DetailsViewHolder holder, List<BookDetailResponse> bean, int pos, int startPos) {
        if (bean == null) return;
        if (bean.size() == 0) return;
        if (startPos < 0) return;
        if (bean.size() < startPos) return;
        final BookDetailResponse bookDetailResponse = bean.get(startPos);
        GlideUtils.loadImage(holder.book_cover, bookDetailResponse.getBook_image(), context);
        GlideUtils.loadImage(holder.author_head, bookDetailResponse.getAuthor_avatar(), context);
        holder.book_name.setText(bookDetailResponse.getBook_name());
        holder.author_name.setText(bookDetailResponse.getAuthor_name());
        holder.book_describe.setText(bookDetailResponse.getBook_intro());
        holder.book_state.setText(bookDetailResponse.getBook_finish_flag() == 0 ? "连载" : "完本");
        holder.book_num.setText(Util.getFloat(bookDetailResponse.getBook_content_byte()));
        holder.book_cate.setText((bookDetailResponse.getBook_keywords() != null && bookDetailResponse.getBook_keywords().size() != 0) ? bookDetailResponse.getBook_keywords().get(0) : "都市");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookDetailResponse.getBook_url() != null && !bookDetailResponse.getBook_url().isEmpty()) {
                    Uri uri = Uri.parse(bookDetailResponse.getBook_url());
                    jumpUrl = uri.getQueryParameter(WebConstant.URL_QUERY);
                    ActivityUtil.toWebViewActivity(context, jumpUrl);
                }

            }
        });
    }

    public void setText(final TextView text, int title, final String url) {
        text.setText(title);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(url)) {
                    Uri uri = Uri.parse(url);
                    jumpUrl = uri.getQueryParameter(WebConstant.URL_QUERY);
                    ActivityUtil.toWebViewActivity(context, jumpUrl);
                }
            }
        });
    }

    public void initBanner(Banner banner, final List<NewBookStoreResponse.TopBannerListsBean> top_banner_lists) {
        if (images.size() == 0) return;
        try {
            banner.setImages(images);
            banner.setBannerAnimation(Transformer.Stack);
            banner.setDelayTime(5000);
            banner.setOffscreenPageLimit(3);
            banner.setImageLoader(new RoundImageLoader() {
                @Override
                public void displayImage(Context context, Object path, RoundedImageView imageView) {
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//                    imageView.setCornerRadius(20);
                    GlideUtils.loadImage(imageView, path.toString(), context);
                }
            });
            banner.start();
            banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {


                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
            banner.setOnBannerListener(new OnBannerListener() {
                @Override
                public void OnBannerClick(int position) {
                    if (position < top_banner_lists.size() && top_banner_lists.get(position).getUrl() != null && !top_banner_lists.get(position).getUrl().isEmpty()) {
                        Uri uri = Uri.parse(top_banner_lists.get(position).getUrl());
                        jumpUrl = uri.getQueryParameter(WebConstant.URL_QUERY);
                        ActivityUtil.toWebViewActivity(context, jumpUrl);
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        if (list.size() == 0) {
            return 0;
        } else {
            num = 1;
            HAVERECOMMAND = false;
            FREELIMITBOOKLIST = false;
            HAVECLASSICAL = false;
            HAVEFREENEW = false;
            HAVERECENT = false;
            recommandSize = 0;
            classicalSize = 0;
            freenewSize = 0;
            recentSize = 0;
            titlePos = -1;
            NewBookStoreResponse newBookStoreResponse = list.get(0);
            if (newBookStoreResponse.getRecommandBookList() != null && newBookStoreResponse.getRecommandBookList().size() != 0) {//重磅推荐
                num = num + newBookStoreResponse.getRecommandBookList().size() + 1;
                HAVERECOMMAND = true;
                recommandSize = newBookStoreResponse.getRecommandBookList().size();
            }
            if (newBookStoreResponse.getFreelimitBookList() != null && newBookStoreResponse.getFreelimitBookList().size() != 0) {//重磅推荐
                num = num + 1;
                FREELIMITBOOKLIST = true;
                freelimitSize = 1;
            }
            if (newBookStoreResponse.getClassicalBookList() != null && newBookStoreResponse.getClassicalBookList().size() != 0) {//经典完本
                num = num + newBookStoreResponse.getClassicalBookList().size() + 1;
                HAVECLASSICAL = true;
                classicalSize = newBookStoreResponse.getClassicalBookList().size();
            }
            if (newBookStoreResponse.getFreeNewBookList() != null && newBookStoreResponse.getFreeNewBookList().size() != 0) {//免费新书
                num = num + newBookStoreResponse.getFreeNewBookList().size() + 1;
                HAVEFREENEW = true;
                freenewSize = newBookStoreResponse.getFreeNewBookList().size();
            }
            if (newBookStoreResponse.getRecentBookList() != null && newBookStoreResponse.getRecentBookList().size() != 0) {//最近更新
                num = num + newBookStoreResponse.getRecentBookList().size() + 1;
                HAVERECENT = true;
                recentSize = newBookStoreResponse.getRecentBookList().size();
            }

        }
//        Log.d("initList", "HAVERECOMMAND==" + HAVERECOMMAND + "HAVECLASSICAL===" + HAVECLASSICAL);
        return num;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return BANNERPOS;
        } else {
            if (HAVERECOMMAND && position == 1) {
                titlePos = 0;
                return TITLEPOS;
            } else if (FREELIMITBOOKLIST && position == recommandSize + (HAVERECOMMAND ? 2 : 1)) {
                return FREELIMITPOS;
            } else if (HAVECLASSICAL && position == recommandSize + freelimitSize + (HAVERECOMMAND ? 2 : 1)) {
                titlePos = 1;
                return TITLEPOS;
            } else if (HAVEFREENEW && position == recommandSize + freelimitSize + classicalSize + (HAVERECOMMAND && HAVECLASSICAL ? 3 : (HAVERECOMMAND || HAVECLASSICAL ? 2 : 1))) {
                titlePos = 2;
                return TITLEPOS;
            } else if (HAVERECENT && position == recommandSize + freelimitSize + classicalSize + freenewSize + (HAVERECOMMAND && HAVECLASSICAL && HAVEFREENEW ? 4 : (HAVERECOMMAND && HAVECLASSICAL ? 3 : (HAVERECOMMAND && HAVEFREENEW ? 3 : (HAVECLASSICAL && HAVEFREENEW ? 3 : (HAVERECOMMAND || HAVECLASSICAL || HAVEFREENEW ? 2 : 1)))))) {
                titlePos = 3;
                return TITLEPOS;
            } else {
                return DETAILSPOS;
            }

        }
    }

    private class BannerViewHolder extends RecyclerView.ViewHolder {//轮播
        private Banner banner;

        public BannerViewHolder(View itemView) {
            super(itemView);
            banner = (Banner) itemView.findViewById(R.id.banner);
            ViewGroup.LayoutParams layoutParams = banner.getLayoutParams();

            layoutParams.height = (BaseActivity.deviceWidth / 34) * 15;
            banner.setLayoutParams(layoutParams);
        }
    }

    private class TitleViewHolder extends RecyclerView.ViewHolder {//标题
        private TextView cate_title;

        public TitleViewHolder(View itemView) {
            super(itemView);
            cate_title = (TextView) itemView.findViewById(R.id.cate_title);
        }
    }

    private class DetailsViewHolder extends RecyclerView.ViewHolder {//详情
        private ImageView book_cover, author_head;
        private TextView book_name, book_describe, book_cate, book_state, book_num, author_name;

        public DetailsViewHolder(View itemView) {
            super(itemView);
            book_cover = (ImageView) itemView.findViewById(R.id.book_cover);
            author_head = (ImageView) itemView.findViewById(R.id.author_head);
            book_name = (TextView) itemView.findViewById(R.id.book_name);
            author_name = (TextView) itemView.findViewById(R.id.author_name);
            book_describe = (TextView) itemView.findViewById(R.id.book_describe);
            book_cate = (TextView) itemView.findViewById(R.id.book_cate);
            book_state = (TextView) itemView.findViewById(R.id.book_state);
            book_num = (TextView) itemView.findViewById(R.id.book_num);

        }
    }

    private class FreeViewHolder extends RecyclerView.ViewHolder {//标题
        private TextView free_title;
        private RecyclerView recyclerView;

        public FreeViewHolder(View itemView) {
            super(itemView);
            free_title = (TextView) itemView.findViewById(R.id.free_title);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
        }
    }
}
