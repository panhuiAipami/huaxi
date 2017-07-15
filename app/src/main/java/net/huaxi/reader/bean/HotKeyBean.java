package net.huaxi.reader.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Saud on 16/4/28.
 */
public class HotKeyBean implements Serializable {
    private static final long serialVersionUID = 3803858414916700868L;

    /**
     * errorid : 0
     * errordesc :
     * vdata : {"list":[{"title":"装傻王妃：窦芽菜"},{"title":"龙王令：妃临天下"},{"title":"特种兵在都市特打下"},{"title":"特种兵在都市特打下"},{"title":"特种兵在都市"},{"title":"安知晓"},{"title":"独步天下:至尊大小姐"},{"title":"化仙"},{"title":"国民男神"},{"title":"夜十三"},{"title":"魔女恩恩"},{"title":"纨绔凰妃冠宠天下"},{"title":"重生"},{"title":"帝少的假面柔妻"},{"title":"傲妃，风华无双"},{"title":"神图腾：兽妃天下"},{"title":"志弟"},{"title":"把数据替换成这个"},{"title":"特种兵在都市特打下"}]}
     * version : 1.0
     */

    private int errorid;
    private String errordesc;
    private VdataBean vdata;
    private String version;

    public int getErrorid() {
        return errorid;
    }

    public void setErrorid(int errorid) {
        this.errorid = errorid;
    }

    public String getErrordesc() {
        return errordesc;
    }

    public void setErrordesc(String errordesc) {
        this.errordesc = errordesc;
    }

    public VdataBean getVdata() {
        return vdata;
    }

    public void setVdata(VdataBean vdata) {
        this.vdata = vdata;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public static class VdataBean implements Serializable {
        private static final long serialVersionUID = 106298533780539731L;
        /**
         * title : 装傻王妃：窦芽菜
         */

        private List<ListBean> list;

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean  implements Serializable{
            private static final long serialVersionUID = 1142862729720177783L;
            private String title;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }
    }

}
