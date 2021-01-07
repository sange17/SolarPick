package com.App.SolarPing;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    String mParam1;
    String mParam2;

    public MainFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        BarChart barchart = view.findViewById(R.id.barchart);
        LineChart linechart = view.findViewById(R.id.linechart);
        TextView titles = view.findViewById(R.id.title);
        //try {
        Bundle bundle = getArguments();


        System.out.println("fragment 실행");
        //number_of_generator_for_each_years 데이터 Main으로 부터 받기 시도


        ArrayList<String> number_of_generator_for_each_years;

        try {
            assert bundle != null;
            number_of_generator_for_each_years = bundle.getStringArrayList("number_of_generator_for_each_years");
            for (String n : number_of_generator_for_each_years) {
                System.out.println("generators =  " + n);
            }
            for (String n : number_of_generator_for_each_years) {
                System.out.println(n);
            }





            ArrayList<String> year = new ArrayList<>();
            ArrayList<BarEntry> entries = new ArrayList<>();
            int count = 0;
            int maxValue = 0;
            for (int i = 2015; i < 2015 + number_of_generator_for_each_years.size(); i++) {
                //수정코드
                entries.add(new BarEntry(Float.parseFloat(number_of_generator_for_each_years.get(count)), count));
                year.add(String.valueOf(i));

                //전코드
                if (Integer.parseInt(number_of_generator_for_each_years.get(count)) > maxValue) {
                    maxValue = Integer.parseInt(number_of_generator_for_each_years.get(count));
                }
                count++;
            }
            XAxis xAxis = barchart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


            System.out.println("개수 size = " + entries.size());
            BarDataSet barDataSet = new BarDataSet(entries, "태양광 발전소 개수");
            barchart.animateY(3000);
            BarData BData = new BarData(year, barDataSet);
            barchart.setData(BData);
            barchart.setDescription(null);
            titles.setText("연별 태양광 발전소 등록수");
        } catch (Exception ignored) {
        }

        //rising_point_of_landslide_comparingTo_last_year 데이터 Main으로 부터 받기 시도

        ArrayList<String> rising_point_of_landslide_comparingTo_last_year;
        try {
            rising_point_of_landslide_comparingTo_last_year = bundle.getStringArrayList("rising_point_of_landslide_comparingTo_last_year");

            for (String n : rising_point_of_landslide_comparingTo_last_year) {
                System.out.println("rising " + n);
            }

            ArrayList<String> year = new ArrayList<>();
            ArrayList<BarEntry> entries = new ArrayList<>();

            int count1 = 0;
            for (int i = 2015; i < 2015 + rising_point_of_landslide_comparingTo_last_year.size(); i++) {
                entries.add(new BarEntry(Float.parseFloat(rising_point_of_landslide_comparingTo_last_year.get(count1)), count1));
                year.add(String.valueOf(i));
                count1++;
            }
            XAxis xAxis = barchart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


            System.out.println("개수 size = " + entries.size());
            BarDataSet barDataSet = new BarDataSet(entries, "%");
            barchart.animateY(3000);
            BarData BData = new BarData(year, barDataSet);
            barchart.setData(BData);
            barchart.setDescription(null);
            String title = "연별 산사태 증감률";
            titles.setText(title);


        } catch (Exception ignored) {
        }


        try {
            ArrayList<String> generating_amount;
            generating_amount = bundle.getStringArrayList("generating_amount");


            String response = "";
            for (String n : generating_amount) {
                System.out.print(n + ",");
                response += n;
                //response.append(n);
            }
            String res = "";
            System.out.println("response" + response);
            if (response.equals("ul") || generating_amount.size() < 1 || response.equals("")) {
                titles.setText("데이터 없음");
                res = "데이터 없음";
            }



            barchart.setVisibility(BarChart.INVISIBLE);
            linechart.setVisibility(LineChart.VISIBLE);


            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            Date stringToDate = dateFormat.parse("2015-01");
            float max = 0;


            ArrayList<String> year = new ArrayList<>();
            ArrayList<Entry> entries = new ArrayList<>();
            int count = 0;


            assert stringToDate != null;
            cal.setTime(stringToDate);
            boolean flag = false;
            for (int i = 0; i < generating_amount.size(); i++) {
                try {
                    if (!generating_amount.get(i).equals("0") && !generating_amount.get(i + 1).equals("0")) {
                        flag = true;
                    } else if (generating_amount.get(i).equals("0") && generating_amount.get(i + 1).equals("0")) {
                        flag = false;
                    }
                } catch (Exception e) {
                    flag = false;
                }


                if (flag) {
                    entries.add(new BarEntry(Float.parseFloat(generating_amount.get(i)), count));
                    year.add(dateFormat.format(cal.getTime()));
                    count++;
                    if (Float.parseFloat(generating_amount.get(i)) > max) {
                        max = Float.parseFloat(generating_amount.get(i));
                    }

                }

                cal.add(Calendar.MONTH, 1);
            }

            System.out.println("개수 size = " + entries.size());
            LineDataSet barDataSet = new LineDataSet(entries, "%");
            linechart.animateY(3000);
            LineData LData = new LineData(year, barDataSet);
            linechart.setData(LData);
            linechart.setDescription(null);
            XAxis xAxis = linechart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


            if (!res.equals("데이터 없음")) {
                String title = "월별 태양광 발전량";
                titles.setText(title);
            }


        } catch (Exception ignored) {
        }

        return view;


    }


    private void setGraph(ColumnChartView chart, String XName, String YName, ColumnChartData
            data, List<AxisValue> mAxisXValues1, List<SubcolumnValue> mPointValues1) {
        Axis axisX1 = new Axis();
        axisX1.setHasLines(true).setValues(mAxisXValues1);
        axisX1.setHasTiltedLabels(true);
        axisX1.setTextSize(15);
        axisX1.setName(XName);
        axisX1.setTextColor(Color.BLACK);
        data.setAxisXBottom(axisX1);

        Axis axisY1 = new Axis();
        axisY1.setTextSize(15);
        axisY1.setTextColor(Color.BLACK);
        axisY1.setName(YName);
        data.setAxisYLeft(axisY1);


        chart.setInteractive(true);
        chart.setZoomEnabled(false);
        chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        Column line1 = new Column(mPointValues1);
        line1.setHasLabels(true);
        List<Column> lines1 = new ArrayList<>();
        lines1.add(line1);
        data.setColumns(lines1);
        chart.setColumnChartData(data);

    }

}
