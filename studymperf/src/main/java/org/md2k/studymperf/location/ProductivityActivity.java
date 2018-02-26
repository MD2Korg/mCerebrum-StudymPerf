package org.md2k.studymperf.location;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;

import org.md2k.studymperf.R;

import java.util.ArrayList;
import java.util.Locale;

import mehdi.sakout.fancybuttons.FancyButton;

public class ProductivityActivity extends DemoBaseProductivity implements SeekBar.OnSeekBarChangeListener,
        OnChartValueSelectedListener {

    private PieChart mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;
    private FancyButton buttonSetLocation;
    private FancyButton work_close;
    int[] MY_COLORS;
    @Override
    public void onResume(){
        readData();
        prepare();
        tvX = (TextView) findViewById(R.id.tvXMax);
        tvY = (TextView) findViewById(R.id.tvYMax);

        mSeekBarX = (SeekBar) findViewById(R.id.seekBar1);
        mSeekBarY = (SeekBar) findViewById(R.id.seekBar2);
        mSeekBarX.setProgress(4);
        mSeekBarY.setProgress(10);
        mSeekBarX.setVisibility(View.GONE);
        mSeekBarY.setVisibility(View.GONE);

        mChart = (PieChart) findViewById(R.id.chart1);
        mChart.setUsePercentValues(true);
        //   mChart.getDescription().setEnabled(false);
        //  mChart.setExtraOffsets(5, 10, 5, 5);

        //   mChart.setDragDecelerationFrictionCoef(0.95f);

        mChart.setCenterTextTypeface(mTfLight);
        mChart.setCenterText(generateCenterSpannableText());
        mChart.setCenterTextSize(14.0f);

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.TRANSPARENT);

        mChart.setTransparentCircleColor(Color.TRANSPARENT);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(70f);
        mChart.setTransparentCircleRadius(0f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(270);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(false);
        //   mChart.setHighlightPerTapEnabled(true);

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);

        setData(locationNames.length, 4);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        mChart.getDescription().setText("");
        mChart.getLegend().setEnabled(false);
        // mChart.spin(2000, 0, 360);

//piechart for data collection starts:

/*
        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
*/

        // entry label styling
        mChart.setDrawEntryLabels(false);
        mChart.setEntryLabelColor(Color.WHITE);
        mChart.setEntryLabelTypeface(mTfRegular);
        mChart.setEntryLabelTextSize(14f);

        super.onResume();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MY_COLORS = new int[]{
                Color.GRAY,
                ContextCompat.getColor(this, R.color.green),
                ContextCompat.getColor(this, R.color.md_blue_700),
                ContextCompat.getColor(this, R.color.md_red_900),
                ContextCompat.getColor(this, R.color.md_amber_900),
                ContextCompat.getColor(this, R.color.md_yellow_700),
                ContextCompat.getColor(this, R.color.md_green_700),
                ContextCompat.getColor(this, R.color.md_brown_600),
                ContextCompat.getColor(this, R.color.md_deep_orange_700),
                ContextCompat.getColor(this, R.color.md_indigo_700),
                ContextCompat.getColor(this, R.color.md_red_700),
                ContextCompat.getColor(this, R.color.md_purple_600)};

        setContentView(R.layout.activity_work_duration);


        work_close= (FancyButton) findViewById(R.id.btn_close_work);
        work_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        buttonSetLocation= (FancyButton) findViewById(R.id.btn_set_location);
        buttonSetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("org.md2k.phonesensor", "org.md2k.phonesensor.ActivitySettingsGeofence"));
                startActivity(intent);
