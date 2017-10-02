package org.md2k.studymperf;

import android.os.Bundle;


public class ActivityMain extends AbstractActivityMenu {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startDataCollection();
        if(getIntent().getBooleanExtra("background", false)==true)
            finish();
    }
}
