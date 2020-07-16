package com.lucio.milive.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.lucio.milive.MainActivity;
import com.lucio.milive.R;
import com.lucio.milive.util.StatusBarUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        StatusBarUtil.setStatusColor(this,false,true,R.color.white);
        playDelayed(1800);
    }

    //延时处理
    public void playDelayed(long milliseconds){
        Observable.timer(milliseconds, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        mDisposable = disposable;
                    }
                    @Override
                    public void onNext(@NonNull Long number) {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }
                    @Override
                    public void onError(@NonNull Throwable e) {cancel();}
                    @Override
                    public void onComplete() {cancel();}
                });
    }
    private Disposable mDisposable;
    public void cancel(){
        if(mDisposable!=null&&!mDisposable.isDisposed()){
            mDisposable.dispose();
        }
    }
}