package com.app.sell.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.util.Log;

import com.app.sell.NotificationsActivity_;
import com.app.sell.R;
import com.app.sell.dao.MessageDao;
import com.app.sell.model.Chatroom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.api.support.app.AbstractIntentService;

import java.util.HashMap;
import java.util.Map;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
@EIntentService
public class MessageNotificationService extends AbstractIntentService {

    private static final String TAG = "MessageNotificationServ";
    public static final int NOTIFICATION_ID = 9002;
    private static final String CHANNEL_NAME = "message_channel";

    @SystemService
    NotificationManager notificationManager;

    @Bean
    MessageDao messageDao;

    public MessageNotificationService() {
        super(MessageNotificationService.class.getSimpleName());
    }

    @ServiceAction
    void handleOfferMessage(String title, String text, int resourceId) {
        Notification notification = createOfferNotification(title, text, resourceId);
        notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_NAME, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH));
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @ServiceAction
    void handleChatMessage(String title, String message, Chatroom chatroom, int numMessages, int resourceId) {
        Log.d(TAG, "handleChatMessage: making notification from message: " + message);
        Notification notification = createChatNotification(title, message, chatroom, numMessages, R.mipmap.ic_launcher);
        notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_NAME, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH));
        notificationManager.notify(Integer.parseInt(chatroom.getChatroomId().replaceAll("[^0-9]", "")), notification);
    }

    private Notification createOfferNotification(String title, String text, int resourceId) {
        return new Notification.Builder(this)
                .setSmallIcon(resourceId)
                .setLargeIcon(Icon.createWithResource(this, resourceId))
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setChannelId(CHANNEL_NAME)
                .setContentIntent(createPendingIntent())
                .build();
    }

    private Notification createChatNotification(String title, String message, Chatroom chatroom, int pendingMessages, int resourceId) {
        return new Notification.Builder(this)
                .setSmallIcon(resourceId)
                .setLargeIcon(Icon.createWithResource(this, resourceId))
                .setContentTitle(title)
                .setContentText("New messages in: " + chatroom.getChatroomName())
                .setSubText(message)
                .setStyle(new Notification.BigTextStyle()
                            .bigText("New messages in: " + chatroom.getChatroomName()).setSummaryText(message))
                .setNumber(pendingMessages)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setChannelId(CHANNEL_NAME)
                .setContentIntent(createPendingIntent())
                .build();
    }

    private PendingIntent createPendingIntent() {
        PendingIntent pendingIntent;
        Intent intent = new Intent(this, NotificationsActivity_.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntent(intent);
        pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }
}
