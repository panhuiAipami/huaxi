package net.huaxi.reader.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tools.commonlibs.dialog.BaseDialog;
import net.huaxi.reader.book.ChapterClickListener;

import java.util.ArrayList;
import java.util.List;

import net.huaxi.reader.R;
import net.huaxi.reader.adapter.XSAdapter;
import net.huaxi.reader.adapter.XSViewHolder;
import net.huaxi.reader.common.AppContext;
import net.huaxi.reader.db.model.ChapterTable;


/**
 * Created by Saud on 16/1/5.
 */
public class BookDetailCatalogueDialog extends BaseDialog implements AdapterView.OnItemClickListener {

    private  View loadingView;
    private ImageView iv_bookconten_catalogue_back;
    private TextView tv_bookcontent_catatlogue_Chapter_head;
    private Activity activity;
    private View ll_bookconten_catalogue_btn;
    private ListView lv_bookcontent_catalogue;
    private View headerView;
    private List<ChapterTable> list = new ArrayList<>();
    private ChapterClickListener chapterClickListener;

    public BookDetailCatalogueDialog(Activity activity) {
        this.activity = activity;

        initDialog(activity, null, R.layout.dialog_bookcontent_catalogue, BaseDialog.TYPE_CENTER_NOMENUCANCEL, true);
        mDialog.getWindow().setWindowAnimations(R.style.RightPopupDialog);
        WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;

        ll_bookconten_catalogue_btn = mDialog.findViewById(R.id.ll_bookconten_catalogue_btn);
        loadingView = mDialog.findViewById(R.id.detail_catalogue_loading);
        lv_bookcontent_catalogue = (ListView) mDialog.findViewById(R.id.lv_bookcontent_catalogue);
        lv_bookcontent_catalogue.setOnItemClickListener(this);
        iv_bookconten_catalogue_back = (ImageView) mDialog.findViewById(R.id.iv_bookconten_catalogue_back);
        headerView = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_bookdetail_catalogue_head, null);
        tv_bookcontent_catatlogue_Chapter_head = (TextView) headerView.findViewById(R.id.tv_bookcontent_catatlogue_chapter_head);
        ll_bookconten_catalogue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
//        List<ChapterTable> chapterList = ((BookDetailActivity) activity).getChaterList();
//        List<ChapterTable> chapterList = ((BookInfoActivity) activity).getChaterList();
//        if (chapterList != null) {
//            list.addAll(chapterList);
//        }
        initView();
    }

    public void setData(List<ChapterTable> chapterList) {
        list.clear();
        if (chapterList != null) {
            list.addAll(chapterList);
        }
        bindData();
    }

    public List<ChapterTable> getData() {
        return list;
    }

    /**
     * 绑定数据
     */
    private void bindData() {
        if (list != null) {
//            adapter.setmDatas(list);
            final String text = String.format(AppContext.context().getString(R.string.chapter_count), list.size());
            tv_bookcontent_catatlogue_Chapter_head.setText(text);
            loadingView.setVisibility(View.GONE);
            lv_bookcontent_catalogue.setVisibility(View.VISIBLE);
        }
    }


    //数据库获取数据


    /**
     * 设置目录的样式
     */
    private void setStyle() {
        iv_bookconten_catalogue_back.setVisibility(View.GONE);
        lv_bookcontent_catalogue.setBackgroundColor(activity.getResources().getColor(R.color.bookcontent_type0_background1));
        lv_bookcontent_catalogue.setDivider(activity.getResources().getDrawable(R.color.bookcontent_type0_listview_driver));
        lv_bookcontent_catalogue.setDividerHeight(1);
    }


    private void initView() {
        lv_bookcontent_catalogue.addHeaderView(headerView);
        lv_bookcontent_catalogue.setAdapter(new XSAdapter<ChapterTable>(activity, R.layout.item_detail_catalogue, list) {

            @Override
            public void convert(XSViewHolder holder, ChapterTable chapterTable) {
                if (chapterTable != null) {
                    holder.setText(R.id.tv_bookcontent_catatlogue_Chapter_text, chapterTable.getName());
                }
            }
        });
    }


    public Dialog getDialog() {
        return mDialog;
    }

    public void show() {
        setStyle();
        mDialog.show();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (list != null && position != 0) {
            ChapterTable chapterTable = list.get(position - 1);
            if (chapterTable != null) {
                String chpterId = chapterTable.getChapterId();
                if (chapterClickListener != null) {
                    chapterClickListener.onCatalogItemClick(chpterId);
                }
            }
        }
        mDialog.dismiss();
    }

    public void setChapterClickListener(ChapterClickListener chapterClickListener) {
        this.chapterClickListener = chapterClickListener;

    }


}
