package org.md2k.studymperf;

import android.content.Intent;
import android.os.Bundle;

import org.md2k.studymperf.data_quality.DataQualityManager;
import org.md2k.studymperf.data_quality.ResultCallback;
import org.md2k.studymperf.data_quality.UserViewDataQuality;


public class ActivityMain extends AbstractActivityMenu {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startBackground();

    }
    public void startBackground(){
        if(appInfos!=null && appInfos.length!=0) {
            Intent intent = new Intent(this, ServiceStudy.class);
            intent.putExtras(getIntent().getExtras());
            startService(intent);
        }
    }
}
