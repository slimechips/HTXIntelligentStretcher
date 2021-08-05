package com.htx.intelligentstretcher;

import android.Manifest;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.widget.TextView;



import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.htx.intelligentstretcher.control.StretcherControlFragment;
import com.htx.intelligentstretcher.dosage.DetailActivity;
import com.htx.intelligentstretcher.dosage.DrugActivity;
import com.htx.intelligentstretcher.inventory.InventoryMainFragment;
import com.htx.intelligentstretcher.inventory.db.InventoryDatabase;
import static com.htx.intelligentstretcher.OxygenTankFragment.Instant_flow_rate;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;


//Test
public class MainActivity extends AppCompatActivity implements NavigationHost {

    public static SpeechRecognizer speechRecognizer;
    public final static Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    String MQTTHOST = "tcp://192.168.4.1:1883";
    public static MqttAndroidClient client;
    MqttConnectOptions options;
    private DashboardFragment dashboardFragment;
    TextToSpeech t1;
    int oxygenTank = 150;
    TextView slpm;
    Date oxygen_currTime = new Date();
    SimpleDateFormat oxygen_sdf = new SimpleDateFormat("HH:mm");
    public static String oxygen_shortTimeStr;


    //Speech to text check words
    String[] powerAssWords = {"power", "assist", "on"};
    String[] cotToChairWords = {"change", "chair"};
    String[] chairToCotWords = {"change", "bed"};
    String[] bloodPressureWords = {"blood", "pressure"};
    String[] oxygenTankWords = {"oxygen", "tank"};
    String[] patientVitalsWords = {"patient", "vital", "signs"};
    String[] reminders = {"pressure", "injection", "Airways", "consciousness"};
    String heartRate = "98 bpm";
    String spo2 = "100%";
    String temp = "38.5 degrees celsius";
    String bloodPressure = "118/78 sys/dia";

    //Searcher
    private static final String[] SUGGESTIONS = {
            "Stretcher Control", "Oxygen Tank", "Inventory Management", "Dosage Cheatsheet"
    };
    private SimpleCursorAdapter mAdapter;

    //Reminders variables
    long startTime = 0;
    long reminder = 0;
    boolean remindersFlag = false;
    int remindersIndex =  0;
    int second = 0;
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
//            int minutes = seconds / 60;
//            seconds = seconds % 60;

            timerHandler.postDelayed(this, 1000);
            if (seconds == reminder) {
                if (reminders[remindersIndex] == "pressure") {
                    t1.speak(bloodPressure, TextToSpeech.QUEUE_FLUSH, null, "Test");
                } else if (reminders[remindersIndex] == "injection") {
                    t1.speak("Reminder to jab patient", TextToSpeech.QUEUE_FLUSH, null, "Test");
                } else if (reminders[remindersIndex] == "Airways") {
                    t1.speak("Reminder to check patient's airway", TextToSpeech.QUEUE_FLUSH, null, "Test");
                } else if (reminders[remindersIndex] == "consciousness") {
                    t1.speak("Reminder to check patient's consciousness", TextToSpeech.QUEUE_FLUSH, null, "Test");
                }
                timerHandler.removeCallbacks(timerRunnable);
            }
            Log.i("timer", Integer.toString(seconds));

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Dosage);
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

        final String[] from = new String[] {"functions"};
        final int[] to = new int[] {android.R.id.text1};
        mAdapter = new SimpleCursorAdapter(MainActivity.this,
                android.R.layout.simple_list_item_1,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        try {
            Log.i("mqtt", "attempting connection");
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.i("mqtt","connected");

                    setSubscription("sensor/oxygen");
                    setSubscription("sensor/weight");

                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.i("test", "did not connect");
                }
            });
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
                Log.i("topic", topic);
