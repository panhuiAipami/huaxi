package net.huaxi.reader.book;

/**
 * 页面排版行内容
 * Created by taoyingfeng on 2015/12/3.
 */
public class BookContentLineInfo {

    public int firstIndex;
    public int endIndex;
    public String content;

    /**
     * 本行的内容类型：0正文，1章节名 ，2段落开始标记，3段落结束标记
     */
    private int lineType = LINETYPE.CONTENT.getType();
    /**
     * 每一行里的坐标集合 x1,y1; x2,y2...
     */
    private float[] lineXys;
    /* 是否回车 */
    private boolean isEnter = false;

    public int getFirstIndex() {
        return firstIndex;
    }

    public void setFirstIndex(int firstIndex) {
        this.firstIndex = firstIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLineType() {
        return lineType;
    }

    public void setLineType(int lineType) {
        this.lineType = lineType;
    }

    public float[] getLineXys() {
        if (lineXys == null) {
            if (content == null) {
                lineXys = new float[0];
            } else {
                lineXys = new float[content.length() * 2];
            }
        }
        return lineXys;
    }

    public void setLineXys(float[] lineXys) {
        this.lineXys = lineXys;
    }

    public boolean isEnter() {
        return isEnter;
    }

    public void setEnter(boolean isEnter) {
        this.isEnter = isEnter;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (lineXys != null && lineXys.length > 0) {
            int i = 0;
            if (i < lineXys.length) {
                sb.append("x = " + lineXys[0] + "y = " + lineXys[1]);
            }
        }
        String lineTypeStr = "";
        if (this.lineType == LINETYPE.CONTENT.getType()) {
            lineTypeStr = "正文";
        } else if (this.lineType == LINETYPE.CHAPTERTITLE.getType()) {
            lineTypeStr = "标题";
        } else if (this.lineType == LINETYPE.PARAGRAPH_START.getType()) {
            lineTypeStr = "段首";
        } else if (this.lineType == LINETYPE.PARAGRASH_END.getType()) {
            lineTypeStr = "断尾";
        } else if (this.lineType == LINETYPE.COMMENT_MARK.getType()) {
            lineTypeStr = "评论";
        } else {
            lineTypeStr = "正文";
        }
        return " firstIndex = " + firstIndex + ", endIndex = " + endIndex + ", x|y=" + sb.toString() + ", type=" + lineTypeStr + ", " +
                "content = " + content;
    }

    public enum LINETYPE {
        CONTENT(0),
        CHAPTERTITLE(1),
        PARAGRAPH_START(2),
        PARAGRASH_END(3),
        COMMENT_MARK(4);
        private int index;

        private LINETYPE(int index) {
            this.index = index;
        }

        public int getType() {
            return this.index;
        }

        @Override
        public String toString() {
            return "state code = " + this.index;
        }
    }
}
