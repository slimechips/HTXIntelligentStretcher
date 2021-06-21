package com.htx.intelligentstretcher.inventory;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.htx.intelligentstretcher.R;
import com.htx.intelligentstretcher.inventory.db.InventoryDatabase;
import com.htx.intelligentstretcher.inventory.db.InventoryRepository;
import com.htx.intelligentstretcher.inventory.db.entity.InventoryItem;
import com.htx.intelligentstretcher.inventory.db.entity.InventoryPage;
import com.htx.intelligentstretcher.inventory.db.models.InventoryItemDoneQty;
import com.htx.intelligentstretcher.inventory.viewmodel.InventoryItemViewModel;
import com.htx.intelligentstretcher.inventory.viewmodel.InventoryPageViewModel;
import com.htx.intelligentstretcher.inventory.viewmodel.InventorySubListItemViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class InventoryChildFragment extends Fragment {
    public static final String INVENTORY_PAGE_KEY = "inventory page";

    private RecyclerView itemRecycler;
    private InventoryItemAdapter adapter;
    private int pageId;
    private InventorySubFragment parentFragment;
    private InventoryPageViewModel inventoryPageViewModel;
    private List<InventoryItemDoneQty> itemDoneQtyList;

    public InventoryChildFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pageId = requireArguments().getInt(INVENTORY_PAGE_KEY);
        initUI();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_inventory_child, container, false);
        itemRecycler = v.findViewById(R.id.inventory_item_recycler);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        itemRecycler.setLayoutManager(manager);
        adapter = new InventoryItemAdapter(getContext(), this, new ArrayList<>());
        itemRecycler.setAdapter(adapter);
        return v;
    }

    private void initUI() {
        inventoryPageViewModel = createInventoryItemVM(pageId);
        subToInventoryPageVMItems();
        subToItemDoneQty();
    }

    private InventoryPageViewModel createInventoryItemVM(int pageId) {
        InventoryRepository repository = InventoryRepository.getInstance(InventoryDatabase.getInstance(getContext()));
        InventoryPageViewModel.Factory factory = new InventoryPageViewModel.Factory(
                requireActivity().getApplication(), repository, pageId, InventoryMainFragment.CHECK_ID);
        return new ViewModelProvider(InventoryChildFragment.this, factory)
                .get(InventoryPageViewModel.class);
    }

    private void subToInventoryPageVMItems() {
        inventoryPageViewModel.getItems().observe(getViewLifecycleOwner(), inventoryItems ->
            updateInventoryPageVMItems(inventoryItems));
    }

    private synchronized void updateInventoryPageVMItems(List<InventoryItem> inventoryItems) {
        adapter.setInventoryItems(inventoryItems);
        adapter.notifyDataSetChanged();
        adapter.init.set(true);
        if (itemDoneQtyList != null && adapter.init.get()) {
            List<InventoryItemDoneQty> _doneQtyList = itemDoneQtyList;
            itemDoneQtyList = null;
            updateItemDoneQty(_doneQtyList);
        }
        adapter.notifyDataSetChanged();
    }

    private void subToItemDoneQty() {
        inventoryPageViewModel.getItemsDoneQty().observe(getViewLifecycleOwner(), items -> {
                if (adapter.init.get()) {
                    updateItemDoneQty(items);
                } else {
                    this.itemDoneQtyList = items;
                }
        });
    }

    private synchronized void updateItemDoneQty(List<InventoryItemDoneQty> items) {
        items.forEach(item -> {
            adapter.getInventoryItems().stream()
                    .filter(_item -> _item.getItemId() == item.getItemId())
                    .findAny()
                    .ifPresent(_item -> _item.setDoneQty(item.getDoneQty()));
        });
        adapter.notifyDataSetChanged();
    }

    public void showDialog(int itemId) {
        new InventoryItemDialog(itemId).show(
                getChildFragmentManager(), InventoryItemDialog.TAG
        );
    }
}