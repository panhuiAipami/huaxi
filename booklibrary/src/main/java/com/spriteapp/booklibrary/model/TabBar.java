package com.spriteapp.booklibrary.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/12/27.
 */

public class TabBar {
    /**
     * color : #7A7E83
     * color_on : #3cc51f
     * border_style : #000000
     * background_color : #ffffff
     * lists : [{"page":"https://s.hxdrive.net/book_weekly?format=html","text":"精选","default":1,"icon":{"2x":"https://img.hxdrive.net/icon/tabbar/chosen_2x_on.png","3x":"https://img.hxdrive.net/icon/tabbar/chosen_3x.png"},"icon_on":{"2x":"https://img.hxdrive.net/icon/tabbar/chosen_2x_on.png","3x":"https://img.hxdrive.net/icon/tabbar/chosen_3x_on.png"}},{"page":"https://s.hxdrive.net/book_store?format=html","text":"书城","default":0,"icon":{"2x":"https://img.hxdrive.net/icon/tabbar/store_2x.png","3x":"https://img.hxdrive.net/icon/tabbar/store_3x.png"},"icon_on":{"2x":"https://img.hxdrive.net/icon/tabbar/store_2x_on.png","3x":"https://img.hxdrive.net/icon/tabbar/store_3x_on.png"}},{"page":"https://s.hxdrive.net/book_weekly?format=html","text":"书架","default":0,"icon":{"2x":"https://img.hxdrive.net/icon/tabbar/shelf_2x.png","3x":"https://img.hxdrive.net/icon/tabbar/shelf_3x.png"},"icon_on":{"2x":"https://img.hxdrive.net/icon/tabbar/shelf_2x_on.png","3x":"https://img.hxdrive.net/icon/tabbar/shelf_3x_on.png"}},{"page":"https://s.hxdrive.net/user_info?format=html","text":"我的","default":0,"icon":{"2x":"https://img.hxdrive.net/icon/tabbar/users_2x.png","3x":"https://img.hxdrive.net/icon/tabbar/users_3x.png"},"icon_on":{"2x":"https://img.hxdrive.net/icon/tabbar/users_2x_on.png","3x":"https://img.hxdrive.net/icon/tabbar/users_3x_on.png"}}]
     */

    private String color;
    private String color_on;
    private String border_style;
    private String background_color;
    private List<ListsBean> lists;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor_on() {
        return color_on;
    }

    public void setColor_on(String color_on) {
        this.color_on = color_on;
    }

    public String getBorder_style() {
        return border_style;
    }

    public void setBorder_style(String border_style) {
        this.border_style = border_style;
    }

    public String getBackground_color() {
        return background_color;
    }

    public void setBackground_color(String background_color) {
        this.background_color = background_color;
    }

    public List<ListsBean> getLists() {
        return lists;
    }

    public void setLists(List<ListsBean> lists) {
        this.lists = lists;
    }

    public static class ListsBean {
        /**
         * page : https://s.hxdrive.net/book_weekly?format=html
         * text : 精选
         * default : 1
         * icon : {"2x":"https://img.hxdrive.net/icon/tabbar/chosen_2x_on.png","3x":"https://img.hxdrive.net/icon/tabbar/chosen_3x.png"}
         * icon_on : {"2x":"https://img.hxdrive.net/icon/tabbar/chosen_2x_on.png","3x":"https://img.hxdrive.net/icon/tabbar/chosen_3x_on.png"}
         */

        private String page;
        private String text;
        @SerializedName("default")
        private int defaultX;
        private IconBean icon;
        private IconOnBean icon_on;

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getDefaultX() {
            return defaultX;
        }

        public void setDefaultX(int defaultX) {
            this.defaultX = defaultX;
        }

        public IconBean getIcon() {
            return icon;
        }

        public void setIcon(IconBean icon) {
            this.icon = icon;
        }

        public IconOnBean getIcon_on() {
            return icon_on;
        }

        public void setIcon_on(IconOnBean icon_on) {
            this.icon_on = icon_on;
        }

        public static class IconBean {
            /**
             * 2x : https://img.hxdrive.net/icon/tabbar/chosen_2x_on.png
             * 3x : https://img.hxdrive.net/icon/tabbar/chosen_3x.png
             */

            @SerializedName("2x")
            private String _$2x;
            @SerializedName("3x")
            private String _$3x;

            public String get_$2x() {
                return _$2x;
            }

            public void set_$2x(String _$2x) {
                this._$2x = _$2x;
            }

            public String get_$3x() {
                return _$3x;
            }

            public void set_$3x(String _$3x) {
                this._$3x = _$3x;
            }
        }

        public static class IconOnBean {
            /**
             * 2x : https://img.hxdrive.net/icon/tabbar/chosen_2x_on.png
             * 3x : https://img.hxdrive.net/icon/tabbar/chosen_3x_on.png
             */

            @SerializedName("2x")
            private String _$2x;
            @SerializedName("3x")
            private String _$3x;

            public String get_$2x() {
                return _$2x;
            }

            public void set_$2x(String _$2x) {
                this._$2x = _$2x;
            }

            public String get_$3x() {
                return _$3x;
            }

            public void set_$3x(String _$3x) {
                this._$3x = _$3x;
            }
        }
    }
}
