package net.huaxi.reader.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tools.commonlibs.tools.ViewUtils;

import net.huaxi.reader.R;
import net.huaxi.reader.adapter.AdapterReadRecord;
import net.huaxi.reader.common.EnterBookContent;
import net.huaxi.reader.common.SharePrefHelper;
import net.huaxi.reader.db.dao.BookDao;
import net.huaxi.reader.db.model.BookTable;
import net.huaxi.reader.dialog.CommonDialogFoot;
import net.huaxi.reader.util.UMEventAnalyze;

import java.util.ArrayList;
import java.util.List;

/**
 * Function:    阅读记录
 * Author:      taoyf
 * Create:      16/9/2
 * Modtime:     16/9/2
 */
public class FmReadRecord extends BaseFragment {

    ListView mListview;
    AdapterReadRecord adapterReadRecord;
    List<BookTable> tableList;
    ImageView goback;
    LinearLayout empty;
    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fm_read_record, null);
        init(root);
        return root;
    }

    private void init(View root) {
        mListview = (ListView) root.findViewById(R.id.listview);
        empty = (LinearLayout) root.findViewById(R.id.common_read_no_record);
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.header_read_record,null);
        TextView cleanBtn = (TextView) header.findViewById(R.id.clear_read_record);
        cleanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CommonDialogFoot dialog = new CommonDialogFoot(getActivity(), getString(R.string.read_record_tips), getString(R
                        .string.ok), getString(R.string.cancel));
                dialog.getOkButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tableList != null) {
                            for (BookTable bookTable : tableList) {
                                if (bookTable != null) {
                                    bookTable.setLastReadDate(0);
                                    BookDao.getInstance().updateBook(bookTable);
                                }
                            }
                            tableList.clear();
                        }
                        adapterReadRecord.notifyDataSetChanged();
                        showEmptyView(true);
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });

        tableList = new ArrayList<BookTable>();
        tableList.addAll(BookDao.getInstance().findRecentLyBooks());
        adapterReadRecord = new AdapterReadRecord(getActivity(),tableList);
        mListview.addHeaderView(header);
        mListview.setAdapter(adapterReadRecord);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    //点击最近阅读书籍。
                    BookTable bookTable = tableList.get(position - 1);
                    if (bookTable != null) {
                        UMEventAnalyze.countEvent(getActivity(),UMEventAnalyze.BOOK_SHELF_READED_RECORD_CLICK);
                        EnterBookContent.openBookDetail(getActivity(),bookTable.getBookId());
                    }
                }
            }
        });
        mListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    //点击最近阅读书籍。
                    final BookTable bookTable = tableList.get(position - 1);
                    if (bookTable != null) {
                        final CommonDialogFoot dialog = new CommonDialogFoot(getActivity(), getString(R.string.read_record_delete_tips), getString(R
                                .string.ok), getString(R.string.cancel));
                        dialog.getOkButton().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (tableList != null) {
                                    tableList.remove(bookTable);
                                    bookTable.setLastReadDate(0);
                                    BookDao.getInstance().updateBook(bookTable);
                                }
                                if (tableList != null && tableList.size() == 0) {
                                    showEmptyView(true);
                                }
                                adapterReadRecord.notifyDataSetChanged();
                                dialog.cancel();
                            }
                        });
                        dialog.show();
                    }
                    return true;
                }
                return false;
            }
        });
        if (tableList != null && tableList.size() > 0) {
            showEmptyView(false);
        }else{
            showEmptyView(true);
        }
    }

    public void showEmptyView(boolean visiable) {
        if(visiable) {
            mListview.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
        }else{
            mListview.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!SharePrefHelper.hasShowReadRecordToast() && tableList != null && tableList.size() > 0) {
            ViewUtils.toastShort("长按删除记录");
            SharePrefHelper.setHasShowReadRecordToast(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //关闭最近阅读界面。
        UMEventAnalyze.countEvent(getActivity(), UMEventAnalyze.BOOK_SHELF_SHOW_LAST);
    }

}
