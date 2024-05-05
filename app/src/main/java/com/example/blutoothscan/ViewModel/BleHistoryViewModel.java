package com.example.blutoothscan.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.example.blutoothscan.Dao.BleHistoryDao;
import com.example.blutoothscan.Database.BleDb;
import com.example.blutoothscan.Model.BleEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class BleHistoryViewModel extends AndroidViewModel {
    private BleHistoryDao bleHistoryDao;
    private LiveData<List<BleEntity>> allBleEntities;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public BleHistoryViewModel(Application application) {
        super(application);
        BleDb database = BleDb.getInstance(application);
        bleHistoryDao = database.bleHistoryDao();
        allBleEntities = bleHistoryDao.getAllBleEntities();
    }

    public LiveData<List<BleEntity>> getAllBleEntities() {
        return allBleEntities;
    }

    public void saveBleEntity(String itemName, String uid) {
        BleEntity bleEntity = new BleEntity(uid,itemName);
        executorService.execute(() -> {
            bleHistoryDao.insertBleEntity(bleEntity);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
