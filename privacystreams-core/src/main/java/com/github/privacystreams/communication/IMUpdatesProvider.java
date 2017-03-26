package com.github.privacystreams.communication;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.github.privacystreams.accessibility.BaseAccessibilityEvent;
import com.github.privacystreams.commons.comparison.Comparators;
import com.github.privacystreams.commons.item.ItemOperators;
import com.github.privacystreams.core.Callback;
import com.github.privacystreams.core.Item;
import com.github.privacystreams.core.providers.MStreamProvider;
import com.github.privacystreams.core.purposes.Purpose;
import com.github.privacystreams.utils.AccessibilityUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by fanglinchen on 1/31/17.
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
class IMUpdatesProvider extends MStreamProvider {
    private int totalNumberOfMessages=0;
    private int result=0;
    private String detPackage = "";
    private String detContactName = "";

    public static final String APP_PACKAGE_WHATSAPP = "com.whatsapp";
    public static final String APP_PACKAGE_FACEBOOK_MESSENGER = "com.facebook.orca";


    private void saveMessage(int eventItemCount, List<AccessibilityNodeInfo> nodeInfoList, String contactName, String packageName) {
        totalNumberOfMessages += 1;
//        AccessibilityNodeInfo nodeInfo = nodeInfoList.get(nodeInfoList.size() - 1);
//        String messageContent = nodeInfoList.get(nodeInfoList.size() - 1).getText().toString();
//        Log.e("new message "+totalNumberOfMessages,messageContent);
        ArrayList<String> messageType = new ArrayList<>();
        ArrayList<String> messages = new ArrayList<>();
        for(AccessibilityNodeInfo nodeInfo: nodeInfoList){
            messageType.add(AccessibilityUtils.isIncomingMessage(nodeInfo,packageName) ?
                    Message.Types.RECEIVED : Message.Types.SENT);
            messages.add(nodeInfo.getText().toString());
        }
//        String messageType = AccessibilityUtils.isIncomingMessage(nodeInfo,packageName) ?
//                Message.Types.RECEIVED : Message.Types.SENT;
        this.output(new Message(eventItemCount,messageType,messages,packageName,
                contactName,System.currentTimeMillis()));
    }

    @Override
    protected void provide() {
        getUQI().getData(BaseAccessibilityEvent.asUpdates(),
                Purpose.INTERNAL("Event Triggers"))
                .filter(ItemOperators.isFieldIn(BaseAccessibilityEvent.PACKAGE_NAME,
                        new String[]{APP_PACKAGE_WHATSAPP, APP_PACKAGE_FACEBOOK_MESSENGER}))
//                .filter(Comparators.eq(BaseAccessibilityEvent.PACKAGE_NAME,APP_PACKAGE_WHATSAPP))
                .filter(Comparators.eq(BaseAccessibilityEvent.EVENT_TYPE,
                        AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED))
                .filter(Comparators.gt(BaseAccessibilityEvent.ITEM_COUNT, 2))
                .forEach(new Callback<Item>() {
                    @Override
                    protected void onSuccess(Item input) {

                        AccessibilityNodeInfo rootView =
                                input.getValueByField(BaseAccessibilityEvent.ROOT_VIEW);
                        String packageName = input.getValueByField(BaseAccessibilityEvent.PACKAGE_NAME);
                        if(!packageName.equals(detPackage)){
                            Log.e("new app", packageName);
                            Log.e("last app", detPackage);
                            totalNumberOfMessages=0;
                        }

                        detPackage=packageName;
                        String contactName = AccessibilityUtils
                                .getContactNameInChat(rootView,packageName);
                        if(contactName==null) {
                            Log.e("cannot get","contact name");
                            return;
                        }

                        if(!contactName.equals(detContactName)){
                            Log.e("new contact name", contactName);
                            Log.e("last contact name", detContactName);
                            totalNumberOfMessages=0;
                        }
                        detContactName=contactName;

                        List<AccessibilityNodeInfo> nodeInfos =
                                AccessibilityUtils.getMessageList(rootView,packageName);
                        if(nodeInfos==null || nodeInfos.size()==0){
                            Log.e("nodeInfo","empty");
                            return;
                        }

                        int eventItemCount = getEventItemCount(packageName,input);

                        if(totalNumberOfMessages==0){
                            totalNumberOfMessages = eventItemCount;
//                            Log.e("totalNumberOfMessages",totalNumberOfMessages+"");
                        }
                        else if (eventItemCount - totalNumberOfMessages > 0) {
                            Log.e("add","new message");
                            saveMessage(eventItemCount,nodeInfos,
                                    contactName,packageName);

                        }
//                        else{
//                            Log.e("totalNumberOfMessages",totalNumberOfMessages+"");
//                            Log.e("eventItemCount",eventItemCount+"");
//                        }
                    }
                });

    }

    public int getEventItemCount(String pckName,  Item input){
        int temp = input.getValueByField(BaseAccessibilityEvent.ITEM_COUNT);
        if(pckName.equals(APP_PACKAGE_WHATSAPP)){
            result=temp-2;
        }else if(pckName.equals(APP_PACKAGE_FACEBOOK_MESSENGER)){
            result=temp-1;
        }
        return result;
    }

}