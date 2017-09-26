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
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.view.BootstrapTextView;

import org.md2k.mcerebrum.commons.dialog.Dialog;
import org.md2k.mcerebrum.commons.dialog.DialogCallback;
import org.md2k.studymperf.R;

import mehdi.sakout.fancybuttons.FancyButton;

public class FragmentWorkAnnonation extends Fragment {
    BootstrapButton start;
    BootstrapButton stop;
    FancyButton work_close;
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_workplace_annotation, parent, false);

    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        final BootstrapTextView textview_work= (BootstrapTextView) view.findViewById(R.id.textview_workplace_type);
        start=(BootstrapButton)view.findViewById(R.id.btn_work_start);
        start.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                 Toast.makeText(getActivity(), "Start", Toast.LENGTH_SHORT).show();

//                start.setBootstrapBrand(DefaultBootstrapBrand.SECONDARY);
                 start.setShowOutline(true);
                 start.setEnabled(false);

                stop.setShowOutline(false);
                stop.setEnabled(true);
            }
        });


        stop=(BootstrapButton)view.findViewById(R.id.btn_work_stop);
        stop.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Stop", Toast.LENGTH_SHORT).show();
                stop.setShowOutline(true);
                stop.setEnabled(false);

                start.setShowOutline(false);
                start.setEnabled(true);

            }
        });

        work_close = (FancyButton) view.findViewById(R.id.btn_work_close);
        work_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });


        BootstrapButton work_type;
        work_type=(BootstrapButton) view.findViewById(R.id.btn_work_type);
        work_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.singleChoice(getActivity(), "Select your work Type", new String[]{"Apple", "Basket","Carrot","Dog","Elephant", "Other"}, 0, new DialogCallback() {
                    @Override
                    public void onSelected(String value) {
                        textview_work.setBootstrapText(new BootstrapText.Builder(getActivity()).addText("" +value).build());
                        if(value.equals("Other")){
                            Dialog.editbox(getActivity(), "Set other work type", "Type your other work type.", new DialogCallback() {
                                @Override
                                public void onSelected(String value) {
                                    textview_work.setBootstrapText(new BootstrapText.Builder(getActivity()).addText("" +value).build());
                                    //   Toast.makeText(getActivity(), "value=" + value, Toast.LENGTH_SHORT).show();

                                }
                            }).show();

                        }
                      //  Toast.makeText(FragmentWorkAnnonation.this,"value="+value,Toast.LENGTH_SHORT).show();
                    }
                }).show();

            }
        });

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
}
