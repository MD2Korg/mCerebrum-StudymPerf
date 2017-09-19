package org.md2k.studymperf;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.md2k.system.app.AppInfo;
import org.md2k.studymperf.app.ApplicationManager;

import java.util.concurrent.TimeUnit;

import br.com.goncalves.pugnotification.notification.PugNotification;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func1;

/*
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * - Nazir Saleheen <nazir.saleheen@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

public class ServiceStudy extends Service {
    ApplicationManager applicationManager;
    Subscription subscription;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("abc","---------------service onCreate()");
        applicationManager=new ApplicationManager();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("abc","---------------service onStartCommand()...start");
        startForeground(98764, getCompatNotification());
        applicationManager.stop();
        AppInfo[] appInfos=null;
        Parcelable[] p= intent.getParcelableArrayExtra("app_info");
        if(p!=null && p.length!=0) {
            appInfos = new AppInfo[p.length];
            for (int i = 0; i < p.length; i++) {
                appInfos[i] = (AppInfo) p[i];
            }
        }
        Log.d("abc","---------------service onStartCommand()...start...appInfo="+appInfos.length);
        applicationManager.set(appInfos);

        applicationManager.start();
        watchDog();
        return START_STICKY; // or whatever your flag
    }
    void watchDog(){

        subscription=Observable.interval(0,30, TimeUnit.SECONDS)
                .map(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        Log.d("abc","-------------------run-----------");
                        applicationManager.startBackground();
                        return false;
                    }
                }).subscribe(new Observer<Boolean>() {
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
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onDestroy(){
        if(subscription!=null && !subscription.isUnsubscribed())
            subscription.unsubscribe();

        applicationManager.stopBackground();
        applicationManager.stop();
        super.onDestroy();
    }
    private android.app.Notification getCompatNotification() {
        Intent myIntent = new Intent(this, ActivityMain.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(getResources().getString(R.string.app_name)).setContentText("Running...").setContentIntent(pendingIntent);
        return builder.build();
    }
}
