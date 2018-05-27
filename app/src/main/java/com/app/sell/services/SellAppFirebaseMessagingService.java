package com.app.sell.services;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.util.Log;

import com.app.sell.R;
import com.app.sell.dao.ChatMessageDao;
import com.app.sell.events.ChatNotificationEvent;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Map;

@SuppressLint("Registered")
@EService
public class SellAppFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "SellAppFirebaseMessaging";

    @Bean
    ChatMessageDao chatMessageDao;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> notificationData;
        try {
            notificationData = remoteMessage.getData();

        } catch (NullPointerException npe) {
            Log.e(TAG, "onMessageReceived: NullPointerException", npe);
            return;
        }

        Log.d(TAG, "onMessageReceived: message data: " + notificationData);

        String title = notificationData.get(getString(R.string.message_data_title));
        String message = notificationData.get(getString(R.string.message_data_message));
        String identifyDataType = notificationData.get(getString(R.string.message_data_type));
        String iconUri = notificationData.get(getString(R.string.message_data_icon_uri));

        if(identifyDataType.contains(getString(R.string.data_type_chat_message)) && !isChatActivityRunning()) {

            String chatroomId = notificationData.get(getString(R.string.message_data_chatroom_id));
            chatMessageDao.processChatMessageForNotification(title, message, chatroomId, iconUri);

        } else if(identifyDataType.contains(getString(R.string.data_type_offer_message))) {

            String offerId = notificationData.get(getString(R.string.field_offer_id));
            buildAndSendOfferNotification(title, message, offerId);

        }
    }


    protected Boolean isChatActivityRunning() {
        SharedPreferences sp = getSharedPreferences(getString(R.string.ask_activity_shared_pref), MODE_PRIVATE);
        return sp.getBoolean("active", false);
    }

    private void buildAndSendOfferNotification(String title, String message, String offerId) {
        MessageNotificationService_.intent(this).handleOfferMessage(title, message, offerId, R.mipmap.ic_launcher).start();
    }

    @Subscribe
    public void buildAndSendChatNotification(ChatNotificationEvent chatRecievedEvent) {
        MessageNotificationService_.intent(this).handleChatMessage(chatRecievedEvent.title, chatRecievedEvent.message, chatRecievedEvent.chatroom, chatRecievedEvent.newMessageNumber, R.mipmap.ic_launcher).start();
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
