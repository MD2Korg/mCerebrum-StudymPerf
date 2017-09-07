package org.md2k.studymperf;

import android.content.Intent;
import android.os.Bundle;

import org.md2k.mcerebrum.commons.app_info.AppInfo;
import org.md2k.mcerebrum.commons.permission.Permission;
import org.md2k.mcerebrum.commons.permission.PermissionCallback;
import org.md2k.mcerebrum.core.access.AbstractActivityMCerebrumAccess;

public class ActivityMCerebrumAccess extends AbstractActivityMCerebrumAccess {

    @Override
    public boolean isRunning() {
        return AppInfo.isServiceRunning(this, ServiceStudy.class.getName());
    }
    @Override
    public boolean plot(){
        return false;
    }

    @Override
    public long runningTime() {
        return AppInfo.serviceRunningTime(this, ServiceStudy.class.getName());
    }

    @Override
    public boolean initialize() {
        Permission.requestPermission(this, new PermissionCallback() {
            @Override
            public void OnResponse(boolean isGranted) {
                if (isGranted) {
                    prepareConfig();
                }
            }
        });
        return true;
    }


    void prepareConfig() {

    }
    @Override
    public boolean hasReport(){
        return false;
    }
    @Override
    public boolean IsRunInBackground() {
        return true;
    }

    @Override
    public boolean isConfigured() {
        return true;/*Configuration.isEqual();*/
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }

    @Override
    public boolean configure() {
/*
        Intent intent = new Intent(this, ActivitySettings.class);
        startActivity(intent);
*/
        return true;
    }

    @Override
    public boolean start() {
        Intent intent = new Intent(this, ActivityMain.class);
        startActivity(intent);
        return true;
    }

    @Override
    public boolean stop() {
/*
        Intent intent = new Intent(this, ServiceStudy.class);
        stopService(intent);
*/
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
