package org.md2k.studymperf;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.md2k.studymperf.app.ApplicationManager;
import org.md2k.system.constant.MCEREBRUM;
import org.md2k.system.provider.AppCP;
import org.md2k.system.provider.appinfo.AppInfoCursor;
import org.md2k.system.provider.appinfo.AppInfoSelection;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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
    public static final int NOTIFY_ID=98764;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("abc","---------------service onCreate()");
        ArrayList<AppCP> appInfos=readApp();
        applicationManager=new ApplicationManager(this, appInfos);
        applicationManager.startMCerebrumService();
        watchDog();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("abc","---------------service onStartCommand()...start");
        startForeground(NOTIFY_ID, getCompatNotification(this, "Data Collection - ON"));
        return START_STICKY; // or whatever your flag
    }
    void watchDog(){
        subscription=Observable.interval(2,30, TimeUnit.SECONDS)
                .map(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        start();
                        return false;
                    }
                }).subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        stop();
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
    void unsubscribe(){
        if(subscription!=null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
    }
    @Override
    public void onDestroy(){
        Log.d("abc","------------------------> onDestroy...................");
        unsubscribe();
        stop();
        applicationManager.stopMCerebrumService();
        super.onDestroy();
    }
    public static android.app.Notification getCompatNotification(Context context, String msg) {
        Intent myIntent = new Intent(context, ActivityMain.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher).setContentTitle(context.getResources().getString(R.string.app_name)).setContentText(msg).setContentIntent(pendingIntent);
        return builder.build();
    }
    ArrayList<AppCP> readApp(){
        AppInfoSelection s= new AppInfoSelection();
        AppInfoCursor c = s.query(this);
        ArrayList<AppCP> appInfos=new ArrayList<>();
        while(c.moveToNext()) {
            AppCP a = new AppCP();
            a.setFromCP(c);
            appInfos.add(a);
        }
        c.close();
        return appInfos;
    }
    void stop(){
        for(int i=0;i<applicationManager.get().size();i++){
            if(!applicationManager.get(i).getmCerebrumController().ismCerebrumSupported()) continue;
            if(applicationManager.get(i).getAppBasicInfoController().isType(MCEREBRUM.APP.TYPE_STUDY)) continue;
            if(applicationManager.get(i).getAppBasicInfoController().isType(MCEREBRUM.APP.TYPE_MCEREBRUM)) continue;
//            if(applicationManager.get(i).getAppBasicInfoController().isType(MCEREBRUM.APP.TYPE_DATAKIT)) continue;
            if(applicationManager.get(i).getmCerebrumController()==null) continue;
            if(!applicationManager.get(i).getmCerebrumController().isRunInBackground()) continue;
            if(applicationManager.get(i).getmCerebrumController().isServiceRunning())
                applicationManager.get(i).getmCerebrumController().stopBackground(null);
        }
        applicationManager.stopMCerebrumService();

    }
    void start(){
        for(int i=0;i<applicationManager.get().size();i++){
            if(!applicationManager.get(i).getmCerebrumController().ismCerebrumSupported()) continue;
            if(applicationManager.get(i).getAppBasicInfoController().isType(MCEREBRUM.APP.TYPE_STUDY)) continue;
            if(applicationManager.get(i).getAppBasicInfoController().isType(MCEREBRUM.APP.TYPE_MCEREBRUM)) continue;
 //           if(applicationManager.get(i).getAppBasicInfoController().isType(MCEREBRUM.APP.TYPE_DATAKIT)) continue;
            if(applicationManager.get(i).getmCerebrumController()==null) continue;
            if(!applicationManager.get(i).getmCerebrumController().isServiceRunning())
                applicationManager.startMCerebrumService(applicationManager.get(i));
            else {
                if (!applicationManager.get(i).getmCerebrumController().isRunInBackground())
                    continue;
                else applicationManager.get(i).getmCerebrumController().startBackground(null);
            }
        }
    }
}
