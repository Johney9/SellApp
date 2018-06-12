package com.app.sell.view;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.sell.R;
import com.app.sell.model.NotificationItem;
import com.bumptech.glide.Glide;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.text.DateFormat;

@EViewGroup(R.layout.notification_item)
public class NotificationItemView extends RelativeLayout {

    @ViewById(R.id.notification_title)
    TextView titleView;
    @ViewById(R.id.notification_description)
    TextView descriptionView;
    @ViewById(R.id.notification_timestamp)
    TextView timestampView;
    @ViewById(R.id.notification_username)
    TextView usernameView;
    @ViewById(R.id.notification_icon)
    ImageView iconView;

    public NotificationItemView(Context context) {
        super(context);
    }

    public void bind(NotificationItem notification) {
        titleView.setText(notification.getTitle());
        descriptionView.setText(notification.getDescription());
        usernameView.setText(notification.getUsername());
        String formattedDateTime = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, getResources().getConfiguration().getLocales().get(0)).format(notification.getTimestamp());
        timestampView.setText(formattedDateTime);
        Glide.with(getContext()).load(notification.getIconUri()).into(iconView);
    }
}
