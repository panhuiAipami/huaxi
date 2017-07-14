package net.huaxi.reader.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import net.huaxi.reader.R;
import net.huaxi.reader.bean.SearchKeyBean;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class AdapterSearchKey extends BaseAdapter implements
        StickyListHeadersAdapter, SectionIndexer {

    private final Context mContext;
    private List<SearchKeyBean> datas;
    private int[] mSectionIndices;
    private LayoutInflater mInflater;
    private String key;

    public AdapterSearchKey(Context context, List<SearchKeyBean> datas) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.datas = datas;
        mSectionIndices = getSectionIndices();
    }

    private int[] getSectionIndices() {
        ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
        int itemTag = 0;
        if (datas.size() > 0) {
            itemTag = datas.get(0).getTag();
        }
        sectionIndices.add(0);
        for (int i = 1; i < datas.size(); i++) {
            if (datas.get(i).getTag() != itemTag) {
                itemTag = datas.get(i).getTag();
                sectionIndices.add(i);
            }
        }
        int[] sections = new int[sectionIndices.size()];
        for (int i = 0; i < sectionIndices.size(); i++) {
            sections[i] = sectionIndices.get(i);
        }
        return sections;
    }


    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_search_key, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.tv_search_key);
            holder.ivTag = (ImageView) convertView.findViewById(R.id.iv_search_tag);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        if (datas.get(position).getTag() == 1) {
            holder.ivTag.setImageResource(R.mipmap.search_author);
        } else {
            holder.ivTag.setImageResource(R.mipmap.search_booktitle);
        }
        String sKey = datas.get(position).getKey();
        SpannableString ss = new SpannableString(sKey);
        int index = sKey.toUpperCase().indexOf(key.toUpperCase());
        if (-1 != index) {
            ss.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.c01_themes_color)), index, index + key.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        holder.text.setText(ss);

        return convertView;
    }


    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {

        HeaderViewHolder holder;

        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.item_search_header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        // set header text as first char in name
        if (datas.get(position).getTag() == 1) {
            holder.text.setText("作者");
        } else {
            holder.text.setText("作品");
        }
        return convertView;
    }

    /**
     * Remember that these have to be static, postion=1 should always return
     * the same Id that is.
     */
    @Override
    public long getHeaderId(int position) {
        // return the first character of the country as ID because this is what
        // headers are based upon
        return datas.get(position).getTag();
    }

    @Override
    public int getPositionForSection(int section) {
        if (mSectionIndices.length == 0) {
            return 0;
        }

        if (section >= mSectionIndices.length) {
            section = mSectionIndices.length - 1;
        } else if (section < 0) {
            section = 0;
        }
        return mSectionIndices[section];
    }

    @Override
    public int getSectionForPosition(int position) {
        for (int i = 0; i < mSectionIndices.length; i++) {
            if (position < mSectionIndices[i]) {
                return i - 1;
            }
        }
        return mSectionIndices.length - 1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    public void setKey(String key) {
        this.key = key;
    }


    class ViewHolder {
        TextView text;
        ImageView ivTag;
    }

    class HeaderViewHolder {
        TextView text;
    }
}
