package com.aishindus.bluetoothexample;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.aishindus.bluetoothexample.R.id.listView;

public class MainActivity extends AppCompatActivity {


    Button bOn, bOff, bVisible;
    BluetoothAdapter ba;
    boolean alreadyThere = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bOn = (Button) findViewById(R.id.buttonOn);
        bOff = (Button) findViewById(R.id.buttonOff);
        bVisible = (Button) findViewById(R.id.buttonVisible);

        ba = BluetoothAdapter.getDefaultAdapter();
        showPairedDevice();

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);


        if (ba != null) {
            Toast.makeText(MainActivity.this, "Bluetooth Supports",
                    Toast.LENGTH_SHORT).show();
            bOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ba.isEnabled()) {
                        String name = ba.getName();
                        String add = ba.getAddress();
                        Toast.makeText(MainActivity.this, "Bluetooth is On \nName: "
                                + name + "\nAddress: " + add, Toast.LENGTH_SHORT).show();
                    } else {
                        //Requesting our android system to turn on the bluetooth.
                        Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(i, 1);
                    }
                }
            });

            bOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ba.disable();
                    Toast.makeText(MainActivity.this, "Bluetooth is Off", Toast.LENGTH_SHORT).show();
                }
            });

            bVisible.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(i, 2);
                    scanDevices();

                }
            });
        } else
            Toast.makeText(MainActivity.this, "Bluetooth Not Supported", Toast.LENGTH_SHORT).show();
    }

    void showPairedDevice() {
        if (ba.isEnabled()) {
            List<String> devicesAdded = new ArrayList<>();
            Set<BluetoothDevice> device = ba.getBondedDevices();
            for (BluetoothDevice d : device) {
                devicesAdded.add(
                        "Device : " + d + "\n" +
                                "Device Name : " + d.getName() + "\n" +
                                "Device Address : " + d.getAddress());
            }

            ArrayAdapter<String> ad = new ArrayAdapter<>(MainActivity.this,
                    android.R.layout.simple_list_item_1, devicesAdded);

            if(!alreadyThere) {
                ListView lv = (ListView) findViewById(listView);
                LayoutInflater layoutinflater = getLayoutInflater();
                ViewGroup header = (ViewGroup) layoutinflater.inflate(R.layout.list_header, lv, false);
                lv.addHeaderView(header);
                lv.setAdapter(ad);
                alreadyThere = true;
            }
        }

    }

    void scanDevices() {
        ba.startDiscovery();
        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    List<String> devicesAdded = new ArrayList<>();
                    devicesAdded.add(
                            "Device : " + device.getName() + "\n" +
                                    "Device Address : " + device.getAddress());
                    Toast.makeText(MainActivity.this,"Device : " + device.getName() +
                            "Device Address : " + device.getAddress(),Toast.LENGTH_SHORT).show();
                    ArrayAdapter<String> ad = new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_list_item_1, devicesAdded);

                    ListView lv = (ListView) findViewById(listView);
                    LayoutInflater layoutinflater = getLayoutInflater();
                    ViewGroup header = (ViewGroup) layoutinflater.inflate(R.layout.list_header, lv, false);
                    lv.addHeaderView(header);
                    lv.setAdapter(ad);
                }
            }
        };

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }


/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                scanDevices();
            }
        }
    }*/
}
