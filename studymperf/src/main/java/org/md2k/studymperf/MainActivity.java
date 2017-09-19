package org.md2k.studymperf;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.content.Intent;
import android.view.View;

import org.md2k.studymperf.step_count.ActivityStepCountPieChart;

import mehdi.sakout.fancybuttons.FancyButton;

public class MainActivity extends AppCompatActivity {

    FancyButton fitness;
    FancyButton productivity;
    FancyButton report;
    FancyButton left_wrist;
    FancyButton right_wrist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_screen_style);



        fitness= (FancyButton) findViewById(R.id.btn_fitness);
        fitness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,ActivityStepCountPieChart.class);
                startActivity(intent);
            }
        });

        productivity=(FancyButton) findViewById(R.id.btn_productivity);
        productivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,ProductivityActivity.class);
                startActivity(intent);
            }
        });



        report= (FancyButton) findViewById(R.id.btn_view_report);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,StackedBarActivity.class);
                startActivity(intent);
            }
        });
        left_wrist= (FancyButton) findViewById(R.id.btn_dq_left_wrist);
        left_wrist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,ActivityLeftWrist.class);
                startActivity(intent);
            }
        });

        right_wrist= (FancyButton) findViewById(R.id.btn_dq_right_wrist);
        right_wrist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,RightWristActivity.class);
                startActivity(intent);
            }
        });
    }
}
