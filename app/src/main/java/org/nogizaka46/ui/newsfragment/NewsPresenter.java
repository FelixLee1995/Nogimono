package org.nogizaka46.ui.newsfragment;

import org.nogizaka46.bean.NewsBean;
import org.nogizaka46.contract.Contract;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by acer on 2017/4/5.
 */

public class NewsPresenter {

     private Contract.INewsModel model ;
    private Contract.INewsView view ;

    public NewsPresenter(Contract.INewsView view) {
        this.view =view ;
        model  = new NewsModelImpl();
    }

    void getData(String string){

        view.onLoading();
        Observable<NewsBean> observable = model.getData(string);
        observable.subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(new Subscriber<NewsBean>() {
                       @Override
                       public void onCompleted() {
                          view.onLoaded();
                       }

                       @Override
                       public void onError(Throwable e) {
                           if (e instanceof HttpException) {
                               HttpException httpException = (HttpException) e;
                               //httpException.response().errorBody().string()
                               int code = httpException.code();
                               if (code == 500 || code == 404) {
                                   view.onLoadFailed("服务器出错");

                               }
                           } else if (e instanceof ConnectException) {
                               view.onLoadFailed("网络断开,请打开网络!");
                           } else if (e instanceof SocketTimeoutException) {
                               view.onLoadFailed("网络连接超时!!");
                           } else {
                               view.onLoadFailed("发生未知错误" + e.getMessage());
                           }
                       }


                       @Override
                       public void onNext(NewsBean newsBean) {
                            view.getData(newsBean);
                       }
                   });

    }
}
