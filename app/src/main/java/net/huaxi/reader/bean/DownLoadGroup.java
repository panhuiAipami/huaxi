package net.huaxi.reader.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class DownLoadGroup extends Observable implements Observer,Serializable {


    private static final long serialVersionUID = 3109654890221714397L;

    private String name;
    private boolean isChecked;
    private List<DownLoadChild> childList = new ArrayList<DownLoadChild>();


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

    public List<DownLoadChild> getChildList() {
        return childList;
    }

    public void setChildList(List<DownLoadChild> cityList) {
        this.childList = cityList;
    }

    public void changeChecked() {
        isChecked = !isChecked;
        setChanged();
        notifyObservers(isChecked);
    }

    @Override
    public void update(Observable observable, Object data) {
        boolean flag = true;
        for (DownLoadChild child : childList) {
            if (child.isChecked() == false) {
                flag = false;
            }
        }
        this.isChecked = flag;
    }


}
