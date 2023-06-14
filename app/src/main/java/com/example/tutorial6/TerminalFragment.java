package com.example.tutorial6;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.widget.TintableImageSourceView;
import androidx.fragment.app.Fragment;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLOutput;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class TerminalFragment extends Fragment implements ServiceConnection, SerialListener {

    private enum Connected { False, Pending, True }

    private String deviceAddress;
    private SerialService service;

    private TextView receiveText;
    private TextView sendText;
    TextView steps;

    private TextUtil.HexWatcher hexWatcher;

    private Connected connected = Connected.False;
    private boolean initialStart = true;
    private boolean hexEnabled = false;
    private boolean pendingNewline = false;
    private String newline = TextUtil.newline_crlf;
    ArrayList<Double> Nnumbers;

    LineChart mpLineChart;
    LineDataSet lineDataSet_x;
    LineDataSet lineDataSet_y;
    LineDataSet lineDataSet_z;
    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    LineData data;
    Boolean flag = Boolean.FALSE;

    ArrayList<String[]> valsBeforeSave = new ArrayList<>();
    CSVWriter csvWriter;

    String selectedMoveType = "Walking";

    public static String csvNameToOpen;



    /*
     * Lifecycle
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        deviceAddress = getArguments().getString("device");

    }


    @Override
    public void onDestroy() {
        if (connected != Connected.False)
            disconnect();
        getActivity().stopService(new Intent(getActivity(), SerialService.class));
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    @Override
    public void onStop() {
        if(service != null && !getActivity().isChangingConfigurations())
            service.detach();
        super.onStop();
    }

    @SuppressWarnings("deprecation") // onAttach(context) was added with API 23. onAttach(activity) works for all API versions
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {
        try { getActivity().unbindService(this); } catch(Exception ignored) {}
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(initialStart && service != null) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        service.attach(this);
        if(initialStart && isResumed()) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    /*
     * UI
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminal, container, false);
        receiveText = view.findViewById(R.id.receive_text);                          // TextView performance decreases with number of spans
        receiveText.setTextColor(getResources().getColor(R.color.colorRecieveText)); // set as default color to reduce number of spans
        receiveText.setMovementMethod(ScrollingMovementMethod.getInstance());
        steps = view.findViewById(R.id.textView2);




        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.con_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        EditText editTextSteps = view.findViewById(R.id.editTextNumber3);
        EditText editTextNameFile = view.findViewById(R.id.editTextTextPersonName);
        Button buttonStart = view.findViewById(R.id.button3);
        Button buttonStop = view.findViewById(R.id.button4);
        Button buttonReset = view.findViewById(R.id.button5);
        Button buttonSave = view.findViewById(R.id.button6);

        sendText = view.findViewById(R.id.send_text);
        hexWatcher = new TextUtil.HexWatcher(sendText);
        hexWatcher.enable(hexEnabled);
        sendText.addTextChangedListener(hexWatcher);
        sendText.setHint(hexEnabled ? "HEX mode" : "");

        View sendBtn = view.findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(v -> send(sendText.getText().toString()));

        mpLineChart = (LineChart) view.findViewById(R.id.line_chart);
        lineDataSet_x =  new LineDataSet(emptyDataValues(), "N");
//        lineDataSet_y =  new LineDataSet(emptyDataValues(), "y");
//        lineDataSet_y.setColor(Color.MAGENTA);
        lineDataSet_x.setCircleColor(Color.RED);
//        lineDataSet_z =  new LineDataSet(emptyDataValues(), "z");
//        lineDataSet_z.setColor(Color.RED);
//        lineDataSet_z.setCircleColor(Color.RED);


        dataSets.add(lineDataSet_x);
//        dataSets.add(lineDataSet_y);
//        dataSets.add(lineDataSet_z);
        data = new LineData(dataSets);
        mpLineChart.setData(data);
        mpLineChart.invalidate();

        Button buttonClear = (Button) view.findViewById(R.id.button1);
        Button buttonCsvShow = (Button) view.findViewById(R.id.button2);


        buttonClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getContext(),"Clear",Toast.LENGTH_SHORT).show();
                LineData data = mpLineChart.getData();
                ILineDataSet set = data.getDataSetByIndex(0);
                data.getDataSetByIndex(0);
                while(set.removeLast()){}

            }
        });
        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Nnumbers = new ArrayList<>();
                flag = Boolean.TRUE;

            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                flag = Boolean.FALSE;
            }

        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                valsBeforeSave = new ArrayList<>();
                flag = Boolean.FALSE;
                steps.setText(String.valueOf(0));
//                data = new LineData();
//                mpLineChart.setData(data);
//                mpLineChart.invalidate();
            }

        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Handle the selected item
                selectedMoveType = adapterView.getItemAtPosition(i).toString();
                // Do something with the selectedMoveType

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Handle the case when nothing is selected
                selectedMoveType = "not selected";
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(View v) {
                String val = editTextNameFile.getText().toString().trim();
                if (val.isEmpty()) {
                    editTextNameFile.setError("Please enter value");
                    editTextNameFile.requestFocus();
                    return;
                }
//                first get the text from user:
                String numStepsString = editTextSteps.getText().toString();
                String csvName = editTextNameFile.getText().toString();


                try {
                    System.out.println("csvName = " + csvName);
//                    File file = new File("/sdcard/csv_dir/");
//                    file.mkdirs();
//                    String csv = "/sdcard/csv_dir/"+ csvName + ".csv";
//                    csvWriter = new CSVWriter(new FileWriter(csv,true));
                    File directory = new File("/sdcard/csv_dir/");
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }

                    String csv = "/sdcard/csv_dir/" + csvName + ".csv";
                    csvWriter = new CSVWriter(new FileWriter(csv, true));

//                    create the header
                    String[] s = {"NAME:",csvName};
                    csvWriter.writeNext(s);
                    LocalDateTime myDateObj = LocalDateTime.now();
                    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                    String formattedDate = myDateObj.format(myFormatObj);
                    String dateStr = String.valueOf(formattedDate);
                    s = new String[] {"EXPERIMENT TIME:", dateStr};
                    csvWriter.writeNext(s);
                    s = new String[] {"ACTIVITY TYPE:", selectedMoveType};
                    csvWriter.writeNext(s);
                    s = new String[] {"COUNT OF ACTUAL STEPS:", numStepsString};
                    csvWriter.writeNext(s);
                    s = new String[] {"ESTIMATED NUMBER OF STEPS:", steps.getText().toString()};
                    csvWriter.writeNext(s);
                    s = new String[]{};
                    csvWriter.writeNext(s);


                    s = new String[] {"Time[sec]", "ACC X","ACC Y","ACC Z"};
                    csvWriter.writeNext(s);
                    int sampleLen = valsBeforeSave.size();
                    s = valsBeforeSave.get(0);
                    String firstT = s[0];
                    Float firstTime = Float.valueOf(firstT);
                    for (int i=0; i < sampleLen ; i++){
                        s = valsBeforeSave.get(i);
                        Float temp = Float.parseFloat(s[0]);
                        temp -= firstTime;
                        temp = roundFloat(temp);
                        s[0] = String.valueOf(temp);
                        csvWriter.writeNext(s);
                    }
                    csvWriter.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        buttonCsvShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                csvNameToOpen = sendText.getText().toString();
                OpenLoadCSV();

            }
        });


        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_terminal, menu);
        menu.findItem(R.id.hex).setChecked(hexEnabled);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.clear) {
            receiveText.setText("");
            return true;
        } else if (id == R.id.newline) {
            String[] newlineNames = getResources().getStringArray(R.array.newline_names);
            String[] newlineValues = getResources().getStringArray(R.array.newline_values);
            int pos = java.util.Arrays.asList(newlineValues).indexOf(newline);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Newline");
            builder.setSingleChoiceItems(newlineNames, pos, (dialog, item1) -> {
                newline = newlineValues[item1];
                dialog.dismiss();
            });
            builder.create().show();
            return true;
        } else if (id == R.id.hex) {
            hexEnabled = !hexEnabled;
            sendText.setText("");
            hexWatcher.enable(hexEnabled);
            sendText.setHint(hexEnabled ? "HEX mode" : "");
            item.setChecked(hexEnabled);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /*
     * Serial + UI
     */
    private String[] clean_str(String[] stringsArr){
         for (int i = 0; i < stringsArr.length; i++)  {
             stringsArr[i]=stringsArr[i].replaceAll(" ","");
        }


        return stringsArr;
    }
    private void connect() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            status("connecting...");
            connected = Connected.Pending;
            SerialSocket socket = new SerialSocket(getActivity().getApplicationContext(), device);
            service.connect(socket);
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    private void disconnect() {
        connected = Connected.False;
        service.disconnect();
    }

    private void send(String str) {
        if(connected != Connected.True) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String msg;
            byte[] data;
            if(hexEnabled) {
                StringBuilder sb = new StringBuilder();
                TextUtil.toHexString(sb, TextUtil.fromHexString(str));
                TextUtil.toHexString(sb, newline.getBytes());
                msg = sb.toString();
                data = TextUtil.fromHexString(msg);
            } else {
                msg = str;
                data = (str + newline).getBytes();
            }
            SpannableStringBuilder spn = new SpannableStringBuilder(msg + '\n');
            spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            receiveText.append(spn);
            service.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }

    private void receive(byte[] message) {
        if(hexEnabled) {
            receiveText.append(TextUtil.toHexString(message) + '\n');
        } else {
            String msg = new String(message);
            if(newline.equals(TextUtil.newline_crlf) && msg.length() > 0) {
                // don't show CR as ^M if directly before LF
                String msg_to_save = msg;
                msg_to_save = msg.replace(TextUtil.newline_crlf, TextUtil.emptyString);
                // check message length
                if (msg_to_save.length() > 1){
                    // split message string by ',' char
                    String[] parts = msg_to_save.split(",");
                    // function to trim blank spaces
                    parts = clean_str(parts);
                    String tempTime = parts[3];
                    float floatTime = roundFloat(Float.parseFloat(tempTime));


                // saving data to csv
                // parse string values, in this case [0] is x  [1] is y [2] is z [3] is count (t)- next will change to time
                    double X = Math.pow(Float.valueOf(parts[0]),2);
                    double Y = Math.pow(Float.valueOf(parts[1]),2);
                    double Z = Math.pow(Float.valueOf(parts[2]),2);
                    double N = Math.sqrt(X + Y + Z);
                    Nnumbers.add(N);

                    if (! Python.isStarted()) {
                        Python.start(new AndroidPlatform(getContext()));
                    }
                    Python py =  Python.getInstance();
                    PyObject pyobj = py.getModule("test");
                    PyObject obj = pyobj.callAttr("main", String.valueOf(Nnumbers));
                    steps.setText(obj.toString());

                    String row[]= new String[]{String.valueOf(floatTime),String.valueOf(N)};
    //              this saves to csv - copy later
    //              csvWriter.writeNext(row);
    //              csvWriter.close();

                    // add received values to line dataset for plotting the linechart

    //                ArrayList<Float> newEntry = new ArrayList<>();
    //                newEntry.add(Float.parseFloat(parts[0]));
    //                newEntry.add(Float.parseFloat(parts[1]));
    //                newEntry.add(Float.parseFloat(parts[2]));
    //                newEntry.add(Float.parseFloat(parts[3]));

                    valsBeforeSave.add(row);

                    //here we do the python and return it to steps
                    //steps.setText(obj.toString());


                    data.addEntry(new Entry(Float.parseFloat(parts[3]),(float) N),0);
//                    data.addEntry(new Entry(Float.parseFloat(parts[3]),Float.parseFloat(parts[1])),1);
//                    data.addEntry(new Entry(Float.parseFloat(parts[3]),Float.parseFloat(parts[2])),2);
                    lineDataSet_x.notifyDataSetChanged(); // let the data know a dataSet changed
                    mpLineChart.notifyDataSetChanged(); // let the chart know it's data changed
                    mpLineChart.invalidate(); // refresh
                }

                msg = msg.replace(TextUtil.newline_crlf, TextUtil.newline_lf);
                // send msg to function that saves it to csv
                // special handling if CR and LF come in separate fragments
                if (pendingNewline && msg.charAt(0) == '\n') {
                    Editable edt = receiveText.getEditableText();
                    if (edt != null && edt.length() > 1)
                        edt.replace(edt.length() - 2, edt.length(), "");
                }
                pendingNewline = msg.charAt(msg.length() - 1) == '\r';
            }
            receiveText.append(TextUtil.toCaretString(msg, newline.length() != 0));
        }
    }

    private void status(String str) {
        SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
        spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorStatusText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        receiveText.append(spn);
    }

    /*
     * SerialListener
     */
    @Override
    public void onSerialConnect() {
        status("connected");
        connected = Connected.True;
    }

    @Override
    public void onSerialConnectError(Exception e) {
        status("connection failed: " + e.getMessage());
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        try {
            if (flag){
                receive(data);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSerialIoError(Exception e) {
        status("connection lost: " + e.getMessage());
        disconnect();
    }

    private ArrayList<Entry> emptyDataValues()
    {
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        return dataVals;
    }

    private void OpenLoadCSV(){
        Intent intent = new Intent(getContext(),LoadCSV.class);
        startActivity(intent);
    }

    public static float roundFloat(float value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        String roundedValueString = decimalFormat.format(value);
        float roundedValue = Float.parseFloat(roundedValueString);
        return roundedValue;
    }

}
