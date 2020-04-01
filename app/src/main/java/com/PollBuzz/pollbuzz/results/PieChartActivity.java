package com.PollBuzz.pollbuzz.results;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.PollBuzz.pollbuzz.R;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PieChartActivity extends AppCompatActivity {
    TextView voters,question;
    Dialog dialog;
    AnyChartView anyChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);
        setGlobals();
        showDialog();
        createPieChart();


       // anyChartView.setProgressBar(findViewById(R.id.progress_bar));



    }

    private void createPieChart() {
        Pie pie = AnyChart.pie();

        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                String t;
                if(Integer.parseInt(event.getData().get("value"))<2)
                    t=" Vote";
                else
                    t=" Votes";
                Toast.makeText(PieChartActivity.this, event.getData().get("x") + " : " + event.getData().get("value")+t, Toast.LENGTH_SHORT).show();
            }
        });
        Map<String,Integer> map=PercentageResult.data;

        List<DataEntry> data = new ArrayList<>();
        for(Map.Entry<String,Integer> entry: map.entrySet())
        {
            data.add(new ValueDataEntry(entry.getKey(),entry.getValue()));
        }

        pie.data(data);
        pie.legend().fontSize(25).fontColor("Black").fontWeight(500).fontFamily("Maven Pro");

        //pie.title(PercentageResult.question).zIndex(10);



        pie.labels().position("outside").fontColor("Black").fontSize(15).fontStyle("Bold");
        pie.selected().fontWeight(200);




        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);

        dialog.dismiss();
        anyChartView.setChart(pie);

    }

    private void setGlobals() {
        anyChartView= findViewById(R.id.any_chart_view);
        voters=findViewById(R.id.voters);
        question=findViewById(R.id.question);
        question.setText(PercentageResult.question);
        String voter="Total Voters : "+String.valueOf(PercentageResult.total);
        voters.setText(voter);
        dialog=new Dialog(PieChartActivity.this);

    }

    private void showDialog() {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading_dialog);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        dialog.setCancelable(false);
        dialog.show();
        window.setAttributes(lp);
    }
}
