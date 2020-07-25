package com.example.beacontest;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easy_bluetooth.BluetoothFunction;
import com.example.easy_bluetooth.Listener.BluetoothDeviceListner;;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;


public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    protected static final String TAG = "MainActivity222";
    private BeaconManager beaconManager;


    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private Button btn_Connect;
    private TextView txt_Result;

    private Button test2;
    private Button test3;
    private Button test4;
    private Button test5;

    private BluetoothService btService = null;
    private BluetoothAdapter bluetoothAdapter;

    private ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
    private DeviceListAdapter deviceListAdapter;
    private RecyclerView recyclerView;

    private LinearLayoutManager layoutManager;

    private BluetoothLeScanner bluetoothLeScanner;

    private boolean mScanning;
    private Handler handler;

    private static final long SCAN_PERIOD = 10000;

    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };

    private BluetoothFunction bluetootheFunction;

    private BluetoothManager manager;
    private BluetoothAdapter leAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "블루투스를 지원하지 않습니", Toast.LENGTH_SHORT).show();
        }

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.bind(this);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            manager = (BluetoothManager) getSystemService(getApplicationContext().BLUETOOTH_SERVICE);
            leAdapter = manager.getAdapter();
        }


        recyclerView = findViewById(R.id.recyclerview);

        layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        btn_Connect = findViewById(R.id.btn_Connect);
        txt_Result = findViewById(R.id.txt_Result);

        test2 = findViewById(R.id.test2);
        test2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), Test2Activity.class);
