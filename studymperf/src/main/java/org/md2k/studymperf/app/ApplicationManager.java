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

import android.content.Context;

import org.md2k.system.app.AbstractApplicationManager;
import org.md2k.system.provider.AppCP;

import java.util.ArrayList;

public class ApplicationManager extends AbstractApplicationManager{
    public ApplicationManager(Context context, ArrayList<AppCP> appCPs) {
        super(context, appCPs);
    }
/*
    //    private ArrayList<AppCP> appCPs;
    private ArrayList<AppInfoController> appInfoControllers;

    //    private AppInfoE[] appCPs;
//    private AppMC[] appMCs;
    public ApplicationManager(ArrayList<AppCP> appCPs){
        appInfoControllers = new ArrayList<>();
        for(int i=0;i<appCPs.size();i++)
            appInfoControllers.add(new AppInfoController(appCPs.get(i)));
        start();
    }
*/
/*
    public void set(AppFile[] cApps) {
        ArrayList<AppInfoBean> as = new ArrayList<>();
        ArrayList<AppInfoController> acs = new ArrayList<>();
        for (AppFile cApp : cApps) {
            if (cApp.getUse_as().equalsIgnoreCase(MCEREBRUM.APP.USE_AS_NOT_IN_USE)) continue;
            AppInfoBean a = new AppInfoBean();
            as.add(a);
            AppInfoController ac = new AppInfoController(a, cApp);
            acs.add(ac);
        }
        appCPs = new AppInfoBean[as.size()];
        appInfoControllers = new AppInfoController[acs.size()];
        for (int i = 0; i < as.size(); i++) {
            appCPs[i] = as.get(i);
            appInfoControllers[i] = acs.get(i);
        }
        start();
    }
*//*


    public void start() {
        for (AppInfoController appInfoController : appInfoControllers) {
            appInfoController.getmCerebrumController().startService();
        }
    }

    public void stop() {
        if (appInfoControllers == null || appInfoControllers.size() == 0) return;
        for (AppInfoController appInfoController : appInfoControllers) {
            try {
                appInfoController.getmCerebrumController().stopService();
            } catch (Exception ignored) {

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

    public boolean startStudy() {
        if (!isCoreInstalled()) {
            Toasty.error(MyApplication.getContext(), "Study/DataKit not installed", Toast.LENGTH_SHORT).show();
            return false;
        }
        AppInfoController a = getAppByType(MCEREBRUM.APP.TYPE_STUDY).get(0);
*/
/*
        Bundle bundle = new Bundle();
        Info info = new Info(userInfo, studyInfo, appCPs);
        bundle.putParcelable("info", info);
*//*

//        Intent intent = MyApplication.getContext().getPackageManager().getLaunchIntentForPackage(a.getAppBasicInfoController().getPackageName());
//        intent.putExtras(bundle);

//        MyApplication.getContext().startActivity(intent);

//        a.getmCerebrumController().launch(bundle);
        a.getmCerebrumController().launch(null);
        stop();
        return true;
    }

    public ArrayList<AppInfoController> getAppInfoControllers() {
        return appInfoControllers;
    }

    public void setmCerebrumInfo() {
        for (AppInfoController appInfoController : appInfoControllers) {
            if (!appInfoController.getmCerebrumController().isServiceRunning())
                appInfoController.getmCerebrumController().startService();
            else appInfoController.getmCerebrumController().setStatus();
        }
    }

    public boolean isCoreInstalled() {
        ArrayList<AppInfoController> apps = getAppByType(MCEREBRUM.APP.TYPE_STUDY);
        if (apps.size() != 0 && !apps.get(0).getInstallInfoController().isInstalled()) return false;
        apps = getAppByType(MCEREBRUM.APP.TYPE_DATAKIT);
        if (apps.size() != 0 && !apps.get(0).getInstallInfoController().isInstalled()) return false;
        return true;
    }

    public boolean isRequiredAppInstalled() {
        return getRequiredAppNotInstalled().size() == 0;
    }

    public ArrayList<AppInfoController> getRequiredAppNotInstalled() {
        ArrayList<AppInfoController> appInfos = new ArrayList<>();
//        if (this.appCPs == null) return appInfos;
        for (AppInfoController appInfo : this.appInfoControllers) {
            if (appInfo.getAppBasicInfoController().isUseAs(MCEREBRUM.APP.USE_AS_REQUIRED) && !appInfo.getInstallInfoController().isInstalled()) {
                appInfos.add(appInfo);
            }
        }
        return appInfos;
    }

    public ArrayList<AppInfoController> getRequiredAppNotConfigured() {
        ArrayList<AppInfoController> appInfos = new ArrayList<>();
//        if (this.appCPs == null) return appInfos;
        for (AppInfoController appInfo : this.appInfoControllers) {
            if (!appInfo.getAppBasicInfoController().isUseAs(MCEREBRUM.APP.USE_AS_REQUIRED)) continue;
            if (!appInfo.getInstallInfoController().isInstalled()) appInfos.add(appInfo);

            else if (appInfo.getmCerebrumController().isServiceRunning()
                    && appInfo.getmCerebrumController().isConfigurable()
                    && !appInfo.getmCerebrumController().isEqualDefault()) {
                appInfos.add(appInfo);
            }
        }
        return appInfos;
    }

    public ArrayList<AppInfoController> getRequiredAppConfigured() {
        ArrayList<AppInfoController> appInfos = new ArrayList<>();
//        if (this.appCPs == null) return appInfos;
        for (AppInfoController appInfo : this.appInfoControllers) {
            if (!appInfo.getAppBasicInfoController().isUseAs(MCEREBRUM.APP.USE_AS_REQUIRED)
                    || !appInfo.getInstallInfoController().isInstalled())
                continue;
            if (appInfo.getmCerebrumController().isServiceRunning()
                    && appInfo.getmCerebrumController().isConfigurable()
                    && !appInfo.getmCerebrumController().isEqualDefault()) {
                continue;
            }
            appInfos.add(appInfo);

        }
        return appInfos;
    }

    public int[] getInstallStatus() {
        int result[] = new int[3];
        result[0] = 0;
        result[1] = 0;
        result[2] = 0;
//        if (appCPs == null) return result;
        for (int i = 0; i < appInfoControllers.size(); i++) {
            if (!appInfoControllers.get(i).getInstallInfoController().isInstalled())
                result[2]++;
            else if (appInfoControllers.get(i).getInstallInfoController().hasUpdate())
                result[1]++;
            else result[0]++;
        }
        return result;
    }
    public void startBackground() {
        for (AppInfoController appInfoController : appInfoControllers) {
            if(appInfoController.getAppBasicInfoController().isType(MCEREBRUM.APP.TYPE_STUDY)) continue;
            if(appInfoController.getAppBasicInfoController().isType(MCEREBRUM.APP.TYPE_DATAKIT)) continue;
            if(appInfoController.getAppBasicInfoController().isType(MCEREBRUM.APP.TYPE_MCEREBRUM)) continue;
            MCerebrumController mCerebrumController = appInfoController.getmCerebrumController();
            if (!mCerebrumController.ismCerebrumSupported()) {
                Log.d("abc","------------->"+appInfoController.getAppBasicInfoController().getPackageName()+"--------->not supported");
                continue;
            }
            if(!mCerebrumController.isConfigured()) {
                Log.d("abc","------------->"+appInfoController.getAppBasicInfoController().getPackageName()+"--------->not configured");
                continue;
            }

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
        if(appInfoControllers==null) return;
        for (AppInfoController appInfoController : appInfoControllers) {
            MCerebrumController mCerebrumController = appInfoController.getmCerebrumController();
            if (!mCerebrumController.ismCerebrumSupported()) continue;
            if (!mCerebrumController.isStarted()) continue;
            if (!mCerebrumController.isRunInBackground()) continue;
            if (!mCerebrumController.isRunningInBackground()) continue;
            mCerebrumController.stopBackground(null);
        }
    }
*/

/*
    private AppInfo[] appInfos;
    private AppInfoController[] appInfoControllers;

    //    private AppInfoE[] appInfos;
//    private AppMC[] appMCs;
    public void set(AppInfo[] appInfos) {
        this.appInfos=appInfos;
        ArrayList<AppInfo> as = new ArrayList<>();
        appInfoControllers = new AppInfoController[appInfos.length];
        for (int i = 0; i < appInfos.length; i++) {
            appInfoControllers[i] = new AppInfoController(appInfos[i]);
            Log.d("abc","i="+i+" "+appInfos[i].getAppBasicInfo().getPackageName());
        }
    }

    public void start() {
        for (AppInfoController appInfoController : appInfoControllers) {
            appInfoController.getmCerebrumController().startService();
        }
    }

    public void stop() {
        if(appInfoControllers==null || appInfoControllers.length==0) return;
        for (AppInfoController appInfoController : appInfoControllers) {
            try {
                appInfoController.getmCerebrumController().stopService();
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

    public boolean startStudy(StudyInfo studyInfo, UserInfo userInfo) {
        if (!isCoreInstalled()) {
            Toasty.error(MyApplication.getContext(), "Study/DataKit not installed", Toast.LENGTH_SHORT).show();
            return false;
        }
        AppInfoController a = getAppByType(AppInfo.TYPE_STUDY).get(0);
        Bundle bundle = new Bundle();
        Info info = new Info(userInfo, studyInfo, appInfos);
        bundle.putParcelable("info", info);
//        Intent intent = MyApplication.getContext().getPackageManager().getLaunchIntentForPackage(a.getAppBasicInfoController().getPackageName());
//        intent.putExtras(bundle);

//        MyApplication.getContext().startActivity(intent);

        a.getmCerebrumController().launch(bundle);
        stop();
        return true;
    }

    public AppInfoController[] getAppInfoControllers() {
        return appInfoControllers;
    }

    public void setmCerebrumInfo() {
        for (AppInfoController appInfoController : appInfoControllers) {
            if (!appInfoController.getmCerebrumController().isServiceRunning())
                appInfoController.getmCerebrumController().startService();
            else appInfoController.getmCerebrumController().setStatus();
            Log.d("abc","-------------"+appInfoController.getAppBasicInfoController().getPackageName()+" ------------ "+appInfoController.getmCerebrumController().getPackageName());
        }
    }

    public boolean isCoreInstalled() {
        ArrayList<AppInfoController> apps=getAppByType(AppInfo.TYPE_STUDY);
        if(apps.size()!=0 && !apps.get(0).getInstallInfoController().isInstalled()) return false;
        apps=getAppByType(AppInfo.TYPE_DATAKIT);
        if(apps.size()!=0 && !apps.get(0).getInstallInfoController().isInstalled()) return false;
        return true;
    }

    public boolean isRequiredAppInstalled() {
        return getRequiredAppNotInstalled().size() == 0;
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
            if (!appInfo.getAppBasicInfoController().isUseAs(AppInfo.USE_AS_REQUIRED)) continue;
            if(!appInfo.getInstallInfoController().isInstalled()) appInfos.add(appInfo);

            else if (appInfo.getmCerebrumController().isServiceRunning()
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
            if (appInfo.getmCerebrumController().isServiceRunning()
                    && appInfo.getmCerebrumController().isConfigurable()
                    && !appInfo.getmCerebrumController().isEqualDefault()) {
                continue;
            }
            appInfos.add(appInfo);

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
            else if (appInfoControllers[i].getInstallInfoController().hasUpdate())
                result[1]++;
            else result[0]++;
        }
        return result;
    }

    */
/*
    public AppMC[] getAppMCs() {
        return appMCs;
    }
    public AppInfoE getAppInfo(String packageName){
        for (AppInfoE appInfo : appInfos)
            if (appInfo.getPackageName().equals(packageName))
                return appInfo;
        return null;
    }

    public boolean isRequiredAppInstalled(){
        if(appInfos ==null) return false;
        for(AppInfoE appInfo : appInfos)
            if(appInfo.isRequired() && !appInfo.isInstalled()) return false;
        return true;
    }

    public ArrayList<AppInfoE> getRequiredAppNotInstalled() {
        ArrayList<AppInfoE> appInfos = new ArrayList<>();
        if(this.appInfos ==null) return appInfos;
        for (AppInfoE appInfo : this.appInfos) {
            if (appInfo.isRequired() && !appInfo.isInstalled()) {
                appInfos.add(appInfo);
            }
        }
        return appInfos;
    }
    public ArrayList<AppInfoE> getAppConfigured() {
        ArrayList<AppInfoE> appInfos = new ArrayList<>();
        if(appInfos ==null) return appInfos;
        for (AppInfoE appInfo : this.appInfos) {
            if (appInfo.isInstalled() && appInfo.isMCerebrumSupported() && appInfo.getInfo()!=null && appInfo.getInfo().isConfigurable() && appInfo.getInfo().isConfigured()) {
                appInfos.add(appInfo);
            }
        }
        return appInfos;
    }
    public ArrayList<AppInfoE> getAppNotConfigured() {
        ArrayList<AppInfoE> a = new ArrayList<>();
        if(appInfos ==null) return a;
        for (AppInfoE appInfo : appInfos) {
            if (appInfo.isInstalled() && appInfo.isMCerebrumSupported() && appInfo.getInfo()!=null && appInfo.getInfo().isConfigurable() && !appInfo.getInfo().isConfigured()) {
                a.add(appInfo);
            }
        }
        return a;
    }


    public void getInfo() {
        if(appMCs ==null) return;
        for (AppMC appMC : appMCs) {
            appMC.setInfo();
        }
    }
    public AppInfoE getStudy(){
        return get(TYPE_STUDY);
    }
    public AppInfoE getMCerebrum(){
        return get(TYPE_MCEREBRUM);
    }

    public AppInfoE getDataKit(){
        return get(TYPE_DATA_KIT);
    }
    private AppInfoE get(String type){
        if(appInfos ==null) return null;
        for (AppInfoE appInfo : appInfos) {
            if (appInfo.getType() == null) continue;
            if (type.equals(appInfo.getType().toUpperCase()))
                return appInfo;
        }
        return null;

    }

    public AppInfoE[] getAppInfos() {
        return appInfos;
    }

    public void reset(String packageName) {
        for(int i=0;i<appInfos.length;i++)
            if(appInfos[i].getPackageName().equals(packageName)){
                boolean lastResult=appInfos[i].isInstalled();
                appInfos[i].setInitialized(false);
                appInfos[i].setInstalled();
                if(appInfos[i].isInstalled()!=lastResult)
                    if(appInfos[i].isInstalled())
                        appMCs[i].startService();
                else appMCs[i].stopService();
            }
    }

    public void setInfo() {
        for (int i=0;i<appInfos.length;i++) {
            if (!appInfos[i].isInstalled()) continue;
            if (!appInfos[i].isMCerebrumSupported()) continue;
            appMCs[i].setInfo();
        }
    }

    public void configure(String packageName) {
        for(int i=0;i<appInfos.length;i++)
            if(appInfos[i].getPackageName().equals(packageName))
                appMCs[i].configure();
    }
    public void clear(String packageName) {
        for(int i=0;i<appInfos.length;i++)
            if(appInfos[i].getPackageName().equals(packageName))
                appMCs[i].clear();
    }
    public AppBasicInfo[] getAppInfo(){
        AppBasicInfo[] a=new AppBasicInfo[appInfos.length];
        for(int i=0;i<appInfos.length;i++)
            a[i]=appInfos[i].getAppInfo();
        return a;
    }
*//*


*/
}
