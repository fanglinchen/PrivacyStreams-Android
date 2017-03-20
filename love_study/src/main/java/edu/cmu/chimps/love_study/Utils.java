package edu.cmu.chimps.love_study;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.github.privacystreams.accessibility.MyAccessibilityService;

/**
 * Created by fanglinchen on 3/17/17.
 */

public class Utils {
    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public static boolean isAccessibilityEnabled(Context context) {
        return isMyServiceRunning(context, MyAccessibilityService.class);
    }

    public static boolean isTrackingEnabled(Context context){
        return isMyServiceRunning(context, TrackingService.class);
    }

    public static String getParticipantID(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPref.getString(context.getResources().getString(R.string.shared_preference_key_participant_id),null);
    }

    public static String getFriendInitialsList(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPref.getString(context.getResources().getString(R.string.shared_preference_key_participant_id),null);
    }

}
