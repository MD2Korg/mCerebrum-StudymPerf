package org.md2k.studymperf.data_collection;

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
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import org.md2k.datakitapi.DataKitAPI;
import org.md2k.datakitapi.datatype.DataTypeInt;
import org.md2k.datakitapi.exception.DataKitException;
import org.md2k.datakitapi.source.datasource.DataSourceBuilder;
import org.md2k.datakitapi.source.datasource.DataSourceClient;
import org.md2k.datakitapi.time.DateTime;
import org.md2k.studymperf.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import mehdi.sakout.fancybuttons.FancyButton;

public class ActivityPieChartDataCollection extends DemoBaseDataCollection implements SeekBar.OnSeekBarChangeListener,
        OnChartValueSelectedListener, OnDateSetListener {

    private PieChart mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;
    int goal =20;
    int totalDataCollection =2;
    TimePickerDialog mDialogHourMinute;

    @Override
    public void onDateSet(TimePickerDialog timePickerDialog, long millseconds) {
        Calendar c=Calendar.getInstance();
        c.setTimeInMillis(millseconds);
        int h=c.get(Calendar.HOUR_OF_DAY);
        int m=c.get(Calendar.MINUTE);

        goal= ((h*60)+m)*60*1000;

        saveToDataKit(goal);
        generateCenterSpannableText();
        mChart.setCenterText(generateCenterSpannableText());
        setData(2,2);

//        String text = getDateToString(millseconds);
//        mTvTime.setText(text);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goal = getIntent().getIntExtra("goal",12*60*60*1000);
        totalDataCollection=getIntent().getIntExtra("total_data_collection",0);
        setContentView(R.layout.activity_data_collection_duration);
        FancyButton step_close = (FancyButton) findViewById(R.id.btn_close_dcd);
        step_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FancyButton buttonSetGoal = (FancyButton) findViewById(R.id.btn_setgoal_dcd);
        buttonSetGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int h=goal/(1000*60*60);
                int m=(goal-h*1000*60*60)/(1000*60);
                Calendar c = Calendar.getInstance();c.set(Calendar.MINUTE, m);c.set(Calendar.HOUR_OF_DAY, h);
                mDialogHourMinute = new TimePickerDialog.Builder()
                        .setType(Type.HOURS_MINS)
                        .setCallBack(ActivityPieChartDataCollection.this)
                        .setHourText("Hour")
                        .setMinuteText("Minute")
                        .setCyclic(true)
                        .setCancelStringId("Cancel")
                        .setSureStringId("Ok")
                        .setThemeColor(getResources().getColor(R.color.tealsecondary))
                        .setCurrentMillseconds(c.getTimeInMillis())
                        .build();
                mDialogHourMinute.show(getSupportFragmentManager(), "hour_minute");
            }
        });


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
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(20, 0, 20, 0);

        mChart.setDragDecelerationFrictionCoef(0.95f);

        mChart.setCenterTextTypeface(mTfLight);
        mChart.setCenterTextSize(30.0f);
        mChart.setCenterText(generateCenterSpannableText());

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.TRANSPARENT);

        mChart.setTransparentCircleColor(Color.TRANSPARENT);
        mChart.setTransparentCircleAlpha(110);
        mChart.setHoleRadius(70f);
        mChart.setTransparentCircleRadius(41f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(270);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(false);
        mChart.setHighlightPerTapEnabled(true);

        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
        mChart.setOnChartValueSelectedListener(this);

        setData(2, 2);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);

        mSeekBarX.setOnSeekBarChangeListener(this);
        mSeekBarY.setOnSeekBarChangeListener(this);
        mChart.getLegend().setEnabled(false);

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
        mChart.setEntryLabelColor(Color.WHITE);
        mChart.setEntryLabelTypeface(mTfRegular);
        mChart.setEntryLabelTextSize(16f);
    }
    @Override
    public void onResume(){
//        Toast.makeText(this, "abc",Toast.LENGTH_SHORT).show();;
        super.onResume();
    }
/*
    TimePickerDialog.OnTimeSetListener onTimeSetListener=new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            goal=(hourOfDay*60+minute)*60*1000;
            saveToDataKit(goal);
            generateCenterSpannableText();
            mChart.setCenterText(generateCenterSpannableText());
            setData(2,2);

        }
    };
*/

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
        if(goal>totalDataCollection) {
            entries.add(new PieEntry(totalDataCollection /(float)(goal),
                    mParties[0],
                    getResources().getDrawable(R.drawable.star)));
            entries.add(new PieEntry((goal-totalDataCollection) /(float)(goal),
                    mParties[1],
                    getResources().getDrawable(R.drawable.star)));
        }else{
            entries.add(new PieEntry(1,
                    mParties[0],
                    getResources().getDrawable(R.drawable.star)));
            entries.add(new PieEntry(0,
                    mParties[1],
                    getResources().getDrawable(R.drawable.star)));

        }
/*
        float mult = range;


        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < count ; i++) {
            entries.add(new PieEntry((float) ((Math.random() * mult) + mult / 5),
                    mParties[i % mParties.length],
                    getResources().getDrawable(R.drawable.star)));
        }
*/

        PieDataSet dataSet = new PieDataSet(entries, "");
//        dataSet.setColor(R.color.colorAccent);

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
        final int[] MY_COLORS = {ContextCompat.getColor(this, R.color.green),
                Color.GRAY};
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

    private SpannableString generateCenterSpannableText() {
//        String stepStr= String.valueOf(totalSteps)+"/";
        int totalDataCollectionMin=totalDataCollection/(1000*60);
        String stepStr = String.format(Locale.getDefault(), "%02d:%02d",totalDataCollectionMin/60, totalDataCollectionMin%60)+"/";

        int goalMin=goal/(1000*60);
        String goalStr = String.format(Locale.getDefault(), "%02d:%02d",goalMin/60, goalMin%60);


//        String goalStr= String.valueOf(goal);
        String totalStr= stepStr+goalStr+"\n(hh:mm)";

        SpannableString s = new SpannableString(totalStr);
        s.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.green)), 0, stepStr.length()-1, 0);
        s.setSpan(new ForegroundColorSpan(Color.WHITE), stepStr.length()-1, stepStr.length(), 0);
        s.setSpan(new ForegroundColorSpan(Color.WHITE), stepStr.length(), s.length(), 0);
        s.setSpan(new RelativeSizeSpan(0.5f), stepStr.length(), s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

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
    void saveToDataKit(int goal){
        DataKitAPI dataKitAPI=DataKitAPI.getInstance(this);
        DataSourceBuilder dataSourceBuilder = new DataSourceBuilder().setType("GOAL_DATA_COLLECTION");
        try {
            DataSourceClient dataSourceClient = dataKitAPI.register(dataSourceBuilder);
            dataKitAPI.insert(dataSourceClient, new DataTypeInt(DateTime.getDateTime(), goal));
        } catch (DataKitException e) {
            Toasty.error(this, "Can't save data. Please try again",Toast.LENGTH_SHORT).show();
        }
    }
}
