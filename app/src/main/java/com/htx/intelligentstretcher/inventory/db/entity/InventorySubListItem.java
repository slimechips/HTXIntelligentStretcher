package com.htx.intelligentstretcher.inventory.db.entity;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;


@Entity
public class InventorySubListItem {
    @PrimaryKey
    private int subListItemId;

    @Ignore
    private int totalQty;

    @Ignore
    private int totalQtyDone;

    private String title;

    private int parentSubListItemId;

    public Integer parentName;

    public InventorySubListItem(int subListItemId, String title, int parentSubListItemId, @Nullable Integer parentName) {
        this.subListItemId = subListItemId;
        this.title = title;
        this.parentSubListItemId = parentSubListItemId;
        this.parentName = parentName;
        this.totalQty = 0;
        this.totalQtyDone = 0;
    }

    public int getSubListItemId() {
        return subListItemId;
    }

    public void setSubListItemId(int subListItemId) {
        this.subListItemId = subListItemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getParentSubListItemId() {
        return parentSubListItemId;
    }

    public void setParentSubListItemId(int parentSubListItemId) {
        this.parentSubListItemId = parentSubListItemId;
    }

    public Integer getParentName() {
        return parentName;
    }

    public void setParentName(Integer parentName) {
        this.parentName = parentName;
    }

    public int getTotalQty() {
        return totalQty;
    }

    public void setTotalQty(int totalQty) {
        this.totalQty = totalQty;
    }

    public int getTotalQtyDone() {
        return totalQtyDone;
    }

    public void setTotalQtyDone(int totalQtyDone) {
        this.totalQtyDone = totalQtyDone;
    }
}
