package edu.cmu.chimps.love_study;

import android.app.IntentService;
import android.content.Intent;
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
import com.github.privacystreams.utils.time.Duration;
import com.google.android.gms.location.LocationRequest;

/**
 * Created by fanglinchen on 3/19/17.
 */

public class DataCollectingIntentService extends IntentService {
    UQI uqi;
    private static final int WIFI_BT_SCAN_INTERVAL = 20*60*1000;
    public DataCollectingIntentService() {
        super("DataCollectingIntentService");
    }
    public DataCollectingIntentService(String name) {
        super(name);
    }
    public void collectNotifications(){
        uqi.getData(com.github.privacystreams.notification.Notification.asUpdates(), Purpose.FEATURE("Love Study Device State Collection"))
                .map(ItemOperators.setField("time_round", ArithmeticOperators.roundUp(DeviceEvent.TIMESTAMP, Duration.seconds(30))))
                .localGroupBy("time_round")
                .debug();
    }

    public void collectBrowserVisits(){
        uqi.getData(BrowserVisit.asUpdates(), Purpose.FEATURE("Love Study Browser Visit Collection"))
                .debug();
    }

    public void collectBrowserSearch(){
        uqi.getData(BrowserSearch.asUpdates(), Purpose.FEATURE("Love Study Browser Search Collection"))
                .debug();

    }

    public void collectLocation(){
        uqi.getData(GeoLocation.asUpdates(Duration.minutes(2), Duration.minutes(1),
                LocationRequest.PRIORITY_HIGH_ACCURACY), Purpose.FEATURE("Collect GPS Coordinate Every 2 minutes"))
                .forEach(DropboxOperators.<Item>uploadAs("Location"));
    }

    public void collectLightIntensity(){
        uqi.getData(Light.asUpdates(),Purpose.FEATURE("Love Study Light Collection"))
                .filter(Comparators.lt(Light.INTENSITY, 50))
                .map(ItemOperators.setField("time_round", ArithmeticOperators.roundUp(Light.TIMESTAMP, Duration.minutes(1))))
                .localGroupBy("time_round")
                .debug();
    }


    public void collectTextEntry(){
        uqi.getData(TextEntry.asUpdates(), Purpose.FEATURE("Love Study Text Entry Collection"))
//                .map(ItemOperators.setField("time_round", ArithmeticOperators.roundUp(TextEntry.TIME_CREATED, Duration.minutes(1))))
//                .localGroupBy("time_round")
                .forEach(DropboxOperators.<Item>uploadAs("TextEntry"));
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
                .debug();
    }

    public void collectDeviceEvent(){
        uqi.getData(DeviceEvent.asUpdates(),Purpose.FEATURE("Love Study Device State Collection"))
                .map(ItemOperators.setField("time_round", ArithmeticOperators.roundUp(DeviceEvent.TIMESTAMP, Duration.minutes(1))))
                .localGroupBy("time_round")
                .debug();
    }

    public void collectDeviceState(){
        uqi.getData(DeviceState.asUpdates(WIFI_BT_SCAN_INTERVAL, DeviceState.Masks.WIFI_AP_LIST
                        | DeviceState.Masks.BLUETOOTH_DEVICE_LIST | DeviceState.Masks.BATTERY_LEVEL),
                Purpose.FEATURE("Love Study Device State Collection"))
                .map(ItemOperators.setField("time_round", ArithmeticOperators.roundUp(DeviceState.TIME_CREATED, Duration.minutes(1))))
                .localGroupBy("time_round")
                .forEach(DropboxOperators.<Item>uploadAs("Device State"));;
    }

    public void collectIM(){
        uqi.getData(Message.asIMUpdates(), Purpose.FEATURE("LoveStudy Message Collection"))
                .forEach(DropboxOperators.<Item>uploadAs("IM"));
//                .map(ItemOperators.setField("time_round", ArithmeticOperators.roundUp(Message.TIMESTAMP, Duration.minutes(1))))
//                .localGroupBy("time_round")
//                .debug();
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        uqi = new UQI(this);
        collectIM();
        collectNotifications();
        collectBrowserVisits();
        collectBrowserSearch();
//        collectLocation();
        collectLightIntensity();
        collectTextEntry();
        collectUIAction();
        collectDeviceEvent();
        collectDeviceState();
    }
}
