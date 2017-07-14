package net.huaxi.reader.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tools.commonlibs.activity.BaseActivity;
import com.tools.commonlibs.tools.StringUtils;
import net.huaxi.reader.book.BookContentSettings;

import java.util.ArrayList;
import java.util.List;

import net.huaxi.reader.R;
import net.huaxi.reader.adapter.AdapterString;

/**
 * 通用的字符串列表
 * ryantao
 * 16/4/11.
 */
public class ListStringActivity extends BaseActivity implements View.OnClickListener {

    List<String> data = new ArrayList<String>();
    ImageView goback;
    TextView title;
    ListView mListView;
    AdapterString adapter;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_string);
        initData();
    }

    private void initData() {
        mListView = (ListView) findViewById(R.id.listview);
        mListView.addFooterView(new View(this));
        goback = (ImageView) findViewById(R.id.toolbar_layout_back);
        goback.setOnClickListener(this);
        title = (TextView) findViewById(R.id.toolbar_layout_title);
        title.setText(getString(R.string.readpage_line_space_set));
        readData();
        adapter = new AdapterString(getActivity(), data);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BookContentSettings.getInstance().setLineStyle(position);
                adapter.notifyDataSetChanged();
                finish();
            }
        });

    }

    private void readData() {
        String[] enums = getActivity().getResources().getStringArray(R.array.line_space_style);
        if (enums != null && enums.length > 0) {
            for (int i = 0; i < enums.length; i++) {
                String value = enums[i];
                if (StringUtils.isNotEmpty(value)) {
                    data.add(value);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toolbar_layout_back) {
            finish();
        }
    }
}
