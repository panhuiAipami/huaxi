package net.huaxi.reader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tools.commonlibs.tools.StringUtils;
import net.huaxi.reader.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import net.huaxi.reader.R;
import net.huaxi.reader.bean.CatalogBean;

/**
 * function:
 * author:      ryantao
 * create:      16/8/2
 * modtime:     16/8/2
 */
public class AdapterMainCatalog extends BaseAdapter {

    private Context mContext;
    private List<CatalogBean> mCatalogBeanList;

    public AdapterMainCatalog(Context context, List<CatalogBean> data) {
        mContext = context;
        mCatalogBeanList = data;
        if (mCatalogBeanList == null) {
            mCatalogBeanList = new ArrayList<CatalogBean>();
        }
    }

    @Override
    public int getCount() {
        return mCatalogBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCatalogBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_category_main, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.catalog_main_name);
            holder.count = (TextView) convertView.findViewById(R.id.catalog_book_number);
            holder.icon = (ImageView) convertView.findViewById(R.id.catalog_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CatalogBean _bean = mCatalogBeanList.get(position);

        holder.title.setText(_bean.getName());
        holder.count.setText(_bean.getBook_num() + "本");
        if (StringUtils.isNotEmpty(_bean.getImageUrl()) && _bean.getImageUrl().startsWith("http")) {
            ImageUtil.loadImage(mContext,_bean.getImageUrl(), holder.icon, R.mipmap.default_catalog);
        }
        return convertView;
    }


    class ViewHolder {
        TextView title;
        TextView count;
        ImageView icon;
    }


}
