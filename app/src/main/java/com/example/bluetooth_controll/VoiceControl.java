package com.example.bluetooth_controll;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class VoiceControl extends AppCompatActivity {

    private OutputStream outputStream;
    TextView status;
    TextView voice;
    Button BtVoice;
    String device_name;
    ArrayList<String> forwardCommands = new ArrayList<String>(Arrays.asList("đi thẳng", "tiến", "go"));
    ArrayList<String> backwardCommands = new ArrayList<String>(Arrays.asList("lùi", "back"));
    ArrayList<String> rightCommands = new ArrayList<String>(Arrays.asList("rẽ phải", "phải", "right"));
    ArrayList<String> leftCommands = new ArrayList<String>(Arrays.asList("rẽ trái", "trái", "left"));
    ActivityResultLauncher<Intent> mVoiceActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()==RESULT_OK){
                        Intent data = result.getData();
                        ArrayList<String> resultVoice = data
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        for (String res : resultVoice){
                            System.out.println(res);
                        }
                        voice.setText(resultVoice.get(0));
                        try {
                            convertSpeechToCommand(resultVoice.get(0));
                        } catch (IOException ioException){
                            ioException.printStackTrace();
                        }
                    }
                }
            }
    );

    private void convertSpeechToCommand(String s) throws IOException {
        String speech = s.toLowerCase();

        if (forwardCommands.contains(speech)){
            Toast.makeText(this, "F", Toast.LENGTH_SHORT).show();
            outputStream.write('F');
        }

        if (backwardCommands.contains(speech)){
            Toast.makeText(this, "B", Toast.LENGTH_SHORT).show();
            outputStream.write('B');
        }
        if (leftCommands.contains(speech)){
            Toast.makeText(this, "L", Toast.LENGTH_SHORT).show();
            outputStream.write('L');
        }

        if (rightCommands.contains(speech)){
            Toast.makeText(this, "R", Toast.LENGTH_SHORT).show();
            outputStream.write('R');
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_control);

        status = findViewById(R.id.textStatusVoice);
        voice = findViewById(R.id.textVoice);
        BtVoice = findViewById(R.id.buttonVoice);
        outputStream = BluetoothConnectionManager.getInstance().getOutputStream();
        device_name = getIntent().getStringExtra("CONNECTED_DEVICE");
        status.setText(device_name);

        BtVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });

    }

    private void promptSpeechInput() {
        Intent intentVoice = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentVoice.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intentVoice.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN");
        intentVoice.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say something ...");
        setResult(RESULT_OK, intentVoice);

        try{
            mVoiceActivityResult.launch(intentVoice);
        } catch (ActivityNotFoundException anfe) {
            Toast.makeText(getApplicationContext(),
                    "Không hỗ trợ voice!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}