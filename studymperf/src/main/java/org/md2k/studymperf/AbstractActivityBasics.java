package org.md2k.studymperf;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.attributes.BootstrapBrand;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.blankj.utilcode.util.Utils;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.messagehandler.OnConnectionListener;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.mcerebrum.commons.dialog.Dialog;
import org.md2k.mcerebrum.commons.dialog.DialogCallback;
import org.md2k.mcerebrum.commons.permission.Permission;
import org.md2k.mcerebrum.commons.permission.PermissionCallback;
import org.md2k.mcerebrum.core.access.appinfo.AppInfo;
import org.md2k.mcerebrum.core.access.studyinfo.StudyCP;
import org.md2k.mcerebrum.system.appinfo.BroadCastMessage;
import org.md2k.mcerebrum.system.update.Update;
import org.md2k.studymperf.data_quality.DataQualityManager;

import es.dmoral.toasty.Toasty;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public abstract class AbstractActivityBasics extends AppCompatActivity {
    static final String TAG = AbstractActivityBasics.class.getSimpleName();
    public static final String INTENT_RESTART = "INTENT_RESTART";
    public DataQualityManager dataQualityManager;
    public boolean isServiceRunning;
    AwesomeTextView tv;

    Toolbar toolbar;
    Handler handler;
    int count = 0;
    public long startT;
    public long cc = 0;

    abstract void updateUI();

    abstract void createUI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(INTENT_RESTART));
        Utils.init(this);
        dataQualityManager = new DataQualityManager();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        isServiceRunning=false;
        tv = (AwesomeTextView) findViewById(R.id.textview_status);

        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getStudyName());
        SharedPreferences sharedpreferences = getSharedPreferences("permission", Context.MODE_PRIVATE);
        if (sharedpreferences.getBoolean("permission", false) == true) {
            connectDataKit();
            createUI();
        } else {
            Permission.requestPermission(this, new PermissionCallback() {
                @Override
                public void OnResponse(boolean isGranted) {
                    SharedPreferences sharedpreferences = getSharedPreferences("permission", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean("permission", isGranted);
                    editor.apply();
                    if (!isGranted) {
                        Toasty.error(getApplicationContext(), "!PERMISSION DENIED !!! Could not continue...", Toast.LENGTH_SHORT).show();
                        stopAll();
                        finish();
                    } else {
                        connectDataKit();
                        createUI();
                    }
                }
            });
        }
        Log.d("abc", "abe_count=:" + (++cc) + " " + (startT - DateTime.getDateTime()));
        startT = DateTime.getDateTime();

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startDataCollection();
        }
    };
    void connectDataKit(){
        try {
            DataKitAPI.getInstance(AbstractActivityBasics.this).connect(new OnConnectionListener() {
                @Override
                public void onConnected() {
                    dataQualityManager.set();
                    startDataCollection();
                }
            });
        } catch (DataKitException e) {
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent(AbstractActivityBasics.INTENT_RESTART));
        }

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
        return StudyCP.getTitle(this);
    }

    public void startDataCollection() {
        Observable.just(true).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).map(new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean) {
                if (!AppInfo.isServiceRunning(AbstractActivityBasics.this, ServiceStudy.class.getName())) {
                    Intent intent = new Intent(AbstractActivityBasics.this, ServiceStudy.class);
                    startService(intent);
                }
                StudyCP.setStarted(AbstractActivityBasics.this, true);
                isServiceRunning=true;
                updateStatus();
                updateUI();
                return true;
            }
        }).subscribe();
    }

    public void stopDataCollection() {
        Dialog.simple(this, "Stop Data Collection", "Do you want to stop data collection?", "Yes", "Cancel", new DialogCallback() {
            @Override
            public void onSelected(String value) {
                if (value.equals("Yes")) {
                    Intent intent = new Intent(AbstractActivityBasics.this, ServiceStudy.class);
                    stopService(intent);
                }
                StudyCP.setStarted(AbstractActivityBasics.this, false);
                isServiceRunning=false;
                updateStatus();
                updateUI();
            }
        }).show();
    }

    public void resetDataCollection() {
        Dialog.simple(this, "Reset Application", "Do you want to reset application?", "Yes", "Cancel", new DialogCallback() {
            @Override
            public void onSelected(String value) {
                if (value.equals("Yes")) {
                    Intent intent = new Intent(AbstractActivityBasics.this, ServiceStudy.class);
                    stopService(intent);
                    handler.postDelayed(runnable, 3000);
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
                        StudyCP.setStarted(AbstractActivityBasics.this, false);
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


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                DataKitAPI.getInstance(MyApplication.getContext()).disconnect();
            } catch (Exception ignored) {
            }
            try {
                DataKitAPI.getInstance(MyApplication.getContext()).connect(new OnConnectionListener() {
                    @Override
                    public void onConnected() {
                        dataQualityManager.set();
                    }
                });
            } catch (DataKitException e) {
                Toasty.error(AbstractActivityBasics.this, "DataKit:" + e.getMessage(), Toast.LENGTH_LONG).show();
                finish();
//                LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent(AbstractActivityBasics.INTENT_RESTART));
            }
        }
    };
    void updateStatus(){
        if(!isServiceRunning) {
            updateStatus("Data collection off", DefaultBootstrapBrand.DANGER, false);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(ServiceStudy.NOTIFY_ID, ServiceStudy.getCompatNotification(this,"Data Collection - OFF (click to start)"));
        }else{
            updateStatus(null, DefaultBootstrapBrand.SUCCESS, true);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(ServiceStudy.NOTIFY_ID, ServiceStudy.getCompatNotification(this,"Data Collection - ON"));
        }

    }

    void updateStatus(String msg, BootstrapBrand brand, boolean isSuccess){

        tv.setBootstrapBrand(brand);
        if(isSuccess) {
            int uNo= Update.hasUpdate(this);
            if(uNo==0)
                tv.setBootstrapText(new BootstrapText.Builder(this).addText("Status: ").addFontAwesomeIcon("fa_check_circle").build());
            else {
                tv.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
                tv.setBootstrapText(new BootstrapText.Builder(this).addText("Status: ").addFontAwesomeIcon("fa_check_circle").addText(" (Update Available)").build());
            }
        }
        else
            tv.setBootstrapText(new BootstrapText.Builder(this).addText("Status: ").addFontAwesomeIcon("fa_times_circle").addText(" ("+msg+")").build());
    }
    @Override
    public void onResume(){
        updateStatus();
        super.onResume();
    }

}

