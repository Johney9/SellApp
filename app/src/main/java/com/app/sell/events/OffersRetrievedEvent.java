package com.app.sell.events;

import com.app.sell.model.Offer;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OffersRetrievedEvent {

    public final List<Offer> offers;
}
