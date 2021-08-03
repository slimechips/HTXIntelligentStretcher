package com.htx.intelligentstretcher;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.htx.intelligentstretcher.control.StretcherControlFragment;
import com.htx.intelligentstretcher.dosage.DetailActivity;
import com.htx.intelligentstretcher.inventory.db.InventoryDatabase;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

//Test
public class MainActivity extends AppCompatActivity implements NavigationHost {

    public static SpeechRecognizer speechRecognizer;
    public final static Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    String MQTTHOST = "tcp://192.168.201.74:1883";
    public static MqttAndroidClient client;
    MqttConnectOptions options;
    private DashboardFragment dashboardFragment;
    TextToSpeech t1;
    int bloodPressure = 210;
    int oxygenTank = 150;

    //Speech to text check words
    String[] powerAssWords = {"power", "assist", "on"};
    String[] cotToChairWords = {"change", "chair"};
    String[] chairToCotWords = {"change", "bed"};
    String[] bloodPressureWords = {"blood", "pressure"};
    String[] oxygenTankWords = {"oxygen", "tank"};
    String[] reminders = {"pressure", "injection"};

    //Reminders variables
    long startTime = 0;
    long reminder = 0;
    boolean remindersFlag = false;
    int remindersIndex =  0;
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            if (seconds == reminder) {
                if (reminders[remindersIndex] == "pressure") {
                    t1.speak(Integer.toString(bloodPressure), TextToSpeech.QUEUE_FLUSH, null, "Test");
                } else if (reminders[remindersIndex] == "injection") {
                    t1.speak("Reminder to jab patient", TextToSpeech.QUEUE_FLUSH, null, "Test");
                }
            }
            Log.i("timer", Integer.toString(seconds));

            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InventoryDatabase.buildDatabase(getApplicationContext());
        dashboardFragment = new DashboardFragment();
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, dashboardFragment)
                    .commit();
        }

        checkPermission();
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this, MQTTHOST, clientId);
        options = new MqttConnectOptions();

        try {
            Log.i("mqtt", "attempting connection");
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.i("mqtt","connected");
                    setSubscription("powerAss");

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.i("test", "did not connect");
                }
            });
            Log.i("mqtt", "done");
        } catch (MqttException e) {
            Log.i("mqtt", "exception");
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.i("msg", new String(message.getPayload()));
                OxygenTankFragment.accumulatedVol = 12.2f;
                DetailActivity.weight_value = 12.5f;


            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        t1=new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {
//                speechText.setText("");
//                speechText.setHint("Listening...");
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
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                MainActivity.this.invalidateOptionsMenu();
                Log.i("speech", data.get(0));
                for (int i = 0; i <= reminders.length - 1; i++) {
                    if (data.get(0).contains(reminders[i])) {
                        remindersFlag = true;
                        remindersIndex = i;
                        break;
                    }
                }
                if (data.get(0).equals("stretcher")) {
                    ((NavigationHost) MainActivity.this).navigateTo(new StretcherControlFragment(), true);
                } else if (Arrays.stream(oxygenTankWords).allMatch(data.get(0)::contains)) {
                    ((NavigationHost) MainActivity.this).navigateTo(new OxygenTankFragment(), true);
                } else if (Arrays.stream(bloodPressureWords).allMatch(data.get(0)::contains)) {
                    t1.speak(Integer.toString(bloodPressure), TextToSpeech.QUEUE_FLUSH, null, "Test");
                } else if (data.get(0).equals("oxygen tank")) {
                    t1.speak(Integer.toString(oxygenTank), TextToSpeech.QUEUE_FLUSH, null, "Test");
                } else if (Arrays.stream(powerAssWords).allMatch(data.get(0)::contains)) {
                    StretcherControlFragment.powerAssOn = true;
                    t1.speak("power assist turned on", TextToSpeech.QUEUE_FLUSH, null, "Test");
                    ((NavigationHost) MainActivity.this).navigateTo(new StretcherControlFragment(), true);
                } else if (Arrays.stream(chairToCotWords).allMatch(data.get(0)::contains)) {
                    StretcherControlFragment.cotSelected = true;
                    t1.speak("changing to bed", TextToSpeech.QUEUE_FLUSH, null, "Test");
                    ((NavigationHost) MainActivity.this).navigateTo(new StretcherControlFragment(), true);
                } else if (Arrays.stream(cotToChairWords).allMatch(data.get(0)::contains)) {
                    StretcherControlFragment.cotSelected = false;
                    t1.speak("changing to chair", TextToSpeech.QUEUE_FLUSH, null, "Test");
                    ((NavigationHost) MainActivity.this).navigateTo(new StretcherControlFragment(), true);
                } else if (remindersFlag) {
                    String time = data.get(0).replaceAll("\\D+","");
                    if (time.equals("")) {
                        t1.speak("Sorry I do not understand, please repeat", TextToSpeech.QUEUE_FLUSH, null, "Test");
                    } else {
                        int second = Integer.valueOf(time);
                        reminder = second;
                        startTime = 0;
                        t1.speak("setting reminder to check " + reminders[remindersIndex] + " in " + time + " seconds", TextToSpeech.QUEUE_FLUSH, null, "Test");
                        timerHandler.postDelayed(timerRunnable, 0);
                    }
                } else {
                    t1.speak("Sorry I do not understand, please repeat", TextToSpeech.QUEUE_FLUSH, null, "Test");
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(1).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_mic_black_off));
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.drug_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.button:
                speechRecognizer.startListening(speechRecognizerIntent);
                item.setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_mic_black_24dp));
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void setSubscription(String topicStr){
        try{
            client.subscribe(topicStr,0);
            Log.i("sub_test", "subscribing");
        }catch (MqttException e){
            e.printStackTrace();
        }
    }

    /**
     * Navigate to the given fragment.
     *
     * @param fragment       Fragment to navigate to.
     * @param addToBackstack Whether or not the current fragment should be added to the backstack.
     */
    @Override
    public void navigateTo(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment);

        if (addToBackstack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
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