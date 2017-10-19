package org.md2k.studymperf;

import android.os.Bundle;

import org.md2k.mcerebrum.system.update.Update;

import rx.Observer;
import rx.Subscription;


public class ActivityMain extends AbstractActivityMenu {
    Subscription subscriptionCheckUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startDataCollection();
        subscriptionCheckUpdate= Update.checkUpdate(this)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }
                });
        if(getIntent().getBooleanExtra("background", false)==true)
            finish();
    }
    public void onDestroy(){
        if (subscriptionCheckUpdate != null && !subscriptionCheckUpdate.isUnsubscribed())
            subscriptionCheckUpdate .unsubscribe();
        super.onDestroy();
    }

}
