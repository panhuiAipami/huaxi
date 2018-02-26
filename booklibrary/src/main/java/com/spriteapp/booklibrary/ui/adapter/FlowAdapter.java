package com.spriteapp.booklibrary.ui.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.spriteapp.booklibrary.R;
import com.spriteapp.booklibrary.util.ColorUtils;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;

import java.util.List;

import static com.spriteapp.booklibrary.util.ColorUtils.bgColors;
import static com.spriteapp.booklibrary.util.ColorUtils.textColors;

/**
 * Created by Administrator on 2017/12/1.
 */

public class FlowAdapter extends TagAdapter<String> {
    private Context context;

    public FlowAdapter(List datas, Context context) {
        super(datas);
        this.context = context;
    }

    @Override
    public View getView(FlowLayout parent, int position, final String title) {
        TextView text = new TextView(context);
        text.setPadding(45, 15, 45, 15);
        //R.color.colorHistory
        try {
            int random = ColorUtils.getRandom();
            text.setTextColor(ContextCompat.getColor(context, textColors.get(random)));
            text.setBackgroundResource(R.drawable.search_bg);
            GradientDrawable dr = (GradientDrawable) text.getBackground();
            dr.setColor(ContextCompat.getColor(context, bgColors.get(random)));
            text.setText(title);
            text.getPaint().setFakeBoldText(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }
}
