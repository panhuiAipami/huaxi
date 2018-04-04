package com.spriteapp.booklibrary.util;

import android.os.Environment;

import com.spriteapp.booklibrary.enumeration.BookEnum;
import com.spriteapp.booklibrary.model.BookList;
import com.spriteapp.booklibrary.model.response.BookChapterResponse;
import com.spriteapp.booklibrary.model.response.BookDetailResponse;
import com.spriteapp.booklibrary.widget.readview.util.Cache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuangxiaoguo on 2017/7/24.
 */

public class BookUtil {
    private static final String cachedPath = Environment.getExternalStorageDirectory() + "/treader/";
    //存储的字符数
    public static final int cachedSize = 30000;
//    protected final ArrayList<WeakReference<char[]>> myArray = new ArrayList<>();

    protected final ArrayList<Cache> myArray = new ArrayList<>();
    //目录
    private List<BookChapterResponse> directoryList = new ArrayList<>();

    private String m_strCharsetName;
    private String bookName;
    private String bookPath;
    private int bookLen;
    private int position;
    private BookList bookList;

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



    public BookUtil(){
        File file = new File(cachedPath);
        if (!file.exists()){
            file.mkdir();
        }
    }



    private void cleanCacheFile(){
        File file = new File(cachedPath);
        if (!file.exists()){
            file.mkdir();
        }else{
            File[] files = file.listFiles();
            for (int i = 0; i < files.length;i++){
                files[i].delete();
            }
        }
    }

    public int next(boolean back){
        position += 1;
        if (position > bookLen){
            position = bookLen;
            return -1;
        }
        char result = current();
        if (back) {
            position -= 1;
        }
        return result;
    }

    public char[] nextLine(){
        if (position >= bookLen){
            return null;
        }
        String line = "";
        while (position < bookLen){
            int word = next(false);
            if (word == -1){
                break;
            }
            char wordChar = (char) word;
            if ((wordChar + "").equals("\r") && (((char)next(true)) + "").equals("\n")){
                next(false);
                break;
            }
            line += wordChar;
        }
        return line.toCharArray();
    }

    public char[] preLine(){
        if (position <= 0){
            return null;
        }
        String line = "";
        while (position >= 0){
            int word = pre(false);
            if (word == -1){
                break;
            }
            char wordChar = (char) word;
            if ((wordChar + "").equals("\n") && (((char)pre(true)) + "").equals("\r")){
                pre(false);
//                line = "\r\n" + line;
                break;
            }
            line = wordChar + line;
        }
        return line.toCharArray();
    }

    public char current(){
//        int pos = (int) (position % cachedSize);
//        int cachePos = (int) (position / cachedSize);
        int cachePos = 0;
        int pos = 0;
        int len = 0;
        for (int i = 0;i < myArray.size();i++){
            long size = myArray.get(i).getSize();
            if (size + len - 1 >= position){
                cachePos = i;
                pos = (int) (position - len);
                break;
            }
            len += size;
        }

        char[] charArray = block(cachePos);
        return charArray[pos];
    }

    public int pre(boolean back){
        position -= 1;
        if (position < 0){
            position = 0;
            return -1;
        }
        char result = current();
        if (back) {
            position += 1;
        }
        return result;
    }

    public int getPosition(){
        return position;
    }

    public void setPostition(int position){
        this.position = position;
    }





    public List<BookChapterResponse> getDirectoryList(){
        return directoryList;
    }

    public long getBookLen(){
        return bookLen;
    }

    protected String fileName(int index) {
        return cachedPath + bookName + index ;
    }

    //获取书本缓存
    public char[] block(int index) {
        if (myArray.size() == 0){
            return new char[1];
        }
        char[] block = myArray.get(index).getData().get();
        if (block == null) {
            try {
                File file = new File(fileName(index));
                int size = (int)file.length();
                if (size < 0) {
                    throw new RuntimeException("Error during reading " + fileName(index));
                }
                block = new char[size / 2];
                InputStreamReader reader =
                        new InputStreamReader(
                                new FileInputStream(file),
                                "UTF-16LE"
                        );
                if (reader.read(block) != block.length) {
                    throw new RuntimeException("Error during reading " + fileName(index));
                }
                reader.close();
            } catch (IOException e) {
                throw new RuntimeException("Error during reading " + fileName(index));
            }
            Cache cache = myArray.get(index);
            cache.setData(new WeakReference<char[]>(block));
//            myArray.set(index, new WeakReference<char[]>(block));
        }
        return block;
    }
}
