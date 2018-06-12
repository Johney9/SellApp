package com.app.sell.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import com.app.sell.MainActivity_;
import com.app.sell.NotificationsActivity_;
import com.app.sell.dao.ChatMessageDao;
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
    ChatMessageDao messageDao;

    public MessageNotificationService() {
        super(MessageNotificationService.class.getSimpleName());
    }

    @ServiceAction
    void handleOfferMessage(String title, String text, @NonNull String offerId, int resourceId) {
        Builder notificationBuilder = createOfferNotificationBuilder(title, text, resourceId);
        int messageId = offerId.hashCode();
        buildAndSendNotification(notificationBuilder, messageId);
    }

    @ServiceAction
    void handleChatMessage(String title, String message, Chatroom chatroom, int newMessages, int resourceId) {
        Log.d(TAG, "handleChatMessage: making notification from message: " + message);
        int messageId = chatroom.getId().hashCode();
        Builder notificationBuilder = createChatNotificationBuilder(title, message, chatroom, newMessages, resourceId);
        buildAndSendNotification(notificationBuilder, messageId);
    }

    private Builder createChatNotificationBuilder(String title, String message, Chatroom chatroom, int newMessages, int resourceId) {
        String summaryText = "New messages in: " + chatroom.getChatroomName();
        return createNotificationBuilderBase(title, resourceId)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), resourceId))
                .setContentText(message)
                .setSubText(summaryText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setNumber(newMessages)
                .setOnlyAlertOnce(true);
    }

    private void buildAndSendNotification(Builder notificationBuilder, int notificationId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_NAME, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH));
            notificationBuilder.setChannelId(CHANNEL_NAME);
        }
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    private Builder createOfferNotificationBuilder(String title, String text, int resourceId) {
        return createNotificationBuilderBase(title, resourceId).setContentText(text);
    }

    @SuppressWarnings("")
    private Builder createNotificationBuilderBase(String title, int resourceId) {
        return new Builder(this, CHANNEL_NAME)
                .setSmallIcon(resourceId)
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
