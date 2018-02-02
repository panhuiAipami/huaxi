package com.spriteapp.booklibrary.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import com.spriteapp.booklibrary.R;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import java.util.List;

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
        text.setTextColor(ContextCompat.getColor(context, R.color.colorHistory));
        text.setBackgroundResource(R.drawable.comment_edit);
        text.setText(title);
        return text;
    }
}
