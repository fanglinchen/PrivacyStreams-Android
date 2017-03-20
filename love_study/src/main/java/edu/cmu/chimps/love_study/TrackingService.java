package edu.cmu.chimps.love_study;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.github.privacystreams.accessibility.BrowserSearch;
import com.github.privacystreams.accessibility.BrowserVisit;
import com.github.privacystreams.accessibility.SerializedAccessibilityNodeInfo;
import com.github.privacystreams.accessibility.TextEntry;
import com.github.privacystreams.accessibility.UIAction;
import com.github.privacystreams.commons.arithmetic.ArithmeticOperators;
import com.github.privacystreams.commons.comparison.Comparators;
import com.github.privacystreams.commons.item.ItemOperators;
import com.github.privacystreams.communication.Message;
import com.github.privacystreams.core.Function;
import com.github.privacystreams.core.Item;
import com.github.privacystreams.core.UQI;
import com.github.privacystreams.core.purposes.Purpose;
import com.github.privacystreams.device.DeviceEvent;
import com.github.privacystreams.device.DeviceState;
import com.github.privacystreams.environment.Light;
import com.github.privacystreams.location.GeoLocation;
import com.github.privacystreams.storage.DropboxOperators;
import com.github.privacystreams.utils.GlobalConfig;
import com.github.privacystreams.utils.time.Duration;
import com.google.android.gms.location.LocationRequest;

import edu.cmu.chimps.love_study.pam.PAMActivity;

