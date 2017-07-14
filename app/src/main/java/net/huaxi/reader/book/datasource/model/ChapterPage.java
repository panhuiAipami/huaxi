package net.huaxi.reader.book.datasource.model;

import java.util.HashMap;

import net.huaxi.reader.book.paging.PageContent;

/**
 * 章节分页.
 * Created by taoyingfeng
 * 2015/12/21.
 */
public class ChapterPage {

    //章节ID
    private String chapterId;
    //章节名称
    private String chapterName;
    //章节长度
    private int length;
    //混存实际的分页内容.
    private HashMap<Integer, PageContent> pageMap;

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public HashMap<Integer, PageContent> getPageMap() {
        return pageMap;
    }

    public void setPageMap(HashMap<Integer, PageContent> pageMap) {
        this.pageMap = pageMap;
    }

    /**
     * 添加分页数据
     *
     * @param num         页码
     * @param pageContent 分页内容
     */
    public void addPageContent(Integer num, PageContent pageContent) {
        if (pageMap == null) {
            pageMap = new HashMap<Integer, PageContent>();
        }
        pageMap.put(num, pageContent);
    }

}
