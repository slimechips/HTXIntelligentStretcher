package com.htx.intelligentstretcher;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.htx.intelligentstretcher.control.StretcherControlFragment;
import com.htx.intelligentstretcher.dosage.DrugFragment;
import com.htx.intelligentstretcher.inventory.InventoryMainFragment;

import java.text.DecimalFormat;

public class DashboardFragment extends Fragment {

    private MaterialCardView invButton;
    private MaterialCardView oxygenButton;
    private MaterialCardView dosageButton;
    private MaterialCardView controlButton;
    private static String oxygenText;
    private TextView otTextView;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dashboard_fragment, container, false);
        oxygenButton = view.findViewById(R.id.oxygenTank);
        MaterialCardView pvButton = view.findViewById(R.id.patientVitals);
        invButton = view.findViewById(R.id.itemTracking);
        dosageButton = view.findViewById(R.id.dosageCheatsheet);
        controlButton = view.findViewById(R.id.stretcherControl);
        otTextView = view.findViewById(R.id.otData);

        oxygenButton.setOnClickListener(v -> {
            ((NavigationHost) getActivity()).navigateTo(new OxygenTankFragment(), true); // Navigate to the next Fragment
        });

        pvButton.setOnClickListener(view12 -> {
            ((NavigationHost) getActivity()).navigateTo(new PatientVitals(), true); // Navigate to the next Fragment
        });

        invButton.setOnClickListener(view1 -> {
            ((NavigationHost) getActivity()).navigateTo(new InventoryMainFragment(), true); // Navigate to the next Fragment
        });

        dosageButton.setOnClickListener(v -> {
            ((NavigationHost) getActivity()).navigateTo(new DrugFragment(), true); // Navigate to the next Fragment
        });

        controlButton.setOnClickListener(v -> {
            ((NavigationHost) getActivity()).navigateTo(new StretcherControlFragment(), true); // Navigate to the next Fragment
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        otTextView.setText("" + OxygenTankFragment.oxygenStr + "SLPM");
    }
}
