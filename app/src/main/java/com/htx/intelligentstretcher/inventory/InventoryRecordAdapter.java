package com.htx.intelligentstretcher.inventory;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.htx.intelligentstretcher.R;
import com.htx.intelligentstretcher.inventory.db.entity.InventoryItemInstance;
import com.htx.intelligentstretcher.inventory.db.entity.InventoryItemRecord;
import com.htx.intelligentstretcher.inventory.db.models.InventoryItemRecordAndInstance;

import java.util.List;

public class InventoryRecordAdapter extends RecyclerView.Adapter<InventoryRecordAdapter.ViewHolder> {
    private List<InventoryItemRecordAndInstance> recordInstances;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView idTextView;
        private final TextView checkDateTextView;
        private final TextView expirydateTextView;
        private final TextView notesTextView;

        public ViewHolder(@NonNull View v) {
            super(v);
            idTextView = v.findViewById(R.id.item_id_text_view);
            checkDateTextView = v.findViewById(R.id.check_date_text_view);
            expirydateTextView = v.findViewById(R.id.expiry_date_text_view);
            notesTextView = v.findViewById(R.id.item_notes_text_view);
        }

        public TextView getIdTextView() {
            return idTextView;
        }

        public TextView getCheckDateTextView() {
            return checkDateTextView;
        }

        public TextView getExpirydateTextView() {
            return expirydateTextView;
        }

        public TextView getNotesTextView() {
            return notesTextView;
        }
    }

    public InventoryRecordAdapter(List<InventoryItemRecordAndInstance> recordInstances) {
        this.recordInstances = recordInstances;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inventory_record_list_item, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getIdTextView().setText(String.format("%d", recordInstances.get(position).instance.getInstanceId()));
        holder.getCheckDateTextView().setText(InventoryItemRecord.INSTANCE_CHECK_DATE_FORMAT
                .format(recordInstances.get(position).record.getInstanceCheckDate()));
        holder.getExpirydateTextView().setText(InventoryItemInstance.EXPIRY_DATE_FORMAT
                .format(recordInstances.get(position).instance.getExpiry()));
        holder.getNotesTextView().setText(recordInstances.get(position).record.getNotes());
    }

    @Override
    public int getItemCount() {
        return recordInstances.size();
    }

    public void setRecordInstances(List<InventoryItemRecordAndInstance> recordInstances) {
        this.recordInstances = recordInstances;
    }
}
