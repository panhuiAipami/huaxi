package com.spriteapp.booklibrary.widget.readview.util;

import java.util.List;

/**
 * Created by Administrator on 2016/8/11 0011.
 */
public class TRPage {
    private int begin = 0;
    private int end = 0;
    private List<ShowLine> lines;

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public List<ShowLine> getLines() {
        return lines;
    }

    public String getLineToString(){
        String text ="";
        if (lines != null){
            for (ShowLine  s: lines){
                text += s.getLineData();
            }
        }
        return text;
    }


    public void setLines(List<ShowLine> lines) {
        this.lines = lines;
    }
}