//                finish();
                /*
                Dialog.singleChoice(ProductivityActivity.this, "Set Location", new String[]{"Home", "Work", "Other"}, 0, new DialogCallback() {
                    @Override
                    public void onSelected(String value) {
                       if(value.equals("Other")){
                           Dialog.editbox(ProductivityActivity.this, "Set Other Location", "Type your other location name.", new DialogCallback() {
                               @Override
                               public void onSelected(String value) {
                                   Toast.makeText(ProductivityActivity.this, "value=" + value, Toast.LENGTH_SHORT).show();
                               }
                           }).show();

                       }
                        Toast.makeText(ProductivityActivity.this,"value="+value,Toast.LENGTH_SHORT).show();
                    }
                }).show();
*/
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionToggleValues: {
                for (IDataSet<?> set : mChart.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleIcons: {
                for (IDataSet<?> set : mChart.getData().getDataSets())
                    set.setDrawIcons(!set.isDrawIconsEnabled());

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleHole: {
                if (mChart.isDrawHoleEnabled())
                    mChart.setDrawHoleEnabled(false);
                else
                    mChart.setDrawHoleEnabled(true);
                mChart.invalidate();
                break;
            }
            case R.id.actionDrawCenter: {
                if (mChart.isDrawCenterTextEnabled())
                    mChart.setDrawCenterText(false);
                else
                    mChart.setDrawCenterText(true);
                mChart.invalidate();
                break;
            }
            case R.id.actionToggleXVals: {

                mChart.setDrawEntryLabels(!mChart.isDrawEntryLabelsEnabled());
                mChart.invalidate();
                break;
            }
            case R.id.actionSave: {
                // mChart.saveToGallery("title"+System.currentTimeMillis());
                mChart.saveToPath("title" + System.currentTimeMillis(), "");
                break;
            }
            case R.id.actionTogglePercent:
                mChart.setUsePercentValues(!mChart.isUsePercentValuesEnabled());
                mChart.invalidate();
                break;
            case R.id.animateX: {
                mChart.animateX(1400);
                break;
            }
            case R.id.animateY: {
                mChart.animateY(1400);
                break;
            }
            case R.id.animateXY: {
                mChart.animateXY(1400, 1400);
                break;
            }
            case R.id.actionToggleSpin: {
                mChart.spin(1000, mChart.getRotationAngle(), mChart.getRotationAngle() + 360, Easing.EasingOption
                        .EaseInCubic);

                break;
            }
        }
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mSeekBarX.setVisibility(View.GONE);
        tvX.setText("" + (mSeekBarX.getProgress()));
        tvY.setText("" + (mSeekBarY.getProgress()));

        setData(mSeekBarX.getProgress(), mSeekBarY.getProgress());
    }

    private void setData(int count, float range) {

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < count ; i++) {
            entries.add(new PieEntry(mTimes[i],
                    locationNames[i],
                    getResources().getDrawable(R.drawable.star)));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
     //  dataSet.setColor(R.color.white);

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

//        ArrayList<Integer> colors = new ArrayList<Integer>();
//        colors.add(R.color.colorAccent);
//        colors.add(R.color.headerLemon);
/*
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
*/
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for(int c: MY_COLORS) colors.add(c);

        dataSet.setColors(colors);
//        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.TRANSPARENT);
        data.setValueTypeface(mTfLight);
        mChart.setData(data);


        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }
    private SpannableString generateCenterSpannableText() {
        String ss="";
        int curLen=0;
        for(int i=0;i<locationWithTime.length;i++){
            ss+=locationWithTime[i]+"\n";
        }
        SpannableString s = new SpannableString(ss);
        for(int i=0;i<locationWithTime.length;i++){
            s.setSpan(new ForegroundColorSpan(MY_COLORS[i%MY_COLORS.length]), curLen, curLen+locationWithTime[i].length(), 0);
            curLen+=locationWithTime[i].length()+1;
        }
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

/*
//        String stepStr= String.valueOf(totalSteps)+"/";
        int totalDataCollectionMin=totalDataCollection/(1000*60);
        String stepStr = String.format(Locale.getDefault(), "%02d:%02d",totalDataCollectionMin/60, totalDataCollectionMin%60)+"/";

        int goalMin=goal/(1000*60);
        String goalStr = String.format(Locale.getDefault(), "%02d:%02d",goalMin/60, goalMin%60);


//        String goalStr= String.valueOf(goal);
        String totalStr= stepStr+goalStr;

        SpannableString s = new SpannableString(totalStr);
        s.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.tealsecondary)), 0, stepStr.length()-1, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), stepStr.length()-1, stepStr.length(), 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), stepStr.length(), s.length(), 0);
        s.setSpan(new RelativeSizeSpan(0.5f), stepStr.length(), s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

*/
/*
        String stepStr= String.valueOf(totalSteps);
        moretogo=goal-totalSteps;
        String goalStr= String.valueOf(moretogo);
        String steps="steps";
        String today="today";
        String achieve= "more to achieve goal";
        String str= "Today \n"+stepStr+" hours of data collected\n"+goalStr+" hours more to go";

        SpannableString s = new SpannableString(str);
        s.setSpan(new ForegroundColorSpan(Color.GREEN), 0, str.length(), 0);
*/
        /*
        String str=stepStr+" "+steps+"\n"+ today+"\n"+goal+" "+achieve;
        SpannableString s = new SpannableString(str);
        s.setSpan(new ForegroundColorSpan(Color.GREEN), 0, stepStr.length(), 0);
        s.setSpan(new RelativeSizeSpan(1.7f), 0,stepStr.length(), 0);

        s.setSpan(new ForegroundColorSpan(Color.WHITE), stepStr.length(), (stepStr.length()+steps.length()+1), 0);
        s.setSpan(new RelativeSizeSpan(0.9f), stepStr.length(), (stepStr.length()+steps.length()+1), 0);

        s.setSpan(new ForegroundColorSpan(Color.WHITE), (stepStr.length()+steps.length()+1), (stepStr.length()+steps.length()+2+today.length()), 0);
        s.setSpan(new RelativeSizeSpan(1.5f),  (stepStr.length()+steps.length()+1), (stepStr.length()+steps.length()+2+today.length()), 0);

        s.setSpan(new ForegroundColorSpan(Color.RED), (stepStr.length()+steps.length()+2+today.length()),  (stepStr.length()+steps.length()+4+today.length()+togoStr.length()), 0);
        s.setSpan(new RelativeSizeSpan(0.9f), (stepStr.length()+steps.length()+2+today.length()),  (stepStr.length()+steps.length()+4+today.length()+togoStr.length()), 0);

        s.setSpan(new ForegroundColorSpan(Color.WHITE), (stepStr.length()+steps.length()+4+today.length()+togoStr.length()), s.length(), 0);
        s.setSpan(new RelativeSizeSpan(0.7f), (stepStr.length()+steps.length()+4+today.length()+togoStr.length()), s.length(), 0);
*/
/*
        SpannableString s = new SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
*/
        return s;


    }

}
