package net.huaxi.reader.appinterface;

import net.huaxi.reader.bean.ClassifyChildBean;

/**
 * @Description: [ 分类子页面的业务回调监听 ]
 * @Author: [ Saud ]
 * @CreateDate: [ 16/7/14 15:20 ]
 * @UpDate: [ 16/7/14 15:20 ]
 * @Version: [ v1.0 ]
 */
public interface ClassifyViewListener {
    void onDataList(ClassifyChildBean bookList);
}
