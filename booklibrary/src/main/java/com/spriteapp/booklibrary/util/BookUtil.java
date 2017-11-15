package com.spriteapp.booklibrary.util;

import com.spriteapp.booklibrary.enumeration.BookEnum;
import com.spriteapp.booklibrary.model.response.BookChapterResponse;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by kuangxiaoguo on 2017/7/24.
 */

public class BookUtil {

    public static String getBookJson(List<BookDetailResponse> detailResponseList) {
        JSONArray array = new JSONArray();
        for (int i = 0; i < detailResponseList.size(); i++) {
            BookDetailResponse bookDetail = detailResponseList.get(i);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("book_id", bookDetail.getBook_id());
                jsonObject.put("chapter_id", bookDetail.getChapter_id());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(jsonObject);
        }
        return array.toString();
    }

    public static int getCurrentChapterPosition(List<BookChapterResponse> chapterList, int chapterId) {
        int position = 0;
        if (CollectionUtil.isEmpty(chapterList)) {
            return position;
        }

        for (int i = 0; i < chapterList.size(); i++) {
            BookChapterResponse chapterResponse = chapterList.get(i);
            if (chapterResponse.getChapter_id() == chapterId) {
                position = i;
                break;
            }
        }
        return position;
    }

    /**
     * 判断书籍是否已经加入书架
     */
    public static boolean isBookAddShelf(BookDetailResponse bookDetail) {
        return bookDetail != null && bookDetail.getBook_add_shelf() == BookEnum.ADD_SHELF.getValue()
                && bookDetail.getIs_recommend_book() == BookEnum.MY_BOOK.getValue();
    }
}
