package com.htx.intelligentstretcher.inventory.db;

import android.util.Log;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.htx.intelligentstretcher.inventory.db.entity.*;
import com.htx.intelligentstretcher.inventory.db.models.*;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryRepository {
    private static final String TAG = "InventoryRepo";
    private static InventoryRepository instance;
    private final InventoryDatabase database;


    private InventoryRepository(final InventoryDatabase database) {
        this.database = database;
    }

    public static InventoryRepository getInstance(final InventoryDatabase database) {
        if (instance == null) {
            synchronized (InventoryRepository.class) {
                if (instance == null) {
                    instance = new InventoryRepository(database);
                }
            }
        }
        return instance;
    }

    public void insertCheck(InventoryCheck check) {
        InventoryDatabase.getDatabaseWriteExecutor().execute(() ->
                database.inventoryCheckDao().insertData(check)
        );
    }

    public LiveData<List<InventorySubListItem>> loadInventorySubListItemsByParentName(int parentName) {
        return database.inventorySubListItemDao().loadSubListItemsByParentName(parentName);
    }

    public int getInventoryItemTotalQtyBySubListId(int subListItemId) {
        return getInventoryItemIdsBySubListItemId(subListItemId)
                .stream()
                .map(this::getInventoryItemReqQty)
                .mapToInt(Integer::intValue)
                .sum();
    }

    public List<Integer> getInventoryItemIdsBySubListItemId(int subListItemId) {
        return getInventoryItemIdsBySubListItemIds(Collections.singletonList(subListItemId));
    }

    public List<Integer> getInventoryItemIdsBySubListItemIds(List<Integer> subListItemIds) {
        return database
                .inventoryPageDao()
                .getPagesWithItemsBySubListItemIds(getFlatChildrenOnlySubListItemIds(subListItemIds))
                .stream()
                .flatMap(inventoryPageWithItems -> inventoryPageWithItems.items
                        .stream()
                        .map(InventoryItem::getItemId))
                .collect(Collectors.toList());
    }

    public List<Integer> getFlatChildrenOnlySubListItemIds(List<Integer> subListItemIds) {
        return subListItemIds
                .stream()
                .flatMap(subListItemId -> getChildrenSubListIdsFromSubListId(subListItemId).stream())
                .collect(Collectors.toList());
    }

    public List<Integer> getChildrenSubListIdsFromSubListId(int subListItemId) {
        List<Integer> children = getSubListItemWithSubListsItems(subListItemId)
                .subListItemsWithPage
                .stream()
                .map(inventorySubListItemWithPage -> inventorySubListItemWithPage.subListItem.getSubListItemId())
                .collect(Collectors.toList());
        if (children.size() == 0) children.add(subListItemId);
        return children;
    }

    public LiveData<InventorySubListItem> loadSubListItem(int subListItemId) {
        return database.inventorySubListItemDao().loadSubListItem(subListItemId);
    }

    public LiveData<InventorySubListItemWithPage> loadSubListItemWithPage(int subListItemId) {
        return database.inventorySubListItemDao().loadSubListItemWithPage(subListItemId);
    }

    public InventorySubListItemWithSubListsAndPage getSubListItemWithSubListsItems(int subListItemId) {
        return database.inventorySubListItemDao().getInventorySubListItemWithSubListItems(subListItemId);
    }

    public LiveData<List<InventorySubListItem>> loadInventorySubListItemsByParentSubListId(int parentSubListId) {
        return database.inventorySubListItemDao().loadInventorySubListItemsByParentSubListId(parentSubListId);
    }

    public LiveData<List<InventorySubListItemWithPage>> loadSubListItemsWithPageByParentSubListId(int parentSubListId) {
        return database.inventorySubListItemDao().loadSubListItemsWithPageByParentSubListId(parentSubListId);
    }

    public LiveData<InventoryPage> loadPageById(int pageId) {
        return database.inventoryPageDao().loadPageById(pageId);
    }

    public LiveData<InventoryItem> loadInventoryItemByItemId(int itemId) {
        return database.inventoryItemDao().loadInventoryItemByItemId(itemId);
    }

    public LiveData<List<InventoryItem>> loadInventoryItemsByPageId(int pageId) {
        return database.inventoryItemDao().loadInventoryItemByPageId(pageId);
    }

    public int getInventoryItemReqQty(int itemId) {
        return database.inventoryItemDao().getInventoryItemReqQty(itemId);
    }

    public LiveData<List<InventoryItemRecordAndInstance>> loadInventoryItemRecordsByCheckAndItemId(int checkId, int itemId) {
        return database.inventoryItemRecordDao().loadItemRecordsByCheckAndItemId(checkId, itemId);
    }

    public void insertJsonItemInstanceAndRecord(int checkId, String jsonStr, @Nullable String notes) {
        try {
            InventoryItemInstance instance = new Gson().fromJson(jsonStr, InventoryItemInstanceQR.class)
                                                        .toInstance();
            InventoryItemRecord record = new InventoryItemRecord(
                    checkId,
                    instance.getItemId(),
                    instance.getInstanceId(),
                    new Date(),
                    notes);
            InventoryDatabase.getDatabaseWriteExecutor().execute(() -> {
                database.inventoryItemInstanceDao().insertData(instance);
                database.inventoryItemRecordDao().insertData(record);
                Log.i(TAG, String.format("Inserted Instance %d", instance.getInstanceId()));
            });
        } catch (JsonSyntaxException|ParseException ex) {
            Log.e(TAG, String.format("Failed to deserialise %s", jsonStr));
        }
    }

    public LiveData<List<InventorySubListItemDoneQty>> loadRecordCountByCheckIdAndSubListItemId(int checkId, int subListItemId) {
        return database.inventoryItemRecordDao().loadRecordCountByCheckIdAndParentSubListItemId(checkId, subListItemId);
    }

    public LiveData<List<InventorySubListItemTotalQty>> loadSubListTotalQtyBySubListId(int subListItemId) {
        return database.inventorySubListItemDao().loadSubListTotalQtyBySubListId(subListItemId);
    }

    public LiveData<List<InventoryItemDoneQty>> loadInventoryItemsDoneQtyByPageId(int checkId, int pageId) {
        return database.inventoryItemDao().loadInventoryItemsDoneQtyByCheckAndPageId(checkId, pageId);
    }

    public LiveData<InventoryItemDoneQty> loadInventoryItemDontQtyByCheckAndItemId(int checkId, int itemId) {
        return database.inventoryItemDao().loadInventoryItemDontQtyByCheckAndItemId(checkId, itemId);
    }
}
