package com.app.sell.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.app.sell.AskActivity_;
import com.app.sell.R;
import com.app.sell.dao.NotificationsDao;
import com.app.sell.events.NotificationsUpdatedEvent;
import com.app.sell.model.NotificationItem;
import com.app.sell.view.NotificationItemView;
import com.app.sell.view.NotificationItemView_;
import com.app.sell.view.ViewWrapper;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@EBean
public class NotificationAdapter extends RecyclerViewAdapterBase<NotificationItem, NotificationItemView> {

    private static final String TAG = "NotificationAdapter";
    @RootContext
    Context context;
    @Bean
    NotificationsDao notificationsDao;

    @Subscribe(sticky = true, threadMode = ThreadMode.POSTING)
    public void updateData(NotificationsUpdatedEvent notificationsUpdatedEvent) {
        this.items = notificationsDao.getNotifications();
        Log.d(TAG, "updateData: items: " + String.valueOf(items));
        this.notifyDataSetChanged();
    }

    @Override
    protected NotificationItemView onCreateItemView(ViewGroup parent, int viewType) {
        return NotificationItemView_.build(context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewWrapper<NotificationItemView> holder, int position) {
        NotificationItemView view = holder.getView();
        final NotificationItem notification = items.get(position);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationAdapter.this.onClick(notification);
            }
        });
        view.bind(notification);
    }

    private void onClick(NotificationItem notification) {
        AskActivity_.intent(context).extra(context.getString(R.string.field_chatroom_id), notification.getChatroomId()).start();
    }
}
