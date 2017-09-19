package org.md2k.studymperf;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.blankj.utilcode.util.Utils;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.mcerebrum.commons.dialog.Dialog;
import org.md2k.mcerebrum.commons.dialog.DialogCallback;
import org.md2k.mcerebrum.commons.permission.Permission;
import org.md2k.mcerebrum.commons.permission.PermissionCallback;
import org.md2k.md2k.system.Info;
import org.md2k.md2k.system.app.AppInfo;
import org.md2k.md2k.system.study.StudyInfo;
import org.md2k.md2k.system.user.UserInfo;
import org.md2k.studymperf.data_quality.DataQualityManager;

import es.dmoral.toasty.Toasty;

public abstract class AbstractActivityBasics extends AppCompatActivity {
    static final String TAG = AbstractActivityBasics.class.getSimpleName();
    public DataQualityManager dataQualityManager;

    public UserInfo userInfo;
    public StudyInfo studyInfo;
    DataKitAPI dataKitAPI;
    Toolbar toolbar;
    AppInfo[] appInfos;
    boolean start;

    abstract void updateUI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.init(this);
        dataQualityManager = new DataQualityManager();
        if (getIntent().getExtras() != null) {
            Info info = getIntent().getExtras().getParcelable("info");
            userInfo = info.getUserInfo();
            studyInfo = info.getStudyInfo();
            appInfos = info.getAppInfo();
            Log.d(TAG, "-----------------appInfos=" + appInfos.length);
/*
            if (p != null && p.length != 0) {
                appInfos = new AppInfo[p.length];
                for (int i = 0; i < p.length; i++) {
                    appInfos[i] = (AppInfo) p[i];
                }
            }
*/
        }
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getStudyName());
        try {
            DataKitAPI.getInstance(this).connect(new OnConnectionListener() {
                @Override
                public void onConnected() {
                    dataQualityManager.set();
                }
            });
        } catch (DataKitException e) {
            e.printStackTrace();
        }

        Permission.requestPermission(this, new PermissionCallback() {
            @Override
            public void OnResponse(boolean isGranted) {
                if (!isGranted) {
                    Toasty.error(getApplicationContext(), "!PERMISSION DENIED !!! Could not continue...", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    updateUI();
                    Log.d("abc", "abc");
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Handle Code
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle the click on the back arrow click
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        if (dataQualityManager != null)
            dataQualityManager.clear();
        if (dataKitAPI != null)
            dataKitAPI.disconnect();
        super.onDestroy();
    }

    public String getStudyName() {
        String studyName = "mPerf Study";
        if (studyInfo != null)
            studyName = studyInfo.getTitle();
        return studyName;
    }

    public String getUserName() {
        String studyName = "Default";
        if (userInfo != null)
            studyName = userInfo.getTitle();
        return studyName;
    }

    public boolean isLoggedIn() {
        boolean loggedIn = false;
        if (userInfo != null)
            loggedIn = userInfo.isLoggedIn();
        return loggedIn;
    }

    public void startDataCollection() {
        if (appInfos != null && appInfos.length != 0) {
            Intent intent = new Intent(this, ServiceStudy.class);
            intent.putExtra("app_info", appInfos);
            startService(intent);
            start=true;
            updateUI();
        }
    }

    public void stopDataCollection() {
        Dialog.simple(this, "Stop Data Collection", "Do you want to stop data collection?", "Yes", "Cancel", new DialogCallback() {
            @Override
            public void onSelected(String value) {
                if(value.equals("Yes")){
                    Intent intent = new Intent(AbstractActivityBasics.this, ServiceStudy.class);
                    stopService(intent);
                    start=false;
                    updateUI();
                }
            }
        }).show();
    }
}

