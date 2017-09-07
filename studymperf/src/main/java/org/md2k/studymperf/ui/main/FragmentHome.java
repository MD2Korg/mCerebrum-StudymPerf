package org.md2k.studymperf.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.md2k.studymperf.LeftWristActivity;
import org.md2k.studymperf.PieChartActivity;
import org.md2k.studymperf.ProductivityActivity;
import org.md2k.studymperf.R;
import org.md2k.studymperf.RightWristActivity;
import org.md2k.studymperf.StackedBarActivity;

import es.dmoral.toasty.Toasty;
import mehdi.sakout.fancybuttons.FancyButton;

public class FragmentHome extends Fragment {

    FancyButton fitness;
    FancyButton productivity;
    FancyButton report;
    FancyButton left_wrist;
    FancyButton right_wrist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_main, parent, false);
    }
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
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
                Intent intent=new Intent(getActivity(),LeftWristActivity.class);
                startActivity(intent);
            }
        });

        right_wrist= (FancyButton) view.findViewById(R.id.btn_dq_right_wrist);
        right_wrist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),RightWristActivity.class);
                startActivity(intent);
            }
        });
    }
}
