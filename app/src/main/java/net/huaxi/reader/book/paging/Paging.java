package net.huaxi.reader.book.paging;

import android.graphics.Paint;
import android.text.TextUtils;

import com.tools.commonlibs.tools.LogUtils;
import net.huaxi.reader.book.BookContentLineInfo;
import net.huaxi.reader.book.BookContentSettings;
import net.huaxi.reader.book.datasource.model.ChapterPage;
import net.huaxi.reader.book.paging.encode.GBKEncoding;
import net.huaxi.reader.book.render.ReadPageState;
import net.huaxi.reader.common.Utility;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import net.huaxi.reader.book.paging.encode.IEncoding;
import net.huaxi.reader.book.paging.encode.UTF8Encoding;

/**
 * 分页排版
 * Created by taoyingfeng on 2015/12/3.
 */
public class Paging {


    /**
     * 段首缩进字符，默认四个空格
     */
    public static final String INDENT_STRING = "    ";
    /**
     * 段间距标识
     */
    public static final String PARAGRAPH_SPACE = "sq|pgh";
    /**
     * 章节尾部评论标识
     */
    public static final String COMMENT_MARK = "content|mark";
    private static final String DIANDIANDIAN = "...";
    private static final String BLANK = "　";
    /**
     * 半角空格
     */
    private static final String BLANL_SEMIANGLE = " ";
    /**
     * 段首缩进字符长度
     */
    private static final int INDENT_LENGTH = 2;

    /**
     * 首行不能包含的字符集合
     */
    private static Set<Character> errorChar = new HashSet<Character>();
    String s = "\\s";// A3A0
    BookContentSettings mBookContentRenderSetting = null;

    public Paging() {
        initErrorChar();
        mBookContentRenderSetting = BookContentSettings.getInstance();
    }

    /**
     * @param chapterId   章节ID
     * @param chapterName 章节名称
     * @param data        章节字节流
     * @param encoding    编码格式
     * @param bookType
     * @return
     * @throws Exception
     */
    public ChapterPage getChapterPageInfos(String chapterId, String chapterName, byte[] data, String encoding, int bookType) throws
            Exception {
        List<BookContentLineInfo> lineInfos = getLineinfosByIndex(null, data, 0, 0, encoding, bookType);
        ChapterPage result = calcPageLineInfos(chapterId, chapterName, lineInfos, bookType);
        if (result != null && data != null && data.length > 0) {
            result.setLength(data.length);
        }
        return result;
    }

