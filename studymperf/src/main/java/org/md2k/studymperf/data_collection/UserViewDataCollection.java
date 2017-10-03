package org.md2k.studymperf.data_collection;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapText;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeInt;
import org.md2k.datakitapi.datatype.DataTypeIntArray;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.mcerebrum.core.data_format.DATA_QUALITY;
import org.md2k.studymperf.AbstractActivityBasics;
import org.md2k.studymperf.MyApplication;
import org.md2k.studymperf.R;

import java.util.ArrayList;
import java.util.Locale;

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
public class UserViewDataCollection {
    private static final String TAG = UserViewDataCollection.class.getSimpleName();
    private Handler handler;
    private View view;
    private Activity activity;

    public UserViewDataCollection(Activity activity, View view) {
        handler = new Handler();
        this.view = view;
        this.activity = activity;
    }
    public void set() {
        handler.removeCallbacks(runnable);
        prepareButton();
        handler.post(runnable);
    }

    public void clear() {
        handler.removeCallbacks(runnable);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int value = readFromDataKit();
            value/=(1000*60);
            int min=value%60;
            value/=60;
            int hour = value;
            String timeStr=String.format(Locale.getDefault(), "Wrist  %02d h : %02d m",hour, min);
            BootstrapText bootstrapText=new BootstrapText.Builder(activity).addText(timeStr).build();
            ((AwesomeTextView)view.findViewById(R.id.textview_data_collected)).setBootstrapText(bootstrapText);
            handler.postDelayed(this, 60000);
        }
    };

    private int readFromDataKit() {
        int goodDataCollected = 0;
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(MyApplication.getContext());
            ArrayList<DataSourceClient> dataSourceClients = dataKitAPI.find(createDataSourceBuilder());
            if (dataSourceClients.size() > 0) {
                for(int i=0;i<dataSourceClients.size();i++) {
                    ArrayList<DataType> dataTypes = dataKitAPI.query(dataSourceClients.get(i), 1);
                    if (dataTypes.size() != 0) {
                        try {
                            DataTypeIntArray dataTypeIntArray = (DataTypeIntArray) dataTypes.get(0);
                            if(goodDataCollected <dataTypeIntArray.getSample()[DATA_QUALITY.GOOD])
                                goodDataCollected = dataTypeIntArray.getSample()[DATA_QUALITY.GOOD];
                        } catch (Exception ignored) {
//                        LocalBroadcastManager.getInstance(modelManager.getContext()).sendBroadcast(new Intent(Constants.INTENT_RESTART));
                        }
                    }
                }
            }
        } catch (DataKitException e) {
            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent(AbstractActivityBasics.INTENT_RESTART));
        }
        return goodDataCollected;
    }

    private DataSourceBuilder createDataSourceBuilder() {
        return new DataSourceBuilder().setType("DATA_QUALITY_SUMMARY_DAY");
    }
    private int readGoal(){
            DataKitAPI dataKitAPI=DataKitAPI.getInstance(activity);
            DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType("GOAL_DATA_COLLECTION");
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
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(activity,ActivityPieChartDataCollection.class);
            intent.putExtra("total_data_collection",readFromDataKit());
            intent.putExtra("goal",readGoal());
            activity.startActivity(intent);
        }
    };
    private void prepareButton(){
        FancyButton dataCollection= (FancyButton) view.findViewById(R.id.btn_data_collection);
        dataCollection.setOnClickListener(onClickListener);
        AwesomeTextView t= (AwesomeTextView) view.findViewById(R.id.textview_data_collected);
        t.setOnClickListener(onClickListener);

    }

/*
    void prepareButton(){
        FancyButton fitness= (FancyButton) view.findViewById(R.id.btn_fitness);
        fitness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(activity,ActivityPieChartDataCollection.class);
                intent.putExtra("total_steps",readFromDataKit());
                intent.putExtra("goal",readGoal());
                activity.startActivity(intent);
            }
        });

    }
*/
}
