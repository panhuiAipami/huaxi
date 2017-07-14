package net.huaxi.reader.util;

/**
 * Created by ZMW on 2016/5/16.
 */
public class EventBusUtil {
    //想要发布的事件
    public static final byte EVENTTYPE_UPDATE_SHELF = 1;
    public static final byte EVENTTYPE_VERSIONSERVICE_DOWNLOAD_COMPLETE=2;
    public static final byte EVENTTYPE_VERSIONSERVICE_DOWNLOAD_FILE=3;
    public static final byte EVENTTYPE_VERSIONSERVICE_DOWNLOAD_PROGRESS=4;
    public static final byte EVENTTYPE_MIGRATE_SHOW_ONE=5;
    public static final byte EVENTTYPE_GIVE_COINS=6;
    public static final byte EVENTTYPE_LOGOUT=7;
    //发布事件的模块
    public static final byte EVENTMODEL_BOOKSHELF = 1;
    public static final byte EVENTMODEL_APPVERSIONSERVICE=2;
    public static final byte EVENTMODEL_MIGRATE=3;
    public static final byte EVENTMODEL_GIVE_COINS=4;
    public static final byte EVENTMODEL_LOGOUT=5;
    public static class EventBean {
        private byte modle;
        private byte type;
        private String desc;

        public EventBean(byte modle, byte type) {
            this.modle = modle;
            this.type = type;
            this.desc="";
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public byte getModle() {
            return modle;
        }

        public void setModle(byte modle) {
            this.modle = modle;
        }

        public byte getType() {
            return type;
        }

        public void setType(byte type) {
            this.type = type;
        }
    }
}
