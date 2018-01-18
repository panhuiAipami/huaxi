package com.spriteapp.booklibrary.model;

import java.util.List;

/**
 * Created by Administrator on 2017/12/20.
 */

public class CateBean {
    private Top_menu top_menu;
    private String hello_messages;
    private TabBar tabbar;
    private String splashscreen;
    private String splashscreen_url;

    public TabBar getTabbar() {
        return tabbar;
    }

    public void setTabbar(TabBar tabbar) {
        this.tabbar = tabbar;
    }

    public String getSplashscreen() {
        return splashscreen;
    }

    public void setSplashscreen(String splashscreen) {
        this.splashscreen = splashscreen;
    }

    public String getHello_messages() {
        return hello_messages;
    }

    public void setHello_messages(String hello_messages) {
        this.hello_messages = hello_messages;
    }

    public Top_menu getTop_menu() {
        return top_menu;
    }

    public void setTop_menu(Top_menu top_menu) {
        this.top_menu = top_menu;
    }

    public String getSplashscreen_url() {
        return splashscreen_url;
    }

    public void setSplashscreen_url(String splashscreen_url) {
        this.splashscreen_url = splashscreen_url;
    }

    public class Top_menu {
        private List<StoreBean> store;
        private List<StoreBean> chosen;

        public List<StoreBean> getStore() {
            return store;
        }

        public void setStore(List<StoreBean> store) {
            this.store = store;
        }

        public List<StoreBean> getChosen() {
            return chosen;
        }

        public void setChosen(List<StoreBean> chosen) {
            this.chosen = chosen;
        }

        @Override
        public String toString() {
            return "Top_menu{" +
                    "store=" + store +
                    ", chosen=" + chosen +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "CateBean{" +
                "top_menu=" + top_menu +
                '}';
    }
}
