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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.view.BootstrapTextView;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataTypeString;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.mcerebrum.commons.dialog.Dialog;
import org.md2k.mcerebrum.commons.dialog.DialogCallback;
import org.md2k.studymperf.ActivityMain;
import org.md2k.studymperf.R;
import org.md2k.studymperf.menu.MyMenu;

import mehdi.sakout.fancybuttons.FancyButton;

public class FragmentWorkAnnonation extends Fragment {
    BootstrapButton start;
    BootstrapButton stop;
    FancyButton work_close;
    BootstrapButton work_type;
    DataSourceClient dataSourceClient;
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_workplace_annotation, parent, false);

    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

/*
        try {
            dataSourceClient = DataKitAPI.getInstance(getActivity()).register(new DataSourceBuilder().setType(DataSourceType.WORK_ANNOTATION));
                DataKitAPI.getInstance(getActivity()).insert(dataSourceClient, new DataTypeString(DateTime.getDateTime(), "OPEN"));
        } catch (DataKitException e) {

        }
*/
//        Spinner spinner = (Spinner) view.findViewById(R.id.spinner_list);
// Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
  //              R.array.work_list, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
      //  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
    //    spinner.setAdapter(adapter);
/*        final BootstrapTextView textview_work= (BootstrapTextView) view.findViewById(R.id.textview_workplace_type);
        start=(BootstrapButton)view.findViewById(R.id.btn_work_start);
        work_type=(BootstrapButton) view.findViewById(R.id.btn_work_type);
        stop=(BootstrapButton)view.findViewById(R.id.btn_work_stop);
        enableButtons(false, false, true);

        start.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                 Toast.makeText(getActivity(), "Start", Toast.LENGTH_SHORT).show();
                try {
                    DataKitAPI.getInstance(getActivity()).insert(dataSourceClient, new DataTypeString(DateTime.getDateTime(), "START"));
                } catch (DataKitException e) {
                    e.printStackTrace();
                }
//                start.setBootstrapBrand(DefaultBootstrapBrand.SECONDARY);
                enableButtons(false, true, false);
            }
        });


        stop.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Stop", Toast.LENGTH_SHORT).show();
                try {
                    DataKitAPI.getInstance(getActivity()).insert(dataSourceClient, new DataTypeString(DateTime.getDateTime(), "STOP"));
                } catch (DataKitException e) {
                    e.printStackTrace();
                }
                enableButtons(true, false, true);

            }
        });

        work_close = (FancyButton) view.findViewById(R.id.btn_work_close);
        work_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    DataKitAPI.getInstance(getActivity()).insert(dataSourceClient, new DataTypeString(DateTime.getDateTime(), "CLOSE"));
                } catch (DataKitException e) {
                    e.printStackTrace();
                }

                ((ActivityMain) getActivity()).responseCallBack.onResponse(null, MyMenu.MENU_HOME);
            }
        });


        work_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.singleChoice(getActivity(), "Select your work Type", new String[]{"Apple", "Basket","Carrot","Dog","Elephant", "Other"}, 0, new DialogCallback() {
                    @Override
                    public void onSelected(String value) {
                        if(value.equals("Other")){
                            Dialog.editbox(getActivity(), "Set other work type", "Type your other work type.", new DialogCallback() {
                                @Override
                                public void onSelected(String value) {
                                    textview_work.setBootstrapText(new BootstrapText.Builder(getActivity()).addText("" +value).build());
                                    try {
                                        DataKitAPI.getInstance(getActivity()).insert(dataSourceClient, new DataTypeString(DateTime.getDateTime(), "WORK_TYPE="+value));
                                        enableButtons(true, false, true);
                                    } catch (DataKitException e) {
                                        e.printStackTrace();
                                    }

                                    //   Toast.makeText(getActivity(), "value=" + value, Toast.LENGTH_SHORT).show();

                                }
                            }).show();

                        }else{
                            textview_work.setBootstrapText(new BootstrapText.Builder(getActivity()).addText("" +value).build());
                            enableButtons(true, false, true);
                            try {
                                DataKitAPI.getInstance(getActivity()).insert(dataSourceClient, new DataTypeString(DateTime.getDateTime(), "WORK_TYPE="+value));
                            } catch (DataKitException e) {
                                e.printStackTrace();
                            }
                        }
                      //  Toast.makeText(FragmentWorkAnnonation.this,"value="+value,Toast.LENGTH_SHORT).show();
                    }
                }).show();

            }
        });

*/
        /*
        BootstrapTextView contact_name= (BootstrapTextView) view.findViewById(R.id.textview_contact_name);
        contact_name.setBootstrapText(new BootstrapText.Builder(getActivity()).addText("David Rudd").build());

        BootstrapTextView contact_phone= (BootstrapTextView) view.findViewById(R.id.textview_contact_phone);
        contact_phone.setBootstrapText(new BootstrapText.Builder(getActivity()).addText("901 678 1111").build());

        BootstrapTextView contact_email= (BootstrapTextView) view.findViewById(R.id.textview_contact_email);
        contact_email.setBootstrapText(new BootstrapText.Builder(getActivity()).addText("abcd@memphis.edu").build());

        BootstrapTextView contact_address= (BootstrapTextView) view.findViewById(R.id.textview_contact_address);
        contact_address.setBootstrapText(new BootstrapText.Builder(getActivity()).addText("110 BRister Ave, Memphis, TN").build());
*/



    }
    void enableButtons(boolean st, boolean sp, boolean wt){
        start.setEnabled(st);start.setShowOutline(!st);
        stop.setEnabled(sp);stop.setShowOutline(!sp);
        work_type.setEnabled(wt);work_type.setShowOutline(!wt);

    }
}