//                startActivity(intent);
                scanLeDevice(true);
            }
        });

        test3 = findViewById(R.id.test3);
        test3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(TAG, "clicked");
                endableDisableBT();

            }
        });

        test4 = findViewById(R.id.test4);
        test4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnEnableDisable_Discoverable(test4);

            }
        });

        test5 = findViewById(R.id.test5);
        test5.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                btn_Discover(test5);

            }
        });


        btn_Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(btService.getDeviceState()){
                    btService.enableBluetooth();
                } else {
                    finish();
                }

            }
        });

        if(btService == null){
            btService = new BluetoothService(this, mHandler);
        }

    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        leAdapter.stopLeScan(leScanCallback);
                    }
                }
            }, SCAN_PERIOD);

            mScanning = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                leAdapter.startLeScan(leScanCallback);
            }
        } else {
            mScanning = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                leAdapter.stopLeScan(leScanCallback);
            }
        }

    }

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    Log.e("device", "device:"+device.getAddress());
//                    leDeviceListAdapter.addDevice(device);
//                    leDeviceListAdapter.notifyDataSetChanged();

                    deviceList = uniqueArray(deviceList, device);
                    Log.e(TAG, "onreceive"+device.getName() + ":"+ device.getAddress());
                    deviceListAdapter = new DeviceListAdapter(getApplicationContext(), R.layout.device_adpter_view, deviceList, true);
                    recyclerView.setAdapter(deviceListAdapter);
                }
            });
        }
    };

    private void btnEnableDisable_Discoverable(View view){
        Log.e(TAG , "btn Enable Dsiable discoverabe");
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(broadcastReciver2, intentFilter);
    }


    private void endableDisableBT(){
        if(bluetoothAdapter == null){
            Log.e(TAG, "bluetoothAdapter null");
        }

        if(!bluetoothAdapter.isEnabled()){

            Log.e(TAG, "isEnable false");

            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(broadcastReciver, BTIntent);


        }

        if(bluetoothAdapter.isEnabled()){

            Log.e(TAG, "is endable true");

            bluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(broadcastReciver, BTIntent);
        }

    }

    private final BroadcastReceiver broadcastReciver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "broadcastReciever");
            if (action.equals(bluetoothAdapter.ACTION_STATE_CHANGED)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                Log.e(TAG, "bluetoothAdapter.Action_State_changed");

                final int status = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, bluetoothAdapter.ERROR);

                switch (status){

                    case BluetoothAdapter.STATE_OFF:

                        Log.e(TAG, "bluetoothAdapter.STATE_OFF");

                        break;

                    case BluetoothAdapter.STATE_TURNING_OFF:

                        Log.e(TAG, "bluetoothAdapter.STATE_Turning_off");

                        break;

                    case BluetoothAdapter.STATE_ON:

                        Log.e(TAG, "bluetoothAdapter.STATE_On");

                        break;

                    case BluetoothAdapter.STATE_TURNING_ON:

                        Log.e(TAG, "bluetoothAdapter.STATE_Turning On");

                        break;
                }

            }
        }
    };


    private final BroadcastReceiver broadcastReciver2 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "broadcastReciever");
            if (action.equals(bluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                Log.e(TAG, "bluetoothAdapter.Action_State_changed");

                final int status = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, bluetoothAdapter.ERROR);

                switch (status){

                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:

                        Log.e(TAG, "bluetoothAdapter.SCAN_MODE_CONNECTAELE_DISCOVERABLE");

                        break;

                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:

                        Log.e(TAG, "bluetoothAdapter.SCAN_MODE_CONNECTABLE");

                        break;

                    case BluetoothAdapter.SCAN_MODE_NONE:

                        Log.e(TAG, "bluetoothAdapter.SCAN_MODE_NONE");

                        break;

                    case BluetoothAdapter.STATE_CONNECTING:

                        Log.e(TAG, "bluetoothAdapter.STATE_CONNECTING");

                        break;

                    case BluetoothAdapter.STATE_CONNECTED:

                        Log.e(TAG, "bluetoothAdapter.STATE_CONNECTED");

                        break;

                }

            }
        }
    };



    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        beaconManager.unbind(this);
        if(broadcastReciver.isInitialStickyBroadcast()){
            unregisterReceiver(broadcastReciver);
        }
        if(broadcastReciver2.isInitialStickyBroadcast()){
            unregisterReceiver(broadcastReciver2);
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_ENABLE_BT:
                if(requestCode == Activity.RESULT_OK){

                    btService.scanDevice();

                } else {
                    Log.d(TAG, "Bluetooth is not enable");
                }
                break;

            case REQUEST_CONNECT_DEVICE:
                if(resultCode == Activity.RESULT_OK){

                }

        }
    }

    @Override
    public void onBeaconServiceConnect() {

        beaconManager.removeAllMonitorNotifiers();
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw an beacon for the first time!");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see an beacon");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: "+state);
            }

        });

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.i(TAG, "The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
                }
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {    }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void btn_Discover(View view) {

        bluetootheFunction = new BluetoothFunction(this, getApplicationContext(), bluetoothAdapter);
        bluetootheFunction.bluetoothDiscover(new BluetoothDeviceListner() {
            @Override
            public void onBluetoothDevice(BluetoothDevice bluetoothDevice) {

                Log.e("device", bluetoothDevice.toString());
                //Log.e("device1", bluetoothDevice.getName());
                Log.e("device2", bluetoothDevice.getAddress().toString());

                deviceList = uniqueArray(deviceList, bluetoothDevice);
                Log.e(TAG, "onreceive"+bluetoothDevice.getName() + ":"+ bluetoothDevice.getAddress());
                deviceListAdapter = new DeviceListAdapter(getApplicationContext(), R.layout.device_adpter_view, deviceList, false);
                recyclerView.setAdapter(deviceListAdapter);

            }
        });

    }



    private ArrayList<BluetoothDevice> uniqueArray(ArrayList<BluetoothDevice> deviceList, BluetoothDevice input){

        Boolean exist = false;

        for(BluetoothDevice device:deviceList){

            if(device.getAddress().equals(input.getAddress())){
                exist = true;
            }
        }

        if(!exist){
            deviceList.add(input);
        }

        return deviceList;

    }




}
