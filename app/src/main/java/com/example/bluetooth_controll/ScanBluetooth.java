package com.example.bluetooth_controll;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ScanBluetooth extends AppCompatActivity {

    String DEVICE_ADDRESS = "";
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    ListView listBL;
    BluetoothDevice deviceCN;
    BluetoothAdapter mBluetoothAdapter;
    List<BluetoothDevice> deviceList;
    ArrayAdapter<String> arrayAdapter;
    boolean deviceFound = false;
    BluetoothSocket socket;
    OutputStream outputStream;
    ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Toast.makeText(ScanBluetooth.this, "Bluetooth OK", Toast.LENGTH_SHORT).show();
                        scanBL();
                    } else {
                        Toast.makeText(ScanBluetooth.this, "Bluetooth not OK", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_bluetooth);

        listBL = findViewById(R.id.listBL);
        deviceList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        listBL.setAdapter(arrayAdapter);
        setupBluetooth();

        listBL.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DEVICE_ADDRESS = deviceList.get(i).toString().substring(0, 17);
                scanBL();
                if(deviceFound){
                    try {
                        BLTConnect();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

    }

    private void setupBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivityResultLauncher.launch(enableBTIntent);
        } else {
            scanBL();
        }
    }

    private void scanBL() {
        deviceList.clear();
        arrayAdapter.clear();

        // Get the paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                deviceList.add(device);
                String deviceName = device.getName();
                String deviceAddress = device.getAddress();
                arrayAdapter.add(deviceName + "\n" + deviceAddress);
                if(device.getAddress().equals(DEVICE_ADDRESS)){
                    deviceCN = device;
                    deviceFound = true;
                    break;
                }
            }
        }
    }

    private void BLTConnect() throws IOException {
        boolean connected = true;

        try
        {
            socket = deviceCN.createRfcommSocketToServiceRecord(PORT_UUID); //Creates a socket to handle the outgoing connection
            socket.connect();
            if(socket.isConnected()) {

                Toast.makeText(getApplicationContext(),
                        "Kết nối thành công", Toast.LENGTH_LONG).show();
//                imgStatus.setImageResource(R.drawable.green_circle);
            } else {
                Toast.makeText(getApplicationContext(),
                        "Chưa kết nối được", Toast.LENGTH_LONG).show();
//                imgStatus.setImageResource(R.drawable.red_circle);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            connected = false;
            Toast.makeText(getApplicationContext(),
                    "Kết nối thất bại", Toast.LENGTH_LONG).show();
//            imgStatus.setImageResource(R.drawable.red_circle);
        }

        if(connected)
        {
            BluetoothConnectionManager.getInstance().setOutputStream(socket.getOutputStream());
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
            Intent data = new Intent();
            data.putExtra("DEVICE_NAME", deviceCN.getName());
            data.putExtra("CONNECTION_STATUS", "connected");
            setResult(RESULT_OK, data);
            finish();
        }
    }

}