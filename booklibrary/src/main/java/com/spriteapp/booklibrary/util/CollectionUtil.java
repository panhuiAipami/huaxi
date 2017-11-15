package com.spriteapp.booklibrary.util;

import com.spriteapp.booklibrary.model.response.BookDetailResponse;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kuangxiaoguo on 2017/7/5.
 */

public class CollectionUtil {

    public static boolean isEmpty(Collection c) {
        return c == null || c.isEmpty();
    }

    public static List<BookDetailResponse> getDiffBook(List<BookDetailResponse> smallSizeList,
                                                       List<BookDetailResponse> biggerSizeList) {
        for (int i = 0; i < smallSizeList.size(); i++) {
            BookDetailResponse nativeDetail = smallSizeList.get(i);
            Iterator<BookDetailResponse> iterator = biggerSizeList.iterator();
            while (iterator.hasNext()) {
                BookDetailResponse serverDetail = iterator.next();
                if (serverDetail.equals(nativeDetail)) {
                    iterator.remove();
                    break;
                }
            }
        }
        return biggerSizeList;
    }

}
