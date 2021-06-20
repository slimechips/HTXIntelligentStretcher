package com.htx.intelligentstretcher.inventory.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;
import com.htx.intelligentstretcher.inventory.db.InventoryRepository;
import com.htx.intelligentstretcher.inventory.db.models.InventorySubListItemDoneQty;
import com.htx.intelligentstretcher.inventory.db.models.InventorySubListItemTotalQty;
import com.htx.intelligentstretcher.inventory.db.models.InventorySubListItemWithPage;

import java.util.List;

public class InventorySubListItemViewModel extends AndroidViewModel {

    private final int subListItemId;
    private final int checkId;
    private final LiveData<InventorySubListItemWithPage> subListItemWithPage;
    private final LiveData<List<InventorySubListItemWithPage>> childrenWithPage;
    private final LiveData<List<InventorySubListItemDoneQty>> totalDoneQtys;
    private final LiveData<List<InventorySubListItemTotalQty>> totalQtys;

    public InventorySubListItemViewModel(@NonNull Application application, InventoryRepository repository,
                                         final int checkId, final int subListItemId) {
        super(application);
        this.checkId = checkId;
        this.subListItemId = subListItemId;
        this.subListItemWithPage = repository.loadSubListItemWithPage(subListItemId);
        this.childrenWithPage = repository.loadSubListItemsWithPageByParentSubListId(subListItemId);
        this.totalDoneQtys = repository.loadRecordCountByCheckIdAndSubListItemId(checkId, subListItemId);
        this.totalQtys = repository.loadSubListTotalQtyBySubListId(subListItemId);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Application application;
        private final int subListItemId;
        private final int checkId;
        private final InventoryRepository repository;

        public Factory(@NonNull Application application, InventoryRepository repository,
                       int checkId, int subListItemId)
        {
            this.application = application;
            this.checkId = checkId;
            this.subListItemId = subListItemId;
            this.repository = repository;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new InventorySubListItemViewModel(application, repository,
                    checkId, subListItemId);
        }
    }

    public LiveData<InventorySubListItemWithPage> getInventorySubListItemWithPage() {
        return subListItemWithPage;
    }



    public LiveData<List<InventorySubListItemWithPage>> getChildrenWithPage() {
        return childrenWithPage;
    }

    public LiveData<List<InventorySubListItemDoneQty>> getTotalDoneQtys() {
        return totalDoneQtys;
    }

    public LiveData<List<InventorySubListItemTotalQty>> getTotalQtys() {
        return totalQtys;
    }

    public int getSubListItemId() {
        return subListItemId;
    }



}
