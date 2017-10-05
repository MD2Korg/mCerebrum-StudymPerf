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
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapButton;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataTypeString;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.studymperf.ActivityMain;
import org.md2k.studymperf.R;
import org.md2k.studymperf.menu.MyMenu;

import java.util.ArrayList;
import java.util.Locale;

import mehdi.sakout.fancybuttons.FancyButton;

public class FragmentWorkAnnotation extends Fragment {
    DataSourceClient dataSourceClient;
    private ArrayList<CheckBox> checkBoxes;
    private BootstrapButton buttonStop;
    private BootstrapButton buttonStart;
    private BootstrapButton buttonCancel;
    private TextView textViewNotes;
    private FancyButton buttonClose;
    private AwesomeTextView textViewMessage;
    String typeOfWork;
    Handler handler;
    private boolean started;
    private long startTime;
    private String notes;

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workplace_annotation, parent, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        handler = new Handler();
        readSharedPreference();
        prepareClose(view);
        prepareWorkType(view);
        prepareNotes(view);
        prepareStart(view);
        prepareFinish(view);
        prepareMessage(view);
        prepareCancel(view);
        if (started) {
            enableButtons(false, true, false);
            handler.post(runnable);
        }
        else enableButtons(true, false, true);
    }
    void showMessage(String msg){
        if(msg==null || msg.length()==0) textViewMessage.setText("");
        else textViewMessage.setText("Work Annotation  "+msg);
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
                long duration = (DateTime.getDateTime() - startTime) / 1000;
                long sec = duration % 60;
                duration /= 60;
                long min = duration % 60;
                duration /= 60;
                long hr = duration;
                showMessage("STARTED: "+String.format(Locale.getDefault(), "%02d:%02d:%02d", hr, min, sec));
                handler.postDelayed(this, 1000);
            } else {
                showMessage(null);
            }
        }
    };

    void prepareClose(View view) {
        buttonClose = (FancyButton) view.findViewById(R.id.btn_work_close);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ActivityMain) getActivity()).responseCallBack.onResponse(null, MyMenu.MENU_HOME);
            }
        });
    }

    void prepareWorkType(View view) {
        checkBoxes = new ArrayList<>();
        checkBoxes.add((CheckBox) view.findViewById(R.id.checkbox_computer));
        checkBoxes.add((CheckBox) view.findViewById(R.id.checkbox_patient));
        checkBoxes.add((CheckBox) view.findViewById(R.id.checkbox_presentation));
        checkBoxes.add((CheckBox) view.findViewById(R.id.checkbox_meeting));
        checkBoxes.add((CheckBox) view.findViewById(R.id.checkbox_phone_call));
        checkBoxes.add((CheckBox) view.findViewById(R.id.checkbox_email));
        checkBoxes.add((CheckBox) view.findViewById(R.id.checkbox_lab_work));
        checkBoxes.add((CheckBox) view.findViewById(R.id.checkbox_personnel));
        checkBoxes.add((CheckBox) view.findViewById(R.id.checkbox_research));
        checkBoxes.add((CheckBox) view.findViewById(R.id.checkbox_other));
        if (started) {
            String[] split=typeOfWork.split(",");
            for (String aSplit : split)
                for (int j = 0; j < checkBoxes.size(); j++)
                    if (aSplit.equals(checkBoxes.get(j).getText()))
                        checkBoxes.get(j).setChecked(true);
        }
    }

    void prepareNotes(View view) {
        textViewNotes = (TextView) view.findViewById(R.id.textView_notes);
        if (started) {
            textViewNotes.setText(notes);
        }
    }

    String getSelectedFromCheckBox() {
        String result = "";
        for (int i = 0; i < checkBoxes.size(); i++)
            if (checkBoxes.get(i).isChecked()) {
                if (result.length() > 0) result += ",";
                result += checkBoxes.get(i).getText();
            }
        return result;
    }

    void prepareStart(View view) {
        buttonStart = (BootstrapButton) view.findViewById(R.id.btn_work_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notes = textViewNotes.getText().toString();
                typeOfWork = getSelectedFromCheckBox();
                insertData("work=" + typeOfWork + ",note=" + notes + ",Started");
                writeSharedPreference(true, typeOfWork, DateTime.getDateTime(), notes);
                handler.removeCallbacks(runnable);
                handler.post(runnable);
                enableButtons(false, true, false);
            }
        });
    }

    void prepareFinish(View view) {
        buttonStop = (BootstrapButton) view.findViewById(R.id.btn_work_finish);
        buttonStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                insertData("work=" + typeOfWork + ",note=" + notes + ",Finished");
                handler.removeCallbacks(runnable);
                writeSharedPreference(false, null, -1, null);
                enableButtons(true, false, true);
                showMessage("FINISHED");
            }
        });
    }

    private void prepareCancel(View view) {
        buttonCancel = (BootstrapButton) view.findViewById(R.id.btn_work_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData("work=" + typeOfWork + ",note=" + notes + ",Cancelled");
                writeSharedPreference(false, null, -1, null);
                handler.removeCallbacks(runnable);
                enableButtons(true, false, true);
                showMessage("CANCELLED");
            }
        });
    }

    void insertData(String data) {
        try {
            DataKitAPI.getInstance(getActivity()).insert(dataSourceClient, new DataTypeString(DateTime.getDateTime(), data));
//            Toast.makeText(getActivity(), data, Toast.LENGTH_SHORT).show();
        } catch (DataKitException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroyView(){
        Log.d("abc","ondestroy View");
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
        for (int i = 0; i < checkBoxes.size(); i++)
            checkBoxes.get(i).setEnabled(wt);
        textViewNotes.setEnabled(wt);
    }


    void readSharedPreference() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                "work", Context.MODE_PRIVATE);
        started = sharedPref.getBoolean("start", false);
        typeOfWork = sharedPref.getString("work_type", null);
        notes = sharedPref.getString("notes", null);
        startTime = sharedPref.getLong("time", -1);
    }

    void writeSharedPreference(boolean started, String typeOfWork, long startTime, String notes) {
        this.started = started;
        this.typeOfWork = typeOfWork;
        this.startTime = startTime;
        this.notes = notes;
        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                "work", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("start", started);
        editor.putString("work_type", typeOfWork);
        editor.putLong("time", startTime);
        editor.putString("notes", notes);
        editor.apply();
    }
}
