package org.md2k.studymperf.ui.main;

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

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.ohoussein.playpause.PlayPauseView;

import org.md2k.datakitapi.source.platform.PlatformId;
import org.md2k.mcerebrum.core.data_format.DATA_QUALITY;
import org.md2k.studymperf.ActivityMain;
import org.md2k.studymperf.FragmentLeftWrist;
import org.md2k.studymperf.PieChartActivity;
import org.md2k.studymperf.ProductivityActivity;
import org.md2k.studymperf.R;
import org.md2k.studymperf.RightWristActivity;
import org.md2k.studymperf.StackedBarActivity;
import org.md2k.studymperf.data_quality.ResultCallback;
import org.md2k.studymperf.data_quality.UserViewDataQuality;

import mehdi.sakout.fancybuttons.FancyButton;

public class FragmentHome extends Fragment {

    FancyButton fitness;
    FancyButton productivity;
    FancyButton report;
    FancyButton left_wrist;
    FancyButton right_wrist;
    PlayPauseView pause_play;
    UserViewDataQuality userViewDataQuality;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_main, parent, false);
    }
    @Override

    public void onViewCreated(final View view, Bundle savedInstanceState) {
        setDataQuality(view);

        fitness= (FancyButton) view.findViewById(R.id.btn_fitness);
        fitness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),PieChartActivity.class);
                startActivity(intent);
            }
        });

        productivity=(FancyButton) view.findViewById(R.id.btn_productivity);
        productivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),ProductivityActivity.class);
                startActivity(intent);
            }
        });

        report= (FancyButton) view.findViewById(R.id.btn_view_report);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),StackedBarActivity.class);
                startActivity(intent);
            }
        });
        left_wrist= (FancyButton) view.findViewById(R.id.btn_dq_left_wrist);
        left_wrist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, FragmentLeftWrist.newInstance(PlatformId.LEFT_WRIST, "Left Wrist"),"findThisFragment")
                        .addToBackStack(null)
                        .commit();
//                Intent intent=new Intent(getActivity(),FragmentLeftWrist.class);
//                startActivity(intent);
            }
        });

        right_wrist= (FancyButton) view.findViewById(R.id.btn_dq_right_wrist);
        right_wrist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, FragmentLeftWrist.newInstance(PlatformId.RIGHT_WRIST, "Right Wrist"),"findThisFragment")
                        .addToBackStack(null)
                        .commit();

/*
                Intent intent=new Intent(getActivity(),RightWristActivity.class);
                startActivity(intent);
*/
            }
        });
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
            case DATA_QUALITY.GOOD: return "Good";
            case DATA_QUALITY.BAND_OFF: return "No Data";
            default: return "Poor";
        }
    }
    Drawable getDataQualityImage(int value){
        switch(value){
            case DATA_QUALITY.GOOD: return new IconicsDrawable(getContext()).icon(FontAwesome.Icon.faw_check_circle).sizeDp(24).color(Color.GREEN);
            case DATA_QUALITY.BAND_OFF: return new IconicsDrawable(getContext()).icon(FontAwesome.Icon.faw_times_circle).sizeDp(24).color(Color.RED);
            default: return new IconicsDrawable(getContext()).icon(FontAwesome.Icon.faw_exclamation_triangle).sizeDp(24).color(Color.YELLOW);
        }
    }
    @Override
    public void onDestroyView(){
        userViewDataQuality.clear();
        super.onDestroyView();
    }

}
