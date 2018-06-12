package com.app.sell.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.app.sell.view.ViewWrapper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerViewAdapterBase<T, V extends View> extends RecyclerView.Adapter<ViewWrapper<V>> {

    protected List<T> items = new ArrayList<>();

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public final ViewWrapper<V> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewWrapper<V>(onCreateItemView(parent, viewType));
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public T getItem(int position) {
        return items.get(position);
    }

    protected abstract V onCreateItemView(ViewGroup parent, int viewType);

    @Override
    public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        EventBus.getDefault().register(this);
    }

    @Override
    public void unregisterAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
        EventBus.getDefault().unregister(this);
    }
}
