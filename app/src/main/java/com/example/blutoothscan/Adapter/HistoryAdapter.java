package com.example.blutoothscan.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.blutoothscan.Database.BleDb;
import com.example.blutoothscan.Model.BleItemEntity;
import com.example.blutoothscan.R;

import java.util.List;
import java.util.Random;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<BleItemEntity> bleItemsList;
    private Context context;

    public HistoryAdapter(List<BleItemEntity> bleItemsList, Context context) {
        this.bleItemsList = bleItemsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ble_scan_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BleItemEntity bleItem = bleItemsList.get(position);
        int rssi = bleItem.getRssi();

        String deviceName = bleItem.getDeviceName();
        if (deviceName != null && !deviceName.isEmpty()) {
            holder.tvName.setText(deviceName);
        } else {
            holder.tvName.setText("N/A");
        }

        holder.tvRssi.setText(bleItem.getRssi() + "\ndBm");
        holder.tvmacId.setText("MAC ID: " + bleItem.getMacAddress());

        // Random color
        setRandomBackgroundColor(holder);

      /*  holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItemFromDatabase(context, bleItem);
            }
        });*/
        calculateDistanceAsync(holder, rssi);

    }

    @Override
    public int getItemCount() {
        return bleItemsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDistance, tvmacId, tvDelete, tvRssi;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvmacId = itemView.findViewById(R.id.tvmacId);
            tvRssi = itemView.findViewById(R.id.tvRssi);
            tvDelete = itemView.findViewById(R.id.tvDelete);
        }

    }

    private void setRandomBackgroundColor(ViewHolder holder) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setSize(102, 100); // Set size of the oval

        int[] colors = {Color.LTGRAY, Color.BLUE, Color.RED, Color.GRAY, Color.MAGENTA};
        Random random = new Random();
        int randomColor = colors[random.nextInt(colors.length)];

        drawable.setColor(randomColor);
        holder.tvRssi.setBackground(drawable);
    }

    private void deleteItemFromDatabase(Context context, BleItemEntity bleItem) {
        BleDb db = BleDb.getInstance(context);

        new Thread(new Runnable() {
            @Override
            public void run() {
                db.bleItemsDao().deleteBleItem(bleItem);
            }
        }).start();
    }

    @SuppressLint("StaticFieldLeak")
    private void calculateDistanceAsync(ViewHolder holder, int rssi) {
        new AsyncTask<Integer, Void, Double>() {
            @Override
            protected Double doInBackground(Integer... params) {
                return calculateDistance(params[0]);
            }

            @Override
            protected void onPostExecute(Double distance) {
                if (distance >= 0) {
                    holder.tvDistance.setText("Aprx Distance: " + String.format("%.2f", distance) + " m");
                } else {
                    holder.tvDistance.setText("Aprx Distance: N/A");
                }
            }
        }.execute(rssi);
    }

    private double calculateDistance(int rssi) {
        double txPower = -59; // Example value for RSSI at 1 meter
        double signalPropagationExponent = 2.0; // Example path loss exponent (adjust as needed)

        // Calculate the signal loss (difference between txPower and RSSI)
        double signalLoss = txPower - rssi;

        // Calculate the estimated distance in meters using the log-distance path loss model
        double distance = Math.pow(10.0, (signalLoss) / (10.0 * signalPropagationExponent));

        return distance;
    }


}

