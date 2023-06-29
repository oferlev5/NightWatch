package com.example.tutorial6;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tutorial6.ui.home.FunctionalityFragment;
import com.github.mikephil.charting.data.Entry;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.units.qual.A;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class TerminalFragment extends Fragment implements ServiceConnection, SerialListener {
    public static final int ARRAY_LEN = 50;
    public static final double MOVING_THRESHOLD = 13;
    public static final double LIGHT_THRESHOLD = 0.05;
    public static final int SOUND_THRESHOLD = 790;

    private enum Connected { False, Pending, True }

    private String deviceAddress;
    private SerialService service;

    private TextView receiveText;
    private TextView sendText;

    private TextUtil.HexWatcher hexWatcher;

    private Connected connected = Connected.False;
    private boolean initialStart = true;
    private boolean hexEnabled = false;
    private boolean pendingNewline = false;
    private String newline = TextUtil.newline_crlf;


    public ArrayList<Float> accelerationArr = new ArrayList<>();
    public ArrayList<Float> lightArr = new ArrayList<>();
    public ArrayList<Float> soundArr = new ArrayList<>();
    public ArrayList<Float> tempArr = new ArrayList<>();

    TextView lightText;
    TextView soundText;
    TextView tempText;
    TextView movementText;
    /*
     * Lifecycle
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        deviceAddress = getArguments().getString("device");
//        System.out.println("device adress = " + deviceAddress);
        System.out.println("idan - in terminalfrag");
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

        Button stopButton = view.findViewById(R.id.end_but);

        receiveText = view.findViewById(R.id.receive_text);                          // TextView performance decreases with number of spans
        receiveText.setTextColor(getResources().getColor(R.color.colorRecieveText)); // set as default color to reduce number of spans
        receiveText.setMovementMethod(ScrollingMovementMethod.getInstance());

        sendText = view.findViewById(R.id.send_text);
        hexWatcher = new TextUtil.HexWatcher(sendText);
        hexWatcher.enable(hexEnabled);
        sendText.addTextChangedListener(hexWatcher);
        sendText.setHint(hexEnabled ? "HEX mode" : "");

        this.lightText = view.findViewById(R.id.light_txt);
        this.tempText = view.findViewById(R.id.temp_txt);
        this.soundText = view.findViewById(R.id.sound_txt);
        this.movementText = view.findViewById(R.id.movement_txt);

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment anotherFragment = new FunctionalityFragment();
                fragmentTransaction.replace(R.id.nav_host_fragment_content_drawer,anotherFragment);
//                fragmentTransaction.add(R.id.nav_host_fragment_content_main2, anotherFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
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
            System.out.println("cinnecting.....");
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
        ArrayList<Float> parsedData = parseData(message);
        if (parsedData.size() < 6){
            return;
        }
        saveDataToArrays(parsedData);

        /** check if the params status changed */
        Boolean isMoving = checkIfMoving();
        Boolean isLight = checkIfLight();
        Boolean isNoise = checkIfSound();

        float temp = sumFloat(this.tempArr)/this.tempArr.size();

        /** updating displayed text according to data */
        if (isMoving) this.movementText.setText("MOVING!!!");
        else this.movementText.setText("");

        if (isLight) this.lightText.setText("LIGHT ON");
        else this.lightText.setText("");

        if (isNoise) this.soundText.setText("CRYING");
        else this.soundText.setText("");
        this.tempText.setText(String.valueOf(temp));
    }

    private Boolean checkIfMoving() {
        float accelMean = sumFloat(this.accelerationArr)/this.accelerationArr.size();
        if (accelMean>MOVING_THRESHOLD) return Boolean.TRUE;
        return Boolean.FALSE;
    }
    private Boolean checkIfLight() {
        float accelMean = sumFloat(this.lightArr)/this.lightArr.size();
        if (accelMean>LIGHT_THRESHOLD) return Boolean.TRUE;
        return Boolean.FALSE;
    }

    private Boolean checkIfSound() {
        float accelMean = sumFloat(this.soundArr)/this.soundArr.size();
        if (accelMean>SOUND_THRESHOLD) return Boolean.TRUE;
        return Boolean.FALSE;
    }

    private void saveDataToArrays(ArrayList<Float> parsedData) {
        float accel = (float) Math.sqrt(Math.pow(parsedData.get(0),2) + Math.pow(parsedData.get(1),2) + Math.pow(parsedData.get(2),2));
        if (this.accelerationArr.size() > ARRAY_LEN){
            delFirstAddLast(this.accelerationArr, accel);
        }
        else this.accelerationArr.add(accel);

        if (this.tempArr.size() >= ARRAY_LEN){
            delFirstAddLast(this.tempArr, parsedData.get(4));
        }
        else this.tempArr.add(parsedData.get(4));
        if (this.lightArr.size() >= ARRAY_LEN){
            delFirstAddLast(this.lightArr, parsedData.get(5));
        }
        else this.lightArr.add(parsedData.get(5));

        if (this.soundArr.size() >= ARRAY_LEN){
            delFirstAddLast(this.soundArr, parsedData.get(6));
        }
        else this.soundArr.add(parsedData.get(6));
    }

    private ArrayList<Float> parseData(byte[] message) {
        ArrayList<Float> res = new ArrayList<>();
        if (hexEnabled) {
            receiveText.append(TextUtil.toHexString(message) + '\n');
        } else {
            String msg = new String(message);
            if (newline.equals(TextUtil.newline_crlf) && msg.length() > 0) {
                // don't show CR as ^M if directly before LF
                String msg_to_save = msg;
                msg_to_save = msg.replace(TextUtil.newline_crlf, TextUtil.emptyString);
                // check message length
                if (msg_to_save.length() > 1) {
                    // split message string by ',' char
                    String[] parts = msg_to_save.split(",");
                    // function to trim blank spaces
                    parts = clean_str(parts);
                    String tempTime = parts[3];
                    float floatTime = roundFloat(Float.parseFloat(tempTime));

//                    cast parts into floats and put in array
                    for (int i = 0; i < parts.length; i++) {
                        if (i == 3){
                            res.add(floatTime);
                            continue;
                        }
                        res.add(Float.parseFloat(parts[i]));
                    }
                }
            }
        }
        return res;
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
            receive(data);
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

    private void status(String str) {
        SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
        spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorStatusText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        receiveText.append(spn);
    }

    public <T> void delFirstAddLast(ArrayList<T> arr, T num){
        arr.remove(0);
        arr.add(num);
    }

    public static float sumFloat(ArrayList<Float> arr){
        float sum = 0f;
        for (float f : arr){
            sum += f;
        }
        return sum;
    }
}
