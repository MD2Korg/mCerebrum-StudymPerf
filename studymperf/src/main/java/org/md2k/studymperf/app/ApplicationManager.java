package org.md2k.studymperf.app;
/*
 * Copyright (c) 2016, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
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

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.md2k.md2k.system.Info;
import org.md2k.md2k.system.app.AppBasicInfo;
import org.md2k.md2k.system.app.AppInfo;
import org.md2k.md2k.system.app.InstallInfo;
import org.md2k.md2k.system.study.StudyInfo;
import org.md2k.md2k.system.user.UserInfo;
import org.md2k.studymperf.MyApplication;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class ApplicationManager {
    private AppInfo[] appInfos;
    private AppInfoController[] appInfoControllers;

    //    private AppInfoE[] appInfos;
//    private AppMC[] appMCs;
    public void set(AppInfo[] appInfos) {
        this.appInfos=appInfos;
        appInfoControllers = new AppInfoController[appInfos.length];
        for (int i = 0; i < appInfos.length; i++) {
            appInfoControllers[i] = new AppInfoController(appInfos[i]);
            Log.d("abc","i="+i+" "+appInfos[i].getAppBasicInfo().getPackageName());
        }
    }

    public void start() {
        for (AppInfoController appInfoController : appInfoControllers) {
            if(appInfoController.getAppBasicInfoController().isType(AppInfo.TYPE_STUDY)) continue;
            if(appInfoController.getAppBasicInfoController().isType(AppInfo.TYPE_DATAKIT)) continue;
            if(appInfoController.getAppBasicInfoController().isType(AppInfo.TYPE_MCEREBRUM)) continue;
            appInfoController.getmCerebrumController().startService();
        }
    }

    public void stop() {
        if(appInfoControllers==null || appInfoControllers.length==0) return;
        for (AppInfoController appInfoController : appInfoControllers) {
            try {
                if(appInfoController.getmCerebrumController().isRunInBackground())
                appInfoController.getmCerebrumController().stopBackground(null);
                appInfoController.getmCerebrumController().stopService(null);
            }catch (Exception ignored){

            }
        }
    }

    public ArrayList<AppInfoController> getAppByType(String type) {
        ArrayList<AppInfoController> acs = new ArrayList<>();
        for (AppInfoController appInfoController : appInfoControllers) {
            if (appInfoController.getAppBasicInfoController().isType(type))
                acs.add(appInfoController);
        }
        return acs;
    }

    public void startStudy(StudyInfo studyInfo, UserInfo userInfo) {
        if (!isCoreInstalled()) {
            Toasty.error(MyApplication.getContext(), "Study/DataKit not installed", Toast.LENGTH_SHORT).show();
            Toasty.error(MyApplication.getContext(), "DataKit not installed", Toast.LENGTH_SHORT).show();
            return;
        }
        AppInfoController a = getAppByType(AppInfo.TYPE_STUDY).get(0);
        Bundle bundle = new Bundle();
        Info info = new Info(userInfo, studyInfo, appInfos);
        bundle.putParcelable("info", info);
//        Intent intent = MyApplication.getContext().getPackageManager().getLaunchIntentForPackage(a.getAppBasicInfoController().getPackageName());
//        intent.putExtras(bundle);

//        MyApplication.getContext().startActivity(intent);

        a.getmCerebrumController().launch(bundle);
    }

    public AppInfoController[] getAppInfoControllers() {
        return appInfoControllers;
    }

    public void setmCerebrumInfo() {
        for (AppInfoController appInfoController : appInfoControllers) {
            if (!appInfoController.getmCerebrumController().isStarted())
                appInfoController.getmCerebrumController().startService();
            else appInfoController.getmCerebrumController().setStatus();
        }
    }

    public boolean isCoreInstalled() {
        return getAppByType(AppInfo.TYPE_STUDY).size() != 0 && getAppByType(AppInfo.TYPE_DATAKIT).size() != 0;
    }

    public boolean isRequiredAppInstalled() {
        for (AppInfoController appInfoController : appInfoControllers)
            if (appInfoController.getAppBasicInfoController().isUseAs(AppInfo.USE_AS_REQUIRED)) {
                if (!appInfoController.getInstallInfoController().isInstalled())
                    return false;
            }
        return true;
    }

    public ArrayList<AppInfoController> getRequiredAppNotInstalled() {
        ArrayList<AppInfoController> appInfos = new ArrayList<>();
        if (this.appInfos == null) return appInfos;
        for (AppInfoController appInfo : this.appInfoControllers) {
            if (appInfo.getAppBasicInfoController().isUseAs(AppInfo.USE_AS_REQUIRED) && !appInfo.getInstallInfoController().isInstalled()) {
                appInfos.add(appInfo);
            }
        }
        return appInfos;
    }

    public ArrayList<AppInfoController> getRequiredAppNotConfigured() {
        ArrayList<AppInfoController> appInfos = new ArrayList<>();
        if (this.appInfos == null) return appInfos;
        for (AppInfoController appInfo : this.appInfoControllers) {
            if (!appInfo.getAppBasicInfoController().isUseAs(AppInfo.USE_AS_REQUIRED)
                    || !appInfo.getInstallInfoController().isInstalled())
                continue;
            if (appInfo.getmCerebrumController().ismCerebrumSupported()
                    && appInfo.getmCerebrumController().isStarted()
                    && appInfo.getmCerebrumController().isConfigurable()
                    && !appInfo.getmCerebrumController().isEqualDefault()) {
                appInfos.add(appInfo);
            }
        }
        return appInfos;
    }

    public ArrayList<AppInfoController> getRequiredAppConfigured() {
        ArrayList<AppInfoController> appInfos = new ArrayList<>();
        if (this.appInfos == null) return appInfos;
        for (AppInfoController appInfo : this.appInfoControllers) {
            if (!appInfo.getAppBasicInfoController().isUseAs(AppInfo.USE_AS_REQUIRED)
                    || !appInfo.getInstallInfoController().isInstalled())
                continue;
            if (appInfo.getmCerebrumController().ismCerebrumSupported()
                    && appInfo.getmCerebrumController().isStarted()
                    && appInfo.getmCerebrumController().isConfigurable()
                    && appInfo.getmCerebrumController().isEqualDefault()) {
                appInfos.add(appInfo);
            }
        }
        return appInfos;
    }

    public int[] getInstallStatus() {
        int result[] = new int[3];
        result[0] = 0;
        result[1] = 0;
        result[2] = 0;
        if (appInfos == null) return result;
        for (int i = 0; i < appInfoControllers.length; i++) {
            if (!appInfoControllers[i].getInstallInfoController().isInstalled())
                result[2]++;
//            else if (appInfoControllers[i].getInstallInfoController().hasUpdate())
//                result[1]++;
            else result[0]++;
        }
        return result;
    }

    public void startBackground() {
        for (AppInfoController appInfoController : appInfoControllers) {
            if(appInfoController.getAppBasicInfoController().isType(AppInfo.TYPE_STUDY)) continue;
            if(appInfoController.getAppBasicInfoController().isType(AppInfo.TYPE_DATAKIT)) continue;
            if(appInfoController.getAppBasicInfoController().isType(AppInfo.TYPE_MCEREBRUM)) continue;
            MCerebrumController mCerebrumController = appInfoController.getmCerebrumController();
            if (!mCerebrumController.ismCerebrumSupported()) {
                Log.d("abc","------------->"+appInfoController.getAppBasicInfoController().getPackageName()+"--------->not supported");
                continue;
            }
/*
            if(!mCerebrumController.isConfigured()) {
                Log.d("abc","------------->"+appInfoController.getAppBasicInfoController().getPackageName()+"--------->not configured");
                continue;
            }
*/
            if (!mCerebrumController.isStarted()) {
                Log.d("abc","------------->"+appInfoController.getAppBasicInfoController().getPackageName()+"--------->not started");
                mCerebrumController.startService();
                continue;
            }
            mCerebrumController.setStatus();
            if (!mCerebrumController.isRunInBackground()) {
                Log.d("abc","------------->"+appInfoController.getAppBasicInfoController().getPackageName()+"--------->not possible to run in background");
                continue;
            }

            if (mCerebrumController.isRunningInBackground()) {
                Log.d("abc","------------->"+appInfoController.getAppBasicInfoController().getPackageName()+"--------->running="+mCerebrumController.getRunningTime()/(1000*60)+" Minutes");
                continue;
            }
            mCerebrumController.startBackground(null);
            Log.d("abc","------------->"+appInfoController.getAppBasicInfoController().getPackageName()+"--------->starting");
        }
    }
    public void stopBackground() {
        for (AppInfoController appInfoController : appInfoControllers) {
            MCerebrumController mCerebrumController = appInfoController.getmCerebrumController();
            if (!mCerebrumController.ismCerebrumSupported()) continue;
            if (!mCerebrumController.isStarted()) continue;
            if (!mCerebrumController.isRunInBackground()) continue;
            if (!mCerebrumController.isRunningInBackground()) continue;
            mCerebrumController.stopBackground(null);
        }
    }

}
