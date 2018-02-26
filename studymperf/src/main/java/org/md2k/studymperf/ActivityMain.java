package org.md2k.studymperf;

import android.os.Bundle;
import android.util.Log;

import org.md2k.datakitapi.time.DateTime;
import org.md2k.mcerebrum.system.update.Update;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class ActivityMain extends AbstractActivityMenu {
    Subscription subscriptionCheckUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscriptionCheckUpdate = Observable.just(true).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<Boolean, Observable<Boolean>>() {
                    @Override
                    public Observable<Boolean> call(Boolean aBoolean) {
                        return Update.checkUpdate(ActivityMain.this);
                    }
                }).subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        Log.d("abc","checking update - completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("abc","checking update - onerror");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        Log.d("abc","checking update onNext="+aBoolean);
                    }
                });
        if (getIntent().getBooleanExtra("background", false))
            finish();
        Log.d("abc","ame_count=:"+(++cc)+" "+ (startT - DateTime.getDateTime()));startT=DateTime.getDateTime();
    }

    public void onDestroy() {
        if (subscriptionCheckUpdate != null && !subscriptionCheckUpdate.isUnsubscribed())
            subscriptionCheckUpdate.unsubscribe();
        super.onDestroy();
    }

}
