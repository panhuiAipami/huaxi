package net.huaxi.reader.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.huaxi.reader.bean.SearchBean;
import net.huaxi.reader.view.RoundImageView;

import java.util.List;

import net.huaxi.reader.R;

import net.huaxi.reader.util.ImageUtil;

/**
 * Created by ZMW on 2016/1/28.
 */
public class AdapterAboutRecommend extends BaseRecyclerAdapter<SearchBean> {

    private Context context;

    public AdapterAboutRecommend(Context context, List<SearchBean> list) {
        mDatas = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_same_book, null);
        return new SearchHonlder(view);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, SearchBean data) {
        if (viewHolder instanceof SearchHonlder) {
            SearchHonlder holder = (SearchHonlder) viewHolder;
            SearchBean searchBean = mDatas.get(RealPosition);
            holder.tvAuthorName.setText(searchBean.getAuthorname());
            holder.tvBookName.setText(searchBean.getbTitle());
            ImageUtil.loadImage(context, searchBean.getImageid(), holder.ivbook, R.mipmap.detail_book_default);
            if (data.getIsMonthly() == 1) {
                holder.ivAboutVip.setVisibility(View.VISIBLE);
            } else {
                holder.ivAboutVip.setVisibility(View.GONE);

            }
        }
    }

    class SearchHonlder extends Holder {
        private RoundImageView ivbook;
        private View ivAboutVip;
        private TextView tvBookName, tvAuthorName;

        public SearchHonlder(View itemView) {
            super(itemView);
            ivbook = (RoundImageView) itemView.findViewById(R.id.detail_same_item_imageview);
            ivAboutVip = itemView.findViewById(R.id.iv_detail_some_vip);
            tvBookName = (TextView) itemView.findViewById(R.id.detail_same_bookname_textview);
            tvAuthorName = (TextView) itemView.findViewById(R.id.detail_same_authorname_textview);

        }

    }
}
