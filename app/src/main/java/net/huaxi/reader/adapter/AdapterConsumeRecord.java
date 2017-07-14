package net.huaxi.reader.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tools.commonlibs.tools.DateUtils;
import net.huaxi.reader.bean.ConsumeRecord;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

import net.huaxi.reader.R;

import net.huaxi.reader.bean.ConsumeRecordCustom;
import com.tools.commonlibs.tools.LogUtils;


/**
 * Created by ZMW on 2015/12/25.
 */
public class AdapterConsumeRecord extends BaseAdapter {
    List<ConsumeRecordCustom> data;

    Activity caller;

    public AdapterConsumeRecord(Activity caller, List<ConsumeRecordCustom> data) {
        this.data = data;
        this.caller = caller;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        if (data != null && position >= 0 && data.size() > position && data.get(position) != null) {
            return data.get(position).getType();
        }
        return super.getItemViewType(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        switch (getItemViewType(position)) {
            case ConsumeRecordCustom.VIEW_TYPE_NOMAL:
                convertView = getNormalConvertView(convertView, position, ConsumeRecordCustom.VIEW_TYPE_NOMAL);
                break;
            case ConsumeRecordCustom.VIEW_TYPE_MONTH:
                convertView = getMonthConvertView(convertView, position, ConsumeRecordCustom.VIEW_TYPE_MONTH);
                break;
        }

        return convertView;
    }

    private View getMonthConvertView(View convertView, int position, int type) {
        MothHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(caller).inflate(R.layout.item_consume_record_month, null);
            holder = new MothHolder(convertView);
            convertView.setTag(holder);
        } else {
            if (null != convertView.getTag() && convertView.getTag() instanceof MothHolder) {
                holder = (MothHolder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(caller).inflate(R.layout.item_consume_record_month, null);
                holder = new MothHolder(convertView);
                convertView.setTag(holder);
            }
        }
        ConsumeRecordCustom consumeRecordCustom = data.get(position);
        holder.tvYearMoth.setText(consumeRecordCustom.getMonthString());
        return convertView;
    }

    private View getNormalConvertView(View convertView, int position, int type) {
        NormalHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(caller).inflate(R.layout.item_consume_record, null);
            holder = new NormalHolder(convertView);
            convertView.setTag(holder);
        } else {
            if (null != convertView.getTag() && convertView.getTag() instanceof NormalHolder) {
                holder = (NormalHolder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(caller).inflate(R.layout.item_consume_record, null);
                holder = new NormalHolder(convertView);
                convertView.setTag(holder);
            }
        }
        ConsumeRecord record = data.get(position);
        holder.tvDate.setText(DateUtils.simpleDateFormat(new Date(record.getCdate() * 1000), "MM.dd"));
        holder.tvTime.setText(DateUtils.simpleDateFormat(new Date(record.getCdate() * 1000), "HH:mm"));
        try {
            JSONObject desc = new JSONObject(record.getCmDesc());
           holder.tvBookName.setText(desc.optString("bk_title"));
            LogUtils.debug("book_title:"+desc.toString());
//            holder.tvCatalogNo.setText(desc.optString("cpt_title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.tvConsumeType.setText("1".equals(record.getCmType()) ? "订阅章节" : "其他");
        LogUtils.debug("record.getCmType"+record.getCmType());
        holder.tvCoinCount.setText("-" + record.getCmCoins());
        LogUtils.debug("record.getCmCoins"+record.getCmCoins());
        holder.tvCoinType.setText("1".equals(record.getCmType()) ? "阅币" : "其他");

        return convertView;
    }


    class NormalHolder {
        TextView tvDate, tvTime, tvBookName, tvCatalogNo, tvCoinCount, tvCoinType, tvConsumeType;

        public NormalHolder(View itemView) {
            tvDate = (TextView) itemView.findViewById(R.id.item_consume_date_textview);
            tvTime = (TextView) itemView.findViewById(R.id.item_consume_time_textview);
            tvBookName = (TextView) itemView.findViewById(R.id
                    .item_consume_bookname_textview);
            tvConsumeType = (TextView) itemView.findViewById(R.id.item_consume_bookname_consume_type);
            tvCoinCount = (TextView) itemView.findViewById(R.id
                    .item_consume_coin_count_textview);
            tvCoinType = (TextView) itemView.findViewById(R.id
                    .item_consume_coin_type_textview);
        }
    }

    class MothHolder {
        TextView tvYearMoth;

        public MothHolder(View itemView) {
            tvYearMoth = (TextView) itemView.findViewById(R.id.item_consume_record_month_textview);
        }
    }

}
