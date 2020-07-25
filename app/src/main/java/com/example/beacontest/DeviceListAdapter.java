package com.example.beacontest;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DeviceListAdapter extends RecyclerView.Adapter {

    //context, R.layout.device_adpter_view, deviceList

    private ArrayList<BluetoothDevice> itemList = new ArrayList<>();
    private Context context;
    private int layout;
    private final String TAG = "DeviceListAdapter";

    private Boolean leTrue = false;

    public DeviceListAdapter() {
    }

    public DeviceListAdapter(Context context, int layout, ArrayList<BluetoothDevice> itemList, Boolean leTrue) {
        this.itemList = itemList;
        this.context = context;
        this.layout = layout;
        this.leTrue = leTrue;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View listView;

        listView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        return new DeviceViewHolder(listView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final BluetoothDevice device = getItem(position);

        Log.e(TAG, device.getAddress());
        if(device != null){
            try {
                Log.e(TAG, device.getUuids().toString());
            } catch (Exception e){
                e.printStackTrace();
            }
            try {
                Log.e(TAG, device.getClass().getCanonicalName());
            } catch (Exception e){
                e.printStackTrace();
            }
            try {
                Log.e(TAG, device.getClass().getSimpleName());
            } catch (Exception e){
                e.printStackTrace();
            }
            try {
                Log.e(TAG, device.getClass().getName());
            } catch (Exception e){
                e.printStackTrace();
            }
            try {
                Log.e(TAG, device.getClass().getAnnotations().toString());
            } catch (Exception e){
                e.printStackTrace();
            }
            try {
                Log.e(TAG, device.getClass().getFields().toString());
            } catch (Exception e){
                e.printStackTrace();
            }
        }


        LinearLayout layout = ((DeviceViewHolder)holder).getLayout();
        TextView name = ((DeviceViewHolder)holder).getName();
        TextView address = ((DeviceViewHolder)holder).getAddress();
        TextView connect_status = ((DeviceViewHolder)holder).getConnect_status();
        Button connect_button = ((DeviceViewHolder)holder).getConnect_button();

        name.setText(device.getName());
        address.setText(device.getAddress());

        connect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("connect", "start");


            }
        });




    }


    private BluetoothDevice getItem(int position){
        return itemList.get(position);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
