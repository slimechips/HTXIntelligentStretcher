package com.htx.intelligentstretcher.inventory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.htx.intelligentstretcher.R;
import com.htx.intelligentstretcher.inventory.db.entity.InventoryPage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InventorySubListAdapter extends BaseExpandableListAdapter {
    public static final String TITLE_FORMAT = "%s %d/%d";

    private final InventorySubFragment inventorySubFragment;
    private final Context context;
    private List<ExpandableViewHolder> expandableListTitle;
    private Map<Integer, List<ExpandableViewHolder>> expandableListDetail;
    private View selectedView = null;
    private int selectedGroup = -1;
    public final AtomicInteger initList = new AtomicInteger();
    public final AtomicBoolean initDetails = new AtomicBoolean();

    public static class ExpandableViewHolder {
        private final int itemId;
        private String subtitle;
        private int qtyDone;
        private int totalQty;
        private InventoryPage page;
        private boolean expandable;

        public ExpandableViewHolder(int itemId, String subtitle, int qtyDone, int totalQty) {
            this.itemId = itemId;
            this.subtitle = subtitle;
            this.qtyDone = qtyDone;
            this.totalQty = totalQty;
            this.page = null;
        }

        public int getItemId() {
            return itemId;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public void setQtyDone(int qtyDone) {
            this.qtyDone = qtyDone;
        }

        public void setTotalQty(int totalQty) {
            this.totalQty = totalQty;
        }

        public InventoryPage getPage() {
            return page;
        }

        public void setPage(InventoryPage page) {
            this.page = page;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public int getQtyDone() {
            return qtyDone;
        }

        public int getTotalQty() {
            return totalQty;
        }

        public boolean isExpandable() {
            return expandable;
        }

        public void setExpandable(boolean expandable) {
            this.expandable = expandable;
        }

        @SuppressLint("DefaultLocale")
        public String getFullSubtitle() {
            return String.format(InventorySubListAdapter.TITLE_FORMAT, subtitle,
                    qtyDone, totalQty);
        }
    }

    public InventorySubListAdapter(Context context, InventorySubFragment inventorySubFragment, List<ExpandableViewHolder> expandableListTitle) {
        this.context = context;
        this.inventorySubFragment = inventorySubFragment;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListTitle.stream()
                .collect(Collectors.toMap(ExpandableViewHolder::getItemId, expandableViewHolder -> new ArrayList<>()));
    }

    /*
    Type ExpandableViewHolder
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return expandableListDetail.get(expandableListTitle.get(groupPosition).itemId)
                .get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = ((ExpandableViewHolder) getChild(groupPosition, childPosition)).getFullSubtitle();
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }
        convertView.setOnClickListener((v) -> {
            if (selectedView != null) {
                selectedView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            }

            if (selectedGroup != -1) {
                onGroupCollapsed(selectedGroup);
            }

            selectedView = v;
            v.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
            inventorySubFragment.createChildFragment(getExpandableListDetail()
                    .get(expandableListTitle.get(groupPosition).itemId)
                    .get(childPosition).getPage());
        });
        TextView expandedListTextView = (TextView) convertView.findViewById(R.id.list_expanded_item);
        expandedListTextView.setText(expandedListText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return expandableListDetail.get(expandableListTitle.get(groupPosition).itemId).size();
    }


    /*
    Type ExpandableViewHolder
     */

    @Override
    public Object getGroup(int groupPosition) {
        return expandableListTitle.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return expandableListTitle.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String listTitle = ((ExpandableViewHolder) getGroup(groupPosition)).subtitle;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);

        }


//        convertView.setOnClickListener((v) -> {
//            if (getExpandableListDetail().get(getExpandableListTitle().get(groupPosition).itemId).size() > 0) {
//                return;
//            }
//
//            if (selectedView != null) {
//                selectedView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
//            }
//            if (selectedGroup != -1) {
//                onGroupCollapsed(selectedGroup);
//            }
//            selectedGroup = groupPosition;
//            selectedView = v;
//            v.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
//            inventorySubFragment.createChildFragment(getExpandableListTitle().get(groupPosition).getPage());
//        });
        TextView listTitleTextView = convertView.findViewById(R.id.list_title);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public List<ExpandableViewHolder> getExpandableListTitle() {
        return expandableListTitle;
    }

    public void setExpandableListTitle(List<ExpandableViewHolder> expandableListTitle) {
        this.expandableListTitle = expandableListTitle;
    }

    public Map<Integer, List<ExpandableViewHolder>> getExpandableListDetail() {
        return expandableListDetail;
    }

    public void setExpandableListDetail(Map<Integer, List<ExpandableViewHolder>> expandableListDetail) {
        this.expandableListDetail = expandableListDetail;
    }
}
