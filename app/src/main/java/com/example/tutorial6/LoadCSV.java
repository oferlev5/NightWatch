package com.example.tutorial6;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import java.util.List;


public class LoadCSV extends AppCompatActivity {
    LineChart mpLineChart;
    LineDataSet lineDataSet_x;
    LineDataSet lineDataSet_y;
    LineDataSet lineDataSet_z;
    String fileName;
    private void generateGraph() {
        ArrayList<String[]> csvData = new ArrayList<>();
        String csvToOpen = fileName;
        System.out.println("fileName = " + fileName);

        csvData = CsvRead("/sdcard/csv_dir/" + csvToOpen);
        ArrayList<Entry> nData = new ArrayList<>();

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        for (int i = 0; i < csvData.size(); i++) {

            if (i > 7) {
                nData.add(new Entry(i, Float.parseFloat(csvData.get(i)[1])));
                System.out.println("csvData = " + csvData.get(i)[1]);
            }
        }

        LineChart lineChart = (LineChart) findViewById(R.id.line_chart);
        lineDataSet_x = new LineDataSet(nData, "N");
        dataSets.add(lineDataSet_x);
        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.invalidate();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_csv);
        Button BackButton = (Button) findViewById(R.id.button_back);
        LineChart lineChart = (LineChart) findViewById(R.id.line_chart);


        Spinner spinner = (Spinner) findViewById(R.id.spinner2);
        String directoryPath = "/sdcard/csv_dir/";
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();
        ArrayList<String> fileNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fileNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Handle the selected item
                fileName = adapterView.getItemAtPosition(i).toString();
                // Do something with the selectedMoveType
                generateGraph();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle the case when nothing is selected
                fileName = "not selected";

            }
        });






//        ArrayList<String[]> csvData = new ArrayList<>();
//        String csvToOpen = fileName;
//
//        csvData = CsvRead("/sdcard/csv_dir/"+ csvToOpen+".csv");
//        ArrayList<Entry> nData = new ArrayList<>();
//
//        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
//        for(int i = 0; i < csvData.size(); i++) {
//            if (i > 7) {
//                nData.add(new Entry(i, Float.parseFloat(csvData.get(i)[1])));
//
//            }
//        }
//        LineDataSet lineDataSet1 =  new LineDataSet(DataValues(csvData),"temperature");
//        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
//        dataSets.add(lineDataSet1);
//        LineData data = new LineData(dataSets);
//        lineChart.setData(data);
//        lineChart.invalidate();

//        lineDataSet_x =  new LineDataSet(nData, "N");
//        dataSets.add(lineDataSet_x);
//        lineDataSet_y =  new LineDataSet(yData, "y");
//        dataSets.add(lineDataSet_y);
//        lineDataSet_y.setColor(Color.MAGENTA);
//        lineDataSet_y.setCircleColor(Color.MAGENTA);
//        lineDataSet_z =  new LineDataSet(zData, "z");
//        lineDataSet_z.setColor(Color.RED);
//        dataSets.add(lineDataSet_z);
//        lineDataSet_z.setCircleColor(Color.RED);
//        LineData data = new LineData(dataSets);
//        lineChart.setData(data);
//        lineChart.invalidate();


        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickBack();
            }
        });
    }

    private void ClickBack(){
        finish();

    }

    private ArrayList<String[]> CsvRead(String path){
        ArrayList<String[]> CsvData = new ArrayList<>();
        try {
            File file = new File(path);
            CSVReader reader = new CSVReader(new FileReader(file));
            String[] nextline;
            int i = 0;
            while((nextline = reader.readNext())!= null){
                if(nextline != null) {
                    CsvData.add(nextline);
                }
                i++;
            }

        }catch (Exception e){}
        return CsvData;
    }

    private ArrayList<Entry> DataValues(ArrayList<String[]> csvData){
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        for (int i = 0; i < csvData.size(); i++){

            dataVals.add(new Entry(Integer.parseInt(csvData.get(i)[1]), //this is the problem location
                    Float.parseFloat(csvData.get(i)[0])));


        }

        return dataVals;
    }

}