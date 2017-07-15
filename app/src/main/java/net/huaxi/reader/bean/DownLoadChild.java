package net.huaxi.reader.bean;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

public class DownLoadChild extends Observable implements Observer, Serializable {


    private static final long serialVersionUID = -5698548494332053039L;

    private String name;


    private int isSubscribe;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    private String bookId;

    private String chapterId;

    private boolean isChecked;

    private int price;

    public int getIsSubscribe() {
        return isSubscribe;
    }

    public void setIsSubscribe(int isSubscribe) {
        this.isSubscribe = isSubscribe;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public void changeChecked() {
        isChecked = !isChecked;
        setChanged();
        notifyObservers();
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof Boolean) {
            this.isChecked = (Boolean) data;
        }
    }


    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

}
