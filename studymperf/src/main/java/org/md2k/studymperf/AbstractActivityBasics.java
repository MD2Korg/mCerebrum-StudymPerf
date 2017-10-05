package org.md2k.studymperf;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.blankj.utilcode.util.Utils;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.mcerebrum.commons.app_info.AppInfo;
import org.md2k.mcerebrum.commons.dialog.Dialog;
import org.md2k.mcerebrum.commons.dialog.DialogCallback;
import org.md2k.mcerebrum.commons.permission.Permission;
import org.md2k.mcerebrum.commons.permission.PermissionCallback;
import org.md2k.studymperf.data_quality.DataQualityManager;
import org.md2k.system.provider.AppCP;
import org.md2k.system.provider.StudyCP;
import org.md2k.system.provider.UserCP;
import org.md2k.system.provider.appinfo.AppInfoCursor;
import org.md2k.system.provider.appinfo.AppInfoSelection;
import org.md2k.system.provider.studyinfo.StudyInfoCursor;
import org.md2k.system.provider.studyinfo.StudyInfoSelection;
import org.md2k.system.provider.userinfo.UserInfoCursor;
import org.md2k.system.provider.userinfo.UserInfoSelection;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public abstract class AbstractActivityBasics extends AppCompatActivity {
    static final String TAG = AbstractActivityBasics.class.getSimpleName();
    public static final String INTENT_RESTART = "INTENT_RESTART";
    public DataQualityManager dataQualityManager;

    public UserCP userInfo;
    public StudyCP studyInfo;
    ArrayList<AppCP> appInfos;
    Toolbar toolbar;
    Handler handler;

    abstract void updateUI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler=new Handler();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(INTENT_RESTART));
        Utils.init(this);
        readStudy();
        readUser();
        readApp();
        dataQualityManager = new DataQualityManager();
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().

                setTitle(getStudyName());
        try {
            DataKitAPI.getInstance(this).connect(new OnConnectionListener() {
                @Override
                public void onConnected() {
                    dataQualityManager.set();
                }
            });
        } catch (DataKitException e) {
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent(AbstractActivityBasics.INTENT_RESTART));
        }

        Permission.requestPermission(this, new

                PermissionCallback() {
                    @Override
                    public void OnResponse(boolean isGranted) {
                        if (!isGranted) {
                            Toasty.error(getApplicationContext(), "!PERMISSION DENIED !!! Could not continue...", Toast.LENGTH_SHORT).show();
                            stopAll();
                            finish();
                        } else {
                            updateUI();
                            Log.d("abc", "abc");
                        }
                    }
                });

    }
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            startDataCollection();
        }
    };

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

    void stopAll() {
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        } catch (Exception ignored) {
        }
        if (dataQualityManager != null)
            dataQualityManager.clear();
        try {
            DataKitAPI.getInstance(this).disconnect();
        } catch (Exception e) {

        }
    }

    @Override
    public void onDestroy() {
        stopAll();
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
        if (appInfos != null && appInfos.size() != 0) {
            if (!AppInfo.isServiceRunning(this, ServiceStudy.class.getName())) {
                Intent intent = new Intent(this, ServiceStudy.class);
                startService(intent);
            }
            studyInfo.setStarted(this, true);
            updateUI();
        }
    }

    public void stopDataCollection() {
        Dialog.simple(this, "Stop Data Collection", "Do you want to stop data collection?", "Yes", "Cancel", new DialogCallback() {
            @Override
            public void onSelected(String value) {
                if (value.equals("Yes")) {
                    Intent intent = new Intent(AbstractActivityBasics.this, ServiceStudy.class);
                    stopService(intent);
                    studyInfo.setStarted(AbstractActivityBasics.this, false);
                }
                updateUI();
            }
        }).show();
    }
    public void resetDataCollection(){
        Dialog.simple(this, "Reset Application", "Do you want to reset application?", "Yes", "Cancel", new DialogCallback() {
            @Override
            public void onSelected(String value) {
                if (value.equals("Yes")) {
                    Intent intent = new Intent(AbstractActivityBasics.this, ServiceStudy.class);
                    stopService(intent);

                    handler.postDelayed(runnable, 2000);
                }
                updateUI();
            }
        }).show();
    }

    public void stopAndQuit() {
        boolean start = AppInfo.isServiceRunning(this, ServiceStudy.class.getName());
        if (start)
            Dialog.simple(this, "Settings", "Do you want to stop data collection and open settings?", "Yes", "Cancel", new DialogCallback() {
                @Override
                public void onSelected(String value) {
                    if (value.equals("Yes")) {
                        Intent intent = new Intent(AbstractActivityBasics.this, ServiceStudy.class);
                        stopService(intent);
                        studyInfo.setStarted(AbstractActivityBasics.this, false);
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("org.md2k.mcerebrum");
                        startActivity(launchIntent);
                        stopAll();
                        finish();
                    } else {
                        updateUI();
//                        responseCallBack.onResponse(null, MyMenu.MENU_HOME);
//                        setTitle(getStudyName());
                    }
                }
            }).show();
        else {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("org.md2k.mcerebrum");
            startActivity(launchIntent);
            stopAll();
            finish();
        }
    }

    void readStudy() {
        StudyInfoSelection s = new StudyInfoSelection();
        StudyInfoCursor c = s.query(this);
        if (c.moveToNext()) {
            studyInfo = new StudyCP();
            studyInfo.setFromCP(c);
        }
        c.close();
    }

    void readUser() {
        UserInfoSelection s = new UserInfoSelection();
        UserInfoCursor c = s.query(this);
        if (c.moveToNext()) {
            userInfo = new UserCP();
            userInfo.set(c);
        }
        c.close();
    }

    void readApp() {
        AppInfoSelection s = new AppInfoSelection();
        AppInfoCursor c = s.query(this);
        appInfos = new ArrayList<>();
        while (c.moveToNext()) {
            AppCP a = new AppCP();
            a.setFromCP(c);
            appInfos.add(a);
        }
        c.close();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try{
                DataKitAPI.getInstance(MyApplication.getContext()).disconnect();
            }catch (Exception ignored){}
            try {
                DataKitAPI.getInstance(MyApplication.getContext()).connect(new OnConnectionListener() {
                    @Override
                    public void onConnected() {
                        dataQualityManager.set();
                    }
                });
            } catch (DataKitException e) {
                Toasty.error(AbstractActivityBasics.this, "DataKit:"+e.getMessage(), Toast.LENGTH_LONG).show();
                finish();
//                LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent(AbstractActivityBasics.INTENT_RESTART));
            }
        }
    };


}

