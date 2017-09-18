package org.md2k.studymperf;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.md2k.datakitapi.source.datasource.DataSource;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceType;
import org.md2k.datakitapi.source.platform.Platform;
import org.md2k.datakitapi.source.platform.PlatformBuilder;
import org.md2k.studymperf.ui.main.ActivityYouTube;
import org.md2k.studymperf.ui.main.FragmentHome;

import mehdi.sakout.fancybuttons.FancyButton;

public class FragmentLeftWrist extends Fragment {
    public static FragmentLeftWrist newInstance(String id, String title) {
        FragmentLeftWrist myFragment = new FragmentLeftWrist();

        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("title",title);
        myFragment.setArguments(args);

        return myFragment;
    }

    FancyButton wrist_graph;
    FancyButton wrist_video;
    FancyButton wrist_close;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.activity_left_wrist, parent, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        final String id = getArguments().getString("id");
        String title = getArguments().getString("title");
        ((TextView) view.findViewById(R.id.textview_wrist_title)).setText(title);

        wrist_graph = (FancyButton) view.findViewById(R.id.btn_left_wrist_graph);
        wrist_graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle b = new Bundle();
                Platform p=new PlatformBuilder().setId(id).build();
                DataSource d = new DataSourceBuilder().setType(DataSourceType.ACCELEROMETER).setPlatform(p).build();
                b.putParcelable(DataSource.class.getSimpleName(), d);
                intent.putExtras(b);
                intent.setComponent(new ComponentName("org.md2k.motionsense", "org.md2k.motionsense.plot.ActivityPlot"));
                startActivity(intent);
//                Intent intent=new Intent(FragmentLeftWrist.this,PieChartActivity.class);
//                startActivity(intent);
            }
        });

        wrist_video = (FancyButton) view.findViewById(R.id.btn_left_wrist_video);
        wrist_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ActivityYouTube.class);
                startActivity(intent);
            }
        });

        wrist_close = (FancyButton) view.findViewById(R.id.btn_left_wrist_close);
        wrist_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new FragmentHome(), "mPerf Study")
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
}
