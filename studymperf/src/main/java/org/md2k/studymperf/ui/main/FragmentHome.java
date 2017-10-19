package org.md2k.studymperf.ui.main;

import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.attributes.BootstrapBrand;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import org.md2k.datakitapi.source.platform.PlatformId;
import org.md2k.mcerebrum.core.access.appinfo.AppInfo;
import org.md2k.mcerebrum.core.data_format.DATA_QUALITY;
import org.md2k.studymperf.ActivityLeftWrist;
import org.md2k.studymperf.ActivityMain;
import org.md2k.studymperf.R;
import org.md2k.studymperf.ServiceStudy;
import org.md2k.studymperf.data_collection.UserViewDataCollection;
import org.md2k.studymperf.data_quality.ResultCallback;
import org.md2k.studymperf.data_quality.UserViewDataQuality;
import org.md2k.studymperf.location.ProductivityActivity;
import org.md2k.studymperf.privacy_control.UserViewPrivacyControl;
import org.md2k.studymperf.step_count.UserViewStepCount;
import org.md2k.mcerebrum.system.update.Update;

import mehdi.sakout.fancybuttons.FancyButton;

import static android.content.Context.NOTIFICATION_SERVICE;

public class FragmentHome extends Fragment {

    FancyButton productivity;
    FancyButton data_collection;
//    FancyButton pause_resume_data_collection;
    FancyButton left_wrist;
    FancyButton right_wrist;
    UserViewDataQuality userViewDataQuality;
    UserViewPrivacyControl userViewPrivacyControl;
    UserViewStepCount userViewStepCount;
    UserViewDataCollection userViewDataCollection;
    AwesomeTextView tv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_main, parent, false);
    }
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        setDataQuality(view);
        userViewPrivacyControl = new UserViewPrivacyControl(getActivity(), view);
        userViewStepCount=new UserViewStepCount(getActivity(), view);
        userViewDataCollection=new UserViewDataCollection(getActivity(), view);
        tv = (AwesomeTextView) view.findViewById(R.id.textview_status);


        productivity=(FancyButton) view.findViewById(R.id.btn_productivity);
        productivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),ProductivityActivity.class);
                startActivity(intent);
            }
        });


/*
        data_collection=(FancyButton) view.findViewById(R.id.btn_data_collection);
        data_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),ActivityPieChartDataCollection.class);
                ...
                startActivity(intent);
            }
        });

*/

        left_wrist= (FancyButton) view.findViewById(R.id.btn_dq_left_wrist);
        left_wrist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, FragmentLeftWrist.newInstance(PlatformId.LEFT_WRIST, "Left Wrist"), "findThisFragment")
                        .addToBackStack(null)
                        .commit();
*/
                Intent intent=new Intent(getActivity(),ActivityLeftWrist.class);
                intent.putExtra("id",PlatformId.LEFT_WRIST);
                intent.putExtra("title","Left Wrist");
                startActivity(intent);
            }
        });

        right_wrist= (FancyButton) view.findViewById(R.id.btn_dq_right_wrist);
        right_wrist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),ActivityLeftWrist.class);
                intent.putExtra("id",PlatformId.RIGHT_WRIST);
                intent.putExtra("title","Right Wrist");
                startActivity(intent);

/*
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, FragmentLeftWrist.newInstance(PlatformId.RIGHT_WRIST, "Right Wrist"),"findThisFragment")
                        .addToBackStack(null)
                        .commit();
*/

/*
                Intent intent=new Intent(getActivity(),RightWristActivity.class);
                startActivity(intent);
*/
            }
        });
//        ((TextView) view.findViewById(R.id.textview_userid)).setText(getUserId());
    }
    void setDataQuality(final View view){
        try {
            userViewDataQuality = new UserViewDataQuality(((ActivityMain)getActivity()).dataQualityManager);
            userViewDataQuality.set(new ResultCallback() {
                @Override
                public void onResult(int[] result) {
                    ((ImageView) view.findViewById(R.id.imageview_left_wrist)).setImageDrawable(getDataQualityImage(result[0]));
                    ((ImageView) view.findViewById(R.id.imageview_right_wrist)).setImageDrawable(getDataQualityImage(result[1]));
                    ((TextView) view.findViewById(R.id.textview_left_wrist)).setText(getDataQualityText(result[0]));
                    ((TextView) view.findViewById(R.id.textview_right_wrist)).setText(getDataQualityText(result[1]));
                }
            });
        }catch (Exception ignored){

        }
    }
    String getDataQualityText(int value){
        switch(value){
            case -1: return "";
            case DATA_QUALITY.GOOD: return "Good";
            case DATA_QUALITY.BAND_OFF: return "No Data";
            default: return "Poor";
        }
    }
    Drawable getDataQualityImage(int value){
        switch(value){
            case -1: return new IconicsDrawable(getContext()).icon(FontAwesome.Icon.faw_refresh).sizeDp(24).color(Color.GRAY);
            case DATA_QUALITY.GOOD: return new IconicsDrawable(getContext()).icon(FontAwesome.Icon.faw_check_circle).sizeDp(24).color(Color.GREEN);
            case DATA_QUALITY.BAND_OFF: return new IconicsDrawable(getContext()).icon(FontAwesome.Icon.faw_times_circle).sizeDp(24).color(Color.RED);
            default: return new IconicsDrawable(getContext()).icon(FontAwesome.Icon.faw_exclamation_triangle).sizeDp(24).color(Color.YELLOW);
        }
    }
    @Override
    public void onResume(){
        userViewPrivacyControl.set();
        userViewStepCount.set();
        userViewDataCollection.set();
        boolean start = AppInfo.isServiceRunning(getActivity(), ServiceStudy.class.getName());

        if(!start) {
            updateStatus("Data collection off", DefaultBootstrapBrand.DANGER, false);
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(ServiceStudy.NOTIFY_ID, ServiceStudy.getCompatNotification(getActivity(),"Data Collection - OFF (click to start)"));

        }else{
            updateStatus(null, DefaultBootstrapBrand.SUCCESS, true);
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(ServiceStudy.NOTIFY_ID, ServiceStudy.getCompatNotification(getActivity(),"Data Collection - ON"));
        }

        super.onResume();
    }
    @Override
    public void onPause(){
        userViewPrivacyControl.clear();
        userViewStepCount.clear();
        userViewDataCollection.clear();
        super.onPause();
    }

    @Override
    public void onDestroyView(){
        userViewDataQuality.clear();
        super.onDestroyView();
    }
    void updateStatus(String msg, BootstrapBrand brand, boolean isSuccess){
        tv.setBootstrapBrand(brand);
        if(isSuccess) {
            int uNo=Update.hasUpdate(getActivity());
            if(uNo==0)
            tv.setBootstrapText(new BootstrapText.Builder(getActivity()).addText("Status: ").addFontAwesomeIcon("fa_check_circle").build());
            else {
                tv.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
                tv.setBootstrapText(new BootstrapText.Builder(getActivity()).addText("Status: ").addFontAwesomeIcon("fa_check_circle").addText(" (Update Available)").build());
            }
        }
        else
            tv.setBootstrapText(new BootstrapText.Builder(getActivity()).addText("Status: ").addFontAwesomeIcon("fa_times_circle").addText(" ("+msg+")").build());
    }

}
