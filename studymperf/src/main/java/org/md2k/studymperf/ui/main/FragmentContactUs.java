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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.view.BootstrapTextView;

import org.md2k.studymperf.ProductivityActivity;
import org.md2k.studymperf.R;
import org.md2k.studymperf.data_collection.UserViewDataCollection;
import org.md2k.studymperf.privacy_control.UserViewPrivacyControl;
import org.md2k.studymperf.step_count.UserViewStepCount;

import mehdi.sakout.fancybuttons.FancyButton;

public class FragmentContactUs extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_contact_us, parent, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
     /*   FancyButton productivity=(FancyButton) view.findViewById(R.id.btn_productivity);
        productivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),ProductivityActivity.class);
                startActivity(intent);
            }
        });
*/
        BootstrapTextView contact_name= (BootstrapTextView) view.findViewById(R.id.textview_contact_name);
        contact_name.setBootstrapText(new BootstrapText.Builder(getActivity()).addText("David Rudd").build());

        BootstrapTextView contact_phone= (BootstrapTextView) view.findViewById(R.id.textview_contact_phone);
        contact_phone.setBootstrapText(new BootstrapText.Builder(getActivity()).addText("901 678 1111").build());

        BootstrapTextView contact_email= (BootstrapTextView) view.findViewById(R.id.textview_contact_email);
        contact_email.setBootstrapText(new BootstrapText.Builder(getActivity()).addText("abcd@memphis.edu").build());

        BootstrapTextView contact_address= (BootstrapTextView) view.findViewById(R.id.textview_contact_address);
        contact_address.setBootstrapText(new BootstrapText.Builder(getActivity()).addText("110 BRister Ave, Memphis, TN").build());


    }
}
