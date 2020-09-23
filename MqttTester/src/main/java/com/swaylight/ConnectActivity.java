package com.swaylight;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.JsonReader;
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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.swaylight.library.SLMqttDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class ConnectActivity extends AppCompatActivity {

    final String tag = "ConnectActivity";
    private EditText etName;
    private EditText etBroker;
    private EditText etDeviceName;
    private EditText etClientId;
    private Button btConnect;
    private ListView lvMqtt;
    private LayoutInflater inflater;
    private MqttAdapter adapter;
    private ArrayList<SLMqttDetail> mqttList = new ArrayList<>();
    private JsonObject mqttObj = new JsonObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        etName = findViewById(R.id.name_edittext);
        etBroker = findViewById(R.id.broker_edittext);
        etDeviceName = findViewById(R.id.device_name_edittext);
        etClientId = findViewById(R.id.client_id_edittext);
        btConnect = findViewById(R.id.connect_button);
        inflater = LayoutInflater.from(this);
        lvMqtt = findViewById(R.id.mqtt_listview);

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
                        mqttObj.remove(detail.getName());
                        writeToFile(mqttObj.toString());
                        Log.d(tag, "mqttObj:" + mqttObj.toString());
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

        Log.d(tag, "getFilesDir():" + getFilesDir().toString());
        Log.d(tag, "readFromFile:" + readFromFile());
        adapter = new MqttAdapter();
        lvMqtt.setAdapter(adapter);
        updateList();

        btConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String broker = etBroker.getText().toString().trim();
                String deviceName = etDeviceName.getText().toString().trim();
                String clientId = etClientId.getText().toString().trim();
                if(name.isEmpty() || broker.isEmpty() || deviceName.isEmpty() || clientId.isEmpty()) {
                    Log.d(tag, "detail field empty!");
                }else {
                    generateJsonObj(new SLMqttDetail(etName.getText().toString(),
                            etBroker.getText().toString(),
                            etDeviceName.getText().toString(),
                            etClientId.getText().toString()));
                    writeToFile(mqttObj.toString());
                    readFromFile();
                    updateList();
                    Intent i = new Intent(ConnectActivity.this, ControlActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(getString(R.string.MQTT_BROKER), String.valueOf(etBroker.getText()));
                    bundle.putString(getString(R.string.DEVICE_NAME), String.valueOf(etDeviceName.getText()));
                    bundle.putString(getString(R.string.MQTT_CLIENT_ID), String.valueOf(etClientId.getText()));
                    i.putExtras(bundle);
                    startActivity(i);
                }
            }
        });
    }

    private void generateJsonObj(SLMqttDetail mqttDetail) {
        JsonObject detail = new JsonObject();
        mqttObj.add(mqttDetail.getName(), detail);
        detail.addProperty(SLMqttDetail.BROKER, mqttDetail.getBroker());
        detail.addProperty(SLMqttDetail.DEVICE_NAME, mqttDetail.getDeviceName());
        detail.addProperty(SLMqttDetail.CLIENT_ID, mqttDetail.getClientId());
    }

    private synchronized void updateList() {
        if(readFromFile().isEmpty()) {
            mqttList.clear();
            return;
        }
        mqttObj = (JsonObject) (new JsonParser().parse(readFromFile()));
        mqttList.clear();
        for (String key: mqttObj.keySet()) {
            Log.d(tag, "name:" + key + ", obj:" + mqttObj.get(key).toString());
            JsonObject temp = (JsonObject) mqttObj.get(key);
            mqttList.add(new SLMqttDetail(key,
                    temp.get(SLMqttDetail.BROKER).toString().replace("\"", ""),
                    temp.get(SLMqttDetail.DEVICE_NAME).toString().replace("\"", ""),
                    temp.get(SLMqttDetail.CLIENT_ID).toString().replace("\"", "")));
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("mqtt_list.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = getApplicationContext().openFileInput("mqtt_list.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
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
            ViewHolder holder;
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.mqtt_list_item, parent, false);
                holder = new ViewHolder();
                holder.tvName = convertView.findViewById(R.id.mqtt_name_textview);
                holder.tvBroker = convertView.findViewById(R.id.broker_textview);
                holder.tvDeviceName = convertView.findViewById(R.id.device_name_textview);
                holder.tvClientId = convertView.findViewById(R.id.cliet_id_textview);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            SLMqttDetail detail = mqttList.get(position);
            holder.tvName.setText(detail.getName());
            holder.tvBroker.setText(detail.getBroker());
            holder.tvDeviceName.setText(detail.getDeviceName());
            holder.tvClientId.setText(detail.getClientId());

            return convertView;
        }

        private class ViewHolder {
            private TextView tvName;
            private TextView tvBroker;
            private TextView tvDeviceName;
            private TextView tvClientId;
        }
    }
}
