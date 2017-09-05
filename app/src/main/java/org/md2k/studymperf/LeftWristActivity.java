package org.md2k.studymperf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import mehdi.sakout.fancybuttons.FancyButton;

public class LeftWristActivity extends AppCompatActivity {


    FancyButton wrist_graph;
    FancyButton wrist_video;
    FancyButton wrist_close;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_left_wrist);

        wrist_graph= (FancyButton) findViewById(R.id.btn_left_wrist_graph);
        wrist_graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LeftWristActivity.this,PieChartActivity.class);
                startActivity(intent);
            }
        });

        wrist_video= (FancyButton) findViewById(R.id.btn_left_wrist_video);
        wrist_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LeftWristActivity.this,PieChartActivity.class);
                startActivity(intent);
            }
        });

        wrist_close= (FancyButton) findViewById(R.id.btn_left_wrist_close);
        wrist_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                finish();
            }
        });


    }
}