//                Log.i("check", String.valueOf(topic.equals("powerAss")));
//                OxygenTankFragment.accumulatedVol = 12.2f;
//                DetailActivity.weight_value = 12.5f;

                if (topic.equals("sensor/weight")){
                    String sensor_weight_value = new String(message.getPayload());
                    JsonObject weight = new Gson().fromJson(sensor_weight_value, JsonObject.class);
                    float weight_result = weight.get("weight").getAsFloat();
                    Log.i("weight_value", String.valueOf(weight_result));
                    DetailActivity.weight_value = weight_result;
                }

                else if (topic.equals("sensor/oxygen")) {
                    String sensor_oxygen_value = new String(message.getPayload());
                    JsonObject weight = new Gson().fromJson(sensor_oxygen_value, JsonObject.class);
                    float oxygen_result = weight.get("slpm").getAsFloat();
                    Log.i("oxygen_test", String.valueOf(oxygen_result));
                    oxygen_shortTimeStr = oxygen_sdf.format(oxygen_currTime);
                    OxygenTankFragment.flow_rate = oxygen_result;
                }




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
                    t1.speak(bloodPressure, TextToSpeech.QUEUE_FLUSH, null, "Test");
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
                } else if ((Arrays.stream(patientVitalsWords).allMatch(data.get(0)::contains))) {
                    t1.speak("Patient vital signs are as follows, " + "heart rate " + heartRate + "spo2" + spo2 + "temperature " + temp
                            + "blood pressure" + bloodPressure, TextToSpeech.QUEUE_FLUSH, null, "Test");
                } else if (remindersFlag) {
                    String time = data.get(0).replaceAll("\\D+","");
                    if (time.equals("")) {
                        t1.speak("Please give a time to set reminder", TextToSpeech.QUEUE_FLUSH, null, "Test");
                    } else {
                        if (data.get(0).contains("minute") || data.get(0).contains("minutes")){
                            second = Integer.valueOf(time) * 60;
                            t1.speak("setting reminder to check " + reminders[remindersIndex] + " in " + time + " minutes", TextToSpeech.QUEUE_FLUSH, null, "Test");
                        } else {
                            second = Integer.valueOf(time);
                            t1.speak("setting reminder to check " + reminders[remindersIndex] + " in " + time + " seconds", TextToSpeech.QUEUE_FLUSH, null, "Test");
                        }
                        reminder = second;
                        startTime = 0;
                        startTime = System.currentTimeMillis();
                        timerHandler.postDelayed(timerRunnable, 0);
                        remindersFlag = false;
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
        super.onPrepareOptionsMenu(menu);
        menu.getItem(1).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_mic_black_off));
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setSuggestionsAdapter(mAdapter);

        // Getting selected (clicked) item suggestion
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) mAdapter.getItem(position);
                String txt = cursor.getString(cursor.getColumnIndex("functions"));
                searchView.setQuery(txt, true);
                if (txt.equals("Stretcher Control")) {
                    ((NavigationHost) MainActivity.this).navigateTo(new StretcherControlFragment(), true);
                } else if (txt.equals("Oxygen Tank")) {
                    ((NavigationHost) MainActivity.this).navigateTo(new OxygenTankFragment(), true);
                } else if (txt.equals("Inventory Management")) {
                    ((NavigationHost) MainActivity.this).navigateTo(new InventoryMainFragment(), true);
                } else if (txt.equals("Dosage Cheatsheet")) {
                    startActivity(new Intent(MainActivity.this, DrugActivity.class));
                }
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                // Your code here
                return true;
            }
        });
        return true;
    }

    private void populateAdapter(String query) {
        final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, "functions" });
        for (int i=0; i<SUGGESTIONS.length; i++) {
            if (SUGGESTIONS[i].toLowerCase().startsWith(query.toLowerCase()))
                c.addRow(new Object[] {i, SUGGESTIONS[i]});
        }
        mAdapter.changeCursor(c);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.app_toolbar,menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("search", query);
                if (query.equals("stretcher control")) {
                    ((NavigationHost) MainActivity.this).navigateTo(new StretcherControlFragment(), true);
                } else if (query.equals("oxygen tank")) {
                    ((NavigationHost) MainActivity.this).navigateTo(new OxygenTankFragment(), true);
                } else if (query.equals("inventory management")) {
                    ((NavigationHost) MainActivity.this).navigateTo(new InventoryMainFragment(), true);
                } else if (query.equals("dosage cheatsheet")) {
                    startActivity(new Intent(MainActivity.this, DrugActivity.class));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                populateAdapter(newText);
                return false;
            }
        });
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