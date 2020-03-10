package com.example.assignment2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RecognizeActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public GoogleApiClient mApiClient;
    SimpleDateFormat ft = new SimpleDateFormat("hh:mm");
    public static TextView aType;
    public static ImageView aImage;
    public static String previousActivity;
    public static long detectCount = 0;
    private static boolean isPlay = false;
    private MediaPlayer mp = MediaPlayer.create(this, R.raw.bgm);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);
        aType = findViewById(R.id.activityType);
        aImage = findViewById(R.id.activityImage);

        // Builds single client object that connects to Drive and Google+
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                //.addScope(SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        //mApiClient.connect();

        Intent it = getIntent();
        String detectType = it.getStringExtra("Type");
        handleDetectType(detectType);

    }

    public void handleDetectType(String detectType){
        if(detectType != previousActivity){
            if(detectType.equals("Running")){
                // play music from the device musing list
                mp.start();
                isPlay = true;
            } else if(detectType.equals("Walking") || detectType.equals("In Vehicle")){
                // display a map of current location, and show continuous movement
                if(isPlay == true){
                    mp.stop();
                    isPlay = false;
                }
            }
            if(isPlay == true){
                mp.stop();
                isPlay = false;
            }
            aType.setText("Current Activity: "+detectType);
            setActivityImage(detectType);
            SimpleDatabase db = new SimpleDatabase(this);
            long span = 0;
            Activity lastActivity = db.getLastActivity();
            if ((lastActivity.getType() != previousActivity) && (detectCount >1)) {

                if (lastActivity.getTime() != null) {
                    span = calculateTimeSpan(lastActivity);
                }
                if (span != 0) {
                    String toast = span + ", " + lastActivity.getType();
                    Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
                }
            }
            db.close();
            previousActivity = detectType;
        } else if(detectType != null){
            aType.setText("Current Activity: "+detectType);
            setActivityImage(detectType);
        }
        detectCount++;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect to Drive and Google+
        mApiClient.connect();
        //Log.e("onStart","START");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Enqueue operation.
        // This operation will be enqueued and issued once the API clients are connected.
        // Only API retrieval operations are allowed.
        // Asynchronous callback required to not lock the UI thread.
        //Plus.PeopleApi.load(mApiClient, "me", "you", "that").setResultCallback(this);
        //Log.e("onResume","RESUME");
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        //System.out.println("current previousActivity: " + previousActivity);
        //System.out.println("current detectCount: " + detectCount);

        Intent intent = new Intent(this, ActivityRecognizedService.class);
        intent.setAction("com.example.assignment2.ACTIVITY_RECOGNIZED_SERVICE");
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //System.out.println("PendingIntent is created.");
        // Set the interval for how often the API should check the user's activity
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 100, pendingIntent);
        //System.out.println("requestActivityUpdates is set.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // At least one of the API client connect attempts failed
        // No client is connected
        // ...
        //System.out.println("Connection is failed.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        // ...
    }

    public long calculateTimeSpan(Activity activity) {
        Date start = null;
        Date stop = new Date();
        try {
            start = ft.parse(activity.getTime());
            stop = ft.parse(ft.format(stop));
        } catch (Exception e) {
            e.printStackTrace();
        }
        long startTime = start.getTime();
        long stopTime = stop.getTime();
        long timeSpan = stopTime - startTime;
        return timeSpan;
    }

    public static void setActivityImage(String s){
        if(s.equals("Still")){
            aImage.setImageResource(R.drawable.still);
        } else if(s.equals("In Vehicle")){
            aImage.setImageResource(R.drawable.vehicle);
        } else if(s.equals("Running")){
            aImage.setImageResource(R.drawable.running);
        } else if(s.equals("Walking")){
            aImage.setImageResource(R.drawable.walking);
        }
    }
}