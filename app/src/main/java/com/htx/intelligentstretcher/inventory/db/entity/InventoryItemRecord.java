package com.htx.intelligentstretcher.inventory.db.entity;

import androidx.room.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(primaryKeys = { "checkId", "linkedInstanceId" },
        foreignKeys = {
        @ForeignKey(entity = InventoryCheck.class,
                parentColumns = "checkId",
                childColumns = "checkId",
                onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = InventoryItem.class,
                parentColumns = "itemId",
                childColumns = "inventoryItemId",
                onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = InventoryItemInstance.class,
                parentColumns = "instanceId",
                childColumns = "linkedInstanceId")
}, indices = {@Index(value = "checkId"), @Index(value = "inventoryItemId"), @Index(value = "linkedInstanceId")})
public class InventoryItemRecord {
    @Ignore
    public static final SimpleDateFormat INSTANCE_CHECK_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private int checkId;

    private int inventoryItemId;

    private int linkedInstanceId;

    private Date instanceCheckDate;

    private String notes;

    public InventoryItemRecord(int checkId, int inventoryItemId, int linkedInstanceId, Date instanceCheckDate,
                               String notes) {
        this.checkId = checkId;
        this.inventoryItemId = inventoryItemId;
        this.linkedInstanceId = linkedInstanceId;
        this.instanceCheckDate = instanceCheckDate;
        this.notes = notes;
    }

    public int getCheckId() {
        return checkId;
    }

    public void setCheckId(int checkId) {
        this.checkId = checkId;
    }

    public int getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(int inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }

    public int getLinkedInstanceId() {
        return linkedInstanceId;
    }

    public void setLinkedInstanceId(int linkedInstanceId) {
        this.linkedInstanceId = linkedInstanceId;
    }

    public Date getInstanceCheckDate() {
        return instanceCheckDate;
    }

    public void setInstanceCheckDate(Date instanceCheckDate) {
        this.instanceCheckDate = instanceCheckDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
