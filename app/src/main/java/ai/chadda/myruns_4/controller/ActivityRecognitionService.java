package ai.chadda.myruns_4.controller;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

public class ActivityRecognitionService extends IntentService {

    // Global flags
    protected static final String TAG = "Activity";
    public static Boolean WALK_FLAG = false;
    public static Boolean RUN_FLAG = false;
    public static Boolean STILL_FLAG = false;
    public static Boolean BIKE_FLAG = false;
    public static Boolean IDK_FLAG = false;

    // Global data
    public static int walkCount = 0;
    public static int standingCount = 0;
    public static int runningCount = 0;
    public static int cyclingCount = 0;
    public static int unknownCount = 0;

    public ActivityRecognitionService() {
        super(TAG);
    }

    @Override
    // Starts activity recognition
    protected void onHandleIntent(@Nullable Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            activityDetected(result.getProbableActivities());
        }
    }

    // Track activity probabilities
    private void activityDetected(List<DetectedActivity> activities) {
        for (DetectedActivity activity: activities) {
            switch (activity.getType()) {
                case DetectedActivity.WALKING: {
                    if (activity.getConfidence() > 60) {
                        walkCount++;
                        WALK_FLAG = true;
                        RUN_FLAG = false;
                        STILL_FLAG = false;
                        BIKE_FLAG = false;
                        IDK_FLAG = false;
                    }
                    break;
                }
                case DetectedActivity.STILL: {
                    if (activity.getConfidence() > 60) {
                        standingCount++;
                        WALK_FLAG = false;
                        RUN_FLAG = false;
                        STILL_FLAG = true;
                        BIKE_FLAG = false;
                        IDK_FLAG = false;
                    }
                    break;
                }
                case DetectedActivity.RUNNING: {
                    if (activity.getConfidence() > 60) {
                        runningCount++;
                        WALK_FLAG = false;
                        RUN_FLAG = true;
                        STILL_FLAG = false;
                        BIKE_FLAG = false;
                        IDK_FLAG = false;
                    }
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    if (activity.getConfidence() > 60) {
                        cyclingCount++;
                        WALK_FLAG = false;
                        RUN_FLAG = false;
                        STILL_FLAG = false;
                        BIKE_FLAG = true;
                        IDK_FLAG = false;
                    }
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    if (activity.getConfidence() > 60) {
                        unknownCount++;
                        WALK_FLAG = false;
                        RUN_FLAG = false;
                        STILL_FLAG = false;
                        BIKE_FLAG = false;
                        IDK_FLAG = true;
                    }
                    break;
                }

            }
        }
    }
}