    private ChapterPage calcPageLineInfos(String chapterId, String chapterName, List<BookContentLineInfo> lineInfos, int bookType) throws
            Exception {
        if (lineInfos == null) {
            return null;
        }
        ChapterPage chapterPage = new ChapterPage();
        chapterPage.setChapterId(chapterId);
        chapterPage.setChapterName(chapterName);
        PageContent pageContent = null;
        int textSize = mBookContentRenderSetting.getTextSize();      //字体宽高
        int titleSize = mBookContentRenderSetting.getTitleTextSize();  //正文标题高度。
        if (bookType != ReadPageState.BOOKTYPE_NORMAL) {
            textSize = mBookContentRenderSetting.getDefaultTextSize();
        }
        final int lineSpace = (int) mBookContentRenderSetting.getLineSpace();   //行间距
        final int viewH = viewHeight(); //屏幕可视宽高
        final int spaceFailover = mBookContentRenderSetting.getTextSize() / 3;  //失败高度
        final int initY = mBookContentRenderSetting.getMarginTop() + mBookContentRenderSetting
                .getTopToTitle() + lineSpace + mBookContentRenderSetting.getTitleTextSize();  //Y坐标的初始高度(标题的Y坐标).
        final int paragraphHeigth = mBookContentRenderSetting.getParagraphSpace();  //段间距;
        LogUtils.info(" 行间距 = " + lineSpace + " 段间距 = " + paragraphHeigth);
        int y = initY;
        int pageNo = 1;
        int startIndex = -1;
        int endIndex = -1;
        int lineHeight = textSize + lineSpace;  //正文行高
        int titleLineHeight = titleSize + lineSpace;  //标题行高
        boolean hasCalcTitleHeight = false;
        List<BookContentLineInfo> pageLines = null;
        for (int j = 0; j < lineInfos.size(); j++) {
            BookContentLineInfo curInfo = lineInfos.get(j);
            if (curInfo != null) {
                String str = curInfo.getContent();
                setAllLineY(curInfo.getLineXys(), y);
                debug("【draw】str=" + str + ",第 " + (j + 1) + "行,y =" + y + ",star=" + curInfo.getFirstIndex() + ",end=" +
                        curInfo.getEndIndex() + ",type=" + curInfo.getLineType());
                if (pageLines == null) {
                    pageLines = new ArrayList<BookContentLineInfo>();
                }
                if (!PARAGRAPH_SPACE.equals(curInfo.getContent())) {
                    pageLines.add(curInfo);
                } else {
                    //首行内容为段间隔(sq|pgh)
                    if (y == initY) {
                        continue;
                    }
                }
                if (y == initY) {
                    startIndex = curInfo.getFirstIndex();
                    endIndex = curInfo.getEndIndex();
                } else {
                    if (curInfo.getEndIndex() >= endIndex) {
                        endIndex = curInfo.getEndIndex();
                    }
                }
                if (!hasCalcTitleHeight) {
                    //预判下一行是不是正文,如果是正文，修改标识。
                    int nextIndex = j + 1;
                    if (nextIndex < lineInfos.size()) {
                        BookContentLineInfo nextInfo = lineInfos.get(nextIndex);
                        if (nextInfo != null && BookContentLineInfo.LINETYPE.CHAPTERTITLE.getType() != nextInfo.getLineType()) {
                            hasCalcTitleHeight = true;
                        }
                    }

                }
                //计算下一行内容的纵坐标
                if (curInfo.getLineType() == BookContentLineInfo.LINETYPE.PARAGRASH_END.getType() || (curInfo.getLineType() == BookContentLineInfo.LINETYPE.CHAPTERTITLE.getType
                        () && hasCalcTitleHeight)) {
                    y += paragraphHeigth;
                } else {
                    if (!hasCalcTitleHeight) {
                        y += titleLineHeight;
                    } else {
                        y += lineHeight;
                    }
                }
                // 当前数据绘制超过一屏
                if (y - lineHeight + spaceFailover > viewH) {
                    pageContent = new PageContent();
                    pageContent.setChapterId(chapterId);
                    pageContent.setStartIndex(startIndex);
                    pageContent.setEndIndex(endIndex);
                    pageContent.setChapterName(chapterName);
                    pageContent.setLines(pageLines);
                    pageContent.setPageNo(pageNo);
                    chapterPage.addPageContent(pageNo, pageContent);
                    y = initY;
                    pageLines = null;
                    pageNo++;
                    pageContent = null;
                    startIndex = -1;
                    endIndex = -1;
                    debug("当前数据绘制超过一屏");
//                    LogUtils.info("当前页内容 " + chapterPage.toString());
                    continue;
                }
                if (j == lineInfos.size() - 1) {
                    pageContent = new PageContent();
                    pageContent.setChapterId(chapterId);
                    pageContent.setStartIndex(startIndex);
                    pageContent.setEndIndex(endIndex);
                    pageContent.setChapterName(chapterName);
                    pageContent.setLines(pageLines);
                    pageContent.setPageNo(pageNo);
                    chapterPage.addPageContent(pageNo, pageContent);
                }
                curInfo = null;
            }
        }

        return chapterPage;
    }

