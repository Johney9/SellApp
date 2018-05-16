package com.app.sell.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.drawable.Icon;

import com.app.sell.NotificationsActivity_;
import com.app.sell.dao.MessageDao;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EIntentService;
import org.androidannotations.annotations.ServiceAction;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.api.support.app.AbstractIntentService;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
@EIntentService
public class MessageNotificationService extends AbstractIntentService {

    public static final int NOTIFICATION_ID = 9002;

    @SystemService
    NotificationManager notificationManager;

    @Bean
    MessageDao messageDao;

    public MessageNotificationService() {
        super(MessageNotificationService.class.getSimpleName());
    }

    @ServiceAction
    void handleMessage(String title, String text, Icon icon) {
        Notification notification = createNotification(title, text, icon);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private Notification createNotification(String title, String text, Icon icon) {
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(createPendingIntent())
                .build();

        return notification;
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
