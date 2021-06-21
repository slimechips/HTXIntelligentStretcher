package com.htx.intelligentstretcher.inventory.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;
import com.htx.intelligentstretcher.inventory.db.InventoryRepository;
import com.htx.intelligentstretcher.inventory.db.entity.InventoryItem;
import com.htx.intelligentstretcher.inventory.db.entity.InventoryPage;
import com.htx.intelligentstretcher.inventory.db.models.InventoryItemDoneQty;

import java.util.List;

public class InventoryPageViewModel extends AndroidViewModel {

    private final int pageId;
    private final int checkId;
    private final LiveData<InventoryPage> page;
    private final LiveData<List<InventoryItem>> items;
    private final LiveData<List<InventoryItemDoneQty>> itemsDoneQty;

    public InventoryPageViewModel(@NonNull Application application, final InventoryRepository repository,
                                  final int pageId, final int checkId) {
        super(application);
        this.pageId = pageId;
        this.checkId = checkId;
        this.page = repository.loadPageById(pageId);
        this.items = repository.loadInventoryItemsByPageId(pageId);
        this.itemsDoneQty = repository.loadInventoryItemsDoneQtyByPageId(checkId, pageId);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final int pageId;
        private final int checkId;
        private final Application application;
        private final InventoryRepository repository;

        public Factory (@NonNull Application application, InventoryRepository repository,
                        int pageId, int checkId)
        {
            this.pageId = pageId;
            this.checkId = checkId;
            this.application = application;
            this.repository = repository;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create (@NonNull Class<T> modelClass) {
            return (T) new InventoryPageViewModel(application, repository, pageId, checkId);
        }
    }

    public int getPageId() {
        return pageId;
    }

    public LiveData<InventoryPage> getPage() {
        return page;
    }

    public LiveData<List<InventoryItem>> getItems() {
        return items;
    }

    public LiveData<List<InventoryItemDoneQty>> getItemsDoneQty() {
        return itemsDoneQty;
    }
}
