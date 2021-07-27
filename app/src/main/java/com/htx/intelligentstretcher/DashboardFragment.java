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
import java.util.Date;
import java.util.Locale;

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
    private SpeechRecognizer speechRecognizer;
    private EditText speechText;
    private ImageView micButton;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 10000;
    Date currTime = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    String shortTimeStr;
    TextToSpeech t1;
    int bloodPressure = 210;
    int oxygenTank = 150;

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
        micButton = view.findViewById(R.id.button);

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
            startActivity(new Intent(getActivity(), DrugActivity.class));
//            ((NavigationHost) getActivity()).navigateTo(new DrugFragment(), true); // Navigate to the next Fragment
        });

        controlButton.setOnClickListener(v -> {
            ((NavigationHost) getActivity()).navigateTo(new StretcherControlFragment(), true); // Navigate to the next Fragment
        });

        checkPermission();

        t1=new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        speechText = view.findViewById(R.id.text);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());

        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {
                speechText.setText("");
                speechText.setHint("Listening...");
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                micButton.setImageResource(R.drawable.ic_mic_black_off);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                speechText.setText(data.get(0));
                t1.speak(data.get(0), TextToSpeech.QUEUE_FLUSH, null, "Test");
                if (data.get(0).equals("stretcher")) {
                    ((NavigationHost) getActivity()).navigateTo(new StretcherControlFragment(), true);
                } else if (data.get(0).equals("oxygen")) {
                    ((NavigationHost) getActivity()).navigateTo(new OxygenTankFragment(), true);
                } else if (data.get(0).equals("blood pressure")) {
                    t1.speak(Integer.toString(bloodPressure), TextToSpeech.QUEUE_FLUSH, null, "Test");
                } else if (data.get(0).equals("oxygen tank")) {
                    t1.speak(Integer.toString(oxygenTank), TextToSpeech.QUEUE_FLUSH, null, "Test");
                } else if (data.get(0).equals("turn on power assist")) {
                    StretcherControlFragment.powerAssOn = true;
                    t1.speak("power assist turned on", TextToSpeech.QUEUE_FLUSH, null, "Test");
                    ((NavigationHost) getActivity()).navigateTo(new StretcherControlFragment(), true);
                } else if (data.get(0).equals("mode a")) {
                    StretcherControlFragment.cotSelected = true;
                    t1.speak("changing to bed", TextToSpeech.QUEUE_FLUSH, null, "Test");
                    ((NavigationHost) getActivity()).navigateTo(new StretcherControlFragment(), true);
                } else if (data.get(0).equals("mode b")) {
                    StretcherControlFragment.cotSelected = false;
                    t1.speak("changing to chair", TextToSpeech.QUEUE_FLUSH, null, "Test");
                    ((NavigationHost) getActivity()).navigateTo(new StretcherControlFragment(), true);
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        micButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    speechRecognizer.stopListening();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    micButton.setImageResource(R.drawable.ic_mic_black_24dp);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                return false;
            }
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

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
        {

        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });
}
