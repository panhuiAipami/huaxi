package net.huaxi.reader.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tools.commonlibs.tools.DateUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.bean.ChargeRecord;

import java.util.Date;
import java.util.List;

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
            holder.item_order_number_textview= (TextView) convertView.findViewById(R.id.item_order_number_textview);
            holder.cash= (TextView) convertView.findViewById(R.id.cash);
            holder.coins= (TextView) convertView.findViewById(R.id.coins);
            holder.petal= (TextView) convertView.findViewById(R.id.petal);
            convertView.setTag(holder);
        }else{
            holder= (Holder) convertView.getTag();
        }
        ChargeRecord record=recordList.get(position);
//        Log.i("jie", "getView: 哈哈哈"+record.toString());
        long cdate = record.getCdate();
        Log.i("cdate", "getView: "+cdate);
        holder.tvOrderCreateTiem.setText(DateUtils.simpleDateFormat(new Date(cdate * 1000),"yyyy-MM-dd HH:mm"));
        String chid=record.getChid();
        //显示订单号
        holder.item_order_number_textview.setText("订单号："+record.getOrsn());
        String chanelType="1".equals(chid)?"支付宝":"2".equals(chid)?"微信":"3".equals(chid)?"苹果充值":"其他";
        holder.coins.setText(record.getOrCoins()+"花贝");
        holder.cash.setText(record.getOrAmountPaid()+"元");

//        holder.tvState.setText("1".equals(record.getOrState()) ? "成功" : "7".equals(record
//                .getOrState()) ? "失败" : "其他");
        holder.petal.setText(record.getPetal()+"花瓣");
//        holder.tvState.setTextColor("1".equals(record.getOrState()) ? context.getResources()
//                .getColor(R.color.c01_themes_color) : context.getResources().getColor(R.color.c05_themes_color));
        return convertView;
    }

    class Holder{
        TextView tvOrderCreateTiem,item_order_number_textview,cash,coins,petal;
    }

}
