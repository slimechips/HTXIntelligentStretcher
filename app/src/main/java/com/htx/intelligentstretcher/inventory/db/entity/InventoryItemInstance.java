package com.htx.intelligentstretcher.inventory.db.entity;

import androidx.room.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(foreignKeys = {
        @ForeignKey(entity = InventoryItem.class,
        parentColumns = "itemId",
        childColumns = "itemId"
        )
}, indices = {@Index(value = "itemId")})
public class InventoryItemInstance {
    @Ignore
    public static final SimpleDateFormat EXPIRY_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");;

    @PrimaryKey
    private int instanceId;

    private int itemId;

    private Date expiry;

    public InventoryItemInstance(int instanceId, int itemId, Date expiry) {
        this.instanceId = instanceId;
        this.itemId = itemId;
        this.expiry = expiry;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry;
    }
}
