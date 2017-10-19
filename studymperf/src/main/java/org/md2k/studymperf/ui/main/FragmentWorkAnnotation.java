package org.md2k.studymperf.ui.main;
/*
 * Copyright (c) 2016, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataTypeJSONObject;
import org.md2k.datakitapi.datatype.DataTypeString;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.mcerebrum.core.data_format.WorkAnnotation;
import org.md2k.studymperf.ActivityMain;
import org.md2k.studymperf.R;
import org.md2k.studymperf.menu.MyMenu;

import java.util.ArrayList;
import java.util.Locale;

import mehdi.sakout.fancybuttons.FancyButton;

public class FragmentWorkAnnotation extends Fragment {
    DataSourceClient dataSourceClient;
    private AwesomeTextView selected_activity;
    private BootstrapButton select_activity;
    private EditText other_activity;

    private AwesomeTextView selected_context;
    private BootstrapButton select_context;
    private EditText other_context;

    private BootstrapButton buttonStop;
    private BootstrapButton buttonStart;
    private BootstrapButton buttonCancel;

    private FancyButton buttonClose;
    private AwesomeTextView textViewMessage;

    WorkAnnotation workAnnotation;
    Handler handler;
    private boolean started;
    CharSequence[] workActivities = new String[]{
            "Simple interactions with people (speaking, presenting, serving, persuading, helping, entertaining)",
            "Complex interactions with people (mentoring, negotiating, instructing, supervising)",
            "Simple work with data (computing, copying, comparing data)",
            "Complex work with data (synthesizing, coordinating, analyzing, compiling data)",
            "Simple activities with equipment (controlling, maintaining, driving)",
            "Complex activities with equipment (setting up, precision working, operating complex machinery)",
    };
    CharSequence[] workContext = new String[]{
            "Alone",
            "With one other person",
            "In small group",
            "In large group",
            "In front of crowd",
            "In person interaction",
            "Virtual interaction"
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workplace_annotation, parent, false);
    }

    void selectWorkActivities() {
        new MaterialDialog.Builder(getActivity())
                .title("Select Work activities")
                .items(workActivities)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        String[] selected=new String[which.length];
                        for(int i=0;i<which.length;i++)
                            selected[i]=workActivities[which[i]].toString();
                        workAnnotation.work_activity=selected;
                        selected_activity.setText("Selected Activity: "+String.valueOf(workAnnotation.work_activity.length));
                        return false;
                    }
                })
                .positiveText("Select")
                .negativeText("Cancel")
                .show();
    }
    void selectWorkContext() {
        new MaterialDialog.Builder(getActivity())
                .title("Select Work Context")
                .items(workContext)
                .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                        String[] selected=new String[which.length];
                        for(int i=0;i<which.length;i++)
                            selected[i]=workContext[which[i]].toString();
                        workAnnotation.work_context=selected;
                        selected_context.setText("Selected Context: "+String.valueOf(workAnnotation.work_context.length));
                        return false;
                    }
                })
                .positiveText("Select")
                .negativeText("Cancel")
                .show();
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        handler = new Handler();
        select_activity= (BootstrapButton) view.findViewById(R.id.select_activity);
        selected_activity= (AwesomeTextView) view.findViewById(R.id.selected_activity);
        other_activity= (EditText) view.findViewById(R.id.other_activity);
        select_context= (BootstrapButton) view.findViewById(R.id.select_context);
        selected_context= (AwesomeTextView) view.findViewById(R.id.selected_context);
        other_context= (EditText) view.findViewById(R.id.other_context);
        textViewMessage= (AwesomeTextView) view.findViewById(R.id.textview_message);
        buttonStart = (BootstrapButton) view.findViewById(R.id.btn_work_start);
        buttonClose = (FancyButton) view.findViewById(R.id.btn_work_close);
        buttonStop = (BootstrapButton) view.findViewById(R.id.btn_work_finish);
        buttonCancel = (BootstrapButton) view.findViewById(R.id.btn_work_cancel);

        readSharedPreference();
        if(workAnnotation.operation!=null && workAnnotation.operation.equals("START"))
            started=true;
        else started=false;
        if(started==true){
            other_activity.setText(workAnnotation.work_activity_other);
            other_context.setText(workAnnotation.work_context_other);
            if(workAnnotation.work_activity==null)
                selected_activity.setText("Selected Activity: 0");
                else
            selected_activity.setText("Selected Activity: "+String.valueOf(workAnnotation.work_activity.length));
            if(workAnnotation.work_context==null)
                selected_context.setText("Selected Context: 0");
            else
            selected_context.setText("Selected Context: "+String.valueOf(workAnnotation.work_context.length));
            enableButtons(false, true, false);
            handler.post(runnable);
        }else{
            other_activity.setText("");
            other_context.setText("");
            selected_activity.setText("Selected Activity: 0");
            selected_context.setText("Selected Context: 0");
            enableButtons(true, false, true);
        }
        prepareClose(view);
        prepareStart(view);
        prepareFinish(view);
        prepareMessage(view);
        prepareCancel(view);
        prepareSelectActivity();
        prepareSelectContext();
    }
    void prepareSelectActivity(){
        select_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectWorkActivities();
            }
        });
    }
    void prepareSelectContext(){
        select_context.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectWorkContext();
            }
        });
    }
    void prepareClose(View view) {
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ActivityMain) getActivity()).responseCallBack.onResponse(null, MyMenu.MENU_HOME);
            }
        });
    }
    void prepareStart(View view) {
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workAnnotation.work_activity_other=other_activity.getText().toString();
                workAnnotation.work_context_other=other_context.getText().toString();
                workAnnotation.operation="START";
                workAnnotation.timestamp=DateTime.getDateTime();
                insertData();
                writeSharedPreference();
                handler.removeCallbacks(runnable);
                handler.post(runnable);
                enableButtons(false, true, false);
            }
        });
    }

    void prepareFinish(View view) {
        buttonStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                workAnnotation.operation="FINISH";
                workAnnotation.timestamp=DateTime.getDateTime();
                insertData();
                writeSharedPreference();
                insertData();
                handler.removeCallbacks(runnable);
                writeSharedPreference();
                enableButtons(true, false, true);
                showMessage("FINISHED");
            }
        });
    }

    private void prepareCancel(View view) {
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                workAnnotation.operation="CANCEL";
                workAnnotation.timestamp=DateTime.getDateTime();
                insertData();
                writeSharedPreference();
                handler.removeCallbacks(runnable);
                enableButtons(true, false, true);
                showMessage("CANCELLED");
            }
        });
    }


    void showMessage(String msg) {
        if (msg == null || msg.length() == 0) textViewMessage.setText("");
        else textViewMessage.setText("Work Annotation  " + msg);
    }

    void prepareMessage(View view) {
        textViewMessage = (AwesomeTextView) view.findViewById(R.id.textview_message);
        showMessage(null);
        textViewMessage.setText("");
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (started) {
                long duration = (DateTime.getDateTime() - workAnnotation.timestamp) / 1000;
                long sec = duration % 60;
                duration /= 60;
                long min = duration % 60;
                duration /= 60;
                long hr = duration;
                showMessage("STARTED: " + String.format(Locale.getDefault(), "%02d:%02d:%02d", hr, min, sec));
                handler.postDelayed(this, 1000);
            } else {
                showMessage(null);
            }
        }
    };



    void insertData() {
        try {
            Gson gson=new Gson();
            JsonObject sample = new JsonParser().parse(gson.toJson(workAnnotation)).getAsJsonObject();
            DataTypeJSONObject dataTypeJSONObject = new DataTypeJSONObject(DateTime.getDateTime(), sample);
            dataSourceClient = DataKitAPI.getInstance(getActivity()).register(new DataSourceBuilder().setType(DataSourceType.WORK_ANNOTATION));

            DataKitAPI.getInstance(getActivity()).insert(dataSourceClient, dataTypeJSONObject);
//            Toast.makeText(getActivity(), data, Toast.LENGTH_SHORT).show();
        } catch (DataKitException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        Log.d("abc", "ondestroy View");
        handler.removeCallbacks(runnable);
        super.onDestroyView();
    }

    void enableButtons(boolean st, boolean sp, boolean wt) {
        buttonStart.setEnabled(st);
        buttonStart.setShowOutline(!st);
        buttonStop.setEnabled(sp);
        buttonStop.setShowOutline(!sp);
        buttonCancel.setEnabled(sp);
        buttonCancel.setShowOutline(!sp);
        other_activity.setEnabled(wt);
        other_context.setEnabled(wt);
        select_activity.setEnabled(wt);
        select_context.setEnabled(wt);
    }


    void readSharedPreference() {
        workAnnotation=new WorkAnnotation();
        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                "work", Context.MODE_PRIVATE);
        workAnnotation.work_activity=getArray(sharedPref.getString("work_activity", null));
        workAnnotation.work_activity_other=sharedPref.getString("work_activity_other",null);
        workAnnotation.work_context=getArray(sharedPref.getString("work_context",null));
        workAnnotation.work_context_other=sharedPref.getString("work_context_other",null);
        workAnnotation.timestamp=sharedPref.getLong("timestamp",0);
        workAnnotation.operation=sharedPref.getString("operation",null);
    }
    String[] getArray(String string){
        if(string==null) return null;
        return string.split("#");
    }

    String setArray(String[] string){
        String res=null;
        if(string==null) return null;
        if(string.length==0) return null;
        for(int i=0;i<string.length;i++){
            if(i==0) res=string[i];
            else res+="#"+string[i];
        }
        return res;
    }

    void writeSharedPreference() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                "work", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("work_activity",setArray(workAnnotation.work_activity));
        editor.putString("work_activity_other",workAnnotation.work_activity_other);
        editor.putString("work_context",setArray(workAnnotation.work_context));
        editor.putString("work_context_other",workAnnotation.work_context_other);
        editor.putLong("timestamp", workAnnotation.timestamp);
        editor.putString("operation",workAnnotation.operation);
        editor.apply();
    }
}
