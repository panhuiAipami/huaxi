package net.huaxi.reader.appinterface;

/**
 * @Description: [ 子分类页面的业务接口 ]
 * @Author: [ Saud ]
 * @CreateDate: [ 16/7/14 15:01 ]
 * @UpDate: [ 16/7/14 15:01 ]
 * @Version: [ v1.0 ]
 */
public interface ClassifyPresenter {


    void loadingData(int limit, int offset, String vip, String orderby, String classityID);
}