    /**
     * @param list
     * @param data
     * @param startByte
     * @param divideByte
     * @param encoding
     * @param bookType
     * @return list
     * @throws UnsupportedEncodingException
     */
    private List<BookContentLineInfo> getLineinfosByIndex(List<BookContentLineInfo> list, byte[] data, int startByte, int divideByte,
                                                          String encoding, int bookType) throws Exception {
        if (data == null || data.length < 1) {
            return null;
        }
        if (list == null) list = new CopyOnWriteArrayList<BookContentLineInfo>();
        int divByte = -1;   //分割字节,保证数据在一行
        /** [0]需要显示索引之前的内容，[1]需要显示索引之后的内容 */
        String content[] = new String[2];
        boolean isChangeFirstByteIndex = false;// 控制是否改变更改字体时的定位字节索引
        // 防止越界
        if (divideByte < 0) {
            divideByte = 0;
        } else if (divideByte > data.length) {
            divideByte = data.length - 1;
            debug("[拆分字节大于文件长度]divideByte=" + divideByte);
            isChangeFirstByteIndex = true;
        }
        divByte = divideByte;
        int readLenth1 = 0;// 前一段文字实际读取的长度
        int readLenth2 = 0;// 后一段文字实际读取的长度

        try {
            IEncoding iEncoding;
            if (IEncoding.ENCODING_GBK.equalsIgnoreCase(encoding)) {
                iEncoding = new GBKEncoding();
                if (divideByte > 0) {
                    readLenth1 = iEncoding.toChars(data, 0, divideByte - 1);
                    content[0] = new String(iEncoding.mValues);
                    divideByte = readLenth1;
                }
                if (divideByte < data.length) {
                    readLenth2 = iEncoding.toChars(data, readLenth1, data.length);
                    content[1] = new String(iEncoding.mValues);
                }
            } else if (IEncoding.ENCODING_UTF8.equalsIgnoreCase(encoding)) {
                iEncoding = new UTF8Encoding();
                if (divideByte > 0) {
                    readLenth1 = iEncoding.toChars(data, 0, divideByte - 1);
                    content[0] = new String(iEncoding.mValues);
                    debug("【content[0]】" + content[0]);
                    divideByte = readLenth1;
                }
                if (divideByte < data.length) {
                    readLenth2 = iEncoding.toChars(data, divideByte, data.length);
                    content[1] = new String(iEncoding.mValues);
                    debug("【content[1]】" + content[1]);
                }
            } else {
                content[0] = new String(data, encoding);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        BookContentLineInfo line;
        // 换行：1：遇到\n 2,字数达到LineMaxNum:15字
        int lastLineEndCharIndex = 0; // 上一行最后一个字符的索引
        int lastLineEndByteIndex = startByte = 0; // 上一行最后一个字节的索引
        boolean addParagraphSpaceTag = true;// 是否添加段间距标记(若遇到标题下面一行是""字符串时，置为false)
        boolean addParagraphStartTag = false;// 添加段落开始标记(to copy model)
        boolean isLastContent = false;// 更改字号时,首字符定位是否从上一段数据获取
        boolean readingTitle = false;   //标识读取到标题,用来处理标题过长换行问题。
        boolean hasReadTitle = false;   //防止章节内标题重复问题。
        BookContentPaint paint = new BookContentPaint();

        for (int j = 0; j < content.length; j++) {

            LogUtils.debug("paging........"+content[j]);
            debug("j=" + j);
            lastLineEndCharIndex = 0;
            if (!TextUtils.isEmpty(content[j])) {
                float[] titleWidth = null;
                float[] widths = new float[content[j].length()];
                String tempTitle = content[j].substring(0, content[j].indexOf("\r\n"));
                LogUtils.debug("paging........"+content.length);
                //解析内容的宽度
                if (bookType == ReadPageState.BOOKTYPE_NORMAL) {
                    //解析标题的宽度
                    if (Utility.isTitle(tempTitle) && !hasReadTitle) {
                        readingTitle = true;    //章节开头找到标题。
                        hasReadTitle = true;
                        titleWidth = new float[tempTitle.length()];
                        paint.changeStyleToContentTitle();
                        paint.getTextWidths(tempTitle, titleWidth);
                    }
                    paint.changeStyleToContent();   //正文内容
                } else {
                    paint.changeStyleToDesc();  //描述
                }
                paint.getTextWidths(content[j], widths);
                //读取titelWidth，覆盖标题的宽度
                if (readingTitle && titleWidth != null && titleWidth.length > 0) {
                    for (int i = 0; i < titleWidth.length; i++) {
                        widths[i] = titleWidth[i];
                    }
                }

                int indentLength = 0;
                float curLineWidth = 0;
                final int BLANK_WIDTH = getStringWidth(paint, BLANK);
                boolean lastLineEndIsEnter = false;
                for (int i = 0; i < content[j].length(); i++) {
                    debug("the " + i + "-> char  = " + content[j].charAt(i));
                    if (content[j].charAt(i) == '\n') {
                        //// TODO: 2015/12/9 排版时，换行判断
                        debug("lastLineEndIsEnter=" + lastLineEndIsEnter);
                        if (lastLineEndIsEnter) {
                            lastLineEndCharIndex++;
                            lastLineEndByteIndex += getContentRealByteLength("\n", encoding);
                            lastLineEndIsEnter = true;
                            curLineWidth = 0;
                            indentLength = 0;
                            continue;
                        } else {
                            String srcContent = content[j].substring(lastLineEndCharIndex, i);
                            if (srcContent != null && srcContent.trim().length() == 1) {
                                addParagraphSpaceTag = true;  //段间距标记
                            }
                            if (srcContent != null && srcContent.trim().length() == 0) {  //真实内容为空。
                                lastLineEndCharIndex += srcContent.length() + 1;
                                lastLineEndByteIndex += getContentRealByteLength("\n", encoding);
                                lastLineEndIsEnter = true;
                                readingTitle = false;
                                line = new BookContentLineInfo();
                                line.setContent(PARAGRAPH_SPACE);
                                list.add(line);
                                curLineWidth = 0;
                                indentLength = 0;
                                continue;
                            }
                        }
                        line = new BookContentLineInfo();
                        line.setEnter(true);
                        list.add(line);
                        String srcContent = content[j].substring(lastLineEndCharIndex, i);
                        debug("1.indentLength=" + indentLength);
                        if (Utility.isTitle(srcContent) && !hasReadTitle) {
                            //// TODO: 2015/12/9 排版时，标题判断
                            indentLength = 0;
                            line.setLineType(BookContentLineInfo.LINETYPE.CHAPTERTITLE.getType());

                        } else {
                            if (!TextUtils.isEmpty(srcContent) && srcContent.length() > 4 && srcContent.startsWith(INDENT_STRING)) {
                                lastLineEndCharIndex += 2;
                                srcContent = content[j].substring(lastLineEndCharIndex, i);
                                debug("【A遇到多个空格】str=" + srcContent + ",str is null=" + (srcContent == null ? true : srcContent.trim()
                                        .length()));
                            } else if (srcContent.startsWith(BLANK)) {
                                lastLineEndCharIndex += getBlankCount(BLANK, srcContent);
                                debug("【a遇到BLANK】lastLineEndCharIndex=" + lastLineEndCharIndex + ",BLANK.length()=" + BLANK.length() * 2);
                                srcContent = content[j].substring(lastLineEndCharIndex, i);
                                indentLength = 2;
                                debug("【A遇到BLANK】srcContent=" + srcContent + ",len=" + srcContent.length());
                            }
                            if (readingTitle) {
                                readingTitle = false;
                                indentLength = 0;
                                line.setLineType(BookContentLineInfo.LINETYPE.CHAPTERTITLE.getType());
                            } else {
                                line.setLineType(BookContentLineInfo.LINETYPE.PARAGRASH_END.getType());
                            }
                        }
                        line.setContent(INDENT_STRING.substring(0, indentLength) + srcContent);
                        line.setFirstIndex(lastLineEndByteIndex);
                        line.setEndIndex(lastLineEndByteIndex + getContentRealByteLength(srcContent, encoding) + 1);
                        debug("【1】" + line.getContent() + ",start=" +
                                line.getFirstIndex() + ",end=" + line.getEndIndex() + ",linType=" + line.getLineType());
                        setXYS(line, widths, lastLineEndCharIndex, false, BLANK_WIDTH, indentLength);
                        lastLineEndCharIndex += srcContent.length() + 1;
                        lastLineEndByteIndex = line.getEndIndex() + 1;
                        indentLength = 0;
                        lastLineEndIsEnter = true;
                        curLineWidth = 0;

                        // 遇到段落结尾，添加段标记
                        if (addParagraphSpaceTag) {
                            line = new BookContentLineInfo();
                            line.setContent(PARAGRAPH_SPACE);
                            list.add(line);
                        }
                        addParagraphSpaceTag = true;
                        addParagraphStartTag = true;
                        readingTitle = false;
                        continue;
                    }

                    if (lastLineEndIsEnter) {
                        /* 如果上一行结束是'/n'换行符 */
                        addParagraphStartTag = true;
                        lastLineEndIsEnter = false;
                        final int size = lastLineEndCharIndex + Math.min(content[j].length() - 1 - lastLineEndCharIndex, INDENT_LENGTH);

                        for (int x = lastLineEndCharIndex; x < size; x++) {
                            if (!Character.isWhitespace(content[j].charAt(x))) {
                                indentLength = size - x;
                                curLineWidth += (BLANK_WIDTH * indentLength);
                                break;
                            }
                        }
                    }

                    curLineWidth += Character.isWhitespace(content[j].charAt(i)) ? BLANK_WIDTH : widths[i];
                    if (curLineWidth > viewWidth()) {
                        if ((errorChar.contains(content[j].charAt(i))) && (i > 0 && !errorChar.contains(content[j].charAt(i - 1)))) {
                            //// TODO: 2015/12/9 处理标点符号
                            line = new BookContentLineInfo();
                            list.add(line);
                            String srcContent = content[j].substring(lastLineEndCharIndex, i - 1);
                            if (srcContent.startsWith(INDENT_STRING)) {
                                lastLineEndCharIndex += 2;
                                srcContent = content[j].substring(lastLineEndCharIndex, i - 1);
                                addParagraphStartTag = true;
                                debug("【B标点遇到多个空格】srcContent=" + srcContent);
                            } else if (srcContent.startsWith(BLANK)) {
                                lastLineEndCharIndex += getBlankCount(BLANK, srcContent);
                                debug("【B遇到BLANK】lastLineEndCharIndex=" + lastLineEndCharIndex + ",BLANK.length()=" + BLANK.length() * 2);
                                srcContent = content[j].substring(lastLineEndCharIndex, i - 1);
                                indentLength = 2;
                                debug("【B遇到BLANK】srcContent=" + srcContent + ",len=" + srcContent.length());
                            }

                            line.setContent(INDENT_STRING.substring(0, indentLength) + srcContent);
                            line.setFirstIndex(lastLineEndByteIndex);
                            line.setEndIndex(lastLineEndByteIndex + getContentRealByteLength(srcContent, encoding) - 1);
                            if (readingTitle) {
                                line.setLineType(BookContentLineInfo.LINETYPE.CHAPTERTITLE.getType());
                            } else {
                                if (addParagraphStartTag) {
                                    line.setLineType(BookContentLineInfo.LINETYPE.PARAGRAPH_START.getType());
                                }
                            }
                            debug("【2】" + line.getContent() + ",start=" + line.getFirstIndex() + ",end=" + line.getEndIndex() +
                                    ",linType=" + line.getLineType() + "indentLength=" + indentLength);
                            setXYS(line, widths, lastLineEndCharIndex, true, BLANK_WIDTH, indentLength);
                            lastLineEndCharIndex += srcContent.length();
                            lastLineEndByteIndex = line.getEndIndex() + 1;
                            addParagraphStartTag = false;
                            indentLength = 0;
                        } else {
                            //// TODO: 2015/12/9 正常折行
                            line = new BookContentLineInfo();
                            list.add(line);
                            String srcContent = content[j].substring(lastLineEndCharIndex, i);
                            if (srcContent.startsWith(INDENT_STRING)) {
                                lastLineEndCharIndex += INDENT_STRING.length();
                                debug("【C遇到多个空格】lastLineEndCharIndex=" + srcContent + ",INDENT_STRING.length()=" +
                                        INDENT_STRING.length());
                                srcContent = content[j].substring(lastLineEndCharIndex, i);
                                debug("【C遇到多个空格】srcContent=" + srcContent);
                                addParagraphStartTag = true;
                                indentLength = 2;
                            } else if (srcContent.startsWith(BLANK)) {
                                lastLineEndCharIndex += getBlankCount(BLANK, srcContent);
                                debug("【C遇到BLANK】lastLineEndCharIndex=" + lastLineEndCharIndex + ",BLANK.length()=" + BLANK.length() * 2);
                                srcContent = content[j].substring(lastLineEndCharIndex, i);
                                indentLength = 2;
                                debug("【C遇到BLANK】srcContent=" + srcContent + ",len=" + srcContent.length());
                            } else if (srcContent.startsWith("\\s")) {
                                debug("【C遇到\\s】str=" + srcContent + ",str is null=" + (srcContent == null ? true : srcContent.trim()
                                        .length()));
                                lastLineEndCharIndex += 2;
                                srcContent = content[j].substring(lastLineEndCharIndex, i);
                                addParagraphStartTag = true;
                                indentLength = 2;
                            } else if (srcContent.startsWith(" ")) {
                                debug("【C遇到英文空格】str=" + srcContent + ",str is null=" + (srcContent == null ? true : srcContent.trim()
                                        .length()));
                                lastLineEndCharIndex += getBlankCount(" ", srcContent);
                                srcContent = content[j].substring(lastLineEndCharIndex, i);
                                addParagraphStartTag = true;
                                indentLength = 2;
                            }
                            if (isLastContent) {
                                indentLength = 0;
                                isLastContent = false;
                            }
                            line.setContent(INDENT_STRING.substring(0, indentLength) + srcContent);
                            line.setFirstIndex(lastLineEndByteIndex);
                            line.setEndIndex(lastLineEndByteIndex + getContentRealByteLength(srcContent, encoding) - 1);
                            if (readingTitle) {
                                line.setLineType(BookContentLineInfo.LINETYPE.CHAPTERTITLE.getType());
                            } else {
                                if (addParagraphStartTag) {
                                    line.setLineType(BookContentLineInfo.LINETYPE.PARAGRAPH_START.getType());
                                }
                            }
                            debug("【3】" + line.getContent() + ",start=" + line.getFirstIndex() + ",end=" + line.getEndIndex() +
                                    ",linType=" + line.getLineType() + ",indentLength=" + indentLength + ",len=" + srcContent.length());
                            setXYS(line, widths, lastLineEndCharIndex, true, BLANK_WIDTH, indentLength);
                            lastLineEndCharIndex += srcContent.length();
                            lastLineEndByteIndex = line.getEndIndex() + 1;
                            addParagraphStartTag = false;
                        }

                        indentLength = 0;
                        curLineWidth = Character.isWhitespace(content[j].charAt(i)) ? BLANK_WIDTH : widths[i];

                        if ((i + 1) == content[j].length()) {
                            //// TODO: 2015/12/9 当剩余字数不足一行时
                            final String contentStr = content[j].substring(lastLineEndCharIndex, i + 1);

                            if (contentStr == null || isBlank(contentStr)) {
                                continue;
                            }
                            debug("【4】 lastLineEndByteIndex=" + lastLineEndByteIndex + ",divByte=" + divByte);
                            if (lastLineEndByteIndex == divByte) {
                                content[1] = contentStr + content[1];
                                debug("【4】 content[1]=" + content[1]);
                                divByte = -1;
                                isLastContent = true;
                                break;
                            }


                            line = new BookContentLineInfo();
                            list.add(line);
                            curLineWidth = 0f;
                            line.setContent(contentStr);
                            line.setFirstIndex(lastLineEndByteIndex);
                            line.setEndIndex(lastLineEndByteIndex + getContentRealByteLength(line.getContent(), encoding));
                            debug("【4】" + line.getContent() + ",start=" + line.getFirstIndex() + ",end=" + line.getEndIndex() +
                                    ",linType=" + line.getLineType());
                            setXYS(line, widths, lastLineEndCharIndex, false, BLANK_WIDTH, indentLength);
                            lastLineEndByteIndex = line.getEndIndex() + 1;
                            lastLineEndCharIndex += line.getContent().length();
                        }
                        continue;
                    }

                    if ((i + 1) == content[j].length()) {
                        String endSrcContent = content[j].substring(lastLineEndCharIndex, i + 1);
                        if (endSrcContent == null || isBlank(endSrcContent)) {
                            continue;
                        }
                        line = new BookContentLineInfo();
                        list.add(line);
                        debug("5.indentLength=" + indentLength);
                        line.setContent(INDENT_STRING.substring(0, indentLength) + endSrcContent);
                        line.setFirstIndex(lastLineEndByteIndex);
                        line.setEndIndex(lastLineEndByteIndex + getContentRealByteLength(endSrcContent, encoding));
                        debug("【5】" + line.getContent() + ",start=" +
                                line.getFirstIndex() + ",end=" + line.getEndIndex() + ",linType=" + line.getLineType());
                        setXYS(line, widths, lastLineEndCharIndex, false, BLANK_WIDTH, indentLength);
                        if (!isLastContent) {
                            lastLineEndByteIndex = line.getEndIndex() + 1;
                        } else {
                            lastLineEndByteIndex = line.getEndIndex();
                        }
                        lastLineEndCharIndex += endSrcContent.length();
                        indentLength = 0;
                        curLineWidth = 0;
                        continue;
                    }
                }
            }
        }
        if (list != null && list.size() > 0 && PARAGRAPH_SPACE.equals(list.get(list.size() - 1).getContent())) {
            list.remove(list.get(list.size() - 1));
        }
        return list;
    }

    /**
     * 绘制区域宽度
     *
     * @return int
     */
    private int viewWidth() {
        int w = mBookContentRenderSetting.getScreenWidth() - mBookContentRenderSetting.getMarginLeft() - mBookContentRenderSetting
                .getMarginRight() - mBookContentRenderSetting.getSideWidth();
//        debug("绘制区域宽度: " + w + ",屏幕宽度：" + mBookContentRenderSetting.getScreenWidth());
//        debug("【viewWidth】" + w + ",screenW=" + mBookContentRenderSetting.getScreenWidth() + ",left=" +
//                mBookContentRenderSetting.getMarginLeft() + ",right=" + mBookContentRenderSetting.getMarginRight() + ",side=" +
//                mBookContentRenderSetting.getSideWidth());
        return w;
    }

    /**
     * 绘制区域高度
     *
     * @return
     */
    private int viewHeight() {
        int h = mBookContentRenderSetting.getScreenHeight() - mBookContentRenderSetting.getMarginTop() - mBookContentRenderSetting
                .getMarginBottom() - 2 * mBookContentRenderSetting.getTopToTitle();
        return h;
    }

    /**
     * 设置每行字的x坐标
     *
     * @param info
     * @param widths
     * @param start
     * @param isFillWidth
     * @param indentLength
     */
    private void setXYS(BookContentLineInfo info, float[] widths, int start, boolean isFillWidth, int blankWidth, int indentLength) {

        final String content = info.getContent();
        if (content == null) {
            info.setLineXys(new float[0]);
            return;
        }
        final int contentLength = info.getContent().length();

        float[] xys = new float[contentLength * 2];
        info.setLineXys(xys);

        float space = 0;
        if (isFillWidth && contentLength > 0) {
            float contentWidth = blankWidth * indentLength;
            for (int j = 0; j < (contentLength - indentLength); j++) {
                contentWidth += (Character.isWhitespace(content.charAt(j + indentLength)) ? blankWidth : widths[j + start]);
            }
            space = (mBookContentRenderSetting.getScreenWidth() - contentWidth - mBookContentRenderSetting.getMarginLeft() -
                    mBookContentRenderSetting.getMarginRight() - mBookContentRenderSetting.getSideWidth()) / (contentLength - 1);

        }

        float x_offset = mBookContentRenderSetting.getMarginLeft();
        for (int j = 0; j < contentLength; j++) {
            if (j == 0) {
                info.getLineXys()[0] = mBookContentRenderSetting.getMarginLeft();
            } else {
                info.getLineXys()[j * 2] = x_offset;
            }
            x_offset += (Character.isWhitespace(content.charAt(j)) ? blankWidth : getTextWidth(widths, j, start, indentLength,
                    blankWidth) + space);
        }
    }

    /**
     * 获取字符串宽度
     */
    public int getStringWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }

    private float getTextWidth(float[] widths, int j, int start, int indentLength, int blankWidth) {

        if (j < indentLength) {
            return blankWidth;
        }
        return widths[Math.min(j + start - indentLength, widths.length - 1)];
    }

    /**
     * 获取当前行首空格个数
     */
    private int getBlankCount(String strTag, String str) {
        int k = 1;
        for (; k < str.length() - 1; k++) {
            if (!strTag.equals(str.substring(k, k + 1))) {
                // Log4an.d(TAG, "【原始getBlankCount】k=" + k + ",str=" + str.substring(k, k + 1));
                if (" ".equals(str.substring(k, k + 1))) {
                    k++;
                }
                // Log4an.d(TAG, "【最终getBlankCount】k=" + k + ",str=" + str.substring(k, k + 1));
                break;
            }
        }
        return k;
    }

    private int getContentRealByteLength(String content, String encoding) {
        try {
            if (encoding.equalsIgnoreCase("Unicode")) {
                return content.getBytes(encoding).length - 2; // 减去开头的BOM信息
            } else {
                return content.getBytes(encoding).length;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return content.length();
    }

    /**
     * 初始化首行不能显示的标点集合
     */
    private void initErrorChar() {
        errorChar.add(',');
        errorChar.add('.');
        errorChar.add('!');
        errorChar.add('?');
        errorChar.add(':');
        errorChar.add(')');
        errorChar.add('"');
        errorChar.add('，');
        errorChar.add('。');
        errorChar.add('、');
        errorChar.add('！');
        errorChar.add('？');
        errorChar.add('：');
        errorChar.add('》');
        errorChar.add('”');
        errorChar.add('~');
        errorChar.add('>');
    }

    /**
     * 是否包含空格
     */
    private boolean isBlank(CharSequence str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((!isWhitespace(str.charAt(i)))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否包含空白
     */
    private boolean isWhitespace(char ch) {
        if (ch == '　') {
            return true;
        }
        if (Character.isWhitespace(ch)) {
            return true;
        }
        return false;
    }

    /**
     * 设置每一行字的y坐标
     */
    public void setAllLineY(float[] xys, float y) {
        if (xys != null && xys.length > 0) {
            for (int i = 0; i < xys.length; i++) {
                if (i % 2 == 1) {
                    xys[i] = y;
                }
            }
        }
    }

    /**
     * 控制日志输出.
     *
     * @param msg
     */
    private void debug(String msg) {
//        System.err.println(msg);
    }


}
