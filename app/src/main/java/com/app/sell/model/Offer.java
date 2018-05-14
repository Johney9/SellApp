package com.app.sell.model;

import lombok.Data;

@Data
public class Offer {

    private String id;
    private String title;
    private Double price;
    private String description;
    private String location;
    private String image;
    private String offererId;

}
