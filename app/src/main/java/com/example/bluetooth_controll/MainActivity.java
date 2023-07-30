package com.example.bluetooth_controll;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView status;
    Button buttonScan;
    Button buttonControl;
    Button buttonVoice;
    String connectStatus="connected";
    String deviceNameCN = "None";
    ActivityResultLauncher<Intent> mActivityResultLauncherScan = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK){
                        if(result.getData()!=null){
                            Intent data =result.getData();
                            deviceNameCN = data.getStringExtra("DEVICE_NAME");
                            connectStatus = data.getStringExtra("CONNECTION_STATUS");
                            status.setText(deviceNameCN);
                        }
                    }
                }
            }
    );
    ActivityResultLauncher<Intent> mActivityResultLauncherControl = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                }
            }
    );

    ActivityResultLauncher<Intent> mActivityResultLauncherVoice = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = findViewById(R.id.textStatus);
        buttonScan = findViewById(R.id.button);
        buttonControl = findViewById(R.id.buttonControl);
        buttonVoice = findViewById(R.id.buttonVoice);

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentScan = new Intent(MainActivity.this,ScanBluetooth.class);
                mActivityResultLauncherScan.launch(intentScan);
            }
        });

        buttonControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(connectStatus.equals("connected")) {
                    Intent intentControl = new Intent(MainActivity.this, ControlScreen.class);
                    intentControl.putExtra("CONNECTED_DEVICE", deviceNameCN);
                    mActivityResultLauncherControl.launch(intentControl);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Thông báo");
                    builder.setMessage("Bạn phải kết nối bluetooth trước!!");
                    builder.setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

        buttonVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(connectStatus.equals("connected")) {
                    Intent intentVoice = new Intent(MainActivity.this, VoiceControl.class);
                    intentVoice.putExtra("CONNECTED_DEVICE",deviceNameCN);
                    mActivityResultLauncherVoice.launch(intentVoice);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Thông báo");
                    builder.setMessage("Bạn phải kết nối với thiết bị trước!!");
                    builder.setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }
}