package org.md2k.studymperf;

import android.content.Intent;

import org.md2k.mcerebrum.core.access.AbstractServiceMCerebrum;

public class ServiceMCerebrum extends AbstractServiceMCerebrum {
    public ServiceMCerebrum() {
    }


    @Override
    public void initialize() {
/*
        Permission.requestPermission(this, new PermissionCallback() {
            @Override
            public void OnResponse(boolean isGranted) {
                if (isGranted) {
                }
            }
        });
*/
    }

    @Override
    public void launch() {
        Intent intent=new Intent(this, ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void startBackground() {
    }

    @Override
    public void stopBackground() {
    }

    @Override
    public void report() {
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean hasReport() {
        return false;
    }

    @Override
    public boolean isRunInBackground() {
        return false;
    }

    @Override
    public long getRunningTime() {
        return -1;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public boolean isConfigured() {
        return true;
    }

    @Override
    public boolean isConfigurable() {
        return false;
    }
    @Override
    public void configure() {
    }

}
