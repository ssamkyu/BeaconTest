package com.example.beacontest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Test2Activity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> bluetoothDevices;

    private int pairedDeviceCount;

    private static final int REQUEST_ENABLE_BT = 2;

    //private static final int REQUEST_ENABLE_BT = 3;
    public BluetoothAdapter mBluetoothAdapter = null;
    Set<BluetoothDevice> mDevices;
    int mPairedDeviceCount;
    BluetoothDevice mRemoteDevice;
    BluetoothSocket mSocket;
    InputStream mInputStream;
    OutputStream mOutputStream;
    Thread mWorkerThread;
    int readBufferPositon;      //버퍼 내 수신 문자 저장 위치
    byte[] readBuffer;      //수신 버퍼
    byte mDelimiter = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null){
            //장치가 블루투스를 지원하지 않음

        } else {

            if(!bluetoothAdapter.isEnabled()){
                //지원은 하지만 비활성화 상태
                //활성화 하고 싶으면 동의 요청
                Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(bluetoothIntent, REQUEST_ENABLE_BT);
            } else {
                selectDevice();
            }
        }


        IntentFilter stateFilter = new IntentFilter();
        stateFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED); //BluetoothAdapter.ACTION_STATE_CHANGED : 블루투스 상태변화 액션
        stateFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        stateFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED); //연결 확인
        stateFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED); //연결 끊김 확인
        stateFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        stateFilter.addAction(BluetoothDevice.ACTION_FOUND);    //기기 검색됨
        stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);   //기기 검색 시작
        stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);  //기기 검색 종료
        stateFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        registerReceiver(bluetoothStateReceiver, stateFilter);

    }

    private void selectDevice(){

        bluetoothDevices = bluetoothAdapter.getBondedDevices();

        pairedDeviceCount = bluetoothDevices.size();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Device");

        final List<String> listItem = new ArrayList<String>();
        for(BluetoothDevice device: bluetoothDevices){
            Log.e("bluetoothDevice", device.toString());
            Log.e("bluetoothDevice", device.getAddress());
            Log.e("bluetoothDevice", device.getName());
            Log.e("bluetoothDevice", device.getUuids().toString());

            listItem.add(device.getName());
        }

        if(listItem.size() == 0){
            Log.e("bluetooth", "No device");

        } else {
            Log.e("bluetooth2", "find device");
            listItem.add("cancel");
        }

        final CharSequence[] item = listItem.toArray(new CharSequence[listItem.size()]);
        builder.setItems(item, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Dialog dialog1 = (Dialog) dialog;

                if(which == listItem.size() -1){
                    Toast.makeText(dialog1.getContext(), "choose cancel", Toast.LENGTH_LONG).show();
                } else {
                    connectToSelectedDevice(item[which].toString());
                }

            }
        });

        builder.setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();

        bluetoothAdapter.startDiscovery();

    }

    private void connectToSelectedDevice(final String selectedDeviceName){

        Log.e("test2", "connectToSelectDevcide");

    }

    private BluetoothDevice getDeviceFromBondedList(String name){
        BluetoothDevice selectedDevice = null;

        bluetoothDevices = bluetoothAdapter.getBondedDevices();
        for(BluetoothDevice device : bluetoothDevices){
            if(name.equals(device.getName())){
                selectedDevice = device;
                break;
            }
        }

        return selectedDevice;
    }

    BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            Toast.makeText(getApplicationContext(),"받은액션: " + action, Toast.LENGTH_SHORT).show();

            Log.d("Bluetooth action", action);

            final BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String name = null;
            if(device != null){
                name = device.getName();
            }

            switch (action){

                case BluetoothAdapter.ACTION_STATE_CHANGED://블루투스 연결상태 변경
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    switch (state){
                        case BluetoothAdapter.STATE_OFF:

                            break;

                        case BluetoothAdapter.STATE_TURNING_OFF:

                            break;


                        case BluetoothAdapter.STATE_ON:

                            break;

                        case BluetoothAdapter.STATE_TURNING_ON:
                            break;
                    }

                    break;

                case BluetoothDevice.ACTION_ACL_CONNECTED: //블루투스 기기 연결

                    break;


                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:

                    break;

                case BluetoothDevice.ACTION_ACL_DISCONNECTED:

                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:

                    break;


                case BluetoothDevice.ACTION_FOUND:

                    String device_name = device.getName();
                    String device_address = device.getAddress();
                    if(device_name != null && device_name.length()>4){
                        Log.d("Bluetooth nane: ", device_name);
                        Log.d("bluetooth address: ", device_address);
                        if(device_name.substring(0,3).equals("GSM")){
                            bluetoothDevices.add(device);
                        }

                    };

                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED://블루투스 기기검색 종료

                    Log.d("bluetooth", "Call Discovery finished");

                    //기기 연결
                    break;

                case BluetoothDevice.ACTION_PAIRING_REQUEST:

                    break;

            }


        }
    };

}
