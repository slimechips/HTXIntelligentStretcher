package com.htx.intelligentstretcher;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.htx.intelligentstretcher.inventory.InventoryMainFragment;

public class DashboardFragment extends Fragment {

    private MaterialCardView invButton;
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dashboard_fragment, container, false);
        MaterialCardView pvButton = view.findViewById(R.id.patientVitals);
        invButton = view.findViewById(R.id.itemTracking);

        pvButton.setOnClickListener(view12 -> {
            ((NavigationHost) getActivity()).navigateTo(new PatientVitals(), true); // Navigate to the next Fragment
        });

        invButton.setOnClickListener(view1 -> {
            ((NavigationHost) getActivity()).navigateTo(new InventoryMainFragment(), true); // Navigate to the next Fragment
        });

        return view;
    }


}
