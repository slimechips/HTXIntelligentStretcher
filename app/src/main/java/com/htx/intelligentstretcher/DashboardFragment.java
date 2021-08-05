package com.htx.intelligentstretcher;

import android.content.Intent;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.htx.intelligentstretcher.control.StretcherControlFragment;
import com.htx.intelligentstretcher.dosage.DrugActivity;
import com.htx.intelligentstretcher.inventory.InventoryMainFragment;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static com.htx.intelligentstretcher.MainActivity.speechRecognizer;
import static com.htx.intelligentstretcher.MainActivity.speechRecognizerIntent;
import static java.util.Calendar.getInstance;

import android.speech.tts.TextToSpeech;

public class DashboardFragment extends Fragment {

    private MaterialCardView invButton;
    private MaterialCardView oxygenButton;
    private MaterialCardView dosageButton;
    private MaterialCardView controlButton;
    private static String oxygenText;
    private TextView otTextView;
    public static final Integer RecordAudioRequestCode = 1;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 10000;
    Date currTime = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    String shortTimeStr;

//    SpeechRecognizer speechRecognizer = MainActivity.speechRecognizer;
//    Intent speechRecognizerIntent = MainActivity.speechRecognizerIntent;


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

        invButton.setOnClickListener(view1 -> {
            ((NavigationHost) getActivity()).navigateTo(new InventoryMainFragment(), true); // Navigate to the next Fragment
        });

        dosageButton.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), DrugActivity.class));
//            ((NavigationHost) getActivity()).navigateTo(new DrugFragment(), true); // Navigate to the next Fragment
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
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                updateCards();
            }
        }, delay);
    }

    public void updateCards() {
        shortTimeStr = sdf.format(currTime);
        otTextView.setText(shortTimeStr);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }


}
