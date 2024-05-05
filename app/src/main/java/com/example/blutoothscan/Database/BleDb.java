package com.example.blutoothscan.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.blutoothscan.Dao.BleHistoryDao;
import com.example.blutoothscan.Dao.BleItemsDao;
import com.example.blutoothscan.Model.BleEntity;
import com.example.blutoothscan.Model.BleItemEntity;

@Database(entities = {BleEntity.class, BleItemEntity.class}, version = 1, exportSchema = false)
public abstract class BleDb extends RoomDatabase {

    public abstract BleHistoryDao bleHistoryDao();

    public abstract BleItemsDao bleItemsDao();

    private static volatile BleDb instance;


    public static synchronized BleDb getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            BleDb.class, "BLE_Database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }


}
