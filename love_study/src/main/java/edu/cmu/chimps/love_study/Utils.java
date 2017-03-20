package edu.cmu.chimps.love_study;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.github.privacystreams.accessibility.MyAccessibilityService;

import java.util.Random;
import java.util.Set;

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

    public static String getPartnerInitial(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPref.getString(context.getResources().getString(R.string.shared_preference_key_partner_initial),null);
    }

    public static String randomlySelectFriendInitial(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        Set<String> set = sharedPref.getStringSet(context.getResources().getString(R.string.friends_key),null);
        if(set==null){
            return null;
        }

        int size = set.size();
        int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
        int i = 0;
        for(String randomlySelected : set)
        {
            if (i == item)
                return randomlySelected;
            i++;
        }
        return null;
    }

}
