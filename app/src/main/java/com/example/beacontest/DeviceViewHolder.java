package com.example.beacontest;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DeviceViewHolder extends RecyclerView.ViewHolder {

    private TextView name;
    private TextView address;
    private LinearLayout layout;
    private TextView connect_status;
    private Button connect_button;

    public DeviceViewHolder(@NonNull View itemView) {
        super(itemView);

        name = itemView.findViewById(R.id.name);
        address = itemView.findViewById(R.id.address);
        layout = itemView.findViewById(R.id.layout);
        connect_status = itemView.findViewById(R.id.connect_status);
        connect_button = itemView.findViewById(R.id.connect_button);

    }

    public TextView getName() {
        return name;
    }

    public void setName(TextView name) {
        this.name = name;
    }

    public TextView getAddress() {
        return address;
    }

    public void setAddress(TextView address) {
        this.address = address;
    }

    public LinearLayout getLayout() {
        return layout;
    }

    public void setLayout(LinearLayout layout) {
        this.layout = layout;
    }

    public TextView getConnect_status() {
        return connect_status;
    }

    public void setConnect_status(TextView connect_status) {
        this.connect_status = connect_status;
    }

    public Button getConnect_button() {
        return connect_button;
    }

    public void setConnect_button(Button connect_button) {
        this.connect_button = connect_button;
    }
}
