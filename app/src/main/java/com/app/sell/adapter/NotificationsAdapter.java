package com.app.sell.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.sell.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotificationsAdapter extends BaseAdapter {

    private final List<NotificationItem> mNotificationsList = new ArrayList<NotificationItem>();
    private final LayoutInflater mInflater;

    public NotificationsAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        for(int i=0; i<15; i++) {
            mNotificationsList.add(new NotificationItem("Message", "How does this price sound? " + new Random().nextInt()*(i+1) % 2000, R.drawable.mv));
        }
    }

    @Override
    public int getCount() {
        return mNotificationsList.size();
    }

    @Override
    public Object getItem(int i) {
        return mNotificationsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mNotificationsList.get(i).drawableId;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView icon;
        TextView description;
        TextView type;

        if (v == null) {
            v = mInflater.inflate(R.layout.notification_item, viewGroup, false);
            v.setTag(R.id.notification_icon, v.findViewById(R.id.notification_icon));
            v.setTag(R.id.notification_type, v.findViewById(R.id.notification_type));
            v.setTag(R.id.notification_description, v.findViewById(R.id.notification_description));
        }

        icon = (ImageView) v.getTag(R.id.notification_icon);
        description = (TextView) v.getTag(R.id.notification_description);
        type = (TextView) v.getTag(R.id.notification_type);

        NotificationItem item = (NotificationItem) getItem(i);

        icon.setImageResource(item.drawableId);
        description.setText(item.description);
        type.setText(item.type);

        v.setPadding(8, 8, 8, 8);

        return v;
    }

    static class NotificationItem {
        public final String type;
        public final String description;
        public final int drawableId;

        public NotificationItem(String type, String description, int drawableId) {
            this.type = type;
            this.description = description;
            this.drawableId = drawableId;
        }
    }
}
