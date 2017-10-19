package org.md2k.studymperf.privacy_control;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;

import org.md2k.mcerebrum.core.access.appinfo.AppInfo;
import org.md2k.studymperf.R;
import org.md2k.studymperf.ServiceStudy;

import java.util.Locale;

import mehdi.sakout.fancybuttons.FancyButton;

import static android.content.Context.NOTIFICATION_SERVICE;


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
public class UserViewPrivacyControl {
    private static final String TAG = UserViewPrivacyControl.class.getSimpleName();
    private Handler handler;
    private PrivacyControlManager privacyControlManager;
    private View view;
    private Activity activity;

    public UserViewPrivacyControl(Activity activity, View view) {
        handler = new Handler();
        this.activity = activity;
        this.view = view;
        privacyControlManager = new PrivacyControlManager();
    }

    private Runnable runnablePrivacy = new Runnable() {
        @Override
        public void run() {
            privacyControlManager.set();
            long remainingTime = privacyControlManager.getRemainingTime();
            if (remainingTime > 0) {
                remainingTime /= 1000;
                int sec = (int) (remainingTime % 60);
                remainingTime/=60;
                int min = (int) (remainingTime % 60);
                remainingTime/=60;
                int hour = (int) (remainingTime);
                String show="Resumed after " + String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, min, sec);
                AwesomeTextView t = ((AwesomeTextView) view.findViewById(R.id.textview_privacy_status));
                t.setBootstrapText(new BootstrapText.Builder(activity).addText(show).build());
                t.setBootstrapBrand(DefaultBootstrapBrand.DANGER);
//                t.setText(show);
//                t.setTextColor(ContextCompat.getColor(activity, R.color.headerOrange));
                FancyButton button = (FancyButton) view.findViewById(R.id.btn_pause_resume_data_collection);
                button.setFontIconSize(16);
                button.setIconResource("\uf00d");
                button.setIconColor(ContextCompat.getColor(activity, R.color.headerOrange));

                NotificationManager notificationManager =
                        (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
                boolean start = AppInfo.isServiceRunning(activity, ServiceStudy.class.getName());
                if(start)
                notificationManager.notify(ServiceStudy.NOTIFY_ID, ServiceStudy.getCompatNotification(activity,"Data Collection - PAUSED ("+show+")"));
                else notificationManager.notify(ServiceStudy.NOTIFY_ID, ServiceStudy.getCompatNotification(activity,"Data Collection - OFF"));

/*
                button.setIconResource(new IconicsDrawable(activity)
                        .icon(FontAwesome.Icon.faw_stop)
                        .color(ContextCompat.getColor(activity, R.color.headerOrange))
                        .sizeDp(24));
*/
  //              button.setBackgroundColor(ContextCompat.getColor(activity, R.color.headerOrange));
                button.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorBackground));

/*
                activity.findViewById(R.id.button_privacy).setBackground(ContextCompat.getDrawable(activity, R.drawable.button_red));
                ((Button) activity.findViewById(R.id.button_privacy)).setTextColor(Color.WHITE);
                ((Button) activity.findViewById(R.id.button_privacy)).setText("Turn Off");
*/
                handler.postDelayed(this, 1000);
            } else {
                FancyButton button = (FancyButton) view.findViewById(R.id.btn_pause_resume_data_collection);
                button.setFontIconSize(16);
                button.setIconColor(Color.WHITE);
                button.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorBackground));
                button.setIconResource("\uf04c");
                String show="Data Collection Active";
                NotificationManager notificationManager =
                        (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
                boolean start = AppInfo.isServiceRunning(activity, ServiceStudy.class.getName());
                if(start)
                    notificationManager.notify(ServiceStudy.NOTIFY_ID, ServiceStudy.getCompatNotification(activity,"Data Collection - ON"));
                else notificationManager.notify(ServiceStudy.NOTIFY_ID, ServiceStudy.getCompatNotification(activity,"Data Collection - OFF (click to start)"));

/*
                TextView t = ((TextView) view.findViewById(R.id.textview_privacy_status));
                t.setText(show);
                t.setTextColor(Color.GREEN);
*/
                AwesomeTextView t = ((AwesomeTextView) view.findViewById(R.id.textview_privacy_status));
                t.setBootstrapText(new BootstrapText.Builder(activity).addText(show).build());
                t.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);

/*
                String text = "Not Active";
                ((TextView) activity.findViewById(R.id.text_view_privacy)).setText(text);
                ((TextView) activity.findViewById(R.id.text_view_privacy)).setTextColor(ContextCompat.getColor(activity, R.color.teal_700));
                activity.findViewById(R.id.button_privacy).setBackground(ContextCompat.getDrawable(activity, R.drawable.button_teal));
                ((Button) activity.findViewById(R.id.button_privacy)).setText("Turn On");
                ((Button) activity.findViewById(R.id.button_privacy)).setTextColor(Color.BLACK);
*/
            }
        }
    };

    private void prepareButton() {
        FancyButton button;
        button = (FancyButton) view.findViewById(R.id.btn_pause_resume_data_collection);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName("org.md2k.datakit", "org.md2k.datakit.ActivityPrivacy");
                activity.startActivity(intent);
            }
        });
    }

    public void set() {
        handler.removeCallbacks(runnablePrivacy);
        prepareButton();
        handler.post(runnablePrivacy);
    }

    public void clear() {
        handler.removeCallbacks(runnablePrivacy);
    }
}
