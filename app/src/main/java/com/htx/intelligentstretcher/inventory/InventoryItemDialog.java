package com.htx.intelligentstretcher.inventory;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.htx.intelligentstretcher.R;
import com.htx.intelligentstretcher.inventory.db.InventoryDatabase;
import com.htx.intelligentstretcher.inventory.db.InventoryRepository;
import com.htx.intelligentstretcher.inventory.db.entity.InventoryItem;
import com.htx.intelligentstretcher.inventory.db.models.InventoryItemDoneQty;
import com.htx.intelligentstretcher.inventory.viewmodel.InventoryItemViewModel;
import com.htx.intelligentstretcher.utils.GenUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class InventoryItemDialog extends DialogFragment {
    public static final String TAG = "InventoryItemDialog";
    public static final String ITEM_KEY = "item key";
    private static final String COUNT_TEXT_FORMAT = "%d/%d";

    private int itemId;
    private InventoryItem item;
    private InventoryItemDoneQty itemDoneQty;
    private final AtomicBoolean itemInit = new AtomicBoolean();
    private ItemViewHolder itemViewHolder;
    private RecyclerView currentRecordRecycler;
    private RecyclerView historyRecycler;
    private TextView historyTextView;
    private InventoryRecordAdapter currentRecordAdapter;
    private InventoryRecordAdapter historyAdapter;
    private List<HistoryClickListener> historyClickListeners = new ArrayList<>();

    public static class ItemViewHolder {
        private final TextView countTextView;
        private final TextView titleTextView;
        private final TextView subtitleTextView;
        private final ImageView itemImageView;
        private final TextView itemDescTextView;
        private final ImageView calendarIconImageView;
        private final TextView expiryTextView;

        public ItemViewHolder(View v) {
            countTextView = v.findViewById(R.id.inventory_item_count_text_view);
            titleTextView = v.findViewById(R.id.inventory_item_title_text_view);
            subtitleTextView = v.findViewById(R.id.inventory_item_subtitle_text_view);
            itemImageView = v.findViewById(R.id.inventory_item_image_view);
            itemDescTextView = v.findViewById(R.id.inventory_item_desc_text_view);
            calendarIconImageView = v.findViewById(R.id.inventory_item_calendar_icon_image_view);
            expiryTextView = v.findViewById(R.id.inventory_item_expiry_date_text_view);
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

    public interface HistoryClickListener {
        void onHistoryClick(int change);
    }

    public InventoryItemDialog() {
        // Required empty public constructor
    }

    public InventoryItemDialog(int itemId) {
        // Required empty public constructor
        this.itemId = itemId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_inventory_item, container, false);
        itemViewHolder = new ItemViewHolder(v.findViewById(R.id.inventory_item_relative_layout));
        currentRecordRecycler = v.findViewById(R.id.scanned_items_recycler);
        historyTextView = v.findViewById(R.id.items_history_title_text_view);
        historyRecycler = v.findViewById(R.id.items_history_recycler);

        currentRecordRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        currentRecordAdapter = new InventoryRecordAdapter(new ArrayList<>());
        currentRecordRecycler.setAdapter(currentRecordAdapter);
        historyRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        historyAdapter = new InventoryRecordAdapter(new ArrayList<>());
        historyRecycler.setAdapter(historyAdapter);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
//        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void initUI() {
        InventoryItemViewModel itemViewModel = createInventoryItemVM(itemId);
        subToVMItem(itemViewModel);
        subToVMDoneQty(itemViewModel);
        subToVMCurrentItems(itemViewModel);
        subtoVMHistory(itemViewModel);
        registerHistoryClickListener(itemViewModel);
    }

    private InventoryItemViewModel createInventoryItemVM(int itemId) {
        InventoryRepository repository = InventoryRepository.getInstance(InventoryDatabase.getInstance(getContext()));
        InventoryItemViewModel.Factory factory = new InventoryItemViewModel.Factory(
                requireActivity().getApplication(), repository, InventoryMainFragment.CHECK_ID, itemId
        );
        return new ViewModelProvider(InventoryItemDialog.this, factory)
                .get(InventoryItemViewModel.class);

    }

    private void subToVMItem(InventoryItemViewModel vm) {
        vm.getItem().observe(getViewLifecycleOwner(), inventoryItem -> {
            updateVMItem(inventoryItem);
            itemInit.set(true);
            synchronized (itemInit) {
                if (itemDoneQty != null && itemInit.get()) {
                    InventoryItemDoneQty _doneQty = itemDoneQty;
                    itemDoneQty = null;
                    updateDoneQty(_doneQty);
                }
            }
        });
    }

    private synchronized void updateVMItem(InventoryItem inventoryItem) {
        item = inventoryItem;
        itemViewHolder.getCountTextView().setText(makeCountString(inventoryItem.getDoneQty(), inventoryItem.getRequiredQty()));

        if (inventoryItem.getDoneQty() == inventoryItem.getRequiredQty()) {
            itemViewHolder.getCountTextView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.inventory_item_color_good));
        } else {
            itemViewHolder.getCountTextView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.inventory_item_color_bad));

        }

        itemViewHolder.getTitleTextView().setText(inventoryItem.getName());
        itemViewHolder.getSubtitleTextView().setText(inventoryItem.getCategory());

        String imageUri = inventoryItem.getImageUri();
        int imageResourceId = inventoryItem.getResourceId();

        if (imageUri != null) {
            itemViewHolder.getItemImageView().setImageBitmap(GenUtils.loadBitmapFromUriString(imageUri));
        } else {
            itemViewHolder.getItemImageView().setImageResource(imageResourceId);
        }

        itemViewHolder.getItemDescTextView().setText(inventoryItem.getDescription());

        Date latestExpiry = inventoryItem.getLatestExpiry();

        if (latestExpiry != null) {
            itemViewHolder.getExpiryTextView().setText(latestExpiry.toString());
        } else {
            itemViewHolder.getExpiryTextView().setText("-");
        }
    }

    private void subToVMDoneQty(InventoryItemViewModel vm) {
        vm.getItemDoneQty().observe(getViewLifecycleOwner(), doneQty -> {
            synchronized (itemInit) {
                if (itemInit.get()) {
                    updateDoneQty(doneQty);
                } else {
                    itemDoneQty = doneQty;
                }
            }
        });
    }

    private synchronized void updateDoneQty(InventoryItemDoneQty doneQty) {
        item.setDoneQty(doneQty.getDoneQty());
        itemViewHolder.getCountTextView().setText(makeCountString(item.getDoneQty(), item.getRequiredQty()));

        if (item.getDoneQty() == item.getRequiredQty()) {
            itemViewHolder.getCountTextView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.inventory_item_color_good));
        } else {
            itemViewHolder.getCountTextView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.inventory_item_color_bad));

        }

    }

    private void subToVMCurrentItems(InventoryItemViewModel vm) {
        vm.getCurrentRecord().observe(getViewLifecycleOwner(), itemRecordWithInstances -> {
            currentRecordAdapter.setRecordInstances(itemRecordWithInstances);
            currentRecordAdapter.notifyDataSetChanged();
        });
    }

    private void subtoVMHistory(InventoryItemViewModel vm) {
        vm.getHistory().observe(getViewLifecycleOwner(), itemRecordWithInstances -> {
            historyAdapter.setRecordInstances(itemRecordWithInstances);
            historyAdapter.notifyDataSetChanged();
        });
    }

    public void registerHistoryClickListener(HistoryClickListener listener) {
        historyClickListeners.add(listener);
    }

    @SuppressLint("DefaultLocale")
    private String makeCountString(int doneQty, int totalQty) {
        return String.format(COUNT_TEXT_FORMAT, doneQty, totalQty);
    }
}