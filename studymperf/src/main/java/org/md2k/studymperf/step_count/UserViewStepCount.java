package org.md2k.studymperf.step_count;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeInt;
import org.md2k.datakitapi.datatype.DataTypeIntArray;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.studymperf.AbstractActivityBasics;
import org.md2k.studymperf.MyApplication;
import org.md2k.studymperf.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import mehdi.sakout.fancybuttons.FancyButton;


/**
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p/>
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
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
public class UserViewStepCount {
    private static final String TAG = UserViewStepCount.class.getSimpleName();
    private Handler handler;
    private View view;
    private Activity activity;

    public UserViewStepCount(Activity activity, View view) {
        handler = new Handler();
        this.view = view;
        this.activity = activity;
    }
    public void set() {
        handler.removeCallbacks(runnableStepCount);
        prepareButton();
        handler.post(runnableStepCount);
    }

    public void clear() {
        handler.removeCallbacks(runnableStepCount);
    }

    private Runnable runnableStepCount = new Runnable() {
        @Override
        public void run() {
            int value = readFromDataKit();
            int goal = readGoal();
            ((TextView)view.findViewById(R.id.textview_total_step)).setText(String.valueOf(value)+"/");
            ((TextView)view.findViewById(R.id.textview_step_goal)).setText(String.valueOf(goal));
            handler.postDelayed(this, 5000);
        }
    };

    private int readFromDataKit() {
        int totalSteps = 0;
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            ArrayList<DataSourceClient> dataSourceClients = dataKitAPI.find(createDataSourceBuilder());
            if (dataSourceClients.size() > 0) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.MILLISECOND, 0);c.set(Calendar.SECOND,0);
                c.set(Calendar.MINUTE, 0);c.set(Calendar.HOUR_OF_DAY, 0);
                long sTime = c.getTimeInMillis();
                long eTime = c.getTimeInMillis()+1000;
                ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClients.get(0), sTime, eTime);
                if (dataTypes.size() != 0) {
                    try {
                        DataTypeIntArray dataTypeIntArray = (DataTypeIntArray) dataTypes.get(0);
                        totalSteps = dataTypeIntArray.getSample()[0];
                    } catch (Exception ignored) {
//                        LocalBroadcastManager.getInstance(modelManager.getContext()).sendBroadcast(new Intent(Constants.INTENT_RESTART));
                    }
                }else totalSteps=0;
            }
        } catch (DataKitException e) {
            LocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent(AbstractActivityBasics.INTENT_RESTART));
        }
        return totalSteps;
    }

    private DataSourceBuilder createDataSourceBuilder() {
        return new DataSourceBuilder().setType("STEP_COUNT_SUMMARY_DAY");
    }
    private int readGoal(){
            DataKitAPI dataKitAPI=DataKitAPI.getInstance(activity);
            DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType("GOAL_STEP_COUNT");
            try {
                ArrayList<DataSourceClient> dataSourceClient = dataKitAPI.find(dataSourceBuilder);
                if(dataSourceClient.size()>0) {
                    ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClient.get(0),1);
                    if(dataTypes.size()>0) {
                        return ((DataTypeInt) dataTypes.get(0)).getSample();
                    }
                }
            } catch (DataKitException e) {
                LocalBroadcastManager.getInstance(activity).sendBroadcast(new Intent(AbstractActivityBasics.INTENT_RESTART));
            }
       return 10000;
    }
    private void prepareButton(){
        final long lastTime=DateTime.getDateTime();
        FancyButton fitness= (FancyButton) view.findViewById(R.id.btn_fitness);
        fitness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DateTime.getDateTime()-lastTime<500) return;
                Intent intent=new Intent(activity,ActivityStepCountPieChart.class);
                intent.putExtra("total_steps",readFromDataKit());
                intent.putExtra("goal",readGoal());
                activity.startActivity(intent);
            }
        });

    }
}
