package com.lucio.milive.util;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class DownloadManager {

    private static final int DEFAULT_TIMEOUT = 15;
    /**
     * 下载文件
     * @param url 下载地址
     * @param destFileDir 保存路径
     * @param destFileName 文件名
     * @param observer observer
     */
    public static void download(String url, final String destFileDir,
                                final String destFileName, final DownloadObserver observer) {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient)
                .baseUrl("https://github.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        retrofit.create(DownloadService.class)
                .download(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(responseBody -> observer.saveFile(responseBody, destFileDir, destFileName))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

}