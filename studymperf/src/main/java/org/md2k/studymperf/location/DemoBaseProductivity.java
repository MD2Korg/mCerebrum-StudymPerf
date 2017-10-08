package org.md2k.studymperf.location;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataType;
import org.md2k.datakitapi.datatype.DataTypeIntArray;
import org.md2k.datakitapi.datatype.DataTypeString;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.studymperf.AbstractActivityBasics;
import org.md2k.studymperf.MyApplication;
import org.md2k.studymperf.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by nusrat on 8/15/2017.
 */

public abstract class DemoBaseProductivity extends FragmentActivity {

    protected String[] mMonths = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };

    protected String[] locationNames;
    protected String[] locationWithTime;

    protected float[] mTimes;
    protected Typeface mTfRegular;
    protected Typeface mTfLight;
    ArrayList<String> locations;
    ArrayList<Integer> time;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");
    }

    protected float getRandom(float range, float startsfrom) {
        return (float) (Math.random() * range) + startsfrom;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }
    void readData(){
        locations=new ArrayList<>();
        time = new ArrayList<>();
        try {
            DataKitAPI dataKitAPI = DataKitAPI.getInstance(this);
            DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType(DataSourceType.GEOFENCE).setId("LIST");
            ArrayList<DataSourceClient> dsc = dataKitAPI.find(dataSourceBuilder);
            if(dsc.size()==0) return;
            ArrayList<DataType> dt=dataKitAPI.query(dsc.get(0), 1);
            if(dt.size()==0) return;
            String str=((DataTypeString)dt.get(0)).getSample();
            if(str==null || str.length()==0) return;
            String[] splits=str.split("#");
            for(int i=0;i<splits.length;i+=3){
                String loc=splits[i];
                int t = 0;
                dataSourceBuilder = dataSourceBuilder.setType(DataSourceType.GEOFENCE+"_SUMMARY_DAY").setId(loc);
                ArrayList<DataSourceClient> dsc1 = dataKitAPI.find(dataSourceBuilder);
                if(dsc1.size()!=0) {
                    ArrayList<DataType> dt1 = dataKitAPI.query(dsc1.get(0), 1);
                    if (dt1.size() != 0){
                        t=((DataTypeIntArray)dt1.get(0)).getSample()[0];
                    }
                }
                locations.add(loc);
                time.add(t);
            }

        } catch (DataKitException e) {

            LocalBroadcastManager.getInstance(MyApplication.getContext()).sendBroadcast(new Intent(AbstractActivityBasics.INTENT_RESTART));
        }
    }
    void prepare(){
        locationNames =new String[locations.size()+1];
        locationWithTime =new String[locations.size()+1];
        mTimes=new float[locations.size()+1];
        int spentTime = 0;
        for(int i=0;i<locations.size();i++){
            int t=time.get(i)/(1000*60);
            int m=t%60;
            int h=t/60;
            locationWithTime[i]=String.format(Locale.getDefault(), "%-10s %02d:%02d",locations.get(i), h,m);
            locationNames[i]=locations.get(i);
//            locationNames[i]=locations.get(i)+":"+h+" h, "+m+" m)";
            mTimes[i]= time.get(i);
            spentTime+=time.get(i);
        }
        int time=(getTimeTillNow()-spentTime)/(1000*60);
        if(time<0) time=0;
        int m=time%60;
        int h=time/60;
        locationWithTime[locations.size()]=String.format(Locale.getDefault(), "%-10s %02d:%02d","Other", h,m);
        locationNames[locations.size()]="Other";
        spentTime+=time;
        mTimes[locations.size()]=time;
        for(int i=0;i<mTimes.length;i++){
            mTimes[i]/=spentTime;
        }

    }
    int getTimeTillNow(){
        long curTime, timeAtMidNight;
        Calendar calendar = Calendar.getInstance();
        curTime=calendar.getTimeInMillis();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        timeAtMidNight=calendar.getTimeInMillis();
        return (int) (curTime - timeAtMidNight);
    }
    int getTimeToMidNight(){
        long curTime, timeAtMidNight;
        Calendar calendar = Calendar.getInstance();
        curTime=calendar.getTimeInMillis();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        timeAtMidNight=calendar.getTimeInMillis()+24*60*60*1000;
        return (int) (timeAtMidNight-curTime);
    }
}
