package com.htx.intelligentstretcher.inventory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.htx.intelligentstretcher.R;
import com.htx.intelligentstretcher.inventory.db.entity.InventoryItem;
import com.htx.intelligentstretcher.utils.GenUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class InventoryItemAdapter extends RecyclerView.Adapter<InventoryItemAdapter.ViewHolder> {
    private Context context;
    private InventoryChildFragment childFragment;
    private List<InventoryItem> inventoryItems;
    private static final String COUNT_TEXT_FORMAT = "%d/%d";
    public AtomicBoolean init = new AtomicBoolean();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final RelativeLayout relativeLayout;
        private final TextView countTextView;
        private final TextView titleTextView;
        private final TextView subtitleTextView;
        private final ImageView itemImageView;
        private final TextView itemDescTextView;
        private final ImageView calendarIconImageView;
        private final TextView expiryTextView;

        public ViewHolder(View v) {
            super(v);
            relativeLayout = v.findViewById(R.id.inventory_item_relative_layout);
            countTextView = v.findViewById(R.id.inventory_item_count_text_view);
            titleTextView = v.findViewById(R.id.inventory_item_title_text_view);
            subtitleTextView = v.findViewById(R.id.inventory_item_subtitle_text_view);
            itemImageView = v.findViewById(R.id.inventory_item_image_view);
            itemDescTextView = v.findViewById(R.id.inventory_item_desc_text_view);
            calendarIconImageView = v.findViewById(R.id.inventory_item_calendar_icon_image_view);
            expiryTextView = v.findViewById(R.id.inventory_item_expiry_date_text_view);
        }

        public RelativeLayout getRelativeLayout() {
            return relativeLayout;
        }

        public TextView getCountTextView() {
            return countTextView;
        }

        public TextView getTitleTextView() {
            return titleTextView;
        }

        public TextView getSubtitleTextView() {
            return subtitleTextView;
        }

        public ImageView getItemImageView() {
            return itemImageView;
        }

        public TextView getItemDescTextView() {
            return itemDescTextView;
        }

        public ImageView getCalendarIconImageView() {
            return calendarIconImageView;
        }

        public TextView getExpiryTextView() {
            return expiryTextView;
        }
    }

    public InventoryItemAdapter(Context context, InventoryChildFragment childFragment, List<InventoryItem> inventoryItems) {
        this.context = context;
        this.childFragment = childFragment;
        this.inventoryItems = inventoryItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inventory_item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        holder.getCountTextView().setText(makeCountString(inventoryItems.get(position).getDoneQty(),
                inventoryItems.get(position).getRequiredQty()));

        if (inventoryItems.get(position).getDoneQty() == inventoryItems.get(position).getRequiredQty()) {
            holder.getCountTextView().setBackgroundColor(ContextCompat.getColor(context, R.color.inventory_item_color_good));
        } else {
            holder.getCountTextView().setBackgroundColor(ContextCompat.getColor(context, R.color.inventory_item_color_bad));
        }

        holder.getTitleTextView().setText(inventoryItems.get(position).getName());
        holder.getSubtitleTextView().setText(inventoryItems.get(position).getCategory());

        String imageUri = inventoryItems.get(position).getImageUri();
        int imageResourceId = inventoryItems.get(position).getResourceId();

        if (imageUri != null) {
            holder.getItemImageView().setImageBitmap(GenUtils.loadBitmapFromUriString(imageUri));
        } else {
            holder.getItemImageView().setImageResource(imageResourceId);
        }

        holder.getItemDescTextView().setText(inventoryItems.get(position).getDescription());

        Date latestExpiry = inventoryItems.get(position).getLatestExpiry();

        if (latestExpiry != null) {
            holder.getExpiryTextView().setText(latestExpiry.toString());
        } else {
            holder.getExpiryTextView().setText("-");
        }

        holder.getRelativeLayout().setOnClickListener(v -> {
            childFragment.showDialog(inventoryItems.get(position).getItemId());
        });
    }

    @Override
    public int getItemCount() {
        return inventoryItems.size();
    }

    public List<InventoryItem> getInventoryItems() {
        return inventoryItems;
    }

    public void setInventoryItems(List<InventoryItem> inventoryItems) {
        this.inventoryItems = inventoryItems;
    }

    @SuppressLint("DefaultLocale")
    private String makeCountString(int doneQty, int totalQty) {
        return String.format(COUNT_TEXT_FORMAT, doneQty, totalQty);
    }
}
