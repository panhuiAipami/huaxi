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
public class AdapterSearchRecord extends BaseRecyclerAdapter<SearchBean> {

    private Context context;

    public AdapterSearchRecord(Context context, List<SearchBean> list) {
        mDatas = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_book, null);
        return new SearchHonlder(view);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, SearchBean data) {
        if (viewHolder instanceof SearchHonlder) {
            SearchHonlder holder = (SearchHonlder) viewHolder;
            SearchBean searchBean = mDatas.get(RealPosition);
            holder.tvAuthorName.setText(searchBean.getAuthorname());
            holder.tvBookName.setText(searchBean.getbTitle());
            holder.tvDesc.setText(searchBean.getDescription());
//            ImageCacheUtils.loadImage(searchBean.getImageid(), ImageLoader.getImageListener(holder.ivbook,R.mipmap.detail_book_default,
// R.mipmap.detail_book_default));
            ImageUtil.loadImage(context, searchBean.getImageid(), holder.ivbook, R.mipmap.detail_book_default);
            if (data.getIsMonthly() == 1) {
                holder.ivSomeVip.setVisibility(View.VISIBLE);
            } else {
                holder.ivSomeVip.setVisibility(View.GONE);
            }
        }
    }

    class SearchHonlder extends Holder {
        private RoundImageView ivbook;
        private TextView tvBookName, tvAuthorName, tvDesc;
        private View ivSomeVip;

        public SearchHonlder(View itemView) {
            super(itemView);
            ivbook = (RoundImageView) itemView.findViewById(R.id.item_search_imageview);
            ivSomeVip = itemView.findViewById(R.id.iv_detail_some_vip);
            tvBookName = (TextView) itemView.findViewById(R.id.item_search_bookname);
            tvAuthorName = (TextView) itemView.findViewById(R.id.item_search_authorname);
            tvDesc = (TextView) itemView.findViewById(R.id.item_search_desc);
        }

    }
}
