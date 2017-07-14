package net.huaxi.reader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tools.commonlibs.tools.DateUtils;

import java.util.Date;
import java.util.List;

import net.huaxi.reader.R;
import net.huaxi.reader.bean.ChargeRecord;

/**
 * Created by ZMW on 2016/1/9.
 */
public class AdapterRechargeRecord  extends BaseAdapter {
    List<ChargeRecord> recordList;
    Context context;
    public AdapterRechargeRecord (Context context,List<ChargeRecord> recordList){
        this.context=context;
        this.recordList=recordList;
    }
    @Override
    public int getCount() {
        return recordList.size();
    }

    @Override
    public Object getItem(int position) {
        return recordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder=null;
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.item_recharge_record,null);
            holder=new Holder();
            holder.tvOrderCreateTiem= (TextView) convertView.findViewById(R.id.item_recharge_order_create_time_textview);
            holder.tvChangetype= (TextView) convertView.findViewById(R.id.item_recharge_changetype_textview);
            holder.tvPaied= (TextView) convertView.findViewById(R.id.item_recharge_paied_textview);
            holder.tvState= (TextView) convertView.findViewById(R.id.item_recharge_state_textview);
            convertView.setTag(holder);
        }else{
            holder= (Holder) convertView.getTag();
        }
        ChargeRecord record=recordList.get(position);
        holder.tvOrderCreateTiem.setText(DateUtils.simpleDateFormat(new Date(record.getOrCdate()*1000), "yyyy.MM" +
                ".dd HH:mm"));
        String chid=record.getChid();
        String chanelType="1".equals(chid)?"支付宝":"2".equals(chid)?"微信":"3".equals(chid)?"苹果充值":"其他";
        holder.tvChangetype.setText(chanelType);
        holder.tvPaied.setText(record.getOrAmountPaid()+"元");

        holder.tvState.setText("1".equals(record.getOrState()) ? "成功" : "7".equals(record
                .getOrState()) ? "失败" : "其他");
        holder.tvState.setTextColor("1".equals(record.getOrState()) ? context.getResources()
                .getColor(R.color.c01_themes_color) : context.getResources().getColor(R.color.c05_themes_color));
        return convertView;
    }

    class Holder{
        TextView tvOrderCreateTiem,tvChangetype,tvPaied,tvState;
    }
}
