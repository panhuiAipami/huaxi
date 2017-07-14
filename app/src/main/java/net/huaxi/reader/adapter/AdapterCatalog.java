package net.huaxi.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.commonlibs.tools.StringUtils;
import net.huaxi.reader.activity.ClassifyActivity;

import java.util.List;

import net.huaxi.reader.R;

import net.huaxi.reader.bean.CatalogBean;
import net.huaxi.reader.util.ImageUtil;

/**
 * function:    主要分类Adapter
 * author:      ryantao
 * create:      16/7/19
 * modtime:     16/7/19
 */
public class AdapterCatalog extends BaseRecyclerAdapter<CatalogBean> {

    private Context mContext;

    public AdapterCatalog(Context context, List<CatalogBean> beanList) {
        mContext = context;
        mDatas = beanList;          //此值是父类定义，并解读赋值给item;
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        View _view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_main,
                parent, false);
        return new MyHolder(_view);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, final CatalogBean data) {

        if(viewHolder instanceof MyHolder){
            final MyHolder _myHolder = (MyHolder)viewHolder;
            _myHolder.name.setText(data.getName());
            _myHolder.number.setText(data.getBook_num() + "本");
            if (StringUtils.isNotEmpty(data.getImageUrl()) && data.getImageUrl().startsWith("http")) {
                    ImageUtil.loadImage(mContext, data.getImageUrl(), _myHolder.mImageView, R.mipmap.default_catalog);
            }
            _myHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, ClassifyActivity.class);
                    it.putExtra("obj",data);
                    mContext.startActivity(it);
                }
            });
        }

    }

    public class MyHolder extends Holder{

        TextView name;
        TextView number;
        ImageView mImageView;

        public MyHolder(View view){
            super(view);
            name = (TextView) view.findViewById(R.id.catalog_main_name);
            number = (TextView) view.findViewById(R.id.catalog_book_number);
            mImageView = (ImageView) view.findViewById(R.id.catalog_icon);
        }


    }



}

