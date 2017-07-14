package net.huaxi.reader.presenter;

import android.app.Activity;

import net.huaxi.reader.appinterface.SearchInteractor;
import net.huaxi.reader.appinterface.SearchPresenter;

import java.util.List;

import net.huaxi.reader.appinterface.SearchViewListener;
import net.huaxi.reader.bean.SearchBean;
import net.huaxi.reader.bean.SearchKeyBean;

/**
 * Created by Saud on 16/5/1.
 */
public class SearchPresenterImpl implements SearchPresenter, SearchInteractor.OnSearchListener {

    private SearchViewListener searchViewInterface;
    private SearchInteractor interactor;

    public SearchPresenterImpl(SearchViewListener searchViewInterface) {
        this.searchViewInterface = searchViewInterface;
        interactor = new SearchInteractorImpl((Activity) searchViewInterface, this);
    }


    @Override
    public void getHistoryList(String key) {

        if (interactor != null) {
            interactor.getHistoryList(key);
        }
    }

    @Override
    public void saveHistoryList(List<String> list) {
        if (interactor != null) {
            interactor.savaHistoryList(list);
        }

    }

    @Override
    public void searchSimpleKey(String key) {

        if (interactor != null) {
            interactor.getSearchSimpleKey(key);
        }

    }

    @Override
    public void searchFromNet(String key) {
        if (interactor!=null){
            interactor.getSearchData(key);
        }
    }

    @Override
    public void searchHotKey() {
        if (interactor != null) {
            interactor.getHotKey();
        }
    }

    @Override
    public void destroy() {
        if (interactor != null) {
            interactor.destroy();
            searchViewInterface = null;
        }
    }

    //////////////////////
    @Override
    public void onHistory(List<String> historys) {
        if (searchViewInterface != null) {
            searchViewInterface.onHistory(historys);
        }
    }

    @Override
    public void onSimpleKey(List<SearchKeyBean> keys) {
        if (searchViewInterface != null) {
            searchViewInterface.onSearchDB(keys);
        }
    }

    @Override
    public void onSearch(List<SearchBean> datas) {
        if (searchViewInterface != null) {
            searchViewInterface.onSearchData(datas);
        }
    }

    @Override
    public void onHotKey(List<String> hotkeys) {
        if (hotkeys != null && hotkeys.size() > 0) {
            if (searchViewInterface != null) {
                searchViewInterface.onHotKey(hotkeys);
            }
        }

    }

    @Override
    public void onAboutBook(List<SearchBean> sList) {
        if (searchViewInterface != null) {
            searchViewInterface.onAboutBook(sList);
        }
    }
}
