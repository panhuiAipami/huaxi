package com.spriteapp.booklibrary.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.base.BaseActivity;
import com.spriteapp.booklibrary.callback.RoundImageLoader;
import com.spriteapp.booklibrary.constant.Constant;
import com.spriteapp.booklibrary.constant.WebConstant;
import com.spriteapp.booklibrary.model.NewBookStoreResponse;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.ui.adapter.second.FreeAdapter;
import com.spriteapp.booklibrary.ui.fragment.NewNativeBookStoreFragment;
import com.spriteapp.booklibrary.util.ActivityUtil;
import com.spriteapp.booklibrary.util.AppUtil;
import com.spriteapp.booklibrary.util.GlideUtils;
import com.spriteapp.booklibrary.util.TimeUtil;
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

    //新加三个标识
    private int recommandSize = 0;
    private int freelimitSize = 0;
    private int classicalSize = 0;
    private int freenewSize = 0;
    private int recentSize = 0;
    private int discountlimitSize = 0;
    private int man_or_woman1Size = 0;
    private int man_or_woman2Size = 0;
    private List<String> images = new ArrayList<>();
    private int titlePos = -1;
    private String jumpUrl = "";
    private int sex;
    private int p1, p2, p3, p4, p5, p6, p7, p8, p9;
    private CountDownTimer timer;

    private NewNativeBookStoreFragment fragment;
    private List<BookDetailResponse> nullList;

    public NewBookStoreAdapter(Activity context, List<NewBookStoreResponse> list, int sex, NewNativeBookStoreFragment fragment) {
        this.context = context;
        this.list = list;
        this.sex = sex;
        this.fragment = fragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        final NewBookStoreResponse newBookStoreResponse = list.get(0);

        if (holder instanceof BannerViewHolder) {
            if (newBookStoreResponse.getTop_banner_lists() != null && newBookStoreResponse.getTop_banner_lists().size() != 0) {
                BannerViewHolder bannerViewHolder = (BannerViewHolder) holder;
                List<NewBookStoreResponse.TopBannerListsBean> top_banner_lists = newBookStoreResponse.getTop_banner_lists();
                images.clear();
                for (NewBookStoreResponse.TopBannerListsBean banner : top_banner_lists
                        ) {
                    images.add(banner.getImages());
                }
//                initBanner(bannerViewHolder.banner, top_banner_lists);
                initBanner(bannerViewHolder.banner, top_banner_lists);
//                ininClick(bannerViewHolder);
            }
        } else if (holder instanceof TitleViewHolder) {
//            if (titlePos == -1) return;
//            TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
//            switch (titlePos) {
//                case 0:
//                    setText(titleViewHolder.cate_title, R.string.tuijian, newBookStoreResponse.getRecommandBookUrl(), titleViewHolder.cate_title_small, R.string.zhong_man);
//                    break;
//                case 1:
//                    setText(titleViewHolder.cate_title, R.string.jingdian, newBookStoreResponse.getClassicalBookUrl(), titleViewHolder.cate_title_small, R.string.wan_man);
//                    break;
//                case 2:
//                    setText(titleViewHolder.cate_title, R.string.mianfei, newBookStoreResponse.getFreeNewBookUrl(), titleViewHolder.cate_title_small, R.string.mian_man);
//                    break;
//                case 3:
//                    setText(titleViewHolder.cate_title, R.string.zuijin, newBookStoreResponse.getRecentBookUrl(), titleViewHolder.cate_title_small, R.string.gengxin_man);
//                    break;
//            }


        } else if (holder instanceof DetailsViewHolder) {
            DetailsViewHolder detailsViewHolder = (DetailsViewHolder) holder;
            if (position == p1 && recommandSize != 0) {//重磅推荐
                Log.d("mianfeixinshu", "重磅推荐position==" + position);
                detailsViewHolder.store_title_bar.setVisibility(View.VISIBLE);
                setText(detailsViewHolder.cate_title, R.string.tuijian, newBookStoreResponse.getRecommandBookUrl(), detailsViewHolder.cate_title_small, R.string.zhong_man, detailsViewHolder.store_title_bar, 1);
            } else if ((position == p3 || position == p2 || position == p1) && classicalSize != 0) {//经典完本
                Log.d("mianfeixinshu", "经典完本position==" + position);
                detailsViewHolder.store_title_bar.setVisibility(View.VISIBLE);
                setText(detailsViewHolder.cate_title, R.string.jingdian, newBookStoreResponse.getClassicalBookUrl(), detailsViewHolder.cate_title_small, R.string.wan_man, detailsViewHolder.store_title_bar, 2);
            } else if ((position == p5 || position == p4 || position == p3 || position == p2 || position == p1) && man_or_woman1Size != 0) {//都市传说、古言穿越
                Log.d("mianfeixinshu", "都市传说position==" + position);
                detailsViewHolder.store_title_bar.setVisibility(View.VISIBLE);
                setText(detailsViewHolder.cate_title, sex == 1 ? R.string.xuanhuan : R.string.zongcai, newBookStoreResponse.getMan_woman_one_url(), detailsViewHolder.cate_title_small, sex == 1 ? R.string.xuanhuan_man : R.string.zongcai_woman, detailsViewHolder.store_title_bar, sex == 1 ? 5 : 6);
            } else if ((position == p7 || position == p6 || position == p5 || position == p4 || position == p3 || position == p2 || position == p1) && freenewSize != 0) {//免费新书
                Log.d("mianfeixinshu", "免费新书position==" + position);
                detailsViewHolder.store_title_bar.setVisibility(View.VISIBLE);
                setText(detailsViewHolder.cate_title, R.string.mianfei, newBookStoreResponse.getRecentBookUrl(), detailsViewHolder.cate_title_small, R.string.mian_man, detailsViewHolder.store_title_bar, 3);
            } else if ((position == p8 || position == p7 || position == p6 || position == p5 || position == p4 || position == p3 || position == p2 || position == p1) && recentSize != 0) {//最近更新
                Log.d("mianfeixinshu", "最近更新position==" + position);
                detailsViewHolder.store_title_bar.setVisibility(View.VISIBLE);
                setText(detailsViewHolder.cate_title, R.string.zuijin, newBookStoreResponse.getRecentBookUrl(), detailsViewHolder.cate_title_small, R.string.gengxin_man, detailsViewHolder.store_title_bar, 4);
            } else {
                detailsViewHolder.store_title_bar.setVisibility(View.GONE);
            }

            if (position < p2) {
                List<BookDetailResponse> recommandBookList = newBookStoreResponse.getRecommandBookList();
                setData(detailsViewHolder, recommandBookList, position, position - 1);

            } else if (position >= p2 && position < p4) {
                List<BookDetailResponse> classicalBookList = newBookStoreResponse.getClassicalBookList();
                setData(detailsViewHolder, classicalBookList, position, position - 1 - recommandSize - freelimitSize);

            } else if (position >= p4 && position < p6) {
                List<BookDetailResponse> freeNewBookList = newBookStoreResponse.getMan_woman_one();
                setData(detailsViewHolder, freeNewBookList, position, position - 1 - recommandSize - freelimitSize - classicalSize - discountlimitSize);

            } else if (position >= p6 && position < p8) {
                List<BookDetailResponse> freeNewBookList = newBookStoreResponse.getFreeNewBookList();
                setData(detailsViewHolder, freeNewBookList, position, position - 1 - recommandSize - freelimitSize - classicalSize - discountlimitSize - man_or_woman1Size - man_or_woman2Size);

            } else if (position >= p8 && position < p9) {
                List<BookDetailResponse> recentBookList = newBookStoreResponse.getRecentBookList();
                setData(detailsViewHolder, recentBookList, position, position - 1 - recommandSize - classicalSize - freelimitSize - freenewSize - discountlimitSize - man_or_woman1Size - man_or_woman2Size);
            }


        } else if (holder instanceof FreeViewHolder) {
            final FreeViewHolder freeViewHolder = (FreeViewHolder) holder;
            freeViewHolder.line1.setVisibility(View.GONE);
            freeViewHolder.line2.setVisibility(View.VISIBLE);
            if (position == p2) {
                freeViewHolder.cate_title_small.setVisibility(View.INVISIBLE);
                freeViewHolder.free_title.setText("限时免费");
                List<BookDetailResponse> freelimitBookList = newBookStoreResponse.getFreelimitBookList();
                if (freelimitBookList != null && freelimitBookList.size() != 0) {
                    countDown(freeViewHolder, freelimitBookList.get(0), 1);
                    freeViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    freeViewHolder.recyclerView.setAdapter(new FreeAdapter(context, freelimitBookList));
                }
            } else if (position == p4) {
                freeViewHolder.cate_title_small.setVisibility(View.INVISIBLE);
                freeViewHolder.free_title.setText("限时折扣");
                List<BookDetailResponse> freelimitBookList = newBookStoreResponse.getDiscountlimitBookList();
                if (freelimitBookList != null && freelimitBookList.size() != 0) {
                    countDown(freeViewHolder, freelimitBookList.get(0), 2);
                    freeViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    freeViewHolder.recyclerView.setAdapter(new FreeAdapter(context, freelimitBookList));
                }
            } else if (position == p6) {
                freeViewHolder.cate_title_small.setVisibility(View.VISIBLE);
                freeViewHolder.cate_title_small.setText(sex == 1 ? R.string.dushi_man : R.string.chuanyue_woman);
                freeViewHolder.free_title.setText(sex == 1 ? R.string.dushi : R.string.chuanyue);
                freeViewHolder.count_time_item.setVisibility(View.GONE);
                List<BookDetailResponse> freelimitBookList = newBookStoreResponse.getMan_woman_two();
                if (freelimitBookList != null && freelimitBookList.size() != 0) {
                    Log.d("FreeViewHolder", "当前position===" + position);
                    freeViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                    freeViewHolder.recyclerView.setAdapter(new FreeAdapter(context, freelimitBookList));
                }
                freeViewHolder.free_title_bar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setText(freeViewHolder.free_title, sex == 1 ? R.string.dushi : R.string.chuanyue, newBookStoreResponse.getMan_woman_two_url(), freeViewHolder.cate_title_small, sex == 1 ? R.string.dushi_man : R.string.chuanyue_woman, freeViewHolder.free_title_bar, sex == 1 ? 7 : 8);
                    }
                });
            }
        }
    }

    boolean flag = false;

    private void countDown(final FreeViewHolder holder, BookDetailResponse response, final int type) {//type为限时免费,2为限时折扣
        if (response == null) return;
        if (System.currentTimeMillis() / 1000 > response.getEnd_time()) return;
        long current = response.getEnd_time() - (System.currentTimeMillis() / 1000);
        Log.d("countDown", "countDown===" + current);
        holder.count_time_item.setVisibility(View.VISIBLE);

        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(current * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished >= 3600000) {
                    long hour = millisUntilFinished / 3600000;
                    Log.d("hour", "hour===" + hour + "millisUntilFinished===" + millisUntilFinished);
                    holder.hour_time.setText(hour < 10 ? "0" + hour : "" + hour);
                } else {
                    holder.hour_time.setText("00");
                }

//                holder.hour_time.setText(TimeUtil.getDateHour(millisUntilFinished));
                holder.minute_time.setText(TimeUtil.getDateMinute(millisUntilFinished));
                holder.second_time.setText(TimeUtil.getDateSecond(millisUntilFinished));
                if (!flag) flag = !flag;
            }

            @Override
            public void onFinish() {
                Log.d("onFinish", "执行onFinish");
                holder.hour_time.setText("00");
                holder.minute_time.setText("00");
                holder.second_time.setText("00");
                if (fragment != null) {//倒计时结束刷新数据
//                    fragment.adapterRefreshData();
                }
                if (list != null && list.size() != 0 && flag) {
                    if (nullList == null) nullList = new ArrayList<>();
                    if (type == 1) {//限时免费
                        list.get(0).setFreelimitBookList(nullList);
                        notifyDataSetChanged();
                    } else if (type == 2) {//限时折扣
                        list.get(0).setDiscountlimitBookList(nullList);
                        notifyDataSetChanged();
                    }
                    flag = false;
                }
            }
        }.start();

    }


    private void ininClick(BannerViewHolder bannerViewHolder) {
        classification_text.setOnClickListener(onClickListener);
        ranking_text.setOnClickListener(onClickListener);
        monthly_text.setOnClickListener(onClickListener);
        sign_text.setOnClickListener(onClickListener);
        gotoMonthly_text.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == classification_text) {//分类

            } else if (v == ranking_text) {//排行
//                ActivityUtil.toNestingActivity(context, 1, sex);

            } else if (v == monthly_text) {//包月

            } else if (v == sign_text) {//签到
                if (!AppUtil.isLogin(context)) return;
                ActivityUtil.toWebViewActivity(context, Constant.CHECK_IN_URL);//去签到

            } else if (v == gotoMonthly_text) {//开通包月

            }
        }
    };

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
        holder.book_state.setText(bookDetailResponse.getBook_finish_flag()?  "完本":"连载" );
        holder.book_num.setText(Util.getFloat(bookDetailResponse.getBook_content_byte()));
        if (startPos == bean.size() - 1) {
            holder.line1.setVisibility(View.GONE);
            holder.line2.setVisibility(View.VISIBLE);
        }else {
            holder.line1.setVisibility(View.VISIBLE);
            holder.line2.setVisibility(View.GONE);
        }
        holder.book_cate.setText((bookDetailResponse.getBook_category() != null && bookDetailResponse.getBook_category().size() != 0) ? bookDetailResponse.getBook_category().get(0).getClass_name() : "都市");
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

    public void setText(final TextView text, final int title, final String url, final TextView text1, int title1, LinearLayout layout, final int type) {
        text.setText(title);
        text1.setText(title1);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!TextUtils.isEmpty(url)) {
//                    Uri uri = Uri.parse(url);
//                    jumpUrl = uri.getQueryParameter(WebConstant.URL_QUERY);
//                    ActivityUtil.toWebViewActivity(context, jumpUrl);
//                }
                ActivityUtil.toStoreDetailsActivity(context, type, sex, title);

            }
        });
    }


    /**
     * 轮播图展示与点击
     *
     * @param banner
     * @param top_banner_lists
     */
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
            p1 = 0;
            p2 = 0;
            p3 = 0;
            p4 = 0;
            p5 = 0;
            p6 = 0;
            p7 = 0;
            p8 = 0;
            p9 = 0;
            num = 1;
            p1 = num;//重磅推荐标题
            recommandSize = 0;

            classicalSize = 0;

            freelimitSize = 0;

            discountlimitSize = 0;

            freenewSize = 0;

            recentSize = 0;

            man_or_woman1Size = 0;

            man_or_woman2Size = 0;
            NewBookStoreResponse newBookStoreResponse = list.get(0);
            if (newBookStoreResponse.getRecommandBookList() != null && newBookStoreResponse.getRecommandBookList().size() != 0) {//重磅推荐，竖向
                num = num + newBookStoreResponse.getRecommandBookList().size();
                p2 = num;//限时免费标题
                recommandSize = newBookStoreResponse.getRecommandBookList().size();
            }
            if (newBookStoreResponse.getFreelimitBookList() != null && newBookStoreResponse.getFreelimitBookList().size() != 0) {//限时免费，横向
                num = num + 1;
                p3 = num;//经典完本标题
                freelimitSize = 1;
            }
            if (newBookStoreResponse.getClassicalBookList() != null && newBookStoreResponse.getClassicalBookList().size() != 0) {//经典完本，竖向
                num = num + newBookStoreResponse.getClassicalBookList().size();
                p4 = num;//限时折扣标题
                classicalSize = newBookStoreResponse.getClassicalBookList().size();
            }
            if (newBookStoreResponse.getDiscountlimitBookList() != null && newBookStoreResponse.getDiscountlimitBookList().size() != 0) {//限时折扣，横向
                num = num + 1;
                p5 = num;//都市标题
                discountlimitSize = 1;
            }

            if (newBookStoreResponse.getMan_woman_one() != null && newBookStoreResponse.getMan_woman_one().size() != 0) {//男女生一，竖向
                num = num + newBookStoreResponse.getMan_woman_one().size();
                p6 = num;//穿越位置
                man_or_woman1Size = newBookStoreResponse.getMan_woman_one().size();
            }
            if (newBookStoreResponse.getMan_woman_two() != null && newBookStoreResponse.getMan_woman_two().size() != 0) {//男女生二，横向
                num = num + 1;
                p7 = num;//新书位置
                man_or_woman2Size = 1;
            }

            if (newBookStoreResponse.getFreeNewBookList() != null && newBookStoreResponse.getFreeNewBookList().size() != 0) {//免费新书，竖向
                num = num + newBookStoreResponse.getFreeNewBookList().size();
                p8 = num;//最近更新标题
                freenewSize = newBookStoreResponse.getFreeNewBookList().size();
            }
            if (newBookStoreResponse.getRecentBookList() != null && newBookStoreResponse.getRecentBookList().size() != 0) {//最近更新，竖向
                num = num + newBookStoreResponse.getRecentBookList().size();
                p9 = num;
                recentSize = newBookStoreResponse.getRecentBookList().size();
            }


        }
        return num;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return BANNERPOS;
        } else {
            if (position == p1) {
                return DETAILSPOS;
            } else if (position == p2 && freelimitSize != 0) {
                return FREELIMITPOS;
            } else if (position == p3) {
                return DETAILSPOS;
            } else if (position == p4 && discountlimitSize != 0) {
                return FREELIMITPOS;
            } else if (position == p5) {
                return DETAILSPOS;
            } else if (position == p6 && man_or_woman1Size != 0) {
                return FREELIMITPOS;
            } else if (position == p7) {
                return DETAILSPOS;
            } else if (position == p8) {
                return DETAILSPOS;
            } else {
                return DETAILSPOS;
            }
        }


    }

    private TextView classification_text, ranking_text, monthly_text, sign_text,
            gotoMonthly_text;


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
        private TextView cate_title, cate_title_small;

        public TitleViewHolder(View itemView) {
            super(itemView);
            cate_title = (TextView) itemView.findViewById(R.id.cate_title);
            cate_title_small = (TextView) itemView.findViewById(R.id.cate_title_small);
        }
    }

    private class DetailsViewHolder extends RecyclerView.ViewHolder {//详情
        private ImageView book_cover, author_head;
        private TextView book_name, book_describe, book_cate, book_state, book_num, author_name;
        //标题
        private TextView cate_title, cate_title_small;
        private LinearLayout store_title_bar;
        private View line1, line2;

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

            cate_title = (TextView) itemView.findViewById(R.id.cate_title);
            cate_title_small = (TextView) itemView.findViewById(R.id.cate_title_small);
            store_title_bar = (LinearLayout) itemView.findViewById(R.id.store_title_bar);
            line1 = itemView.findViewById(R.id.line1);
            line2 = itemView.findViewById(R.id.line2);

        }
    }

    private class FreeViewHolder extends RecyclerView.ViewHolder {//标题
        private TextView free_title;
        private View line1, line2;
        private RecyclerView recyclerView;
        private LinearLayout count_time_item, free_title_bar;
        private TextView hour_time, minute_time, second_time, cate_title_small;

        public FreeViewHolder(View itemView) {
            super(itemView);
            free_title = (TextView) itemView.findViewById(R.id.free_title);
            cate_title_small = (TextView) itemView.findViewById(R.id.cate_title_small);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView);
            count_time_item = (LinearLayout) itemView.findViewById(R.id.count_time_item);
            free_title_bar = (LinearLayout) itemView.findViewById(R.id.free_title_bar);
            hour_time = (TextView) itemView.findViewById(R.id.hour_time);
            minute_time = (TextView) itemView.findViewById(R.id.minute_time);
            second_time = (TextView) itemView.findViewById(R.id.second_time);
            line1 = itemView.findViewById(R.id.line1);
            line2 = itemView.findViewById(R.id.line2);
        }
    }
}
