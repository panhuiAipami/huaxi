package net.huaxi.reader.bean;

import java.io.Serializable;

/**
 * @Description: [ 一句话描述该类的功能 ]
 * @Author: [ Saud ]
 * @CreateDate: [ 16/6/17 16:27 ]
 * @UpDate: [ 16/6/17 16:27 ]
 * @Version: [ v1.0 ]
 */
public class MigrateUserBean implements Serializable {
    private static final long serialVersionUID = 1428821641465688759L;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    private String userName;
    private boolean isSuccess;

    @Override
    public String toString() {
        return "MigrateUserBean{" +
                "userName='" + userName + '\'' +
                ", isSuccess=" + isSuccess +
                '}';
    }
}
