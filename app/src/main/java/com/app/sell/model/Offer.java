package com.app.sell.model;

import lombok.Data;

@Data
public class Offer {

    private String id;
    private String title;
    private Double price;
    private Boolean firmOnPrice;
    private String condition;
    private String description;
    private String location;
    private String image;
    private String offererId;
    private Long timestamp;
    private String categoryId;
    private Boolean isDeleted;

}
