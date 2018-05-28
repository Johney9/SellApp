package com.app.sell.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import lombok.Getter;

public class ViewWrapper<V extends View> extends RecyclerView.ViewHolder {

    @Getter
    private V view;

    public ViewWrapper(V itemView) {
        super(itemView);
        view = itemView;
    }
}
