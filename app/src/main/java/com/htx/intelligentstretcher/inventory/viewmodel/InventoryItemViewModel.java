package com.htx.intelligentstretcher.inventory.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.htx.intelligentstretcher.inventory.InventoryItemDialog;
import com.htx.intelligentstretcher.inventory.db.InventoryRepository;
import com.htx.intelligentstretcher.inventory.db.entity.InventoryItem;
import com.htx.intelligentstretcher.inventory.db.models.InventoryItemDoneQty;
import com.htx.intelligentstretcher.inventory.db.models.InventoryItemRecordAndInstance;

import java.util.List;

public class InventoryItemViewModel extends AndroidViewModel implements InventoryItemDialog.HistoryClickListener {
    private final InventoryRepository repository;
    private final int checkId;
    private final int itemId;
    private int historyRecordId;
    private final LiveData<InventoryItem> item;
    private final LiveData<InventoryItemDoneQty> itemDoneQty;
    private final LiveData<List<InventoryItemRecordAndInstance>> currentRecord;
    private LiveData<List<InventoryItemRecordAndInstance>> history;


    public InventoryItemViewModel(@NonNull Application application, final InventoryRepository repository,
                                  final int checkId,
                                  final int itemId) {
        super(application);
        this.repository = repository;
        this.checkId = checkId;
        this.itemId = itemId;
        this.item = repository.loadInventoryItemByItemId(itemId);
        this.itemDoneQty = repository.loadInventoryItemDontQtyByCheckAndItemId(checkId, itemId);
        this.currentRecord = repository.loadInventoryItemRecordsByCheckAndItemId(checkId, itemId);
        this.historyRecordId = checkId - 1;
        this.history = repository.loadInventoryItemRecordsByCheckAndItemId(historyRecordId, itemId);
    }

    @Override
    public void onHistoryClick(int change) {
        this.historyRecordId += change;
        this.history = repository.loadInventoryItemRecordsByCheckAndItemId(historyRecordId, itemId);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final int checkId;
        private final int itemId;
        private final Application application;
        private final InventoryRepository repository;

        public Factory(Application application, InventoryRepository repository, int checkId,
                       int itemId) {
            this.checkId = checkId;
            this.itemId = itemId;
            this.application = application;
            this.repository = repository;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new InventoryItemViewModel(application, repository, checkId, itemId);
        }
    }

    public int getItemId() {
        return itemId;
    }

    public LiveData<InventoryItem> getItem() {
        return item;
    }

    public LiveData<List<InventoryItemRecordAndInstance>> getCurrentRecord() {
        return currentRecord;
    }

    public LiveData<List<InventoryItemRecordAndInstance>> getHistory() {
        return history;
    }

    public LiveData<InventoryItemDoneQty> getItemDoneQty() {
        return itemDoneQty;
    }
}
