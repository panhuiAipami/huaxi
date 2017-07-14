package net.huaxi.reader.util;

import java.io.File;

import net.huaxi.reader.book.FileConstant;
import net.huaxi.reader.common.Utility;

/**
 * Created by Saud on 16/1/19.
 */
public class XSFileUtils {


    public static boolean ChapterExists(String bookId, String chapterId) {
//        String filePath = Utility.getBookRootPath() + bookId + File.separator;
//        File file = new File(filePath + chapterId + "." + FileConstant.XSREADER_FILE_SUFFIX);
//        return file.exists();
        return new File(Utility.getChapterFilePath(bookId, chapterId, FileConstant.XSREADER_FILE_SUFFIX)).exists();
    }
}
