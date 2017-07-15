package net.huaxi.reader.bean;

/**
 * Created by ZMW on 2016/5/12.
 */
public class ConsumeRecordCustom extends ConsumeRecord {
    public static final int VIEW_TYPE_NOMAL=1;
    public static final int VIEW_TYPE_MONTH=2;
    private static final long serialVersionUID = 3861837608032716034L;;

    private int type=VIEW_TYPE_NOMAL;
    private String monthString;

    public String getMonthString() {
        return monthString;
    }

    public void setMonthString(String monthString) {
        this.monthString = monthString;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
