package com.swaylight;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.swaylight.data.FilePath;
import com.swaylight.library.SLMqttDetail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ConnectActivity extends AppCompatActivity {

    final String tag = "ConnectActivity";
    private EditText etName;
    private EditText etBroker;
    private EditText etDeviceName;
    private EditText etClientId;
    private Button btConnect;
    private Button btConnectToMain;
    private ListView lvMqtt;
    private LayoutInflater inflater;
    private MqttAdapter adapter;
    private ArrayList<SLMqttDetail> mqttList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        etName = findViewById(R.id.name_edittext);
        etBroker = findViewById(R.id.broker_edittext);
        etDeviceName = findViewById(R.id.device_name_edittext);
        etClientId = findViewById(R.id.client_id_edittext);
        btConnect = findViewById(R.id.connect_button);
        btConnectToMain =findViewById(R.id.connect_to_main_button);

        inflater = LayoutInflater.from(this);
        lvMqtt = findViewById(R.id.mqtt_listview);
        Log.d(tag, "getFilesDir():" + getFilesDir().toString());
        adapter = new MqttAdapter();
        lvMqtt.setAdapter(adapter);

        lvMqtt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final SLMqttDetail detail = (SLMqttDetail) parent.getItemAtPosition(position);
                etName.setText(detail.getName());
                etBroker.setText(detail.getBroker());
                etDeviceName.setText(detail.getDeviceName());
                etClientId.setText(detail.getClientId());
            }
        });
        lvMqtt.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final SLMqttDetail detail = (SLMqttDetail) parent.getItemAtPosition(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(ConnectActivity.this);
                builder.setMessage("Delete " + detail.getName() + "?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mqttList.remove(detail);
                        Utils.Companion.writeToFile(getFilesDir().toString(), FilePath.MQTT_LIST, mqttList);
                        Log.d(tag, "mqttList:" + mqttList.toString());
                        updateList();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
                return false;
            }
        });

        mqttList.clear();
        mqttList = (ArrayList<SLMqttDetail>) Utils.Companion.readFromFile(getFilesDir().toString(), FilePath.MQTT_LIST);
        updateList();

        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToActivity(ControlActivity.class);
            }
        });

        btConnectToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToActivity(SwayLightMainActivity.class);
            }
        });
    }

    private synchronized void updateList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void connectToActivity(Class<?> cls) {
        String name = etName.getText().toString().trim();
        String broker = etBroker.getText().toString().trim();
        String deviceName = etDeviceName.getText().toString().trim();
        String clientId = etClientId.getText().toString().trim();
        if(name.isEmpty() || broker.isEmpty() || deviceName.isEmpty() || clientId.isEmpty()) {
            Log.d(tag, "detail field empty!");
        }else {
            SLMqttDetail newDetail = new SLMqttDetail(etName.getText().toString(),
                    etBroker.getText().toString(),
                    etDeviceName.getText().toString(),
                    etClientId.getText().toString());
            boolean contain = false;
            for(SLMqttDetail detail: mqttList) {
                if(detail.equals(newDetail)) {
                    detail.setBroker(newDetail.getBroker());
                    detail.setDeviceName(newDetail.getDeviceName());
                    detail.setClientId(newDetail.getClientId());
                    contain = true;
                    break;
                }
            }
            if(!contain) {
                mqttList.add(newDetail);
            }
            Utils.Companion.writeToFile(getFilesDir().toString(), FilePath.MQTT_LIST, mqttList);
            mqttList.clear();
            mqttList = (ArrayList<SLMqttDetail>) Utils.Companion.readFromFile(getFilesDir().toString(), FilePath.MQTT_LIST);
            updateList();
            Intent i = new Intent(ConnectActivity.this, cls);
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.MQTT_BROKER), String.valueOf(etBroker.getText()));
            bundle.putString(getString(R.string.DEVICE_NAME), String.valueOf(etDeviceName.getText()));
            bundle.putString(getString(R.string.MQTT_CLIENT_ID), String.valueOf(etClientId.getText()));
            i.putExtras(bundle);
            startActivity(i);
        }
    }

    private class MqttAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mqttList.size();
        }

        @Override
        public Object getItem(int position) {
            return mqttList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.mqtt_list_item, parent, false);
                holder = new ViewHolder();
                holder.tvName = convertView.findViewById(R.id.mqtt_name_textview);
                holder.tvBroker = convertView.findViewById(R.id.broker_textview);
                holder.tvDeviceName = convertView.findViewById(R.id.device_name_textview);
                holder.tvClientId = convertView.findViewById(R.id.cliet_id_textview);
                holder.btConnect = convertView.findViewById(R.id.item_connect_button);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            SLMqttDetail detail = mqttList.get(position);
            holder.tvName.setText(detail.getName());
            holder.tvBroker.setText(detail.getBroker());
            holder.tvDeviceName.setText(detail.getDeviceName());
            holder.tvClientId.setText(detail.getClientId());
            holder.btConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String broker = holder.tvBroker.getText().toString().trim();
                    String deviceName = holder.tvDeviceName.getText().toString().trim();
                    String clientId = holder.tvClientId.getText().toString().trim();

                    Intent i = new Intent(ConnectActivity.this, ControlActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(getString(R.string.MQTT_BROKER), broker);
                    bundle.putString(getString(R.string.DEVICE_NAME), deviceName);
                    bundle.putString(getString(R.string.MQTT_CLIENT_ID), clientId);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            });

            return convertView;
        }

        private class ViewHolder {
            private TextView tvName;
            private TextView tvBroker;
            private TextView tvDeviceName;
            private TextView tvClientId;
            private Button btConnect;
        }
    }
}