/**
 * Created by fanglinchen on 3/16/17.
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class TrackingService extends Service {
    private  static final int NOTIFICATION_ID = 1234;
    private static final int WIFI_BT_SCAN_INTERVAL = 20*60*1000;
    private static final int POLLING_TASK_INTERVAL = 1*30*1000;
    UQI uqi;

//    public static void scheduleAllSurveyReminders(){
//
//        Reminder endOfTheDaySurveyReminder = new Reminder();
//        endOfTheDaySurveyReminder.hour = 22;
//        endOfTheDaySurveyReminder.minute = 0;
//        endOfTheDaySurveyReminder.type = REMINDER_TYPE_DAILY;
//        endOfTheDaySurveyReminder.url = Constants.URL.END_OF_THE_DAY_EMA_URL;
//        endOfTheDaySurveyReminder.notifText = "Self report";
//        endOfTheDaySurveyReminder.notifTitle = "Survey";
//
//        scheduleReminder(endOfTheDaySurveyReminder);
//
//        Reminder dailyRandomSurveyReminder = new Reminder();
//        dailyRandomSurveyReminder.type = REMINDER_TYPE_DAILY_RANDOM;
//        dailyRandomSurveyReminder.url = Constants.URL.DAILY_EMA_URL;
//        dailyRandomSurveyReminder.notifText = "Self report";
//        dailyRandomSurveyReminder.notifTitle = "Survey";
//
//        scheduleReminder(dailyRandomSurveyReminder);
//
//        Reminder weeklySurveyReminder = new Reminder();
//        weeklySurveyReminder.hour = 10;
//        weeklySurveyReminder.minute = 0;
//        weeklySurveyReminder.type = REMINDER_TYPE_DAILY;
//        weeklySurveyReminder.url = Constants.URL.WEEKLY_EMA_URL;
//        weeklySurveyReminder.notifText = "Self report";
//        weeklySurveyReminder.notifTitle = "Survey";
//
//        scheduleReminder(weeklySurveyReminder);
//
//    }

    private void surveyScheduling(){

    }
    private void setUpDropbox(){
        GlobalConfig.DropboxConfig.accessToken = uqi.getContext()
                .getResources().getString(R.string.dropbox_access_token);
        GlobalConfig.DropboxConfig.leastSyncInterval = Duration.seconds(3);
        GlobalConfig.DropboxConfig.onlyOverWifi = false;
    }

    private class PollingTask extends RepeatingTask{

        public PollingTask(int frequency) {
            super(frequency);
        }

        @Override
        protected void doWork() {
//         uqi.getData(Contact.asList(), Purpose.FEATURE("LoveStudy ContactList Collection"))
//                .forEach(DropboxOperators.<Item>uploadAs("ContactList"));

//         uqi.getData(CalendarEvent.asList(), Purpose.FEATURE("LoveStudy Calendar Event Collection"))
//                .forEach(DropboxOperators.<Item>uploadAs("CalendarEvent"));
//
//         uqi.getData(Image.readFromStorage(),Purpose.FEATURE("Love Study Image Collection"))
//                 .forEach(DropboxOperators.<Item>uploadAs("Image"));
//
//         uqi.getData(Phonecall.asLogs(),Purpose.FEATURE("Love Study Phonecall Collection"))
//                .debug();

        }
    }


    private void showNotification() {
        Intent notificationIntent = new Intent(this, PAMActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.heart);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Love study is running")
                .setSmallIcon(R.drawable.heart)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
        startForeground(NOTIFICATION_ID, notification);

    }


    public void collectData(){
        Log.e("TrackingService","Collecting Data");

        PollingTask pollingTask = new PollingTask(POLLING_TASK_INTERVAL);
        pollingTask.run();
        collectTextEntry();
        collectLocation();
        collectIM();
        collectNotifications();
        collectBrowserVisits();
        collectBrowserSearch();
        collectLightIntensity();
        collectUIAction();
        collectDeviceEvent();
        collectDeviceState();


    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("TrackingService","TrackingService");
        uqi = new UQI(this);
        if(intent!=null&&
                intent.getAction()!=null
                && intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)){
            showNotification();
            setUpDropbox();
            collectData();
        }
        return START_STICKY;
    }

    public void collectLocation(){
        uqi.getData(GeoLocation.asUpdates(Duration.minutes(2), Duration.minutes(1),
                LocationRequest.PRIORITY_HIGH_ACCURACY),
                Purpose.FEATURE("Collect GPS Coordinate Every 2 minutes"))
                .forEach(DropboxOperators.<Item>uploadAs("Location"));
    }

    public void collectNotifications(){
        uqi.getData(com.github.privacystreams.notification.Notification.asUpdates(), Purpose.FEATURE("Love Study Device State Collection"))
                .map(ItemOperators.setField("time_round", ArithmeticOperators.roundUp(DeviceEvent.TIMESTAMP, Duration.seconds(30))))
                .localGroupBy("time_round")
                .forEach(DropboxOperators.<Item>uploadAs("Notification"));
    }

    public void collectBrowserVisits(){
        uqi.getData(BrowserVisit.asUpdates(), Purpose.FEATURE("Love Study Browser Visit Collection"))
                .forEach(DropboxOperators.<Item>uploadAs("BrowserVisits"));
    }

    public void collectBrowserSearch(){
        uqi.getData(BrowserSearch.asUpdates(), Purpose.FEATURE("Love Study Browser Search Collection"))
                .forEach(DropboxOperators.<Item>uploadAs("BrowserSearches"));

    }

    public void collectLightIntensity(){
        uqi.getData(Light.asUpdates(),Purpose.FEATURE("Love Study Light Collection"))
                .filter(Comparators.lt(Light.INTENSITY, 50))
                .map(ItemOperators.setField("time_round", ArithmeticOperators.roundUp(Light.TIMESTAMP, Duration.minutes(1))))
                .localGroupBy("time_round")
                .forEach(DropboxOperators.<Item>uploadAs("DarkLight"));
    }
    public void collectUIAction(){
        uqi.getData(UIAction.asUpdates(), Purpose.FEATURE("Love Study UIAction Collection"))
                .setField("serialized_node", new Function<Item, String>() {
                    @Override
                    public String apply(UQI uqi, Item input) {
                        AccessibilityNodeInfo node = input.getValueByField(UIAction.ROOT_VIEW);
                        SerializedAccessibilityNodeInfo serialized = SerializedAccessibilityNodeInfo.serialize(node);
                        return uqi.getGson().toJson(serialized);
                    }
                })
                .map(ItemOperators.setField("time_round", ArithmeticOperators.roundUp(UIAction.TIME_CREATED, Duration.seconds(30))))
                .localGroupBy("time_round")
                .forEach(DropboxOperators.<Item>uploadAs("UIAction"));
    }

    public void collectDeviceEvent(){
        uqi.getData(DeviceEvent.asUpdates(),Purpose.FEATURE("Love Study Device State Collection"))
                .map(ItemOperators.setField("time_round", ArithmeticOperators.roundUp(DeviceEvent.TIME_CREATED, Duration.minutes(1))))
                .localGroupBy("time_round")
                .forEach(DropboxOperators.<Item>uploadAs("DeviceEvent"));
    }

    public void collectDeviceState(){
        uqi.getData(DeviceState.asUpdates(Duration.seconds(30), DeviceState.Masks.WIFI_AP_LIST
                        | DeviceState.Masks.BLUETOOTH_DEVICE_LIST | DeviceState.Masks.BATTERY_LEVEL),
                Purpose.FEATURE("Love Study Device State Collection"))
                .map(ItemOperators.setField("time_round", ArithmeticOperators.roundUp(DeviceState.TIME_CREATED, Duration.minutes(1))))
                .localGroupBy("time_round")
                .forEach(DropboxOperators.<Item>uploadAs("Device State"));;
    }

    public void collectIM(){
        uqi.getData(Message.asIMUpdates(), Purpose.FEATURE("LoveStudy Message Collection"))
                .map(ItemOperators.setField("time_round", ArithmeticOperators.roundUp(Message.TIMESTAMP, Duration.seconds(30))))
                .localGroupBy("time_round")
                .forEach(DropboxOperators.<Item>uploadAs("IM"));
//                .debug();
    }

    public void collectTextEntry(){
        uqi.getData(TextEntry.asUpdates(), Purpose.FEATURE("Love Study Text Entry Collection"))
//                .map(ItemOperators.setField("time_round", ArithmeticOperators.roundUp(TextEntry.TIME_CREATED, Duration.minutes(1))))
//                .localGroupBy("time_round")
                .forEach(DropboxOperators.<Item>uploadAs("TextEntry"));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
