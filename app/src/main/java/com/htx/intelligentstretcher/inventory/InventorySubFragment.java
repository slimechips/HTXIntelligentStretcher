package com.htx.intelligentstretcher.inventory;

import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import com.htx.intelligentstretcher.R;
import com.htx.intelligentstretcher.inventory.db.InventoryDatabase;
import com.htx.intelligentstretcher.inventory.db.InventoryRepository;
import com.htx.intelligentstretcher.inventory.db.entity.InventoryPage;
import com.htx.intelligentstretcher.inventory.db.entity.InventorySubListItem;
import com.htx.intelligentstretcher.inventory.db.models.InventorySubListItemDoneQty;
import com.htx.intelligentstretcher.inventory.db.models.InventorySubListItemTotalQty;
import com.htx.intelligentstretcher.inventory.db.models.InventorySubListItemWithPage;
import com.htx.intelligentstretcher.inventory.viewmodel.InventorySubListItemViewModel;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InventorySubFragment extends Fragment {
    private static final String TAG = "InventorySubFragment";
    private int optsRef;
    public static final String OPTS_REF_KEY = "optsRef";
    private InventorySubListAdapter inventorySubListAdapter;
    private ExpandableListView expandableListView;
    private List<InventorySubListAdapter.ExpandableViewHolder> expandableListTitles;
    private List<InventorySubListItemViewModel> expandableListDetailsVM;
    private Map<Integer, List<InventorySubListAdapter.ExpandableViewHolder>> expandableListDetails;
    private List<InventorySubListItemDoneQty> totalDoneQtys;
    private List<InventorySubListItemTotalQty> totalQtys;

    public InventorySubFragment() {
        // Required empty public constructor
    }

    public static InventorySubFragment newInstance(int optsRef) {
        InventorySubFragment fragment = new InventorySubFragment();
        fragment.optsRef = optsRef;
        return fragment;
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
        View v = inflater.inflate(R.layout.fragment_inventory_sub, container, false);
        expandableListView = v.findViewById(R.id.inventory_expandable_listview);

        return v;
    }

    private void initUI() {
        optsRef = requireArguments().getInt(OPTS_REF_KEY);
        initSubListItemViewHolders(optsRef);
    }

    private void initSubListItemViewHolders(int optsRef) {
        InventoryRepository repository = InventoryRepository.getInstance(InventoryDatabase.getInstance(getContext()));
        repository.loadInventorySubListItemsByParentName(optsRef).observe(getViewLifecycleOwner(), (subListItems) -> {
            expandableListTitles = getExpandableListTitles(subListItems);
            expandableListDetails = getExpandableListDetail(subListItems);
            inventorySubListAdapter = new InventorySubListAdapter(getContext(), this, expandableListTitles);
            expandableListDetailsVM = getSubListModelVMMap(repository, subListItems, InventoryMainFragment.CHECK_ID);
            expandableListView.setAdapter(inventorySubListAdapter);
            expandableListView.setOnGroupExpandListener((groupPosition) ->
                    Toast.makeText(getContext(), expandableListTitles.get(groupPosition) + " List Expanded.", Toast.LENGTH_SHORT).show()
            );
        });

    }

    private List<InventorySubListAdapter.ExpandableViewHolder> getExpandableListTitles(List<InventorySubListItem> subListItems) {
        return subListItems.stream()
                .map(subListItem -> new InventorySubListAdapter.ExpandableViewHolder(
                        subListItem.getSubListItemId(),
                        subListItem.getTitle(),
                        subListItem.getTotalQtyDone(),
                        subListItem.getTotalQty()
                        )
                )
                .collect(Collectors.toList());
    }

    private Map<Integer, List<InventorySubListAdapter.ExpandableViewHolder>> getExpandableListDetail(List<InventorySubListItem> subListItems) {
        return subListItems.stream()
                .collect(Collectors.toMap(InventorySubListItem::getSubListItemId, subListItem -> new ArrayList<>()));
    }

    private List<InventorySubListItemViewModel> getSubListModelVMMap(InventoryRepository repository,
                                                                            List<InventorySubListItem> subListItems,
                                                                            int checkId) {
        return subListItems.stream()
                .map((subListItem) -> {
                    InventorySubListItemViewModel.Factory factory = new InventorySubListItemViewModel.Factory(
                            requireActivity().getApplication(), repository, checkId, subListItem.getSubListItemId()
                    );
                    final InventorySubListItemViewModel model = new ViewModelProvider(InventorySubFragment.this, factory)
                            .get(InventorySubListItemViewModel.class);
                    return model;
                })
                .peek(vm -> subToInventorySubListViewModel(vm.getSubListItemId(), vm))
                .collect(Collectors.toList());
    }

    private void subToInventorySubListViewModel(int subListItemId, InventorySubListItemViewModel model) {
        observeMainSubListItem(subListItemId, model);
        observeSubListItemsInList(subListItemId, model);
        observeTotalDoneCount(subListItemId, model);
        observeTotalCount(subListItemId, model);
    }

    private void observeMainSubListItem(int subListItemId, InventorySubListItemViewModel model) {
        model.getInventorySubListItemWithPage().observe(getViewLifecycleOwner(), (subListItemWithPage) ->
                updateMainSubListItem(subListItemId, subListItemWithPage));
    }

    private synchronized void updateMainSubListItem(int subListItemId, InventorySubListItemWithPage subListItemWithPage) {
        inventorySubListAdapter.getExpandableListTitle().stream()
                .filter(expandableViewHolder -> expandableViewHolder.getItemId() == subListItemWithPage.subListItem.getSubListItemId())
                .findAny()
                .map(_item -> {
                    _item.setSubtitle(subListItemWithPage.subListItem.getTitle());
                    _item.setPage(subListItemWithPage.page);
                    return _item;
                });
        inventorySubListAdapter.notifyDataSetChanged();
        inventorySubListAdapter.initList.incrementAndGet();
        if (totalDoneQtys != null && inventorySubListAdapter.initList.get() >= inventorySubListAdapter.getExpandableListTitle().size()
                && inventorySubListAdapter.initDetails.get()) {
            List<InventorySubListItemDoneQty> _totalDoneQtys = totalDoneQtys;
            totalDoneQtys = null;
            updateTotalDoneCount(_totalDoneQtys);
        }

    }

    private void observeSubListItemsInList(int subListItemId, InventorySubListItemViewModel model) {
        model.getChildrenWithPage().observe(getViewLifecycleOwner(), (childrenSubListItems ->
                updateSubListItemsInList(subListItemId, childrenSubListItems)));
    }

    private synchronized void updateSubListItemsInList(int subListItemId, List<InventorySubListItemWithPage> childrenSubListItems) {
        final List<InventorySubListAdapter.ExpandableViewHolder> oldDetails =
                inventorySubListAdapter.initDetails.get() ?
                        inventorySubListAdapter.getExpandableListDetail().get(subListItemId) : null;
        final AtomicInteger counter = new AtomicInteger();
        List<InventorySubListAdapter.ExpandableViewHolder> expandableViewHolders = childrenSubListItems.stream()
                .map(childSubListItemWithPage -> {
                    InventorySubListAdapter.ExpandableViewHolder vh = new InventorySubListAdapter.ExpandableViewHolder(
                            childSubListItemWithPage.subListItem.getSubListItemId(),
                            childSubListItemWithPage.subListItem.getTitle(),
                            oldDetails == null ? childSubListItemWithPage.subListItem.getTotalQtyDone() : oldDetails.get(counter.get()).getQtyDone(),
                            oldDetails == null ? childSubListItemWithPage.subListItem.getTotalQty() : oldDetails.get(counter.get()).getTotalQty()
                    );
                    vh.setPage(childSubListItemWithPage.page);
                    counter.incrementAndGet();
                    return vh;
                })
                .collect(Collectors.toList());
        inventorySubListAdapter.getExpandableListDetail().replace(subListItemId, expandableViewHolders);
        inventorySubListAdapter.notifyDataSetChanged();
        inventorySubListAdapter.initDetails.set(true);
        if (inventorySubListAdapter.initList.get() >= inventorySubListAdapter.getExpandableListTitle().size()
                && inventorySubListAdapter.initDetails.get()) {
            if (totalDoneQtys != null) {
                List<InventorySubListItemDoneQty> _totalDoneQtys = totalDoneQtys;
                totalDoneQtys = null;
                updateTotalDoneCount(_totalDoneQtys);
            }

            if (totalQtys != null) {
                List<InventorySubListItemTotalQty> _totalQtys = totalQtys;
                totalQtys = null;
                updateTotalCount(_totalQtys);
            }

        }
    }

    private void observeTotalDoneCount(int subListItemId, InventorySubListItemViewModel model) {
        model.getTotalDoneQtys().observe(getViewLifecycleOwner(), totalDoneQtys -> {
             if (inventorySubListAdapter.initList.get() >= inventorySubListAdapter.getExpandableListTitle().size()
                     && inventorySubListAdapter.initDetails.get()) {
                 updateTotalDoneCount(totalDoneQtys);
             } else {
                 this.totalDoneQtys = totalDoneQtys;
             }
        });
    }

    private synchronized void updateTotalDoneCount(List<InventorySubListItemDoneQty> totalDoneQtys) {
        totalDoneQtys.forEach(subListItem -> Objects.requireNonNull(inventorySubListAdapter.getExpandableListDetail()
                .get(subListItem.getParentSubListItemId()))
                .stream()
                .filter(childSubListItem -> childSubListItem.getItemId() == subListItem.getSubListItemId())
                .findAny()
                .ifPresent(expandableViewHolder -> expandableViewHolder.setQtyDone(subListItem.getTotalQtyDone())));
        inventorySubListAdapter.notifyDataSetChanged();

    }

    private void observeTotalCount(int subListItemId, InventorySubListItemViewModel model) {
        model.getTotalQtys().observe(getViewLifecycleOwner(), totalQtys -> {
            if (inventorySubListAdapter.initList.get() >= inventorySubListAdapter.getExpandableListTitle().size()
                    && inventorySubListAdapter.initDetails.get()) {
                updateTotalCount(totalQtys);
            } else {
                this.totalQtys = totalQtys;
            }
        });
    }

    private synchronized void updateTotalCount(List<InventorySubListItemTotalQty> totalQtys) {
        totalQtys.forEach(subListItem -> Objects.requireNonNull(inventorySubListAdapter.getExpandableListDetail()
                .get(subListItem.getParentSubListItemId()))
                .stream()
                .filter(childSubListItem -> childSubListItem.getItemId() == subListItem.getSubListItemId())
                .findAny()
                .ifPresent(expandableViewHolder -> expandableViewHolder.setTotalQty(subListItem.getTotalQty())));
        inventorySubListAdapter.notifyDataSetChanged();
    }

    public void createChildFragment(InventoryPage page) {
        Bundle bundle = new Bundle();
        bundle.putInt(InventoryChildFragment.INVENTORY_PAGE_KEY, page.getPageId());
        getChildFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.inventory_sub_fragment_container, InventoryChildFragment.class, bundle)
                .commit();
    }


    public InventorySubListAdapter getInventorySubListAdapter() {
        return inventorySubListAdapter;
    }

    public List<InventorySubListItemViewModel> getExpandableListDetailsVM() {
        return expandableListDetailsVM;
    }

}