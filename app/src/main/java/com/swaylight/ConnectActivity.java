package com.swaylight;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class ConnectActivity extends AppCompatActivity {

    final String tag = "ConnectActivity";
    private Spinner spBrokers;
    private EditText etDeviceName;
    private Button btConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        spBrokers = findViewById(R.id.broker_spinner);
        etDeviceName = findViewById(R.id.device_name_edittext);
        btConnect = findViewById(R.id.connect_button);

        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ConnectActivity.this, ControlActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(getString(R.string.MQTT_BROKER), String.valueOf(spBrokers.getSelectedItem()));
                bundle.putString(getString(R.string.DEVICE_NAME), String.valueOf(etDeviceName.getText()));
                i.putExtras(bundle);
                startActivity(i);
            }
        });
    }
}
