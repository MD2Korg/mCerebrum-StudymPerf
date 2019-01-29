package org.md2k.studymperf.wrist_battery;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeDouble;
import org.md2k.datakitapi.datatype.DataTypeDoubleArray;
import org.md2k.datakitapi.datatype.DataTypeInt;
import org.md2k.datakitapi.datatype.DataTypeIntArray;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.messagehandler.OnReceiveListener;
import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.source.platform.Platform;
import org.md2k.datakitapi.source.platform.PlatformBuilder;
import org.md2k.datakitapi.source.platform.PlatformId;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.studymperf.AbstractActivityBasics;
import org.md2k.studymperf.MyApplication;
import org.md2k.studymperf.R;
import org.md2k.studymperf.step_count.ActivityStepCountPieChart;
import org.md2k.studymperf.step_count.UserViewStepCount;

import java.util.ArrayList;
import java.util.Calendar;

import mehdi.sakout.fancybuttons.FancyButton;

public class UserViewWristBattery {
    private static final String TAG = UserViewWristBattery.class.getSimpleName();
    private Handler leftHandler;
    private Handler rightHandler;
    private View view;
    private Activity activity;


    private DataSourceBuilder leftWristBuilder;
    private DataSourceBuilder rightWristBuilder;
    private DataKitAPI dataKitAPI;

    public UserViewWristBattery(Activity activity, View view) {
        Log.d(TAG, "UserViewWristBattery started...");
        leftHandler = new Handler();
        rightHandler = new Handler();
        this.view = view;
        this.activity = activity;

        Platform pleft = new PlatformBuilder().setId(PlatformId.LEFT_WRIST).build();
        leftWristBuilder = new DataSourceBuilder().setType(DataSourceType.BATTERY).setPlatform(pleft);
        Platform pright = new PlatformBuilder().setId(PlatformId.RIGHT_WRIST).build();
        rightWristBuilder = new DataSourceBuilder().setType(DataSourceType.BATTERY).setPlatform(pright);
    }

    public void set() {
        leftHandler.removeCallbacks(runnableLeftWristBattery);
        leftHandler.post(runnableLeftWristBattery);
        rightHandler.removeCallbacks(runnableRightWristBattery);
        rightHandler.post(runnableRightWristBattery);
    }

    public void clear() {
        leftHandler.removeCallbacks(runnableLeftWristBattery);
        rightHandler.removeCallbacks(runnableRightWristBattery);
    }

    private Runnable runnableLeftWristBattery = new Runnable() {
        @Override
        public void run() {
            subFromDataKit(leftWristBuilder);
            if (!dataKitAPI.isConnected()) {
                leftHandler.postDelayed(this, 5000);
            }
        }
    };

    private Runnable runnableRightWristBattery = new Runnable() {
        @Override
        public void run() {
            subFromDataKit(rightWristBuilder);
            if (!dataKitAPI.isConnected()) {
                rightHandler.postDelayed(this, 5000);
            }
        }
    };

    private void subFromDataKit(DataSourceBuilder dataSourceBuilder) {
        Log.d(TAG, "subFromDataKit started");
        try {
            dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            Log.d(TAG, dataKitAPI.isConnected() + "");
            ArrayList<DataSourceClient> dataSourceClients = dataKitAPI.find(dataSourceBuilder);

            if (dataSourceClients.size() > 0) {
                if (dataSourceClients.get(0).getDataSource().getPlatform().getId().equals(PlatformId.LEFT_WRIST)) {
                    dataKitAPI.subscribe(dataSourceClients.get(0), subLeftListener);
                    Log.d(TAG, "Subbed left wrist.");
                } else if (dataSourceClients.get(0).getDataSource().getPlatform().getId().equals(PlatformId.RIGHT_WRIST)) {
                    dataKitAPI.subscribe(dataSourceClients.get(0), subRightListener);
                    Log.d(TAG, "Subbed right wrist.");
                }
            } else {
                Log.d(TAG, "Wrist Battery not available");
            }
        } catch (Exception e) {
            Log.d(TAG, "subFromDatakit failed", e);
        }
    }

    private OnReceiveListener subLeftListener = new OnReceiveListener() {
        @Override
        public void onReceived(DataType dataType) {
            if (dataType instanceof DataTypeDoubleArray) {
                Log.d(TAG, "Left wrist battery data received. Array length: " + ((DataTypeDoubleArray) dataType).getSample().length);
                int sample = (int)((DataTypeDoubleArray) dataType).getSample()[0];
                ((TextView) view.findViewById(R.id.textview_left_wrist_battery)).setText(sample + "%");
            } else {
                Log.d(TAG, "Battery returned non-double array sample value.");
            }
        }
    };

    private OnReceiveListener subRightListener = new OnReceiveListener() {
        @Override
        public void onReceived(DataType dataType) {
            if (dataType instanceof DataTypeDoubleArray) {
                Log.d(TAG, "Right wrist battery data received. Array length: " + ((DataTypeDoubleArray) dataType).getSample().length);
                int sample = (int)((DataTypeDoubleArray) dataType).getSample()[0];
                ((TextView) view.findViewById(R.id.textview_right_wrist_battery)).setText(sample + "%");
            } else {
                Log.d(TAG, "Battery returned non-double array sample value.");
            }
        }
    };
}