package com.example.assignment2;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityRecognizedService extends IntentService {

    private Activity currentActivity = new Activity();

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
    }

    public ActivityRecognizedService(String name){
        super(name);
    }

    SimpleDateFormat ft = new SimpleDateFormat("hh:mm");
    public static String detect;

    @Override
    protected void onHandleIntent(Intent intent){
        if(intent.getAction().equals("com.example.assignment2.ACTIVITY_RECOGNIZED_SERVICE")) {
            if (ActivityRecognitionResult.hasResult(intent)) {
                ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
                handleDetectedActivity(result.getMostProbableActivity());
            }
        }
    }

    private void handleDetectedActivity(DetectedActivity detectedActivity) {
        Log.e("ActivityRecognition", "Start detect.");
        int activityType = detectedActivity.getType();
        if(activityType == DetectedActivity.STILL){
            detect = "Still";
            Log.e("ActivityRecognition", detect);
            System.out.println("Still");
            if(checkActivitySwitch(detect)) {
                storeAndReturn(detect);
            }

        } else if(activityType == DetectedActivity.IN_VEHICLE){
            detect = "In Vehicle";
            Log.e("ActivityRecognition", "In Vehicle.");
            System.out.println("In Vehicle");
            if(checkActivitySwitch(detect)) {
                storeAndReturn(detect);
            }

        } else if(activityType == DetectedActivity.RUNNING){
            detect = "Running";
            Log.e("ActivityRecognition", "Running.");
            System.out.println("Running");
            if(checkActivitySwitch(detect)) {
                storeAndReturn(detect);
            }

        } else if(activityType == DetectedActivity.WALKING){
            detect = "Walking";
            Log.e("ActivityRecognition", "Walking.");
            System.out.println("Walking");
            if(checkActivitySwitch(detect)) {
                storeAndReturn(detect);
            }

        }
        System.out.println("The result of this detect is: "+ detect);
    }

    public void storeAndReturn(String detect){
        Date currentTime = new Date();
        currentActivity.setTime(ft.format(currentTime));
        currentActivity.setType(detect);
        storeDB(currentActivity);
        Intent intent = new Intent(this, RecognizeActivity.class);
        intent.putExtra("Type", detect);
        startActivity(intent);
    }

    public void storeDB(Activity activity){
        SimpleDatabase db = new SimpleDatabase(this);
        long id = db.addActivity(activity);
        System.out.println("stored: "+ id + ", Activity: " + activity.getType()+ ", Time: " + activity.getTime());
    }

    public boolean checkActivitySwitch(String s){
        if(s.equals(RecognizeActivity.previousActivity)){
            return false;
        } else
            return true;
    }

}
