package com.spriteapp.booklibrary.model;

import java.util.List;

/**
 * Created by Administrator on 2017/12/20.
 */

public class CateBean {
    private Top_menu top_menu;
    private String hello_messages;

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
