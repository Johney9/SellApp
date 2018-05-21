package com.app.sell.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.util.Log;

import com.app.sell.MainActivity_;
import com.app.sell.NotificationsActivity_;
import com.app.sell.dao.MessageDao;
import com.app.sell.model.Chatroom;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.api.support.app.AbstractIntentService;

import lombok.NonNull;


@SuppressLint("Registered")
@EIntentService
public class MessageNotificationService extends AbstractIntentService {

    private static final String TAG = "MessageNotificationServ";
    private static final String CHANNEL_NAME = "message_channel";

    @SystemService
    NotificationManager notificationManager;

    @Bean
    MessageDao messageDao;

    public MessageNotificationService() {
        super(MessageNotificationService.class.getSimpleName());
    }

    @ServiceAction
    void handleOfferMessage(String title, String text, @NonNull String offerId, int resourceId) {
        Notification.Builder notificationBuilder = createOfferNotificationBuilder(title, text, resourceId);
        int messageId = offerId.hashCode();
        buildAndSendNotification(notificationBuilder, messageId);
    }

    @ServiceAction
    void handleChatMessage(String title, String message, Chatroom chatroom, int newMessages, int resourceId) {
        Log.d(TAG, "handleChatMessage: making notification from message: " + message);
        int messageId = chatroom.getChatroomId().hashCode();
        Notification.Builder notificationBuilder = createChatNotificationBuilder(title, message, chatroom, newMessages, resourceId);
        buildAndSendNotification(notificationBuilder, messageId);
    }

    private Notification.Builder createChatNotificationBuilder(String title, String message, Chatroom chatroom, int newMessages, int resourceId) {
        return createNotificationBuilderBase(title, resourceId)
                    .setContentText("New messages in: " + chatroom.getChatroomName())
                    .setSubText(message)
                    .setStyle(new Notification.BigTextStyle()
                        .bigText("New messages in: " + chatroom.getChatroomName()).setSummaryText(message))
                    .setNumber(newMessages);
    }

    private void buildAndSendNotification(Notification.Builder notificationBuilder, int notificationId) {

        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_NAME, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH));
            notificationBuilder.setChannelId(CHANNEL_NAME);
        }
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    private Notification.Builder createOfferNotificationBuilder(String title, String text, int resourceId) {
        return createNotificationBuilderBase(title, resourceId).setContentText(text);
    }

    private Notification.Builder createNotificationBuilderBase(String title, int resourceId) {
        return new Notification.Builder(this)
                .setSmallIcon(resourceId)
                .setLargeIcon(Icon.createWithResource(this, resourceId))
                .setContentTitle(title)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setContentIntent(createPendingIntent());
    }

    private PendingIntent createPendingIntent() {
        PendingIntent pendingIntent;
        Intent intent = new Intent(this, MainActivity_.class);
        intent.putExtra("forwardToActivity", NotificationsActivity_.class.getName());
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntent(intent);
        pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }
}
