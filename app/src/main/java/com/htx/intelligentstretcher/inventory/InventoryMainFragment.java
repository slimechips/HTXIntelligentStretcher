package com.htx.intelligentstretcher.inventory;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.htx.intelligentstretcher.R;
import com.htx.intelligentstretcher.inventory.db.InventoryDataGenerator;
import com.htx.intelligentstretcher.inventory.db.InventoryDatabase;
import com.htx.intelligentstretcher.inventory.db.InventoryRepository;
import com.htx.intelligentstretcher.inventory.db.entity.InventoryCheck;
import com.journeyapps.barcodescanner.*;

import java.util.*;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class InventoryMainFragment extends Fragment {
    private NavigationView nav;
    private BeepManager beepManager;
    private DecoratedBarcodeView barcodeView;
    private static final HashMap<Integer, Integer> titleOptsMap = new HashMap<Integer, Integer>() {{
        put(R.string.medical_bag, R.array.medical_bag_opts);
    }};

    // TEMP
    public static int CHECK_ID = 3;
    public static String CHECKER = "Jason";

    private String lastText;

    public InventoryMainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startCheck(CHECK_ID);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        for (int i = 0; i < nav.getMenu().size(); ++i)  {
            MenuItem item = nav.getMenu().getItem(i);
            item.setOnMenuItemClickListener((MenuItem _item) -> {
                Log.i("Main Fragment Menu Item",  _item.getTitle().toString());
                Bundle bundle = new Bundle();
                bundle.putInt(InventorySubFragment.OPTS_REF_KEY, getOptsRef(_item));
                getChildFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.inventory_main_fragment_container, InventorySubFragment.class, bundle)
                        .commit();
                return true;
            });
        }
        checkCamera();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_inventory_main, container, false);
        nav = v.findViewById(R.id.inventory_main_nav_view);
        barcodeView = v.findViewById(R.id.barcode_scanner);
        return v;
    }

    private void checkCamera() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        {
            initCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null || result.getText().equals(lastText)) {
                // Prevent duplicate scans
                return;
            }

            lastText = result.getText();
            barcodeView.setStatusText(result.getText());

            beepManager.playBeepSoundAndVibrate();
            InventoryRepository.getInstance(InventoryDatabase.getInstance(getContext()))
                    .insertJsonItemInstanceAndRecord(CHECK_ID, result.getText(), null);
            //Added preview of scanned barcode
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    initCamera();
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });

    private void initCamera() {
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.initializeFromIntent(getActivity().getIntent());
        barcodeView.decodeContinuous(callback);

        beepManager = new BeepManager(getActivity());
    }


    private int getOptsRef(MenuItem item) {
        String title = item.getTitle().toString();
        for (Map.Entry<Integer, Integer> entry : titleOptsMap.entrySet()) {
            if (title.compareTo(getString(entry.getKey())) == 0) {
                return entry.getValue();
            }
        }
        return -1;
    }



    private void startCheck(int checkId) {
        InventoryRepository.getInstance(InventoryDatabase.getInstance(getContext()))
                .insertCheck(new InventoryCheck(checkId, CHECKER, new Date()));
    }

    @Override
    public void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

}