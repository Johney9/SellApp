package com.app.sell.events;

import com.app.sell.model.Offer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OfferLoadedEvent {
    public final Offer offer;
}
